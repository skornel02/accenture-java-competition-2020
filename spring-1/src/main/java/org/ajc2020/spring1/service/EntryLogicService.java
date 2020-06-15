package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;

import java.time.LocalTime;
import java.util.Optional;

public interface EntryLogicService {

    boolean isWorkerAllowedInside(Worker worker);

    LocalTime getEstimatedTimeRemainingForWorker(Worker worker);

}
