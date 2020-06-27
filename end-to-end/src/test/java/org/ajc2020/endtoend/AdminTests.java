package org.ajc2020.endtoend;

import org.ajc2020.utility.communication.AdminCreationRequest;
import org.junit.*;
import org.openqa.selenium.By;

public class AdminTests extends SeleniumTestBase {

    private void createAdmin(String name, String email, String password) {
        webDriver.navigate().to(baseUrl + "/admins");
        webDriver.findElement(By.cssSelector("[href='#create-admin-modal']")).click();
        webDriver.findElement(By.id("create.admin.name")).sendKeys(name);
        webDriver.findElement(By.id("create.admin.email")).sendKeys(email);
        webDriver.findElement(By.id("create.admin.password")).sendKeys(password);
        webDriver.findElement(By.id("create.admin.password.confirm")).sendKeys(password);
        webDriver.findElement(By.cssSelector("form[action='/createAdmin']")).submit();
    }

    private void createAdmin(AdminCreationRequest admin) {
        createAdmin(admin.getName(), admin.getEmail(), admin.getPassword());
    }

    private boolean loginWith(AdminCreationRequest admin) {
        return loginWith(admin.getEmail(), admin.getPassword());
    }

    private void deleteAdmin(AdminCreationRequest admin) {
        webDriver.navigate().to(baseUrl + "/admins");
        getRowElement(admin.getEmail(), "delete").click();
        webDriver.findElement(By.cssSelector("[onclick='requestRemoval()']")).click();
    }

    private final AdminCreationRequest mike = AdminCreationRequest.builder()
            .name("Mike Test")
            .email("mike.test@kibe")
            .password("secret").build();

    private void changePassword(AdminCreationRequest newCredentials) {
        webDriver.navigate().to(baseUrl + "/admins");
        getRowElement(newCredentials.getEmail(), "password").click();
        webDriver.findElement(By.id("edit.admin.password")).sendKeys(newCredentials.getPassword());
        webDriver.findElement(By.id("edit.admin.password.confirm")).sendKeys(newCredentials.getPassword());
        webDriver.findElement(By.cssSelector("form[action='/updateAdmin']")).submit();
    }

    private void changeEmail(AdminCreationRequest oldAccount, AdminCreationRequest newAccount) {
        webDriver.navigate().to(baseUrl + "/admins");
        getRowElement(oldAccount.getEmail(), "email").click();
        webDriver.findElement(By.id("edit.admin.email")).clear();
        webDriver.findElement(By.id("edit.admin.email")).sendKeys(newAccount.getEmail());
        webDriver.findElement(By.cssSelector("form[action='/updateAdmin']")).submit();
    }

    private void changeName(AdminCreationRequest newAccount) {
        webDriver.navigate().to(baseUrl + "/admins");
        getRowElement(newAccount.getEmail(), "name").click();
        webDriver.findElement(By.id("edit.admin.name")).clear();
        webDriver.findElement(By.id("edit.admin.name")).sendKeys(newAccount.getName());
        webDriver.findElement(By.cssSelector("form[action='/updateAdmin']")).submit();
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
    public void testAdminPasswordChange() {
        Assert.assertTrue(loginWithSuperAdmin());
        createAdmin(mike);

        AdminCreationRequest mikeNewPassword = AdminCreationRequest.builder()
                .email(mike.getEmail())
                .name(mike.getName())
                .password("terces")
                .build();

        sleep(1000);

        changePassword(mikeNewPassword);
        logout();


        sleep(1000);

        Assert.assertFalse(loginWith(mike));
        Assert.assertTrue(loginWith(mikeNewPassword));
        logout();

        Assert.assertTrue(loginWithSuperAdmin());
        changePassword(mike);
        logout();

        sleep(1000);

        Assert.assertFalse(loginWith(mikeNewPassword));
        Assert.assertTrue(loginWith(mike));
        logout();

        Assert.assertTrue(loginWithSuperAdmin());
        deleteAdmin(mike);

    }

    @Test
    public void testAdminEmailChange() {
        Assert.assertTrue(loginWithSuperAdmin());
        createAdmin(mike);

        AdminCreationRequest mikeNewEmail = AdminCreationRequest.builder()
                .email("mrmike@kibe")
                .name(mike.getName())
                .password(mike.getPassword())
                .build();

        sleep(1000);

        changeEmail(mike, mikeNewEmail);
        logout();


        sleep(1000);

        Assert.assertFalse(loginWith(mike));
        Assert.assertTrue(loginWith(mikeNewEmail));
        logout();

        Assert.assertTrue(loginWithSuperAdmin());
        changeEmail(mikeNewEmail, mike);
        logout();

        sleep(1000);

        Assert.assertFalse(loginWith(mikeNewEmail));
        Assert.assertTrue(loginWith(mike));
        logout();

        Assert.assertTrue(loginWithSuperAdmin());
        deleteAdmin(mike);

    }

    @Test
    public void testAdminNameChange() {
        Assert.assertTrue(loginWithSuperAdmin());
        createAdmin(mike);

        AdminCreationRequest mikeNewName = AdminCreationRequest.builder()
                .email(mike.getEmail())
                .name("Mike A Test")
                .password(mike.getPassword())
                .build();

        sleep(1000);

        webDriver.navigate().to(baseUrl + "/admins");
        Assert.assertEquals(1, webDriver.findElements(By.xpath("//a[contains(text(), '" + mike.getName() + "')]")).size());
        Assert.assertEquals(0, webDriver.findElements(By.xpath("//a[contains(text(), '" + mikeNewName.getName() + "')]")).size());


        changeName(mikeNewName);

        sleep(1000);

        webDriver.navigate().to(baseUrl + "/admins");
        Assert.assertEquals(0, webDriver.findElements(By.xpath("//a[contains(text(), '" + mike.getName() + "')]")).size());
        Assert.assertEquals(1, webDriver.findElements(By.xpath("//a[contains(text(), '" + mikeNewName.getName() + "')]")).size());

        changeName(mike);

        sleep(1000);

        webDriver.navigate().to(baseUrl + "/admins");
        Assert.assertEquals(1, webDriver.findElements(By.xpath("//a[contains(text(), '" + mike.getName() + "')]")).size());
        Assert.assertEquals(0, webDriver.findElements(By.xpath("//a[contains(text(), '" + mikeNewName.getName() + "')]")).size());

        deleteAdmin(mike);

    }

    @Test
    public void testAdminCreation() {
        Assert.assertTrue(loginWithSuperAdmin());

        webDriver.navigate().to(baseUrl + "/admins");
        Assert.assertEquals(0, webDriver.findElements(By.xpath("//a[contains(text(), '" + mike.getEmail() + "')]")).size());
        Assert.assertEquals(0, webDriver.findElements(By.xpath("//a[contains(text(), '" + mike.getName() + "')]")).size());

        createAdmin(mike);

        sleep(1000);

        webDriver.navigate().to(baseUrl + "/admins");
        Assert.assertEquals(1, webDriver.findElements(By.xpath("//a[contains(text(), '" + mike.getEmail() + "')]")).size());
        Assert.assertEquals(1, webDriver.findElements(By.xpath("//a[contains(text(), '" + mike.getName() + "')]")).size());

        logout();

        Assert.assertTrue(loginWith(mike));
        logout();

        Assert.assertTrue(loginWithSuperAdmin());
        deleteAdmin(mike);
        logout();

        Assert.assertFalse(loginWith("mike.test@kibe", "secret"));
    }

}
