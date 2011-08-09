package test;

import junit.framework.Assert;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class WebDriverTest
{

	@Test
	public void frontPage() throws InterruptedException
	{
		WebDriver driver = new FirefoxDriver();
		driver.get("http://www.google.com");
		Assert.assertEquals("Google", driver.getTitle());
	}
	
}
