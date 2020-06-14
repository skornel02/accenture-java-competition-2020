package org.ajc2020.spring2.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.utilty.communication.MeInformation;
import org.ajc2020.utilty.resource.PermissionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Slf4j
@Controller
@SessionAttributes("login")
public class WebController {

    @Data
    private static class UserInfo {
        public UserInfo() {
        }

        public UserInfo(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        private String userName = "";
        private String password = "";
        private boolean isAdmin;
        private boolean isSuperAdmin;
    }

    @ModelAttribute("login")
    private UserInfo userInfo() {
        return new UserInfo();
    }

    @Value("${frontend.rest.service.url}")
    private String restServiceUrl;

    public WebController() {
    }

    @GetMapping("/login")
    public String login() {

        return "lui";
    }

    @GetMapping("/")
    public String main(@ModelAttribute("login") UserInfo userInfo) {
        try {
            ResponseEntity<MeInformation> response = getRequest(userInfo, "auth-information", MeInformation.class);
        } catch (RestClientException ignored) {
            return "lui";
        }
        return "ui";
    }

    private String joinUrlParts(String... parts) {
        return String.join("/", parts);
    }

    private <T> ResponseEntity<T> getRequest(UserInfo login, String path, Class<T> t) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Get for " + url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.userName, login.password)
                .build();
        return restTemplate.getForEntity(url, t);
    }

    @PostMapping("/login")
    public RedirectView handleLogin(@RequestParam String username, @RequestParam String password, @ModelAttribute("login") UserInfo userInfo) {
        try {
            ResponseEntity<MeInformation> response = getRequest(new UserInfo(username, password), "auth-information", MeInformation.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                userInfo.setPassword(password);
                userInfo.setUserName(username);
                userInfo.setAdmin(response.getBody().getPermission().atLeast(PermissionLevel.ADMIN));
                userInfo.setSuperAdmin(response.getBody().getPermission().atLeast(PermissionLevel.SUPER_ADMIN));
                return new RedirectView("/");
            }
        } catch (RestClientException ignored) {

        }
        return new RedirectView("/login");
    }

    @GetMapping("/logout")
    public RedirectView handleLogout(@ModelAttribute("login") UserInfo userInfo) {
        userInfo.userName = "";
        userInfo.password = "";
        return new RedirectView("/login");
    }
}
