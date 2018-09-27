package com.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.base.PageBase;

public class DrugPage extends PageBase{
	int count =0;
	public void verifyDrugTitle(String str) {
		String drugname=getText("drugtitle");
		Assert.assertEquals(drugname, str);
	}
	
	public void verifyFreeCoupen(String str) {
		List<WebElement> freeCoupens = getListofElementsXpath("freeCoupenButton1");
		
		for(WebElement button:freeCoupens){
			if(button.getText().equalsIgnoreCase(str)){
				String text = getTextXpath("freeCoupenPrice");
			
				String handle= driver.getWindowHandle();
					click(button);
					
					for(String handle1:driver.getWindowHandles()){
						driver.switchTo().window(handle1);
						//Assert.assertEquals(actual, expected);
					}
					if(count==0){
					Assert.assertEquals(getText("priceOnNewWindow"), text);
					count++;
					}
					
					driver.close();
					
					driver.switchTo().window(handle);
					if(isElementPresentXpath("closeButton")){
						
						clickXpath("closeButton");
						
					
					}
				}
			}
	}

	
	
}
