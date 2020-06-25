package org.ajc2020.backend.model;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.ResponseEntity;

@EqualsAndHashCode(callSuper = true)
@Value
public class ErrorResponseEntity<T> extends ResponseEntity<T> {
    @SuppressWarnings("unchecked")
    public ErrorResponseEntity(ExceptionResponse response) {
        super((T) response, response.getStatusCode());
    }
}
