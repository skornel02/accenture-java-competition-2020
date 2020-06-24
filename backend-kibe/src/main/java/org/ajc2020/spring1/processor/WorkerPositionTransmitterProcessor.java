package org.ajc2020.spring1.processor;

import org.ajc2020.utility.communication.WorkerPosition;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface WorkerPositionTransmitterProcessor {

    String OUT = "worker-position";

    @Output(OUT)
    MessageChannel updateWorkerPosition();

}
