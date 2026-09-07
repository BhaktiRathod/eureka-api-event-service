package com.eventhub.bookingservice.service;

import com.eventhub.bookingservice.dto.BookingRequestDTO;
import com.eventhub.bookingservice.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO requestDTO);

    BookingResponseDTO getBooking(Long id);

    List<BookingResponseDTO> getAllBookings();
}
