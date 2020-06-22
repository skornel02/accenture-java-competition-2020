package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ajc2020.utility.resource.DeskOrientation;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class WorkStationResource extends RepresentationModel<WorkStationResource> {

    private String id;
    private DeskOrientation orientation;
    private double x;
    private double y;
    private int zone;
    private WorkerResource occupier;
    private boolean enabled;

}
