package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetTime;

@Data
@Builder
public class RemainingTime {

    private OffsetTime projectedEntryTime;

}
