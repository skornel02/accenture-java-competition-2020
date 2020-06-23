package org.ajc2020.utility.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoEmptyWorkstation extends RuntimeException {

    public NoEmptyWorkstation(String message) {
        super(message);
    }
}
