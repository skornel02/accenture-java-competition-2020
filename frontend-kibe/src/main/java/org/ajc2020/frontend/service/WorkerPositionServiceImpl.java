package org.ajc2020.frontend.service;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.frontend.processor.WorkerPositionReceiverProcessor;
import org.ajc2020.utility.communication.WorkerPosition;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

@Service
@Slf4j
public class WorkerPositionServiceImpl implements WorkerPositionService {

    private final HashMap<String, List<IntConsumer>> workerListeners;

    public WorkerPositionServiceImpl() {
        workerListeners = new HashMap<>();
    }

    private List<IntConsumer> getListeners(String workerId) {
        return workerListeners.computeIfAbsent(workerId, id -> new CopyOnWriteArrayList<>());
    }

    @Override
    public void registerListener(String workerId, IntConsumer position) {
        getListeners(workerId).add(position);
    }

    @Override
    public void removeListener(String workerId, IntConsumer position) {
        getListeners(workerId).remove(position);
    }

    @StreamListener(WorkerPositionReceiverProcessor.IN)
    public void handlePositionUpdate(WorkerPosition workerPosition) {
        getListeners(workerPosition.getWorkerId())
                .forEach(consumer -> consumer.accept(workerPosition.getPosition()));
        log.info("Position update event: {}", workerPosition);
    }
}
