package org.ajc2020.endtoend;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class AdminTests {

    public String baseUrl = "http://frontend.kibe:8080";

    public WebDriver webDriver;

    @Before
    public void setup() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();

        if (System.getProperty("browser", "").equals("chrome")) {
            desiredCapabilities = DesiredCapabilities.chrome();
        }

        String host = System.getProperty("seleniumHubHost", "localhost");

        webDriver = new RemoteWebDriver(new URL("http://" + host + ":4444/wd/hub"), desiredCapabilities);
        webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() {
        logout();
        webDriver.quit();
    }

    private void logout() {
        webDriver.get(baseUrl + "/logout");
    }

    private boolean loginWith(String username, String password) {
        webDriver.get(baseUrl + "/login");
        webDriver.findElement(By.id("username")).sendKeys(username);
        webDriver.findElement(By.id("password")).sendKeys(password);
        webDriver.findElement(By.tagName("button")).click();

        System.out.println(webDriver.getTitle());

        return webDriver.findElement(By.tagName("body")).getAttribute("class").equals("container");
    }

    private boolean loginWithSuperAdmin() {
        return loginWith("admin@kibe", "nimda");
    }

    private void sleep(long millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidCredentials() {
        Assert.assertFalse(loginWith("nouser@example.com", "password"));
    }

    @Test
    public void testAdminLogin() {
        Assert.assertTrue(loginWithSuperAdmin());
        Assert.assertNotNull(webDriver.findElement(By.xpath("//*[contains(text(), 'admin!')]")));
    }

    @Test
    public void testAdminCreation() {
        Assert.assertTrue(loginWithSuperAdmin());
        webDriver.navigate().to(baseUrl + "/admins");
        webDriver.findElement(By.cssSelector("[data-target='create-admin-modal']")).click();
        webDriver.findElement(By.id("create.admin.name")).sendKeys("Mike Test");
        webDriver.findElement(By.id("create.admin.email")).sendKeys("mike.test@kibe");
        webDriver.findElement(By.id("create.admin.password")).sendKeys("secret");
        webDriver.findElement(By.id("create.admin.password.confirm")).sendKeys("secret");
        webDriver.findElement(By.cssSelector("form[action='/createAdmin']")).submit();


        logout();

        // TODO: update needs more time. We don't know why.
        sleep(1000);

        Assert.assertTrue(loginWith("mike.test@kibe", "secret"));

        logout();

        Assert.assertTrue(loginWithSuperAdmin());
        webDriver.navigate().to(baseUrl + "/admins");
        WebElement adminCell = webDriver.findElement(By.xpath("//*[contains(text(), 'mike.test@kibe')]"));
        System.out.println(adminCell.getAttribute("onclick"));
        String uuid = adminCell.getAttribute("onclick").split("\"")[1];
        webDriver.findElement(By.xpath("//a[contains(@href, '/deleteAdmin/" + uuid + "')]")).click();

        logout();
        Assert.assertFalse(loginWith("mike.test@kibe", "secret"));
    }
}
