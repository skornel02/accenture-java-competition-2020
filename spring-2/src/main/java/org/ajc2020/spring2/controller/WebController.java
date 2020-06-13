package org.ajc2020.spring2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
public class WebController {

    @GetMapping("/login")
    public String login(Model model) {

        return "lui";
    }

    @GetMapping("/")
    public String main() {
        return "ui";
    }
}
