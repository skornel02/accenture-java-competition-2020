package org.ajc2020.spring1;

import org.ajc2020.spring1.model.OfficeSettings;
import org.ajc2020.spring1.repository.OfficeSettingsRepository;
import org.ajc2020.spring1.service.OfficeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OfficeSettingsTest {

    @Autowired
    OfficeService officeService;

    @Autowired
    OfficeSettingsRepository settingsRepository;

    @Test
    public void noMultipleSettings() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0 ; i < 5 ; i++) {
            executor.submit(() -> {
                OfficeSettings settings = officeService.getOfficeSetting();
                System.out.println("settings = " + settings);
                assertNotEquals(null, settings);
            });
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("all = " + settingsRepository.findAll());
        assertEquals(1, settingsRepository.findAll().size());
    }

    @Test
    public void incorrectUpdates() {
        assertThrows(IllegalArgumentException.class, () -> {
            OfficeSettings settings = officeService.getOfficeSetting();
            settings.setCapacity(0);
            officeService.updateOfficeSettings(settings);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            OfficeSettings settings = officeService.getOfficeSetting();
            settings.setOperationPercentage(10);
            officeService.updateOfficeSettings(settings);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            OfficeSettings settings = officeService.getOfficeSetting();
            settings.setOperationPercentage(-3.14);
            officeService.updateOfficeSettings(settings);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            OfficeSettings settings = officeService.getOfficeSetting();
            settings.setCentimetersBetweenEmployeeStations(-42);
            officeService.updateOfficeSettings(settings);
        });
    }

}
