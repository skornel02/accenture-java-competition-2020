package org.ajc2020.spring1.model;

public interface User {

    String getLoginName();
    String getLoginPassword();
    PermissionLevel getPermissionLevel();

}
