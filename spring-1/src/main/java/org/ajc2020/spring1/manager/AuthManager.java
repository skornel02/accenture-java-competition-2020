package org.ajc2020.spring1.manager;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.WorkerService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthManager {

    private final WorkerService workerService;

    public AuthManager(WorkerService workerService) {
        this.workerService = workerService;
    }

    public Optional<Worker> findValidUser(String username, String password) {
        Optional<Worker> userOptional = workerService.findByUsername(username);

        if (userOptional.isPresent()) {
            Worker user = userOptional.get();
            if (Objects.equals(user.getPassword(), password)) {
                 return userOptional;
            }
        }

        return Optional.empty();
    }

}
