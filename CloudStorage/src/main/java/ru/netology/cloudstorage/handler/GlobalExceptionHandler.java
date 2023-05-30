package ru.netology.cloudstorage.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudstorage.exception.ApplicationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<String> droneApplicationExceptionHandler(ApplicationException e) {
        return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
    }
}
