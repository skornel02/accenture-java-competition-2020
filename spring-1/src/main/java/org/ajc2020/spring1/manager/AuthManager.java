package org.ajc2020.spring1.manager;

import org.ajc2020.spring1.model.User;
import org.ajc2020.spring1.service.AdminService;
import org.ajc2020.spring1.service.WorkerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AuthManager {

    private final WorkerService workerService;
    private final AdminService adminService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthManager(WorkerService workerService,
                       AdminService adminService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.workerService = workerService;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findValidUser(String email, String password) {
        Optional<User> adminOptional = adminService.findByEmail(email).map(a -> a);
        Optional<User> userOptional = workerService.findByEmail(email).map(a -> a);
        return Stream.of(adminOptional, userOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(user -> passwordEncoder.matches(password, user.getLoginPassword()))
                .findFirst();
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
