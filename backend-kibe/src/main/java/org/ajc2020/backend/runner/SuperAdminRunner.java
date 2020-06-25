package org.ajc2020.backend.runner;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.backend.config.KIBeConfig;
import org.ajc2020.backend.manager.AuthManager;
import org.ajc2020.backend.model.Admin;
import org.ajc2020.backend.service.AdminService;
import org.ajc2020.utility.communication.AdminCreationRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SuperAdminRunner implements CommandLineRunner {

    private final KIBeConfig config;
    private final AdminService adminService;
    private final AuthManager authManager;

    public SuperAdminRunner(KIBeConfig config,
                            AdminService adminService,
                            AuthManager authManager) {
        this.config = config;
        this.adminService = adminService;
        this.authManager = authManager;
    }

    @SuppressWarnings("RedundantThrows") // OFC this is an intellij warning when it knows this MUST be in the signature
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
            ), authManager.encryptPassword(config.getAdmin().getPassword())));
        }
    }

}
