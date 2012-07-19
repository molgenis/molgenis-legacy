package org.molgenis.animaldb.test;

import java.io.File;
import java.util.Calendar;

import org.molgenis.MolgenisOptions.MapperImplementation;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.jpa.JpaUtil;
import org.molgenis.util.DetectOS;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import plugins.emptydb.emptyDatabase;
import app.DatabaseFactory;
import app.FillMetadata;
import app.servlet.UsedMolgenisOptions;
import boot.Helper;
import boot.RunStandalone;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

public class AnimaldbSeleniumTest
{
	private final Integer TIME_OUT = 1000;
	private final String PAGE_LOAD_TIME_OUT = "60000";
	
	private Selenium selenium;
	
	
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
		//selenium.setSpeed("1000");
		selenium.start();
		selenium.setTimeout(PAGE_LOAD_TIME_OUT);
		
		if(new UsedMolgenisOptions().mapper_implementation == MapperImplementation.JPA) { 
			Database db = DatabaseFactory.create();
			JpaUtil.dropAndCreateTables(db, null);
			FillMetadata.fillMetadata(db, false);
		} else {
			// To be sure, empty db and don't add MolgenisUsers etc.
			new emptyDatabase(DatabaseFactory.create("apps/animaldb/org/molgenis/animaldb/animaldb.properties"), false);
		}
		if(!this.tomcat) new RunStandalone(webserverPort);
	}

	@Test
	public void startup() throws InterruptedException
	{
		selenium.open("/animaldb/molgenis.do");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
			
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
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Now we get to the Welcome screen
		Assert.assertEquals(selenium.getTitle(), "AnimalDB");
		Assert.assertTrue(selenium.isTextPresent("Welcome to AnimalDB!"));
		// Go to Import database plugin
		selenium.click("id=Admin_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("systemmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("LoadLegacy_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Import database"));
		// Since Ate hates waiting, first see if we are on his laptop ;)		
		selenium.type("id=zip","/home/paraiko/Projects/AnimalDB/prefill data/PrefillAnimalDB_2012-05-16.zip");
		selenium.click("id=source1");
		selenium.click("id=load");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);	
		// Then  try and see if we're on Roan's laptop
		if (!selenium.isTextPresent("Pre-filling AnimalDB successful")) {
			// If not, let's assume we're on the Hudson server
			selenium.type("id=zip", "/Users/roankanninga/Work/AnimalDB/PrefillAnimalDB_2012-05-16.zip");
			selenium.click("id=source1");
			selenium.click("id=load");
			selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		}
		if (!selenium.isTextPresent("Pre-filling AnimalDB successful")) {
			// If not, let's assume we're on the Hudson server
			//selenium.type("id=zip", "/data/home/erikroos/PrefillAnimalDB_2012-05-16.zip");
			selenium.type("id=zip", "/data/hudson/jobs/molgenis_animaldb/workspace/molgenis_apps/apps/animaldb/org/molgenis/animaldb/configurations/PrefillAnimalDB_default.zip");
			selenium.click("id=source1");
			selenium.click("id=load");
			selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		}
		if (!selenium.isTextPresent("Pre-filling AnimalDB successful")) {
			// If not, maybe we're on Joeri's Mac? :P
			selenium.type("id=zip", "/Users/joerivandervelde/Dropbox/GCC/AnimalDB/Data/legacy/PrefillAnimalDB_2012-05-16.zip");
			selenium.click("id=source1");
			selenium.click("id=load");
			selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		}
		
		sleepHelper("loginAdmin");
	}
	
	@Test (dependsOnMethods={"loginAdmin"})
	public void fileStorageSettings() throws Exception {
		selenium.click("id=Admin_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=systemmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=FileStorage_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("File storage property status:"));
		selenium.type("id=inputBox", "/tmp");
		selenium.click("id=filestorage_setpath");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Properties are set"));
		selenium.click("//input[@value='Validate']");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Validation status: VALIDATED"));
	}
	
	@Test(dependsOnMethods={"fileStorageSettings"})
	public void makeUser() throws InterruptedException
	{
		// Go to AnimalDB user mgmt. plugin (first item in Admin -> Security  menu)
		selenium.click("securitymenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Make user 'test'
		selenium.click("link=Make new user");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=firstname", "test");
		selenium.type("id=lastname", "test");
		selenium.type("id=email", "test@test.org");
		selenium.type("id=username", "test");
		selenium.type("id=password1", "test");
		selenium.type("id=password2", "test");
		selenium.type("id=newinv", "testInv");
		selenium.click("id=adduser");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("User test successfully added and assigned ownership of investigation testInv"));
		
		sleepHelper("makeUser");
	}
	
	@Test(dependsOnMethods={"makeUser"})
	public void logoutAdmin() throws InterruptedException
	{
		selenium.click("id=UserLogin_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=Logout");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		
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
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Now we get to the Welcome screen
		Assert.assertEquals(selenium.getTitle(), "AnimalDB");
		Assert.assertTrue(selenium.isTextPresent("Welcome to AnimalDB!"));
		
		sleepHelper("loginUser");
	}
	
	@Test(dependsOnMethods={"loginUser"})
	public void addAnimals() throws Exception {
		// Go to Add Animal plugin
		selenium.click("id=animalmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=AddAnimal_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Bring in animals"));
		// Add 10 female GMO House mice
		selenium.select("id=species", "label=House mouse");
		selenium.select("id=source", "label=Harlan");
		selenium.select("id=animaltype", "label=B. Transgeen dier");
		selenium.click("id=Cont1");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.select("id=background", "label=C57BL/6j");
		selenium.select("id=gene", "label=Cry1 KO");
		//selenium.select("id=gene", "label=Cry2 KO"); NOTE: Selenium does not support multiple select, only last click is remembered!
		selenium.click("id=Cont2");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.select("id=genestate_Cry1 KO", "label=+/-");
		//selenium.select("id=genestate_Cry2 KO", "label=+/-"); NOTE: Selenium does not support multiple select, only last click is remembered!
		selenium.click("id=Cont3");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=numberoffemales", "10");
		selenium.click("id=Save");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("10 animal(s) successfully added"));
		// Add 10 male non-GMO House mice
		selenium.select("id=species", "label=House mouse");
		selenium.select("id=source", "label=Harlan");
		selenium.select("id=animaltype", "label=A. Gewoon dier");
		selenium.click("id=Cont1");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.select("id=background", "label=C57BL/6j");
		selenium.click("id=Cont2");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=numberofmales", "10");
		selenium.click("id=Save");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("10 animal(s) successfully added"));
		
		sleepHelper("addAnimals");
	}
	
	@Test(dependsOnMethods={"addAnimals"})
	public void breedingWorkflow() throws Exception {
		// Go to Breeding line plugin
		selenium.click("id=Settings_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=ManageLines_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Breeding lines"));
		// Add a breeding line
		selenium.type("id=linename", "MyLine");
		selenium.select("id=species", "label=House mouse");
		selenium.click("id=add");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Line successfully added"));
		// Go to Breeding plugin
		selenium.click("id=animalmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		//selenium.click("id=Breeding_tab_button");
		selenium.click("id=Breeding_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Parentgroups"));
		// Add a parentgroup
		
		selenium.click("id=createParentgroup");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=numberPG", "3");
		
		selenium.click("id=selectt");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=showHideSettingsButton");
		Thread.sleep(1000);
		selenium.click("id=mothermatrix_removeFilter_2");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=mothermatrix_selected_0"); //select first mother
		selenium.click("id=motherB0");
		//Thread.sleep(1000);
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		//Thread.sleep(1000);
		
		selenium.click("id=mothermatrix_selected_1"); //select second mother
		selenium.click("id=motherB1");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=mothermatrix_selected_2"); //select third mother
		selenium.click("id=motherB2");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		//click on next page for mothers in the matrix
		selenium.click("id=mothermatrix_moveDownEnd");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);

		selenium.click("id=mothermatrix_selected_0"); //select first father
		selenium.click("id=fatherB0");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=mothermatrix_selected_1"); //select second father
		selenium.click("id=fatherB1");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=mothermatrix_selected_2"); //select third father
		selenium.click("id=fatherB2");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=from2to3");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("successfully added"));		
		
//		selenium.click("id=createParentgroup");
//		selenium.waitForPageToLoad(pageLoadTimeout);
//		// Screen 1: mothers
//		selenium.click("id=mothermatrix_removeFilter_3"); // remove filter on line (line is not set for new animals)
//		selenium.waitForPageToLoad(pageLoadTimeout);
//		selenium.click("id=mothermatrix_selected_0"); // toggle selectbox for first female in list
//		selenium.click("id=from2to3");
//		selenium.waitForPageToLoad(pageLoadTimeout);
//		// Screen 2: fathers
//		selenium.click("id=fathermatrix_removeFilter_3"); // remove filter on line (line is not set for new animals)
//		selenium.waitForPageToLoad(pageLoadTimeout);
//		selenium.click("id=fathermatrix_selected_0"); // toggle selectbox for first male in list
//		selenium.click("id=from3to4");
//		selenium.waitForPageToLoad(pageLoadTimeout);
//		// Screen 3: start date and remarks
//		selenium.click("id=addpg");
//		selenium.waitForPageToLoad(pageLoadTimeout);
//		Assert.assertTrue(selenium.isTextPresent("successfully added"));
		// Add a litter
		selenium.click("id=pgmatrix_selected_0");
		selenium.click("id=createlitter");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=littersize", "5");
		selenium.click("id=birthdate");
		selenium.type("id=birthdate", "2012-01-01");
		
		selenium.click("id=addlitter");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		//Assert.assertTrue(selenium.isTextPresent("successfully added"));
		// Wean litter
		selenium.click("id=littermatrix_selected_0");
		selenium.click("id=weanlitter");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=weandate");
		selenium.type("id=weandate", "2012-01-02");
		selenium.type("id=weansizefemale", "2");
		selenium.type("id=weansizemale", "3");
		selenium.click("id=wean");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("All 5 animals successfully weaned"));
		Assert.assertTrue(selenium.isTextPresent("LT_MyLine_000001"));
		// Check cage labels link
		selenium.click("id=littermatrix_selected_0");
		selenium.click("id=label");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Download cage labels as pdf"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Genotype litter
		// TODO: expand
		selenium.click("id=littermatrix_selected_0");
		selenium.click("id=genotypelitter");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=genodate");
		selenium.type("id=genodate", "2012-01-03");
		Assert.assertTrue(selenium.isTextPresent("Parentgroup: PG_MyLine_000001"));
		Assert.assertTrue(selenium.isTextPresent("Line: MyLine"));
		selenium.click("id=save");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("All 5 animals successfully genotyped"));
		// Check definitive cage labels link
		selenium.click("id=littermatrix_selected_0");
		selenium.click("id=label");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Download cage labels as pdf"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		
		sleepHelper("breedingWorkflow");
	}
	
	@Test(dependsOnMethods={"breedingWorkflow"})
	public void decWorkflow() throws Exception {
		
		Calendar calendar = Calendar.getInstance();
		//String[] months = new String[] {"January", "February", "March", "April", "May", "June",
		//								"July", "August", "September", "October", "November", "December"};
		// Go to DEC project plugin
		selenium.click("id=decmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=AddProject_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("DEC applications"));
		// Make a DEC project
		boolean keepTrying = true;
		int test = 0; //Check if we are on ate's laptop";
		// for now just assume we are running on hudson.
		String pdfFileName = "/data/hudson/jobs/molgenis_animaldb/workspace/molgenis_apps/apps/animaldb/org/molgenis/animaldb/configurations/PrefillAnimalDB_default.zip";
		//String pdfFileName = "/home/paraiko/Projects/AnimalDB/prefill data/PrefillAnimalDB_default.zip";
		
		selenium.click("id=add_decproject");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=dectitle", "MyDEC");
		selenium.type("id=decnumber", "12345");
		//pretend like these are PDFs...
		selenium.type("id=decapppdf", pdfFileName);
		selenium.type("id=decapprovalpdf", pdfFileName);
		int thisYear = calendar.get(Calendar.YEAR);
		selenium.type("id=startdate", thisYear + "-01-01");
		selenium.type("id=enddate", thisYear + "-12-31");
		selenium.type("id=decbudget", "20");
		selenium.click("id=addproject");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		
		Assert.assertTrue(selenium.isTextPresent("DEC project successfully added"));
		Assert.assertTrue(selenium.isTextPresent("MyDEC"));
			
		
		// Go to DEC subproject plugin
		selenium.click("id=AddSubproject_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("DEC subprojects"));
		// Make a DEC subproject
		selenium.click("id=add_subproject");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=experimenttitle", "MyProject");
		selenium.type("id=expnumber", "A");
		selenium.type("id=decapppdf", "/home/test/subapp.pdf");
		//int thisMonth = calendar.get(Calendar.MONTH);
		//int thisYear = calendar.get(Calendar.YEAR);
		selenium.type("id=startdate", thisYear + "-01-01");
		selenium.type("id=enddate", thisYear + "-02-01");
		selenium.type("id=decsubprojectbudget", "10");
		selenium.click("id=addsubproject");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("DEC subproject successfully added"));
		// Add animals to DEC
		selenium.click("id=manage_animals_in_subproject");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=startadd");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// toggle selectboxes for first five animals in list
		selenium.click("id=addanimalsmatrix_selected_0");
		selenium.click("id=addanimalsmatrix_selected_1");
		selenium.click("id=addanimalsmatrix_selected_2");
		selenium.click("id=addanimalsmatrix_selected_3");
		selenium.click("id=addanimalsmatrix_selected_4");
		selenium.type("id=subprojectadditiondate", thisYear + "-01-01");
		selenium.click("id=doadd");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Animal(s) successfully added"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Remove animals from DEC
		// toggle selectboxes for first two animals in list
		selenium.click("id=remanimalsmatrix_selected_0");
		selenium.click("id=remanimalsmatrix_selected_1");
		selenium.click("id=dorem"); // click Remove button
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=dorem"); // click Apply button
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Animal(s) successfully removed"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Check portal
		selenium.click("DecStatus_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("DEC status portal"));
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[1]/td[1]"), "12345");
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[2]/td[4]"), "A");
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[2]/td[7]"), "3");
		Assert.assertEquals(selenium.getText("//table[@id='StatusTable']/tbody/tr[2]/td[8]"), "2");
		
		sleepHelper("decWorkflow");
		
	}
	
	//@Test(dependsOnMethods={"decWorkflow"})
	@Test(dependsOnMethods={"breedingWorkflow"})
	public void locations() throws Exception {
		// Go to locations plugin to create two locations
		selenium.click("id=Settings_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=Locations_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Locations"));
		selenium.click("link=Make new location");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=locname", "Room 101");
		selenium.click("id=addloc");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Location successfully added"));
		selenium.click("link=Make new location");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=locname", "IVC");
		selenium.click("id=addloc");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Location successfully added"));
		// Go to animals in locations plugin
		selenium.click("id=animalmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=LocationPlugin_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Animals in locations"));
		// Add five animals to Room 101
		selenium.click("id=manage_loc_Room 101");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=add");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=animalsnotinlocmatrix_selected_5");
		selenium.click("id=animalsnotinlocmatrix_selected_6");
		selenium.click("id=animalsnotinlocmatrix_selected_7");
		selenium.click("id=animalsnotinlocmatrix_selected_8");
		selenium.click("id=animalsnotinlocmatrix_selected_9");
		selenium.click("id=applyadd");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Animals successfully added to Room 101. Now showing animals in that location."));
		// Move two to IVC
		selenium.click("id=animalsinlocmatrix_selected_0");
		selenium.click("id=animalsinlocmatrix_selected_1");
		selenium.click("id=move");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Animals successfully moved to IVC. Now switching to that location."));
		Assert.assertTrue(selenium.isTextPresent("Animals in IVC:"));
		selenium.click("link=Back to overview");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		
		sleepHelper("locations");
	}
	
	@Test(dependsOnMethods={"locations"})
	public void yearlyReports() throws Exception {
		// Go to Report plugin
		selenium.click("id=decmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=YearlyReportModule_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Report 4A (normal animals -> 10 males)
		selenium.select("id=form", "value=4A");
		selenium.click("id=generate");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertEquals(selenium.getTable("css=#reporttablediv > table.2.0"), "Muizen");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[2]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[3]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[4]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[5]/strong"), "10");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[6]/strong"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[7]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[8]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[9]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[10]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[11]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[12]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[13]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[14]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[15]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[16]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[17]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[18]"), "10");
		// Report 4B (GMO animals -> 10 females + 5 weaned - 2 removed)
		selenium.select("id=form", "value=4B");
		selenium.click("id=generate");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertEquals(selenium.getTable("css=#reporttablediv > table.2.0"), "Muizen");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[2]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[3]"), "5");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[4]"), "0");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[5]/strong"), "10");
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
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[18]"), "13");
		// Report 5
		selenium.select("id=form", "value=5");
		selenium.click("id=generate");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[1]"), "12345A - DEC 12345");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[6]"), "2");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[15]"), "A. Dood in het kader van de proef");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[16]"), "12345A");
		Assert.assertEquals(selenium.getText("//div[@id='reporttablediv']/table/tbody/tr[3]/td[17]"), "Mus musculus");
		
		sleepHelper("yearlyReports");
	}
	
	
	@Test(dependsOnMethods={"yearlyReports"})
	public void removeAnimals() throws Exception {
		// Remove 2 animals to test if removal by death via the remove animals plugin works.
		Calendar calendar = Calendar.getInstance();
		int thisYear = calendar.get(Calendar.YEAR);
		
		selenium.click("id=animalmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=RemAnimal_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=targetmatrix_selected_6");
		selenium.click("id=targetmatrix_selected_7");
		selenium.click("id=Select");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=deathdate", thisYear + "-02-02");
		selenium.click("id=remarks");
		selenium.type("id=remarks", "test");
		selenium.click("id=Apply");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);		
		
		Assert.assertTrue(selenium.isTextPresent("Animal(s) mm_000009 mm_000010 successfully removed"));
		
		sleepHelper("removeAnimals");
		
	}	
	
	/*
	@Test(dependsOnMethods={"removeAnimals"})
	public void applyProtocol() throws Exception {
		// First log in as admin to be able to do this
		selenium.click("id=UserLogin_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=Logout");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.type("id=username", "admin");
		selenium.type("id=password", "admin");
		selenium.click("id=Login");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Go to Protocol plugin
		selenium.click("id=Admin_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=systemmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=ApplyProtocol_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		// Apply 'SetWeight' protocol on animal 'mm_000001'
		selenium.select("id=Protocols", "label=SetWeight");
		selenium.click("id=targetmatrix_selected_0"); // toggle selectbox for first animal in matrix
		selenium.click("id=TimeBox");
		selenium.click("id=Select");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertEquals(selenium.getText("//div[@id='divValueTable']/table/thead/tr/th[2]"), "Weight");
		Assert.assertEquals(selenium.getText("//div[@id='divValueTable']/table/thead/tr/th[3]"), "Weight start");
		Assert.assertEquals(selenium.getText("//div[@id='divValueTable']/table/thead/tr/th[4]"), "Weight end");
		selenium.type("id=0_1_0", "239");
		selenium.click("id=ApplyStartTime_1");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=Apply");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		Assert.assertTrue(selenium.isTextPresent("Protocol applied successfully"));
		// Check in Timeline value viewer
		selenium.click("id=animalmenu_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=TimelineViewer_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		

		//add weight and select the first value from matrix
		selenium.click("id=targetmatrix_selected_0"); // toggle radio button for first animal in list
		selenium.click("id=showHideSettingsButton");
		selenium.click("css=input.default");	
		
		final String selector = "css=div#targetmatrix_measurementChooser_chzn.chzn-container ul.chzn-choices li.search-field input";
		selenium.type(selector, "Weigh");
		selenium.keyUp(selector, "t");	
		//keyDown("css=div#targetmatrix_measurementChooser_chzn.chzn-container ul.chzn-choices li.search-field input", "t");
		sleepHelper("applyProtocol");
		selenium.click("id=targetmatrix_measurementChooser_chzn_o_0");
		selenium.click("css=#divtargetmatrix > div");
		selenium.click("id=targetmatrix_updateColHeaderFilter");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
	

		Assert.assertTrue(selenium.isTextPresent("Weight"));
		Assert.assertTrue(selenium.isTextPresent("239"));
		sleepHelper("applyProtocol");
	} */
	
	
	
	@Test(dependsOnMethods={"removeAnimals"})
	public void logoutUser() throws InterruptedException
	{
		selenium.click("id=UserLogin_tab_button");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		selenium.click("id=Logout");
		selenium.waitForPageToLoad(PAGE_LOAD_TIME_OUT);
		
		sleepHelper("logout");
	}
	
	@AfterClass(alwaysRun=true)
	public void stop() throws Exception
	{
		selenium.stop();
		
		//added to fix TestDatabase which runs after this one...
		//see comment in TestDatabase!
		
		UsedMolgenisOptions usedMolgenisOptions = new UsedMolgenisOptions(); 
		if(usedMolgenisOptions.mapper_implementation == MapperImplementation.JPA) {
			Database db = DatabaseFactory.create();
			JpaUtil.dropAndCreateTables(db, null);
			FillMetadata.fillMetadata(db, false);
		} else {		
			new emptyDatabase(DatabaseFactory.create("apps/animaldb/org/molgenis/animaldb/animaldb.properties"), false);
		}
		//Helper.deleteStorage();
		//Helper.deleteDatabase();
	}
	
	//helper function to get a good storage path
	private String storagePath()
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
	
	private void sleepHelper(String who) throws InterruptedException
	{
		System.out.println(who + " done, now sleeping for " + TIME_OUT + " msec");
		Thread.sleep(TIME_OUT);
	}

}
