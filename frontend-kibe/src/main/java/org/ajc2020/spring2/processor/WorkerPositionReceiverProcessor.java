package org.ajc2020.spring2.processor;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
public interface WorkerPositionReceiverProcessor {

    String IN = "worker-position";

    @Input(IN)
    SubscribableChannel sourceOfWorkerPosition();

}
