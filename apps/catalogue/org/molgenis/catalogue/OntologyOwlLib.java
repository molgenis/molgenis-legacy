package org.molgenis.catalogue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OntologyOwlLib {
	
	
	
	public OWLOntology loadOntology(String path, OWLOntologyManager manager, OWLDataFactory factory) throws OWLOntologyCreationException {
		
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(path));
		
		System.out.println("The ontology " + ontology.getOntologyID().toString() + " has been loaded!");
		return ontology;
		
		
	}
	
	public ArrayList<String> extractFromOntology (OWLOntology ontology, OWLDataFactory factory, OWLClass extractedClass){
		
		ArrayList<String> array = new ArrayList<String>();
		
		for (OWLIndividual individual : extractedClass.getIndividuals(ontology)){
			for(OWLEntity entity : individual.getSignature()){
				array.add(getLabel(entity, ontology, factory));
			}
		}
		return array;
	}
	
	public ArrayList<String> getAnnotation (OWLEntity entity, OWLOntology ontology, OWLDataFactory factory){
		
		ArrayList<String> annotation = new ArrayList<String>();
		
		for(OWLAnnotation anno : entity.getAnnotations(ontology)){
			
			if (anno.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) anno.getValue();
				annotation.add(val.getLiteral());
			}
		}
		
		return annotation;
	}
	
	public HashMap labelMapURI(OWLOntology owlontology, OWLDataFactory factory){

		HashMap mapURI = new HashMap();

		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		//System.out.println(label.getIRI());
		for (OWLClass cls : owlontology.getClassesInSignature(true)) {
			// Get the annotations on the class that use the label property
			//System.out.println(cls);
			for (OWLAnnotation annotation : cls.getAnnotations(owlontology)) {
				//System.out.println(annotation);
				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					String labelString = val.getLiteral();
					mapURI.put(labelString, cls);
				}
			}
		}
		return mapURI;
	}//end of labelMapURI method
	
	public HashMap labelMapURIinstance(OWLOntology owlontology, OWLDataFactory factory){

		HashMap mapURI = new HashMap();

		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		//System.out.println(label.getIRI());
		for (OWLEntity cls : owlontology.getIndividualsInSignature()) {
			// Get the annotations on the class that use the label property
			//System.out.println(cls);
			for (OWLAnnotation annotation : cls.getAnnotations(owlontology)) {
				//System.out.println(annotation);
				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					String labelString = val.getLiteral();
					mapURI.put(labelString, cls);
				}
			}
		}
		return mapURI;
	}//end of labelMapURI method
	public String getLabel(OWLEntity cls, OWLOntology owlontology, OWLDataFactory factory){
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
