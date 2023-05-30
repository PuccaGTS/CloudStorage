package ru.netology.cloudstorage.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ApplicationException{
    public InternalServerException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
