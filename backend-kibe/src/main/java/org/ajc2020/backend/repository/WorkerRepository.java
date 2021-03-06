package org.ajc2020.backend.repository;

import org.ajc2020.backend.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, String> {

    Optional<Worker> findWorkerByRfid(String rfid);

    Optional<Worker> findWorkerByEmail(String email);

    List<Worker> findAll();

    Optional<Worker> findWorkerByUuid(String uuid);

}
