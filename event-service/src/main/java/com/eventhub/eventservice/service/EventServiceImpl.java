package com.eventhub.eventservice.service;

import com.eventhub.eventservice.dto.EventRequestDTO;
import com.eventhub.eventservice.dto.EventResponseDTO;
import com.eventhub.eventservice.entity.Event;
import com.eventhub.eventservice.exception.EventNotFoundException;
import com.eventhub.eventservice.mapper.EventMapper;
import com.eventhub.eventservice.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("name", "eventDate");

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        Event event =
                EventMapper.requestDTOToEntity(eventRequestDTO);
        Event savedEvent =
                eventRepository.save(event);
        return EventMapper.entityToResponseDTO(savedEvent);
    }

    @Override
    public EventResponseDTO getEvent(Long id) {
        Event event =
                eventRepository.findById(id)
                        .orElseThrow(() ->
                                new EventNotFoundException(
                                        "Event not found: " + id));

        return EventMapper.entityToResponseDTO(event);
    }

    @Override
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::entityToResponseDTO)
                .toList();
    }
}

