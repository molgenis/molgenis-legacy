package plugins.harmonizationPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;

public class levenshteinDistance {

	//Choose n-grams to tokenize the input string by default nGrams is 2
	private int nGrams = 2;

	private String separator = ";";

	private HashMap<String, OWLClass> labelToOWLClass = null;

	private HashMap<String, List<String>> normalizedOntologyTerms = null;

	private OWLOntologyManager manager = null;

	private OWLDataFactory factory = null;

	private OWLFunction owlFunction = null;

	private OntologyService os = null;

	private HashMap<String, Boolean> mappingResult = new HashMap<String, Boolean>();

	private HashMap<String, List<String>> ontologyTermAndDataItems = new HashMap<String, List<String>>();

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

		//		String fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/InputForOntologyBuild.xls";
		//
		//		tableModel model = new tableModel(fileName);
		//
		//		List<String> descriptions = new ArrayList<String>();
		//
		//		descriptions.add("Original terms");
		//
		//		descriptions.add("Ontology terms");
		//
		//		descriptions.add("Definition");
		//
		//		descriptions.add("Building blocks");
		//
		//		fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/LifeLines_Data_itmes.xls";
		//
		//		tableModel model_2 = new tableModel(fileName);
		//
		//		HashMap<String, String> descriptionForVariable = model_2.getDescriptionForVariable("Data", "Description");
		//
		//		test.findOntologyTerms(descriptions, model, descriptionForVariable, 1);
		//
		//		test.findOntologyTerms(descriptions, model, descriptionForVariable, 2);
		//
		//		test.findOntologyTerms(descriptions, model, descriptionForVariable, 3);
		//
		//		System.out.println();

		System.out.println("Parsing the ontology");

		test.parseOntology("/Users/pc_iverson/Desktop/Input/Thesaurus.owl");

		System.out.println("Ontology has been loaded");

		String fileName = "/Users/pc_iverson/Desktop/Ontology_term_pilot/LifeLines_Data_itmes.xls";

		tableModel model_2 = new tableModel(fileName);

		HashMap<String, String> descriptionForVariable = model_2.getDescriptionForVariable("Data", "Description");

		test.annotateTextWithOntologyTerm(descriptionForVariable);
		//test.findMatch(annotatedTerms, tokens);
	}


	public void annotateTextWithOntologyTerm(HashMap<String, String> descriptionForVariable){

		for(String dataItem : descriptionForVariable.keySet()){

			String description = descriptionForVariable.get(dataItem);
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
	public void findOntologyTerms(List<String> annotations, tableModel model, HashMap<String, String> descriptionForVariable, int level){

		HashMap<String, String> levelAnnotation = model.getDescriptionForVariable(annotations.get(0), annotations.get(level));

		if(level == 1){
			firstLevelAnnotation(levelAnnotation, descriptionForVariable);
		}else if(level == 2){
			secondLevelAnnotation(levelAnnotation, descriptionForVariable);
		}else if(level == 3){
			thirdLevelAnnotation(levelAnnotation, descriptionForVariable);
		}
	}

	public void outPutMapping(HashMap<String, HashMap<String, Double>> mappingResultAndSimiarity){

		for(String key : mappingResultAndSimiarity.keySet()){

			System.out.println("The parameter is " + key + "\t");

			for(String dataItem : mappingResultAndSimiarity.get(key).keySet()){
				System.out.print("The dataItem is " + dataItem + "\t" + "The similarity is " + mappingResultAndSimiarity.get(key).get(dataItem));
				System.out.println();
			}

			System.out.println();

		}
	}

	public void thirdLevelAnnotation(HashMap<String, String> levelAnnotation, HashMap<String, String> descriptionForVariable){

		HashMap<String, HashMap<String, Double>> mappingResultAndSimiarity = new HashMap<String, HashMap<String, Double>>();

		for(String key : levelAnnotation.keySet()){

			if(!levelAnnotation.get(key).equals("") && !mappingResult.get(key)){

				String definitions[] = levelAnnotation.get(key).split(separator);

				for(int i = 0; i < definitions.length; i++){

					double maxSimilarity = 0;

					String matchedDataItem = "";

					List<String> tokens = createNGrams(definitions[i].toLowerCase().trim(), " ", nGrams, false);

					for(String dataItem : descriptionForVariable.keySet()){

						List<String> dataItemTokens = createNGrams(descriptionForVariable.get(dataItem).toLowerCase().trim(), " ", nGrams, true);

						double similarity = calculateScore(dataItemTokens, tokens);

						if(similarity > maxSimilarity){
							maxSimilarity = similarity;
							matchedDataItem = descriptionForVariable.get(dataItem);
						}
					}

					HashMap<String, Double> temp = new HashMap<String, Double>();

					if(mappingResultAndSimiarity.containsKey(key)){
						temp = mappingResultAndSimiarity.get(key);
					}

					temp.put(matchedDataItem, maxSimilarity);

					mappingResultAndSimiarity.put(key, temp);
				}
			}
		}

		outPutMapping(mappingResultAndSimiarity);
	}

	public void secondLevelAnnotation(HashMap<String, String> levelAnnotation, HashMap<String, String> descriptionForVariable){

		for(String key : levelAnnotation.keySet()){
			//The input has to be non-empty and the input has not been annotated
			if(!levelAnnotation.get(key).equals("") && !mappingResult.get(key)){

				String definitions[] = levelAnnotation.get(key).split(separator);

				boolean definitionExisted = true;

				for(int i = 0; i < definitions.length; i++){

					String eachTerm = definitions[i].trim();

					for(String dataItem : descriptionForVariable.keySet()){

						String description = descriptionForVariable.get(dataItem);

						if (description.equalsIgnoreCase(eachTerm)){
							addingNewMatchedItem(key, dataItem);
						}else{
							definitionExisted = false;
						}
					}
				}

				mappingResult.put(key, definitionExisted);
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
						addingNewMatchedItem(key, dataItem);
					}
				}
			}
		}
	}

	public void addingNewMatchedItem(String key, String dataItem){

		if(!ontologyTermAndDataItems.containsKey(key)){

			List<String> dataItems = new ArrayList<String>();
			dataItems.add(dataItem);
			ontologyTermAndDataItems.put(key, dataItems);
		}else{

			List<String> dataItems = ontologyTermAndDataItems.get(key);

			if(!dataItems.contains(key))
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

	public HashMap<String, List<String>> getNormalizedOntologyTerms() {
		return normalizedOntologyTerms;
	}

	public void setOntologyService (String ontologyFileName) throws OntologyServiceException{

		File ontologyFile = new File(ontologyFileName);

		this.os = new FileOntologyService(ontologyFile.toURI());
	}

	public HashMap<String, List<String>> extensiveSearching (List<String> intialQueries) throws OntologyServiceException{

		HashMap <String, List<String>> stringToTokens = new HashMap<String, List<String>>();
		//TODO not only search for the labels in the ontologies but also the synonyms. But the 
		//annotation property of synonyms had the different name in different ontologies.
		//For example synonyms is called FUL_SYN in NCI while is called alternative_term
		//in Experiment factor ontology. So we start with searching for labels only!

		//TODO Ask Despoina how to do it! Index the ontology terms in the hash table

		for(String eachQuery : intialQueries){

			String[] eachWords = eachQuery.split(" ");

			List<String> allTokens = new ArrayList<String>();

			for(int i = 0; i < eachWords.length; i++){
				//TODO split by space symbol. This needs to be changed later. Create 
				//n-possibilities combination of queries. 
				for(OntologyTerm term : os.getAllTerms(eachWords[i])){
					System.out.println(term.getLabel());
					allTokens.add(term.getLabel());
				}
			}

			stringToTokens.put(eachQuery, allTokens);
		}

		return stringToTokens;
	}

	/**
	 * This is method is to load the ontology file from local system and create
	 * a hash table where the label is key and owlClass is the content
	 * 
	 * @param ontologyFilePath
	 * @throws OWLOntologyCreationException 
	 */
	public void parseOntology(String ontologyFilePath) throws OWLOntologyCreationException{

		OWLOntology localOntology = manager.loadOntologyFromOntologyDocument(new File(ontologyFilePath));

		labelToOWLClass = this.owlFunction.labelMapURI(factory, localOntology, null);

		List<String> listOfOntologyTerms = new ArrayList<String>();

		listOfOntologyTerms.addAll(labelToOWLClass.keySet());

		//Levenshtein TODO
		normalizedOntologyTerms = createNGrams(listOfOntologyTerms, " ", nGrams);

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
	public HashMap<String, List<String>> createNGrams(List<String> inputString, String separator, int nGrams){

		HashMap<String, List<String>> normalizedInputString = new HashMap<String, List<String>>();

		for(String eachString : inputString){

			String [] singleWords = eachString.split(separator);

			List<String> tokens = new ArrayList<String>();

			//Padding the string
			for(int index = 0; index < singleWords.length; index++){
				//TODO what if there is overlapping between different words such diebetes mellitus. 
				//The s$ will be the produced from two words. 
				singleWords[index] = singleWords[index].toLowerCase();
				singleWords[index] = "^" + singleWords[index];
				singleWords[index] = singleWords[index] + "$";

				for(int i = 0; i < singleWords[index].length(); i++){

					if(i + nGrams < singleWords[index].length()){
						tokens.add(singleWords[index].substring(i, i + nGrams));
					}else{
						if(!tokens.contains(singleWords[index].substring(singleWords[index].length() - 2))){
							tokens.add(singleWords[index].substring(singleWords[index].length() - 2).toLowerCase());
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
		HashMap<String, List<String>> annotatedByOntologyTerm = new HashMap<String, List<String>>();
		//Iterate the string
		for(String stringToMatch : listOfInputString){

			List<String> eachString = new ArrayList<String>();

			eachString.add(stringToMatch);

			//Levenshtein TODO
			HashMap<String, List<String>> temp = createNGrams(eachString, " " ,nGrams);

			//Method from BBMRI plugin! All the possibilities of string term
			//HashMap<String, List<String>> temp = createNGrams(eachString);
			String matchedOntologyTerm = null;

			double maxSimilarity = 0;

			boolean ontologyTermFound = false;

			for(String eachOntologyTerm : ontologyTerms.keySet()){

				ontologyTermFound = searchForOntologyTerm(temp.get(stringToMatch), 
						ontologyTerms.get(eachOntologyTerm));

				if(ontologyTermFound == true){
					if(!annotatedByOntologyTerm.containsKey(stringToMatch)){
						List<String> listOfOntologyTerms = new ArrayList<String>();
						listOfOntologyTerms.add(eachOntologyTerm);
						annotatedByOntologyTerm.put(stringToMatch, listOfOntologyTerms);
					}else{
						List<String> listOfOntologyTerms = annotatedByOntologyTerm.get(stringToMatch);
						listOfOntologyTerms.add(eachOntologyTerm);
						annotatedByOntologyTerm.put(stringToMatch, listOfOntologyTerms);
					}
				}

				double similarity = calculateScore(temp.get(stringToMatch), 
						ontologyTerms.get(eachOntologyTerm));
				if(similarity > maxSimilarity){
					maxSimilarity = similarity;
					matchedOntologyTerm = eachOntologyTerm;
				}
			}
			
//			System.out.println("The original string is " + stringToMatch  + "! The matched ontology term is " + matchedOntologyTerm + ". The similarity is " + maxSimilarity);
//			System.out.println();
			HashMap<String, Double> matchedTermAndSimilarity = new HashMap<String, Double>();
			matchedTermAndSimilarity.put(matchedOntologyTerm, maxSimilarity);
			mappingResult.put(stringToMatch, matchedTermAndSimilarity);
		}
		
		for(String key : annotatedByOntologyTerm.keySet()){
			
			System.out.println("The original string is " + key);
			System.out.print("The ontology terms are ");
			for(String eachOntologyTerm : annotatedByOntologyTerm.get(key)){
				System.out.print(eachOntologyTerm + "\t");
			}
			System.out.println();
			System.out.println();
			
		}

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

	public boolean searchForOntologyTerm(List<String> inputStringTokens, List<String> ontologyTermTokens){

		int matchedTokens = 0;

		for(String eachToken : ontologyTermTokens){
			if(inputStringTokens.contains(eachToken)){
				matchedTokens++;
			}
		}
		if(ontologyTermTokens.size() == matchedTokens){
			return true;
		}else{
			return false;
		}
	}

}
