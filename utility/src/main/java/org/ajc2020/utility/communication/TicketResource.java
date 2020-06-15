package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class TicketResource extends RepresentationModel<TicketResource> {

    @NotNull
    private LocalDate targetDay;
    @NotNull
    private OffsetDateTime creationDate;

}
