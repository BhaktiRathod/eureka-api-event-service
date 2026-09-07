package com.eventhub.organizerservice.mapper;

import com.eventhub.organizerservice.dto.OrganizerRequestDTO;
import com.eventhub.organizerservice.dto.OrganizerResponseDTO;
import com.eventhub.organizerservice.entity.Organizer;

public final class OrganizerMapper {

    private OrganizerMapper() {
    }

    public static Organizer requestDTOToEntity(
            OrganizerRequestDTO dto) {

        Organizer organizer = new Organizer();
        organizer.setName(dto.getName());
        organizer.setEmail(dto.getEmail());
        organizer.setPhone(dto.getPhone());
        organizer.setCity(dto.getCity());
        return organizer;
    }

    public static OrganizerResponseDTO entityToResponseDTO(
            Organizer organizer) {

        OrganizerResponseDTO dto = new OrganizerResponseDTO();
        dto.setId(organizer.getId());
        dto.setName(organizer.getName());
        dto.setEmail(organizer.getEmail());
        dto.setPhone(organizer.getPhone());
        dto.setCity(organizer.getCity());

        return dto;
    }
}