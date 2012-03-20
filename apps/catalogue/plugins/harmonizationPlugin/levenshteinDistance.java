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

	private HashMap<String, OWLClass> labelToOWLClass = null;

	private HashMap<String, List<String>> normalizedOntologyTerms = null;

	private OWLOntologyManager manager = null;

	private OWLDataFactory factory = null;

	//private OntocatQueryExpansion_lucene queryExpansion = null;

	private OWLFunction owlFunction = null;

	private OntologyService os = null;

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

		//this.queryExpansion = new OntocatQueryExpansion_lucene();

		manager = OWLManager.createOWLOntologyManager();

		factory = manager.getOWLDataFactory();

		this.owlFunction = new OWLFunction();
		
		for(int i = 0; i <STOP_WORDS.length; i++){
			STOPWORDSLIST.add(STOP_WORDS[i]);
		}
	}

	public static void main(String args[]) throws OWLOntologyCreationException, OntologyServiceException{

		levenshteinDistance test = new levenshteinDistance(2);

		//test.parseOntology("/Users/pc_iverson/Desktop/Input/PredictionModel.owl");

		List<String> testString = new ArrayList<String>();

		testString.add("Sex");

		testString.add("Age");

		testString.add("BMI");

		testString.add("Parent Diabetes Mellitus");

		testString.add("Former Smoker");

		testString.add("Current Smoker");

		testString.add("Hypertension");

		testString.add("Fasting glucose");

		HashMap <String, List<String>> annotatedTerms = test.readInSpreadSheet(
				"/Users/pc_iverson/Desktop/Ontology_term_pilot/InputForOntologyBuild.xls",";",false,3,0);
		
		List<String> tokens = new ArrayList<String>();
		
		for(String eachKey : annotatedTerms.keySet()){
			tokens.addAll(annotatedTerms.get(eachKey));
		}
		
		annotatedTerms = test.createNGrams(tokens, test.getnGrams());
		
		HashMap <String, List<String>> originalTerms = test.readInSpreadSheet(
				"/Users/pc_iverson/Desktop/Ontology_term_pilot/LifeLines_Data_itmes.xls"," ",true,3,1);
		
		tokens.clear();
		
		for(String eachString : originalTerms.keySet()){
			if(!eachString.equals("")){
				String removedStopWords = "";
				for(String term : originalTerms.get(eachString)){
					removedStopWords += term + " ";
				}
				tokens.add(removedStopWords.trim());
			}	
		}
		
		test.findMatch(annotatedTerms, tokens);
		
		
		
		//test.findMatch(test.getNormalizedOntologyTerms(), testString);

		//		System.out.println("Loading NCI Thesaurus ontology!");
		//		
		//		test.setOntologyService ("/Users/pc_iverson/Desktop/Input/Thesaurus.owl");
		//		
		//		System.out.println("NCI Thesaurus ontology is loaded!");
		//		
		//		HashMap <String, List<String>> stringToTokens = test.extensiveSearching(testString);
		//		
		//		System.out.println("The query terms are extended!");
		//		
		
	}

	public int getnGrams() {
		return nGrams;
	}

	public HashMap <String, List<String>> prePrecessInput(HashMap<String, List<String>> annotatedTerms){
		
		
		
		return null;
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
	 * Read in the spreadsheet with annotated ontology terms
	 * @param spreadSheet
	 */
	public HashMap<String, List<String>> readInSpreadSheet(String spreadSheet, String separator, boolean StopWords, int description, int dataItem){

		HashMap <String, List<String>> stringToTokens = new HashMap<String, List<String>>();

		File file = new File(spreadSheet);

		Workbook workbook;

		try {

			workbook = Workbook.getWorkbook(file);

			Sheet sheet = workbook.getSheet(0);

			int rows = sheet.getRows();

			int startingRow = 1;

			//TODO hard coding on which column the information should be collected. Right now it`s 3
			for(int i = startingRow; i < rows; i++){

				List<String> mappingForEachColumn = new ArrayList<String>();
				String inputString = sheet.getCell(description, i).getContents().toString().toLowerCase();
				String originalString = sheet.getCell(dataItem, i).getContents().toString().toLowerCase();

				String buildingBlocks [] = inputString.split(separator);

				for(int k = 0; k < buildingBlocks.length; k++){
					
					if(StopWords == true){
						if(!STOPWORDSLIST.contains(buildingBlocks[k]))
							mappingForEachColumn.add(buildingBlocks[k].trim());
					}else{
						mappingForEachColumn.add(buildingBlocks[k].trim());
					}
				}
				stringToTokens.put(originalString, mappingForEachColumn);
			}

		} catch (Exception e){
			e.printStackTrace();
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
		normalizedOntologyTerms = createNGrams(listOfOntologyTerms, nGrams);

		//Method from BBMRI plugin! All the possibilities of string term
		//normalizedOntologyTerms = createNGrams(listOfOntologyTerms);

		System.out.println("Ontology has been loaded and stored in the hash table");
	}

	/**
	 * This creates all the possibilities of the string (different length)
	 * @param inputString
	 * @return
	 */
//	public HashMap<String, List<String>> createNGrams(List<String> inputString){
//
//		HashMap<String, List<String>> normalizedInputString = new HashMap<String, List<String>>();
//
//		for(String eachString : inputString){
//
//			List<String> listOfString = new ArrayList<String>();
//
//			listOfString.add(eachString);
//
//			List<String> tokens = queryExpansion.chunk(listOfString);
//
//			normalizedInputString.put(eachString, tokens);
//
//		}
//		return normalizedInputString;
//	}

	public void createNGrams(HashMap<String, List<String>> inputString, int nGrams){}
	/**
	 * //create n-grams tokens of the string.
	 * @param inputString
	 * @param nGrams
	 * @return
	 */
	public HashMap<String, List<String>> createNGrams(List<String> inputString, int nGrams){

		//System.out.println("Processing the string in " + nGrams + "-gram!");

		HashMap<String, List<String>> normalizedInputString = new HashMap<String, List<String>>();

		for(String eachString : inputString){

			String [] singleWords = eachString.split(" ");

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

			//			for(int i = 0; i < nGrams - 1; i++){
			//				eachString = "^" + eachString;
			//				eachString = eachString + "$";
			//			}

			//			List<String> tokens = new ArrayList<String>();
			//			
			//			for(int i = 0; i < eachString.length(); i++){
			//
			//				if(i + nGrams < eachString.length()){
			//					tokens.add(eachString.substring(i, i + nGrams));
			//				}else{
			//					if(!tokens.contains(eachString.substring(eachString.length() - 2))){
			//						tokens.add(eachString.substring(eachString.length() - 2));
			//					}
			//				}
			//			}
			//			normalizedInputString.put(eachString.substring(1, eachString.length() - 1), tokens);

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

		//Iterate the string
		for(String stringToMatch : listOfInputString){

			List<String> eachString = new ArrayList<String>();

			eachString.add(stringToMatch);

			//Levenshtein TODO
			HashMap<String, List<String>> temp = createNGrams(eachString, nGrams);

			//Method from BBMRI plugin! All the possibilities of string term
			//HashMap<String, List<String>> temp = createNGrams(eachString);

			String matchedOntologyTerm = null;

			double maxSimilarity = 0;

			for(String eachOntologyTerm : ontologyTerms.keySet()){

				double similarity = calculateScore(temp.get(stringToMatch), 
						ontologyTerms.get(eachOntologyTerm));
				if(similarity > maxSimilarity){
					maxSimilarity = similarity;
					matchedOntologyTerm = eachOntologyTerm;
				}
			}
			System.out.println("The original string is " + stringToMatch  + "! The matched ontology term is " + matchedOntologyTerm + ". The similarity is " + maxSimilarity);
			System.out.println();
			HashMap<String, Double> matchedTermAndSimilarity = new HashMap<String, Double>();
			matchedTermAndSimilarity.put(stringToMatch, maxSimilarity);
			mappingResult.put(stringToMatch, matchedTermAndSimilarity);
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

}
