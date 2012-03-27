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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLFunction {
	
	private String separtor = ";";
	private OWLDataFactory factory = null;
	private OWLOntology owlontology = null;
	private HashMap<String, List<String>> classLabelToSynonyms = new HashMap<String, List<String>>();
	private HashMap<String, String> synonymToClassLabel = new HashMap<String, String>();
	private HashMap<String, OWLClass> labelToClass = new HashMap<String, OWLClass>();
	private HashMap<String, List<String>> classRelations = new HashMap<String, List<String>>();
	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();

	public OWLFunction(){

	}

	public OWLFunction(OWLDataFactory factory, OWLOntology owlontology){
		this.owlontology = owlontology;
		this.factory = factory;
	}

	public void setSeparator(String separator){
		this.separtor = separator;
	}
	
	public String getSeparator(){
		return this.separtor;
	}
	
	public HashMap<String, String> getSynonymToClassLabel() {
		return synonymToClassLabel;
	}

	public HashMap<String, List<String>> getClassRelations() {
		return classRelations;
	}
	
	public HashMap<String, List<String>> getExpandedQueries(){
		return this.expandedQueries;
	}

	public HashMap<String, List<String>> getClassLabelToSynonyms() {
		return classLabelToSynonyms;
	}

	public HashMap<String, OWLClass> labelMapURI(List<String> annotationProperty){

		List<IRI> AnnotataionProperty = new ArrayList<IRI>();

		if(annotationProperty != null){
			for(String property : annotationProperty){
				AnnotataionProperty.add(IRI.create(property));
			}
		}

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
			this.synonymToLabel(cls, labelString, AnnotataionProperty);
		}

		this.convertOWLRelationToTokens();

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


	public void convertOWLRelationToTokens(){

		for(String labelOfClass : labelToClass.keySet()){

			OWLClass cls = labelToClass.get(labelOfClass);

			List<String> relations = null;

			if(classRelations.containsKey(getLabel(cls, owlontology))){
				relations = classRelations.get(getLabel(cls, owlontology));
			}else{
				relations = new ArrayList<String>();
			}

			for(OWLSubClassOfAxiom axiom : owlontology.getSubClassAxiomsForSubClass(cls)){

				OWLClassExpression expression = axiom.getSuperClass();

				List<OWLClass> tokens = new ArrayList<OWLClass>();

				if(expression.isAnonymous()){
					String definition = "";
					for(OWLClass classToken : expression.getClassesInSignature()){
						if(!relations.contains(getLabel(classToken, owlontology))){
							definition += " " + getLabel(classToken, owlontology);
						}
						tokens.add(classToken);
					}
					definition = definition.trim().toLowerCase();
					if(!definition.equalsIgnoreCase(labelOfClass))
						relations.add(definition.trim());
				}

				queryExpansion(labelOfClass, tokens);
			}
			classRelations.put(labelOfClass, relations);
		}
	}

	public void queryExpansion(String labelOfClass, List<OWLClass> tokens){

		if(tokens.size() > 1){

			List<String> firstTokens = this.getAllChildren(owlontology, tokens.get(0), new ArrayList());

			List<String> secondTokens = this.getAllChildren(owlontology, tokens.get(1), new ArrayList());

			firstTokens.add(getLabel(tokens.get(0), owlontology));

			secondTokens.add(getLabel(tokens.get(1), owlontology));

			for(String firstToken : firstTokens){

				for(String secondToken : secondTokens){

					List<String> expandedClassLabel = null;

					if(!expandedQueries.containsKey(labelOfClass)){
						expandedClassLabel = new ArrayList<String>();
						expandedClassLabel.add(firstToken + separtor + secondToken);
					}else{
						expandedClassLabel = expandedQueries.get(labelOfClass);
						if(!expandedClassLabel.contains(firstToken + separtor + secondToken)){
							expandedClassLabel.add(firstToken + separtor + secondToken);
						}
					}
					expandedQueries.put(labelOfClass, expandedClassLabel);
				}
			}
		}
	}

	public List<String> getAllChildren(OWLOntology localOntology, OWLClass cls, List<String> allChildren){

		for(OWLSubClassOfAxiom axiom : localOntology.getSubClassAxiomsForSuperClass(cls)){

			for(OWLClassExpression expression : axiom.getClassesInSignature()){
				if(!expression.isAnonymous() && !expression.asOWLClass().equals(cls)){
					String label = this.getLabel(expression.asOWLClass(), localOntology);
					allChildren.add(label);
					if(!expression.asOWLClass().isBottomEntity()){
						getAllChildren(localOntology, expression.asOWLClass(), allChildren);
					}
				}
			}
		}

		return allChildren;
	}

	public List<String> getAllParents(OWLOntology localOntology, OWLClass cls, List<String> allParents){

		for(OWLSubClassOfAxiom axiom : localOntology.getSubClassAxiomsForSubClass(cls)){

			for(OWLClassExpression expression : axiom.getClassesInSignature()){
				if(!expression.isAnonymous() && !expression.asOWLClass().equals(cls)){
					String label = this.getLabel(expression.asOWLClass(), localOntology);
					if(!label.equals("")){
						if(!allParents.contains(label)){
							allParents.add(label);
							getAllParents(localOntology, expression.asOWLClass(), allParents);
						}
					}
				}
			}
		}

		return allParents;
	}

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
