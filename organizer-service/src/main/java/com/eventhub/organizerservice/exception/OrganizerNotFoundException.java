package com.eventhub.organizerservice.exception;

public class OrganizerNotFoundException extends RuntimeException {

    public OrganizerNotFoundException(String message) {
        super(message);
    }
}
