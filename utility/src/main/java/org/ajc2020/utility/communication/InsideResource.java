package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class InsideResource {

    @NotNull
    private WorkerResource worker;
    @NotNull
    private OfficeHoursResource officeHoursResource;

}
