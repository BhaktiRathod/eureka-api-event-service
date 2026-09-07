package com.eventhub.bookingservice.mapper;

import com.eventhub.bookingservice.dto.BookingRequestDTO;
import com.eventhub.bookingservice.dto.BookingResponseDTO;
import com.eventhub.bookingservice.entity.Booking;
import com.eventhub.bookingservice.entity.BookingStatus;

import java.time.LocalDate;

public class BookingMapper {

    private BookingMapper() {
    }

    public static Booking requestDTOToEntity(BookingRequestDTO requestDTO) {
        Booking booking = new Booking();
        booking.setEventId(requestDTO.getEventId());
        booking.setUserId(requestDTO.getUserId());
        booking.setQuantity(requestDTO.getQuantity());
        booking.setBookingDate(LocalDate.now());
        booking.setStatus(BookingStatus.PENDING);
        return booking;
    }

    public static BookingResponseDTO entityToResponseDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getEventId(),
                booking.getUserId(),
                booking.getQuantity(),
                booking.getBookingDate(),
                booking.getStatus()
        );
    }
}
