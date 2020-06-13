package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetTime;

@Value
@Builder
public class LoginResource {

    long id;
    OffsetTime arrivedAt;
    OffsetTime leftAt;
    WorkerResource worker;

}
