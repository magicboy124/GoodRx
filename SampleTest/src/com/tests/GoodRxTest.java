package com.tests;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import com.pages.DrugPage;
import com.pages.HomePage;

public class GoodRxTest {

	HomePage homePage = new HomePage();
	

	@Test(priority=0)
	public void checkHomePage() {

		homePage.verifyHomePage();
	}

	@Test(priority=1)
	public void searchText() {

		homePage.enterDrugToSerch("Amoxil");

	}

	@Test(priority=2)
	public void checkSearchList() {

		homePage.verifySearchList("Amoxil");
	}
	
	@SuppressWarnings("null")
	@Test(priority=3)
	public void verifyDrug() {

		DrugPage drugPage = new DrugPage();
		drugPage.verifyDrugTitle("Amoxil");
		drugPage.verifyFreeCoupen("Get free Coupon");
	}
	
	//@AfterSuite
	/*public void afterSuite(){
		homePage.quit();
	}*/
	
	
	
}
