package org.molgenis.Catalogue.test;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;



public class SearchTest
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
//		try{
//		try{
//			new WebserverGui();
//		}catch(HeadlessException e){
//			System.out.println("No GUI available going into commandline mode");
//			new Thread(new WebserverCmdLine()).start();
//		}
//		}catch(IOException e){
//			System.out.println("IO exception bubbled up to main\nSomething went wrong: " + e.getMessage());
//		}
	}
	
	@Test
	public void testSearch() throws InterruptedException
	{
		selenium.open("");
		selenium.waitForPageToLoad("20000");
		Thread.sleep(30000);
		Assert.assertEquals(selenium.getTitle(), "Catalogue of Dutch biobanks");
		
		selenium.click("css=div.leftNavigationNotSelected");
		selenium.waitForPageToLoad("20000");
		
		selenium.type("username", "admin");
		selenium.type("password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad("10000");
		
		
		Thread.sleep(1000);
		selenium.open("/molgenis_apps/molgenis.do?__target=main&select=BiobankOverview"); 
		selenium.waitForPageToLoad("30000");
	       
		
		selenium.type("id=__filter_value", "cohort");
		selenium.click("id=filter_add");

		Thread.sleep(10000);

	}
}
