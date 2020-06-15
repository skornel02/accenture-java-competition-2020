package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;
import org.ajc2020.utility.resource.WorkerStatus;

import java.time.LocalTime;
import java.time.OffsetTime;

@Data
@Builder
public class RemainingTime {

    private LocalTime projectedEntryTime;
    private WorkerStatus status;

}
