package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ajc2020.utilty.resource.WorkerStatus;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class WorkerResource extends RepresentationModel<WorkerResource> {

    private String id;
    private String name;
    private String email;
    private String rfId;
    private double averageTime;
    private WorkerStatus status;

}
