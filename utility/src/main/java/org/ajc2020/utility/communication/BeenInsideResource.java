package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class BeenInsideResource {

    @NotNull
    private WorkerResource worker;
    @NotNull
    private List<OfficeHoursResource> records;

}
