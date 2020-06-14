package org.ajc2020.utilty.communication;

import lombok.*;
import org.ajc2020.utilty.resource.PermissionLevel;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeInformation extends RepresentationModel<MeInformation> {

    @NotNull
    private PermissionLevel permission;
    private WorkerResource worker;
    private AdminResource admin;

}
