package org.ajc2020.spring1.controller;

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
        station.add(linkTo(methodOn(WorkstationController.class).returnWorkstation(station.getId(), null)).withRel("self"));
        if (station.getOccupier() != null) {
            station.add(linkTo(methodOn(WorkerController.class)
                    .returnWorker(station.getOccupier().getId(), null)).withRel("view"));
        }
        return station;
    }

    @GetMapping
    public List<WorkStationResource> returnWorkstations() {
        return workstationService.findAll().stream()
                .map(Workstation::toResource)
                .map(WorkstationController::addLinks)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<WorkStationResource> createWorkstation(@Valid @RequestBody WorkStationCreationRequest request) {
        Workstation workstation = new Workstation(request);
        workstationService.save(workstation);
        return ResponseEntity.created(URI.create("/workstations/" + workstation.getId())).body(
                WorkstationController.addLinks(workstation.toResource())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkStationResource> returnWorkstation(@PathVariable String id, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        Workstation workstation = workstationService.findById(id)
                .orElseThrow(() -> new WorkstationNotFound(resourceBundle.getString("error.workstation.not.found")));
        return ResponseEntity.ok(
                WorkstationController.addLinks(workstation.toResource())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeWorkstation(@PathVariable String id, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        workstationService.findById(id)
                .orElseThrow(() -> new WorkstationNotFound(resourceBundle.getString("error.workstation.not.found")));
        workstationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
