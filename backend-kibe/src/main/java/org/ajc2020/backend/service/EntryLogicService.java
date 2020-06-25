package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Worker;

import java.time.LocalTime;

public interface EntryLogicService {

    boolean isWorkerAllowedInside(Worker worker);

    LocalTime getEstimatedTimeRemainingForWorker(Worker worker);

}
