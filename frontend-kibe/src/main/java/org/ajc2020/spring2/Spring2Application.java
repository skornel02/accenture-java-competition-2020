package org.ajc2020.spring2;

import org.ajc2020.spring2.processor.WorkerPositionReceiverProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(WorkerPositionReceiverProcessor.class)
public class Spring2Application {

    public static void main(String[] args) {
        SpringApplication.run(Spring2Application.class, args);
    }

}
