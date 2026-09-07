package com.eventhub.organizerservice.service;

import com.eventhub.organizerservice.entity.Organizer;

import java.util.List;
import java.util.Optional;

public interface OrganizerService {

    public Organizer createOrganizer(Organizer organizer);

    // Returns Optional because the Organizer may or may not exist.
    public Optional<Organizer> getOrganizer(Long Id);

    public List<Organizer> getAllOrganizers();

}
