package org.ajc2020.utilty.communication;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResource extends RepresentationModel<AdminResource> {
    private String uuid;
    private String name;
    private String email;
    private boolean superAdmin;
}
