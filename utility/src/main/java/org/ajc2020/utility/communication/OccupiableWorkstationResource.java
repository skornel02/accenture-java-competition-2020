package org.ajc2020.utility.communication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OccupiableWorkstationResource {

    private WorkstationResource workstation;
    private boolean occupiable;

}
