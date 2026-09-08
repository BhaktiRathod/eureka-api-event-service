package com.eventhub.bookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Step 3: Handle Event Service failure with a clear 503 response
    @ExceptionHandler(EventServiceUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleEventServiceUnavailable(
            EventServiceUnavailableException ex) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);

        problemDetail.setTitle("Event Service Unavailable");
        problemDetail.setDetail(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(problemDetail);

    }
}