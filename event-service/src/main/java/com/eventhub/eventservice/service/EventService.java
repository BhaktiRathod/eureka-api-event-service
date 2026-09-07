package com.eventhub.eventservice.service;



import com.eventhub.eventservice.dto.EventRequestDTO;
import com.eventhub.eventservice.dto.EventResponseDTO;

import java.util.List;

public interface EventService {

    EventResponseDTO createEvent(EventRequestDTO eventRequestDTO);

    EventResponseDTO getEvent(Long id);

    List<EventResponseDTO> getAllEvents();
}

