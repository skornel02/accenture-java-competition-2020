package org.ajc2020.backend.processor;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface WorkerPositionTransmitterProcessor {

    String OUT = "worker-position";

    @Output(OUT)
    MessageChannel updateWorkerPosition();

}
