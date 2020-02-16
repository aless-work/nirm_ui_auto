package main.java;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NirmataTools {

    public static WebElement waitUntilVisible (WebDriver webDriver, long waitTime, String xpath) {
        WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
        By by = new By.ByXPath(xpath);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return webDriver.findElement(by);
    }

    public static void waitUntilInvisible (WebDriver webDriver, long waitTime, String xpath) {
        WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
        By by = new By.ByXPath(xpath);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    public static void waitUntilTitleContains (WebDriver webDriver, long waitTime, String titleText) {
        WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
        wait.until(ExpectedConditions.titleContains(titleText));
    }
}
