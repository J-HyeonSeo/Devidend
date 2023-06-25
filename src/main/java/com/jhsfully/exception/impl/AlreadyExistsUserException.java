package com.jhsfully.exception.impl;

import com.jhsfully.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsUserException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "Already exists username";
    }
}
