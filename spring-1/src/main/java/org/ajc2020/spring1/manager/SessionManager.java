package org.ajc2020.spring1.manager;

import org.ajc2020.spring1.model.Worker;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SessionManager {

    public boolean isSessionWorker() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Worker;
    }

    public Worker getWorker(){
        return (Worker) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Object getSession () {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
