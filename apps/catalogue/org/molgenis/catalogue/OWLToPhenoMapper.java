package org.molgenis.catalogue;

import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OWLToPhenoMapper {
	





public static void main (String args[]) throws OWLOntologyCreationException{
	
	OntologyOwlLib owlLib = new OntologyOwlLib();
	String path  = "/Users/pc_iverson/Documents/generic.owl";
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	OWLDataFactory factory = manager.getOWLDataFactory();
	
	OWLOntology genericOntology = owlLib.loadOntology(path, manager, factory);
	
	OWLOntology dataShaperOntology = manager.getOntology(IRI.create("http://www.datashaper.org/owl/2009/10/dataschema.owl"));
	
	OWLClass variableClass = factory.getOWLClass(IRI.create("http://www.datashaper.org/owl/2009/10/dataschema.owl#Variable"));
	
	ArrayList<String> variableArray = owlLib.extractFromOntology(genericOntology, factory, variableClass);
	
	System.out.println(variableArray);
//	
//	HashMap dataShaperLabelMap = test.labelMapURI(dataShaperOntology,factory);
//	
//	HashMap genericLabelMap = test.labelMapURIinstance(ontology, factory);
//	
	OWLClass domainClass = factory.getOWLClass(IRI.create("http://www.datashaper.org/owl/2009/10/dataschema.owl#Domain"));
	
	
//	for (OWLIndividual individual : domainClass.getIndividuals(ontology)){
//		
//		for(OWLEntity entity : individual.getSignature()){
//			System.out.println(test.getLabel(entity, ontology, factory));
//			ArrayList<String> array = test.getAnnotation(entity, ontology, factory);
//			System.out.println(array);
//		}
//	}
//	
	System.out.println();
}

}

