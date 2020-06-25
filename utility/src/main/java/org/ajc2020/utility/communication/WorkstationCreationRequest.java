package org.ajc2020.utility.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ajc2020.utility.resource.DeskOrientation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkstationCreationRequest {

    @NotNull
    @NotEmpty
    private String id;
    @NotNull
    private DeskOrientation orientation;
    @NotNull
    private double x;
    @NotNull
    private double y;
    @NotNull
    private int zone;
    @NotNull
    private boolean enabled;

}
