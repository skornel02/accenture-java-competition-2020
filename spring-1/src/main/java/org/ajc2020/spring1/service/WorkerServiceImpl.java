package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WorkerServiceImpl implements WorkerService {
    Logger logger = LoggerFactory.getLogger(WorkerServiceImpl.class);

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

    public Worker findByEmail(String email) {
        return workerRepository.findWorkerByEmail(email);
    }

    @Override
    public Worker findByRfid(String rfid) {
        return workerRepository.findWorkerByRfid(rfid);
    }
}
