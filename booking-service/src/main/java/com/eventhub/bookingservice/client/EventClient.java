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

        this.restClient = restClientBuilder
                .baseUrl("http://event-service")
                .build();
    }

    public EventServiceResponseDTO getEventById(Long eventId) {

        try {
            return restClient.get()
                    .uri("/events/{id}", eventId)
                    .retrieve()
                    .body(EventServiceResponseDTO.class);

        } catch (Exception ex) {
            throw new EventServiceUnavailableException(
                    "Event Service is currently unavailable"
            );
        }
    }
}