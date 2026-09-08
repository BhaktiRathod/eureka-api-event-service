# Demo 2: Implement Inter-Service Communication Using Spring RestClient

## Overview

This demo extends the EventHub microservices application by enabling the **Booking Service** to communicate synchronously with the **Event Service**.

Before a booking is saved, the Booking Service checks the requested event by calling the Event Service using Spring `RestClient`.

The service-to-service call:

- Uses **Spring RestClient**.
- Uses the **Eureka service name** instead of a hardcoded host and port.
- Uses **Spring Cloud LoadBalancer** for service discovery.
- Configures a **read timeout** for the downstream call.
- Handles Event Service failures and returns a controlled **503 Service Unavailable** response.

---

## Services Used

The demo uses the existing EventHub microservices setup:

```text
Client
  |
  v
API Gateway
  |
  v
Booking Service
  |
  | RestClient
  v
Eureka / Spring Cloud LoadBalancer
  |
  v
Event Service
```

The client sends the booking request through the API Gateway.

The Booking Service then communicates directly with the Event Service using its registered Eureka service name.

---

## Communication Pattern

The Booking Service to Event Service interaction uses **synchronous communication**.

```text
Create Booking
     |
     v
Booking Service receives eventId
     |
     v
Call Event Service
     |
     v
Wait for Event response
     |
     v
Continue with booking
```

The Booking Service needs the Event Service response before it can continue processing the booking.

---

## Successful Request Flow

```text
POST /bookings
      |
      v
API Gateway
      |
      v
Booking Service
      |
      v
BookingServiceImpl
      |
      v
EventClient
      |
      | GET /events/{eventId}
      v
RestClient
      |
      v
Eureka resolves "event-service"
      |
      v
Event Service
      |
      v
Event returned
      |
      v
Booking saved
      |
      v
Booking response returned
```

---

# Changes Made in Booking Service

## 1. Add Spring Cloud LoadBalancer

Add the following dependency to `booking-service/pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

Spring Cloud LoadBalancer allows the Booking Service to use a registered service name such as:

```text
http://event-service
```

instead of:

```text
http://localhost:<port>
```

---

## 2. Configure RestClient

File:

```text
booking-service/src/main/java/com/eventhub/bookingservice/config/RestClientConfig.java
```

```java
package com.eventhub.bookingservice.config;

import java.time.Duration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Normal RestClient.Builder used by framework components
    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // Step 1: Load-balanced RestClient for service-to-service communication
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {

        // Step 2: Configure timeout for downstream calls
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory();

        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return RestClient.builder()
                .requestFactory(requestFactory);
    }
}
```

### Why are two builders used?

The normal `RestClient.Builder` is kept as the primary builder for framework components.

The `loadBalancedRestClientBuilder` is specifically used for Booking Service to Event Service communication.

This avoids making framework-level RestClient calls depend on Eureka service resolution.

---

## 3. Create Event Service Response DTO

The Booking Service needs a Java type to receive the JSON returned by the Event Service.

File:

```text
booking-service/src/main/java/com/eventhub/bookingservice/dto/EventServiceResponseDTO.java
```

Example:

```java
package com.eventhub.bookingservice.dto;

public class EventServiceResponseDTO {

    private Long id;
    private String name;

    public EventServiceResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

This DTO represents the Event Service response consumed by the Booking Service.

It is separate from:

- `BookingRequestDTO` - data received by the Booking API.
- `BookingResponseDTO` - data returned by the Booking API.

---

## 4. Create EventClient

File:

```text
booking-service/src/main/java/com/eventhub/bookingservice/client/EventClient.java
```

```java
package com.eventhub.bookingservice.client;

import com.eventhub.bookingservice.dto.EventServiceResponseDTO;
import com.eventhub.bookingservice.exception.EventServiceUnavailableException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EventClient {

    private final RestClient restClient;

    public EventClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {

        // Step 1: Use the Eureka service name instead of localhost:port
        this.restClient = restClientBuilder
                .baseUrl("http://event-service")
                .build();
    }

    public EventServiceResponseDTO getEventById(Long eventId) {

        try {

            // Step 1: Make a synchronous call to Event Service
            return restClient.get()
                    .uri("/events/{id}", eventId)
                    .retrieve()
                    .body(EventServiceResponseDTO.class);

        } catch (Exception ex) {

            // Step 3: Convert downstream failure into an application exception
            throw new EventServiceUnavailableException(
                    "Event Service is currently unavailable"
            );
        }
    }
}
```

### Important

The client uses:

```text
http://event-service
```

The service name is resolved through Eureka and Spring Cloud LoadBalancer.

No Event Service host or port is hardcoded in the Booking Service.

---

## 5. Call EventClient Before Saving a Booking

The existing `BookingServiceImpl#createBooking()` flow is extended so the Event Service is contacted before the booking is saved.

Conceptually:

```java
EventServiceResponseDTO event =
        eventClient.getEventById(bookingRequestDTO.getEventId());

Booking booking =
        BookingMapper.requestDTOToEntity(bookingRequestDTO);

Booking savedBooking =
        bookingRepository.save(booking);

return BookingMapper.entityToResponseDTO(savedBooking);
```

The important operation is:

```java
eventClient.getEventById(bookingRequestDTO.getEventId());
```

This makes the Booking Service verify the downstream Event Service call before continuing with persistence.

---

## 6. Configure a Timeout

The RestClient uses a three-second read timeout:

```java
requestFactory.setReadTimeout(Duration.ofSeconds(3));
```

The timeout prevents a slow downstream service from making the caller wait indefinitely.

---

## 7. Handle Event Service Failure

Create:

```text
booking-service/src/main/java/com/eventhub/bookingservice/exception/EventServiceUnavailableException.java
```

```java
package com.eventhub.bookingservice.exception;

public class EventServiceUnavailableException extends RuntimeException {

    public EventServiceUnavailableException(String message) {
        super(message);
    }
}
```

`EventClient` converts downstream failures into this custom exception.

---

## 8. Return 503 Service Unavailable

Add a handler for `EventServiceUnavailableException` to the existing global exception handler.

```java
@ExceptionHandler(EventServiceUnavailableException.class)
public ResponseEntity<ProblemDetail> handleEventServiceUnavailable(
        EventServiceUnavailableException ex) {

    ProblemDetail problemDetail =
            ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);

    problemDetail.setTitle("Event Service Unavailable");
    problemDetail.setDetail(ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(problemDetail);
}
```

Instead of exposing an uncontrolled `500 Internal Server Error`, the Booking Service now returns a clear downstream failure response.

Example:

```json
{
  "title": "Event Service Unavailable",
  "status": 503,
  "detail": "Event Service is currently unavailable"
}
```

---

# Running the Demo

## Start the Services

Start the services in this order:

```text
1. Discovery Server
2. Event Service
3. Booking Service
4. API Gateway
```

Confirm that the Event Service and Booking Service are registered with Eureka before testing.

---

# Postman Testing

## Test 1: Verify an Event Exists

### Request

```http
GET http://localhost:8080/events/1
```

### Expected Result

```text
200 OK
```

The response should contain the event with ID `1`.

---

## Test 2: Create a Booking

### Request

```http
POST http://localhost:8080/bookings
```

### Header

```text
Content-Type: application/json
```

### Body

```json
{
  "eventId": 1,
  "userId": 101,
  "quantity": 2
}
```

### Expected Flow

```text
POST /bookings
      |
      v
Booking Service
      |
      v
EventClient
      |
      v
GET http://event-service/events/1
      |
      v
Event found
      |
      v
Booking saved
```

The booking should be created successfully.

---

## Test 3: View Bookings

### Request

```http
GET http://localhost:8080/bookings
```

The newly created booking should appear in the response.

A specific booking can also be retrieved using:

```http
GET http://localhost:8080/bookings/1
```

---

# Test Downstream Failure

This test verifies the failure-handling behaviour required for the demo.

## Step 1

Keep the following services running:

```text
Discovery Server
Booking Service
API Gateway
```

## Step 2

Stop only:

```text
Event Service
```

## Step 3

Send the same booking request:

```http
POST http://localhost:8080/bookings
```

```json
{
  "eventId": 1,
  "userId": 101,
  "quantity": 2
}
```

## Expected Result

```text
503 Service Unavailable
```

Example response:

```json
{
  "title": "Event Service Unavailable",
  "status": 503,
  "detail": "Event Service is currently unavailable"
}
```

The booking should not be saved because the required downstream Event Service call failed.

---

# Demo Progression

## Step 1 - Implement Inter-Service Communication

```text
Booking Service
      |
      v
RestClient
      |
      v
event-service
      |
      v
Eureka + LoadBalancer
      |
      v
Event Service
```

Key points:

- Use Spring `RestClient`.
- Use the Eureka service name.
- Do not hardcode a host and port.

---

## Step 2 - Add Timeout

```text
RestClient call
      |
      v
3-second read timeout
```

Key point:

A downstream service should not be allowed to make the caller wait indefinitely.

---

## Step 3 - Handle Downstream Failure

```text
Event Service unavailable
        |
        v
RestClient call fails
        |
        v
EventServiceUnavailableException
        |
        v
GlobalExceptionHandler
        |
        v
503 Service Unavailable
```

Key point:

A downstream failure should be handled explicitly instead of exposing a generic server error.

---

# What This Demo Demonstrates

After completing the demo, the application supports:

- Synchronous Booking Service to Event Service communication.
- Spring `RestClient` for service-to-service HTTP calls.
- Eureka-based service discovery.
- Spring Cloud LoadBalancer.
- Service-name based URLs.
- Timeout configuration.
- Explicit downstream failure handling.
- Controlled `503 Service Unavailable` responses.

---

# Sprint 3 Learning Outcome Coverage

This demo supports the implementation learning outcome:

> Implement synchronous communication between microservices using RestClient, discovery-resolved, with timeouts and handled downstream failure.

The final implementation demonstrates:

```text
Booking Request
      |
      v
Booking Service
      |
      v
RestClient
      |
      v
Service Discovery
      |
      v
Event Service
      |
      +---- Available ----> Continue and save booking
      |
      +---- Unavailable --> Return controlled 503 response
```

---

# Key Takeaway

A microservice should not depend on hardcoded addresses of other services.

For EventHub:

```text
Booking Service
      |
      v
http://event-service
      |
      v
Eureka + LoadBalancer
      |
      v
Available Event Service instance
```

Spring `RestClient`, Eureka and Spring Cloud LoadBalancer together allow the Booking Service to make synchronous service-to-service calls while keeping service location dynamic and downstream failures controlled.