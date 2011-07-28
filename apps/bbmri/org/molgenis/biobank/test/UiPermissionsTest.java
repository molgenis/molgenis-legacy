package org.molgenis.biobank.test;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;

import jxl.WorkbookSettings;

import org.molgenis.util.XlsWriter;
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



public class UiPermissionsTest
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
	public void test1() throws InterruptedException
	{
		selenium.open("");
		selenium.waitForPageToLoad("20000");
		Thread.sleep(10000);
		Assert.assertEquals(selenium.getTitle(), "Catalogue of Dutch biobanks");
		
		selenium.click("css=div.leftNavigationNotSelected");
		selenium.waitForPageToLoad("20000");
		
		selenium.type("username", "admin");
		selenium.type("password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad("20000");
		
		
		Thread.sleep(10000);
		//selenium.click("css=div.leftNavigationNotSelected");
		selenium.clickAt("css=div.leftNavigationNotSelected", "UserLogin");
		Thread.sleep(9000);

		selenium.clickAt("css=div.leftNavigationNotSelected", "Admin");
		//TODO : find how to select different div.leftNavigationNotSelected items , maybe change css ids?
		//selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='BiobankOverview';document.forms.main.submit()\"]");
		selenium.waitForPageToLoad("30000");
		
		Thread.sleep(10000);
		

		String inputFile = 	System.getProperty("java.io.tmpdir");


		//create a path in java tmp directory
		File excelFile = new File(inputFile+ "a.xls");


		/* Create new Excel workbook and sheet */
		WorkbookSettings wbSettings = new WorkbookSettings();
		//wbSettings.setLocale(new Locale("en", "EN"));
		

		//WritableWorkbook workbook = Workbook.createWorkbook(excelFile, wbSettings);
		//WritableSheet s = workbook.createSheet("Sheet1", 0);
		
		XlsWriter xlswr = new XlsWriter();


		

	}
}
