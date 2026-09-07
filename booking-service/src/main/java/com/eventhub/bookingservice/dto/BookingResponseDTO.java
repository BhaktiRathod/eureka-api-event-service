package com.eventhub.bookingservice.dto;

import com.eventhub.bookingservice.entity.BookingStatus;
import java.time.LocalDate;

public class BookingResponseDTO {

    private Long id;
    private Long eventId;
    private Long userId;
    private Integer quantity;
    private LocalDate bookingDate;
    private BookingStatus status;

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(Long id, Long eventId, Long userId, Integer quantity,
                             LocalDate bookingDate, BookingStatus status) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.quantity = quantity;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
