package org.ajc2020.spring1;

import org.ajc2020.spring1.model.OfficeSettings;
import org.ajc2020.spring1.model.Workstation;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.OfficeServiceImpl;
import org.ajc2020.spring1.service.WorkstationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkstationTest {

    @SpyBean
    WorkstationService workStationService;

    @MockBean
    OfficeServiceImpl officeService;

    List<Workstation> stations;

    @BeforeEach
    public void setUp() {
        stations = new LinkedList<>();
        Mockito.when(workStationService.findAll()).thenReturn(stations);

        OfficeSettings settings = new OfficeSettings();
        settings.setCentimetersBetweenEmployeeStations(new Workstation().unitToCentimeter(2));
        Mockito.when(officeService.getOfficeSetting()).thenReturn(settings);
    }

    @Test
    public void testDistanceCalculator() {
        Workstation station1 = new Workstation();
        station1.setX(1);
        station1.setY(2);
        Workstation station2 = new Workstation();
        station2.setX(station1.getX() + 3);
        station2.setY(station1.getY() - 4);
        assertEquals(5, station1.distanceFrom(station2));
    }

    @Test
    public void testAvailability() {
        Workstation isolated = new Workstation();
        isolated.setId("isolated");
        isolated.setX(-10);
        isolated.setY(-10);
        stations.add(isolated);

        Workstation twoFreeNeighbours1 = new Workstation();
        twoFreeNeighbours1.setId("twoFreeNeighbours1");
        twoFreeNeighbours1.setY(-5);
        twoFreeNeighbours1.setX(0);
        Workstation twoFreeNeighbours2 = new Workstation();
        twoFreeNeighbours2.setId("twoFreeNeighbours2");
        twoFreeNeighbours2.setY(-5);
        twoFreeNeighbours2.setX(1);
        stations.add(twoFreeNeighbours1);
        stations.add(twoFreeNeighbours2);

        Workstation inRow1 = new Workstation();
        inRow1.setId("inRow1");
        inRow1.setY(0);
        inRow1.setX(0);
        inRow1.setOccupier(new Worker());
        Workstation inRow2 = new Workstation();
        inRow2.setId("inRow2");
        inRow2.setY(0);
        inRow2.setX(1);
        Workstation inRow3 = new Workstation();
        inRow3.setId("inRow3");
        inRow3.setY(0);
        inRow3.setX(2);
        stations.add(inRow1);
        stations.add(inRow2);
        stations.add(inRow3);

        Workstation inSecondRow1 = new Workstation();
        inSecondRow1.setId("inSecondRow1");
        inSecondRow1.setY(5);
        inSecondRow1.setX(0);
        Workstation inSecondRow2 = new Workstation();
        inSecondRow2.setId("inSecondRow2");
        inSecondRow2.setY(5);
        inSecondRow2.setX(1);
        inSecondRow2.setOccupier(new Worker());
        Workstation inSecondRow3 = new Workstation();
        inSecondRow3.setId("inSecondRow3");
        inSecondRow3.setY(5);
        inSecondRow3.setX(2);
        stations.add(inSecondRow1);
        stations.add(inSecondRow2);
        stations.add(inSecondRow3);

        List<Workstation> all = workStationService.findAll();
        List<Workstation> free = workStationService.findAllFree();
        List<Workstation> inUse = workStationService.findAllInUse();
        List<Workstation> occupiable = workStationService.findAllOccupiable();
        System.out.println("all = " + all);
        System.out.println("free = " + free);
        System.out.println("inUse = " + inUse);
        System.out.println("occupiable = " + occupiable);

        assertThat(occupiable).contains(isolated);
        assertThat(occupiable).contains(twoFreeNeighbours1);
        assertThat(occupiable).contains(twoFreeNeighbours2);
        assertThat(occupiable).doesNotContain(inRow1);
        assertThat(occupiable).doesNotContain(inRow2);
        assertThat(occupiable).contains(inRow3);
        assertThat(occupiable).doesNotContain(inSecondRow1);
        assertThat(occupiable).doesNotContain(inSecondRow2);
        assertThat(occupiable).doesNotContain(inSecondRow3);
    }

    @Test
    public void zoneTest() {
        Workstation differentZoneRow1 = new Workstation();
        differentZoneRow1.setY(10);
        differentZoneRow1.setX(0);
        differentZoneRow1.setZone(2);
        Workstation differentZoneRow2 = new Workstation();
        differentZoneRow2.setY(10);
        differentZoneRow2.setX(1);
        differentZoneRow2.setZone(3);
        differentZoneRow2.setOccupier(new Worker());
        Workstation differentZoneRow3 = new Workstation();
        differentZoneRow3.setY(10);
        differentZoneRow3.setX(2);
        differentZoneRow3.setZone(3);
        stations.add(differentZoneRow1);
        stations.add(differentZoneRow2);
        stations.add(differentZoneRow3);

        List<Workstation> occupiable = workStationService.findAllOccupiable();
        System.out.println("occupiable = " + occupiable);

        assertThat(occupiable).contains(differentZoneRow1);
        assertThat(occupiable).doesNotContain(differentZoneRow2);
        assertThat(occupiable).doesNotContain(differentZoneRow3);
    }

    @Test
    public void disabledTest() {
        Workstation inRow1 = new Workstation();
        inRow1.setId("inRow1");
        inRow1.setY(0);
        inRow1.setX(0);
        inRow1.setOccupier(new Worker());
        Workstation inRow2 = new Workstation();
        inRow2.setId("inRow2");
        inRow2.setY(0);
        inRow2.setX(1);
        Workstation inRow3 = new Workstation();
        inRow3.setId("inRow3");
        inRow3.setY(0);
        inRow3.setX(2);
        inRow3.setEnabled(false);
        stations.add(inRow1);
        stations.add(inRow2);
        stations.add(inRow3);

        List<Workstation> occupiable = workStationService.findAllOccupiable();
        System.out.println("occupiable = " + occupiable);

        assertThat(occupiable).doesNotContain(inRow1);
        assertThat(occupiable).doesNotContain(inRow2);
        assertThat(occupiable).doesNotContain(inRow3);
    }

}
