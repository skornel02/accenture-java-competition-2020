package org.ajc2020.spring1.controller;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.OfficeService;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.ajc2020.utilty.resource.RegistrationStatus;
import org.ajc2020.utilty.resource.RfIdStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@Slf4j
@RestController
public class HomeController {

    @Autowired
    private WorkerServiceImpl workerService;

    @Autowired
    private OfficeService officeService;

    @GetMapping(path = "/rfids/{rfid}/checkin")
    public RfIdStatus checkin(@PathVariable String rfid) {
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

    @GetMapping(path = "/rfids/{rfid}/checkout")
    public RfIdStatus checkout(@PathVariable String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return RfIdStatus.unknownRfid();
        if (worker.checkout(new Date())) {
            workerService.save(worker);
            return RfIdStatus.ok();
        }
        return RfIdStatus.error();
    }

    @PutMapping(path = "/users/{uuid}/tickets/{date}")
    public RegistrationStatus register(@PathVariable String uuid, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) return new RegistrationStatus(RegistrationStatus.Status.UnknownUser);
        worker.get().register(date);
        workerService.save(worker.get());
        return new RegistrationStatus(RegistrationStatus.Status.OK);
    }

    @DeleteMapping(path = "/users/{uuid}/tickets/{date}")
    public RegistrationStatus cancel(@PathVariable String uuid, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) return new RegistrationStatus(RegistrationStatus.Status.UnknownUser);
        if (!worker.get().cancel(date)) {
            return new RegistrationStatus(RegistrationStatus.Status.Error);
        }
        workerService.save(worker.get());
        return new RegistrationStatus(RegistrationStatus.Status.OK);
    }

}
