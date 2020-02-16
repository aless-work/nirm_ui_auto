package main.java;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestContext;
import org.testng.annotations.*;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

@Test (groups = { "NirmataSetUp" })
public class NirmataSetUp {

	public static WebDriver webDriver;
	public static NirmataApplicationProperties appproperties;
	public static ExtentReports reports;
	public static ExtentTest suiteInfo;
	public static ExtentTest methodInfo;
	public static ExtentTest testInfo;
	public static ExtentHtmlReporter htmlReporter;
	public static String testName;

	@BeforeSuite
	@Parameters({"browser", "headless"})
	public void setupReport(String browser, @Optional String headless, ITestContext ctx) throws MalformedURLException {
		String suiteName = ctx.getCurrentXmlTest().getSuite().getName();
		appproperties = new NirmataApplicationProperties();
		htmlReporter = new ExtentHtmlReporter(appproperties.properties.getProperty("ReportDirectory"));
		htmlReporter.loadXMLConfig(new File(System.getProperty("user.dir") + "/src/main/resources/extent-config.xml"));
		htmlReporter.config().setChartVisibilityOnOpen(false);
		// htmlReporter.setAppendExisting(true);

		boolean _headless = (headless != null) && headless.equalsIgnoreCase("true");

		try {
			FileWriter fileWriterTest = new FileWriter("report/failedMethods.txt", false);

			fileWriterTest.close();
		} catch (Exception e) {
			e.printStackTrace();

		}

		try {
			FileWriter fileWriterSubject = new FileWriter("report/failedSubject.txt", false);

			fileWriterSubject.close();
		} catch (Exception e) {
			e.printStackTrace();

		}

		reports = new ExtentReports();
		reports.setAnalysisStrategy(AnalysisStrategy.TEST);
		reports.attachReporter(htmlReporter);
		// suiteInfo=reports.createTest(suiteName).assignCategory(suiteName);

		String os = System.getProperty("os.name").toLowerCase();
		switch (browser) {
		case "Chrome":

//			System.setProperty("webdriver.chrome.driver", appproperties.properties.getProperty("ChromeDriverPath"));
			ChromeOptions Chromeoptions = new ChromeOptions();
			Chromeoptions.addArguments("start-maximized");
			Chromeoptions.addArguments("--disable-infobars");
			Chromeoptions.addArguments("--no-sandbox");
			Chromeoptions.setHeadless(_headless);

			if (os.contains("win")) { System.setProperty("webdriver.chrome.driver",
					appproperties.properties.getProperty("ChromeDriverWin")); }
			else if (os.contains("mac")) { System.setProperty("webdriver.chrome.driver",
					appproperties.properties.getProperty("ChromeDriverMac")); }
			else { System.setProperty("webdriver.chrome.driver",
					appproperties.properties.getProperty("ChromeDriverUbu"));
					Chromeoptions.setHeadless(true);}

			webDriver = new ChromeDriver(Chromeoptions);
			/*
			 * Set<Cookie> allCookies = webDriver.manage().getCookies(); for(Cookie cookie :
			 * allCookies) { webDriver.manage().addCookie(cookie); }
			 */
			webDriver.get(appproperties.getApplicationUrl());

			break;
		case "Firefox":

//			System.setProperty("webdriver.gecko.driver", appproperties.properties.getProperty("FireFoxDriverPath"));
			FirefoxOptions Firefoxoptions = new FirefoxOptions();

			if (_headless) Firefoxoptions.addArguments("-headless");

			if (os.contains("win")) { System.setProperty("webdriver.gecko.driver",
					appproperties.properties.getProperty("FireFoxDriverWin")); }
			else if (os.contains("mac")) { System.setProperty("webdriver.gecko.driver",
					appproperties.properties.getProperty("FireFoxDriverMac")); }
			else { System.setProperty("webdriver.gecko.driver",
					appproperties.properties.getProperty("FireFoxDriverUbu"));
				Firefoxoptions.addArguments("-headless");}

			webDriver = new FirefoxDriver(Firefoxoptions);
			webDriver.get(appproperties.getApplicationUrl());
			break;
		default:
			break;
		}

	}

	@AfterSuite
	public void closeReport() {
		reports.flush();
		if (webDriver != null) {
			webDriver.close();
		}

	}

	@BeforeMethod
	public void reportStart(Method method) {
		String descriptiveTestName = method.getAnnotation(Test.class).testName();

		if (descriptiveTestName != null && !descriptiveTestName.trim().equals("")) {
			methodInfo = NirmataSetUp.testInfo.createNode(descriptiveTestName).assignCategory("Host Groups");
		} else {
			methodInfo = NirmataSetUp.testInfo.createNode(method.getName()).assignCategory("Host Groups");
		}
		NirmataSetUp.methodInfo = this.methodInfo;
	}

}
