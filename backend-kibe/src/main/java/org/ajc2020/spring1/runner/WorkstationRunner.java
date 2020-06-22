package org.ajc2020.spring1.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.spring1.model.Workstation;
import org.ajc2020.spring1.service.WorkstationService;
import org.ajc2020.utility.communication.SeatResource;
import org.ajc2020.utility.resource.DeskOrientation;
import org.apache.commons.codec.Charsets;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class WorkstationRunner implements CommandLineRunner {
    private final WorkstationService workstationService;

    public WorkstationRunner(WorkstationService workstationService) {
        this.workstationService = workstationService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (workstationService.findAll().size() == 0) {
            Pattern pattern = Pattern.compile("L([\\d]*)R([\\d]*)C([\\d]*)");
            ObjectMapper objectMapper = new ObjectMapper();
            SeatResource[] places = objectMapper.readValue(Resources.toString(Resources.getResource("default-seating.json"), Charsets.UTF_8), SeatResource[].class);
            for (SeatResource seatResource : places) {
                Workstation workstation = new Workstation();
                workstation.setId(seatResource.getId());
                workstation.setEnabled(true);
                switch (seatResource.getRotation()) {
                    case 0:
                        workstation.setOrientation(DeskOrientation.DOWN);
                        break;
                    case 1:
                        workstation.setOrientation(DeskOrientation.LEFT);
                        break;
                    case 2:
                        workstation.setOrientation(DeskOrientation.UP);
                        break;
                    case 3:
                        workstation.setOrientation(DeskOrientation.RIGHT);
                        break;
                }
                workstation.setX(seatResource.getX());
                workstation.setY(seatResource.getY());

                log.info("Adding workstation [{}]", seatResource.getId());
                Matcher m = pattern.matcher(seatResource.getId());
                if (m.matches()) {
                    workstation.setZone(Integer.parseInt(m.group(1)));

                    workstationService.save(workstation);
                } else {
                    log.warn(seatResource.getId() + " not added!");
                }
            }
        }
    }
}
