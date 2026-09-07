package com.eventhub.eventservice.controller;

import com.eventhub.eventservice.dto.EventRequestDTO;
import com.eventhub.eventservice.dto.EventResponseDTO;
import com.eventhub.eventservice.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestBody EventRequestDTO eventRequestDTO) {

        EventResponseDTO savedEvent =
                eventService.createEvent(eventRequestDTO);

        return new ResponseEntity<>(
                savedEvent,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(
            @PathVariable Long id) {

        EventResponseDTO event =
                eventService.getEvent(id);

        return new ResponseEntity<>(
                event,
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>>
    getAllEvents() {

        List<EventResponseDTO> events =
                eventService.getAllEvents();

        return new ResponseEntity<>(
                events,
                HttpStatus.OK
        );
    }
}
