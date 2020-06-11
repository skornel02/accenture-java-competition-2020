package org.ajc2020.spring1.controller;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.WorkerService;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HomeController {

    @Autowired
    private WorkerServiceImpl workerService;

    @RequestMapping("/v3/api-docs")
    public String home() {
        return "It works!";
    }

    @RequestMapping(path = "/v3/api-docs/kecske")
    public String asd() {
        return "kecske";
    }

    @RequestMapping(path = "/v3/api-docs/enroll", method = RequestMethod.POST, produces = "application/json")
    public RedirectView enroll(@ModelAttribute("worker") Worker worker, HttpServletRequest request) {
        workerService.save(worker);
        return new RedirectView("/v3/api-docs");
    }
}
