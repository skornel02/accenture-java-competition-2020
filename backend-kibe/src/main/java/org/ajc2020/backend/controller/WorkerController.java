package org.ajc2020.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.backend.manager.AuthManager;
import org.ajc2020.backend.manager.SessionManager;
import org.ajc2020.backend.model.OfficeHours;
import org.ajc2020.backend.model.Ticket;
import org.ajc2020.backend.model.Worker;
import org.ajc2020.backend.service.WorkerService;
import org.ajc2020.utility.communication.OfficeHoursResource;
import org.ajc2020.utility.communication.TicketResource;
import org.ajc2020.utility.communication.WorkerCreationRequest;
import org.ajc2020.utility.communication.WorkerResource;
import org.ajc2020.utility.exceptions.ForbiddenException;
import org.ajc2020.utility.exceptions.UserNotFoundException;
import org.ajc2020.utility.exceptions.UserUpdateFailedException;
import org.ajc2020.utility.resource.PermissionLevel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("users")
public class WorkerController {

    private final SessionManager sessionManager;
    private final WorkerService workerService;
    private final AuthManager authManager;

    public WorkerController(SessionManager sessionManager,
                            WorkerService workerService,
                            AuthManager authManager) {
        this.sessionManager = sessionManager;
        this.workerService = workerService;
        this.authManager = authManager;
    }

    public static WorkerResource addLinks(WorkerResource resource) {
        resource.add(linkTo(methodOn(WorkerController.class)
                .returnWorker(resource.getId(), Locale.getDefault())).withRel("view"));
        resource.add(linkTo(methodOn(WorkerController.class)
                .returnOfficeHours(resource.getId(), Locale.getDefault())).withRel("office hours"));
        resource.add(linkTo(methodOn(WorkerController.class)
                .returnTickets(resource.getId(), Locale.getDefault())).withRel("tickets"));
        resource.add(linkTo(methodOn(HomeController.class)
                .register(resource.getRfId(), null, Locale.getDefault())).withRel("manage ticket"));
        resource.add(linkTo(methodOn(HomeController.class)
                .calculateRemainingTimeTillEntry(resource.getId(), Locale.getDefault())).withRel("view estimated time"));

        return resource;
    }

    @Operation(
            description = "Returns all workers",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping
    public List<WorkerResource> returnWorkers(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return workerService.findAll()
                .stream()
                .map(Worker::toResource)
                .map(WorkerController::addLinks)
                .collect(Collectors.toList());
    }

    @Operation(
            description = "Creates new worker",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @PostMapping
    public ResponseEntity<WorkerResource> createWorker(@Valid @RequestBody WorkerCreationRequest workerCreationRequest, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        Worker worker = new Worker(workerCreationRequest, authManager.encryptPassword(workerCreationRequest.getPassword()));
        workerService.save(worker);
        return ResponseEntity.created(URI.create("/users/" + worker.getUuid()))
                .body(addLinks(worker.toResource()));
    }

    @Operation(
            description = "Returns specified worker",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping("/{uuid}")
    public WorkerResource returnWorker(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(uuid, sessionManager.getWorker().getUuid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return workerService
                .findByUuid(uuid)
                .map(Worker::toResource)
                .map(WorkerController::addLinks)
                .orElseThrow(
                        () -> new UserNotFoundException(resourceBundle.getString("error.user.not.found")));
    }

    @Operation(
            description = "Updates specified worker",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @PatchMapping("/{uuid}")
    public WorkerResource updateWorker(@PathVariable String uuid, Locale locale, @RequestBody WorkerCreationRequest workerUpdateRequest) throws UserUpdateFailedException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return updateWorkerResource(uuid, workerUpdateRequest, resourceBundle);
    }

    @Operation(
            description = "Updates Worker password for non-admins",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user")}
    )
    @PatchMapping("/{uuid}/password")
    public WorkerResource updateWorkerPassword(@PathVariable String uuid, Locale locale, @RequestBody WorkerCreationRequest workerUpdateRequest) throws UserUpdateFailedException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.isSessionWorker() || (!sessionManager.getWorker().getUuid().equals(uuid)))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return updateWorkerResource(uuid, workerUpdateRequest, resourceBundle);
    }

    private WorkerResource updateWorkerResource(String uuid, WorkerCreationRequest workerUpdateRequest, ResourceBundle resourceBundle) {
        log.info("Update for {}", uuid);
        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) {
            log.info("Worker not found");
            throw new UserNotFoundException( resourceBundle.getString("error.user.not.found"));
        }
        try {
            if (!workerUpdateRequest.getPassword().isEmpty()) {
                workerUpdateRequest.setPassword(authManager.encryptPassword(workerUpdateRequest.getPassword()));
            }
            workerService.save(worker.get().updateWith(workerUpdateRequest));

            return addLinks(worker.get().toResource());
        } catch (DataIntegrityViolationException e) {
            throw new UserUpdateFailedException( resourceBundle.getString("error.user.unable.to.update") + " - " + e.getMessage());
        }
    }

    @Operation(
            description = "Removes specified worker",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteWorker(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        try {
            workerService.deleteByUuid(uuid);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(resourceBundle.getString("error.user.not.found"));
        }
    }

    @Operation(
            description = "Returns office hours from worker",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping("/{uuid}/office-hours")
    public List<OfficeHoursResource> returnOfficeHours(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(uuid, sessionManager.getWorker().getUuid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return workerService
                .findByUuid(uuid)
                .map(Worker::getOfficeHoursHistory)
                .map(list -> list.stream()
                        .map(OfficeHours::toResource)
                        .collect(Collectors.toList()))
                .orElseThrow(
                        () -> new UserNotFoundException(resourceBundle.getString("error.user.not.found")));
    }

    @Operation(
            description = "Returns tickets from worker",
            tags = "Workers",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping("/{uuid}/tickets")
    public List<TicketResource> returnTickets(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(uuid, sessionManager.getWorker().getUuid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return workerService
                .findByUuid(uuid)
                .map(Worker::getTickets)
                .map(list -> list.stream()
                        .map(Ticket::toResource)
                        .collect(Collectors.toList()))
                .orElseThrow(
                        () -> new UserNotFoundException(resourceBundle.getString("error.user.not.found")));
    }

}
