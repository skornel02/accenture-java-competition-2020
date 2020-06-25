package org.ajc2020.frontend.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.frontend.communication.PasswordStatus;
import org.ajc2020.frontend.communication.UserInfo;
import org.ajc2020.utility.communication.*;
import org.ajc2020.utility.resource.PermissionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@SessionAttributes("login")
public class WebController {

    @ModelAttribute("login")
    private UserInfo userInfo() {
        return new UserInfo();
    }

    @Value("${frontend.rest.service.url}")
    private String restServiceUrl;

    public WebController() {
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("login") UserInfo userInfo, Model model) {
        return requireLogin(userInfo, model);
    }

    private Optional<String> authorize(UserInfo userInfo) {
        String fullName = "Anonymous";
        userInfo.setBackendReady(true);
        try {
            ResponseEntity<MeInformation> response = getRequest(userInfo, "auth-information", MeInformation.class);
            MeInformation meInformation = response.getBody();
            if (meInformation != null) {
                if (meInformation.getWorker() != null) fullName = meInformation.getWorker().getName();
                if (meInformation.getAdmin() != null) fullName = meInformation.getAdmin().getName();
            }
        } catch (HttpClientErrorException.Forbidden e) {

            return Optional.empty();
        } catch (RestClientException e) {
            userInfo.setBackendReady(false);
            return Optional.empty();
        }
        return Optional.of(fullName);
    }

    private void setModel(UserInfo userInfo, String fullName, Model model) {
        model.addAttribute("actingAdmin", false);
        model.addAttribute("backendReady", userInfo.isBackendReady());
        model.addAttribute("adminMode", userInfo.isAdmin() || userInfo.isSuperAdmin());
        model.addAttribute("username", fullName);
        model.addAttribute("uuid", userInfo.getUuid());
    }

    private String requireLogin(UserInfo userInfo, Model model) {
        setModel(userInfo, "", model);
        return "lui";
    }

    @GetMapping("/")
    public String main(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return requireLogin(userInfo, model);
        setModel(userInfo, fullName.get(), model);
        if (!userInfo.isAdmin() && !userInfo.isSuperAdmin()) {
            String[] reservations =
                    Arrays.stream(
                            Objects.requireNonNull(
                                    getRequest(userInfo, "users/" + userInfo.getUuid() + "/tickets", TicketResource[].class)
                                            .getBody()))
                            .map(TicketResource::getTargetDay)
                            .map(x -> x.format(DateTimeFormatter.ISO_DATE))
                            .toArray(String[]::new);
            model.addAttribute("reservations", reservations);
        } else {
            InsideResourcePlain[] workersIn = getRequest(userInfo, "office/inside", InsideResourcePlain[].class).getBody();
            model.addAttribute("workersIn", workersIn);
            WaitingResourcePlain[] workersWaiting = getRequest(userInfo, "office/waiting", WaitingResourcePlain[].class).getBody();
            // TODO: sort workers by rank
            model.addAttribute("workersWaiting", workersWaiting);
        }
        return "ui";
    }

    @GetMapping("/users")
    public String users(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return requireLogin(userInfo, model);
        setModel(userInfo, fullName.get(), model);

        model.addAttribute("users", getRequest(userInfo, "users", WorkerResource[].class).getBody());
        return "usermanagement";
    }

    @GetMapping("/admins")
    public String admions(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return requireLogin(userInfo, model);
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
        if (!fullName.isPresent()) return requireLogin(userInfo, model);
        setModel(userInfo, fullName.get(), model);
        try {
            ResponseEntity<OfficeResource> officeResource = getRequest(userInfo, "office/settings", OfficeResource.class);
            model.addAttribute("building", officeResource.getBody());
            return "building";
        } catch (Exception ignored) {
            return requireLogin(userInfo, model);
        }
    }

    @GetMapping("/register")
    public RedirectView register(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam String rid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date
    ) {
        putRequest(userInfo, "users/" + rid + "/tickets/" + new SimpleDateFormat("yyyy-MM-dd").format(date), "");
        putRequest(userInfo, "users/" + rid + "/tickets/" + new SimpleDateFormat("yyyy-MM-dd").format(date), "");
        if (userInfo.isAdmin() || userInfo.isSuperAdmin())
            return new RedirectView("/userview?rid=" + rid);
        return new RedirectView("/");
    }

    @GetMapping("/cancel")
    public RedirectView cancel(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam String rid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date
    ) {
        deleteRequest(userInfo, "users/" + rid + "/tickets/" + new SimpleDateFormat("yyyy-MM-dd").format(date));
        if (userInfo.isAdmin() || userInfo.isSuperAdmin())
            return new RedirectView("/userview?rid=" + rid);
        return new RedirectView("/");
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
        return new RedirectView("/admins");
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
        log.info("Get for {}", url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.getUserName(), login.getPassword())
                .build();
        return restTemplate.getForEntity(url, t);
    }

    private <T> String patchRequest(UserInfo login, String path, T input) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Patch for {}", url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.getUserName(), login.getPassword())
                .build();
        return restTemplate.patchForObject(url, input, String.class);
    }

    private <T> String postRequest(UserInfo login, String path, T input) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Post for {}", url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.getUserName(), login.getPassword())
                .build();
        return restTemplate.postForObject(url, input, String.class);
    }

    private <T> void putRequest(UserInfo login, String path, T input) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Put for {}", url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.getUserName(), login.getPassword())
                .build();
        restTemplate.put(url, input, String.class);
    }

    private String deleteRequest(UserInfo login, String path) {
        String url = joinUrlParts(restServiceUrl, path);
        log.info("Delete for {}", url);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(login.getUserName(), login.getPassword())
                .build();
        return restTemplate.exchange(url, HttpMethod.DELETE, null, String.class).getBody();
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
        userInfo.setUserName("");
        userInfo.setPassword("");
        userInfo.setUuid("");
        return new RedirectView("/login");
    }

    @GetMapping("/profile")
    public String profile(@ModelAttribute("login") UserInfo userInfo, Model model) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) {
            return requireLogin(userInfo, model);
        }
        setModel(userInfo, fullName.get(), model);
        return "userprofile";
    }

    final int PASSWORD_LENGTH_MIN = 8;

    private PasswordStatus validatePassword(UserInfo userInfo, String oldPassword, String newPassword, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
        if (oldPassword.equals(newPassword))
            return new PasswordStatus("Error", resourceBundle.getString("profile.passwords.same"));

        if (!userInfo.getPassword().equals(oldPassword))
            return new PasswordStatus("Error", resourceBundle.getString("profile.old.password.incorrect"));

        if (newPassword.length() < PASSWORD_LENGTH_MIN) {
            return new PasswordStatus("Error", MessageFormat.format(resourceBundle.getString("profile.password.length"), PASSWORD_LENGTH_MIN));
        }
        return new PasswordStatus("OK", "");
    }

    @PostMapping("/checkPassword")
    public @ResponseBody
    PasswordStatus getPasswordStatus(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(defaultValue = "") String oldPassword,
            @RequestParam(defaultValue = "") String newPassword,
            Locale locale
    ) {
        return validatePassword(userInfo, oldPassword, newPassword, locale);
    }

    @PostMapping("/updateProfile")
    public RedirectView updateProfile(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam(name = "edit.user.password.current") String currentPassword,
            @RequestParam(name = "edit.user.password") String newPassword,
            Locale locale
    ) {
        PasswordStatus passwordStatus = validatePassword(userInfo, currentPassword, newPassword, locale);
        if (!passwordStatus.getStatus().equals("OK")) return new RedirectView("/");
        patchRequest(userInfo, "users/" + userInfo.getUuid() + "/password", WorkerCreationRequest.builder().password(newPassword).build());
        userInfo.setPassword(newPassword);
        return new RedirectView("/");
    }

    @GetMapping("/timeToEnter")
    public @ResponseBody
    RemainingTime getRemainingTime(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam String rid
    ) {
        return getRequest(userInfo, "users/" + rid + "/entry-time-remaining", RemainingTime.class).getBody();
    }

    @GetMapping("/checkin/{rfid}")
    public RedirectView checkin(
            @ModelAttribute("login") UserInfo userInfo,
            @PathVariable String rfid
    ) {
        postRequest(userInfo, "rfids/" + rfid + "/checkin", String.class);
        return new RedirectView("/users");
    }

    @GetMapping("/checkout/{rfid}")
    public RedirectView checkout(
            @ModelAttribute("login") UserInfo userInfo,
            @PathVariable String rfid
    ) {
        postRequest(userInfo, "rfids/" + rfid + "/checkout", String.class);
        return new RedirectView("/users");
    }

    @GetMapping("/userview")
    public String reservations(
            @ModelAttribute("login") UserInfo userInfo,
            @RequestParam String rid,
            Model model
    ) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return requireLogin(userInfo, model);
        WorkerResource workerResource = getRequest(userInfo, "users/" + rid, WorkerResource.class).getBody();
        UserInfo workerInfo = new UserInfo(Objects.requireNonNull(workerResource).getEmail(), "", workerResource.getId());
        setModel(workerInfo, fullName.get() + " Effective: " + workerResource.getName(), model);
        model.addAttribute("actingAdmin", true);

        String[] reservations =
                Arrays.stream(
                        Objects.requireNonNull(
                                getRequest(userInfo, "users/" + rid + "/tickets", TicketResource[].class)
                                        .getBody()))
                        .map(TicketResource::getTargetDay)
                        .map(x -> x.format(DateTimeFormatter.ISO_DATE))
                        .toArray(String[]::new);
        model.addAttribute("reservations", reservations);

        model.addAttribute("adminMode", userInfo.isAdmin() || userInfo.isSuperAdmin());

        return "ui";
    }


    @GetMapping("/plan")
    public String buildingPlan(
            @ModelAttribute("login") UserInfo userInfo,
            Model model,
            @RequestParam(defaultValue = "2") int dim
    ) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return requireLogin(userInfo, model);
        setModel(userInfo, fullName.get(), model);

        model.addAttribute("places",
                getRequest(userInfo, "workstations", WorkstationResource[].class)
                        .getBody());
        return (dim == 3) ? "plan3d" : "plan";
    }

    @GetMapping("/plan/update/{planId}/{operation}")
    public @ResponseBody
    PasswordStatus updatePlan(
            @ModelAttribute("login") UserInfo userInfo,
            @PathVariable String planId,
            @PathVariable String operation
    ) {
        Optional<String> fullName = authorize(userInfo);
        if (!fullName.isPresent()) return new PasswordStatus("Error", "Unauthorized");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        String result;
        try {
            if (operation.equals("permit")) {
                result = postRequest(userInfo, "workstations/" + planId + "/enabled", "");
                WorkstationResource r = objectMapper.readValue(result, WorkstationResource.class);
                if (r.isEnabled()) return new PasswordStatus("OK", "Updated");
            }
            if (operation.equals("forbid")) {
                result = deleteRequest(userInfo, "workstations/" + planId + "/enabled");
                WorkstationResource r = objectMapper.readValue(result, WorkstationResource.class);
                if (!r.isEnabled()) return new PasswordStatus("OK", "Updated");
            }
            if (operation.equals("kick")) {
                result = deleteRequest(userInfo, "workstations/" + planId + "/occupier");
                WorkstationResource r = objectMapper.readValue(result, WorkstationResource.class);
                if (r.getOccupier() == null) return new PasswordStatus("OK", "Updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new PasswordStatus("Error", "Unkown result");
        }
        return new PasswordStatus("Error", "Unknown method");
    }
}
