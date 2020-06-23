package org.ajc2020.spring1.manager;

import org.ajc2020.spring1.model.ErrorResponseEntity;
import org.ajc2020.spring1.model.ExceptionResponse;
import org.ajc2020.utility.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionManager extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ForbiddenException.class, NoEmptyWorkstation.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ExceptionResponse> handleForbiddenException(RuntimeException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        return new ErrorResponseEntity<>(response);
    }

    @ExceptionHandler({UserNotFoundException.class, WorkstationNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(RuntimeException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ErrorResponseEntity<>(response);
    }

    @ExceptionHandler({UserCreationFailedException.class, UserUpdateFailedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(RuntimeException ex) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ErrorResponseEntity<>(response);
    }

}
