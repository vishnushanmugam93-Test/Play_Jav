package base;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import utils.ExtentManager;

/**
 * Base test class that initializes Playwright, Browser, and Page for tests.
 */
public class BaseTest {

	protected Playwright playwright;
	protected Browser browser;
	protected BrowserContext context;
	protected Page page;
	protected ExtentReports extent;
	protected ExtentTest test;
	
	@BeforeMethod
	public void setup(Method method) {
		// Initialize ExtentReports and create a test entry for the current test method
		extent = ExtentManager.getInstance();
		test = extent.createTest(method.getName());
		// Initialize Playwright and launch a Chromium browser with specified options
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
				.setHeadless(false)
				.setSlowMo(50)
				.setArgs(Arrays.asList("--window-size=1920,1080"))
				);
		// Create a new browser context (no explicit viewport size) to allow true maximized window
		context = browser.newContext();

		page = context.newPage();
	}
	
	
	@AfterMethod(alwaysRun = true)
	public void tearDownReport(ITestResult result) {
		// Log the test result to ExtentReports based on the test outcome
			if (result.getStatus() == ITestResult.FAILURE) {
				// Capture a screenshot on failure and attach it to the report using MediaEntityBuilder
				String screenshotPath = utils.ScreenshotUtil.takeScreenshot(page, result.getName());
				try {
					test.fail(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
				} catch (Exception e) {
					// Fallback: log the throwable and add the path as plain text if attaching fails
					test.fail(result.getThrowable());
					test.info("Screenshot: " + screenshotPath);
				}
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				test.pass("Test passed");
			} else  {
				test.skip("Test skipped");
			}
			extent.flush();
			//browser cleanup
			if (browser != null)browser.close();
			if (playwright != null)playwright.close();
		}
	}
