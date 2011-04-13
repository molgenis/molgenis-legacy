/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.settings;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.DetectOS;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class Settings<E extends Entity> extends PluginModel<E> {

	private static final long serialVersionUID = 4037475429590054858L;
	private SettingsModel model = new SettingsModel();
	public static String systemTableName = "XGAPsettings_090527PBDB00QCGEXP4G";
	//public static String systemTableName = "XGAPsettings_090527PBDB00QCGEXP4G";
	// joeri: first of all, this name is quite arbitrary :)
	// secondly, if you change it, also update the usage in the documentation (use Find)
	// thirdly, having this hacky 'off the grid' table might be very bad, so updates here are not useful
	public static String fileDirField = "filedirpath";
	public static String verifiedField = "verified";
	private static Pattern MsWindowsDrive = Pattern.compile("^([a-zA-Z]:\\\\)(.+)");

	public SettingsModel getModel() {
		return model;
	}

	public Settings(String name, ScreenModel<E> parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "Settings";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/system/settings/Settings.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {

		System.out.println("*** request:\n" + request.toString());

		System.out.println("*** handleRequest WRAPPER __action: "
				+ request.getString("__action"));
		this.handleRequest(db, request, null);
	}

	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		if (this.getLogin().isAuthenticated()){
			return true;
		} else {
			return false;
		}
	}
	
	private void resetModel(){
		model.setMkDirSuccess(null);
		model.setRwDirSuccess(null);
		model.setFolderExists(null);
		model.setFolderHasContent(null);
	}

	private void OsSpecificStuff(String value) throws Exception{
		Matcher m = MsWindowsDrive.matcher(value);
		if(m.matches()){
			//matched a drive letter + :\ combination, ie. C:\
			//so MS WINDOWS OS
			//do not add an additional seperator!
			System.out.println("m matches");
			System.out.println("DetectOS.getOS() = " + DetectOS.getOS());
			if(!DetectOS.getOS().startsWith("windows")){
				throw new Exception(
				"Drive designation used but your OS does not seem to be a Windows variant.");
			}
		}
		else{
			System.out.println("DetectOS.getOS() = " + DetectOS.getOS());
			if(DetectOS.getOS().startsWith("windows")){
//				WTF: MAC nerds Always throw an exception for windows				
//				throw new Exception(
//				"Drive designation not used (eg. 'C:&#92;data') but your OS seems to be a Windows variant.");
//	joeri: this exception is here is helpful, why comment it out? non-windows drive on a windows system..
				throw new Exception(
				"Drive designation not used (eg. 'C:&#92;data') but your OS seems to be a Windows variant.");
			}
			if(!value.startsWith(File.separator)){
				value = File.separator + value;
			}
		}
	}
	
	public void handleRequest(Database db, Tuple request, PrintWriter out) {
		if (request.getString("__action") != null) {

			System.out.println("*** handleRequest __action: "
					+ request.getString("__action"));

			try {

				if (request.getString("__action").equals("setFileDirPath")) {

					resetModel();
					System.out.println("resetModel");
					
					if (this.model.getHasSystemSettingsTable().equals("true")) {
						
						System.out.println("true");
						
						String value = request.getString("fileDirPath");
						
						if(value == null || value.equals("") || value.equals("null")){
							throw new Exception(
									"Empty path not allowed");
						}
						
						OsSpecificStuff(value);

						Object o = TableUtil.getFromTable(db, systemTableName,
								fileDirField);

						if (o != null) {
							throw new Exception(
									"Could not write field in system table: already present.");

						} else {
							boolean success = TableUtil.insertInTable(db,
									systemTableName, fileDirField, value);
							if (!success) {
								throw new Exception(
										"Could not write field in system table.");
							}
						}

					} else if (this.model.getHasSystemSettingsTable().equals("false")) {
						
						System.out.println("false");
						
						boolean success = TableUtil.addSystemSettingsTable(db,
								systemTableName, fileDirField);
						if (!success) {
							throw new Exception("Could not add system table.");
						}

						String value = request.getString("fileDirPath");
						
						if(value == null || value.equals("") || value.equals("null")){
							throw new Exception(
									"Empty path not allowed");
						}
						
						OsSpecificStuff(value);
						
						success = TableUtil.insertInTable(db, systemTableName,
								fileDirField, value);
						if (!success) {
							throw new Exception(
									"Could not write field in system table.");
						}

					} else {
						System.out.println("ERROR!");
					}

				} else if (request.getString("__action").equals("deleteFileDirPath")) {
					boolean success = TableUtil
							.removeTable(db, systemTableName);
					if (!success) {
						throw new Exception("Remove failed");
					}
					
					resetModel();
					
				} else if (request.getString("__action").equals("testDirLocValid")) {
					System.out.println("*** testDirLocValid");
					if (this.model.getHasSystemSettingsTable().equals("true")) {
						Object o = TableUtil.getFromTable(db, systemTableName,	fileDirField);
						if (o != null) {
							
//							File base = new File("");
//							int a = base.getAbsolutePath().split(File.separator).length - 2;
//							String goback = "";
//							for(int i = 0; i < a; i++){
//								goback+="/..";
//							}
//							//String path = "/Users/joerivandervelde";
//							String path = o.toString();
//							
//							String filePath = goback + path;
//							
//							File f = new File(filePath);
							
							String path = o.toString();
							
							File f = null;
							if(path.startsWith(File.separator)){
								f = new File(path);
							}else{
								f = new File(File.separator + path);
							}
							
							System.out.println("*** file ref " + f.getAbsolutePath());
							
							if(f.exists()){
								this.model.setMkDirSuccess("exists");
							}else{
							boolean success = f.mkdirs();
							this.model.setMkDirSuccess(success ? "success"
									: "fail");
							if (success) {
								this.model.setFileDir(f);
							}}
						}else{
							this.model.setMkDirSuccess("fail");
							throw new Exception("No field in system table");
						}
					}else{
						this.model.setMkDirSuccess("fail");
						throw new Exception("No system table");
					}

				} else if (request.getString("__action").equals(
						"testDirRwValid")) {
					System.out.println("*** testDirRwValid");
					if (this.model.getHasSystemSettingsTable().equals("true")) {
						Object o = TableUtil.getFromTable(db, systemTableName,
								fileDirField);
						if (o != null) {
							
//							File base = new File("");
//							int a = base.getAbsolutePath().split(File.separator).length - 2;
//							String goback = "";
//							for(int i = 0; i < a; i++){
//								goback+=File.separator+"..";
//							}
//							//String path = "/Users/joerivandervelde";
							String path = o.toString();
							
							File f = null;
							if(path.startsWith(File.separator)){
								f = new File(path);
							}else{
								f = new File(File.separator + path);
							}
							
							//String filePath = goback + File.separator + path;
							
							
							
							if (f.exists()) {
								File tmp = new File(f.getAbsolutePath()
										+ File.separator + "tmp.txt");
								boolean createSuccess = tmp.createNewFile();
								if(createSuccess){
									System.out.println("*** created " + tmp.getAbsolutePath());
								FileOutputStream fos = new FileOutputStream(tmp);
								DataOutputStream dos = new DataOutputStream(fos);
								dos.writeChars("test");
								dos.close();
								fos.close();
								
								boolean deleteSuccess = tmp.delete();
								if(deleteSuccess){
									this.model.setRwDirSuccess("success");
								}else{
									this.model.setRwDirSuccess("fail");
									throw new Exception("Could not delete file");
								}
								
								}else{
									this.model.setRwDirSuccess("fail");
									throw new Exception("Could not write to file");
								}
								
							} else {
								this.model.setRwDirSuccess("fail");
								throw new Exception("Path does not exist");
							}
						}else{
							this.model.setRwDirSuccess("fail");
							throw new Exception("No field in system table");
						}
					}else{
						this.model.setRwDirSuccess("fail");
						throw new Exception("No system table");
					}
				}

				this.setMessages();
			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e
						.getMessage() : "null", false));
			}
		}
	}
	
	private boolean folderExists(String path){
		File f = new File(path);
		if(f.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean folderHasContent(String path){
		File f = new File(path);
		if(f.exists()){
			if(f.listFiles().length == 0){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}

	public void clearMessage() {
		this.setMessages();
	}

	@Override
	public void reload(Database db) {

		try{
		this.model.setHasSystemSettingsTable(TableUtil.hasTable(db, systemTableName));
		
		this.model.setVerified(false);

		if (this.model.getHasSystemSettingsTable().equals("true")) {
			Object o = TableUtil
					.getFromTable(db, systemTableName, fileDirField);
			if (o != null) {
				String dir = o.toString();
				if (this.model.getKeyValsFromSettingsTable() == null) {
					HashMap<String, String> keyVals = new HashMap<String, String>();
					keyVals.put(fileDirField, dir);
					this.model.setKeyValsFromSettingsTable(keyVals);
				} else {
					this.model.getKeyValsFromSettingsTable().put(fileDirField,
							dir);
				}
				
				this.model.setFolderExists(folderExists(dir));
				if(this.model.getFolderExists()){
					this.model.setFolderHasContent(folderHasContent(dir));
				}
				
				if(model.getMkDirSuccess() != null && model.getRwDirSuccess() != null && (model.getMkDirSuccess().equals("success") || model.getMkDirSuccess().equals("exists")) && model.getRwDirSuccess().equals("success")){
					//TableUtil.insertInTable(db, systemTableName, "verified", "1");
					System.out.println("*** UPDATING!!");
					boolean success = TableUtil.updateInTable(db, systemTableName, verifiedField, "1", fileDirField+"='"+dir+"'");
					if (!success) {
						throw new Exception("Could not update system table.");
					}
				}
				
			} else {
				if (this.model.getKeyValsFromSettingsTable() == null) {
					HashMap<String, String> keyVals = new HashMap<String, String>();
					keyVals.put(fileDirField, "NULL");
					this.model.setKeyValsFromSettingsTable(keyVals);
				} else {
					this.model.getKeyValsFromSettingsTable().put(fileDirField,
							"NULL");
				}
			}
			
			o = TableUtil.getFromTable(db, systemTableName, verifiedField);
			
			if(o != null){
				//System.out.println(o + " and "+ o.toString());
				if(o.toString().equals("true")){
					this.model.setVerified(true);
				}
			}
			
		}
		
		
		
		}catch(Exception e){
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e
					.getMessage() : "null", false));
		}

	}

}
