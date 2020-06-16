package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class EntryLogicServiceImpl implements EntryLogicService {

    private final WorkerService workerService;
    private final OfficeService officeService;

    public EntryLogicServiceImpl(WorkerService workerService, OfficeService officeService) {
        this.workerService = workerService;
        this.officeService = officeService;
    }

    @Override
    public boolean isWorkerAllowedInside(@NotNull Worker worker) {
        int workerRank = getWorkerRank(worker);
        int freeCapacity = getFreeCapacityInBuilding();

        // 15 real capacity | 15 rank = 16th in line => full house
        return freeCapacity > workerRank;
    }

    @Override
    public LocalTime getEstimatedTimeRemainingForWorker(@NotNull Worker worker) {
        LocalTime time = LocalTime.of(0, 0, 0);

        if (isWorkerAllowedInside(worker))
            return time;

        int workerRank = getWorkerRank(worker);

        List<Double> timeRequired = new LinkedList<>();
        List<Worker> workersInOffice = workerService.getUsersInOffice();
        workersInOffice.sort(Comparator.comparingDouble(Worker::getAverageTime));
        List<Worker> workersWaiting = workerService.getUsersWaiting();
        workersWaiting.sort(Comparator.comparing(w -> w.getTicketForDay(w.today()).getCreationDate()));

        for (int i = 0; i <= workerRank; i++) {
            if (i < workersInOffice.size()) {
                timeRequired.add(workersInOffice.get(i).getAverageTime());
            } else {
                if (i == 0 && workersInOffice.size() == 0)
                    continue;
                int k = i - workersInOffice.size();
                timeRequired.add(timeRequired.get(k) + workersWaiting.get(k).getAverageTime());
            }
        }

        return time.plus(Math.round(timeRequired.get(workerRank)), ChronoUnit.MICROS);
    }

    public int getWorkerRank(@NotNull Worker worker) {
        return worker.hasTicketForToday() ?
                workerService.getRank(worker) :
                workerService.countUsersWaiting();
    }

    public int getFreeCapacityInBuilding() {
        return officeService.getOfficeSetting().getEffectiveCapacity() - workerService.countUsersInOffice();
    }

}
