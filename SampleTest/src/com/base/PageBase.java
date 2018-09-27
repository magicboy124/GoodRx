package com.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class PageBase {
	
	
	public static WebDriver driver = null;
	public static Properties CONFIG =null;
	public static Properties ELEMENTS =null;

	public PageBase(){
		
	
		if(driver==null){
		CONFIG= new Properties();
		ELEMENTS = new Properties();
		try{
			FileInputStream fs = new FileInputStream("src\\config\\config.properties");
			CONFIG.load(fs);
			fs = new FileInputStream("src\\config\\elements.properties");
			ELEMENTS.load(fs);
			}catch(Exception e){
				return;
		}
		
		System.out.println(CONFIG.getProperty("browser"));
		if(CONFIG.getProperty("browser").equals("Mozilla")){
			System.setProperty("webdriver.gecko.driver", "firefox\\geckodriver.exe");
			driver=new FirefoxDriver();
		}
		else if(CONFIG.getProperty("browser").equals("IE"))
		    driver=new InternetExplorerDriver();
		else if(CONFIG.getProperty("browser").equals("Chrome")){
			System.setProperty("webdriver.chrome.driver", "chromedriver\\chromedriver.exe");
		    driver=new ChromeDriver();
		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
}
}
	
	public void click(String cssKey){
		try{
	        driver.findElement(By.cssSelector(ELEMENTS.getProperty(cssKey))).click();
		}catch(Exception e){
			System.out.println("error");
			e.printStackTrace();
		}
	}
	public void clickXpath(String xpath){
		try{
	        driver.findElement(By.xpath(ELEMENTS.getProperty(xpath))).click();
		}catch(Exception e){
			System.out.println("error");
			e.printStackTrace();
		}
	}
	public void click(WebElement ele){
		try{
	        ele.click();
		}catch(Exception e){
			System.out.println("error");
			e.printStackTrace();
		}
	}
	public void enterText(String cssKey, String text){
		try{
		driver.findElement(By.cssSelector(ELEMENTS.getProperty(cssKey))).sendKeys(text);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean isElementPresent(String cssKey){
		try{
			driver.findElement(By.cssSelector(ELEMENTS.getProperty(cssKey)));
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	public boolean isElementPresentXpath(String xpath){
		try{
			driver.findElement(By.xpath(ELEMENTS.getProperty(xpath)));
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	public String getText(String cssKey){
		String text = null;
		try{
			text=driver.findElement(By.cssSelector(ELEMENTS.getProperty(cssKey))).getText();
		}catch(Exception e){
			e.printStackTrace();
		}
		return text;
	}
	
	public String getTextXpath(String xpath){
		String text = null;
		try{
			text=driver.findElement(By.xpath(ELEMENTS.getProperty(xpath))).getText();
		}catch(Exception e){
			e.printStackTrace();
		}
		return text;
	}
	public static void takeScreenshot(String fileName) {
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	    try {
			FileUtils.copyFile(scrFile, new File("screenshots\\"+fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<WebElement> getListofElements(String cssKey){
		List<WebElement> list = null;
		try{
			System.out.println(ELEMENTS.getProperty(cssKey));
	        list=driver.findElements(By.cssSelector(ELEMENTS.getProperty(cssKey)));
	        System.out.println(list);
		}catch(Exception e){
			System.out.println("error");
			e.printStackTrace();
		}
		return list;
	}
	
	public List<WebElement> getListofElementsXpath(String xpath){
		List<WebElement> list = null;
		try{
			System.out.println(ELEMENTS.getProperty(xpath));
	        list=driver.findElements(By.xpath(ELEMENTS.getProperty(xpath)));
	        System.out.println(list);
		}catch(Exception e){
			System.out.println("error");
			e.printStackTrace();
		}
		return list;
	}
	public void quit(){
		if(driver!=null){
			driver.quit();
		}
	}
}
