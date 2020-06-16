package org.ajc2020.spring2.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.utility.communication.*;
import org.ajc2020.utility.resource.PermissionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@SessionAttributes("login")
public class WebController {

    @Data
    private static class UserInfo {
        public UserInfo() {
        }

        public UserInfo(String userName, String password, String uuid) {
            this.userName = userName;
            this.password = password;
            this.uuid = uuid;
        }

        private String uuid = "";
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

    private Optional<String> authorize(UserInfo userInfo) {
        String fullName = "Anonymous";
        try {
            ResponseEntity<MeInformation> response = getRequest(userInfo, "auth-information", MeInformation.class);
            MeInformation meInformation = response.getBody();
            if (meInformation != null) {
                if (meInformation.getWorker() != null) fullName = meInformation.getWorker().getName();
                if (meInformation.getAdmin() != null) fullName = meInformation.getAdmin().getName();
            }
        } catch (RestClientException ignored) {
            return Optional.empty();
        }
        return Optional.of(fullName);
    }

    private void setModel(UserInfo userInfo, String fullName, Model model) {
        model.addAttribute("adminMode", userInfo.isAdmin || userInfo.isSuperAdmin);
        model.addAttribute("username", fullName);
        model.addAttribute("uuid", userInfo.uuid);
    }

    @GetMapping("/")
    public String main(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return "lui";
        setModel(userInfo, fullName.get(), model);
        if (!userInfo.isAdmin() && !userInfo.isSuperAdmin()) {
            String[] reservations =
                    Arrays.stream(
                            Objects.requireNonNull(
                                    getRequest(userInfo, "users/" + userInfo.getUuid() + "/tickets", TicketResource[].class)
                                            .getBody()))
                            .map(TicketResource::getTargetDay)
                            .map(x->x.format(DateTimeFormatter.ISO_DATE))
                            .toArray(String[]::new);
            model.addAttribute("reservations", reservations);
        }
        return "ui";
    }

    @GetMapping("/users")
    public String users(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return "lui";
        setModel(userInfo, fullName.get(), model);

        model.addAttribute("users", getRequest(userInfo, "users", WorkerResource[].class).getBody());
        return "usermanagement";
    }

    @GetMapping("/admins")
    public String admions(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return "lui";
        setModel(userInfo, fullName.get(), model);

        if (userInfo.isSuperAdmin()) {
            model.addAttribute("admins", getRequest(userInfo, "admins", AdminResource[].class).getBody());
        }
        return "adminmanagement";
    }

    @PostMapping("/createAdmin")
    public RedirectView createAdmin(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(name = "create.admin.name") String name,
            @RequestParam(name = "create.admin.email") String email,
            @RequestParam(name = "create.admin.password") String password
    ) {
        AdminCreationRequest adminCreationRequest = AdminCreationRequest.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
        try {
            postRequest(userInfo, "admins", adminCreationRequest);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new RedirectView("/admins");

    }

    @GetMapping("/register")
    public RedirectView register(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date
    ) {
        putRequest(userInfo, "users/"+userInfo.getUuid()+"/tickets/"+new SimpleDateFormat("yyyy-MM-dd").format(date), "");
        return new RedirectView("/");
    }

    @GetMapping("/building/capacity")
    public RedirectView setCapacity(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam int capacity,
            @RequestParam double percentage
    ) {
        OfficeResource officeResource = OfficeResource.builder()
                .capacity(capacity)
                .percentage(percentage / 100)
                .build();
        patchRequest(userInfo, "office/settings", officeResource);
        return new RedirectView("/building");
    }

    @GetMapping("/building")
    public String building(
            @ModelAttribute("login") UserInfo userInfo,
            Model model
    ) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return "lui";
        setModel(userInfo, fullName.get(), model);
        try {
            ResponseEntity<OfficeResource> officeResource = getRequest(userInfo, "office/settings", OfficeResource.class);
            model.addAttribute("building", officeResource.getBody());
            return "building";
        } catch (Exception ignored)  {
            return "lui";
        }
    }

    @GetMapping("/cancel")
    public RedirectView cancel(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date
    ) {
        deleteRequest(userInfo, "users/"+userInfo.getUuid()+"/tickets/"+new SimpleDateFormat("yyyy-MM-dd").format(date));
        return new RedirectView("/");
    }

    @GetMapping("/users/{uuid}/reservations")
    public String reservations(
            @ModelAttribute("login") UserInfo userInfo,
            @PathVariable String uuid
    ) {
        try {
            WorkerResource workerResource = getRequest(userInfo, "users/"+uuid, WorkerResource.class).getBody();
            // TODO: 2.0:  get tickets
            return "reservations";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ui";
    }

    @PostMapping("/updateAdmin")
    public RedirectView updateAdmin(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(name = "edit.admin.uuid") String uuid,
            @RequestParam(name = "edit.admin.name") String name,
            @RequestParam(name = "edit.admin.email") String email,
            @RequestParam(name = "edit.admin.password") String password
    ) {
        AdminCreationRequest adminCreationRequest = AdminCreationRequest.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
        try {
            patchRequest(userInfo, "admins/" + uuid, adminCreationRequest);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new RedirectView("/admins");
    }

    @GetMapping("/deleteAdmin/{uuid}")
    public RedirectView deleteAdmin(
            @ModelAttribute("login") UserInfo userInfo,
            @PathVariable(name = "uuid") String uuid
    ) {
        try {
            deleteRequest(userInfo, "admins/" + uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("/users");
    }

    @PostMapping("/createUser")
    public RedirectView createUser(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(name = "create.user.name") String name,
            @RequestParam(name = "create.user.email") String email,
            @RequestParam(name = "create.user.rfid") String rfid,
            @RequestParam(name = "create.user.password") String password
    ) {
        WorkerCreationRequest workerCreationRequest = WorkerCreationRequest.builder()
                .email(email)
                .name(name)
                .password(password)
                .rfId(rfid)
                .build();
        try {
            postRequest(userInfo, "users", workerCreationRequest);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new RedirectView("/users");

    }

    @PostMapping("/updateUser")
    public RedirectView updateUser(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(name = "edit.user.uuid") String uuid,
            @RequestParam(name = "edit.user.name") String name,
            @RequestParam(name = "edit.user.email") String email,
            @RequestParam(name = "edit.user.rfid") String rfid,
            @RequestParam(name = "edit.user.password") String password
    ) {
        WorkerCreationRequest workerCreationRequest = WorkerCreationRequest.builder()
                .email(email)
                .name(name)
                .password(password)
                .rfId(rfid)
                .build();
        try {
            log.info(workerCreationRequest.toString());
            patchRequest(userInfo, "users/" + uuid, workerCreationRequest);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new RedirectView("/users");
    }

    @GetMapping("/deleteUser/{uuid}")
    public RedirectView deleteUser(
            @ModelAttribute("login") UserInfo userInfo,
            @PathVariable(name = "uuid") String uuid
    ) {
        try {
            deleteRequest(userInfo, "users/" + uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("/users");
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

    private <T> void patchRequest(UserInfo login, String path, T input) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Patch for " + url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.userName, login.password)
                .build();
        restTemplate.patchForObject(url, input, String.class);
    }

    private <T> void postRequest(UserInfo login, String path, T input) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Post for " + url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.userName, login.password)
                .build();
        restTemplate.postForObject(url, input, String.class);
    }

    private <T> void putRequest(UserInfo login, String path, T input) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Put for " + url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.userName, login.password)
                .build();
        restTemplate.put(url, input, String.class);
    }

    private void deleteRequest(UserInfo login, String path) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Delete for " + url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.userName, login.password)
                .build();
        restTemplate.delete(url);
    }

    @PostMapping("/login")
    public RedirectView handleLogin(@RequestParam String username, @RequestParam String password, @ModelAttribute("login") UserInfo userInfo) {
        try {
            ResponseEntity<MeInformation> response = getRequest(new UserInfo(username, password, ""), "auth-information", MeInformation.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                userInfo.setPassword(password);
                userInfo.setUserName(username);
                userInfo.setAdmin(Objects.requireNonNull(response.getBody()).getPermission().atLeast(PermissionLevel.ADMIN));
                userInfo.setUuid(response.getBody().getUuid());
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
        userInfo.uuid = "";
        return new RedirectView("/login");
    }

    @GetMapping("/profile")
    public String profile(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) {
            return "lui";
        }
        setModel(userInfo, fullName.get(), model);
        return "userprofile";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PasswordStatus {

        private String status;
        private String reason;
    }

    private PasswordStatus validatePassword(UserInfo userInfo, String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword))
            return new PasswordStatus("Error", "Old and new password must be different!");

        if (!userInfo.getPassword().equals(oldPassword)) return new PasswordStatus("Error", "Old password is not correct!");
        if (newPassword.length() < 8) {
            return new PasswordStatus("Error", "Password must be at least 8 characters long!");
        }
        return new PasswordStatus("OK", "");
    }

    @PostMapping("/checkPassword")
    public @ResponseBody PasswordStatus getPasswordStatus(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(defaultValue = "") String password,
            @RequestParam(defaultValue = "") String newPassword
    ) {
        return validatePassword(userInfo, password, newPassword);
    }

    @PostMapping("/updateProfile")
    public RedirectView updateProfile(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(name = "edit.user.password.current") String currentPassword,
            @RequestParam(name = "edit.user.password") String newPassword
    ) {
        PasswordStatus passwordStatus = validatePassword(userInfo, currentPassword, newPassword);
        if (!passwordStatus.status.equals("OK")) return new RedirectView("/");
        patchRequest(userInfo, "users/" + userInfo.getUuid() + "/password", WorkerCreationRequest.builder().password(newPassword).build());
        userInfo.setPassword(newPassword);
        return new RedirectView("/");
    }

    @GetMapping("/timeToEnter")
    public @ResponseBody RemainingTime getRemainingTime(@ModelAttribute("login") UserInfo userInfo) {
        return getRequest(userInfo, "users/"+userInfo.getUuid()+"/entry-time-remaining", RemainingTime.class).getBody();
    }

}
