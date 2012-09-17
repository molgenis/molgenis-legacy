/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.cluster.demo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.auth.MolgenisPermission;
import org.molgenis.auth.MolgenisRoleGroupLink;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.RuntimeProperty;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.render.LinkoutRenderDecorator;
import org.molgenis.framework.ui.html.render.RenderDecorator;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

import regressiontest.cluster.DataLoader;
import filehandling.storage.StorageHandler;



public class ClusterDemo extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5307970595544892186L;

	public ClusterDemo(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public static final String XQTL_HOMESCREEN_HIDELOGINBUTTONS = "xqtl_homescreen_hideloginbuttons";
	
	private boolean userIsAdminAndDatabaseIsEmpty;
	private String validpath;
	private boolean loggedIn;
	private RenderDecorator linkouter;
	private StorageHandler sh;
	private Boolean hideLoginButtons;
	
	public RenderDecorator getLinkouter()
	{
		return linkouter;
	}

	public boolean isLoggedIn()
	{
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn)
	{
		this.loggedIn = loggedIn;
	}

	public String getValidpath()
	{
		return validpath;
	}
	
	public Boolean getHideLoginButtons() {
		return hideLoginButtons;
	}

	public void setValidpath(String validpath)
	{
		this.validpath = validpath;
	}

	public boolean isUserIsAdminAndDatabaseIsEmpty()
	{
		return userIsAdminAndDatabaseIsEmpty;
	}

	public void setUserIsAdminAndDatabaseIsEmpty(boolean userIsAdminAndDatabaseIsEmpty)
	{
		this.userIsAdminAndDatabaseIsEmpty = userIsAdminAndDatabaseIsEmpty;
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_ClusterDemo";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/cluster/demo/ClusterDemo.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
		String action = request.getString("__action");
//		
		if(action.equals("setPathAndLoad")){
			setupStorageAndLoadExample(db, request.getString("fileDirPath"));
		}
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
	}
	
	/**
	 * Set a file path, validate, and load example data. This function
	 * should only be used when trying to quickly populate an empty database.
	 * 
	 * There are three scenarios: 1) There already is a validated path. Continue
	 * to load the examples. 2) There is a path, but is has not been validated.
	 * Discard the existing one, and validate the user input instead. Load
	 * example if succesful. 3) There is no path. Validate user input and continue
	 * if succesful.
	 * @param db
	 * @param path
	 */
	public void setupStorageAndLoadExample(Database db, String path){
		try
		{
			
			// first, lets add the example users if admin/anynomous are the only users
			// unrelated to the rest of the function
			if(db.find(MolgenisUser.class).size() == 2){
				addExampleUsers(db);
				giveExtraNeededPermissions(db);
			}
			
			// case 1
			if(sh.hasValidFileStorage(db))
			{
				// don't do anything extra, but prevents from going
				// into case 2 which also applies to valid paths..
				// (as well as unvalid ones!)
				
				//however - request.getString("fileDirPath") is now null
				//so set string here for nice output
				path = sh.getFileStorage(true, db).getAbsolutePath();
			}
			//case 2 (not a validated path: just delete and use input)
			else if(sh.hasFileStorage(false, db))
			{
				sh.deleteFileStorage(db);
				sh.setFileStorage(path, db);
				sh.validateFileStorage(db);
			}else{
				sh.setFileStorage(path, db);
				sh.validateFileStorage(db);
			}

			//if the case 2 or 3 path proves valid, continue to load data
			if(sh.hasValidFileStorage(db)){
				
				//run example data loader
				ArrayList<String> result = DataLoader.load(db, false);
				
				if(result.get(result.size()-2).equals("Complete success")){
					
					//query the data to find out if it is really there
					Data metab = db.find(Data.class, new QueryRule("name", Operator.EQUALS, "metaboliteexpression")).get(0);
					DataMatrixHandler dmh = new DataMatrixHandler(db);
					DataMatrixInstance instance = dmh.createInstance(metab, db);
					double element = (Double) instance.getSubMatrixByOffset(1, 1, 1, 1).getElement(0, 0);
					
					if(element == 4.0){
						//example data verified!
						this.setMessages(new ScreenMessage("File path '"+path+"' was validated and the dataloader succeeded", true));
					}else{
						throw new Exception("File path '"+path+"' was validated and the dataloader succeeded, but data query failed");
					}
					
				}else{
					//dataloader did not report complete success
					throw new Exception("File path '"+path+"' was validated but the data loader failed. Try using Settings -> Database for more detail");
				}
				
			}else{
				//only reachable in case 2 or case 3, when the new path was not valid
				throw new Exception("File path '"+path+"' could not be validated. Try using Settings -> File storage for more detail");
			}

	
		}
			catch(Exception e)
			{
				this.setMessages(new ScreenMessage(e.getMessage(), false));
			}
	}
	
	
	/**
	 * Give permissions on datatypes which are not covered by the gui xml to the group 'biologist'.
	 * By default, when setting a group="biologist" on a form, permissions on that form and its
	 * entity are automatically added by the molgenis parser. But we can't reach them all.
	 * @throws DatabaseException 
	 * 
	 * TODO: make more fine grained for biologist vs bioinformatician
	 * 
	 */
	public static void giveExtraNeededPermissions(Database db) throws DatabaseException{
		String[] entities = new String[]{
				
				//needed to view/add files
				"org.molgenis.core.MolgenisFile",
				
				//needed to view/add matrices
				"org.molgenis.data.BinaryDataMatrix",
				"org.molgenis.data.CSVDataMatrix",
				"org.molgenis.data.DecimalDataElement",
				"org.molgenis.data.TextDataElement",
				"org.molgenis.protocol.ProtocolApplication_Performer",
				
				//needed to add most data and view investigation overview
				"org.molgenis.pheno.ObservationElement",
				
				//needed to be able to start jobs
				"org.molgenis.cluster.Analysis",
				
				//needed to view/delete job status
				"org.molgenis.cluster.Job",
				"org.molgenis.cluster.Subjob",
				"org.molgenis.cluster.DataName",
				"org.molgenis.cluster.DataSet",
				"org.molgenis.cluster.DataValue",
				"org.molgenis.cluster.ParameterName",
				"org.molgenis.cluster.ParameterSet",
				"org.molgenis.cluster.ParameterValue",
				"org.molgenis.cluster.SelectedData",
				"org.molgenis.cluster.SelectedParameter"
				};
		
		for(String e : entities){
			MolgenisPermission mp = new MolgenisPermission();
			mp.setEntity_ClassName(e);
			mp.setRole_Name("biologist");
			mp.setPermission("write"); //might fail? need Selenium test!!
			db.add(mp);
		}
		
		//let biologist read RuntimeProperty, for file storage location and other app settings
		MolgenisPermission mp = new MolgenisPermission();
		mp.setEntity_ClassName("org.molgenis.core.RuntimeProperty");
		mp.setRole_Name("biologist");
		mp.setPermission("read");
		db.add(mp);
		
		//let anonymous read RuntimeProperty, for file storage location and other app settings
		MolgenisPermission mp2 = new MolgenisPermission();
		mp2.setEntity_ClassName("org.molgenis.core.RuntimeProperty");
		mp2.setRole_Name("anonymous");
		mp2.setPermission("read");
		db.add(mp2);
		
	}
	
	/**
	 * Adds example users. Depends on having roles 'biologist' and 'bioinformatician' in the GUI xml.
	 * @param db
	 * @throws DatabaseException
	 * @throws SQLException 
	 */
	public static void addExampleUsers(Database db) throws DatabaseException, SQLException{
		MolgenisUser bioUser = new MolgenisUser();
		bioUser.setName("bio-user");
		bioUser.setPassword("bio");
		bioUser.setFirstName("bio_firstname");
		bioUser.setLastName("bio_lastname");
		bioUser.setEmail("bio_email");
		bioUser.setActive(true);
		
		MolgenisUser bioInfoUser = new MolgenisUser();
		bioInfoUser.setName("bioinfo-user");
		bioInfoUser.setPassword("bioinfo");
		bioInfoUser.setFirstName("bioinfo_firstname");
		bioInfoUser.setLastName("bioinfo_lastname");
		bioInfoUser.setEmail("bioinfo_email");
		bioInfoUser.setActive(true);
		
		db.add(bioUser);
		db.add(bioInfoUser);
		
		MolgenisRoleGroupLink bioLink = new MolgenisRoleGroupLink();
		bioLink.setGroup_Name("biologist");
		bioLink.setRole(bioUser);
		
		MolgenisRoleGroupLink bioInfoLink = new MolgenisRoleGroupLink();
		bioInfoLink.setGroup_Name("bioinformatician");
		bioInfoLink.setRole(bioInfoUser);
		
		MolgenisRoleGroupLink makeBioinfoPartOfBio = new MolgenisRoleGroupLink();
		makeBioinfoPartOfBio.setGroup_Name("biologist");
		makeBioinfoPartOfBio.setRole_Name("bioinformatician");
		
		db.add(bioLink);
		db.add(bioInfoLink);
		db.add(makeBioinfoPartOfBio);
		
	}
	
	private void queryHideLoginSetting(Database db)
	{
		
		try
		{
			List<RuntimeProperty> rp = db.find(RuntimeProperty.class, new QueryRule(RuntimeProperty.NAME, Operator.EQUALS, XQTL_HOMESCREEN_HIDELOGINBUTTONS));
			
			if(rp.size() == 1 && rp.get(0).getValue().equals("false"))
			{
				this.hideLoginButtons = false;
			}
			else if(rp.size() == 1 && rp.get(0).getValue().equals("true"))
			{
				this.hideLoginButtons = true;
			}
			else
			{
				this.hideLoginButtons = false;
			}
		}
		catch(DatabaseException e)
		{
			this.setMessages(new ScreenMessage("Could not query runtime propery: " + e.getMessage(), false));
		}
	}
	

	@Override
	public void reload(Database db)
	{
		if(linkouter == null)
		{
			linkouter = new LinkoutRenderDecorator();
		}
		
		queryHideLoginSetting(db);

		sh = new StorageHandler(db);
				
		//HtmlSettings.uiToolkit = UiToolkit.ORIGINAL;
		
		if(this.getLogin().isAuthenticated()){
			this.setLoggedIn(true);
		}else{
			this.setLoggedIn(false);
		}
		
		if(this.getLogin() instanceof DatabaseLogin)
	{
		try
		{
			//fails when there is no table 'MolgenisUser', or no MolgenisUser named 'admin'
			//assume database has not been setup yet
			db.find(MolgenisUser.class, new QueryRule("name", Operator.EQUALS, "admin")).get(0);
		}
		catch(Exception e)
		{
			//setup database and report back
			String report = ResetXgapDb.reset(this.getDatabase(), true);
			if(report.endsWith("SUCCESS")){
				this.setMessages(new ScreenMessage("Database setup success!", true));
			}else{
				this.setMessages(new ScreenMessage("Database setup fail! Review report: "+report, false));
			}
		}
		
		try
		{
			//show special dataloader box for admin when the database has no investigations
			if(this.getLogin().getUserName().equals("admin")){
				List<Investigation> invList = db.find(Investigation.class);
				if(invList.size() == 0){
					
					//flip bool to enable box
					setUserIsAdminAndDatabaseIsEmpty(true);
					
					// since we're now showing the special box,
					// find out if there is a validated path and save this info
					if(sh.hasValidFileStorage(db)){
						this.setValidpath(sh.getFileStorage(true, db).getAbsolutePath());
					}else{
						this.setValidpath(null);
					}
					
				}else{
					setUserIsAdminAndDatabaseIsEmpty(false);
				}
			}else{
				setUserIsAdminAndDatabaseIsEmpty(false);
			}
		}catch(Exception e)
		{
			//something went wrong, set boolean to false for safety
			setUserIsAdminAndDatabaseIsEmpty(false);
		}
		
	}
		else
		{
			// for simplelogin, just check if there are investigations present
			try
			{
				List<Investigation> invList = db.find(Investigation.class);
				if(invList.size() == 0){
					
					//flip bool to enable box
					setUserIsAdminAndDatabaseIsEmpty(true);
					
					// since we're now showing the special box,
					// find out if there is a validated path and save this info
					if(sh.hasValidFileStorage(db)){
						this.setValidpath(sh.getFileStorage(true, db).getAbsolutePath());
					}else{
						this.setValidpath(null);
					}
				}else{
					setUserIsAdminAndDatabaseIsEmpty(false);
				}
			
			}catch(Exception e)
			{
				//something went wrong, set boolean to false for safety
				setUserIsAdminAndDatabaseIsEmpty(false);
			}
			
			
		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}
