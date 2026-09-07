package com.eventhub.organizerservice.controller;


import com.eventhub.organizerservice.entity.Organizer;
import com.eventhub.organizerservice.service.OrganizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @PostMapping
    public Organizer createOrganizer(
            @RequestBody Organizer organizer) {
        // Converts the JSON request body into an Organizer object
        // and passes it to the service layer.
        return organizerService.createOrganizer(organizer);
    }

    @GetMapping("/{id}")
    public Organizer getOrganizer(
            @PathVariable Long id) {

        // findById() returns Optional because the Organizer
        // may or may not exist in the database.
        // orElse(null) returns the Organizer if present;
        // otherwise it returns null.
        return organizerService.getOrganizer(id).orElse(null);
    }
    @GetMapping
    public List<Organizer> getAllOrganizers() {
        return organizerService.getAllOrganizers();
    }
}
