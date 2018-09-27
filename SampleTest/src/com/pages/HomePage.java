package com.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.base.PageBase;

public class HomePage extends PageBase{

	public void verifyHomePage() {
		driver.get(CONFIG.getProperty("testSiteLadingPageURL"));
		Assert.assertEquals(driver.getCurrentUrl(), CONFIG.getProperty("testSiteLadingPageURL"));
	}
	
	public void enterDrugToSerch(String str){
		System.err.println("Enter");
		enterText("searchIcon", str);
	}
	
	public void verifySearchList(String str){
		System.err.println(str);
		if(isElementPresent("predictiveSearchDropdown")){
		List<WebElement> searchList = getListofElements("predictiveSearchDropdown");
		
		for(WebElement drug: searchList){
			System.err.println(drug.getText());
			if(drug.getText().contains(str)){
				click(drug);
				break;
			}
		}
		}
	}	
}
