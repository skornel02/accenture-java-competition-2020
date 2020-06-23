package org.ajc2020.endtoend;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class FloorPlanTests extends SeleniumTestBase {

    private void selectPlan() {
        webDriver.findElement(By.id("menu-plan")).click();
    }

    private List<String> getWorkstationIds() {
        return webDriver
                .findElements(By.className("workstation"))
                .stream()
                .map(x -> x.getAttribute("id"))
                .collect(Collectors.toList());
    }

    private WebElement getById(String id) {
        return webDriver.findElement(By.id(id));
    }

    @Test
    public void defaultFloorPlan() {
        Assert.assertTrue(loginWithSuperAdmin());
        selectPlan();

        Assert.assertTrue(getWorkstationIds().size() > 0);

        getWorkstationIds()
                .stream()
                .map(x -> x + "rect")
                .map(this::getById).forEach(
                element -> Assert.assertTrue(element.getAttribute("class").contains("fill-green"))
        );
    }

    @Test
    public void testPermitForbid() {
        Assert.assertTrue(loginWithSuperAdmin());
        selectPlan();
        Assert.assertTrue(getWorkstationIds().size() > 0);
        String firstPlaceId = getWorkstationIds().get(0);
        WebElement seatingItem = getById(firstPlaceId + "rect");
        seatingItem.click();
        sleep(1000);
        Assert.assertTrue(seatingItem.getAttribute("class").contains("fill-green"));
        Assert.assertEquals(firstPlaceId, getById("sitting-id").getText());

        getById("sitting-action-forbid").click();
        sleep(1000);

        Assert.assertFalse(seatingItem.getAttribute("class").contains("fill-green"));
        Assert.assertTrue(seatingItem.getAttribute("class").contains("fill-red"));

        getById("sitting-action-permit").click();
        sleep(1000);

        Assert.assertTrue(seatingItem.getAttribute("class").contains("fill-green"));
        Assert.assertFalse(seatingItem.getAttribute("class").contains("fill-red"));

    }
}
