package com.eventhub.bookingservice.exception;

// Step 3: Custom exception for Event Service failure
public class EventServiceUnavailableException extends RuntimeException {

    public EventServiceUnavailableException(String message) {
        super(message);
    }
}