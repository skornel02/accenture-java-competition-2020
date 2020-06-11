package org.ajc2020.spring1.repository;

import org.ajc2020.spring1.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, String> {

    Worker findWorkerByRfid(String rfid);
    Worker findWorkerByEmail(String email);
    List<Worker> findAll();
}
