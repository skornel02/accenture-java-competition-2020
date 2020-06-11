package org.ajc2020.spring1.controller;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
public class HomeController {

    @Autowired
    private WorkerServiceImpl workerService;

    @RequestMapping("/v3/api-docs")
    public List<Worker> home() {
        List<Worker> workers = workerService.findAll();
        return workers;
    }

    @RequestMapping(path = "/v3/api-docs/kecske")
    public String asd() {
        return "kecske";
    }


    @RequestMapping(path = "/v3/api-docs/enroll", method = RequestMethod.POST)
    public RedirectView enroll(@RequestBody Worker worker, HttpServletRequest request) {
        workerService.save(worker);
        return new RedirectView("/v3/api-docs");
    }

    @RequestMapping(path = "/v3/api-docs/checkin")
    public String checkin(@RequestParam String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return "Unknown RFID";
        // TODO: refuse login in case of full house
        worker.checkin(new Date());
        workerService.save(worker);
        return "OK";
    }

    @RequestMapping(path = "/v3/api-docs/checkout")
    public String checkout(@RequestParam String rfid) {
        Worker worker = workerService.findByRfid(rfid);
        if (worker == null) return "Unknown RFID";
        // TODO: free up space in the office
        worker.checkout(new Date());
        workerService.save(worker);
        return "OK";
    }


}
