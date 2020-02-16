package main.java;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;

public class NirmataReportListener implements ITestListener {
	
	private static Logger _logger = LoggerFactory.getLogger(NirmataReportListener.class);
	public static NirmataApplicationProperties appproperties;

	FileWriter fileWriter;
	FileWriter fileWriterTest;

	@Override
	public void onTestStart(ITestResult result) {

		NirmataSetUp.methodInfo = NirmataSetUp.testInfo.createNode(result.getName());// .assignCategory(NirmataSetUp.testName);

	}

	@Override
	public void onTestSuccess(ITestResult result) {
		NirmataSetUp.methodInfo.pass("Test Case Name : " + result.getName() + " is passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		NirmataSetUp.methodInfo.log(Status.FAIL, "Test Case Name : " + result.getName() + " is failed");
		NirmataSetUp.methodInfo.log(Status.FAIL, "Test fallure : " + result.getThrowable());
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		NirmataSetUp.methodInfo = NirmataSetUp.testInfo.createNode(result.getName());// .assignCategory(NirmataSetUp.testName);
		NirmataSetUp.methodInfo.log(Status.SKIP, "Test Case Name : " + result.getName() + " is skiped");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) { 
		//_logger.info("onTestFailedButWithinSuccessPercentage");
	}

	@Override
	public void onStart(ITestContext context) {
		appproperties = new NirmataApplicationProperties();
		// NirmataSetUp.testInfo=NirmataSetUp.suiteInfo.createNode(context.getName());
		NirmataSetUp.testInfo = NirmataSetUp.reports.createTest(context.getName()).assignCategory(context.getName());
		NirmataSetUp.testName = context.getName();
		_logger.info("Test Case : {}",context.getName());
		_logger.info("\n");
	}

	@Override
	public void onFinish(ITestContext context) {

		if (context.getFailedConfigurations().size() > 0 || context.getFailedTests().size() > 0) {
			_logger.info("Status : Failed");
		} else { 
			_logger.info("Status : Pass");
		}

		if (context.getFailedConfigurations().size() > 0 || context.getFailedTests().size() > 0) {
			
			try {
				fileWriter = new FileWriter("report/failedSubject.txt", false);
				fileWriter.write("FAILED\n");
				fileWriter.close();
			} catch (Exception e) {
				e.printStackTrace();

			}

			try {
				fileWriterTest = new FileWriter("report/failedMethods.txt", true);
				fileWriterTest.write(context.getName() + " Failed.\n");
				fileWriterTest.close();
			} catch (Exception e) {
				e.printStackTrace();

			}
		} else {
			
			try {
				Stream<String> lines = Files.lines(Paths.get("report/failedSubject.txt"));
				
				if (!lines.filter(t -> t.contains("FAILED")).findAny().isPresent()) {
					fileWriter = new FileWriter("report/failedSubject.txt", false);
					fileWriter.write("PASSED\n");
					fileWriter.close();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

		}
	}

}
