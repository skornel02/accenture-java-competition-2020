package org.ajc2020.spring1.service;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.utility.communication.WorkerPosition;

public interface WorkerPositionService {

    void updateWorkerPosition(Worker worker, int position);

}
