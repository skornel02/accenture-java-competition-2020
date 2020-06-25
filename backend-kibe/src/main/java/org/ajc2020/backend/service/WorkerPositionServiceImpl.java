package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Worker;
import org.ajc2020.backend.processor.WorkerPositionTransmitterProcessor;
import org.ajc2020.utility.communication.WorkerPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class WorkerPositionServiceImpl implements WorkerPositionService {

    private final WorkerPositionTransmitterProcessor processor;

    @Autowired
    public WorkerPositionServiceImpl(WorkerPositionTransmitterProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void updateWorkerPosition(Worker worker, int position) {
        processor.updateWorkerPosition().send(MessageBuilder.withPayload(
                new WorkerPosition(worker.getUuid(), position)
        ).build());
    }
}
