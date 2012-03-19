package plugins.harmonizationPlugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class levenshteinDistance {

	//Choose n-grams to tokenize the input string by default nGrams is 2
	private int nGrams = 2;

	private HashMap<String, OWLClass> labelToOWLClass = null;

	private HashMap<String, List<String>> normalizedOntologyTerms = null;

	private OWLOntologyManager manager = null;

	private OWLDataFactory factory = null;

	//Constructor
	public levenshteinDistance(int nGrams){

		this.nGrams = nGrams;

		manager = OWLManager.createOWLOntologyManager();

		factory = manager.getOWLDataFactory();

	}

	public static void main(String args[]) throws OWLOntologyCreationException{

		levenshteinDistance test = new levenshteinDistance(2);

		test.parseOntology("/Users/pc_iverson/Desktop/Input/PredictionModel.owl");

		List<String> testString = new ArrayList<String>();

		testString.add("sex ha");

		testString.add("smoker");
		
		testString.add("Blood PRESSURE");

		test.findMatch(test.getNormalizedOntologyTerms(), testString);
	}

	public HashMap<String, List<String>> getNormalizedOntologyTerms() {
		return normalizedOntologyTerms;
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

		labelToOWLClass = labelMapURI(localOntology, null);

		List<String> listOfOntologyTerms = new ArrayList<String>();

		listOfOntologyTerms.addAll(labelToOWLClass.keySet());

		normalizedOntologyTerms = createNGrams(listOfOntologyTerms, nGrams);

		System.out.println("Ontology has been loaded and stored in the hash table");
	}

	public void parseOntology(IRI ontologyIRI){}


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
	public HashMap<String, HashMap<String, Double>> findMatch(HashMap<String, 
			List<String>> ontologyTerms, List<String> listOfInputString){

		//Variable to store the mapping result
		HashMap<String, HashMap<String, Double>> mappingResult = new HashMap<String, HashMap<String, Double>>();

		//Iterate the string
		for(String stringToMatch : listOfInputString){
			
			List<String> eachString = new ArrayList<String>();

			eachString.add(stringToMatch);

			HashMap<String, List<String>> temp = createNGrams(eachString, nGrams);
			
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
			System.out.println("The matched ontology term is " + matchedOntologyTerm + ". The similarity is " + maxSimilarity);
			HashMap<String, Double> matchedTermAndSimilarity = new HashMap<String, Double>();
			matchedTermAndSimilarity.put(stringToMatch, maxSimilarity);
			mappingResult.put(stringToMatch, matchedTermAndSimilarity);
		}
		
		return mappingResult;
	}

	public void checkData(List<String> inputString){}

	public void checkData(String fileName){}

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

	public HashMap<String, OWLClass> labelMapURI(OWLOntology owlontology, IRI AnnotataionProperty){

		HashMap<String, OWLClass> mapURI = new HashMap<String, OWLClass>();
		OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		//		OWLAnnotationProperty synonym = factory.getOWLAnnotationProperty(
		//				IRI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN"));
		for (OWLClass cls : owlontology.getClassesInSignature()) {
			// Get the annotations on the class that use the label property
			for (OWLAnnotation annotation : cls.getAnnotations(owlontology, label)) {
				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					String labelString = val.getLiteral();
					mapURI.put(labelString.toLowerCase(), cls);
				}
			}
		}
		return mapURI;
	}//end of labelMapURI method

}
