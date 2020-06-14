package org.ajc2020.utilty.resource;

import lombok.Getter;

@Getter
public enum PermissionLevel {

    WORKER("worker"),
    ADMIN("admin"),
    SUPER_ADMIN("super-admin"),
    DEVICE("device"),
    INVALID("");

    private final String authority;

    PermissionLevel(String authority) {
        this.authority = authority;
    }

    public boolean atLeast(PermissionLevel level) {
        if (level == DEVICE)
            return this == DEVICE;

        switch (this) {
            case SUPER_ADMIN:
                return true;
            case ADMIN:
                return level != SUPER_ADMIN;
            case WORKER:
                return level == WORKER;
            default:
                return false;
        }
    }

}
