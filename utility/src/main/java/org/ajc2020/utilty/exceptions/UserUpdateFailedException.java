package org.ajc2020.utilty.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserUpdateFailedException  extends RuntimeException {
    public UserUpdateFailedException(String message) {
        super(message);
    }
}
