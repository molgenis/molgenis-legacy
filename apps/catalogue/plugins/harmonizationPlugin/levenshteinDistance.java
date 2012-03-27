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

	private HashMap<String, Boolean> mappingResult = new HashMap<String, Boolean>();

	private HashMap<String, List<String>> ontologyTermAndDataItems = new HashMap<String, List<String>>();

	private HashMap<String, String> synonymToLabel = new HashMap<String, String>();

	private HashMap<String, List<String>> classLabelToSynonyms = new HashMap<String, List<String>>();

	private HashMap<String, List<String>> foundTermInDataDescription = new HashMap<String, List<String>>();

	private HashMap<String, List<String>> foundDescriptionByCutOff = new HashMap<String, List<String>>();

	private HashMap<String, String> synonymToClassLabel = new HashMap<String, String>();

	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();

	private OWLOntology localOntology = null;

	private static String regex = "[!?/]";

	public static final String[] STOP_WORDS = {"a","you","about","above","after","again",
		"against","all","am","an","and","any","are","aren't","as","at","be","because","been",
		"before","being","below","between","both","but","by","can't","cannot","could","couldn't",
		"did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from",
		"further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her",
		"here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've",
		"if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my",
		"myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours "," ourselves",
		"out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such",
		"than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they",
		"they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was",
		"wasn't","we","we'd","we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's",
		"which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're",
		"you've","your","yours","yourself","yourselves"};

	public static List<String> STOPWORDSLIST = new ArrayList<String>();

	//Constructor
	public levenshteinDistance(int nGrams){

		this.nGrams = nGrams;

		manager = OWLManager.createOWLOntologyManager();

		factory = manager.getOWLDataFactory();

		this.owlFunction = new OWLFunction();

		for(int i = 0; i <STOP_WORDS.length; i++){
			STOPWORDSLIST.add(STOP_WORDS[i]);
		}
	}

	public static void main(String args[]) throws OWLOntologyCreationException, OntologyServiceException{

		levenshteinDistance test = new levenshteinDistance(2);

		//Read in annotated ontology terms for the KORA model
		String fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/InputForOntologyBuild.xls";

		tableModel model = new tableModel(fileName, false);

		model.processingTable();

		//Read in additional information for ontology terms
		test.readInOntologyTermFromLocalFile("/Users/pc_iverson/Desktop/Input/PredictionModel.owl");

		List<String> descriptions = new ArrayList<String>();

		descriptions.add("Original terms");

		descriptions.add("Ontology terms");

		descriptions.add("Definition");

		descriptions.add("Building blocks");

		//fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/PREVEND.xls";

		fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/LifeLines_Data_itmes.xls";

		tableModel model_2 = new tableModel(fileName, true);

		//model_2.setStartingRow(11);

		model_2.processingTable();

		HashMap<String, String> descriptionForVariable = model_2.getDescriptionForVariable("Data", "Description");

		//HashMap<String, String> descriptionForVariable = model_2.getDescriptionForVariable("Veldnaam", "SPSS Omschrijving");

		System.out.println("Parsing the ontology");

		List<String> listOfAnnotationProperty = new ArrayList<String>();

		listOfAnnotationProperty.add("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN");

		test.parseOntology("/Users/pc_iverson/Desktop/Input/Thesaurus.owl", listOfAnnotationProperty);

		System.out.println("Ontology has been loaded");

		//System.out.println("Annotating the description of data items");

		//test.annotateTextWithOntologyTerm(descriptionForVariable);

		//System.out.println("The description has been annotated");

		//test.findOntologyTerms(descriptions, model, descriptionForVariable, 1);

		//test.findOntologyTerms(descriptions, model, descriptionForVariable, 2);

		test.findOntologyTerms(descriptions, model, descriptionForVariable, 0.50);

		System.out.println();

		System.out.println("Parsing the ontology");

		//List<String> ontologies = new ArrayList<String>();

		//ontologies.add("/Users/pc_iverson/Desktop/Input/Thesaurus.owl");

		//test.ontoCatSearching(ontologies);

		System.out.println("Ontology has been loaded");

	}

	public void annotateTextWithOntologyTerm(HashMap<String, String> descriptionForVariable){

		for(String dataItem : descriptionForVariable.keySet()){

			String description = descriptionForVariable.get(dataItem).replaceAll(regex, " ");
			List<String> list = new ArrayList<String>();
			list.add(description);
			findMatch(normalizedOntologyTerms, list);
		}

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

		searchingForOntologyTermInDescription(levelAnnotation, descriptionForVariable);

		searchingForOntologyTermByStringMatching(levelAnnotation, descriptionForVariable, cutOff);
	}

	public void outPutMapping(HashMap<String, HashMap<String, Double>>mappingResultAndSimiarity, HashMap<String, String> dataItemNameToDescription) {

		System.out.println();

		for(String key : mappingResultAndSimiarity.keySet()){

			System.out.println("The parameter is \"" + key + "\"\t");

			for(String dataItem : mappingResultAndSimiarity.get(key).keySet()){
				System.out.print("The dataItem is \"" + dataItem + "\"\t" + "The similarity is " + mappingResultAndSimiarity.get(key).get(dataItem));
				System.out.println();
			}
			System.out.println("Mapping by cutoff " + this.cutOff);
			if(foundDescriptionByCutOff.containsKey(key)){
				for(String dataItem : foundDescriptionByCutOff.get(key)){
					System.out.println("The dataItem mapped by cut off " + cutOff + " is \"" + dataItem + "\" and its description is \"" + dataItemNameToDescription.get(dataItem) + "\"");
				}
			}
			System.out.println();

			System.out.println("Mapping by the pattern matching in the description!");
			if(foundTermInDataDescription.containsKey(key)){
				for(String dataItem : foundTermInDataDescription.get(key)){
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

	public void searchingForOntologyTermByStringMatching(HashMap<String, String> levelAnnotation, HashMap<String, String> descriptionForVariable, double cutOff){

		HashMap<String, HashMap<String, Double>> mappingResultAndSimiarity = new HashMap<String, HashMap<String, Double>>();

		HashMap<String, List<String>> originalQueryToExpanded = new HashMap<String, List<String>>();

		for(String key : levelAnnotation.keySet()){

			if(!levelAnnotation.get(key).equals("")){

				List<String> expansion = expandedQueries.get(key.toLowerCase());

				List<String> queries = new ArrayList<String>();

				if(expansion == null){
					expansion = new ArrayList<String>();
					expansion.add(levelAnnotation.get(key));
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

				String matchedQuery = "";

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
						originalQueryToExpanded.put(key, temp);
					}

					List<String> listOfSynonyms = new ArrayList<String>();

					if(classLabelToSynonyms.containsKey(eachQuery)){
						listOfSynonyms = classLabelToSynonyms.get(eachQuery);
					}

					double maxSimilarity = 0;

					String matchedDataItem = "";

					List<String> tokens = createNGrams(eachQuery.toLowerCase().trim(), " ", nGrams, false);

					for(String dataItem : descriptionForVariable.keySet()){

						List<String> dataItemTokens = createNGrams(descriptionForVariable.get(dataItem).toLowerCase().trim(), " ", nGrams, true);

						double similarity = calculateScore(dataItemTokens, tokens);

						if(similarity > maxSimilarity){

							maxSimilarity = similarity;
							matchedDataItem = descriptionForVariable.get(dataItem);
						}
						if(similarity > cutOff*100){
							addingNewMatchedItem(foundDescriptionByCutOff, key, dataItem);
						}
					}

					double synonymSimilarity = 0;

					String synonymMatchedDataItem = "";

					for(String eachSynonym : listOfSynonyms){

						tokens = createNGrams(eachSynonym.toLowerCase().trim(), " ", nGrams, false);

						for(String dataItem : descriptionForVariable.keySet()){

							List<String> dataItemTokens = createNGrams(descriptionForVariable.get(dataItem).toLowerCase().trim(), " ", nGrams, true);

							double similarity = calculateScore(dataItemTokens, tokens);

							if(similarity > synonymSimilarity){
								synonymSimilarity = similarity;
								synonymMatchedDataItem = descriptionForVariable.get(dataItem);
							}
							if(similarity > cutOff*100){
								addingNewMatchedItem(foundDescriptionByCutOff, key, dataItem);
							}
						}
					}				

					if(synonymSimilarity > maxSimilarity){
						maxSimilarity = synonymSimilarity;
						matchedDataItem = synonymMatchedDataItem;
					}

					HashMap<String, Double> temp = new HashMap<String, Double>();

					if(mappingResultAndSimiarity.containsKey(key)){
						temp = mappingResultAndSimiarity.get(key);
					}
					if(temp.containsKey(matchedDataItem)){
						if(temp.get(matchedDataItem) < maxSimilarity)
							temp.put(matchedDataItem, maxSimilarity);
					}else{
						temp.put(matchedDataItem, maxSimilarity);
					}

					mappingResultAndSimiarity.put(key, temp);
				}
			}
			System.out.println();
		}

		for(String dataItem : ontologyTermAndDataItems.keySet()){
			System.out.println("The data item is " + dataItem);
			for(String eachMapping : ontologyTermAndDataItems.get(dataItem)){
				System.out.println("The ontology term is " + eachMapping);
			}
			System.out.println();
		}
		outPutMapping(mappingResultAndSimiarity, descriptionForVariable);
	}

	public void searchingForOntologyTermInDescription(HashMap<String, String> levelAnnotation, HashMap<String, String> descriptionForVariable){

		for(String key : levelAnnotation.keySet()){
			//The input has to be non-empty and the input has not been annotated
			if(!levelAnnotation.get(key).equals("")){

				String definitions[] = levelAnnotation.get(key).split(separator);

				List<String> searchTokens = arrayToList(definitions);

				searchTokens.add(levelAnnotation.get(key).replaceAll(separator, " "));

				for(String eachTerm : searchTokens){

					eachTerm = eachTerm.trim();

					for(String dataItem : descriptionForVariable.keySet()){

						String description = descriptionForVariable.get(dataItem).toLowerCase();
						if(description.matches("^" + eachTerm.toLowerCase() + "[\\W].*")){
							addingNewMatchedItem(foundTermInDataDescription, key, dataItem);
						}else if(description.matches(".*[\\W]" + eachTerm.toLowerCase() + "$")){
							addingNewMatchedItem(foundTermInDataDescription, key, dataItem);
						}else if(description.matches(".*[\\W]" + eachTerm.toLowerCase() + "[\\W].*")){
							addingNewMatchedItem(foundTermInDataDescription, key, dataItem);
						}else if(description.equalsIgnoreCase(eachTerm)){
							addingNewMatchedItem(foundTermInDataDescription, key, dataItem);
						}
					}
				}

			}
		}
	}

	public void firstLevelAnnotation(HashMap<String, String> levelAnnotation, HashMap<String, String> descriptionForVariable){

		for(String key : levelAnnotation.keySet()){

			mappingResult.put(key, false);

			if(!levelAnnotation.get(key).equals("")){

				for(String dataItem : descriptionForVariable.keySet()){

					String description = descriptionForVariable.get(dataItem);

					if (description.equalsIgnoreCase(levelAnnotation.get(key))){

						mappingResult.put(key, true);
						addingNewMatchedItem(ontologyTermAndDataItems, key, dataItem);
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

		normalizedOntologyTerms = this.createNGrams(allTerms, " ", this.nGrams, false);
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

		//Levenshtein TODO
		normalizedOntologyTerms = createNGrams(listOfOntologyTerms, " ", nGrams, false);

		//Method from BBMRI plugin! All the possibilities of string term
		//normalizedOntologyTerms = createNGrams(listOfOntologyTerms);

		System.out.println("Ontology has been loaded and stored in the hash table");
	}


	public List<String> removeStopWords(String[] listOfWords, List<String> STOPWORDSLIST){

		List<String> removedStopWordsList = new ArrayList<String>();

		for(int index = 0; index < listOfWords.length; index++){

			if(STOPWORDSLIST == null){
				removedStopWordsList.add(listOfWords[index]);
			}else if(!STOPWORDSLIST.contains(listOfWords[index])){
				removedStopWordsList.add(listOfWords[index]);
			}
		}

		return removedStopWordsList;
	}


	/**
	 * //create n-grams tokens of the string.
	 * @param inputString
	 * @param nGrams
	 * @return
	 */
	public List<String> createNGrams(String eachString, String separator, int nGrams, boolean stopWords){

		String [] singleWords = eachString.split(separator);

		List<String> removedStopWordsList = new ArrayList<String>();

		List<String> tokens = new ArrayList<String>();

		if(stopWords == true){
			removedStopWordsList = removeStopWords(singleWords, STOPWORDSLIST);
		}else{
			removedStopWordsList = removeStopWords(singleWords, null);
		}

		//Padding the string
		for(String singleWord : removedStopWordsList){
			//TODO what if there is overlapping between different words such diebetes mellitus. 
			//The s$ will be the produced from two words. 
			singleWord = singleWord.toLowerCase();
			singleWord = "^" + singleWord;
			singleWord = singleWord + "$";

			for(int i = 0; i < singleWord.length(); i++){

				if(i + nGrams < singleWord.length()){
					tokens.add(singleWord.substring(i, i + nGrams));
				}else{
					if(!tokens.contains(singleWord.substring(singleWord.length() - 2))){
						tokens.add(singleWord.substring(singleWord.length() - 2).toLowerCase());
					}
				}
			}
		}
		return tokens;
	}

	/**
	 * //create n-grams tokens of the string.
	 * @param inputString
	 * @param nGrams
	 * @return
	 */
	public HashMap<String, List<String>> createNGrams(List<String> inputString, String separator, int nGrams, boolean stopWords){

		HashMap<String, List<String>> normalizedInputString = new HashMap<String, List<String>>();

		for(String eachString : inputString){

			String [] singleWords = eachString.split(separator);

			List<String> tokens = new ArrayList<String>();

			List<String> removedStopWordsList = new ArrayList<String>();

			if(stopWords == true){
				removedStopWordsList = removeStopWords(singleWords, STOPWORDSLIST);
			}else{
				removedStopWordsList = removeStopWords(singleWords, null);
			}

			//Padding the string
			for(String singleWord : removedStopWordsList){
				//TODO what if there is overlapping between different words such diebetes mellitus. 
				//The s$ will be the produced from two words. 
				singleWord = singleWord.toLowerCase();
				singleWord = "^" + singleWord;
				singleWord = singleWord + "$";

				for(int i = 0; i < singleWord.length(); i++){

					if(i + nGrams < singleWord.length()){
						tokens.add(singleWord.substring(i, i + nGrams));
					}else{
						if(!tokens.contains(singleWord.substring(singleWord.length() - 2))){
							tokens.add(singleWord.substring(singleWord.length() - 2).toLowerCase());
						}
					}
				}
			}

			normalizedInputString.put(eachString, tokens);
		}

		return normalizedInputString;
	}

	/**
	 * This method is used to match the input string with ontology terms and calculate the score.
	 * 
	 * @param ontologyTerms
	 * @param listOfInputString
	 * @return 
	 */
	public HashMap<String, HashMap<String, Double>> findMatch(HashMap<String, List<String>> ontologyTerms, List<String> listOfInputString){

		//Variable to store the mapping result
		HashMap<String, HashMap<String, Double>> mappingResult = new HashMap<String, HashMap<String, Double>>();
		//HashMap<String, List<String>> annotatedByOntologyTerm = new HashMap<String, List<String>>();
		//Iterate the string
		for(String stringToMatch : listOfInputString){

			List<String> eachString = new ArrayList<String>();

			eachString.add(stringToMatch);

			//Levenshtein TODO
			HashMap<String, List<String>> temp = createNGrams(eachString, " " ,nGrams, true);

			//Method from BBMRI plugin! All the possibilities of string term
			//HashMap<String, List<String>> temp = createNGrams(eachString);
			String matchedOntologyTerm = null;

			double maxSimilarity = 0;

			//boolean ontologyTermFound = false;

			for(String eachOntologyTerm : ontologyTerms.keySet()){

				//				ontologyTermFound = searchForOntologyTerm(stringToMatch, eachOntologyTerm);
				//
				//				if(ontologyTermFound == true){
				//					if(!annotatedByOntologyTerm.containsKey(stringToMatch)){
				//						List<String> listOfOntologyTerms = new ArrayList<String>();
				//						listOfOntologyTerms.add(eachOntologyTerm);
				//						annotatedByOntologyTerm.put(stringToMatch, listOfOntologyTerms);
				//					}else{
				//						List<String> listOfOntologyTerms = annotatedByOntologyTerm.get(stringToMatch);
				//						listOfOntologyTerms.add(eachOntologyTerm);
				//						annotatedByOntologyTerm.put(stringToMatch, listOfOntologyTerms);
				//					}
				//				}

				double similarity = calculateScore(temp.get(stringToMatch), 
						ontologyTerms.get(eachOntologyTerm));
				if(similarity > maxSimilarity){
					maxSimilarity = similarity;
					matchedOntologyTerm = eachOntologyTerm;
				}
			}

			List<String> removedList = removeStopWords(stringToMatch.split(" "), STOPWORDSLIST);

			String newString = "";

			for(String each : removedList){
				newString += each + " ";
			}

			System.out.println("The original string is " + stringToMatch  + "! The string without stop words is " + newString +
					" The matched ontology term is " + matchedOntologyTerm + ". The similarity is " + maxSimilarity);
			System.out.println();
			HashMap<String, Double> matchedTermAndSimilarity = new HashMap<String, Double>();
			matchedTermAndSimilarity.put(matchedOntologyTerm, maxSimilarity);
			mappingResult.put(stringToMatch, matchedTermAndSimilarity);
		}

		//		for(String key : annotatedByOntologyTerm.keySet()){
		//			
		//			System.out.println("The original string is " + key);
		//			System.out.print("The ontology terms are ");
		//			for(String eachOntologyTerm : annotatedByOntologyTerm.get(key)){
		//				System.out.print(eachOntologyTerm + "\t");
		//			}
		//			System.out.println();
		//			System.out.println();
		//		}

		return mappingResult;
	}

	/**
	 * Calculate the levenshtein distance
	 * @param inputStringTokens
	 * @param ontologyTermTokens
	 * @return
	 */
	public double calculateScore(List<String> inputStringTokens, List<String> ontologyTermTokens){

		int matchedTokens = 0;
		double similarity = 0;

		for(String eachToken : inputStringTokens){
			if(ontologyTermTokens.contains(eachToken)){
				matchedTokens++;
			}
		}
		double totalToken = Math.max(inputStringTokens.size(), ontologyTermTokens.size());
		similarity = matchedTokens/totalToken*100;
		DecimalFormat df = new DecimalFormat("#0.000");
		return Double.parseDouble(df.format(similarity));
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
