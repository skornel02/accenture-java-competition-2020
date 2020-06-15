package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class WaitListItemResource {

    private long id;
    private LocalDate targetDate;
    private OffsetDateTime creationDateTime;
    private WorkerResource worker;

}
