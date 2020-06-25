package org.ajc2020.backend.model;

import org.ajc2020.utility.resource.PermissionLevel;

public interface User {

    String getLoginName();
    String getLoginPassword();
    PermissionLevel getPermissionLevel();

}
