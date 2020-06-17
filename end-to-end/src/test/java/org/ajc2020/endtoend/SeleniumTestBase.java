package org.ajc2020.endtoend;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class SeleniumTestBase {
    private final String propertiesPath = System.getProperty("selenium.test.kibe");
    final Properties properties;

    public String baseUrl = "";

    public WebDriver webDriver;

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


    }

    @After
    public void tearDown() {
        try {
            logout();
        } finally {
            webDriver.quit();
        }
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

    protected boolean loginWithSuperAdmin() {
        return loginWith("admin@kibe", "nimda");
    }

}
