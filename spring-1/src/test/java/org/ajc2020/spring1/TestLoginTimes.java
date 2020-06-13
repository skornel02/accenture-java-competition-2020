package org.ajc2020.spring1;

import org.ajc2020.utilty.resource.Status;
import org.ajc2020.spring1.model.Worker;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class TestLoginTimes {

    @Test
    public void userLoginChangesAverageTime() throws Exception {

        Worker joseph = new Worker();
        joseph.setStatus(Status.WorkingFromHome);

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
