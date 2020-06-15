package org.ajc2020.spring1.runner;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.config.KIBeConfig;
import org.ajc2020.spring1.model.Admin;
import org.ajc2020.spring1.service.AdminService;
import org.ajc2020.utility.communication.AdminCreationRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SuperAdminRunner implements CommandLineRunner {

    private final KIBeConfig config;
    private final AdminService adminService;

    public SuperAdminRunner(KIBeConfig config, AdminService adminService) {
        this.config = config;
        this.adminService = adminService;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<Admin> adminOptional = adminService.findByEmail(config.getAdmin().getEmail());
        if (!adminOptional.isPresent()) {
            log.info("Admin account not found... generating '{}' super admin account...", config.getAdmin().getEmail());
            adminService.save(new Admin(new AdminCreationRequest(
                    config.getAdmin().getEmail(),
                    "admin",
                    config.getAdmin().getPassword(),
                    true
            )));
        }
    }

}
