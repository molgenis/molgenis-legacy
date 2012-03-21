package plugins.harmonizationPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLFunction {

	private OWLDataFactory factory = null;
	private OWLOntology owlontology = null;
	private HashMap<String, List<String>> classLabelToSynonyms = new HashMap<String, List<String>>();
	private HashMap<String, String> synonymToClassLabel = new HashMap<String, String>();
	private HashMap<String, OWLClass> labelToClass = new HashMap<String, OWLClass>();

	public OWLFunction(){

	}

	public OWLFunction(OWLDataFactory factory, OWLOntology owlontology){
		this.owlontology = owlontology;
		this.factory = factory;
	}

	public HashMap<String, String> getSynonymToClassLabel() {
		return synonymToClassLabel;
	}

	public HashMap<String, List<String>> getClassLabelToSynonyms() {
		return classLabelToSynonyms;
	}

	public HashMap<String, OWLClass> labelMapURI(List<IRI> AnnotataionProperty){

		labelToClass.clear();
		classLabelToSynonyms.clear();

		for (OWLClass cls : owlontology.getClassesInSignature()) {

			OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

			String labelString = "";
			// Get the annotations on the class that use the label property
			for (OWLAnnotation annotation : cls.getAnnotations(owlontology, label)) {

				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					labelString = val.getLiteral().toLowerCase();
					labelToClass.put(labelString.toLowerCase(), cls);
				}
			}
			synonymToLabel(cls, labelString, AnnotataionProperty);
		}

		return labelToClass;
	}//end of labelMapURI method

	public void synonymToLabel(OWLClass cls, String classLabel, List<IRI> AnnotataionProperty){

		List<OWLAnnotationProperty> listOfAnnotationProperty = new ArrayList<OWLAnnotationProperty>();

		for(IRI annotationPropertyIRI : AnnotataionProperty){
			listOfAnnotationProperty.add(factory.getOWLAnnotationProperty(annotationPropertyIRI));
		}

		Pattern pattern = Pattern.compile(">[a-zA-Z0-9\\s]+<");

		Matcher matcher = pattern.matcher("");

		List<String> listOfSynonyms = new ArrayList<String>();

		for(OWLAnnotationProperty property : listOfAnnotationProperty){

			String sysnonym = "";

			for (OWLAnnotation annotation : cls.getAnnotations(owlontology, property)) {

				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					sysnonym = val.getLiteral().toLowerCase();
					matcher.reset(sysnonym);
					if(matcher.find()){
						sysnonym = matcher.group(0);
						sysnonym = sysnonym.substring(1, sysnonym.length() - 1);
					}
					if(!listOfSynonyms.contains(sysnonym) && !classLabel.equals(sysnonym))
						listOfSynonyms.add(sysnonym);

					synonymToClassLabel.put(sysnonym, classLabel);
				}
			}
		}
		if(classLabelToSynonyms.containsKey(classLabel)){
			List<String> temp = classLabelToSynonyms.get(classLabel);
			temp.addAll(listOfSynonyms);
			classLabelToSynonyms.put(classLabel, temp);
		}else{
			classLabelToSynonyms.put(classLabel, listOfSynonyms);
		}
	}
}
