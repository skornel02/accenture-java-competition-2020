package org.ajc2020.spring1.controller;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class HomeController {

    @Autowired
    private WorkerServiceImpl workerService;

    @GetMapping("/v3/api-docs")
    public List<Worker> home() {
        return workerService.findAll();
    }

    @PostMapping(path = "/v3/api-docs/enroll")
    public RedirectView enroll(@RequestBody Worker worker) {
        workerService.save(worker);
        return new RedirectView("/v3/api-docs");
    }

    @GetMapping(path = "/v3/api-docs/checkin")
    public String checkin(@RequestParam String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return "Unknown RFID";
        // TODO: refuse login in case of full house
        if (worker.checkin(new Date())) {
            workerService.save(worker);
            return "OK";
        }
        return "Error";
    }

    @GetMapping(path = "/v3/api-docs/checkout")
    public String checkout(@RequestParam String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return "Unknown RFID";
        // TODO: free up space in the office
        if (worker.checkout(new Date())) {
            workerService.save(worker);
            return "OK";
        }
        return "Error";
    }

    @GetMapping(path = "/v3/api-docs/register")
    public String register(@RequestParam String email, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        Optional<Worker> worker = workerService.findByEmail(email);
        if (!worker.isPresent()) return "Unknown email address!";
        // TODO: register / cancel events should be bound to user
        if (!worker.get().register(date)) {
            return "Error";
        }
        workerService.save(worker.get());
        return "OK";
    }
    @GetMapping(path = "/v3/api-docs/cancel")
    public String cancel(@RequestParam String email, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        Optional<Worker> worker = workerService.findByEmail(email);
        if (!worker.isPresent()) return "Unknown email address!";
        // TODO: register / cancel events should be bound to user
        if (!worker.get().cancel(date)) {
            return "Error";
        }
        workerService.save(worker.get());
        return "OK";
    }


}
