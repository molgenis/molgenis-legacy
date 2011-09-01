package test.webtestframeworks;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.Selenium;

public class SeleniumOnWebDriverBackendTestWebFrameworkOnly
{
	
	Selenium selenium;
	String pageLoadTimeout = "30000";

	@BeforeClass
	public void start() throws Exception
	{
		WebDriver driver = new FirefoxDriver();
		selenium = new WebDriverBackedSelenium(driver, "http://www.google.com");
	}
	
	@Test
	public void google() throws InterruptedException
	{
		selenium.open("");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getTitle(), "Google");
	}
	
	@AfterClass
	public void stop() throws Exception
	{
		selenium.stop();
	}

}
