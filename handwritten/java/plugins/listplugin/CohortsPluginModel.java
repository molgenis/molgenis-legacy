/* Date:        May 24, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.listplugin;

import java.util.List;

import org.molgenis.bbmri.BiobankPanel;
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
	
	private List<BiobankPanel> cohorts;
	
	//another example, you can also use getInvestigations() and setInvestigations(...)
	//public List<Investigation> investigations = new ArrayList<Investigation>();

	public CohortsPluginModel(CohortsPlugin controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}

	public void setCohorts(List<BiobankPanel> cohorts) {
		this.cohorts = cohorts;
	}

	public List<BiobankPanel> getCohorts() {
		return cohorts;
	}
	
	
}
