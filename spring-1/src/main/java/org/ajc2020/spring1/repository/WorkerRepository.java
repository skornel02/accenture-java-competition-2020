package org.ajc2020.spring1.repository;

import org.ajc2020.spring1.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, String> {

    Worker findWorkerByRfid(String rfid);

    Optional<Worker> findWorkerByEmail(String email);

    List<Worker> findAll();

    Optional<Worker> findWorkerByUuid(String uuid);

}
