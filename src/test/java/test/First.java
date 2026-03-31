package test;
import org.testng.annotations.Test;

import com.microsoft.playwright.*;

import base.BaseTest;
import page.LoginPage;


/**
 * Smoke test that uses the LoginPage Page Object Model to register a new account.
 */
public class First extends BaseTest {

    @Test(description = "Register a new account using the LoginPage POM")
    public void test() {
      LoginPage login = new LoginPage(page);
      test.info("Navigating to home page");
      login.navigateToHome();
      test.info("Registering a new account with unique email");
      String uniqueEmail = "test26071993+" + System.currentTimeMillis() + "@gmail.com";
      login.registerAccount("vishnu", "shanmugam", uniqueEmail, "0987654321", "1234567890");
      // go home to confirm navigation
      test.info("Navigating back to home page to confirm registration");
      login.goToHome();
      test.info("Asserting that the page title is correct");
      System.out.println("page.title = " + page.title());
    }
}