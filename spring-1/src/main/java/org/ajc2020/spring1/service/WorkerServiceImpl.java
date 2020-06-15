package org.ajc2020.spring1.service;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.model.Ticket;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.repository.WorkerRepository;
import org.ajc2020.utility.resource.WorkerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        return findAll().stream().filter(x -> x.getStatus().equals(WorkerStatus.InOffice)).count();
    }

    @Override
    public long countUsersWaiting() {
        return findAll().stream().filter(x -> x.getStatus().equals(WorkerStatus.OnList)).count();
    }

    @Override
    public long getRank(Worker worker) {
        if (!worker.hasTicketForToday()) return Long.MAX_VALUE;
        Date today = worker.today();
        return findAll().stream()
                .filter(Worker::hasTicketForToday)
                .map(x -> x.getTicketForDay(today))
                .map(Ticket::getCreationDate)
                .filter(x -> x.before(worker.getTicketForDay(today).getCreationDate()))
                .count();
    }

}
