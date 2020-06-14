package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    void save(Worker worker);

    List<Worker> findAll();

    Optional<Worker> findByEmail(String email);

    Worker findByRfid(String rfid);

    Optional<Worker> findByUuid(String uuid);

    void deleteByUuid(String uuid);

    long countUsersInOffice();

    long countUsersWaiting();

    long getRank(Worker worker);
}
