package org.ajc2020.spring1.controller;

import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.utilty.communication.DeviceResource;
import org.ajc2020.utilty.communication.MeInformation;
import org.ajc2020.utilty.communication.UserType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final SessionManager sessionManager;

    public AuthenticationController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @GetMapping("/auth-information")
    public ResponseEntity<MeInformation> returnInformation() {
        UserType type = new DeviceResource();
        if (sessionManager.isSessionWorker()) {
            type = sessionManager.getWorker().toResource();
        }
        if (sessionManager.isSessionAdmin()) {
            type = sessionManager.getAdmin().toResource();
        }
        return ResponseEntity.ok(MeInformation.builder()
                .permission(sessionManager.getPermission())
                .user(type)
                .build());
    }

}
