package org.ajc2020.spring2.service;

import java.util.function.IntConsumer;

public interface WorkerPositionService {

    void registerListener(String workerId, IntConsumer position);

    void removeListener(String workerId, IntConsumer position);

}