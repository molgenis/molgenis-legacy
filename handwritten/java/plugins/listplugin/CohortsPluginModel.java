/* Date:        May 24, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.bbmri.Biobank;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.ui.EasyPluginModel;


/**
 * CohortsPluginModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class CohortsPluginModel extends EasyPluginModel
{
	//a system veriable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	
	private List<Biobank> cohorts;
	
	//another example, you can also use getInvestigations() and setInvestigations(...)
	//public List<Investigation> investigations = new ArrayList<Investigation>();

	public CohortsPluginModel(CohortsPlugin controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}

	public void setCohorts(List<Biobank> cohorts) {
		this.cohorts = cohorts;
	}

	public List<Biobank> getCohorts() {
		return cohorts;
	}
	
	public java.util.List<Biobank> removeEmptyValues(Database db) {
		
		java.util.List<Biobank> Biobank = new ArrayList<Biobank>();
		Iterator<Biobank> iterator  = Biobank.iterator();
		
		Query<Biobank> q = db.query(Biobank.class);
		try {
			Biobank =  q.find();
			while (iterator.hasNext()) {
				if (iterator.next().getGwaDataNum().isEmpty()) {
					iterator.next().setGwaDataNum("not available");
				}
				
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return Biobank;
		
		
	}
	
//	@Override
//	public boolean isVisible() {
//		try {
//			return this.getController().getApplicationController().getLogin().canRead(this.getController());
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	
}
