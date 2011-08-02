import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import boot.RunStandalone;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;



public class WebTest
{
	SeleniumServer server;
	HttpCommandProcessor proc;
	Selenium selenium;
	
	@BeforeSuite(alwaysRun = true)
	public void setupBeforeSuite(ITestContext context) {
	  String seleniumHost = "localhost";
	  String seleniumPort = "9080";
	  String seleniumBrowser = "firefox";
	  String seleniumUrl = "http://localhost:8080/molgenis_apps/";
	  
	  RemoteControlConfiguration rcc = new RemoteControlConfiguration();
	  rcc.setSingleWindow(true);
	  rcc.setPort(Integer.parseInt(seleniumPort));
	  
	  try {
	    server = new SeleniumServer(false, rcc);
	    server.boot();
	  } catch (Exception e) {
	    throw new IllegalStateException("Can't start selenium server", e);
	  }
	  
	  proc = new HttpCommandProcessor(seleniumHost, Integer.parseInt(seleniumPort),
	      seleniumBrowser, seleniumUrl);
	  selenium = new DefaultSelenium(proc);
	  selenium.start();
	}
	
	
	@BeforeClass
	public void setUp()
	{
		new RunStandalone();
	}
	
	@Test
	public void test1() throws InterruptedException
	{
		selenium.open("");
		selenium.waitForPageToLoad("30000");
		Thread.sleep(10000);
		Assert.assertEquals(selenium.getTitle(), "xQTL workbench");
		
		selenium.click("css=div.navigationNotSelected");
		selenium.waitForPageToLoad("30000");
		
		selenium.type("username", "admin");
		
		Thread.sleep(10000);
		

	}
}
