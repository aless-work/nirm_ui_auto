package main.java.login;

import java.util.Date;
import java.util.List;

import main.java.NirmataMailer;
import main.java.NirmataSetUp;
import main.java.TestData;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

@Test (groups = { "SignIn"} )
public class SignIn {

	private WebDriver webDriver;
	private Date testStartTime;
	private String login_email;
	private String app_url;

	@BeforeGroups(groups = {"SignIn"})
	@Parameters({"login_email", "app_url"})
	public void SetupDriver(String login_email, @Optional String app_url) {

		this.webDriver = NirmataSetUp.webDriver;
		testStartTime = new Date();
		this.login_email = login_email;
		this.app_url = (app_url.isEmpty()) ? TestData.getProperty("App_URL") : app_url;
	}

	@Test (testName = "Open Login Page")
	public void openLoginPage() {
//		webDriver.get(app_url);
		webDriver.get(app_url + TestData.getProperty("Logout_URI"));
		String title = webDriver.getTitle();
		Assert.assertEquals(title, TestData.getAssertions("LoginPageTitle"));
	}

	@Test (dependsOnMethods = { "openLoginPage" }, testName = "Verify Login Page")
	public void verifyLoginPage() {
		WebDriverWait wait = new WebDriverWait(webDriver, 40);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-title")));

		WebElement signinpage = webDriver.findElement(By.className("login-title"));
		String loginPagetitel = signinpage.getText();
		Assert.assertEquals(loginPagetitel, TestData.getAssertions("SigninPageHeader"));
	}

	@Test (dependsOnMethods = { "verifyLoginPage" }, testName = "Set Email Id")
	public void setEmailID() {

		webDriver.findElement(By.id("email")).sendKeys(TestData.getUser("email", login_email));
		webDriver.findElement(By.xpath("//button[@id='btnLogin']")).click();
		WebDriverWait wait = new WebDriverWait(webDriver, 40);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("email-address")));
	}

	@Test (dependsOnMethods = { "setEmailID" }, testName = "Get access code from email")
	public void verifyAccessCode() {
		if (checkVerificationType() == 0) {
			NirmataMailer nm = new NirmataMailer(TestData.getUser("email_host", login_email),
					TestData.getUser("email_protocol", login_email),
					TestData.getUser("email_folder", login_email),
					TestData.getUser("email", login_email),
					TestData.getUser("email_password", login_email));
			String code = nm.getAccessCode(testStartTime, 300000);
			webDriver.findElement(By.id("password")).clear();
			webDriver.findElement(By.id("password")).sendKeys(code);
			webDriver.findElement(By.xpath("//button[@id='btnLogin']")).click();
		}

		WebDriverWait wait = new WebDriverWait(webDriver, 40);
		wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//div[@class='login-title']"), "Verify Identity")));
	}

	@Test (dependsOnMethods = { "verifyAccessCode" }, testName = "Select account to login")
	@Parameters("login_account")
	public void selectAccount(@Optional String login_account) {

		if (checkVerificationType() == 1) {
			List<WebElement> selectAccount = webDriver.findElements(By.xpath("//*[contains(@class,'text-primary')]//*[.='" + login_account + "']"));
			if (selectAccount.size()>0)	selectAccount.get(0).click();
			else webDriver.findElement(By.xpath("//*[@id=\"accounts\"]/div[1]")).click();
		}
		Assert.assertTrue(checkVerificationType() > 1);
	}

	@Test (dependsOnMethods = { "selectAccount" }, testName = "Set Password")
	public void setPassword() {

		// enter the password
		webDriver.findElement(By.id("password")).sendKeys(TestData.getUser("user_password", login_email));
		webDriver.findElement(By.xpath("//button[@id='btnLogin']")).click();

		// wait until 'Sign In' btn disappeared
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//button[@id='btnLogin']")));
	}

	@Test (dependsOnMethods = { "setPassword" }, testName = "Verify Main page")
	public void verifyMainPage() {

		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='navbar-header']")));

		if (webDriver.findElements(By.id("skipSetup")).size() > 0)
			webDriver.findElements(By.id("skipSetup")).get(0).click();
		else if (webDriver.findElements(By.id("convertToTire")).size() > 0)
			Assert.fail("Nirmata free trial has ended. Sign in is cancelled.");
//			webDriver.findElements(By.id("convertToTire")).get(0).click();

		wait = new WebDriverWait(webDriver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("main-navigation")));
	}

	@AfterGroups (groups = {"SignIn", "SignOut"})
	public void signOut() {
		webDriver.get("https://nirmata.io/webclient/#logout");
	}

	private int checkVerificationType () {
		WebDriverWait wait = new WebDriverWait(webDriver, 40);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='login-title']")));
		String login_title = webDriver.findElement(By.xpath("//div[@class='login-title']")).getText();
		if (login_title.contains("Verify Identity")) return 0;
		if (login_title.contains("Select an Account")) return 1;
		if (login_title.contains("Hello,")) return 2;
		return -1;
	}

}
