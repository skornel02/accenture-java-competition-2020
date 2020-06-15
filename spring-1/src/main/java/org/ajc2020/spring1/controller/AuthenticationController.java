package org.ajc2020.spring1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.WorkerService;
import org.ajc2020.utility.communication.MeInformation;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class AuthenticationController {

    private final SessionManager sessionManager;
    private final WorkerService workerService;

    public AuthenticationController(SessionManager sessionManager, WorkerService workerService) {
        this.sessionManager = sessionManager;
        this.workerService = workerService;
    }

    @GetMapping("/auth-information")
    @Operation(
            description = "Returns permission and user resource (if supported)",
            tags = "Authentication",
            security = {@SecurityRequirement(name = "user"), @SecurityRequirement(name = "device")}
    )
    public ResponseEntity<MeInformation> returnInformation() {
        MeInformation.MeInformationBuilder builder = MeInformation.builder()
                .permission(sessionManager.getPermission());
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(OfficeController.class).returnOfficeSettings(null)).withRel("office-settings"));
        if (sessionManager.isSessionWorker()) {
            Worker worker = workerService.findByUuid(sessionManager.getWorker().getUuid()).get();
            builder = builder.worker(worker.toResource());
            links.add(linkTo(methodOn(WorkerController.class)
                    .returnWorker(worker.getUuid(), null)).withRel("self"));
            links.add(linkTo(methodOn(HomeController.class)
                    .register(worker.getRfid(), null, null)).withRel("manage ticket"));
            links.add(linkTo(methodOn(WorkerController.class)
                    .returnOfficeHours(worker.getUuid(), null)).withRel("office hours"));
            links.add(linkTo(methodOn(WorkerController.class)
                    .returnTickets(worker.getUuid(), null)).withRel("tickets"));
            links.add(linkTo(methodOn(HomeController.class)
                    .calculateRemainingTimeTillEntry(worker.getUuid(), null)).withRel("view estimated time"));
        }
        if (sessionManager.isSessionAdmin()) {
            builder = builder.admin(sessionManager.getAdmin().toResource());
            links.add(linkTo(methodOn(AdminController.class)
                    .returnAdmin(sessionManager.getAdmin().getUuid(), null)).withRel("self"));
            links.add(linkTo(methodOn(WorkerController.class).returnWorkers(null)).withRel("workers"));
            links.add(linkTo(methodOn(OfficeController.class).returnBeenInsideResources(null)).withRel("workers that left"));
            links.add(linkTo(methodOn(OfficeController.class).returnInsideResource(null)).withRel("workers inside"));
            links.add(linkTo(methodOn(OfficeController.class).returnWaitingResource(null)).withRel("workers waiting"));
            if (sessionManager.getAdmin().isSuperAdmin()) {
                links.add(linkTo(methodOn(AdminController.class).returnAdmins(null)).withRel("admins"));
            }
        }
        return ResponseEntity.ok(builder.build().add(links));
    }

}
