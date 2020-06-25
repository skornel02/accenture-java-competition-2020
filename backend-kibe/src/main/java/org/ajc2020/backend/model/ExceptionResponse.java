package org.ajc2020.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class ExceptionResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private OffsetDateTime time;
    @JsonIgnore
    private HttpStatus statusCode;
    private String title;
    private String message = "";
    private String url = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toString();


    public ExceptionResponse(HttpStatus statusCode, String title) {
        this.statusCode = statusCode;
        this.title = title;
        this.time = OffsetDateTime.now();
    }

    public int getStatus() {
        return statusCode.value();
    }

}
