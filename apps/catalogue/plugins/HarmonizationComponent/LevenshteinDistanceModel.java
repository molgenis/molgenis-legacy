package plugins.HarmonizationComponent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class has implemented Levenshtein distance algorithm so a similarity score could be calculated 
 * between two sequences. The two input strings would be tokenized depending on what nGrams we have specified.
 * The default ngram is 2 which can be changed in the constructor. The two groups of tokens will be further
 * used to work out the similarity score. In addition, by default a list of stop words has been defined, in
 * the method stringMatching(), one of the parameters "removeStopWords" indicates whether the stop words will
 * be used to remove the useless or meaningless words from the String. This the stop words could be customized
 * by setStopWords(List<String> stopWords) or setStopWords(String[] stopWords). 
 *  
 * How to use? 
 * LevenShteinDistanceModel model = new LevenShteinDistanceModel(2);
 * double similarityScore = model.stringMatching("Smoking", "Smoker", false);
 * System.out.println(similarityScore);
 * 
 * The other way
 * List<String> tokens_1 = model.createNGrams("Smoking", false);
 * List<String> tokens_2 = model.createNGrams("Have you smoked last year?", true); //remove stop words!
 * double similarityScore = model.calculateScore(tokens_1, tokens_2);
 * 
 * 
 * @author Chao
 *
 */

public class LevenshteinDistanceModel {

	private int nGrams = 0;

	private String[] STOP_WORDS = {"a","you","about","above","after","again",
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
			"you've","your","yours","yourself","yourselves","many"};

	private List<String> STOPWORDSLIST = new ArrayList<String>();

	public LevenshteinDistanceModel(){
		this.nGrams = 2;
		this.STOPWORDSLIST = convertArrayToList(STOP_WORDS);
	}

	public LevenshteinDistanceModel(int nGrams){
		this.nGrams = nGrams;
		this.STOPWORDSLIST = convertArrayToList(STOP_WORDS);
	}

	public List<String> convertArrayToList(String[] inputArray){

		List<String> convertedList = new ArrayList<String>();

		for(int i = 0; i < inputArray.length; i++){

			if(!convertedList.contains(inputArray[i])){
				convertedList.add(inputArray[i]);
			}
		}
		return convertedList;
	}

	public double stringMatching(String query, String query_2, boolean removeStopWords){

		double similarityScore = this.calculateScore(createNGrams(query.toLowerCase().trim(), removeStopWords), 
				createNGrams(query_2.toLowerCase().trim(), removeStopWords));
		return similarityScore;
	}

	public List<String> removeStopWords(List<String> listOfWords){

		List<String> removedStopWordsList = new ArrayList<String>();

		for(String eachWord : listOfWords){

			if(STOPWORDSLIST == null){
				removedStopWordsList.add(eachWord);
			}else if(!STOPWORDSLIST.contains(eachWord)){
				removedStopWordsList.add(eachWord);
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
	public HashMap<String, List<String>> createNGrams(List<String> inputString, boolean removeStopWords){

		HashMap<String, List<String>> normalizedInputString = new HashMap<String, List<String>>();

		for(String eachString : inputString){

			String [] wordsInString = eachString.split(" ");

			List<String> tokens = new ArrayList<String>();

			List<String> removedStopWordsList = new ArrayList<String>();

			if(removeStopWords == true){
				removedStopWordsList = removeStopWords(convertArrayToList(wordsInString));
			}else{
				removedStopWordsList = convertArrayToList(wordsInString);
			}

			//Padding the string
			for(String singleWord : removedStopWordsList){
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
	 * //create n-grams tokens of the string.
	 * @param inputString
	 * @param nGrams
	 * @return
	 */
	public List<String> createNGrams(String inputQuery, boolean removeStopWords){

		List<String> tokens = new ArrayList<String>();

		List<String> removedStopWordsList = new ArrayList<String>();

		String[] wordsInString = inputQuery.split(" ");

		if(removeStopWords == true){
			removedStopWordsList = removeStopWords(convertArrayToList(wordsInString));
		}else{
			removedStopWordsList = convertArrayToList(wordsInString);
		}

		//Padding the string
		for(String singleWord : removedStopWordsList){

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


	public int getnGrams() {
		return this.nGrams;
	}

	public void setnGrams(int nGrams) {
		this.nGrams = nGrams;
	}

	public void setStopWords(String[] STOPWORDS) {
		this.STOP_WORDS = STOPWORDS;
		this.STOPWORDSLIST = convertArrayToList(STOP_WORDS);
	}
	public void setStopWords(List<String> STOPWORDS){
		this.STOPWORDSLIST = STOPWORDS;
	}
}
