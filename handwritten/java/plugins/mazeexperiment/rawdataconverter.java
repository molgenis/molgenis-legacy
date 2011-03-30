/* Date:        October 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.mazeexperiment;



import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;



public class rawdataconverter extends PluginModel<Entity>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1594418382140333547L;
	private int success;
	private String binval;
	private String dbval;
	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}
	
	public String getBinval() {
		return binval;
	}

	public void setBinval(String binval) {
		this.binval = binval;
	}
	
	public String getDbval() {
		return dbval;
	}

	public void setDbval(String dbval) {
		this.dbval = dbval;
	}

	public rawdataconverter(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_animaldb_mazeexperiment_rawdataconverter";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/animaldb/mazeexperiment/rawdataconverter.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getString("__action");
		
		if( action.equals("binconv") )
		{
			try {
				int i = request.getInt("intval");
				//String binstr = Integer.toBinaryString(i);
				
				String binstr = Integer.toBinaryString(256 + i);
				binstr = binstr.substring(binstr.length() -8);
				
				String binstr2 = Integer.toBinaryString(256 + (255-i));
				binstr2 = binstr2.substring(binstr2.length() -8);
				
				binstr = "<table><tr><td>Original integer: </td><td>"+ Integer.toString(i) + "</td></tr><tr><td> binaryval: </td><td>" + binstr +"</td></tr><tr><td>Inverted binary value: </td><td>" + binstr2 +"</td></tr></table>";
				
				this.binval = binstr;
				
			}catch (Exception e){
				this.binval = "Please supply an integer value in the inputobox";
			}
			
			
			//return binstr;
		}
		
		if( action.equals("dbconv") )
		{
			try {
				ConvertRawToBinaryData myConvertRawToBinaryData = new ConvertRawToBinaryData(db);		
				
				myConvertRawToBinaryData.getData("*", "*", "*", "*", "*", "*", "*", "*", "*");
				//this.dbval = myConvertRawToBinaryData.printData();			
				myConvertRawToBinaryData.convertData();
			
			}			
			catch (Exception e1) {
			e1.printStackTrace();
			}
		}
		
		
		
		
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
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

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		if(this.getLogin().isAuthenticated()){
			return true;
		}else
		{
			return false;
		}
	}
}
