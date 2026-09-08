package com.eventhub.bookingservice.service;

import com.eventhub.bookingservice.client.EventClient;
import com.eventhub.bookingservice.dto.BookingRequestDTO;
import com.eventhub.bookingservice.dto.BookingResponseDTO;
import com.eventhub.bookingservice.dto.EventServiceResponseDTO;
import com.eventhub.bookingservice.entity.Booking;
import com.eventhub.bookingservice.exception.BookingNotFoundException;
import com.eventhub.bookingservice.mapper.BookingMapper;
import com.eventhub.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventClient eventClient;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              EventClient eventClient) {
        this.bookingRepository = bookingRepository;
        this.eventClient = eventClient;
    }
    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {

        EventServiceResponseDTO event =
                eventClient.getEventById(bookingRequestDTO.getEventId());

        Booking booking = BookingMapper.requestDTOToEntity(bookingRequestDTO);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.entityToResponseDTO(savedBooking);
    }

    @Override
    public BookingResponseDTO getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        return BookingMapper.entityToResponseDTO(booking);
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingMapper::entityToResponseDTO)
                .toList();
    }
}
