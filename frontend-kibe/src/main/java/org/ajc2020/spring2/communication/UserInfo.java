package org.ajc2020.spring2.communication;

import lombok.Data;

@Data
public class UserInfo {
    public UserInfo() {
    }

    public UserInfo(String userName, String password, String uuid) {
        this.userName = userName;
        this.password = password;
        this.uuid = uuid;
    }

    private String uuid = "";
    private String userName = "";
    private String password = "";
    private boolean isAdmin;
    private boolean isSuperAdmin;
    private boolean isBackendReady;
}
