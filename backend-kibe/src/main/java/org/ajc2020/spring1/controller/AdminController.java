package org.ajc2020.spring1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.ajc2020.spring1.manager.AuthManager;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.Admin;
import org.ajc2020.spring1.service.AdminService;
import org.ajc2020.utility.communication.AdminCreationRequest;
import org.ajc2020.utility.communication.AdminResource;
import org.ajc2020.utility.exceptions.ForbiddenException;
import org.ajc2020.utility.exceptions.UserNotFoundException;
import org.ajc2020.utility.resource.PermissionLevel;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("admins")
public class AdminController {

    private final SessionManager sessionManager;
    private final AdminService adminService;
    private final AuthManager authManager;

    public AdminController(SessionManager sessionManager,
                           AdminService adminService,
                           AuthManager authManager) {
        this.sessionManager = sessionManager;
        this.adminService = adminService;
        this.authManager = authManager;
    }

    public static AdminResource addLinks(AdminResource resource) {
        resource.add(linkTo(methodOn(AdminController.class)
                .returnAdmin(resource.getUuid(), null)).withRel("view"));

        return resource;
    }

    @Operation(
            description = "Returns administrators",
            tags = "Administrators",
            security = {@SecurityRequirement(name = "user", scopes = "super-admin")}
    )
    @GetMapping
    public List<AdminResource> returnAdmins() {
        return adminService.findAll()
                        .stream()
                        .map(Admin::toResource)
                        .map(AdminController::addLinks)
                        .collect(Collectors.toList());
    }

    @Operation(
            description = "Creates an administrator",
            tags = "Administrators",
            security = {@SecurityRequirement(name = "user", scopes = "super-admin")}
    )
    @PostMapping
    public ResponseEntity<AdminResource> createAdmin(@Valid @RequestBody AdminCreationRequest adminCreationRequest, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.SUPER_ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.superadmin"));

        Admin admin = new Admin(adminCreationRequest, authManager.encryptPassword(adminCreationRequest.getPassword()));
        adminService.save(admin);
        return ResponseEntity.created(URI.create("/admins/" + admin.getUuid()))
                .body(addLinks(admin.toResource()));
    }

    @Operation(
            description = "Returns a specified administrator",
            tags = "Administrators",
            security = {@SecurityRequirement(name = "user", scopes = "super-admin")}
    )
    @GetMapping(path = "/{uuid}")
    public AdminResource returnAdmin(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

        return adminService.findByUuid(uuid)
                .map(Admin::toResource)
                .map(AdminController::addLinks)
                .orElseThrow(() -> new UserNotFoundException(resourceBundle.getString("error.user.not.found")));
    }

    @Operation(
            description = "Updates a specified administrator",
            tags = "Administrators",
            security = {@SecurityRequirement(name = "user", scopes = "super-admin")}
    )
    @PatchMapping("/{uuid}")
    public AdminResource updateAdmin(@PathVariable String uuid, @RequestBody AdminCreationRequest adminUpdateRequest, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

        Optional<Admin> admin = adminService.findByUuid(uuid);
        if (!admin.isPresent())
            throw new UserNotFoundException(resourceBundle.getString("error.user.not.found"));
        if (!adminUpdateRequest.getPassword().isEmpty()) {
            adminUpdateRequest.setPassword(authManager.encryptPassword(adminUpdateRequest.getPassword()));
        }
        adminService.save(admin.get().updateWith(adminUpdateRequest));
        return addLinks(admin.get().toResource());
    }

    @Operation(
            description = "Removes a specified administrator",
            tags = "Administrators",
            security = {@SecurityRequirement(name = "user", scopes = "super-admin")}
    )
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

        try {
            adminService.deleteByUuid(uuid);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException( resourceBundle.getString("error.user.not.found"));
        }
    }

}
