package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class OfficeHoursResource extends RepresentationModel<OfficeHoursResource> {

    private OffsetDateTime arrive;
    private OffsetDateTime leave;

}
