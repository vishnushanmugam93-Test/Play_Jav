package base;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import page.LoginPage;
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

    // Initialize Playwright/browser/page once per test class so multiple @Test methods
    // within the same class reuse the same browser/page instance
    @BeforeClass
    public void initBrowser() {
        extent = ExtentManager.getInstance();
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(50)
                .setArgs(Arrays.asList("--window-size=1920,1080"))
        );
        // Create a new browser context (no explicit viewport size) to allow true maximized window
        context = browser.newContext();

        page = context.newPage();

        // Navigate to home once for the class so tests start from the same place
        try {
            LoginPage login = new LoginPage(page);
            login.navigateToHome();
        } catch (Exception e) {
            // swallow navigation errors here; individual tests may navigate as needed
            System.out.println("Warning: failed to navigate to home in @BeforeClass: " + e.getMessage());
        }
    }

    @BeforeMethod
    public void setup(Method method) {
        // Initialize ExtentReports entry for the current test method
        // extent is already initialized in @BeforeClass
        test = extent.createTest(method.getName());
        // Note: Playwright/browser/page are already created in @BeforeClass and reused across @Test methods
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
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        // browser cleanup
        try {
            if (browser != null) browser.close();
        } catch (Exception e) {
            System.out.println("Warning: failed to close browser: " + e.getMessage());
        }
        try {
            if (playwright != null) playwright.close();
        } catch (Exception e) {
            System.out.println("Warning: failed to close playwright: " + e.getMessage());
        }
    }
}