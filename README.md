# Demo 2: Eureka + API Gateway Microservice Communication

This demo focuses on the core Spring Cloud microservices pattern:

- service registration and discovery with Eureka
- routing and request forwarding with Spring Cloud Gateway
- communication between multiple services through a single API entry point

The goal is not to build a complex pagination or sorting feature. The main objective is to understand how independent services register themselves, discover each other, and are accessed through the API Gateway.

## Overview

This project contains the following services:

- `discovery-server` — Eureka server for service discovery
- `api-gateway` — single entry point for clients
- `event-service` — handles event-related APIs
- `organizer-service` — handles organizer-related APIs
- `booking-service` — handles booking-related APIs

## Important Concept

In a microservices architecture, clients should not call individual service ports directly. Instead, they call the gateway, and the gateway routes the request to the correct service instance using Eureka and load balancing.

Example:

- Client hits: `http://localhost:8080/events`
- API Gateway receives the request
- Gateway checks the route configuration
- Gateway finds the `EVENT-SERVICE` instance through Eureka
- Gateway forwards the request to the event service

This makes the system:

- easier to scale
- easier to manage
- loosely coupled
- simpler to expose to clients

## Architecture

```text
Client
  |
  v
API Gateway (8080)
  | \
  |  -> event-service (registered in Eureka, route: /events/**)
  |  -> organizer-service (registered in Eureka, route: /organizers/**)
  |  -> booking-service (registered in Eureka, route: /bookings/**)
  |
  v
Eureka Discovery Server (8761)
```

## Why Eureka Is Important

Eureka acts as a service registry.

Each service registers itself with the discovery server when it starts.

Example:

- `event-service` registers as `EVENT-SERVICE`
- `organizer-service` registers as `ORGANIZER-SERVICE`
- `booking-service` registers as `BOOKING-SERVICE`

The API Gateway then resolves these names dynamically instead of hardcoding direct URLs.

## Why API Gateway Is Important

The API Gateway is the single public access point for the system.

Instead of clients calling:

- `http://localhost:8081/events`
- `http://localhost:8082/organizers`
- `http://localhost:8083/bookings/status`

They can call:

- `http://localhost:8080/events`
- `http://localhost:8080/organizers`
- `http://localhost:8080/bookings/status`

This hides internal service details from the client.

## Required Tools

Before running the project, ensure the following are installed:

- Java 21+
- Maven
- PostgreSQL
- Postman or curl for testing requests

## Project Structure

```text
eventhub-microservices/
├── pom.xml
├── discovery-server/
├── api-gateway/
├── event-service/
├── organizer-service/
├── booking-service/
├── README.md
└── .idea/   (optional IDE files)
```

## Run the Demo

Start the services in this order:

1. Discovery Server

```bash
cd discovery-server
./mvnw spring-boot:run
```

2. Event Service

```bash
cd event-service
./mvnw spring-boot:run
```

3. Organizer Service

```bash
cd organizer-service
./mvnw spring-boot:run
```

4. Booking Service

```bash
cd booking-service
./mvnw spring-boot:run
```

5. API Gateway

```bash
cd api-gateway
./mvnw spring-boot:run
```

## Service Endpoints to Test

After all services are started, verify that each service is registered in Eureka and available through the API Gateway.

### Eureka Dashboard

```text
http://localhost:8761
```

You should see all services listed there:

- `EVENT-SERVICE`
- `ORGANIZER-SERVICE`
- `BOOKING-SERVICE`
- `API-GATEWAY`

### Direct Service URLs

These are the inner service URLs if accessed directly:

```text
http://localhost:8081/events
http://localhost:8082/organizers
http://localhost:8083/bookings
```

### Gateway URLs

These are the public routes through the API Gateway:

```text
http://localhost:8080/events
http://localhost:8080/organizers
http://localhost:8080/bookings
```

### Booking Service Status

```text
http://localhost:8080/bookings/status
```

Expected response:

```text
Booking Service is running
```

### Event Service Example

```text
POST http://localhost:8080/events
GET  http://localhost:8080/events
GET  http://localhost:8080/events/1
```

### Organizer Service Example

```text
POST http://localhost:8080/organizers
GET  http://localhost:8080/organizers
GET  http://localhost:8080/organizers/1
```

### Booking Service Example

```text
POST http://localhost:8080/bookings
GET  http://localhost:8080/bookings
GET  http://localhost:8080/bookings/1
```

## What You Should See in Eureka

When everything is working correctly, Eureka should show the following registered application names:

- `EVENT-SERVICE`
- `ORGANIZER-SERVICE`
- `BOOKING-SERVICE`
- `API-GATEWAY`

This confirms that the microservices are discovered and mapped correctly through the gateway.

## Gateway Routing Configuration

The API Gateway routes are configured in `api-gateway/src/main/resources/application.properties`:

```properties
spring.cloud.gateway.server.webflux.routes[0].id=event-route
spring.cloud.gateway.server.webflux.routes[0].uri=lb://EVENT-SERVICE
spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/events/**

spring.cloud.gateway.server.webflux.routes[1].id=organizer-route
spring.cloud.gateway.server.webflux.routes[1].uri=lb://ORGANIZER-SERVICE
spring.cloud.gateway.server.webflux.routes[1].predicates[0]=Path=/organizers/**

spring.cloud.gateway.server.webflux.routes[2].id=booking-route
spring.cloud.gateway.server.webflux.routes[2].uri=lb://BOOKING-SERVICE
spring.cloud.gateway.server.webflux.routes[2].predicates[0]=Path=/bookings/**
```

This means:

- `/events/**` -> forwarded to the event service
- `/organizers/**` -> forwarded to the organizer service
- `/bookings/**` -> forwarded to the booking service

## Sample Requests Through the Gateway

Use these requests through the gateway on port `8080`.

### 1. Check booking service status

```http
GET http://localhost:8080/bookings/status
```

Expected:

```text
Booking Service is running
```

### 2. Get all events

```http
GET http://localhost:8080/events
```

### 3. Create an event

```http
POST http://localhost:8080/events
Content-Type: application/json
```

```json
{
  "name": "Java Workshop",
  "description": "Introduction to Java programming",
  "eventDate": "2026-09-10",
  "venue": "Bangalore",
  "capacity": 50,
  "organizerId": 1
}
```

### 4. Get event by ID

```http
GET http://localhost:8080/events/1
```

### 5. Get all organizers

```http
GET http://localhost:8080/organizers
```

### 6. Create organizer

```http
POST http://localhost:8080/organizers
Content-Type: application/json
```

```json
{
  "name": "Tech Events Pvt Ltd",
  "email": "info@techevents.com",
  "phone": "9876543210"
}
```

## Request Flow Example

```text
Client Request
    |
    v
http://localhost:8080/events
    |
    v
API Gateway
    |
    v
Route matching: /events/**
    |
    v
lb://EVENT-SERVICE
    |
    v
Event Service instance
    |
    v
Response back through the gateway
```

This is the primary learning objective of the demo.

## Why This Demo Matters

This project teaches the following important microservice concepts:

- independent service startup
- service registration with Eureka
- dynamic service lookup
- centralized API access via the gateway
- routing based on URL path
- service-to-service communication without hardcoded hostnames

## Best Practices for This Demo

- Start `discovery-server` first
- Keep all services running before testing the gateway
- Use the gateway as the main access point for all requests
- Prefer calling the gateway instead of direct service ports in demos and tests
- Use Eureka UI to confirm registration and health

## Common Troubleshooting

### 1. Service not registered in Eureka

Check if the service is started successfully and that the `eureka.client.service-url.defaultZone` property is correct.

### 2. Gateway cannot route the request

Check whether the route path matches the service path exactly.

Example:

- `/events/**` should match requests to `/events` and `/events/1`

### 3. Port mismatch

Remember:

- discovery server: `8761`
- API gateway: `8080`
- event service: `8081`
- organizer service: `8082`
- booking service: `8083`

## Summary

This demo is a good introduction to the design of a Spring Cloud microservices system:

- Eureka helps services discover each other
- API Gateway acts as the entry point
- routes forward requests to the correct service
- clients work with a single URL instead of many service ports

This is the key concept to understand before moving to advanced patterns like load balancing, authentication, resiliency, and distributed tracing.

---

If you are learning this for the first time, focus on the flow:

`Client -> API Gateway -> Eureka -> Target Service`

This is the heart of the demo.
