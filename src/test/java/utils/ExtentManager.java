package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {

            // Updated path → inside target folder
            ExtentSparkReporter reporter = new ExtentSparkReporter("target/ExtentReport/Reports/extent-report.html");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }
        return extent;
    }
}