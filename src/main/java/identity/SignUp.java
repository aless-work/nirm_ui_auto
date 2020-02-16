package main.java.identity;

import main.java.NirmataSetUp;
import main.java.NirmataTools;
import main.java.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Date;

@Test (groups = {"SignUp"})
public class SignUp extends ActivateNewUser {
	private WebDriver webDriver;
	private String signup_email;
	private Date testStartTime;
	private String app_url;

	@BeforeGroups (groups = {"SignUp"})
	@Parameters({"signup_email", "app_url"})
	public void SetupDriver(String signup_email, @Optional String app_url) {
		this.webDriver = NirmataSetUp.webDriver;
		testStartTime = new Date();
		this.signup_email = signup_email;
		this.app_url = (app_url.isEmpty()) ? TestData.getProperty("App_URL") : app_url;
	}

	@Test(testName = "Open Signup Page")
	public void openSignupPage() {
		webDriver.get(app_url + TestData.getProperty("SignUp_URI"));
		String title = webDriver.getTitle();
		Assert.assertEquals(title, TestData.getAssertions("SignUpPageTitle"));
	}

	@Test(testName = "Set Data For SignUp", dependsOnMethods = { "openSignupPage" })
	public void fillDataForSignup() {
		NirmataTools.waitUntilVisible(webDriver,60,"//input[@id='name']").sendKeys(TestData.getUser("user_name", signup_email));
		NirmataTools.waitUntilVisible(webDriver,60,"//input[@id='email']").sendKeys(TestData.getUser("email", signup_email));
		NirmataTools.waitUntilVisible(webDriver,60,"//input[@id='company']").sendKeys(TestData.getUser("company_name", signup_email));
		NirmataTools.waitUntilVisible(webDriver,60,"//input[@id='phone']").sendKeys(TestData.getUser("user_phone", signup_email));
		NirmataTools.waitUntilVisible(webDriver,60,"//button[@id='btnSignupEmail']").click();
	}

	@Test(testName = "Accept user agreement", dependsOnMethods = { "fillDataForSignup" })
	public void acceptAgreement() {
		NirmataTools.waitUntilVisible(webDriver,60,"//button[contains(text(),'Accept and Proceed')]").click();
		NirmataTools.waitUntilInvisible(webDriver,60,"//button[contains(text(),'Accept and Proceed')]");
	}

	@Test(testName = "Verify if user created", dependsOnMethods = { "acceptAgreement" })
	public void verifyUserCreated() {
		WebElement we = NirmataTools.waitUntilVisible(webDriver,60,"//div[@class='login-title']");
		Assert.assertTrue(we.getText().contains("Sign up completed "), "Validation error. User wasn't created.");

		super.webDriver = this.webDriver;
		super.user_email = this.signup_email;
		super.testStartTime = this.testStartTime;
		super.getActivationLink();
		super.setupPassword();
	}
}
