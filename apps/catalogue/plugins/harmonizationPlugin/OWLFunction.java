package plugins.harmonizationPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLFunction {

	private String ontologyIRI = "";
	private String separtor = ";";
	private OWLDataFactory factory = null;
	private OWLOntology owlontology = null;
	private OWLOntologyManager manager = null;
	private HashMap<String, List<String>> classLabelToSynonyms = new HashMap<String, List<String>>();
	private HashMap<String, String> synonymToClassLabel = new HashMap<String, String>();
	private HashMap<String, OWLClass> labelToClass = new HashMap<String, OWLClass>();


	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();
	private HashMap<String, String> variableFormula = new HashMap<String, String>();
	private List<String> listOfVariables = new ArrayList<String>();

	public OWLFunction(){

	}

	public OWLFunction(String ontologyFileName) throws OWLOntologyCreationException{
		this.manager = OWLManager.createOWLOntologyManager();
		this.factory = manager.getOWLDataFactory();
		this.owlontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFileName));
		this.ontologyIRI = owlontology.getOntologyID().getOntologyIRI().toString();
	}

	public OWLFunction(OWLDataFactory factory, OWLOntology owlontology){
		this.owlontology = owlontology;
		this.factory = factory;
	}

	public void setSeparator(String separator){
		this.separtor = separator;
	}

	public OWLOntology getOntology(){
		return this.owlontology;
	}

	public String getSeparator(){
		return this.separtor;
	}

	public HashMap<String, OWLClass> getLabelToClass() {
		return labelToClass;
	}

	public HashMap<String, String> getSynonymToClassLabel() {
		return synonymToClassLabel;
	}

	public HashMap<String, List<String>> getExpandedQueries(){
		return this.expandedQueries;
	}

	public HashMap<String, List<String>> getClassLabelToSynonyms() {
		return classLabelToSynonyms;
	}

	public void labelMapURI(String... annotationProperty){

		List<IRI> AnnotataionProperty = new ArrayList<IRI>();

		if(annotationProperty != null){
			for(String property : annotationProperty){
				AnnotataionProperty.add(IRI.create(ontologyIRI + "#" + property));
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

			OWLAnnotationProperty formulaAnnotation = factory.getOWLAnnotationProperty(
					IRI.create(ontologyIRI + "#Formula"));

			for (OWLAnnotation annotation : cls.getAnnotations(owlontology, formulaAnnotation)) {

				if (annotation.getValue() instanceof OWLLiteral) {

					OWLLiteral val = (OWLLiteral) annotation.getValue();
					String formula = val.getLiteral().toLowerCase();
					variableFormula.put(getLabel(cls, owlontology), formula);
				}
			}

			if(labelString.equalsIgnoreCase("diabetes mellitus")){
				System.out.println();
			}

			this.synonymToLabel(cls, labelString, AnnotataionProperty);
		}

		this.convertOWLRelationToTokens();
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
					if(!listOfSynonyms.contains(sysnonym) && !classLabel.equals(sysnonym)){

						listOfSynonyms.add(sysnonym);
						synonymToClassLabel.put(sysnonym, classLabel);
					}
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

			if(labelOfClass.equalsIgnoreCase("Gender")){
				System.out.println();
			}

			OWLClass cls = labelToClass.get(labelOfClass);

			for(OWLSubClassOfAxiom axiom : owlontology.getSubClassAxiomsForSubClass(cls)){

				OWLClassExpression expression = axiom.getSuperClass();

				List<OWLClass> tokens = new ArrayList<OWLClass>();

				String labelOfProperty = "";

				if(expression.isAnonymous()){
					for(OWLClass classToken : expression.getClassesInSignature()){
						if(!tokens.contains(classToken))
							tokens.add(classToken);
					}
					for(OWLObjectProperty property : expression.getObjectPropertiesInSignature()){
						labelOfProperty = getLabel(property, owlontology);
					}
				}
				queryExpansion(labelOfClass, labelOfProperty, tokens);
			}

			List<String> childAndParent = new ArrayList<String>();

			List<String> temp = null;

			if(!expandedQueries.containsKey(labelOfClass)){
				temp = new ArrayList<String>();
			}else{
				temp = expandedQueries.get(labelOfClass);
			}

			childAndParent = getAllChildren(owlontology, cls, new ArrayList<String>(), 1);

			temp.addAll(childAndParent);

			childAndParent = getAllParents(owlontology, cls, new ArrayList<String>(), 1);

			temp.addAll(childAndParent);

			temp.add(labelOfClass);

			temp = removeDuplication(temp);

			expandedQueries.put(labelOfClass, temp);
		}

		for(Entry<String, List<String>> eachEntry : classLabelToSynonyms.entrySet()){

			if(expandedQueries.containsKey(eachEntry.getKey())){

				if(eachEntry.getValue().size() > 0){
					List<String> temp = expandedQueries.get(eachEntry.getKey());
					temp.addAll(eachEntry.getValue());
					temp = removeDuplication(temp);
					expandedQueries.put(eachEntry.getKey(), temp);
				}
			}
		}
	}

	private List<String> removeDuplication(List<String> temp) {

		List<String> removedList = new ArrayList<String>();

		for(String eachElement : temp){
			if(!removedList.contains(eachElement.toLowerCase())){
				removedList.add(eachElement.toLowerCase());
			}
		}

		return removedList;
	}

	public void queryExpansion(String labelOfClass, String labelOfProperty, List<OWLClass> tokens){

		if(tokens.size() == 1 && !labelOfProperty.equals("")){

			List<String> firstTokens = new ArrayList<String>();

			List<String> temp = this.getAllChildren(owlontology, tokens.get(0), new ArrayList(), 1);

			firstTokens.addAll(temp);

			temp = this.getAllParents(owlontology, tokens.get(0), new ArrayList(), 1);

			firstTokens.addAll(temp);

			firstTokens.add(getLabel(tokens.get(0), owlontology));

			firstTokens = addSynonymsToList(firstTokens);

			for(String firstToken : firstTokens){

				List<String> expandedClassLabel = null;

				if(!expandedQueries.containsKey(labelOfClass)){
					expandedClassLabel = new ArrayList<String>();
					expandedClassLabel.add( labelOfProperty + separtor + firstToken);
				}else{
					expandedClassLabel = expandedQueries.get(labelOfClass);
					if(!expandedClassLabel.contains(labelOfProperty + separtor + firstToken)){
						expandedClassLabel.add(labelOfProperty + separtor + firstToken);
					}
				}

				expandedClassLabel = removeDuplication(expandedClassLabel);
				expandedQueries.put(labelOfClass, expandedClassLabel);
			}

		}else if(tokens.size() > 1){

			List<String> firstTokens = new ArrayList<String>();

			List<String> secondTokens = new ArrayList<String>();

			List<String> temp = this.getAllChildren(owlontology, tokens.get(0), new ArrayList(), 1);

			firstTokens.addAll(temp);

			temp = this.getAllParents(owlontology, tokens.get(0), new ArrayList(), 1);

			firstTokens.addAll(temp);

			temp = this.getAllChildren(owlontology, tokens.get(1), new ArrayList(), 1);

			secondTokens.addAll(temp);

			temp = this.getAllParents(owlontology, tokens.get(1), new ArrayList(), 1);

			secondTokens.addAll(temp);

			firstTokens.add(getLabel(tokens.get(0), owlontology));

			secondTokens.add(getLabel(tokens.get(1), owlontology));

			firstTokens = addSynonymsToList(firstTokens);

			secondTokens = addSynonymsToList(secondTokens);

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

					expandedClassLabel = removeDuplication(expandedClassLabel);
					expandedQueries.put(labelOfClass, expandedClassLabel);
				}
			}
		}
	}

	public List<String> addSynonymsToList(List<String> listOfString){

		List<String> addedList = new ArrayList<String>();

		for(String eachString : listOfString){

			addedList.add(eachString);

			addedList.addAll(classLabelToSynonyms.get(eachString.toLowerCase()));
		}

		return addedList;
	}

	public List<String> getAllChildren(String classLabel, List<String> allChildren, int recursiveTimes){

		if(labelToClass.containsKey(classLabel)){
			
			OWLClass cls = labelToClass.get(classLabel);
			
			for(OWLSubClassOfAxiom axiom : owlontology.getSubClassAxiomsForSuperClass(cls)){

				OWLClassExpression expression = axiom.getSubClass();

				if(!expression.isAnonymous()){
					String label = this.getLabel(expression.asOWLClass(), owlontology);
					allChildren.add(label);
					if(!expression.asOWLClass().isBottomEntity()){
						if(recursiveTimes - 1 > 0)
							getAllChildren(owlontology, expression.asOWLClass(), allChildren, --recursiveTimes);
					}
				}
			}
		}
		
		return allChildren;
	}
	
	public List<String> getAllChildren(OWLOntology localOntology, OWLClass cls, List<String> allChildren, int recursiveTimes){

		for(OWLSubClassOfAxiom axiom : localOntology.getSubClassAxiomsForSuperClass(cls)){

			OWLClassExpression expression = axiom.getSubClass();

			if(!expression.isAnonymous()){
				String label = this.getLabel(expression.asOWLClass(), localOntology);
				allChildren.add(label);
				if(!expression.asOWLClass().isBottomEntity()){
					if(recursiveTimes - 1 > 0)
						getAllChildren(localOntology, expression.asOWLClass(), allChildren, --recursiveTimes);
				}
			}
		}

		return allChildren;
	}

	public List<String> getAllParents(OWLOntology localOntology, OWLClass cls, List<String> allParents, int recursiveTimes){

		for(OWLSubClassOfAxiom axiom : localOntology.getSubClassAxiomsForSubClass(cls)){

			OWLClassExpression expression = axiom.getSuperClass();

			if(!expression.isAnonymous()){
				String label = this.getLabel(expression.asOWLClass(), localOntology);
				if(!label.equals("")){
					if(!allParents.contains(label)){
						allParents.add(label);
						if(recursiveTimes - 1 > 0)
							getAllParents(localOntology, expression.asOWLClass(), allParents, --recursiveTimes);
					}
				}
			}
		}

		return allParents;
	}

	public HashMap <String, String> getFormula(){

		return variableFormula;
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

	public void setListOfVariables(List<String> listOfVariable)
	{
		this.listOfVariables  = listOfVariable;
	}

	public List<String> getComposites(String mappedParameter)
	{

		List<String> buildingBlocks = new ArrayList<String>();

		OWLClass cls = labelToClass.get(mappedParameter.toLowerCase());

		for(OWLSubClassOfAxiom axiom : owlontology.getSubClassAxiomsForSubClass(cls)){

			OWLClassExpression expression = axiom.getSuperClass();

			if(expression.isAnonymous()){

				for(OWLClass composites : expression.getClassesInSignature()){
					buildingBlocks.add(getLabel(composites, owlontology));
				}
			}
		}

		return buildingBlocks;
	}

	public List<String> getSynonyms(String classLabel){

		List<String> synonyms = new ArrayList<String>();

		OWLClass cls = labelToClass.get(classLabel.toLowerCase());

		OWLAnnotationProperty synonymsAnnotationProperty = factory.getOWLAnnotationProperty(
				IRI.create(ontologyIRI + "#alternative_term"));
		
		for (OWLAnnotation annotation : cls.getAnnotations(owlontology, synonymsAnnotationProperty)) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				synonyms.add(val.getLiteral().toString());
			}      
		}
		
		return synonyms;
	}

	public List<String> getAnnotation(String classLabel, String annotationProperty)
	{
		List<String> listOfAnnotation = new ArrayList<String>();
		
		OWLClass cls = labelToClass.get(classLabel.toLowerCase());
		
		OWLAnnotationProperty synonymsAnnotationProperty = factory.getOWLAnnotationProperty(
				IRI.create(ontologyIRI + "#" + annotationProperty));
		
		for (OWLAnnotation annotation : cls.getAnnotations(owlontology, synonymsAnnotationProperty)) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				listOfAnnotation.add(val.getLiteral().toString());
			}      
		}
		return listOfAnnotation;
	}
}
