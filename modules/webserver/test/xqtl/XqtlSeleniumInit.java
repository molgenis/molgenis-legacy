package test.xqtl;

import java.io.File;

import org.molgenis.util.DetectOS;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import test.Helper;
import app.servlet.MolgenisServlet;
import boot.RunStandalone;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

/**
 * Fundamental class for xQTL Selenium test. Takes care of setting up the
 * server, the data, shutdown, and helper functions.
 * 
 */
public class XqtlSeleniumInit
{

	Selenium selenium;
	String pageLoadTimeout = "30000";
	boolean tomcat = false;
	String appName;

	/**
	 * Configure Selenium server and delete the database
	 */
	@BeforeClass
	public void start() throws Exception
	{
		appName = MolgenisServlet.getMolgenisVariantID();
		int webserverPort = 8080;
		if (!tomcat) webserverPort = Helper.getAvailablePort(11000, 100);

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

		if (!tomcat) new RunStandalone(webserverPort);
	}

	/**
	 * Stop Selenium server and remove files
	 */
	@AfterClass
	public void stop() throws Exception
	{
		selenium.stop();
		Helper.deleteStorage();
	}

	/**
	 * Start the app and verify home page
	 */
	@Test
	public void startup() throws InterruptedException
	{
		selenium.open("/" + appName + "/molgenis.do");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getTitle(), "xQTL workbench");
		Assert.assertTrue(selenium.isTextPresent("Welcome"));
		Assert.assertEquals(selenium.getText("link=R api"), "R api");
	}

	/**
	 * Login as admin and redirect
	 */
	@Test(dependsOnMethods =
	{ "startup" })
	public void login() throws InterruptedException
	{
		clickAndWait("id=UserLogin_tab_button");
		Assert.assertEquals(selenium.getText("link=Register"), "Register");
		selenium.type("id=username", "admin");
		selenium.type("id=password", "admin");
		clickAndWait("id=Login");
		// note: page now redirects to the Home screen ('auth_redirect' in
		// properties)
		Assert.assertTrue(selenium
				.isTextPresent("You are logged in as admin, and the database does not contain any investigations or other users."));
	}

	/**
	 * Press 'Load' example data
	 */
	@Test(dependsOnMethods =
	{ "login" })
	public void loadExampleData() throws InterruptedException
	{
		selenium.type("id=inputBox", storagePath());
		clickAndWait("id=loadExamples");
		Assert.assertTrue(selenium.isTextPresent("File path '" + storagePath()
				+ "' was validated and the dataloader succeeded"));
	}

	/**
	 * Function that sets the application to a 'default' state after setting up.
	 * All downstream test functions require this to be done.
	 */
	@Test(dependsOnMethods =
	{ "loadExampleData" })
	public void returnHome() throws InterruptedException
	{
		clickAndWait("id=ClusterDemo_tab_button");
	}

	/**
	 * Helper function. Click a target and wait.
	 */
	public void clickAndWait(String target)
	{
		selenium.click(target);
		selenium.waitForPageToLoad(pageLoadTimeout);
	}

	/**
	 * Helper function. Get DOM property using JavaScript.
	 */
	public String propertyScript(String element, String property)
	{
		return "var x = window.document.getElementById('" + element
				+ "'); window.document.defaultView.getComputedStyle(x,null).getPropertyValue('" + property + "');";
	}

	/**
	 * Helper function. Get the storage path to use in test.
	 */
	public String storagePath()
	{
		String storagePath = new File(".").getAbsolutePath() + File.separator + "tmp_selenium_test_data";
		if (DetectOS.getOS().startsWith("windows"))
		{
			return storagePath.replace("\\", "/");
		}
		else
		{
			return storagePath;
		}
	}

}
