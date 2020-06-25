package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Worker;

import java.util.List;
import java.util.Optional;

public interface WorkerService {

    void save(Worker worker);

    List<Worker> findAll();

    Optional<Worker> findByEmail(String email);

    Optional<Worker> findByRfid(String rfid);

    Optional<Worker> findByUuid(String uuid);

    void deleteByUuid(String uuid);

    List<Worker> getUsersInOffice();

    int countUsersInOffice();

    List<Worker> getUsersWaiting();

    int countUsersWaiting();

    int getRank(Worker worker);
}
