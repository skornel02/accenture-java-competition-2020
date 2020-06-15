package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class TicketResource extends RepresentationModel<TicketResource> {

    private LocalDate targetDay;
    private OffsetDateTime creationDate;

}
