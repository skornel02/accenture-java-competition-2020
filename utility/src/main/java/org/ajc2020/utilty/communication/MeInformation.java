package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.ajc2020.utilty.resource.PermissionLevel;

@Value
@Builder
public class MeInformation {

    PermissionLevel permission;
    UserType user;

}
