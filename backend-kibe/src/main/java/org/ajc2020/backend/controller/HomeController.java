package org.ajc2020.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.backend.manager.SessionManager;
import org.ajc2020.backend.model.Worker;
import org.ajc2020.backend.model.Workstation;
import org.ajc2020.backend.service.*;
import org.ajc2020.utility.communication.RemainingTime;
import org.ajc2020.utility.exceptions.ForbiddenException;
import org.ajc2020.utility.exceptions.UserNotFoundException;
import org.ajc2020.utility.resource.PermissionLevel;
import org.ajc2020.utility.resource.RegistrationStatus;
import org.ajc2020.utility.resource.RfIdStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@RestController
public class HomeController {

    private final SessionManager sessionManager;
    private final WorkerService workerService;
    private final EntryLogicService entryLogicService;
    private final WorkstationService workstationService;
    private final WorkerPositionService workerPositionService;
    private final PlanRendererService planRendererService;

    public HomeController(SessionManager sessionManager,
                          WorkerService workerService,
                          EntryLogicService entryLogicService,
                          WorkstationService workstationService,
                          WorkerPositionService workerPositionService,
                          PlanRendererService planRendererService) {
        this.sessionManager = sessionManager;
        this.workerService = workerService;
        this.entryLogicService = entryLogicService;
        this.workstationService = workstationService;
        this.workerPositionService = workerPositionService;
        this.planRendererService = planRendererService;
    }

    @Operation(
            description = "Registers an entry request",
            tags = "Entry management",
            security = {@SecurityRequirement(name = "user", scopes = "admin"), @SecurityRequirement(name = "device")}
    )
    @PostMapping(path = "/rfids/{rfid}/checkin")
    public RfIdStatus checkin(@PathVariable String rfid) {
        Optional<Worker> workerOptional = workerService.findByRfid(rfid);
        if (!workerOptional.isPresent()) return RfIdStatus.unknownRfid();
        Worker worker = workerOptional.get();

        if (worker.isExceptional())
            return RfIdStatus.ok();
        if (!entryLogicService.isWorkerAllowedInside(worker)) {
            return RfIdStatus.fullHouse();
        }
        if (worker.checkin(OffsetDateTime.now())) {
            workstationService.occupyWorkstation(worker);
            workerService.save(worker);
            workerService.getUsersWaiting().forEach(waiter
                    -> workerPositionService.updateWorkerPosition(waiter, workerService.getRank(waiter)));
            return RfIdStatus.ok();
        }
        return RfIdStatus.error();
    }

    @Operation(
            description = "Registers an exit request",
            tags = "Entry management",
            security = {@SecurityRequirement(name = "user", scopes = "admin"), @SecurityRequirement(name = "device")}
    )
    @PostMapping(path = "/rfids/{rfid}/checkout")
    public RfIdStatus checkout(@PathVariable String rfid) {
        Optional<Worker> workerOptional = workerService.findByRfid(rfid);
        if (!workerOptional.isPresent()) return RfIdStatus.unknownRfid();
        Worker worker = workerOptional.get();

        if (worker.isExceptional())
            return RfIdStatus.ok();
        if (worker.checkout(OffsetDateTime.now())) {
            workerService.save(worker);
            workstationService.freeWorkstations(worker);
            return RfIdStatus.ok();
        }
        return RfIdStatus.error();
    }

    @Operation(
            description = "Create an entry ticket",
            tags = "Tickets",
            security = {@SecurityRequirement(name = "user")}
    )
    @PutMapping(path = "/users/{uuid}/tickets/{date}")
    public RegistrationStatus register(@PathVariable String uuid,
                                       @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                       Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(uuid, sessionManager.getWorker().getUuid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) return RegistrationStatus.unknownUser();
        if (worker.get().isExceptional()) return RegistrationStatus.notRequired();
        worker.get().register(date);
        workerService.save(worker.get());
        return RegistrationStatus.ok();
    }

    @Operation(
            description = "Remove an entry ticket",
            tags = "Tickets",
            security = {@SecurityRequirement(name = "user")}
    )
    @DeleteMapping("/users/{uuid}/tickets/{date}")
    public RegistrationStatus cancel(@PathVariable String uuid,
                                     @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                     Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(uuid, sessionManager.getWorker().getUuid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) return RegistrationStatus.unknownUser();
        if (worker.get().isExceptional()) return RegistrationStatus.notRequired();
        if (!worker.get().cancel(date)) {
            return RegistrationStatus.error();
        }
        workerService.save(worker.get());
        return RegistrationStatus.ok();
    }

    @Operation(
            description = "Calculate expected waiting time",
            tags = "Tickets",
            security = {@SecurityRequirement(name = "user")}
    )
    @GetMapping("/users/{uuid}/entry-time-remaining")
    public ResponseEntity<RemainingTime> calculateRemainingTimeTillEntry(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(uuid, sessionManager.getWorker().getUuid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        Optional<Worker> workerOpt = workerService.findByUuid(uuid);
        if (!workerOpt.isPresent())
            throw new UserNotFoundException(resourceBundle.getString("error.user.not.found"));
        Worker worker = workerOpt.get();

        return ResponseEntity.ok(
                RemainingTime.builder()
                        .projectedEntryTime(entryLogicService.getEstimatedTimeRemainingForWorker(worker))
                        .status(worker.getStatus())
                        .workstation(worker.getStation() != null
                                ? WorkstationController.addLinks(worker.getStation().toResource()).setOccupier(null)
                                : null)
                        .locationSVG(worker.getStation() != null
                                ? planRendererService.createWorker2DSVG(workstationService.findAll(), worker.getStation())
                                : null)
                        .build()
        );
    }

    @Operation(
            description = "Returns all workstations in svg form",
            tags = "Layout",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping(value = "/layout", produces = "image/svg+xml")
    public ResponseEntity<String> returnWorkstationLayout() {
        List<Workstation> workstations = workstationService.findAll();
        List<Workstation> occupiable = workstationService.findAllOccupiable();

        return ResponseEntity.ok(
                planRendererService.createAdmin2DSVG(workstations, occupiable)
        );
    }

    @Operation(
            description = "Returns all workstations in 3 dimensional svg form",
            tags = "Layout",
            security = {@SecurityRequirement(name = "user", scopes = "admin")}
    )
    @GetMapping(value = "/layout/3d", produces = "image/svg+xml")
    public ResponseEntity<String> returnWorkstationLayout3d() {
        List<Workstation> workstations = workstationService.findAll();
        List<Workstation> occupiable = workstationService.findAllOccupiable();

        return ResponseEntity.ok(
                planRendererService.createAdmin3DSVG(workstations, occupiable)
        );
    }

}
