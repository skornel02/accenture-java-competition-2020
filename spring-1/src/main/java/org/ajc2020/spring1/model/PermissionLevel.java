package org.ajc2020.spring1.model;

import lombok.Getter;

@Getter
public enum PermissionLevel {

    WORKER("worker"),
    ADMIN("admin"),
    SUPER_ADMIN("superAdmin"),
    INVALID("");

    private final String authority;

    PermissionLevel(String authority) {
        this.authority = authority;
    }

}
