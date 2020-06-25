package org.ajc2020.endtoend;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
                element -> Assert.assertTrue(element.getAttribute("class").contains("ws-occupiable"))
        );
    }

    @Test
    public void testPermitForbid() {
        Assert.assertTrue(loginWithSuperAdmin());
        selectPlan();
        Assert.assertTrue(getWorkstationIds().size() > 0);
        String firstPlaceId = getWorkstationIds().get(0);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id(firstPlaceId + "rect")));
        WebElement seatingItem = getById(firstPlaceId + "rect");
        seatingItem.click();
        webDriverWait.until(ExpectedConditions.attributeContains(seatingItem, "class", "ws-occupiable"));
        Assert.assertTrue(seatingItem.getAttribute("class").contains("ws-occupiable"));
        Assert.assertEquals(firstPlaceId, getById("sitting-id").getText());

        getById("sitting-action-forbid").click();

        webDriverWait.until(ExpectedConditions.attributeContains(seatingItem, "class", "ws-disabled"));
        Assert.assertFalse(seatingItem.getAttribute("class").contains("ws-occupiable"));
        Assert.assertTrue(seatingItem.getAttribute("class").contains("ws-disabled"));

        getById("sitting-action-permit").click();

        webDriverWait.until(ExpectedConditions.attributeContains(seatingItem, "class", "ws-occupiable"));
        Assert.assertTrue(seatingItem.getAttribute("class").contains("ws-occupiable"));
        Assert.assertFalse(seatingItem.getAttribute("class").contains("ws-disabled"));

    }
}
