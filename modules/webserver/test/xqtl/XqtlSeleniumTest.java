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

public class XqtlSeleniumTest
{
	
	Selenium selenium;
	Integer sleepTime = 100;
	String pageLoadTimeout = "30000";
	boolean tomcat = false;
	String appName;

	@BeforeClass
	public void start() throws Exception
	{
		appName = MolgenisServlet.getMolgenisVariantID();
		int webserverPort = 8080;
		if(!tomcat) webserverPort = Helper.getAvailablePort(11000, 100);
		
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
		
		if(!tomcat) new RunStandalone(webserverPort);
	}

	@Test
	public void startup() throws InterruptedException
	{
		selenium.open("/"+appName+"/molgenis.do");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getTitle(), "xQTL workbench");
		Assert.assertTrue(selenium.isTextPresent("Welcome"));
		Assert.assertEquals(selenium.getText("link=R api"), "R api");
		sleepHelper("startup");
	}

	@Test(dependsOnMethods={"startup"})
	public void login() throws InterruptedException
	{
		selenium.click("id=UserLogin_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("link=Register"), "Register");
		selenium.type("id=username", "admin");
		selenium.type("id=password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad(pageLoadTimeout);
		//note: page now redirects to the Home screen ('auth_redirect' in properties)
		Assert.assertTrue(selenium.isTextPresent("You are logged in as admin, and the database does not contain any investigations or other users."));
		sleepHelper("login");
	}

	@Test(dependsOnMethods={"login"})
	public void loadExampleData() throws InterruptedException
	{
		selenium.type("id=inputBox", storagePath());
		sleepHelper("loadExampleData page loaded, now pressing button to load users, data, permissions etc");
		selenium.click("id=loadExamples");
		selenium.waitForPageToLoad("60000");
		Assert.assertTrue(selenium.isTextPresent("File path '"+storagePath()+"' was validated and the dataloader succeeded"));
		sleepHelper("loadExampleData");
	}

	@Test(dependsOnMethods={"loadExampleData"})
	public void exploreExampleData() throws InterruptedException
	{
		//selenium.click("//div[@onclick=\"document.forms.main.__target.value='main';document.forms.main.select.value='Investigations';document.forms.main.submit();\"]");
		selenium.click("id=Investigations_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("link=Marker (117)"), "Marker (117)");
		selenium.click("link=metaboliteexpression");
		selenium.waitForPageToLoad(pageLoadTimeout);
		//Assert.assertEquals(selenium.getText("//div[@id='Datas_screen']/div[2]/form/div/div[2]/div/table/tbody/tr[3]/td/table/tbody/tr[3]/td[1]"),"942");
		Assert.assertTrue(selenium.isTextPresent("942") && selenium.isTextPresent("4857") && selenium.isTextPresent("20716"));
		sleepHelper("exploreExampleData");
	}
	
	@Test(dependsOnMethods={"exploreExampleData"})
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
		
		//assert if the hide investigation and data table rows are exposed
		Assert.assertEquals(selenium.getEval(propertyScript("Investigations_collapse_tr_id", "display")), "table-row");
		Assert.assertEquals(selenium.getEval(propertyScript("Datas_collapse_tr_id", "display")), "table-row");
		
		//click both unhide buttons
		selenium.click("id=Investigations_collapse_button_id");
		selenium.click("id=Datas_collapse_button_id");
		
		//assert if the hide investigation and data table rows are hidden again
		Assert.assertEquals(selenium.getEval(propertyScript("Investigations_collapse_tr_id", "display")), "none");
		Assert.assertEquals(selenium.getEval(propertyScript("Datas_collapse_tr_id", "display")), "none");
		
		sleepHelper("compactView");
	
	}
	
	@Test(dependsOnMethods={"compactView"})
	public void individualForms() throws Exception {
		//browse to basic annotations, individuals is the first form
		selenium.click("id=BasicAnnotations_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		
		//check some values here
		Assert.assertEquals(selenium.getText("//form[@id='Individuals_form']/div[3]/div/div/table/tbody/tr[7]/td[4]"), "X7");
		Assert.assertTrue(selenium.isTextPresent("xgap_rqtl_straintype_riself"));
		
		//switch to edit view and check some values there too
		selenium.click("id=Individuals_editview");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getValue("id=Individual_name"), "X1");
		Assert.assertEquals(selenium.getText("css=#Individual_ontologyReference_chzn > a.chzn-single > span"), "xgap_rqtl_straintype_riself");
		Assert.assertEquals(selenium.getText("//img[@onclick=\"if ($('#Individuals_form').valid() && validateForm(document.forms.Individuals_form,new Array())) {setInput('Individuals_form','_self','','Individuals','update','iframe'); document.forms.Individuals_form.submit();}\"]"), "");

		//click add new, wait for popup, and select it
		selenium.click("id=Individuals_edit_new");
		selenium.waitForPopUp("molgenis_edit_new", "30000");
		selenium.selectWindow("name=molgenis_edit_new");
		
		//fill in the form and click add
		selenium.type("id=Individual_name", "testindv");
		selenium.click("id=Add");
		selenium.waitForPageToLoad("30000");
		
		//select main window and check if add was successful
		selenium.selectWindow("title=xQTL workbench");
		Assert.assertTrue(selenium.isTextPresent("ADD SUCCESS: affected 1"));
		
		//page back and forth and check values
		selenium.click("id=prev_Individuals");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getValue("id=Individual_name"), "X193");
		selenium.click("id=last_Individuals");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getValue("id=Individual_name"), "testindv");
		
		//delete the test individual and check if it happened
		selenium.click("id=delete_Individuals");
		Assert.assertEquals(selenium.getConfirmation(), "You are about to delete a record. If you click [yes] you won't be able to undo this operation.");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("REMOVE SUCCESS: affected 1"));

		sleepHelper("individualForms");
	}
	
	@Test(dependsOnMethods={"individualForms"})
	public void enumInput() throws Exception {
		//browse to 'Data' view and expand compact view
		selenium.open("/"+appName+"/molgenis.do?__target=InvestigationMenu&select=Datas");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=Investigations_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=Datas_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=Datas_collapse_button_id");
		
		//assert content of enum fields
		Assert.assertEquals(selenium.getTable("css=#Datas_form > div.screenbody > div.screenpadding > table > tbody > tr > td > table.8.1"), "Individual\n\nChromosomeClassicalPhenotypeDerivedTraitEnvironmentalFactorGeneIndividualMarkerMassPeakMetabolitePanelProbeSampleSpot");
		Assert.assertEquals(selenium.getTable("css=#Datas_form > div.screenbody > div.screenpadding > table > tbody > tr > td > table.9.1"), "Metabolite\n\nChromosomeClassicalPhenotypeDerivedTraitEnvironmentalFactorGeneIndividualMarkerMassPeakMetabolitePanelProbeSampleSpot");
		
		//change Individual to Gene and save
		selenium.click("css=#Data_FeatureType_chzn > a.chzn-single > span");
		selenium.click("id=Data_FeatureType_chzn_o_4");
		selenium.click("id=save_Datas");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("UPDATE SUCCESS: affected 1"));
		
		//expand compact view again and check value has changed
		selenium.click("id=Datas_collapse_button_id");
		Assert.assertEquals(selenium.getTable("css=#Datas_form > div.screenbody > div.screenpadding > table > tbody > tr > td > table.8.1"), "Gene\n\nChromosomeClassicalPhenotypeDerivedTraitEnvironmentalFactorGeneIndividualMarkerMassPeakMetabolitePanelProbeSampleSpot");
		
		//change back to Individual and save
		selenium.click("css=#Data_FeatureType_chzn > a.chzn-single > div > b");
		selenium.click("id=Data_FeatureType_chzn_o_5");
		selenium.click("id=save_Datas");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("UPDATE SUCCESS: affected 1"));
		
		//expand compact view again and check value is back to normal again
		selenium.click("id=Datas_collapse_button_id");
		Assert.assertEquals(selenium.getTable("css=#Datas_form > div.screenbody > div.screenpadding > table > tbody > tr > td > table.8.1"), "Individual\n\nChromosomeClassicalPhenotypeDerivedTraitEnvironmentalFactorGeneIndividualMarkerMassPeakMetabolitePanelProbeSampleSpot");

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
	
	private String storagePath(){
		String storagePath = new File(".").getAbsolutePath() + File.separator + "tmp_selenium_test_data";
		if(DetectOS.getOS().startsWith("windows")){
			return storagePath.replace("\\", "/");
		}else{
			return storagePath;
		}
	}
	
//	private void printAllWindows(String what)
//	{
//		System.out.println("printAllWindows - "+ what);
//		for(String s : selenium.getAllWindowNames()){
//			System.out.println("window name = "+s);
//		}
//		for(String s : selenium.getAllWindowIds()){
//			System.out.println("window id = "+s);
//		}
//		for(String s : selenium.getAllWindowTitles()){
//			System.out.println("window title = "+s);
//		}
//	}

}
