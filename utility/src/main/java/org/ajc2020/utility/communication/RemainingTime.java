package org.ajc2020.utility.communication;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ajc2020.utility.resource.WorkerStatus;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemainingTime {

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime projectedEntryTime;
    private WorkerStatus status;
    private WorkstationResource workstation;
    private String locationSVG;

}
