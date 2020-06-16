package org.ajc2020.spring1.manager;

import org.ajc2020.spring1.model.Admin;
import org.ajc2020.spring1.model.User;
import org.ajc2020.spring1.service.AdminService;
import org.ajc2020.spring1.service.GoogleTokenService;
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
    private final GoogleTokenService googleTokenService;

    public AuthManager(WorkerService workerService,
                       AdminService adminService,
                       BCryptPasswordEncoder passwordEncoder,
                       GoogleTokenService googleTokenService) {
        this.workerService = workerService;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
        this.googleTokenService = googleTokenService;
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

    public Optional<User> findValidUserGoogle(String googleToken) {
        return googleTokenService.parseOpenIdToken(googleToken)
                .flatMap(email -> {
                    Optional<User> admin = adminService.findByEmail(email).map(a -> a);
                    if (admin.isPresent())
                        return admin;
                    return workerService.findByEmail(email).map(a -> a);
                });
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }


}
