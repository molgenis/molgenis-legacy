package plugins.harmonizationPlugin;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;



public class createOntology {

	private OWLOntologyManager manager;
	private OWLOntology referenceOntology;
	private OWLOntology createdOntology;
	private OWLDataFactory factory;
	private HashMap<String, OWLClass> labelToOWLClass;
	private HashMap<String, OWLClass> synonymsToOWLClass;

	public createOntology() throws OWLOntologyCreationException, OWLOntologyStorageException{

		manager = OWLManager.createOWLOntologyManager();

		factory = manager.getOWLDataFactory();

		referenceOntology = this.loadOntology("/Users/pc_iverson/Desktop/Input/Thesaurus.owl");
		
		System.out.println(referenceOntology.getOntologyID().getOntologyIRI().toString());
		
		createdOntology = this.loadOntology("/Users/pc_iverson/Desktop/Input/PredictionModel.owl");

		this.labelToOWLClass = labelMapURI(referenceOntology, OWLRDFVocabulary.RDFS_LABEL.getIRI());
		
		this.synonymsToOWLClass = labelMapURI(referenceOntology, IRI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN"));
		
		this.addingClassHierarchy(referenceOntology, createdOntology);

		this.OntologySave(createdOntology);

	}

	public void OntologySave (OWLOntology ontology) throws OWLOntologyStorageException{
		manager.saveOntology(ontology);
	}

	private void addingClassHierarchy(OWLOntology referenceOntology, OWLOntology createdOntology) {
		
		Set<OWLOntology> setOfOntologies = new HashSet<OWLOntology>();
		
		setOfOntologies.add(createdOntology);
		
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, setOfOntologies);
		
		for(OWLClass cls : createdOntology.getClassesInSignature()){

			String className = this.getLabel(cls, createdOntology);
			
			OWLClass replacedClass = null;
			
			if(labelToOWLClass.containsKey(className.toLowerCase())){
				replacedClass = labelToOWLClass.get(className.toLowerCase());
			}
			if(synonymsToOWLClass.containsKey(className.toLowerCase())){
				replacedClass = labelToOWLClass.get(className.toLowerCase());
			}
			if(replacedClass != null){
				List<OWLOntologyChange> changes = renamer.changeIRI(cls, replacedClass.getIRI());
				manager.applyChanges(changes);
			}
		}
	}

	private void recursiveAddingSuperClass(OWLClass cls,
			OWLOntology referenceOntology2, OWLOntology createdOntology2) {




	}

	public static void main (String args[]) throws OWLOntologyCreationException, OWLOntologyStorageException {

		new createOntology();
	}

	public void openSpreadSheet(String fileName){

	}

	public OWLOntology loadOntology(String ontologyFileName) throws OWLOntologyCreationException{

		System.out.println("Loading the ontology file!");

		File file = new File(ontologyFileName);

		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		System.out.println("The ontology is loaded!");

		return ontology;
	}

	//	public void startOntologyService() throws OntologyServiceException, URISyntaxException{
	//		
	//		System.out.println("Start the service");
	//		
	//		FileOntologyService os = new FileOntologyService(new URI("https://cabig.nci.nih.gov/community/concepts/EVS/"), "NCLt");
	//		
	//		System.out.println("Start the searching");
	//		
	//		for (OntologyTerm ot : os.searchAll("thymus", SearchOptions.EXACT)) {
	//			System.out.println(ot.getLabel());
	//		}
	//		
	//		
	//		
	//	}

	public HashMap labelMapURI(OWLOntology owlontology, IRI AnnotataionProperty){

		HashMap<String, OWLClass> mapURI = new HashMap<String, OWLClass>();
		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(AnnotataionProperty);
		OWLAnnotationProperty synonym = factory.getOWLAnnotationProperty(
				IRI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN"));
		for (OWLClass cls : owlontology.getClassesInSignature()) {
			// Get the annotations on the class that use the label property
			for (OWLAnnotation annotation : cls.getAnnotations(owlontology, label)) {
				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					String labelString = val.getLiteral();
					mapURI.put(labelString.toLowerCase(), cls);
				}
			}
		}
		return mapURI;
	}//end of labelMapURI method

	/*
	 * This method is used to get a label of corresponding OWLClass. 
	 * @param      cls is the class we want to get label 
	 * @return     the label of the class
	 */ 
	public String getLabel(OWLEntity cls, OWLOntology owlontology){
		String labelValue = "";
		try{
			OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
			for (OWLAnnotation annotation : cls.getAnnotations(owlontology, label)) {
				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					labelValue = val.getLiteral().toString();
				}      
			}       
		}catch(Exception e){
			System.out.println("The annotation is null!");
		}
		return labelValue;
	}//end of the getLabel method

}
