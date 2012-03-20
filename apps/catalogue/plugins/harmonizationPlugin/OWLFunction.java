package plugins.harmonizationPlugin;

import java.util.HashMap;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLFunction {
	
	public HashMap<String, OWLClass> labelMapURI(OWLDataFactory factory, OWLOntology owlontology, IRI AnnotataionProperty){

		HashMap<String, OWLClass> mapURI = new HashMap<String, OWLClass>();
		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		//		OWLAnnotationProperty synonym = factory.getOWLAnnotationProperty(
		//				IRI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN"));
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
}
