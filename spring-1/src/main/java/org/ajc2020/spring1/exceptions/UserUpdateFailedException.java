package org.ajc2020.spring1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserUpdateFailedException  extends ResponseStatusException {
    public UserUpdateFailedException(HttpStatus status, String message) {
        super(status, message);
    }
}
