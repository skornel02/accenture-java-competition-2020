package org.ajc2020.spring1;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.utility.resource.WorkerStatus;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.time.ZoneOffset.UTC;


public class TestOfficeHours {

    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @Test
    public void userLoginChangesAverageTime() {

        Worker joseph = new Worker();
        joseph.checkin(LocalDateTime.parse("01/01/2020 15:00:00", DATETIME).atOffset(UTC));
        joseph.checkout(LocalDateTime.parse("01/01/2020 15:15:00", DATETIME).atOffset(UTC));

        Assert.assertEquals(15 * 60 * 1000 * 1000, joseph.getAverageTime(), 0.1);

        joseph.checkin(LocalDateTime.parse("02/01/2020 15:00:00", DATETIME).atOffset(UTC));
        joseph.checkout(LocalDateTime.parse("02/01/2020 15:15:00", DATETIME).atOffset(UTC));

        Assert.assertEquals(15 * 60 * 1000 * 1000, joseph.getAverageTime(), 0.1);

        joseph.checkin(LocalDateTime.parse("02/01/2020 15:00:00", DATETIME).atOffset(UTC));
        joseph.checkout(LocalDateTime.parse("02/01/2020 15:30:00", DATETIME).atOffset(UTC));


        Assert.assertEquals(20 * 60 * 1000 * 1000, joseph.getAverageTime(), 0.1);
    }

    private static class WorkerStub extends Worker {
        @Override
        public LocalDate today() {
            try {
                return LocalDate.parse("01/01/2020", DATE);
            } catch (DateTimeParseException ignored) {
                return LocalDate.now();
            }
        }
    }

    @Test
    public void userStateChangesAccordingly() {
        Worker joseph = new WorkerStub();

        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

        Assert.assertTrue(joseph.checkin(LocalDateTime.parse("01/01/2020 06:15:00", DATETIME).atOffset(UTC)));
        Assert.assertEquals(WorkerStatus.InOffice, joseph.getStatus());

        Assert.assertTrue(joseph.checkout(LocalDateTime.parse("01/01/2020 06:25:00", DATETIME).atOffset(UTC)));
        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

        Assert.assertFalse(joseph.checkout(LocalDateTime.parse("01/01/2020 06:30:00", DATETIME).atOffset(UTC)));
        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

        Assert.assertTrue(joseph.register(LocalDate.parse("01/01/2020", DATE)));
        Assert.assertEquals(WorkerStatus.OnList, joseph.getStatus());

        Assert.assertFalse(joseph.register(LocalDate.parse("01/01/2020", DATE)));
        Assert.assertEquals(WorkerStatus.OnList, joseph.getStatus());

        Assert.assertTrue(joseph.checkin(LocalDateTime.parse("01/01/2020 06:35:00", DATETIME).atOffset(UTC)));
        Assert.assertEquals(WorkerStatus.InOffice, joseph.getStatus());

        Assert.assertTrue(joseph.checkout(LocalDateTime.parse("01/01/2020 06:45:00", DATETIME).atOffset(UTC)));
        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

    }

    @Test
    public void testTicketStates() {
        Worker joseph = new WorkerStub();
        LocalDate today = LocalDate.parse("01/01/2020", DATE);
        LocalDate tomorrow = LocalDate.parse("02/01/2020", DATE);
        LocalDate yesterday = LocalDate.parse("31/12/2019", DATE);
        Assert.assertEquals(0, joseph.getTickets().size());
        Assert.assertFalse(joseph.hasTicketForToday());

        Assert.assertFalse(joseph.register(yesterday));
        Assert.assertEquals(0, joseph.getTickets().size());
        Assert.assertFalse(joseph.hasTicketForToday());

        Assert.assertTrue(joseph.register(tomorrow));
        Assert.assertEquals(1, joseph.getTickets().size());
        Assert.assertFalse(joseph.hasTicketForToday());


        Assert.assertTrue(joseph.register(today));
        Assert.assertEquals(2, joseph.getTickets().size());
        Assert.assertTrue(joseph.hasTicketForToday());

        Assert.assertTrue(joseph.cancel(today));
        Assert.assertEquals(1, joseph.getTickets().size());
        Assert.assertFalse(joseph.hasTicketForToday());

        Assert.assertFalse(joseph.cancel(today));
        Assert.assertEquals(1, joseph.getTickets().size());
        Assert.assertFalse(joseph.hasTicketForToday());

        Assert.assertTrue(joseph.cancel(tomorrow));
        Assert.assertEquals(0, joseph.getTickets().size());
        Assert.assertFalse(joseph.hasTicketForToday());

    }

}
