package org.ajc2020.spring1;

import org.ajc2020.spring1.processor.WorkerPositionTransmitterProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(WorkerPositionTransmitterProcessor.class)
public class Spring1Application {

    public static void main(String[] args) {
        SpringApplication.run(Spring1Application.class, args);
    }

}
