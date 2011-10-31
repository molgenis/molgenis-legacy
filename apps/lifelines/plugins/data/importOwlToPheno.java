package plugins.data;

import java.io.File;
import java.net.URI;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

	
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;


public class importOwlToPheno extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6149846107377048848L;
	
	public importOwlToPheno(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	
	@Override
	public String getViewName()
	{
		return "plugins_data_importOwlToPheno";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/importOwlToPheno.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{
		if ("ImportOwlToPheno".equals(request.getAction())) {
			System.out.println("Starting importing owl");
			
			//get hold of ontology manager
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

            //if ontology available from the web 
            IRI iri = IRI.create("http://www.datashaper.org/datashaper/owl/2009/10/generic.owl");
            OWLOntology dataShaperOntologyWeb = manager.loadOntology(iri);
            System.out.println("Loaded ontology: " + dataShaperOntologyWeb);
            // Remove the ontology so that we can load a local copy.
	        manager.removeOntology(dataShaperOntologyWeb);
	        
	        //load a local copy 
            File file = new File("/Users/despoina/Documents/Datashaper/generic.owl");

            //load local copy 
            // Now load the local copy
            OWLOntology dataShaperOntology = manager.loadOntologyFromOntologyDocument(file);
            System.out.println("Loaded ontology: " + dataShaperOntology);
            
            IRI documentIRI = manager.getOntologyDocumentIRI(dataShaperOntology);
            System.out.println("    from: " + documentIRI);
            
		}
	}

	@Override
	public void reload(Database db)	{
	}
	
	@Override
	public boolean isVisible() {
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}