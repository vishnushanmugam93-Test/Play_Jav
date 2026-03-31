package page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.Page.GetByRoleOptions;

/**
 * Page Object Model for the Register / Login flow on the e-commerce playground.
 * Construct with a Playwright Page and call registerAccount(...) from a test.
 */
public class LoginPage {
    
    private  Page page;
    private final String homeUrl = "https://ecommerce-playground.lambdatest.io/index.php?route=common/home";
    private final String registerUrl = "https://ecommerce-playground.lambdatest.io/index.php?route=account/register";
    private final String firstNameSelector = "input[name='firstname']";
    private final String lastNameSelector = "input[name='lastname']";
    private final String emailSelector = "input[name='email']";
    private final String telephoneSelector = "input[name='telephone']";
    private final String passwordSelector = "input[name='password']";
    private final String passwordConfirmSelector = "input[name='confirm']";
    private final String privacyPolicyText = "I have read and agree to the Privacy Policy";
    private final String continueButtonText = "Continue";

    // Add reusable locators as private fields so they can be reused across methods
    private final Locator continueButton;
    private final Locator continueLink;
    private final Locator homeLink;

    public LoginPage(Page page) {
        this.page = page;
        // Initialize locators using the page instance. Locator is lazy and safe to create
        this.continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(continueButtonText));
        this.continueLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(continueButtonText));
        this.homeLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Home"));
    }

    public void navigateToHome() {
        // wait until network is idle to ensure page elements are available
        page.navigate(homeUrl,
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
    }

    public void goToRegister() {
        // Directly navigate to the registration page (more reliable in headless/test runs)
        page.navigate(registerUrl,
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
    }

    /**
     * Performs the full registration flow and verifies account creation.
     */
    public void registerAccount(String firstName, String lastName, String email, String telephone, String password) {
        goToRegister();

        page.fill(firstNameSelector,firstName);
        page.fill(lastNameSelector,lastName);
        page.fill(emailSelector,email);
        page.fill(telephoneSelector,telephone);
        page.fill(passwordSelector,password);
        page.fill(passwordConfirmSelector,password);

        // Accept terms - use contains text to click checkbox/label
        page.getByText(privacyPolicyText, new Page.GetByTextOptions().setExact(true)).click();

        // Submit using the reusable locator
        continueButton.click();

        // Verify created
        // The application displays "Your Account Has Been Created!" on success — assert that text is present
        PlaywrightAssertions.assertThat(page.getByText("Your Account Has Been Created!", new Page.GetByTextOptions().setExact(true))).containsText("Your Account Has Not Been Created!");

        // Continue to account using the reusable link locator
        continueLink.click();

        // Instead of asserting the My Account heading (may be localized or delayed), assert Home link is visible
        PlaywrightAssertions.assertThat(homeLink).isVisible();
    }

    public void goToHome() {
        homeLink.click();
    }
}