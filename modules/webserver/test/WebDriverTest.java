package test;

import junit.framework.Assert;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WebDriverTest
{

	WebDriver driver;
	
	@BeforeClass
	public void start() throws InterruptedException
	{
		driver = new FirefoxDriver();
	}
	
	@Test
	public void frontPage() throws InterruptedException
	{
		driver.get("http://www.google.com");
		Assert.assertEquals("Google", driver.getTitle());
	}
	
	@AfterClass
	public void stop() throws InterruptedException
	{
		driver.close();
	}
	
}
