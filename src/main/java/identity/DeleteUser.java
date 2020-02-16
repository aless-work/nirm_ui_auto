package main.java.identity;

import main.java.NirmataSetUp;
import main.java.NirmataTools;
import main.java.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Date;

@Test(groups = { "DeleteUser" }, dependsOnGroups = {"SignIn"})
public class DeleteUser {
    private WebDriver webDriver;
    private Date testStartTime;
    private String deluser_email;

    @BeforeGroups(groups = {"DeleteUser"})
    @Parameters("deluser_email")
    public void SetupDriver(String deluser_email) {
        this.webDriver = NirmataSetUp.webDriver;
        testStartTime = new Date();
        this.deluser_email = deluser_email;
    }

    @Test(testName = "Open 'Users' web-page")
    public void openUsersPage() {
        NirmataTools.waitUntilVisible(webDriver,60,"//*[@id='iam_menu']").click();
        NirmataTools.waitUntilTitleContains(webDriver, 60, "Nirmata | IAM | Users");
    }

    @Test(dependsOnMethods = { "openUsersPage" }, testName = "Verify if User exists")
    public void checkUserExists() {
        NirmataTools.waitUntilVisible(webDriver,60,"//table[*]");
        By newUser_email = By.xpath("//table[*]//td[contains(.,'" + TestData.getUser("email", deluser_email) + "')]");
        Assert.assertTrue(webDriver.findElements(newUser_email).size() > 0, "User doesn't exist, cannot be deleted.");
    }

    @Test(dependsOnMethods = { "checkUserExists" }, testName = "Click 'Delete User' button")
    public void ClickDelUserBtn() {
        // locate 'delete' button for 'deluser_email' and click
        NirmataTools.waitUntilVisible(webDriver,60,
                "//table[*]//td[contains(.,'" + deluser_email + "')]/ancestor::tr[*]//button[@title='Delete User']").click();
        // verify if text in warning message contains correct user name
        Assert.assertTrue(NirmataTools.waitUntilVisible(webDriver,60,
                "//div[@class='dialog-title']").getText().contains(TestData.getUser("user_name", deluser_email)),
                "Deleted user name doesn't match to user email. Will not be deleted.");
        // confirm deletion
        NirmataTools.waitUntilVisible(webDriver,60,"//div[@class='modal-content']//button[contains(text(),'Delete')]").click();
        // wait until 'Delete' dialog disappears
        NirmataTools.waitUntilInvisible(webDriver,60,"//div[@class='modal-content']//button[contains(text(),'Delete')]");
    }

    @Test(dependsOnMethods = { "ClickDelUserBtn" }, testName = "Verify if User not exists anymore")
    public void checkUserExists2() {
        NirmataTools.waitUntilVisible(webDriver,60,"//table[*]");
        By newUser_email = By.xpath("//table[*]//td[contains(.,'" + TestData.getUser("email", deluser_email) + "')]");
        Assert.assertTrue(webDriver.findElements(newUser_email).size() < 1, "User still exists, it seems user hasn't been deleted.");
    }
}
