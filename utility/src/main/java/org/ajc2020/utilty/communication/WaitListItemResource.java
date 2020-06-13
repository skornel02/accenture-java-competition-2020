package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Value
@Builder
public class WaitListItemResource {

    long id;
    LocalDate targetDate;
    OffsetDateTime creationDateTime;
    WorkerResource worker;

}
