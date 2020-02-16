package main.java.identity;

import main.java.NirmataMailer;
import main.java.NirmataSetUp;
import main.java.NirmataTools;
import main.java.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Date;

@Test (groups = { "AddUser" }, dependsOnGroups = {"SignIn"})
public class AddUser {
    private WebDriver webDriver;
    private Date testStartTime;
    private String adduser_email;

    @BeforeGroups (groups = {"AddUser"})
    @Parameters("adduser_email")
    public void SetupDriver(String adduser_email) {
        this.webDriver = NirmataSetUp.webDriver;
        testStartTime = new Date();
        this.adduser_email = adduser_email;
    }

    @Test(testName = "Open 'Users' web-page")
    public void openUsersPage() {
        NirmataTools.waitUntilVisible(webDriver,60,"//*[@id='iam_menu']").click();
        NirmataTools.waitUntilTitleContains(webDriver, 60, "Nirmata | IAM | Users");
    }

    @Test(dependsOnMethods = { "openUsersPage" }, testName = "Verify if User already exists")
    public void checkUserExists() {
        NirmataTools.waitUntilVisible(webDriver,60,"//table[*]");
        By newUser_email = By.xpath("//table[*]//td[contains(.,'" + TestData.getUser("email", adduser_email) + "')]");
        Assert.assertTrue(webDriver.findElements(newUser_email).size() < 1, "User cannot be created, user exists.");
    }

    @Test(dependsOnMethods = { "checkUserExists" }, testName = "Click 'Add User' button")
    public void ClickAddUserBtn() {
        NirmataTools.waitUntilVisible(webDriver,60,"//button[contains(., 'Add User')]").click();
        NirmataTools.waitUntilInvisible(webDriver,60,"//div[@class='modal-content']//*[contains(text(),'Add a User')]");
    }

    @Test(dependsOnMethods = { "ClickAddUserBtn" }, testName = "Add new user data")
    public void AddNewUserData() {

        NirmataTools.waitUntilVisible(webDriver,60,"//div[contains(@class,'field-role')]//*[@role='combobox']").click();
        NirmataTools.waitUntilVisible(webDriver,60,"//li[contains(text(),'" + TestData.getUser("user_role", adduser_email) + "')]").click();
        NirmataTools.waitUntilInvisible(webDriver, 60, "//*[contains(text(), 'No results found')]");

        NirmataTools.waitUntilVisible(webDriver,60,"//*[@id='name']").sendKeys(TestData.getUser("user_name", adduser_email));

        NirmataTools.waitUntilVisible(webDriver,60,"//*[@id='email']").sendKeys(TestData.getUser("email", adduser_email));

        NirmataTools.waitUntilVisible(webDriver,60,"//div[contains(@class,'field-identityProvider')]//*[@role='combobox']").click();
        NirmataTools.waitUntilVisible(webDriver,60,"//li[contains(text(),'" + TestData.getUser("user_id_provider", adduser_email) + "')]").click();
        NirmataTools.waitUntilInvisible(webDriver, 60, "//*[contains(text(), 'No results found')]");

        NirmataTools.waitUntilVisible(webDriver,60,"//div[contains(@class,'field-teams')]//*[@role='combobox']").click();
        try { webDriver.findElement(By.xpath ("//li[contains(text(),'" + TestData.getUser("user_teams", adduser_email) + "')]")).click(); }
        catch (Exception ignored) { webDriver.findElement(By.xpath ("//div[contains(@class,'field-teams')]//*[@role='combobox']")).click(); }

        NirmataTools.waitUntilVisible(webDriver,60,"//button[contains(.,'Finish')]").click();

        NirmataTools.waitUntilInvisible(webDriver,60,"//div[@class='modal-content']//*[contains(text(),'Add a User')]");
    }

    @Test(dependsOnMethods = { "AddNewUserData" }, testName = "Verify if new User added")
    public void verifyUserAdded() {
        NirmataTools.waitUntilTitleContains(webDriver, 60, "Nirmata | IAM | Users");
        NirmataTools.waitUntilVisible(webDriver,60,"//table[*]");
        By newUser_email = By.xpath("//table[*]//td[contains(.,'" + TestData.getUser("email", adduser_email) + "')]");
        Assert.assertTrue(webDriver.findElements(newUser_email).size() > 0, "User creation failed. User not found.");
    }

    @Test(dependsOnMethods = { "verifyUserAdded" }, testName = "Receive activation link")
    public void getActivationLink() {

        NirmataMailer nm = new NirmataMailer(TestData.getUser("email_host", adduser_email),
                TestData.getUser("email_protocol", adduser_email),
                TestData.getUser("email_folder", adduser_email),
                TestData.getUser("email", adduser_email),
                TestData.getUser("email_password", adduser_email));
        String code = nm.getActivationLink(testStartTime, 300000);

        webDriver.get("https://nirmata.io/webclient/#logout");
        webDriver.get(code);
        NirmataTools.waitUntilVisible(webDriver,60,"//div[@class='login-title' and contains(text(),'Set your password')]");
    }

    @Test(dependsOnMethods = { "getActivationLink" }, testName = "Set up new User password")
    public void setupPassword() {
        NirmataTools.waitUntilVisible(webDriver,60,"//input[@id='password']");

        webDriver.findElement(By.xpath("//input[@id='password']")).sendKeys(TestData.getUser("user_password", adduser_email));
        webDriver.findElement(By.xpath("//input[@id='confirmPassword']")).sendKeys(TestData.getUser("user_password", adduser_email));
        webDriver.findElement(By.xpath("//button[@type='submit']")).click();

        NirmataTools.waitUntilVisible(webDriver,60,"//div[@class='login-body' and contains(.,'Your password has been reset.')]");
    }

//    @AfterGroups (groups = {"AddUser"}, alwaysRun = true)
//    public void signOut() {
//        webDriver.get("https://nirmata.io/webclient/#logout");
//    }

}
