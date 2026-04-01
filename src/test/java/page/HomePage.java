package page;

import com.microsoft.playwright.Page;

public class HomePage {

	private Page page;
	private final String Search ="input[name='search']";
	private final String searchButton = "button[class='type-text']";
	
	
	public HomePage(Page page) {
		this.page = page;
	}
	
	public void searchProduct(String productName) {
		page.fill(Search, productName);
		page.click(searchButton);
	}
	
	
}
