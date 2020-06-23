package org.ajc2020.spring1.service;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.model.Ticket;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.repository.WorkerRepository;
import org.ajc2020.utility.resource.WorkerStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;

    public WorkerServiceImpl(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

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
    public Optional<Worker> findByRfid(String rfid) {
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
    public int countUsersInOffice() {
        return getUsersInOffice().size();
    }

    @Override
    public List<Worker> getUsersInOffice() {
        return findAll().stream()
                .filter(worker -> worker.getStatus() == WorkerStatus.InOffice)
                .collect(Collectors.toList());
    }

    @Override
    public int countUsersWaiting() {
        return getUsersWaiting().size();
    }

    @Override
    public List<Worker> getUsersWaiting() {
        return findAll().stream()
                .filter(worker -> worker.getStatus() == WorkerStatus.OnList)
                .collect(Collectors.toList());
    }

    @Override
    public int getRank(Worker worker) {
        if (!worker.hasTicketForToday()) return Integer.MAX_VALUE;
        LocalDate today = worker.today();
        return Math.toIntExact(findAll().stream()
                .filter(Worker::hasTicketForToday)
                .map(x -> x.getTicketForDay(today))
                .map(Ticket::getCreationDate)
                .filter(x -> x.isBefore(worker.getTicketForDay(today).getCreationDate()))
                .count());
    }

}
