package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsideResourcePlain {
    private WorkerResource worker;
    private OfficeHoursPlain officeHoursResource;
}
