package test;

import java.util.Calendar;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import plugins.emptydb.emptyDatabase;
import app.DatabaseFactory;
import boot.RunStandalone;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

import core.Helper;

public class AnimaldbSeleniumTest
{
	Selenium selenium;
	Integer sleepTime = 1000;
	String pageLoadTimeout = "120000";
	boolean tomcat = false;
	//String storagePath = new File(".").getAbsolutePath() + File.separator + "tmp_selenium_test_data";

	@BeforeClass
	public void start() throws Exception
	{
		int webserverPort = 8080;
		if(!this.tomcat) webserverPort = Helper.getAvailablePort(11000, 10);
		
		String seleniumUrl = "http://localhost:" + webserverPort + "/";
		String seleniumHost = "localhost";
		String seleniumBrowser = "firefox";
		int seleniumPort = Helper.getAvailablePort(11010, 10);
	
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
		selenium.setTimeout(pageLoadTimeout);
		
		// To be sure, empty db and don't add MolgenisUsers etc.
		if(!this.tomcat) new emptyDatabase(DatabaseFactory.create("apps/animaldb/org/molgenis/animaldb/animaldb.properties"), false);
		else new emptyDatabase(DatabaseFactory.create("apps/animaldb/org/molgenis/animaldb/animaldb.properties"), false);
		if(!this.tomcat) new RunStandalone(webserverPort);
	}

	@Test
	public void startup() throws InterruptedException
	{
		selenium.open("/animaldb/molgenis.do");
		selenium.waitForPageToLoad(pageLoadTimeout);
			
		sleepHelper("startup");
	}

	@Test(dependsOnMethods={"startup"})
	public void loginAdmin() throws InterruptedException
	{
		// Login
		Assert.assertEquals(selenium.getText("link=Register"), "Register");
		selenium.type("id=username", "admin");
		selenium.type("id=password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Now we get to the Welcome screen
		Assert.assertEquals(selenium.getTitle(), "AnimalDB");
		Assert.assertTrue(selenium.isTextPresent("Welcome to AnimalDB!"));
		
		sleepHelper("loginAdmin");
	}
	
	@Test(dependsOnMethods={"loginAdmin"})
	public void makeUser() throws InterruptedException
	{
		// Go to AnimalDB user mgmt. plugin (first item in Admin menu)
		selenium.click("id=Admin_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Make user 'test'
		selenium.click("link=Make new user");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.type("id=firstname", "test");
		selenium.type("id=lastname", "test");
		selenium.type("id=email", "test@test.org");
		selenium.type("id=username", "test");
		selenium.type("id=password1", "test");
		selenium.type("id=password2", "test");
		selenium.type("id=newinv", "testInv");
		selenium.click("id=adduser");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("User test successfully added and assigned ownership of investigation testInv"));
		
		sleepHelper("makeUser");
	}
	
	@Test(dependsOnMethods={"makeUser"})
	public void logoutAdmin() throws InterruptedException
	{
		selenium.click("id=UserLogin_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=Logout");
		selenium.waitForPageToLoad(pageLoadTimeout);
		
		sleepHelper("logoutAdmin");
	}
	
	@Test(dependsOnMethods={"logoutAdmin"})
	public void loginUser() throws InterruptedException
	{
		// Login
		Assert.assertEquals(selenium.getText("link=Register"), "Register");
		selenium.type("id=username", "test");
		selenium.type("id=password", "test");
		selenium.click("id=Login");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Now we get to the Welcome screen
		Assert.assertEquals(selenium.getTitle(), "AnimalDB");
		Assert.assertTrue(selenium.isTextPresent("Welcome to AnimalDB!"));
		
		sleepHelper("loginUser");
	}
	
	@Test(dependsOnMethods={"loginUser"})
	public void addAnimals() throws Exception {
		// Go to Add Animal plugin
		selenium.click("id=animalmenu_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=AddAnimal_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Bring in animals"));
		// Add 10 female Syrian hamsters
		selenium.select("id=species", "label=Syrian hamster");
		selenium.select("id=background", "label=C57BL/6j"); // TODO: add useful Hamster background
		selenium.select("id=sex", "label=Female");
		selenium.select("id=source", "label=Harlan");
		selenium.select("id=animaltype", "label=A. Gewoon dier");
		selenium.type("id=numberofanimals", "10");
		selenium.click("id=Add");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("10 animal(s) successfully added"));
		// Add 10 male Syrian hamsters
		selenium.select("id=species", "label=Syrian hamster");
		selenium.select("id=background", "label=C57BL/6j");
		selenium.select("id=sex", "label=Male");
		selenium.select("id=source", "label=Harlan");
		selenium.select("id=animaltype", "label=A. Gewoon dier");
		selenium.type("id=numberofanimals", "10");
		selenium.click("id=Add");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("10 animal(s) successfully added"));
		
		sleepHelper("addAnimals");
	}
	
	@Test(dependsOnMethods={"addAnimals"})
	public void breedingWorkflow() throws Exception {
		// Go to Breeding line plugin
		selenium.click("id=breedingmodule_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=ManageLines_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Breeding lines"));
		// Add a breeding line
		selenium.type("id=linename", "MyLine");
		selenium.click("id=add");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Line successfully added"));
		// Go to Parentgroup plugin
		selenium.click("id=ManageParentgroups_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Parent groups"));
		// Add a parent group
		selenium.click("link=Create new parent group");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Screen 1: line
		selenium.click("id=from1to2");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Screen 2: mothers
		selenium.click("id=mothermatrix_removeFilter_3"); // remove filter on line (line is not set for new animals)
		// We have to wait here, but it's Ajax, so it's faster than a normal full page load
		// (however, 1 sec. is not (always) enough on Hudson, so set to 5 sec.)
		Thread.sleep(5000);
		selenium.click("id=mothermatrix_selected_0"); // toggle selectbox for first female in list
		selenium.click("id=from2to3");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Screen 3: fathers
		selenium.click("id=fathermatrix_removeFilter_3"); // remove filter on line (line is not set for new animals)
		// We have to wait here, but it's Ajax, so it's faster than a normal full page load
		// (however, 1 sec. is not (always) enough on Hudson, so set to 5 sec.)
		Thread.sleep(5000);
		selenium.click("id=fathermatrix_selected_0"); // toggle selectbox for first male in list
		selenium.click("id=from3to4");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Screen 4: start date and remarks
		selenium.click("id=addpg");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("successfully added"));
		// Go to Litter plugin
		selenium.click("ManageLitters_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Litters"));
		Assert.assertTrue(selenium.isTextPresent("Create new litter"));
		// Add a litter
		selenium.click("link=Create new litter");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=matrix_selected_0"); // toggle selectbox for first parent group in list
		selenium.type("id=littersize", "5");
		selenium.click("id=addlitter");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("successfully added"));
		// Wean litter
		selenium.click("link=Wean");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.type("id=weansizefemale", "2");
		selenium.type("id=weansizemale", "3");
		selenium.click("id=wean");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("All 5 animals successfully weaned"));
		Assert.assertTrue(selenium.isTextPresent("LT_MyLine_000001"));
		// Check cage labels link
		selenium.click("link=Create temporary cage labels");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Download temporary wean labels as pdf"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Genotype litter
		// TODO: expand
		selenium.click("link=Genotype");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Parentgroup: PG_MyLine_000001"));
		Assert.assertTrue(selenium.isTextPresent("Line: MyLine"));
		selenium.click("id=save");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("All 5 animals successfully genotyped"));
		// Check definitive cage labels link
		selenium.click("link=Show weaned and genotyped litters");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("LT_MyLine_000001"));
		selenium.click("link=Create definitive cage labels");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Download definitive cage labels as pdf"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(pageLoadTimeout);
		
		sleepHelper("breedingWorkflow");
	}
	
	@Test(dependsOnMethods={"breedingWorkflow"})
	public void decWorkflow() throws Exception {
		Calendar calendar = Calendar.getInstance();
		//String[] months = new String[] {"January", "February", "March", "April", "May", "June",
		//								"July", "August", "September", "October", "November", "December"};
		// Go to DEC project plugin
		selenium.click("id=decmenu_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=AddProject_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("DEC applications"));
		// Make a DEC project
		selenium.click("link=Add");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.type("id=dectitle", "MyDEC");
		selenium.type("id=decnumber", "12345");
		selenium.type("id=decapppdf", "/home/test/app.pdf");
		selenium.type("id=decapprovalpdf", "/home/test/app2.pdf");
		int thisYear = calendar.get(Calendar.YEAR);
		selenium.type("id=startdate", thisYear + "-01-01");
		selenium.type("id=enddate", thisYear + "-12-31");
		selenium.click("id=addproject");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("DEC Project successfully added"));
		Assert.assertTrue(selenium.isTextPresent("MyDEC"));
		// Go to DEC subproject plugin
		selenium.click("id=AddSubproject_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("DEC subprojects"));
		// Make a DEC subproject
		selenium.click("link=Add");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.type("id=experimenttitle", "MyProject");
		selenium.type("id=expnumber", "A");
		selenium.type("id=decapppdf", "/home/test/subapp.pdf");
		//int thisMonth = calendar.get(Calendar.MONTH);
		selenium.type("id=startdate", thisYear + "-01-01");
		selenium.type("id=enddate", thisYear + "-02-01");		
		selenium.click("id=addsubproject");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("DEC subproject successfully added"));
		// Add animals to DEC
		selenium.click("link=Manage");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=startadd");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// toggle selectboxes for first five animals in list
		selenium.click("id=addanimalsmatrix_selected_0");
		selenium.click("id=addanimalsmatrix_selected_1");
		selenium.click("id=addanimalsmatrix_selected_2");
		selenium.click("id=addanimalsmatrix_selected_3");
		selenium.click("id=addanimalsmatrix_selected_4");
		selenium.type("id=subprojectadditiondate", thisYear + "-01-01");
		selenium.click("id=doadd");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Animal(s) successfully added"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Remove animals from DEC
		// toggle selectboxes for first two animals in list
		selenium.click("id=remanimalsmatrix_selected_0");
		selenium.click("id=remanimalsmatrix_selected_1");
		selenium.click("id=dorem"); // click Remove button
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=dorem"); // click Apply button
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Animal(s) successfully removed"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Check portal
		selenium.click("DecStatus_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("DEC status portal"));
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[1]/th"), "12345");
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[2]/td[3]"), "A");
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[2]/td[6]"), "3");
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[2]/td[7]"), "2");
		
		sleepHelper("decWorkflow");
	}
	
	@Test(dependsOnMethods={"decWorkflow"})
	public void yearlyReports() throws Exception {
		// Go to Report plugin
		selenium.click("id=YearlyReportModule_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Report 4A (default)
		selenium.click("id=generate");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getTable("css=#reporttablediv > table.2.0"), "Hamsters");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[2]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[3]"), "5");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[4]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[5]/strong"), "20");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[6]/strong"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[7]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[8]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[9]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[10]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[11]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[12]"), "2");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[13]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[14]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[15]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[16]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[17]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[18]"), "23");
		// Report 5
		selenium.select("id=form", "value=5");
		selenium.click("id=generate");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[1]"), "12345A - DEC 12345");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[6]"), "2");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[15]"), "A. Dood in het kader van de proef");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[16]"), "12345A");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[17]"), "Mesocricetus auratus");
		
		sleepHelper("yearlyReports");
	}
	
	@Test(dependsOnMethods={"yearlyReports"})
	public void applyProtocol() throws Exception {
		// Go to Protocol plugin
		selenium.click("id=animalmenu_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=valuemenu_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=ApplyProtocol_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		// Apply 'SetWeight' protocol on animal '000001'
		selenium.select("id=Protocols", "label=SetWeight");
		selenium.click("id=targetmatrix_selected_0"); // toggle selectbox for first animal in matrix
		selenium.click("id=TimeBox");
		selenium.click("id=Select");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertEquals(selenium.getText("//div[@id='divValueTable']/table/thead/tr/th[2]"), "Weight");
		Assert.assertEquals(selenium.getText("//div[@id='divValueTable']/table/thead/tr/th[3]"), "Weight start");
		Assert.assertEquals(selenium.getText("//div[@id='divValueTable']/table/thead/tr/th[4]"), "Weight end");
		selenium.type("id=0_1_0", "200");
		selenium.click("id=ApplyStartTime_1");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=Apply");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Protocol applied successfully"));
		// Check in Timeline value viewer
		selenium.click("id=EventViewer_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=targetmatrix_selected_0"); // toggle radio button for first animal in list
		selenium.click("id=select");
		selenium.waitForPageToLoad(pageLoadTimeout);
		Assert.assertTrue(selenium.isTextPresent("Weight"));
		Assert.assertTrue(selenium.isTextPresent("200"));
		
		sleepHelper("applyProtocol");
	}
	
	@Test(dependsOnMethods={"applyProtocol"})
	public void logoutUser() throws InterruptedException
	{
		selenium.click("id=UserLogin_tab_button");
		selenium.waitForPageToLoad(pageLoadTimeout);
		selenium.click("id=Logout");
		selenium.waitForPageToLoad(pageLoadTimeout);
		
		sleepHelper("logout");
	}
	
	@AfterClass(alwaysRun=true)
	public void stop() throws Exception
	{
		selenium.stop();
		
		//added to fix TestDatabase which runs after this one...
		//see comment in TestDatabase!
		if (!this.tomcat) {
			new emptyDatabase(DatabaseFactory.create("apps/animaldb/org/molgenis/animaldb/animaldb.properties"), false);
		} else {
			new emptyDatabase(DatabaseFactory.create("apps/animaldb/org/molgenis/animaldb/animaldb.properties"), false);
		}

		//Helper.deleteStorage();
		//Helper.deleteDatabase();
	}
	
	private void sleepHelper(String who) throws InterruptedException
	{
		System.out.println(who + " done, now sleeping for " + sleepTime + " msec");
		Thread.sleep(sleepTime);
	}

}
