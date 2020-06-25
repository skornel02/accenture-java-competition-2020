package org.ajc2020.spring1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.ajc2020.spring1.model.Workstation;
import org.ajc2020.spring1.service.WorkstationService;
import org.ajc2020.utility.communication.WorkStationCreationRequest;
import org.ajc2020.utility.communication.WorkStationResource;
import org.ajc2020.utility.exceptions.WorkstationNotFound;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/workstations")
public class WorkstationController {

    private final WorkstationService workstationService;

    public WorkstationController(WorkstationService workstationService) {
        this.workstationService = workstationService;
    }

    public static WorkStationResource addLinks(WorkStationResource station) {
        station.add(linkTo(methodOn(WorkstationController.class).returnWorkstation(station.getId(), Locale.getDefault())).withRel("self"));
        if (station.getOccupier() != null) {
            station.add(linkTo(methodOn(WorkerController.class)
                    .returnWorker(station.getOccupier().getId(), Locale.getDefault())).withRel("occupier"));
        }
        return station;
    }

    @Operation(
            description = "Returns all workstations",
            tags = "Workstations",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping
    public List<WorkStationResource> returnWorkstations() {
        return workstationService.findAll().stream()
                .map(Workstation::toResource)
                .map(WorkstationController::addLinks)
                .collect(Collectors.toList());
    }

    @Operation(
            description = "Create a workstation",
            tags = "Workstations",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @PostMapping
    public ResponseEntity<WorkStationResource> createWorkstation(@Valid @RequestBody WorkStationCreationRequest request) {
        Workstation workstation = new Workstation(request);
        workstationService.save(workstation);
        return ResponseEntity.created(URI.create("/workstations/" + workstation.getId())).body(
                WorkstationController.addLinks(workstation.toResource())
        );
    }

    @Operation(
            description = "Returns a workstation",
            tags = "Workstations",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping("/{id}")
    public ResponseEntity<WorkStationResource> returnWorkstation(@PathVariable String id, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        Workstation workstation = workstationService.findById(id)
                .orElseThrow(() -> new WorkstationNotFound(resourceBundle.getString("error.workstation.not.found")));
        return ResponseEntity.ok(
                WorkstationController.addLinks(workstation.toResource())
        );
    }

    @Operation(
            description = "Removes a workstation",
            tags = "Workstations",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeWorkstation(@PathVariable String id, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if(!workstationService.findById(id).isPresent())
            throw new WorkstationNotFound(resourceBundle.getString("error.workstation.not.found"));
        workstationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            description = "Removes the current user from the workstation",
            tags = "Workstations",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @DeleteMapping("/{id}/occupier")
    public ResponseEntity<WorkStationResource> kickWorker(@PathVariable String id, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        Workstation workStation = workstationService.findById(id)
                .orElseThrow(() -> new WorkstationNotFound(resourceBundle.getString("error.workstation.not.found")));
        workStation.setOccupier(null);
        workstationService.save(workStation);
        return ResponseEntity.ok(workStation.toResource());
    }

    @Operation(
            description = "Allows the use of the workstation",
            tags = "Workstations",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @PostMapping("/{id}/enabled")
    public ResponseEntity<WorkStationResource> enableWorkstation(@PathVariable String id, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        Workstation workStation = workstationService.findById(id)
                .orElseThrow(() -> new WorkstationNotFound(resourceBundle.getString("error.workstation.not.found")));
        if (workStation.isEnabled()) {
            return ResponseEntity.badRequest().body(workStation.toResource());
        }
        workStation.setEnabled(true);
        workstationService.save(workStation);
        return ResponseEntity.ok(workStation.toResource());
    }

    @Operation(
            description = "Disallows the use of the workstation",
            tags = "Workstations",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @DeleteMapping("/{id}/enabled")
    public ResponseEntity<WorkStationResource> disableWorkstation(@PathVariable String id, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        Workstation workStation = workstationService.findById(id)
                .orElseThrow(() -> new WorkstationNotFound(resourceBundle.getString("error.workstation.not.found")));
        if (!workStation.isEnabled()) {
            return ResponseEntity.badRequest().body(workStation.toResource());
        }
        workStation.setEnabled(false);
        workstationService.save(workStation);
        return ResponseEntity.ok(workStation.toResource());
    }

}
