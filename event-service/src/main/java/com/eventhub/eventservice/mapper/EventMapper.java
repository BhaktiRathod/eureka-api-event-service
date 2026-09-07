package com.eventhub.eventservice.mapper;

import com.eventhub.eventservice.dto.EventRequestDTO;
import com.eventhub.eventservice.dto.EventResponseDTO;
import com.eventhub.eventservice.entity.Event;

public class EventMapper {
    public static EventResponseDTO entityToResponseDTO(Event event) {

        EventResponseDTO dto = new EventResponseDTO();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setVenue(event.getVenue());
        dto.setCapacity(event.getCapacity());
        dto.setOrganizerId(event.getOrganizerId());

        return dto;
    }

    public static Event requestDTOToEntity(
            EventRequestDTO dto) {

        Event event = new Event();

        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setVenue(dto.getVenue());
        event.setCapacity(dto.getCapacity());
        event.setOrganizerId(dto.getOrganizerId());
        return event;
    }
}

