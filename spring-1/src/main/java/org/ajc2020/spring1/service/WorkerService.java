package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;

public interface WorkerService {

    void save(Worker worker);

    Worker findByEmail(String email);
}
