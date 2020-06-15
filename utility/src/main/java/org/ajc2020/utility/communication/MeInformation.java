package org.ajc2020.utility.communication;

import lombok.*;
import org.ajc2020.utility.resource.PermissionLevel;
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

    public String getUuid() {
        if (admin != null) return admin.getUuid();
        if (worker != null) return worker.getId();
        return "";
    }
}
