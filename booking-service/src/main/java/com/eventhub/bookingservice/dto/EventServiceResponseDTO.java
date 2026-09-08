package com.eventhub.bookingservice.dto;

import java.time.LocalDate;

public class EventServiceResponseDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate eventDate;
    private String venue;
    private Integer capacity;
    private Long organizerId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }
}
