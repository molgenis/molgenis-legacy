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
	Integer deployPort = 11000;
	Integer sleepTime = 5000;
	String pageLoadTimeout = "30000";

	public void sleepHelper(String who) throws InterruptedException
	{
		System.out.println(who + " done, now sleeping for " + sleepTime + " msec");
		Thread.sleep(sleepTime);
	}

	@BeforeSuite(alwaysRun = true)
	public void setupBeforeSuite(ITestContext context)
	{
		String seleniumHost = "localhost";
		String seleniumPort = "9080";
		String seleniumBrowser = "firefox";
		String seleniumUrl = "http://localhost:" + deployPort + "/";

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
		new RunStandalone(deployPort);
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
		selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='UserLogin';document.forms.main.submit();\"]");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("link=Register"), "Register");
		selenium.type("id=username", "admin");
		selenium.type("id=password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Logged in as admin"));
		sleepHelper("login");
	}

	@Test
	public void loadExampleData() throws InterruptedException
	{
		selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='ClusterDemo';document.forms.main.submit();\"]");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("You are logged in as admin, and the database does not contain any investigations or other users."));
		sleepHelper("loadExampleData page loaded, now pressing button to load users, data, permissions etc");
		selenium.click("id=loadExamples");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("File path './data' was validated and the dataloader succeeded"));
		sleepHelper("loadExampleData");
	}

	@Test
	public void exploreExampleData() throws InterruptedException
	{
		selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='Investigations';document.forms.main.submit();\"]");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("link=Marker (117)"), "Marker (117)");
		selenium.click("link=metaboliteexpression");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("//div[@id='Datas_screen']/div[2]/form/div/div[2]/div/table/tbody/tr[3]/td/table/tbody/tr[3]/td[4]"),"942");
		sleepHelper("exploreExampleData");
	}
}
