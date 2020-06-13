package org.ajc2020.spring1.controller;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.ajc2020.utilty.resource.RegistrationStatus;
import org.ajc2020.utilty.resource.RfIdStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Slf4j
@RestController
public class HomeController {

    @Autowired
    private WorkerServiceImpl workerService;

    @GetMapping("/users")
    public List<Worker> home(Locale locale) {
        return workerService.findAll();
    }

    @GetMapping("/users/{uuid}")
    public Worker worker(@PathVariable String uuid) {
        return workerService.findByUuid(uuid).orElse(null);
    }

    @PostMapping(path = "/users/enroll")
    public RedirectView enroll(@RequestBody Worker worker) {
        workerService.save(worker);
        return new RedirectView("/users/" + worker.getUuid());
    }

    @GetMapping(path = "/rfid/{rfid}/checkin")
    public RfIdStatus checkin(@PathVariable String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return RfIdStatus.UnkownRfid();
        // TODO: refuse login in case of full house
        if (worker.checkin(new Date())) {
            workerService.save(worker);
            return RfIdStatus.Ok();
        }
        return RfIdStatus.Error();
    }

    @GetMapping(path = "/rfid/{rfid}/checkout")
    public RfIdStatus checkout(@PathVariable String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return RfIdStatus.UnkownRfid();
        // TODO: free up space in the office
        if (worker.checkout(new Date())) {
            workerService.save(worker);
            return RfIdStatus.Ok();
        }
        return RfIdStatus.Error();
    }

    @GetMapping(path = "/users/{uuid}/tickets/{date}/register")
    public RegistrationStatus register(@PathVariable String uuid, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) return new RegistrationStatus(RegistrationStatus.Status.UnknownUser);
        // TODO: register / cancel events should be bound to user
        if (!worker.get().register(date)) {
            return new RegistrationStatus(RegistrationStatus.Status.Error);
        }
        workerService.save(worker.get());
        return new RegistrationStatus(RegistrationStatus.Status.OK);
    }

    @GetMapping(path = "/users/{uuid}/tickets/{date}/cancel")
    public RegistrationStatus cancel(@PathVariable String uuid, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) return new RegistrationStatus(RegistrationStatus.Status.UnknownUser);
        // TODO: register / cancel events should be bound to user
        if (!worker.get().cancel(date)) {
            return new RegistrationStatus(RegistrationStatus.Status.Error);
        }
        workerService.save(worker.get());
        return new RegistrationStatus(RegistrationStatus.Status.OK);
    }


}
