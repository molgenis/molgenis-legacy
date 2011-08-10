package test;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import boot.RunStandalone;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

public class XqtlSeleniumTest
{
//	SeleniumServer server;
//	HttpCommandProcessor proc;
	
	Selenium selenium;
	Integer sleepTime = 1000;
	String pageLoadTimeout = "30000";
	String storagePath = new File(".").getAbsolutePath() + File.separator + "tmp_selenium_test_data";



	@BeforeClass
	public void start() throws Exception
	{
		
		int webserverPort = Helper.getAvailablePort(11000, 100);
		
		String seleniumUrl = "http://localhost:" + webserverPort + "/";
		String seleniumHost = "localhost";
		String seleniumBrowser = "firefox";
		int seleniumPort = Helper.getAvailablePort(9080, 100);
	
		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setSingleWindow(true);
		rcc.setPort(seleniumPort);

		try
		{
			SeleniumServer server = new SeleniumServer(false, rcc);
			server.boot();
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Cannot start selenium server: ", e);
		}

		HttpCommandProcessor proc = new HttpCommandProcessor(seleniumHost, seleniumPort, seleniumBrowser, seleniumUrl);
		selenium = new DefaultSelenium(proc);
		selenium.start();
		
		Helper.deleteDatabase();
		
		new RunStandalone(webserverPort);
	}

	@Test
	public void startup() throws InterruptedException
	{
		selenium.open("/molgenis_apps/molgenis.do");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getTitle(), "xQTL workbench");
		Assert.assertTrue(selenium.isTextPresent("Welcome"));
		Assert.assertEquals(selenium.getText("link=R api"), "R api");
		sleepHelper("startup");
	}

	@Test
	public void login() throws InterruptedException
	{
		//selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='UserLogin';document.forms.main.submit();\"]");
		selenium.click("id=UserLogin_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("link=Register"), "Register");
		selenium.type("id=username", "admin");
		selenium.type("id=password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad(pageLoadTimeout);
		
		//note: page now redirects to the Home screen ('auth_redirect' in properties)
		//note2: REDIRECT BROKEN, DISABLED ATM
		selenium.click("id=ClusterDemo_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		
		Assert.assertTrue(selenium.isTextPresent("You are logged in as admin, and the database does not contain any investigations or other users."));
		sleepHelper("login");
	}

	@Test
	public void loadExampleData() throws InterruptedException
	{
		selenium.type("id=inputBox", storagePath);
		sleepHelper("loadExampleData page loaded, now pressing button to load users, data, permissions etc");
		selenium.click("id=loadExamples");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("File path '"+storagePath+"' was validated and the dataloader succeeded"));
		sleepHelper("loadExampleData");
	}

	@Test
	public void exploreExampleData() throws InterruptedException
	{
		//selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='Investigations';document.forms.main.submit();\"]");
		selenium.click("id=Investigations_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("link=Marker (117)"), "Marker (117)");
		selenium.click("link=metaboliteexpression");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("//div[@id='Datas_screen']/div[2]/form/div/div[2]/div/table/tbody/tr[3]/td/table/tbody/tr[3]/td[4]"),"942");
		sleepHelper("exploreExampleData");
	}
	
	@Test
	public void compactView() throws InterruptedException
	{
		/*
		 * REMARK
		 * 
		 * Here you'd want to use a statement like:
		 * 
		 * Assert.assertFalse(selenium.isElementPresent("Investigation_description"));
		 * 
		 * or maybe
		 * 
		 * Assert.assertFalse(selenium.isTextPresent("description"));
		 * 
		 * But this is currently NOT WORKING. Selenium does not check the graphical layout
		 * of the page, and things hidden with JavaScript/CSS tricks are often not technically hidden.
		 * 
		 * So testing this directly will always result in TRUE even while the user CANNOT see the
		 * element on his/her screen. We'll use a trick instead: execute a piece of JavaScript to
		 * retrieve style that was applied to an element. We can check the result to find out if a
		 * property (ie. 'display') has a certain value we expect is has.
		 * 
		 */
		
		//assert if the hide investigation and data table rows are hidden
		Assert.assertEquals(selenium.getEval(propertyScript("Investigations_collapse_tr_id", "display")), "none");
		Assert.assertEquals(selenium.getEval(propertyScript("Datas_collapse_tr_id", "display")), "none");
		
		//click both unhide buttons
		selenium.click("id=Investigations_collapse_button_id");
		selenium.click("id=Datas_collapse_button_id");
		
		//assert if the hide investigation and data table rows are hidden
		Assert.assertEquals(selenium.getEval(propertyScript("Investigations_collapse_tr_id", "display")), "table-row");
		Assert.assertEquals(selenium.getEval(propertyScript("Datas_collapse_tr_id", "display")), "table-row");		
		
		sleepHelper("compactView");
	
	}
	
	@AfterClass
	public void stop() throws Exception
	{
		selenium.stop();
		Helper.deleteStorage();
	}
	
	private void sleepHelper(String who) throws InterruptedException
	{
		System.out.println(who + " done, now sleeping for " + sleepTime + " msec");
		Thread.sleep(sleepTime);
	}
	
	private String propertyScript(String element, String property){
		return "var x = window.document.getElementById('"+element+"'); window.document.defaultView.getComputedStyle(x,null).getPropertyValue('"+property+"');";
	}

}
