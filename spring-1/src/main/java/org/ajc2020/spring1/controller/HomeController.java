package org.ajc2020.spring1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.OfficeService;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.ajc2020.utilty.exceptions.ForbiddenException;
import org.ajc2020.utilty.resource.PermissionLevel;
import org.ajc2020.utilty.resource.RegistrationStatus;
import org.ajc2020.utilty.resource.RfIdStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
public class HomeController {

    private final SessionManager sessionManager;
    private final WorkerServiceImpl workerService;
    private final OfficeService officeService;

    public HomeController(SessionManager sessionManager,
                          OfficeService officeService,
                          WorkerServiceImpl workerService) {
        this.sessionManager = sessionManager;
        this.officeService = officeService;
        this.workerService = workerService;
    }

    @Operation(
            description = "Registers an entry request",
            tags = "Entry management",
            security = {@SecurityRequirement(name = "user", scopes = "admin"), @SecurityRequirement(name = "device")}
    )
    @GetMapping(path = "/rfids/{rfid}/checkin")
    public RfIdStatus checkin(@PathVariable String rfid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(rfid, sessionManager.getWorker().getRfid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return RfIdStatus.unknownRfid();
        long workerRank = worker.hasTicketForToday() ?
                workerService.getRank(worker) :
                workerService.countUsersWaiting();

        if (officeService.getOfficeSetting().getEffectiveCapacity() >=
                workerService.countUsersInOffice() + workerRank) {
            return RfIdStatus.fullHouse();
        }
        if (worker.checkin(new Date())) {
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
    public RfIdStatus checkout(@PathVariable String rfid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        if (!sessionManager.getPermission().atLeast(PermissionLevel.ADMIN)
                && !(sessionManager.isSessionWorker() && Objects.equals(rfid, sessionManager.getWorker().getRfid())))
            throw new ForbiddenException(resourceBundle.getString("error.forbidden.admin"));

        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return RfIdStatus.unknownRfid();
        if (worker.checkout(new Date())) {
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
                                       @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                       Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
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
    @DeleteMapping(path = "/users/{uuid}/tickets/{date}")
    public RegistrationStatus cancel(@PathVariable String uuid,
                                     @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                     Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
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

}
