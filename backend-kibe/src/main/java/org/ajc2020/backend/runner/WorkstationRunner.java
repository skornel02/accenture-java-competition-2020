package org.ajc2020.backend.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ajc2020.backend.model.Workstation;
import org.ajc2020.backend.service.WorkstationService;
import org.ajc2020.utility.communication.SeatResource;
import org.ajc2020.utility.resource.DeskOrientation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkstationRunner implements CommandLineRunner {
    private final WorkstationService workstationService;

    public WorkstationRunner(WorkstationService workstationService) {
        this.workstationService = workstationService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (workstationService.findAll().isEmpty()) {
            log.info("No workstations detected, running importer...");
            Pattern pattern = Pattern.compile("L([\\d]*)R([\\d]*)C([\\d]*)");
            ObjectMapper objectMapper = new ObjectMapper();
            String defaultSeating = new BufferedReader(new InputStreamReader(new ClassPathResource("default-seating.json").getInputStream())).lines().collect(Collectors.joining());
            SeatResource[] places = objectMapper.readValue(defaultSeating, SeatResource[].class);
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
                    default:
                        break;
                }
                workstation.setX(seatResource.getX());
                workstation.setY(seatResource.getY());

                log.trace("Adding workstation [{}]", seatResource.getId());
                Matcher m = pattern.matcher(seatResource.getId());
                if (m.matches()) {
                    workstation.setZone(Integer.parseInt(m.group(1)));

                    workstationService.save(workstation);
                } else {
                    log.warn("{} from default seating cannot be added!", seatResource.getId());
                }
            }
        }
    }
}
