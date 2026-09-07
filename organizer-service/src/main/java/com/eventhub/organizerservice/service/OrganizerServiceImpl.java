package com.eventhub.organizerservice.service;

import com.eventhub.organizerservice.entity.Organizer;
import com.eventhub.organizerservice.repository.OrganizerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizerServiceImpl implements OrganizerService {

    private final OrganizerRepository organizerRepository;

    public OrganizerServiceImpl(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    // Save the Organizer to the database and return the saved entity.
    public Organizer createOrganizer(Organizer organizer) {
        return organizerRepository.save(organizer);
    }

    // findById() returns Optional because the Organizer
// may or may not exist in the database.
    public Optional<Organizer> getOrganizer(Long id) {
        return organizerRepository.findById(id);
    }

    // Reproduce N+1
    public List<Organizer> getAllOrganizers() {
         return organizerRepository.findAll();
      }

}
