package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.microsoft.playwright.Page;

public class ScreenshotUtil {

	public static String takeScreenshot(Page page, String testName) {
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String filename = testName + "_" + timestamp + ".png";
		// Return a path relative to test-output (where the extent report lives)
		String relativePath = "screenshots/" + filename;
		Path absolutePath = Paths.get("test-output").resolve(relativePath);
		try {
			// Ensure parent directories exist
			Files.createDirectories(absolutePath.getParent());
			// Save screenshot to the absolute path
			page.screenshot(new Page.ScreenshotOptions().setPath(absolutePath).setFullPage(true));
		} catch (Exception e) {
			e.printStackTrace();
			// If screenshot failed, still return the relative path so report link is consistent
			return relativePath;
		}
		// Return relative path so Extent report (located in test-output) can resolve it as "screenshots/....png"
		return relativePath;
	}
}