package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Worker;

public interface WorkerPositionService {

    void updateWorkerPosition(Worker worker, int position);

}
