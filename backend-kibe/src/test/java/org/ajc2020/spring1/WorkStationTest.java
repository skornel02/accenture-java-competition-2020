package org.ajc2020.spring1;

import org.ajc2020.spring1.model.OfficeSettings;
import org.ajc2020.spring1.model.WorkStation;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.OfficeServiceImpl;
import org.ajc2020.spring1.service.WorkStationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkStationTest {

    @SpyBean
    WorkStationService workStationService;

    @MockBean
    OfficeServiceImpl officeService;

    List<WorkStation> stations;

    @BeforeEach
    public void setUp() {
        stations = new LinkedList<>();
        Mockito.when(workStationService.findAll()).thenReturn(stations);

        OfficeSettings settings = new OfficeSettings();
        settings.setCentimetersBetweenEmployeeStations(new WorkStation().pixelToCentimeter(2));
        Mockito.when(officeService.getOfficeSetting()).thenReturn(settings);
    }

    @Test
    public void testDistanceCalculator() {
        WorkStation station1 = new WorkStation();
        station1.setX(1);
        station1.setY(2);
        WorkStation station2 = new WorkStation();
        station2.setX(station1.getX() + 3);
        station2.setY(station1.getY() - 4);
        assertEquals(5, station1.distanceFrom(station2));
    }

    @Test
    public void testAvailability() {
        WorkStation isolated = new WorkStation();
        isolated.setId("isolated");
        isolated.setX(-10);
        isolated.setY(-10);
        stations.add(isolated);

        WorkStation twoFreeNeighbours1 = new WorkStation();
        twoFreeNeighbours1.setId("twoFreeNeighbours1");
        twoFreeNeighbours1.setY(-5);
        twoFreeNeighbours1.setX(0);
        WorkStation twoFreeNeighbours2 = new WorkStation();
        twoFreeNeighbours2.setId("twoFreeNeighbours2");
        twoFreeNeighbours2.setY(-5);
        twoFreeNeighbours2.setX(1);
        stations.add(twoFreeNeighbours1);
        stations.add(twoFreeNeighbours2);

        WorkStation inRow1 = new WorkStation();
        inRow1.setId("inRow1");
        inRow1.setY(0);
        inRow1.setX(0);
        inRow1.setOccupier(new Worker());
        WorkStation inRow2 = new WorkStation();
        inRow2.setId("inRow2");
        inRow2.setY(0);
        inRow2.setX(1);
        WorkStation inRow3 = new WorkStation();
        inRow3.setId("inRow3");
        inRow3.setY(0);
        inRow3.setX(2);
        stations.add(inRow1);
        stations.add(inRow2);
        stations.add(inRow3);

        WorkStation inSecondRow1 = new WorkStation();
        inSecondRow1.setId("inSecondRow1");
        inSecondRow1.setY(5);
        inSecondRow1.setX(0);
        WorkStation inSecondRow2 = new WorkStation();
        inSecondRow2.setId("inSecondRow2");
        inSecondRow2.setY(5);
        inSecondRow2.setX(1);
        inSecondRow2.setOccupier(new Worker());
        WorkStation inSecondRow3 = new WorkStation();
        inSecondRow3.setId("inSecondRow3");
        inSecondRow3.setY(5);
        inSecondRow3.setX(2);
        stations.add(inSecondRow1);
        stations.add(inSecondRow2);
        stations.add(inSecondRow3);

        System.out.println("workStationService.findAll() = " + workStationService.findAll());
        System.out.println("workStationService.findAllFree() = " + workStationService.findAllFree());
        System.out.println("workStationService.findAllInUse() = " + workStationService.findAllInUse());
        System.out.println("workStationService.findAllOccupiable() = " + workStationService.findAllOccupiable());

        assertEquals(9, workStationService.findAll().size());
        assertEquals(7, workStationService.findAllFree().size());
        assertEquals(2, workStationService.findAllInUse().size());

        assertTrue(workStationService.findAllOccupiable().contains(isolated));
        assertTrue(workStationService.findAllOccupiable().contains(twoFreeNeighbours1));
        assertTrue(workStationService.findAllOccupiable().contains(twoFreeNeighbours2));
        assertFalse(workStationService.findAllOccupiable().contains(inRow1));
        assertFalse(workStationService.findAllOccupiable().contains(inRow2));
        assertTrue(workStationService.findAllOccupiable().contains(inRow3));
        assertFalse(workStationService.findAllOccupiable().contains(inSecondRow1));
        assertFalse(workStationService.findAllOccupiable().contains(inSecondRow2));
        assertFalse(workStationService.findAllOccupiable().contains(inSecondRow3));
    }

}
