package ru.netology.cloudstorage.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApplicationException{
    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
