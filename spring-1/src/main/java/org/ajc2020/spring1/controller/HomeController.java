package org.ajc2020.spring1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.EntryLogicService;
import org.ajc2020.spring1.service.WorkerService;
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

    public HomeController(SessionManager sessionManager,
                          WorkerService workerService,
                          EntryLogicService entryLogicService) {
        this.sessionManager = sessionManager;
        this.workerService = workerService;
        this.entryLogicService = entryLogicService;
    }

    @Operation(
            description = "Registers an entry request",
            tags = "Entry management",
            security = {@SecurityRequirement(name = "user", scopes = "admin"), @SecurityRequirement(name = "device")}
    )
    @GetMapping(path = "/rfids/{rfid}/checkin")
    public RfIdStatus checkin(@PathVariable String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return RfIdStatus.unknownRfid();

        if (!entryLogicService.isWorkerAllowedInside(worker)) {
            return RfIdStatus.fullHouse();
        }
        if (worker.checkin(OffsetDateTime.now())) {
            workerService.save(worker);
            return RfIdStatus.ok();
        }
        return RfIdStatus.error();
    }

    @Operation(
            description = "Registers an exit request",
            tags = "Entry management",
            security = {@SecurityRequirement(name = "user", scopes = "admin"), @SecurityRequirement(name = "device")}
    )
    @GetMapping(path = "/rfids/{rfid}/checkout")
    public RfIdStatus checkout(@PathVariable String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return RfIdStatus.unknownRfid();
        if (worker.checkout(OffsetDateTime.now())) {
            workerService.save(worker);
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
        if (!worker.isPresent()) return new RegistrationStatus(RegistrationStatus.Status.UnknownUser);
        worker.get().register(date);
        workerService.save(worker.get());
        return new RegistrationStatus(RegistrationStatus.Status.OK);
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
        if (!worker.isPresent()) return new RegistrationStatus(RegistrationStatus.Status.UnknownUser);
        if (!worker.get().cancel(date)) {
            return new RegistrationStatus(RegistrationStatus.Status.Error);
        }
        workerService.save(worker.get());
        return new RegistrationStatus(RegistrationStatus.Status.OK);
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
                        .build()
        );
    }


}
