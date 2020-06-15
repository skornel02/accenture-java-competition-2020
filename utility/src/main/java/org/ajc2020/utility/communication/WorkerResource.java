package org.ajc2020.utility.communication;

import lombok.*;
import org.ajc2020.utility.resource.WorkerStatus;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerResource extends RepresentationModel<WorkerResource> {

    private String id;
    private String name;
    private String email;
    private String rfId;
    private double averageTime;
    private WorkerStatus status;

}
