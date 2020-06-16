package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficeHoursPlain {
    private OffsetDateTime arrive;
    private OffsetDateTime leave;
}
