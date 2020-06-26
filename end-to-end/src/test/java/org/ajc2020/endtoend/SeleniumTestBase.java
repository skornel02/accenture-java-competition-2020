package org.ajc2020.endtoend;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@Slf4j
public class SeleniumTestBase {
    @Rule public TestName testName = new TestName();

    private final String propertiesPath = System.getProperty("selenium.test.kibe");
    final Properties properties;

    public String baseUrl = "";

    public WebDriver webDriver;
    protected WebDriverWait webDriverWait;
    protected JavascriptExecutor javascriptExecutor;

    protected void onTeardown() {}

    public SeleniumTestBase() {
        properties = new Properties();
        try {
            InputStream resourceInputStream;
            if (propertiesPath == null) {
                resourceInputStream = getClass().getClassLoader().getResourceAsStream("selenium.test.kibe.local.properties");
            } else {
                resourceInputStream = new FileInputStream(propertiesPath);
            }
            properties.load(resourceInputStream);
            assert resourceInputStream != null;
            resourceInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sleep(long millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setup() throws MalformedURLException {

        baseUrl = properties.getProperty("selenium.sut.host");
        String driverSelector = properties.getProperty("selenium.driver.type");


        switch (driverSelector) {
            case "RemoteWebDriver":
                DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();

                if (properties.getProperty("selenium.hub.driver").equals("chrome")) {
                    desiredCapabilities = DesiredCapabilities.chrome();
                }

                String seleniumHubHost = properties.getProperty("selenium.hub.host");

                webDriver = new RemoteWebDriver(new URL("http://" + seleniumHubHost + ":4444/wd/hub"), desiredCapabilities);
                break;
            case "LocalFirefoxDriver":
                System.setProperty("webdriver.gecko.driver", properties.getProperty("webdriver.gecko.driver"));
                webDriver = new FirefoxDriver(new FirefoxOptions());
                break;
            case "LocalChromeDriver":
                System.setProperty("webdriver.chrome.driver", properties.getProperty("webdriver.chrome.driver"));
                webDriver = new ChromeDriver(new ChromeOptions());
                break;
            default:
                Assert.fail();
                break;
        }
        webDriver.get(baseUrl);
        webDriverWait = new WebDriverWait(webDriver, 30);
        if (webDriver.findElements(By.id("username")).size() == 0) {
            log.info("{} - Waiting for login prompt", testName.getMethodName());
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            log.info("{} - Login ready, resume tests", testName.getMethodName());
        }
         javascriptExecutor = (JavascriptExecutor) webDriver;
    }

    @After
    public void tearDown() {
        try {
            onTeardown();
            logout();
        } finally {
            webDriver.quit();
        }
    }

    protected WebElement getRowElement(String email, String role) {
        return webDriver.findElement(By.xpath("//a[contains(text(), '" + email + "')]/../..//*[@data-role='" + role + "']"));
    }

    protected long countElements(String email, String role) {
        return webDriver.findElements(By.xpath("//a[contains(text(), '" + email + "')]/../..//*[@data-role='" + role + "']")).size();
    }


    protected void logout() {
        webDriver.get(baseUrl + "/logout");
    }

    protected boolean loginWith(String username, String password) {
        webDriver.get(baseUrl + "/login");
        webDriver.findElement(By.id("username")).sendKeys(username);
        webDriver.findElement(By.id("password")).sendKeys(password);
        webDriver.findElement(By.tagName("button")).click();

        return webDriver.findElement(By.tagName("body")).getAttribute("class").equals("container");
    }

    protected void setBuildingParameters(int max, int percentage) {
        webDriver.get(baseUrl + "/building");
        webDriver.findElement(By.id("building.capacity")).clear();
        webDriver.findElement(By.id("building.capacity")).sendKeys(String.valueOf(max));
        webDriver.findElement(By.id("building.percentage")).clear();
        webDriver.findElement(By.id("building.percentage")).sendKeys(String.valueOf(percentage));
        webDriver.findElement(By.cssSelector("form[action='/building/capacity'")).submit();
    }

    protected boolean loginWithSuperAdmin() {
        for (int i = 0; i < 3; i++) {
            boolean success = loginWith("admin@kibe", "nimda");
            if (success) return true;
            log.info("{} - Super-admin login failed. Retrying...{}", testName.getMethodName(), i+1);
            sleep(500);
        }
        return false;
    }

}
