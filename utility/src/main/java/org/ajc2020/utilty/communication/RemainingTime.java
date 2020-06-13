package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetTime;

@Value
@Builder
public class RemainingTime {

    OffsetTime projectedEntryTime;

}
