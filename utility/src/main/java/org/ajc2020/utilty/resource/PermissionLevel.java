package org.ajc2020.utilty.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum PermissionLevel {

    WORKER("worker"),
    ADMIN("admin"),
    SUPER_ADMIN("super-admin"),
    DEVICE("device"),
    INVALID("");


    private final String authority;

    PermissionLevel() {
        this.authority = "";
    }

    PermissionLevel(String authority) {
        this.authority = authority;
    }

    public boolean atLeast(PermissionLevel level) {
        switch (this) {
            case SUPER_ADMIN:
                return true;
            case DEVICE:
            case ADMIN:
                return level != SUPER_ADMIN;
            case WORKER:
                return level == WORKER;
            default:
                return false;
        }
    }

}
