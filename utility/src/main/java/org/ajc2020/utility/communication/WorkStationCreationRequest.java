package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ajc2020.utility.resource.DeskOrientation;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class WorkStationCreationRequest {

    @NotNull
    @NotEmpty
    private String id;
    @NotNull
    private DeskOrientation orientation;
    @NotNull
    private double x;
    @NotNull
    private double y;
    @NotNull
    private int zone;
    @NotNull
    private boolean enabled;

}
