package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class AdminResource extends RepresentationModel<AdminResource> {
    private String uuid;
    private String name;
    private String email;
    private boolean superAdmin;
}
