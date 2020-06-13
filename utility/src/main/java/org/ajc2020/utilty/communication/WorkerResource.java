package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Value;
import org.ajc2020.utilty.resource.Status;

@Value
@Builder
public class WorkerResource {

    long id;
    String name;
    String email;
    String rfId;
    long averageTime;
    Status status;

}
