package plugins.HarmonizationComponent;

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
	private List<String> allTerms = new ArrayList<String>();

	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();
	private HashMap<String, String> variableFormula = new HashMap<String, String>();

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
	
	public List<String> getAllTerms(){
		
		for(OWLClass cls : owlontology.getClassesInSignature()){
			allTerms.add(getLabel(cls, owlontology));
		}
		return allTerms;
	}
	
	public String getOntologyTermID(String ontologyTerm){
		
		OWLClass cls = labelToClass.get(ontologyTerm.toLowerCase());
		
		String owlClassID = cls.getIRI().toString().substring(ontologyIRI.length() - 2);
		
		return owlClassID;
	}
	
	public void labelMapURI(){
		
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
		}
		
	}
	
	public void labelMapURI(List<String> listOfParameters, String... annotationProperty){

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

		this.convertOWLRelationToTokens(listOfParameters);
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


	public void convertOWLRelationToTokens(List<String> listOfParameters){

		for(String labelOfClass : listOfParameters){

			labelOfClass = labelOfClass.toLowerCase();

			if(labelOfClass.equalsIgnoreCase("Parental diabetes mellitus")){
				System.out.println();
			}

			OWLClass cls = labelToClass.get(labelOfClass);

			for(OWLSubClassOfAxiom axiom : owlontology.getSubClassAxiomsForSubClass(cls)){

				OWLClassExpression expression = axiom.getSuperClass();

				List<OWLClass> tokens = new ArrayList<OWLClass>();

				List<String> nameOfClasses = new ArrayList<String>();

				String labelOfProperty = "";

				if(expression.isAnonymous()){
					for(OWLClass classToken : expression.getClassesInSignature()){
						if(!tokens.contains(classToken)){
							tokens.add(classToken);
							nameOfClasses.add(getLabel(classToken, owlontology));
						}
					}
					for(OWLObjectProperty property : expression.getObjectPropertiesInSignature()){
						labelOfProperty = getLabel(property, owlontology);
					}
				}
				if(nameOfClasses.size() > 2){
					queryExpansion(labelOfClass, labelOfProperty, nameOfClasses, new ArrayList<String> ());
				}
				queryExpansionPairWise(labelOfClass, labelOfProperty, tokens);
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
			
			temp.remove("Prediction Model");
			
			temp.remove("Composite");

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

	public void queryExpansion(String labelOfClass, String labelOfProperty, List<String> tokens, List<String> concatenatedString){

		int tokenSize = tokens.size();

		String lastToken = tokens.get(tokenSize - 1).toLowerCase();

		tokens.remove(tokens.get(tokenSize - 1));

		List<String> firstGroup = new ArrayList<String>();

		if(labelToClass.containsKey(lastToken)){

			List<String> temp = this.getAllChildren(owlontology, labelToClass.get(lastToken), new ArrayList<String>(), 1);

			firstGroup.addAll(temp);

			temp = this.getAllParents(owlontology, labelToClass.get(lastToken), new ArrayList<String>(), 1);

			firstGroup.addAll(temp);
		}

		firstGroup = addSynonymsToList(firstGroup);
		
		firstGroup.add(lastToken);

		List<String> combination = new ArrayList<String>();

		for(String elementOne : firstGroup){

			if(concatenatedString.size() == 0){
				combination.add(elementOne);
			}
			for(String elementTwo : concatenatedString){
				combination.add(elementOne + separtor + elementTwo);
			}
		}

		if(tokens.size() == 0){

			List<String> expandedClassLabel = null;

			if(expandedQueries.containsKey(labelOfClass)){

				expandedClassLabel = expandedQueries.get(labelOfClass);

				expandedClassLabel.addAll(combination);

			}else{
				expandedClassLabel = combination;
			}

			expandedClassLabel = removeDuplication(expandedClassLabel);
			expandedQueries.put(labelOfClass, expandedClassLabel);

		}else{
			queryExpansion(labelOfClass, labelOfProperty, tokens, combination);
		}
	}

	public void queryExpansionPairWise(String labelOfClass, String labelOfProperty, List<OWLClass> tokens){

		if(tokens.size() == 1 && !labelOfProperty.equals("")){

			List<String> firstTokens = new ArrayList<String>();

			List<String> temp = this.getAllChildren(owlontology, tokens.get(0), new ArrayList<String>(), 1);

			firstTokens.addAll(temp);

			temp = this.getAllParents(owlontology, tokens.get(0), new ArrayList<String>(), 1);

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

			for(OWLClass firstToken : tokens){

				for(OWLClass secondToken : tokens){

					if(!firstToken.equals(secondToken)){

						List<String> firstGroup = new ArrayList<String>();

						List<String> secondGroup = new ArrayList<String>();

						List<String> temp = this.getAllChildren(owlontology, firstToken, new ArrayList<String>(), 1);

						firstGroup.addAll(temp);

						temp = this.getAllParents(owlontology, firstToken, new ArrayList<String>(), 1);

						firstGroup.addAll(temp);

						temp = this.getAllChildren(owlontology, secondToken, new ArrayList<String>(), 1);

						secondGroup.addAll(temp);

						temp = this.getAllParents(owlontology, secondToken, new ArrayList<String>(), 1);

						secondGroup.addAll(temp);

						firstGroup.add(getLabel(firstToken, owlontology));

						secondGroup.add(getLabel(secondToken, owlontology));

						firstGroup = addSynonymsToList(firstGroup);

						secondGroup = addSynonymsToList(secondGroup);

						for(String elementOne : firstGroup){

							for(String elementTwo : secondGroup){

								List<String> expandedClassLabel = null;

								if(!expandedQueries.containsKey(labelOfClass)){
									expandedClassLabel = new ArrayList<String>();
									expandedClassLabel.add(elementOne + separtor + elementTwo);
								}else{
									expandedClassLabel = expandedQueries.get(labelOfClass);
									if(!expandedClassLabel.contains(elementOne + separtor + elementTwo)){
										expandedClassLabel.add(elementOne + separtor + elementTwo);
									}
								}

								expandedClassLabel = removeDuplication(expandedClassLabel);
								expandedQueries.put(labelOfClass, expandedClassLabel);
							}
						}
					}
				}
			}
		}
	}

	public List<String> addSynonymsToList(List<String> listOfString){

		List<String> addedList = new ArrayList<String>();

		for(String eachString : listOfString){

			addedList.add(eachString);

			if(classLabelToSynonyms.containsKey(eachString.toLowerCase())){

				addedList.addAll(classLabelToSynonyms.get(eachString.toLowerCase()));

			}
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
	
	public List<String> getAnnotation(String classLabel, IRI annotationProperty)
	{
		List<String> listOfAnnotation = new ArrayList<String>();

		OWLClass cls = labelToClass.get(classLabel.toLowerCase());

		OWLAnnotationProperty synonymsAnnotationProperty = factory.getOWLAnnotationProperty(annotationProperty);

		for (OWLAnnotation annotation : cls.getAnnotations(owlontology, synonymsAnnotationProperty)) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				listOfAnnotation.add(val.getLiteral().toString());
			}      
		}
		return listOfAnnotation;
	}
	
	public String getOntologyIRI()
	{
		return ontologyIRI;
	}
}
