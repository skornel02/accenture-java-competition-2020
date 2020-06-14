package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.OffsetTime;

@Data
@Builder
public class RemainingTime {

    private OffsetTime projectedEntryTime;

}
