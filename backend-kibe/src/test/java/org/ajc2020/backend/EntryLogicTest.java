package org.ajc2020.backend;

import org.ajc2020.backend.model.OfficeSettings;
import org.ajc2020.backend.model.Worker;
import org.ajc2020.backend.service.EntryLogicServiceImpl;
import org.ajc2020.backend.service.OfficeService;
import org.ajc2020.backend.service.WorkerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EntryLogicTest {

    private static final int TEST_FOLD_AMOUNT = 5;

    @Autowired
    EntryLogicServiceImpl entryLogicService;

    @MockBean
    WorkerServiceImpl workerService;

    @MockBean
    OfficeService officeService;

    OfficeSettings settings;

    double insideAverageTime = 30d * 60 * 1000 * 1000;
    double waiterAverageTime = 60d * 60 * 1000 * 1000;

    @BeforeEach
    public void setUp() {
        settings = new OfficeSettings();
        settings.setCapacity(5);
        settings.setOperationPercentage(1);

        Mockito.when(officeService.getOfficeSetting()).thenReturn(settings);
    }

    @Test
    public void emptyEntryTest() {
        Worker withTicket = new Worker();
        withTicket.register(withTicket.today());
        Worker withoutTicket = new Worker();

        assertTrue(entryLogicService.isWorkerAllowedInside(withTicket));
        assertTrue(entryLogicService.isWorkerAllowedInside(withoutTicket));
    }

    @Test
    public void complexFullHouseTest() {
        for (int fold = 0 ; fold < TEST_FOLD_AMOUNT ; fold++) {
            List<Worker> inside = new LinkedList<>();
            for (int i = 0 ; i < settings.getCapacity() ; i++) {
                Worker insideWorker = new Worker();
                insideWorker.setAverageTime(insideAverageTime);
                inside.add(insideWorker);
            }
            Mockito.when(workerService.getUsersInOffice()).thenReturn(inside);
            Mockito.when(workerService.countUsersInOffice()).thenReturn(inside.size());

            List<Worker> waiting = new LinkedList<>();
            for (int i = 0 ; i < settings.getCapacity() * fold ; i++) {
                Worker waitingWorker = new Worker();
                waitingWorker.setAverageTime(waiterAverageTime);
                waitingWorker.register(waitingWorker.today());
                waiting.add(waitingWorker);
            }
            Mockito.when(workerService.getUsersWaiting()).thenReturn(waiting);
            Mockito.when(workerService.countUsersWaiting()).thenReturn(waiting.size());

            Worker withTicket = new Worker();
            withTicket.register(withTicket.today());
            Worker withoutTicket = new Worker();

            Mockito.when(workerService.getRank(withTicket)).thenReturn(waiting.size());

            assertFalse(entryLogicService.isWorkerAllowedInside(withTicket));
            assertFalse(entryLogicService.isWorkerAllowedInside(withoutTicket));

            LocalTime expected = LocalTime.of(0,0).plus(Math.round(insideAverageTime), ChronoUnit.MICROS);
            for (int i = 0 ; i < fold ; i++) {
                expected = expected.plus(Math.round(waiterAverageTime), ChronoUnit.MICROS);
            }

            assertEquals(expected, entryLogicService.getEstimatedTimeRemainingForWorker(withTicket));
            assertEquals(expected, entryLogicService.getEstimatedTimeRemainingForWorker(withoutTicket));
            System.out.println("Expected time with fold " + fold + " is " + expected.toString());
        }
    }

}
