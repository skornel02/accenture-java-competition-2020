package org.ajc2020.spring1.controller;

import org.ajc2020.spring1.exceptions.UserUpdateFailedException;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.Admin;
import org.ajc2020.utilty.resource.PermissionLevel;
import org.ajc2020.spring1.service.AdminService;
import org.ajc2020.utilty.communication.AdminCreationRequest;
import org.ajc2020.utilty.communication.AdminResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admins")
public class AdminController {

    private final SessionManager sessionManager;
    private final AdminService adminService;

    public AdminController(SessionManager sessionManager,
                           AdminService adminService) {
        this.sessionManager = sessionManager;
        this.adminService = adminService;
    }

    @GetMapping
    public List<AdminResource> admins(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.SUPER_ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.superadmin"));

        return adminService.findAll()
                .stream()
                .map(Admin::toResource)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<AdminResource> createAdmin(@Valid @RequestBody AdminCreationRequest adminCreationRequest, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.SUPER_ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.superadmin"));

        Admin admin = new Admin(adminCreationRequest);
        adminService.save(admin);
        return ResponseEntity.created(URI.create("/admins/"+admin.getUuid()))
                .body(admin.toResource());
    }

    @GetMapping(path = "/{uuid}")
    public AdminResource getAdmin(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.SUPER_ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.superadmin"));

        return adminService.findByUuid(uuid)
                .map(Admin::toResource)
                .orElseThrow(()->new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found")));
    }

    @PatchMapping("/{uuid}")
    public AdminResource updateAdmin(@PathVariable String uuid, @RequestBody AdminCreationRequest adminUpdateRequest, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.SUPER_ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.superadmin"));

        Optional<Admin> admin = adminService.findByUuid(uuid);
        if (!admin.isPresent()) throw new UserUpdateFailedException(HttpStatus.NOT_FOUND,  resourceBundle.getString("error.user.not.found"));
        adminService.save(admin.get().updateWith(adminUpdateRequest));
        return admin.get().toResource();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.SUPER_ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.superadmin"));

        try {
            adminService.deleteByUuid(uuid);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            throw new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found"));
        }
    }

}
