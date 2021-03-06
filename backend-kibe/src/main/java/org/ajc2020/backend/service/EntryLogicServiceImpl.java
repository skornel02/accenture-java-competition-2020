package org.ajc2020.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.backend.model.Worker;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class EntryLogicServiceImpl implements EntryLogicService {

    private final WorkerService workerService;
    private final OfficeService officeService;
    private final WorkstationService workstationService;

    public EntryLogicServiceImpl(WorkerService workerService,
                                 OfficeService officeService,
                                 WorkstationService workstationService) {
        this.workerService = workerService;
        this.officeService = officeService;
        this.workstationService = workstationService;
    }

    @Override
    public boolean isWorkerAllowedInside(@NotNull Worker worker) {
        int workerRank = getWorkerRank(worker);
        int freeCapacity = getFreeCapacityInBuilding();

        // 15 real capacity | 15 rank = 16th in line => full house
        return freeCapacity > workerRank && workstationService.occupiableWorkstationExists();
    }

    @Override
    public LocalTime getEstimatedTimeRemainingForWorker(@NotNull Worker worker) {
        LocalTime time = LocalTime.of(0, 0, 0);

        if (worker.isExceptional() || isWorkerAllowedInside(worker))
            return time;

        int effectiveCapacity = officeService.getOfficeSetting().getEffectiveCapacity();
        int workerRank = getWorkerRank(worker);

        List<Double> timeRequired = new LinkedList<>();
        List<Worker> workersInOffice = workerService.getUsersInOffice();
        workersInOffice.sort(Comparator.comparingDouble(Worker::getAverageTime));
        List<Worker> workersWaiting = workerService.getUsersWaiting();
        workersWaiting.sort(Comparator.comparing(w -> w.getTicketForDay(w.today()).getCreationDate()));

        int indexOfPersonThatWillFreeYourSpace = (workerRank) - effectiveCapacity + workersInOffice.size();

        for (Worker value : workersInOffice) {
            timeRequired.add(value.getAverageTime());
        }

        for (int i = 0; i < workerRank; i++) {
            if (effectiveCapacity > timeRequired.size()) {
                timeRequired.add(workersWaiting.get(i).getAverageTime());
            } else {
                timeRequired.add(
                        workersWaiting.get(i).getAverageTime()
                                + timeRequired.get(i - effectiveCapacity + workersInOffice.size())
                );
            }
        }

        return time.plus(Math.round(timeRequired.get(indexOfPersonThatWillFreeYourSpace)), ChronoUnit.MICROS);
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
