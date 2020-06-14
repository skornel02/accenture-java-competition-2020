package org.ajc2020.spring1.controller;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.exceptions.UserCreationFailedException;
import org.ajc2020.spring1.exceptions.UserUpdateFailedException;
import org.ajc2020.spring1.model.Admin;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.AdminServiceImpl;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.ajc2020.utilty.communication.AdminCreationRequest;
import org.ajc2020.utilty.communication.AdminResource;
import org.ajc2020.utilty.communication.WorkerCreationRequest;
import org.ajc2020.utilty.communication.WorkerResource;
import org.ajc2020.utilty.resource.RegistrationStatus;
import org.ajc2020.utilty.resource.RfIdStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Slf4j
@RestController
public class HomeController {

    @Autowired
    private WorkerServiceImpl workerService;

    @Autowired
    private AdminServiceImpl adminService;

    @GetMapping("/users")
    public List<Worker> home(Locale locale) {
        return workerService.findAll();
    }

    @GetMapping("/users/{uuid}")
    public WorkerResource worker(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        return workerService
                .findByUuid(uuid)
                .map(Worker::toResource)
                .orElseThrow(
                        () -> new UserCreationFailedException(HttpStatus.NOT_ACCEPTABLE, resourceBundle.getString("error.user.not.created")));
    }

    @PatchMapping("/users/{uuid}")
    public void updateWorker(@PathVariable String uuid, Locale locale, @RequestBody WorkerCreationRequest workerUpdateRequest) throws UserUpdateFailedException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        Optional<Worker> worker = workerService.findByUuid(uuid);
        if (!worker.isPresent()) {
            throw new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found"));
        }
        try {
            workerService.save(worker.get().updateWith(workerUpdateRequest));
        } catch (DataIntegrityViolationException e) {
            throw new UserUpdateFailedException(HttpStatus.NOT_ACCEPTABLE, resourceBundle.getString("error.user.unable.to.update"));
        }
    }

    @DeleteMapping("/users/{uuid}")
    public void deleteWorker(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        try {
            workerService.deleteByUuid(uuid);
        } catch (EmptyResultDataAccessException e) {
            throw new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found"));
        }
    }

    @PostMapping(path = "/users")
    public RedirectView enroll(@RequestBody WorkerCreationRequest workerCreationRequest) {
        Worker worker = new Worker(workerCreationRequest);
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

    @PostMapping(path = "/admins")
    public RedirectView createAdmin(@RequestBody AdminCreationRequest adminCreationRequest) {
        Admin admin = new Admin(adminCreationRequest);
        adminService.save(admin);
        return new RedirectView("/admins/"+admin.getUuid());
    }

    @GetMapping(path = "/admins")
    public List<Admin> admins() {
        return adminService.findAll();
    }

    @GetMapping(path = "/admins/{uuid}")
    public AdminResource getAdmin(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        return adminService.findByUuid(uuid).map(Admin::toResource).orElseThrow(()->new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found")));
    }

    @PatchMapping("/admins/{uuid}")
    public void updateAdmin(@PathVariable String uuid, @RequestBody AdminCreationRequest adminUpdateRequest, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        Optional<Admin> admin = adminService.findByUuid(uuid);
        if (!admin.isPresent()) throw new UserUpdateFailedException(HttpStatus.NOT_FOUND,  resourceBundle.getString("error.user.not.found"));
        adminService.save(admin.get().updateWith(adminUpdateRequest));
    }

    @DeleteMapping("/admins/{uuid}")
    public void deleteAdmin(@PathVariable String uuid, Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(locale.getDisplayLanguage()));
        try {
            adminService.deleteByUuid(uuid);
        } catch (EmptyResultDataAccessException e) {
            throw new UserUpdateFailedException(HttpStatus.NOT_FOUND, resourceBundle.getString("error.user.not.found"));
        }
    }

}
