package org.ajc2020.utility.communication;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResourcePlain {

    private LocalDate targetDay;
    private OffsetDateTime creationDate;

}
