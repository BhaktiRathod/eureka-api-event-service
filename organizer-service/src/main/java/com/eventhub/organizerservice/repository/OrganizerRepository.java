package com.eventhub.organizerservice.repository;

import com.eventhub.organizerservice.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository
        extends JpaRepository<Organizer, Long> {
}
