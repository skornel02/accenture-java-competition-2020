package org.ajc2020.utilty.communication;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdminResource implements UserType {
    String uuid;
    String name;
    String email;
    boolean superAdmin;
}
