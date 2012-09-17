
package plugins.listplugin;

import org.molgenis.bbmri.Biobank;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;

/**
 * CohortsPluginController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>CohortsPluginModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>CohortsPluginView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class CohortsPlugin extends EasyPluginController<CohortsPluginModel>
{
	private static final long serialVersionUID = -6218788933409278070L;

	public CohortsPlugin(String name, ScreenController<?> parent)
	{ 
		super(name, parent);
		this.setModel(new CohortsPluginModel(this)); //the default model
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("CohortsPluginView.ftl", getModel());
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/scripts/custom/jquery.dataTables.js\"></script>\n"
			//+ "<script type=\"text/javascript\" charset=\"utf-8\">jQuery.noConflict();</script>\n"
			//+ "<script src=\"res/scripts/custom/jquery-ui-1.8.6.custom.min.js\" type=\"text/javascript\" language=\"javascript\"></script>"
			+ "<script src=\"res/scripts/custom/jquery.autocomplete.combobox.js\" type=\"text/javascript\" language=\"javascript\"></script>"
			//+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_table.css\">\n"
			+ "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/demo_page.css\">\n";
			//+"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/ui-lightness/jquery-ui-1.8.6.custom.css\">";
		   //+ "<script>$(document).ready(function(){ $( \"#arf\" ).combobox();  }); </script>";
			//+ "<script> $(function() { $( \"#arf\" ).combobox();  });	</script>;";
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
		// Fill list of Cohorts
		Query<Biobank> q = db.query(Biobank.class);
		this.getModel().setCohorts(q.find());
	
	}
	
//	public boolean isVisible() {
//		if (this.getApplicationController().getLogin().isAuthenticated()) {
//			try {
//				if (this.getApplicationController().getLogin().canRead(this)) {
//					return true;
//				}
//			} catch (DatabaseException e) {
//				e.printStackTrace();
//			}
//			
//		}
//		return false;
//	}


	
//	public java.util.List<BiobankPanel> removeEmptyValues(Database db) {
//		
//		java.util.List<BiobankPanel> biobankPanel = new ArrayList<BiobankPanel>();
//		Iterator<BiobankPanel> iterator  = biobankPanel.iterator();
//		
//		Query<BiobankPanel> q = db.query(BiobankPanel.class);
//		try {
//			biobankPanel =  q.find();
//			while (iterator.hasNext()) {
//				if (iterator.next().getGwaDataNum().isEmpty()) {
//					iterator.next().setGwaDataNum("not available");
//				}
//				
//			}
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		return biobankPanel;
//		
//		
//	}
}
