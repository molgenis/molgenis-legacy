package plugins.harmonizationPlugin;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class harmonizationPlugin extends PluginModel<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2146491520971364766L;
	
	private List<String> matchingResult = new ArrayList<String>();

	public harmonizationPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	public List<String> getMatchingResult(){
		return matchingResult;
	}
	
	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_harmonizationPlugin_harmonizationPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/harmonizationPlugin/harmonizationPlugin.ftl";
	}


	@Override
	public void handleRequest(Database db, Tuple request){
		// TODO Auto-generated method stub
		if(request.getAction().equals("mapping")){
			levenshteinDistance matching = new levenshteinDistance(2);
			try {
				matching.startMatching();
				matchingResult  = matching.getMatchingResult();
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void reload(Database db) {
		// TODO Auto-generated method stub
		
	}

}
