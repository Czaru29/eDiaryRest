package com.ediary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAccessException extends RuntimeException {

    public NoAccessException() {
        super();
    }

    public NoAccessException(String message){
        super(message);
    }

    public NoAccessException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
