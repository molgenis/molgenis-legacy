
package plugins.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.molgenis.catalogue.OntologyBuilder;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

	
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;


public class importOwlToPheno extends PluginModel<Entity>
{
	private String Status = "";

	private static final long serialVersionUID = 6149846107377048848L;
    public static final String DOCUMENT_IRI = "http://www.datashaper.org/datashaper/owl/2009/10/generic.owl";

	
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

	
	public OWLOntology loadOntologyFromWeb(OWLOntologyManager manager, String base,IRI iri) throws OWLOntologyCreationException {
		
			System.out.println("Starting importing owl from web");
			this.setStatus("Starting importing owl from web");
			
            OWLOntology dataShaperOntologyWeb = manager.loadOntology(iri);
            
            System.out.println("Loaded ontology from web: " + dataShaperOntologyWeb);
            this.setStatus("Loaded ontology from web: " + dataShaperOntologyWeb);
            
            // Remove the ontology so that we can load a local copy.
	        manager.removeOntology(dataShaperOntologyWeb);
	
	        return dataShaperOntologyWeb;
	}
	
	public OWLOntology loadOntologyFromLocal(OWLOntology dataShaperOntology, OWLOntologyManager manager, String path) throws OWLOntologyCreationException {
		
        File file = new File(path);
		
        if (file.exists()) {
            dataShaperOntology = manager.loadOntologyFromOntologyDocument(file);

            System.out.println("Loaded ontology from local file : " + dataShaperOntology + dataShaperOntology.getOntologyID());
            this.setStatus("Loaded ontology from local file: " + dataShaperOntology + dataShaperOntology.getOntologyID());

            manager.removeOntology(dataShaperOntology);
        
            return dataShaperOntology;
        } else {
        	System.out.println("The ontology file is not available!");
        	this.setStatus("The ontology file is not available!");
        	return null;
        } 
	}
	
	
	
	public void buildOntology(OWLOntologyManager manager,IRI iri, String base, OWLOntology ontology, OWLDataFactory factory) throws OWLOntologyCreationException, OWLOntologyStorageException {
		//base ="http://www.semanticweb.org/owlapi/ontologies/ontology#"
		
		//obtain references to entities (classes, properties, individuals etc.)
        // We can get a reference to a data factory from an OWLOntologyManager.
        
        // The first is by specifying the full IRI.  First we create an IRI object:
        OWLClass clsAMethodA = factory.getOWLClass(iri); //TODO : recheck the argument 
        
        // The first is by specifying the full IRI.  First we create an IRI object:
        // Now we create the class
        
        //we''ll need to build a prefix manager . 
        PrefixManager pm = new DefaultPrefixManager(base);
        OWLClass clsAMethodB = factory.getOWLClass(":A", pm);
      
        OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(clsAMethodA);
        manager.addAxiom(ontology, declarationAxiom);
        
        //////////////////////////////////////////////
        //work with literals
        OWLLiteral literal1 = factory.getOWLLiteral("My string literal", "");
        OWLLiteral literal2 = factory.getOWLLiteral("My string literal", "en");

        System.out.println(">>>>>test " + literal1 + ">>>>>>"+ literal2);	
        //////////////////////////////////////////////
        //

        OWLClass domain = factory.getOWLClass(":Domain", pm);
        // Get the reference to the :GENERIC19 class 
        OWLNamedIndividual generic19 = factory.getOWLNamedIndividual(":GENERIC_19", pm);
        
        // Now create a ClassAssertion to specify that :GENERIC19 is an instance of :DOMAIN
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(domain, generic19);
        // We need to add the class assertion to the ontology that we want specify that :generic_19Mary is a :Domain
        // Add the class assertion
        manager.addAxiom(ontology, classAssertion);
        	
        // Dump the ontology to stdout
        manager.saveOntology(ontology, new SystemOutDocumentTarget());

		
	}
	
	public void extractFromOntology(OWLOntologyManager manager, OWLOntology ontology,  OWLDataFactory df, String path) throws OWLOntologyCreationException, OWLOntologyStorageException {
	
		// We want to extract a module for all toppings.
		// We therefore have to generate a seed signature that contains "PizzaTopping" and its subclasses.
		// We start by creating a signature that consists of "PizzaTopping".
        OWLClass DomainCls = df.getOWLClass(IRI.create(ontology.getOntologyID().getOntologyIRI().toString() + "#GENERIC_491"));
        Set<OWLEntity> sig = new HashSet<OWLEntity>();
        
        sig.add(DomainCls);
        
        // We now add all subclasses (direct and indirect) of the chosen classes.
        // Ideally, it should be done using a DL reasoner, in order to take inferred subclass relations into account.
        // We are using the structural reasoner of the OWL API for simplicity.

        Set<OWLEntity> seedSig = new HashSet<OWLEntity>();
        OWLReasoner reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);

        for (OWLEntity ent : sig) {
        	seedSig.add(ent);
        	if (OWLClass.class.isAssignableFrom(ent.getClass())) {
        		NodeSet<OWLClass> subClasses = reasoner.getSubClasses((OWLClass) ent, false);
        		seedSig.addAll(subClasses.getFlattened());
        	}
        }
        
     // Output for debugging purposes
        System.out.println();
        System.out.println("Extracting the module for the seed signature consisting of the following entities:");
        for (OWLEntity ent : seedSig) {
               System.out.println("  " + ent);
         }
        System.out.println();
        System.out.println("Some statistics of the original ontology:");
        System.out.println("  " + ontology.getSignature(true).size() + " entities");
        System.out.println("  " + ontology.getLogicalAxiomCount() + " logical axioms");
        System.out.println("  " + (ontology.getAxiomCount() - ontology.getLogicalAxiomCount()) + " other axioms");
        System.out.println();
        
     // We now extract a locality-based module.
        // For most reuse purposes, the module type should be STAR -- this yields the smallest possible locality-based module.
        // These modules guarantee that all entailments of the original ontology that can be formulated using only
        // terms from the seed signature or the module will also be entailments of the module.
        // In easier words, the module preserves all knowledge of the ontology about the terms in the seed signature or the module.
        
        SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(manager, ontology, ModuleType.STAR);
        IRI moduleIRI = IRI.create(path);
        OWLOntology mod = sme.extractAsOntology(seedSig, moduleIRI);
        
        	            // Output for debugging purposes
        	            System.out.println("Some statistics of the module:");
        	            System.out.println("  " + mod.getSignature(true).size() + " entities");
        	            System.out.println("  " + mod.getLogicalAxiomCount() + " logical axioms");
        	            System.out.println("  " + (mod.getAxiomCount() - mod.getLogicalAxiomCount()) + " other axioms");
        	            System.out.println();
        	
        	            // And we save the module.
        	            System.out.println("Saving the module as " + mod.getOntologyID().getOntologyIRI() + " .");
        	            manager.saveOntology(mod);
        	      
	}

	
	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{
		if ("ImportOwlToPheno".equals(request.getAction())) {
			try {
	            	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	                String base = "http://www.datashaper.org/datashaper/owl/2009/10/generic.owl";
	                String path = "/Users/despoina/Documents/Datashaper/generic.owl";
	                IRI iri = IRI.create(base);
	                OntologyBuilder ob = new OntologyBuilder(manager, base);
	                OWLDataFactory factory = manager.getOWLDataFactory();

					//this.loadOntologyFromWeb(manager, base, iri);  
	                OWLOntology dataShaperOntology  = null;
	                dataShaperOntology = this.loadOntologyFromLocal(dataShaperOntology, manager, path);
	                
//	                IRI documentIRI = manager.getOntologyDocumentIRI(dataShaperOntology);
//
//	                System.out.println("    from: " + documentIRI);
//	                this.setStatus("     from: " + documentIRI);
//	                
	                extractFromOntology(manager, dataShaperOntology, factory, path);
	                
	                PrefixManager pm = new DefaultPrefixManager(base);
	                OWLClass ClassSet[] = new OWLClass[142];
	               
	                HashMap mapURI = new HashMap();


	                
	                
				    OWLEntity domain = factory.getOWLClass(":Domain", pm);
				    //OWLClass domain = factory.getOWLClass(":Domain", pm);

					System.out.println(">>>>>label " + ob.getLabel(domain, dataShaperOntology));
				            
				      
				            try{
				                OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
					        
				                System.out.println(">>HERE>>>>>> " );

				                for (OWLAnnotation annotation : domain.getAnnotations(dataShaperOntology, label)) {
						            System.out.println(">>HERE>>>>>> " );

				                    if (annotation.getValue() instanceof OWLLiteral) {
				                        OWLLiteral val = (OWLLiteral) annotation.getValue();
				                        String labelValue = val.getLiteral().toString();

							            System.out.println(">>>>>label>>>>>> " + labelValue);
								         
				                    }      
				                }       
				            }catch(Exception e){
				                System.out.println("The annotation is null!");
				            }
				    
				            
				                
				            
		            } catch (OWLOntologyCreationIOException e) {
            		            // IOExceptions during loading get wrapped in an OWLOntologyCreationIOException
            		            IOException ioException = e.getCause();
            		            if (ioException instanceof FileNotFoundException) {
            		                System.out.println("Could not load ontology. File not found: " + ioException.getMessage());
            		            }
            		            else if (ioException instanceof UnknownHostException) {
            		                System.out.println("Could not load ontology. Unknown host: " + ioException.getMessage());
            		            }
            		            else {
            		                System.out.println("Could not load ontology: " + ioException.getClass().getSimpleName() + " " + ioException.getMessage());
            		            }
            		        }
            		   catch (UnparsableOntologyException e) {
            		            // If there was a problem loading an ontology because there are syntax errors in the document (file) that
            		            // represents the ontology then an UnparsableOntologyException is thrown
            		            System.out.println("Could not parse the ontology: " + e.getMessage());
            		            // A map of errors can be obtained from the exception
            		            Map<OWLParser, OWLParserException> exceptions = e.getExceptions();
            		            // The map describes which parsers were tried and what the errors were
            		            for (OWLParser parser : exceptions.keySet()) {
            		                System.out.println("Tried to parse the ontology with the " + parser.getClass().getSimpleName() + " parser");
            		                System.out.println("Failed because: " + exceptions.get(parser).getMessage());
            		            }
            		        }
            		   catch (UnloadableImportException e) {
            		            // If our ontology contains imports and one or more of the imports could not be loaded then an
            		            // UnloadableImportException will be thrown (depending on the missing imports handling policy)
            		            System.out.println("Could not load import: " + e.getImportsDeclaration());
            		            // The reason for this is specified and an OWLOntologyCreationException
            		            OWLOntologyCreationException cause = e.getOntologyCreationException();
            		            System.out.println("Reason: " + cause.getMessage());
            		        }
            		   catch (OWLOntologyCreationException e) {
            		            System.out.println("Could not load ontology: " + e.getMessage());
            		        }
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


	public void setStatus(String status) {
		Status = status;
	}


	public String getStatus() {
		return Status;
	}
}