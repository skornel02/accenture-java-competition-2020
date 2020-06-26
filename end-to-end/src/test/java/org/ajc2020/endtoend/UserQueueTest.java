package org.ajc2020.endtoend;

import lombok.extern.slf4j.Slf4j;
import org.ajc2020.utility.communication.WorkerCreationRequest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

@Slf4j
public class UserQueueTest extends SeleniumTestBase {
    private final WorkerCreationRequest[] workerCreationRequests = {
            WorkerCreationRequest.builder().email("adam@kibe").name("Adam").password("adam").rfId("000001").build(),
            WorkerCreationRequest.builder().email("baal@kibe").name("Baal").password("baal").rfId("000002").build(),
            WorkerCreationRequest.builder().email("cara@kibe").name("Cara").password("cara").rfId("000003").build(),
            WorkerCreationRequest.builder().email("dale@kibe").name("Dale").password("dale").rfId("000004").build(),
            WorkerCreationRequest.builder().email("earl@kibe").name("Earl").password("earl").rfId("000005").build(),
            WorkerCreationRequest.builder().email("faye@kibe").name("Faye").password("faye").rfId("000006").build(),
            WorkerCreationRequest.builder().email("gabe@kibe").name("Gabe").password("gabe").rfId("000007").build(),
            WorkerCreationRequest.builder().email("hank@kibe").name("Hank").password("hank").rfId("000008").build(),
            WorkerCreationRequest.builder().email("igor@kibe").name("Igor").password("igor").rfId("000009").build(),
            WorkerCreationRequest.builder().email("jack@kibe").name("Jack").password("jack").rfId("000010").build(),
            WorkerCreationRequest.builder().email("kyle@kibe").name("Kyle").password("kyle").rfId("000011").build(),
            WorkerCreationRequest.builder().email("lara@kibe").name("Lara").password("lara").rfId("000012").build(),
    };
    @Override
    protected void onTeardown() {
        loginWithSuperAdmin();
        webDriver.findElement(By.id("menu-users")).click();
        for (WorkerCreationRequest worker: workerCreationRequests) {
            deleteWorker(worker);
        }
    }

    private void deleteWorker(WorkerCreationRequest worker) {
        try {
            getRowElement(worker.getEmail(), "checkout").click();
        } catch (Exception ignored) {

        }
        try {
            getRowElement(worker.getEmail(), "delete").click();
        } catch (Exception ignored) {

        }
    }

    private void createWorker(WorkerCreationRequest worker) {
        createWorker(worker.getName(), worker.getEmail(), worker.getPassword(), worker.getRfId());
    }

    private void createWorker(String name, String email, String password, String rfId) {
        webDriver.findElement(By.id("menu-users")).click();
        webDriver.findElement(By.cssSelector("[href='#create-user-modal']")).click();
        webDriver.findElement(By.id("create.user.name")).sendKeys(name);
        webDriver.findElement(By.id("create.user.email")).sendKeys(email);
        webDriver.findElement(By.id("create.user.password")).sendKeys(password);
        webDriver.findElement(By.id("create.user.password.confirm")).sendKeys(password);
        webDriver.findElement(By.id("create.user.rfid")).sendKeys(rfId);
        webDriver.findElement(By.cssSelector("form[action='/createUser'")).submit();
    }

    @Test
    public void testWorkerCreation() {
        Assert.assertTrue(loginWithSuperAdmin());
        WorkerCreationRequest adam = workerCreationRequests[0];
        createWorker(adam);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(), '" + adam.getEmail() + "')]")));

        webDriver.navigate().to(baseUrl + "/users");

        Assert.assertEquals(adam.getEmail(), getRowElement(adam.getEmail(), "email").getText());
        Assert.assertEquals(adam.getName(), getRowElement(adam.getEmail(), "name").getText());
        Assert.assertTrue(getRowElement(adam.getEmail(), "password").isDisplayed());
        Assert.assertEquals(adam.getRfId(), getRowElement(adam.getEmail(), "rfid").getText());

        WorkerCreationRequest baal = workerCreationRequests[1];
        createWorker(baal);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(), '" + baal.getEmail() + "')]")));

        webDriver.navigate().to(baseUrl + "/users");

        Assert.assertEquals(adam.getEmail(), getRowElement(adam.getEmail(), "email").getText());
        Assert.assertEquals(adam.getName(), getRowElement(adam.getEmail(), "name").getText());
        Assert.assertTrue(getRowElement(adam.getEmail(), "password").isDisplayed());
        Assert.assertEquals(adam.getRfId(), getRowElement(adam.getEmail(), "rfid").getText());

        Assert.assertEquals(baal.getEmail(), getRowElement(baal.getEmail(), "email").getText());
        Assert.assertEquals(baal.getName(), getRowElement(baal.getEmail(), "name").getText());
        Assert.assertTrue(getRowElement(adam.getEmail(), "password").isDisplayed());
        Assert.assertEquals(baal.getRfId(), getRowElement(baal.getEmail(), "rfid").getText());
    }

    private void addAllWorkers() {
        for (WorkerCreationRequest worker : workerCreationRequests) {
            createWorker(worker);
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(), '" + worker.getEmail() + "')]")));
        }
    }

    private void checkIn(String email) {
        webDriver.navigate().to(baseUrl + "/users");
        getRowElement(email, "checkin").click();
    }
    private void checkOut(String email) {
        webDriver.navigate().to(baseUrl + "/users");
        getRowElement(email, "checkout").click();
    }

    private void assertUserCheckedIn(String email) {
        Assert.assertEquals(0, countElements(email, "checkin"));
        Assert.assertEquals(1, countElements(email, "checkout"));
    }
    private void assertUserCheckedOut(String email) {
        Assert.assertEquals(1, countElements(email, "checkin"));
        Assert.assertEquals(0, countElements(email, "checkout"));
    }

    @Test
    public void testAdmissionOnFullHouse() {
        Assert.assertTrue(loginWithSuperAdmin());
        addAllWorkers();

        // Admit only 10 people
        setBuildingParameters(200, 5);

        webDriver.navigate().to(baseUrl + "/users");

        for (int i = 0; i < 10; i++) {
            assertUserCheckedOut(workerCreationRequests[i].getEmail());
            checkIn(workerCreationRequests[i].getEmail());
            assertUserCheckedIn(workerCreationRequests[i].getEmail());
        }

        assertUserCheckedOut(workerCreationRequests[10].getEmail());
        checkIn(workerCreationRequests[10].getEmail());
        assertUserCheckedOut(workerCreationRequests[10].getEmail());

        assertUserCheckedIn(workerCreationRequests[6].getEmail());
        checkOut(workerCreationRequests[6].getEmail());
        assertUserCheckedOut(workerCreationRequests[6].getEmail());

        assertUserCheckedOut(workerCreationRequests[10].getEmail());
        checkIn(workerCreationRequests[10].getEmail());
        assertUserCheckedIn(workerCreationRequests[10].getEmail());
    }

    private void navigateToUserPage(WorkerCreationRequest worker) {
        webDriver.navigate().to(baseUrl + "/users");
        getRowElement(worker.getEmail(), "reservations").click();
    }

    private void reserveForToday() {
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".is-today")));
        webDriver.findElement(By.cssSelector(".is-today")).click();
        webDriver.findElement(By.id("doneButton")).click();
    }

    private boolean isReservedForToday() {
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".is-today")));
        return webDriver.findElement(By.cssSelector(".is-today")).getAttribute("class").contains("has-event");
    }

    private String getCounter(WorkerCreationRequest worker) {
        navigateToUserPage(worker);
        webDriverWait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(By.id("counter"), "class", "waiting")));
        return webDriver.findElement(By.id("counter")).getText();
    }


    @Test
    public void testWaitQueueOnFullHouse() {
        Assert.assertTrue(loginWithSuperAdmin());
        addAllWorkers();

        navigateToUserPage(workerCreationRequests[0]);

        String ticketEnter = (String)javascriptExecutor.executeScript("return window.eval('ticketEnter')");

        // Admit only 2 people
        setBuildingParameters(200, 1);

        for (WorkerCreationRequest worker : workerCreationRequests) {
            navigateToUserPage(worker);
            Assert.assertFalse(isReservedForToday());
            reserveForToday();
            Assert.assertTrue(isReservedForToday());
        }

        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[0]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[1]));
        Assert.assertEquals("", getCounter(workerCreationRequests[2]));
        Assert.assertEquals("", getCounter(workerCreationRequests[3]));
    }

    @Test
    public void testWaitQueueOnFullHouseWithAdmission() {
        Assert.assertTrue(loginWithSuperAdmin());
        addAllWorkers();

        navigateToUserPage(workerCreationRequests[0]);

        String ticketEnter = (String)javascriptExecutor.executeScript("return window.eval('ticketEnter')");

        // Admit only 5 people
        setBuildingParameters(200, 5);

        navigateToUserPage(workerCreationRequests[0]);
        reserveForToday();
        checkIn(workerCreationRequests[0].getEmail());

        for (int i = 1; i < 8; i++) {
            navigateToUserPage(workerCreationRequests[i]);
            Assert.assertFalse(isReservedForToday());
            reserveForToday();
            Assert.assertTrue(isReservedForToday());
        }

        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[1]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[2]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[3]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[4]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[5]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[6]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[7]));

        checkIn(workerCreationRequests[1].getEmail());
        checkIn(workerCreationRequests[3].getEmail());
        checkIn(workerCreationRequests[4].getEmail());
        checkIn(workerCreationRequests[5].getEmail());

        Assert.assertEquals("", getCounter(workerCreationRequests[0]));
        Assert.assertEquals("", getCounter(workerCreationRequests[1]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[2]));
        Assert.assertEquals("", getCounter(workerCreationRequests[3]));
        Assert.assertEquals("", getCounter(workerCreationRequests[4]));
        Assert.assertEquals("", getCounter(workerCreationRequests[5]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[6]));
        Assert.assertEquals(ticketEnter, getCounter(workerCreationRequests[7]));


    }
}
