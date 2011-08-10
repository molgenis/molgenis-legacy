package test;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import boot.RunStandalone;

/**
 * Same as WebTest.java except now (4/5) implemented in WebDriver
 * @author joerivandervelde
 *
 */
public class XqtlWebDriverTest
{
	private WebDriver driver;
	Integer deployPort = 11000;
	Integer sleepTime = 5000;
	String pageLoadTimeout = "30000";
	String storagePath = new File(".").getAbsolutePath() + File.separator + "tmp_selenium_test_data";

	private void sleepHelper(String who) throws InterruptedException
	{
		System.out.println(who + " done, now sleeping for " + sleepTime + " msec");
		Thread.sleep(sleepTime);
	}
	
	
	private String propertyScript(String element, String property){
		return "var x = window.document.getElementById('"+element+"'); window.document.defaultView.getComputedStyle(x,null).getPropertyValue('"+property+"');";
	}

	@BeforeClass(alwaysRun = true)
	public void setupBeforeClass(ITestContext context) throws Exception
	{
		Helper.deleteDatabase();
		new RunStandalone(deployPort);
		driver = new FirefoxDriver();
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanupAfterClass() throws InterruptedException, Exception
	{
		driver.close();
		//TestHelper.deleteStorage();
	}

	@Test
	public void startup() throws InterruptedException
	{
		driver.get("http://localhost:"+deployPort+"/molgenis_apps/molgenis.do");
		Assert.assertEquals(driver.getTitle(), "xQTL workbench");
		Assert.assertTrue(driver.getPageSource().contains("Welcome"));
		Assert.assertEquals(driver.findElement(By.linkText("R api")).getText(), "R api");
		sleepHelper("startup");
	}

	@Test
	public void login() throws InterruptedException
	{
		driver.findElement(By.id("UserLogin_tab_button")).click();
		Assert.assertEquals(driver.findElement(By.linkText("Register")).getText(), "Register");
		driver.findElement(By.id("username")).sendKeys("admin");
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("Login")).click();
		//note: page now redirects to the Home screen ('auth_redirect' in properties)
		Assert.assertTrue(driver.getPageSource().contains("You are logged in as admin"));
		sleepHelper("login");
	}

	@Test
	public void loadExampleData() throws InterruptedException
	{
		driver.findElement(By.id("inputBox")).sendKeys(storagePath);
		sleepHelper("loadExampleData page loaded, now pressing button to load users, data, permissions etc");
		driver.findElement(By.id("loadExamples")).click();
		Assert.assertTrue(driver.getPageSource().contains("was validated and the dataloader succeeded"));
		sleepHelper("loadExampleData");
	}

	@Test
	public void exploreExampleData() throws InterruptedException
	{
		driver.findElement(By.id("Investigations_tab_button")).click();
		Assert.assertEquals(driver.findElement(By.linkText("Marker (117)")).getText(), "Marker (117)");
		driver.findElement(By.linkText("metaboliteexpression")).click();		
		Assert.assertTrue(driver.getPageSource().contains("<td class=\"matrixTableCell matrixRowColor1\">942</td>"));
		sleepHelper("exploreExampleData");
	}
	
	@Test
	public void compactView() throws InterruptedException
	{
		//CANNOT DO THIS TEST IN NATIVE WEBDRIVER
		//NEED TO WRAP SELENIUM LIKE THIS..
		//Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);
	
	}

}
