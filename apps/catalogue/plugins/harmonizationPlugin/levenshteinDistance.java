package plugins.harmonizationPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.cxf.binding.corba.wsdl.Array;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;


import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;
import uk.ac.ebi.ontocat.virtual.CompositeDecorator;

public class levenshteinDistance {

	//Choose n-grams to tokenize the input string by default nGrams is 2
	private int nGrams = 2;

	private double cutOff = 100;

	private String separator = ";";

	private HashMap<String, OWLClass> labelToOWLClass = null;

	private HashMap<String, List<String>> normalizedOntologyTerms = null;

	private OWLOntologyManager manager = null;

	private OWLDataFactory factory = null;

	private OWLFunction owlFunction = null;

	private OntologyService os = null;

	private HashMap<String, List<String>> ontologyTermAndDataItems = new HashMap<String, List<String>>();

	private HashMap<String, String> synonymToLabel = new HashMap<String, String>();

	private HashMap<String, List<String>> classLabelToSynonyms = new HashMap<String, List<String>>();

	private HashMap<String, List<String>> foundTermInDataDescription = new HashMap<String, List<String>>();

	private HashMap<String, HashMap<String, Double>> foundDescriptionByCutOff = new HashMap<String, HashMap<String, Double>>();

	private HashMap<String, String> synonymToClassLabel = new HashMap<String, String>();

	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();

	private OWLOntology localOntology = null;

	private static String regex = "[!?/]";

	private LevenshteinDistanceModel model = null;

	//Constructor
	public levenshteinDistance(int nGrams){

		this.nGrams = nGrams;

		this.model = new LevenshteinDistanceModel(this.nGrams);

		manager = OWLManager.createOWLOntologyManager();

		factory = manager.getOWLDataFactory();

		this.owlFunction = new OWLFunction();
	}

	public static void main(String args[]) throws OWLOntologyCreationException, OntologyServiceException{

		levenshteinDistance test = new levenshteinDistance(2);

		//Read in annotated ontology terms for the KORA model
		String fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/InputForOntologyBuild.xls";

		tableModel model = new tableModel(fileName, false);

		model.processingTable();

		List<String> descriptions = model.getHeaders();
		
		//Read in additional information for ontology terms
		test.readInOntologyTermFromLocalFile("/Users/pc_iverson/Desktop/Input/PredictionModel.owl");

		fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/PREVEND.xls";

		tableModel model_2 = new tableModel(fileName, true);

		model_2.setStartingRow(11);

		model_2.processingTable();

		HashMap<String, String> descriptionForVariable = model_2.getDescriptionForVariable("Veldnaam", "SPSS Omschrijving");

		System.out.println("Parsing the ontology");

		List<String> listOfAnnotationProperty = new ArrayList<String>();

		listOfAnnotationProperty.add("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN");

		test.parseOntology("/Users/pc_iverson/Desktop/Input/Thesaurus.owl", listOfAnnotationProperty);

		System.out.println("Ontology has been loaded");

		test.findOntologyTerms(descriptions, model, descriptionForVariable, 0.50);

		System.out.println();

		System.out.println("Parsing the ontology");

		System.out.println("Ontology has been loaded");

	}

	/**
	 * This method is used to find the exact matching between input terms and ontology terms. If there is any matching found,
	 * the record will be stored in mappingResult and ontologyTermAndDataItems variables. 
	 * 
	 * @param annotations
	 * @param model
	 * @param descriptionForVariable
	 * @param level
	 */
	public void findOntologyTerms(List<String> annotations, tableModel model, HashMap<String, String> descriptionForVariable, double cutOff){

		this.cutOff = cutOff;

		HashMap<String, String> levelAnnotation = model.getDescriptionForVariable(annotations.get(0), annotations.get(3));

		matchingByStringSimilarity(levelAnnotation, descriptionForVariable, cutOff);
	}

	public void outPutMappingDetails (HashMap<String, HashMap<String, Double>>mappingResultAndSimiarity, 
			HashMap<String, List<String>> originalQueryToExpanded, HashMap<String, String> dataItemNameToDescription) {

		System.out.println();

		for(String eachOriginalQuery : originalQueryToExpanded.keySet()){

			System.out.println("The original query is -------------- " + eachOriginalQuery);

			for(String key : originalQueryToExpanded.get(eachOriginalQuery)){

				System.out.println("The expanded query is \"" + key + "\"\t");

				key = key.toLowerCase();

				for(String dataItem : mappingResultAndSimiarity.get(key).keySet()){
					System.out.print("The dataItem is \"" + dataItem + "\"\t" + "The similarity is " + mappingResultAndSimiarity.get(key).get(dataItem));
					System.out.println();
				}

				if(foundDescriptionByCutOff.containsKey(key)){
					System.out.println("Mapping by cutoff " + this.cutOff);
					for(String dataItem : foundDescriptionByCutOff.get(key).keySet()){
						System.out.println("The dataItem mapped by cut off " + cutOff + " is \"" + dataItem + "\" and its description is \"" + foundDescriptionByCutOff.get(key).get(dataItem) + "\"");
					}
				}
				System.out.println();
			}

			System.out.println("Mapping by the pattern matching in the description!");
			if(foundTermInDataDescription.containsKey(eachOriginalQuery)){
				for(String dataItem : foundTermInDataDescription.get(eachOriginalQuery)){
					System.out.println("The dataItem mapped by pattern matching is \"" + dataItem + "\" and its description is \"" + dataItemNameToDescription.get(dataItem) + "\"");
				}
			}

			System.out.println();
		}
	}

	public List<String> queryExpansionByRelations(String query, OWLOntology localOntology){

		if(localOntology == null)
			localOntology = this.localOntology;

		List<String> allChildrenAndParents = new ArrayList<String>();

		if(labelToOWLClass.containsKey(query.toLowerCase())){

			OWLClass queryClass = labelToOWLClass.get(query.toLowerCase());

			List<String> allChildren = owlFunction.getAllChildren(localOntology, queryClass, new ArrayList<String>());

			List<String> allParents = owlFunction.getAllParents(localOntology, queryClass, new ArrayList<String>());

			allChildrenAndParents.addAll(allChildren);

			allChildrenAndParents.addAll(allParents);
		}

		return allChildrenAndParents;
	}

	public HashMap<String, String> queryExpansionBySynonyms(List<String> queries, HashMap<String, List<String>> classLabelToSynonyms){

		HashMap<String, String> expanadedQueryToOriginalLabel = new HashMap<String, String>();

		List<String> newQueries = new ArrayList<String>();

		for(String eachQuery : queries){

			String definitions[] = eachQuery.split(separator);

			if(definitions.length > 1){

				List<String> firstTokens = classLabelToSynonyms.get(definitions[0]);

				List<String> secondTokens = classLabelToSynonyms.get(definitions[1]);

				if(firstTokens != null && secondTokens != null){

					if(firstTokens.size() > 0 || secondTokens.size() > 0){
						firstTokens.add(definitions[0]);
						secondTokens.add(definitions[1]);
						for(String firstToken : firstTokens){

							for(String secondToken : secondTokens){
								if(!eachQuery.equalsIgnoreCase(firstToken + separator + secondToken) && !eachQuery.equalsIgnoreCase(secondToken + separator + firstToken)){
									newQueries.add(firstToken + " " + secondToken);
									expanadedQueryToOriginalLabel.put(firstToken + " " + secondToken, eachQuery);
								}
							}
						}
					}
				}
			}
			newQueries.add(eachQuery.replaceAll(separator, " "));
			queries = newQueries;
		}

		return expanadedQueryToOriginalLabel;
	}

	public void matchingByStringSimilarity(HashMap<String, String> dataDescription, HashMap<String, String> descriptionForVariable, double cutOff){

		HashMap<String, HashMap<String, Double>> mappingResultAndSimiarity = new HashMap<String, HashMap<String, Double>>();

		HashMap<String, List<String>> originalQueryToExpanded = new HashMap<String, List<String>>();

		for(String key : dataDescription.keySet()){

			if(!dataDescription.get(key).equals("")){

				List<String> expansion = expandedQueries.get(key.toLowerCase());

				List<String> queries = new ArrayList<String>();

				if(expansion == null){
					expansion = new ArrayList<String>();
					expansion.add(dataDescription.get(key));
				}

				for(String eachExpansion : expansion){

					String definitions[] = eachExpansion.split(separator);

					for(int i = 0; i < definitions.length; i++){
						if(!queries.contains(definitions[i].toLowerCase()))
							queries.add(definitions[i].toLowerCase());
					}

					if(!queries.contains(eachExpansion.toLowerCase()))
						queries.add(eachExpansion.toLowerCase());
				}

				HashMap<String, String> expanadedQueryToOriginalLabel = queryExpansionBySynonyms(queries, classLabelToSynonyms);

				for(String eachExpandedQuery : expanadedQueryToOriginalLabel.keySet()){
					queries.add(eachExpandedQuery);
				}

				for(String eachQuery : queries){

					eachQuery = eachQuery.replaceAll(separator, " ");

					if(originalQueryToExpanded.containsKey(key)){
						List<String> temp = originalQueryToExpanded.get(key);
						if(!temp.contains(eachQuery)){
							temp.add(eachQuery);
						}
						originalQueryToExpanded.put(key, temp);
					}else{
						List<String> temp = new ArrayList<String>();
						temp.add(eachQuery);
						originalQueryToExpanded.put(key, temp);
					}

					List<String> listOfSynonyms = new ArrayList<String>();

					if(classLabelToSynonyms.containsKey(eachQuery)){
						listOfSynonyms = classLabelToSynonyms.get(eachQuery);
					}

					double maxSimilarity = 0;

					String matchedDataItem = "";

					for(String dataItem : descriptionForVariable.keySet()){

						double similarity = model.stringMatching(eachQuery, dataItem, true);

						if(similarity > maxSimilarity){
							maxSimilarity = similarity;
							matchedDataItem = descriptionForVariable.get(dataItem);
						}
						if(similarity > cutOff*100){

							HashMap<String, Double> temp = new HashMap<String, Double>();

							if(foundDescriptionByCutOff.containsKey(eachQuery)){
								temp = foundDescriptionByCutOff.get(eachQuery);
							}
							if(temp.containsKey(matchedDataItem)){
								if(temp.get(matchedDataItem) < maxSimilarity)
									temp.put(matchedDataItem, maxSimilarity);
							}else{
								temp.put(matchedDataItem, maxSimilarity);
							}
							foundDescriptionByCutOff.put(eachQuery, temp);
						}
					}

					double synonymSimilarity = 0;

					String synonymMatchedDataItem = "";

					for(String eachSynonym : listOfSynonyms){

						for(String dataItem : descriptionForVariable.keySet()){
							
							double similarity = model.stringMatching(eachSynonym, dataItem, true);

							if(similarity > synonymSimilarity){
								synonymSimilarity = similarity;
								synonymMatchedDataItem = descriptionForVariable.get(dataItem);
							}
							if(similarity > cutOff*100){
								//addingNewMatchedItem(foundDescriptionByCutOff, eachQuery, dataItem);
								HashMap<String, Double> temp = new HashMap<String, Double>();

								if(foundDescriptionByCutOff.containsKey(eachQuery)){
									temp = foundDescriptionByCutOff.get(eachQuery);
								}
								if(temp.containsKey(matchedDataItem)){
									if(temp.get(matchedDataItem) < maxSimilarity)
										temp.put(matchedDataItem, maxSimilarity);
								}else{
									temp.put(matchedDataItem, maxSimilarity);
								}
								foundDescriptionByCutOff.put(eachQuery, temp);
							}
						}
					}				

					if(synonymSimilarity > maxSimilarity){
						maxSimilarity = synonymSimilarity;
						matchedDataItem = synonymMatchedDataItem;
					}

					HashMap<String, Double> temp = new HashMap<String, Double>();

					if(mappingResultAndSimiarity.containsKey(eachQuery)){
						temp = mappingResultAndSimiarity.get(eachQuery);
					}
					if(temp.containsKey(matchedDataItem)){
						if(temp.get(matchedDataItem) < maxSimilarity)
							temp.put(matchedDataItem, maxSimilarity);
					}else{
						temp.put(matchedDataItem, maxSimilarity);
					}

					mappingResultAndSimiarity.put(eachQuery, temp);
				}
			}
			System.out.println();
		}

		matchingByregularExpression(originalQueryToExpanded, descriptionForVariable);

		for(String dataItem : ontologyTermAndDataItems.keySet()){
			System.out.println("The data item is " + dataItem);
			for(String eachMapping : ontologyTermAndDataItems.get(dataItem)){
				System.out.println("The ontology term is " + eachMapping);
			}
			System.out.println();
		}
		outPutMappingDetails(mappingResultAndSimiarity, originalQueryToExpanded, descriptionForVariable);

	}

	public void matchingByregularExpression(HashMap<String, List<String>> levelAnnotation, HashMap<String, String> descriptionForVariable){

		for(String originalQuery : levelAnnotation.keySet()){

			for(String key : levelAnnotation.get(originalQuery)){

				//The input has to be non-empty and the input has not been annotated
				if(!key.equals("")){

					String definitions[] = key.split(separator);

					List<String> searchTokens = arrayToList(definitions);

					searchTokens.add(key.replaceAll(separator, " "));

					for(String eachTerm : searchTokens){

						eachTerm = eachTerm.trim();

						for(String dataItem : descriptionForVariable.keySet()){

							String description = descriptionForVariable.get(dataItem).toLowerCase();
							//							if(description.matches("^" + eachTerm.toLowerCase() + "[\\W].*")){
							//								addingNewMatchedItem(foundTermInDataDescription, originalQuery, dataItem);
							//							}else if(description.matches(".*[\\W]" + eachTerm.toLowerCase() + "$")){
							//								addingNewMatchedItem(foundTermInDataDescription, originalQuery, dataItem);
							//							}else if(description.matches(".*[\\W]" + eachTerm.toLowerCase() + "[\\W].*")){
							//								addingNewMatchedItem(foundTermInDataDescription, originalQuery, dataItem);
							//							}else if(description.equalsIgnoreCase(eachTerm)){
							//								addingNewMatchedItem(foundTermInDataDescription, originalQuery, dataItem);
							//							}
							if(description.matches(".*" + eachTerm.toLowerCase() + ".*")){
								addingNewMatchedItem(foundTermInDataDescription, originalQuery, dataItem);
							}
						}
					}

				}
			}
		}
	}
	public void addingNewMatchedItem(HashMap<String, List<String>> ontologyTermAndDataItems, String key, String dataItem){

		if(!ontologyTermAndDataItems.containsKey(key)){

			List<String> dataItems = new ArrayList<String>();
			if(!dataItems.contains(dataItem)){
				dataItems.add(dataItem);
				ontologyTermAndDataItems.put(key, dataItems);
			}
		}else{

			List<String> dataItems = ontologyTermAndDataItems.get(key);

			if(!dataItems.contains(dataItem))
				dataItems.add(dataItem);
			ontologyTermAndDataItems.put(key, dataItems);
		}
	}

	public int getnGrams() {
		return nGrams;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List<String> arrayToList(String[] array){

		List<String> list = new ArrayList<String>();

		for(int i = 0; i < array.length; i++){
			if(!list.contains(array[i]))
				list.add(array[i]);
		}

		return list;
	}

	public HashMap<String, List<String>> getNormalizedOntologyTerms() {
		return normalizedOntologyTerms;
	}

	public void setOntologyService (List<String> ontologyFiles) throws OntologyServiceException{

		List<FileOntologyService> services = new ArrayList<FileOntologyService>();

		for(String ontologyName : ontologyFiles){
			File ontologyFile = new File(ontologyName);
			services.add(new FileOntologyService(ontologyFile.toURI(), ontologyName));
		}

		os = CompositeDecorator.getService(services);
	}

	public void ontoCatSearching(List<String> ontologies) throws OntologyServiceException{

		this.setOntologyService(ontologies);

		List<String> allTerms = new ArrayList<String>();

		for(String ontologyName : ontologies){

			for(OntologyTerm ot : os.getAllTerms(ontologyName)){

				for(String eachSynonym : os.getSynonyms(ot)){
					synonymToLabel.put(eachSynonym, ot.getLabel());
				}
				allTerms.add(ot.getLabel());
				allTerms.addAll(os.getSynonyms(ot));
			}
		}
	}

	public void readInOntologyTermFromLocalFile(String ontologyName) throws OWLOntologyCreationException{

		OWLOntology ontologyModel  = manager.loadOntologyFromOntologyDocument(new File(ontologyName));

		List<String> listOfAnnotationProperty = new ArrayList<String>();

		listOfAnnotationProperty.add(ontologyModel.getOntologyID().getOntologyIRI().toString() + "#additionalInfo");

		OWLFunction owlFunctionLocal = new OWLFunction(factory, ontologyModel);

		owlFunctionLocal.labelMapURI(listOfAnnotationProperty);

		this.expandedQueries = owlFunctionLocal.getExpandedQueries();
	}

	/**
	 * This is method is to load the ontology file from local system and create
	 * a hash table where the label is key and owlClass is the content
	 * 
	 * @param ontologyFilePath
	 * @throws OWLOntologyCreationException 
	 */
	public void parseOntology(String ontologyFilePath, List<String> annotationProperty) throws OWLOntologyCreationException{

		localOntology  = manager.loadOntologyFromOntologyDocument(new File(ontologyFilePath));

		owlFunction = new OWLFunction(factory, localOntology);

		labelToOWLClass = owlFunction.labelMapURI(annotationProperty);

		classLabelToSynonyms = owlFunction.getClassLabelToSynonyms();

		synonymToClassLabel = owlFunction.getSynonymToClassLabel();

		List<String> listOfOntologyTerms = new ArrayList<String>();

		listOfOntologyTerms.addAll(labelToOWLClass.keySet());

		System.out.println("Ontology has been loaded and stored in the hash table");
	}

	public boolean searchForOntologyTerm(String inputString, String ontologyTermTokens){

		String[] inputStringTokens = inputString.split(" ");

		for(int i = 0; i < inputStringTokens.length; i++){
			if(inputStringTokens[i].equalsIgnoreCase(ontologyTermTokens))
				return true;
		}
		return false;
	}

}
