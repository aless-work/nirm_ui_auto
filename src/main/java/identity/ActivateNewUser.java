package main.java.identity;

import main.java.NirmataMailer;
import main.java.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.util.Date;

public class ActivateNewUser {
    public WebDriver webDriver;
    public String user_email;
    public Date testStartTime;

    @Test(testName = "Receive activation link")
    public void getActivationLink() {

        NirmataMailer nm = new NirmataMailer(TestData.getUser("email_host", user_email),
                TestData.getUser("email_protocol", user_email),
                TestData.getUser("email_folder", user_email),
                TestData.getUser("email", user_email),
                TestData.getUser("email_password", user_email));
        String code = nm.getActivationLink(testStartTime, 300000);

        webDriver.get("https://nirmata.io/webclient/#logout");
        webDriver.get(code);
        WebDriverWait wait = new WebDriverWait(webDriver, 40);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='login-title' and contains(text(),'Set your password')]")));
    }

    @Test(dependsOnMethods = { "getActivationLink" }, testName = "Set up new User password")
    public void setupPassword() {

        WebDriverWait wait = new WebDriverWait(webDriver, 40);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='password']")));
        webDriver.findElement(By.xpath("//input[@id='password']")).sendKeys(TestData.getUser("user_password", user_email));
        webDriver.findElement(By.xpath("//input[@id='confirmPassword']")).sendKeys(TestData.getUser("user_password", user_email));
        webDriver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='login-body' and contains(.,'Your password has been reset.')]")));
    }
}
