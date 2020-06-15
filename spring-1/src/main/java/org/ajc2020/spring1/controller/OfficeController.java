package org.ajc2020.spring1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.OfficeHours;
import org.ajc2020.spring1.model.OfficeSettings;
import org.ajc2020.spring1.service.EntryLogicService;
import org.ajc2020.spring1.service.OfficeService;
import org.ajc2020.spring1.service.WorkerService;
import org.ajc2020.utility.communication.*;
import org.ajc2020.utility.exceptions.ForbiddenException;
import org.ajc2020.utility.resource.PermissionLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("office")
public class OfficeController {

    private final SessionManager sessionManager;
    private final OfficeService officeService;
    private final WorkerService workerService;
    private final EntryLogicService entryLogicService;

    public OfficeController(SessionManager sessionManager,
                            OfficeService officeService,
                            WorkerService workerService,
                            EntryLogicService entryLogicService) {
        this.sessionManager = sessionManager;
        this.officeService = officeService;
        this.workerService = workerService;
        this.entryLogicService = entryLogicService;
    }

    @Operation(
            description = "Retrieves office settings",
            tags = "Office",
            security = {@SecurityRequirement(name = "user")}
    )
    @GetMapping("/settings")
    public ResponseEntity<OfficeResource> returnOfficeSettings(Locale locale) {
        return ResponseEntity.ok(officeService.getOfficeSetting().toResource());
    }

    @Operation(
            description = "Updates office settings",
            tags = "Office",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @PatchMapping("/settings")
    public ResponseEntity<OfficeResource> updateOfficeSettings(@Valid @RequestBody OfficeResource newValues, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));
        OfficeSettings currentSettings = officeService.getOfficeSetting();
        currentSettings.fromResource(newValues);
        officeService.updateOfficeSettings(currentSettings);
        return ResponseEntity.ok(currentSettings.toResource());
    }

    @Operation(
            description = "Returns the workers inside the building",
            tags = "Office",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping("/inside")
    public List<InsideResource> returnInsideResource(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return workerService.getUsersInOffice().stream()
                .map(worker -> {
                    WorkerResource workerResource = WorkerController.addLinks(worker.toResource());
                    OfficeHoursResource officeHoursResource = worker.getOfficeHoursHistory().stream()
                            .filter(officeHours -> officeHours.getLeave() == null)
                            .findAny().get().toResource()
                            .add(linkTo(methodOn(WorkerController.class)
                                    .returnOfficeHours(workerResource.getId(), null))
                                    .withRel("view"));

                    return new InsideResource(workerResource, officeHoursResource);
                })
                .collect(Collectors.toList());
    }

    @Operation(
            description = "Returns the workers waiting to enter the building",
            tags = "Office",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping("/waiting")
    public List<WaitingResource> returnWaitingResource(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return workerService.getUsersWaiting().stream()
                .map(worker -> {
                    WorkerResource workerResource = WorkerController.addLinks(worker.toResource());
                    TicketResource ticketResource = worker.getTicketForDay(worker.today()).toResource();
                    ticketResource.add(linkTo(methodOn(WorkerController.class)
                            .returnTickets(workerResource.getId(), null)).withRel("view"));
                    boolean canGoIn = entryLogicService.isWorkerAllowedInside(worker);
                    return new WaitingResource(workerResource, ticketResource, canGoIn);
                })
                .collect(Collectors.toList());
    }

    @Operation(
            description = "Returns the workers that have been in the building and left",
            tags = "Office",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping("/been-inside")
    public List<BeenInsideResource> returnBeenInsideResources(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        return workerService.findAll().stream()
                .filter(worker -> worker.getOfficeHoursHistory().stream()
                        .anyMatch(hours -> hours.getArrive().toLocalDate().isEqual(worker.today()) && hours.getLeave() != null))
                .map(worker -> {
                    WorkerResource workerResource = WorkerController.addLinks(worker.toResource());

                    List<OfficeHoursResource> officeHoursResources = worker.getOfficeHoursHistory().stream()
                            .filter(hours -> hours.getLeave() != null)
                            .filter(hours -> hours.getArrive().toLocalDate().isEqual(worker.today()))
                            .map(OfficeHours::toResource)
                            .map(resource -> resource
                                    .add(linkTo(methodOn(WorkerController.class)
                                            .returnOfficeHours(workerResource.getId(), null))
                                            .withRel("view")))
                            .collect(Collectors.toList());
                    return new BeenInsideResource(workerResource, officeHoursResources);
                })
                .collect(Collectors.toList());
    }

}
