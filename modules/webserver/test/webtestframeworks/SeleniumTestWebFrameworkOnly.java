package test.webtestframeworks;

import java.io.IOException;
import java.net.ServerSocket;

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
		int seleniumPort = getAvailablePort(9080, 100);
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
	
	/**
	 * COPIED FROM Helper.java TO KEEP THE TEST STANDALONE
	 * @param initialPort
	 * @param range
	 * @return
	 * @throws IOException
	 */
	public static int getAvailablePort(int initialPort, int range)
			throws IOException {
		for (int port = initialPort; port < (initialPort + range); port++) {
			boolean portTaken = false;
			ServerSocket socket = null;
			try {
				socket = new ServerSocket(port);
			} catch (IOException e) {
				portTaken = true;
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
			if (!portTaken) {
				return port;
			}
		}
		throw new IOException(
				"All ports in the range "
						+ initialPort
						+ "-"
						+ (initialPort + range)
						+ " were unavailable. Select a different initial port or increase the scanning range.");
	}

}
