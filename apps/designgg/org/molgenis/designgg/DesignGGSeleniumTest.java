package org.molgenis.designgg;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import app.servlet.MolgenisServlet;
import app.servlet.UsedMolgenisOptions;
import boot.Helper;
import boot.RunStandalone;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

/**
 * 
 * The complete xQTL Selenium web test.
 * Has a section for init/helpers and section for the real tests.
 * 
 */
public class DesignGGSeleniumTest
{
	
	/**
	 *******************************************************************
	 *************************  Init and helpers  **********************
	 *******************************************************************
	 */

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
		appName = new UsedMolgenisOptions().appName;
		int webserverPort = 8080;
		if (!tomcat) webserverPort = Helper.getAvailablePort(11020, 10);

		String seleniumUrl = "http://localhost:" + webserverPort + "/";
		String seleniumHost = "localhost";
		String seleniumBrowser = "firefox";
		int seleniumPort = Helper.getAvailablePort(11030, 10);

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

		if (!tomcat) new RunStandalone(webserverPort);
	}

	/**
	 * Stop Selenium server and remove files
	 */
	@AfterClass
	public void stop() throws Exception
	{
		selenium.stop();
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
	 * Start the app and verify home page
	 */
	@Test
	public void homepage() throws InterruptedException
	{
		selenium.open("/" + appName + "/molgenis.do");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.getTitle().equals("Genetical Genomics Experiment Designer"));
		Assert.assertTrue(selenium.isTextPresent("Define analysis platform"));
		Assert.assertTrue(selenium.isTextPresent("Define individual genotypes"));
		Assert.assertTrue(selenium.isTextPresent("Define experimental factors"));
		Assert.assertTrue(selenium.isTextPresent("Set constraints"));
	}
	
	/**
	 * Login as admin and redirect
	 */
	@Test(dependsOnMethods =
	{ "homepage" })
	public void pressTest() throws InterruptedException
	{
		clickAndWait("name=test");
		Assert.assertTrue(selenium.isTextPresent("Required parameter missing: You must provide a valid file with genotype data."));
		
		// Unfortunately, uploading files with Selenium is very complicated, so we cannot test more than this!
		// Maybe create a hidden action that runs a test with a predefined file instead?
	}


}
