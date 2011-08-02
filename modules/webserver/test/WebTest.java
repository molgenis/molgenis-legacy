package test;

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
	public void setupBeforeSuite(ITestContext context)
	{
		String seleniumHost = "localhost";
		String seleniumPort = "9080";
		String seleniumBrowser = "firefox";
		String seleniumUrl = "http://localhost:8080/";

		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setSingleWindow(true);
		rcc.setPort(Integer.parseInt(seleniumPort));

		try
		{
			server = new SeleniumServer(false, rcc);
			server.boot();
		}
		catch (Exception e)
		{
			throw new IllegalStateException("Can't start selenium server", e);
		}

		proc = new HttpCommandProcessor(seleniumHost, Integer.parseInt(seleniumPort), seleniumBrowser, seleniumUrl);
		selenium = new DefaultSelenium(proc);
		selenium.start();
	}

	@BeforeClass
	public void setUp()
	{
		new RunStandalone();
	}

	@Test
	public void setupDatabase() throws InterruptedException
	{
		selenium.open("/molgenis_apps/resetdatabase_4EE1D7A3E73C504183B69F7D20108853");
		selenium.waitForPageToLoad("30000");
	}

	@Test
	public void login() throws InterruptedException
	{
		selenium.open("/molgenis_apps/molgenis.do");
		selenium.waitForPageToLoad("30000");
		Assert.assertEquals(selenium.getTitle(), "xQTL workbench");
		selenium.click("css=div.navigationNotSelected");
		selenium.waitForPageToLoad("30000");
		selenium.type("id=username", "admin");
		selenium.type("id=password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad("30000");
	}

	@Test
	public void setupStorage() throws InterruptedException
	{

		selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='Admin';document.forms.main.submit();\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@onclick=\"document.forms.Admin.__target.value='Admin';document.forms.Admin.select.value='Settings';document.forms.Admin.submit();\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("//input[@value='Set path']");
		selenium.waitForPageToLoad("30000");
		selenium.click("//input[@value='Validate']");
		selenium.waitForPageToLoad("30000");

	}

	@Test
	public void loadExampleData() throws InterruptedException
	{
		selenium.click("//div[@onclick=\"document.forms.Admin.__target.value='Admin';document.forms.Admin.select.value='DatabaseSettings';document.forms.Admin.submit();\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Load example data");
		selenium.waitForPageToLoad("30000");
	}

	@Test
	public void exploreExampleData() throws InterruptedException
	{
		selenium.open("/molgenis_apps/molgenis.do");
		selenium.waitForPageToLoad("30000");
		selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='Investigations';document.forms.main.submit();\"]");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=metaboliteexpression");
		selenium.waitForPageToLoad("30000");
	}
}
