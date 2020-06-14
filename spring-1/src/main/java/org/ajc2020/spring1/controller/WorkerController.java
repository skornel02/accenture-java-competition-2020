package org.ajc2020.spring1.controller;

import org.ajc2020.spring1.exceptions.UserCreationFailedException;
import org.ajc2020.spring1.exceptions.UserUpdateFailedException;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.PermissionLevel;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.WorkerService;
import org.ajc2020.utilty.communication.WorkerCreationRequest;
import org.ajc2020.utilty.communication.WorkerResource;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("users")
public class WorkerController {

    private final SessionManager sessionManager;
    private final WorkerService workerService;

    public WorkerController(SessionManager sessionManager,
                            WorkerService workerService) {
        this.sessionManager = sessionManager;
        this.workerService = workerService;
    }

    @GetMapping
    public List<WorkerResource> home(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.admin"));

        return workerService.findAll()
                .stream()
                .map(Worker::toResource)
                .collect(Collectors.toList());
    }

    @GetMapping("/{uuid}")
    public WorkerResource worker(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.admin"));

        return workerService
                .findByUuid(uuid)
                .map(Worker::toResource)
                .orElseThrow(
                        () -> new UserCreationFailedException(HttpStatus.NOT_ACCEPTABLE, resourceBundle.getString("error.user.not.created")));
    }

    @PatchMapping("/{uuid}")
    public WorkerResource updateWorker(@PathVariable String uuid, Locale locale, @RequestBody WorkerCreationRequest workerUpdateRequest) throws UserUpdateFailedException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.admin"));

        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) {
            throw new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found"));
        }
        try {
            workerService.save(worker.get().updateWith(workerUpdateRequest));
            return worker.get().toResource();
        } catch (DataIntegrityViolationException e) {
            throw new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.unable.to.update"));
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteWorker(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.admin"));

        try {
            workerService.deleteByUuid(uuid);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            throw new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found"));
        }
    }

    @PostMapping
    public ResponseEntity<WorkerResource> enroll(@Valid @RequestBody WorkerCreationRequest workerCreationRequest, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, resourceBundle.getString("error.forbidden.admin"));

        Worker worker = new Worker(workerCreationRequest);
        workerService.save(worker);
        return ResponseEntity.created(URI.create("/users/" + worker.getUuid()))
                .body(worker.toResource());
    }


}
