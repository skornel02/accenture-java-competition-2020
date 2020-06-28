package org.ajc2020.utility.communication;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.ajc2020.utility.resource.WorkerStatus;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemainingTime {

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime projectedEntryTime;
    private boolean permittedToEnter;
    private WorkerStatus status;
    private WorkstationResource workstation;
    private String locationSVG;


    public RemainingTime(RemainingTime copy) {
        this(copy.projectedEntryTime, copy.permittedToEnter, copy.status, copy.workstation, copy.locationSVG);
    }
}
