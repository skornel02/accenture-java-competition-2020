package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;

import java.util.List;

public interface WorkerService {

    void save(Worker worker);

    List<Worker> findAll();

    Worker findByEmail(String email);

    Worker findByRfid(String rfid);
}
