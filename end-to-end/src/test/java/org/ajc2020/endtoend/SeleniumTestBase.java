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

import java.net.MalformedURLException;
import java.net.URL;

public class SeleniumTestBase {
    public String baseUrl = "";

    public WebDriver webDriver;

    protected String getenv(String key, String defaultValue) {
        if (System.getenv().containsKey(key))
            return System.getenv(key);
        return defaultValue;
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
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();

        if (System.getProperty("browser", "").equals("chrome")) {
            desiredCapabilities = DesiredCapabilities.chrome();
        }

        String seleniumHubHost = getenv("seleniumHubHost", "localhost");
        baseUrl = getenv("seleniumSUTHost", "http://frontend.kibe:8080");

        String driverSelector = getenv("seleniumDriverType", "RemoteWebDriver");
        System.setProperty("webdriver.gecko.driver", getenv("webdriver.gecko.driver", ""));
        System.setProperty("webdriver.chrome.driver", getenv("webdriver.chrome.driver", ""));

        switch (driverSelector) {
            case "RemoteWebDriver":
                webDriver = new RemoteWebDriver(new URL("http://" + seleniumHubHost + ":4444/wd/hub"), desiredCapabilities);
                break;
            case "LocalFirefoxDriver":
                webDriver = new FirefoxDriver(new FirefoxOptions());
                break;
            case "LocalChromeDriver":
                webDriver = new ChromeDriver(new ChromeOptions());
                break;
        }

        if (webDriver == null) Assert.fail();

    }

    @After
    public void tearDown() {
        logout();
        webDriver.quit();
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
