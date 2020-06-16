package org.ajc2020.spring1.manager;

import org.ajc2020.spring1.model.Admin;
import org.ajc2020.utility.resource.PermissionLevel;
import org.ajc2020.spring1.model.Worker;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionManager {

    public boolean isSessionWorker() {
        return getSession() instanceof Worker;
    }

    public boolean isSessionAdmin() {
        return getSession() instanceof Admin;
    }

    public boolean isSessionDevice() {
        return getPermission() == PermissionLevel.DEVICE && getSession() == null;
    }

    public Worker getWorker() {
        return (Worker) getSession();
    }

    public Admin getAdmin() {
        return (Admin) getSession();
    }

    public Object getSession() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public PermissionLevel getPermission() {
        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        if (authorities.stream().anyMatch(PermissionLevel.SUPER_ADMIN.getAuthority()::equals))
            return PermissionLevel.SUPER_ADMIN;
        if (authorities.stream().anyMatch(PermissionLevel.ADMIN.getAuthority()::equals))
            return PermissionLevel.ADMIN;
        if (authorities.stream().anyMatch(PermissionLevel.WORKER.getAuthority()::equals))
            return PermissionLevel.WORKER;
        if (authorities.stream().anyMatch(PermissionLevel.DEVICE.getAuthority()::equals))
            return PermissionLevel.DEVICE;
        return PermissionLevel.INVALID;
    }

}
