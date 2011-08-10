package test;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

public class SeleniumTestWebFrameworkOnly
{
	
	Selenium selenium;
	String pageLoadTimeout = "30000";

	@BeforeClass
	public void start() throws Exception
	{
		String seleniumHost = "localhost";
		int seleniumPort = Helper.getAvailablePort(9080, 100);
		String seleniumBrowser = "firefox";
		String seleniumUrl = "http://www.google.com";

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
			throw new IllegalStateException("Can't start selenium server", e);
		}

		HttpCommandProcessor proc = new HttpCommandProcessor(seleniumHost, seleniumPort, seleniumBrowser, seleniumUrl);
		selenium = new DefaultSelenium(proc);
		selenium.start();
	}
	
	@Test
	public void google() throws InterruptedException
	{
		selenium.open("");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getTitle(), "Google");
	}
	
	@AfterClass
	public void stop() throws Exception
	{
		selenium.stop();
	}

}
