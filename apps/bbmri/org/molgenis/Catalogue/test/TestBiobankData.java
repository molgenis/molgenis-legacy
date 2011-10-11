package org.molgenis.Catalogue.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;


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



public class TestBiobankData
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
	public void setUp() {
		
	}
	
	@Test
	public void TestBiobankData() throws FileNotFoundException, SQLException, IOException, Exception {
		selenium.open("");
		selenium.waitForPageToLoad("2000");
		//Thread.sleep(30000);
		//Assert.assertEquals(selenium.getTitle(), "Catalogue of Dutch biobanks");
		Assert.assertEquals(selenium.getTitle(), "MOLGENIS load page");

		
		
		//update database 
		/*
		new Molgenis("apps/bbmri/org/molgenis/biobank/bbmri.molgenis.properties").updateDb(true);
		
		Database db = DatabaseFactory.create("apps/bbmri/org/molgenis/biobank/bbmri.molgenis.properties");
		
		MolgenisUser u = new MolgenisUser();
		u.setName("bbmri");
		u.setPassword("bbmri");
		u.setSuperuser(true);
		u.setFirstname("Margreet");
		u.setLastname(" Brandsma");
		u.setEmailaddress("m.brandsma@bbmri.nl");
		
		db.add(u);
		*/
		
		//login
		
		selenium.open("/molgenis_apps/molgenis.do?__target=main&select=SimpleUserLogin");
		selenium.waitForPageToLoad("20000");
		
		selenium.type("username", "admin");
		selenium.type("password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad("10000");
		//Thread.sleep(1000);
	
		//Browse to Biobank 
		selenium.open("/molgenis_apps/molgenis.do?__target=main&select=BiobankOverview"); 
		selenium.waitForPageToLoad("30000");
		
		//add a new record
		selenium.click("id=Cohorts_edit_new");
		selenium.waitForPopUp("molgenis_edit_new", "3000");
		
		Thread.sleep(10000);

		
		
	       
		
	
	

		Thread.sleep(10000);

	}
}
