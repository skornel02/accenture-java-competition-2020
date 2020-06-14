package org.ajc2020.spring1;

import org.ajc2020.spring1.model.Worker;
import org.ajc2020.utilty.resource.WorkerStatus;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TestOfficeHours {

    @Test
    public void userLoginChangesAverageTime() throws Exception {

        Worker joseph = new Worker();

        joseph.checkin(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 15:00:00"));
        joseph.checkout(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 15:15:00"));

        Assert.assertEquals(15 * 60 * 1000 * 1000, joseph.getAverageTime(), 0.1);

        joseph.checkin(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 15:00:00"));
        joseph.checkout(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 15:15:00"));

        Assert.assertEquals(15 * 60 * 1000 * 1000, joseph.getAverageTime(), 0.1);

        joseph.checkin(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 15:00:00"));
        joseph.checkout(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 15:30:00"));


        Assert.assertEquals(20 * 60 * 1000 * 1000, joseph.getAverageTime(), 0.1);
    }

    private static class WorkerStub extends Worker {
        @Override
        public Date today() {
            try {
                return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 00:00:00");
            } catch (ParseException e) {
                return new Date();
            }
        }
    }

    @Test
    public void userStateChangesAccordingly() throws Exception {
        Worker joseph = new WorkerStub();

        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

        Assert.assertTrue(joseph.checkin(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 06:15:00")));
        Assert.assertEquals(WorkerStatus.InOffice, joseph.getStatus());

        Assert.assertTrue(joseph.checkout(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 06:25:00")));
        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

        Assert.assertFalse(joseph.checkout(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 06:30:00")));
        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

        Assert.assertTrue(joseph.register(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 00:00:00")));
        Assert.assertEquals(WorkerStatus.OnList, joseph.getStatus());

        Assert.assertFalse(joseph.register(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 00:00:00")));
        Assert.assertEquals(WorkerStatus.OnList, joseph.getStatus());

        Assert.assertTrue(joseph.checkin(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 06:35:00")));
        Assert.assertEquals(WorkerStatus.InOffice, joseph.getStatus());

        Assert.assertTrue(joseph.checkout(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 06:45:00")));
        Assert.assertEquals(WorkerStatus.WorkingFromHome, joseph.getStatus());

    }

    @Test
    public void testTicketStates() throws ParseException {
        Worker joseph = new WorkerStub();
        Date today = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/01/2020 00:00:00");
        Date tomorrow = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 00:00:00");
        Date yesterday = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("31/12/2019 00:00:00");
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

    @Test
    public void timeTruncation() throws ParseException {
        Assert.assertEquals(
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 00:00:00"),
                Worker.truncateDay(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 15:30:00")));

        Assert.assertEquals(
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 00:00:00"),
                Worker.truncateDay(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("02/01/2020 00:00:00")));

        Assert.assertEquals(
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("08/08/2020 00:00:00"),
                Worker.truncateDay(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("08/08/2020 23:59:00")));
    }
}
