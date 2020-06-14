package org.ajc2020.spring1.service;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.repository.WorkerRepository;
import org.ajc2020.utilty.resource.WorkerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WorkerServiceImpl implements WorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public void save(Worker worker) {
        workerRepository.save(worker);
    }

    @Override
    public List<Worker> findAll() {
        return workerRepository.findAll();
    }

    public Optional<Worker> findByEmail(String email) {
        return workerRepository.findWorkerByEmail(email);
    }

    @Override
    public Worker findByRfid(String rfid) {
        return workerRepository.findWorkerByRfid(rfid);
    }

    @Override
    public Optional<Worker> findByUuid(String uuid) {
        return workerRepository.findWorkerByUuid(uuid);
    }

    @Override
    public void deleteByUuid(String uuid) {
        workerRepository.deleteById(uuid);
    }

    @Override
    public long countUsersInOffice() {
        return findAll().stream().filter(x->x.getStatus().equals(WorkerStatus.InOffice)).count();
    }

}
