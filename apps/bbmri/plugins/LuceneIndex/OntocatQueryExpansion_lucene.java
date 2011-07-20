
package plugins.LuceneIndex;

import java.io.File;
//import java.net.URI;
//import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
import java.util.Vector;

//import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
//import uk.ac.ebi.ontocat.OntologyTerm;
//import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;
import uk.ac.ebi.ontocat.file.FileOntologyService;
//import uk.ac.ebi.ontocat.virtual.CompositeDecorator;

/**
 *  Expands the query by adding synonyms and children to initial query, using Boolean OR (not necessary, but more convenient to look through the query), expansion terms are weighted less than initial query terms 
 *  @param query_terms - chunks: all possible combinations of query terms(subsequent)
 *  @param init_query - list of query terms, to which the expansion terms are then added
 *  @param boost_factor - the weight of expansion terms
 *  @param spec_symbols - supported Boolean search operators (& = AND, | = OR, both variants can be used) 
 *  
 */

public class OntocatQueryExpansion_lucene {
	List<String> query_terms;// n-grams
	OntologyService os;
	List<String> init_query = new Vector<String>();

	static String spec_symbols = "\\(\\)\\&\\|\\+\\-\\~";
	static final float boost_factor = 0.8f;
	public OntocatQueryExpansion_lucene() {}
	
	public OntocatQueryExpansion_lucene(String fname) throws OntologyServiceException {
	  File file1 = new File(fname);
	  os = new FileOntologyService(file1.toURI());
	 }

	public void setInit_query(List<String> query) {
		for(String s : query)
			init_query.add(s);
	}

	public void setQuery_terms(List<String> query) {
		query_terms = query;
	}
	
	private boolean isIn(String s, char ch){
		for (int i = 0; i <s.length(); i++)
			if (s.charAt(i) == ch)
				return true;
		return false;
	}

	/**
	 * Chunking the query into strings of different length 
	 */
	public List<String> chunk (List<String> words){
		String q = "";
		List<String> result = new ArrayList<String>();
		int l = words.size();

		for (int i = l; i > 0; i--) {
			for (int x = 0; x <= (l - i); x++) {
				q = "";
				for (int j = x; j < (x + i); j++) {
					q = q + words.get(j) + " ";
				}
				result.add(q.trim());
				// System.out.println(q);
			}
		}
		System.out.println("parsed query: " + result);
		query_terms = result;
		return result;
	}
	
	/**
	 * generating a list of possible word combinations of different length from 
	 * the query and setting it as query_terms
	 */
	public List<String> parseQuery(String query)
	{
		
		List<String> words = new ArrayList<String>();
		int i = 0;
		int first_letter = 0;
		char ch;
		
		
		String ignore = "[,.\\:\\!\\?;]";  /** punctuation to be ignored*/
		query = query.replaceAll(ignore, " ").trim(); /** remove punctuation */
		
		/** replacing special symbols */
		query = query.replaceAll("( *OR)|(OR *)", "|");
		query = query.replaceAll(" *AND *", "&");
		query = query.toLowerCase();
		int len = query.length();
		//* splitting by ' ' and by spec_symbols, leaving phrases in "" as single unit phrases */
		while(i < len){
			
			ch = query.charAt(i);
			if (ch == ' '){
				words.add(query.substring(first_letter, i));
				i++;
				first_letter = i;
				
			}
			else if (ch =='"'){
				int j = 0;
				i++;
				ch = query.charAt(i);
				while (ch != '"'){
					j++;
					ch = query.charAt(i+j);
					
				}
				words.add(query.substring(i, i+j));
				i += j+1;
				if (i >= len)
					break;
				if ((query.charAt(i) == ' '))
					{
					i++;
					first_letter = i;
					}
				else if (isIn(spec_symbols, query.charAt(i)))
				{
					words.add(query.substring(i, i+1));
					i++;
					first_letter = i;
					}
				
			}
			else if (isIn(spec_symbols, ch)){
				if (i != first_letter)
					words.add(query.substring(first_letter, i));
				char[] tmp = new char[1];
				tmp[0] = ch;
				String s = new String(tmp);
				words.add(s);
				i++;
				first_letter = i;
				
			}
			else{
				i++;
				if (i == len){
					words.add(query.substring(first_letter, i));
					break;
				}
			}
			
		}
		
		/**
		 * setting the initial query list (with Boolean operators) 
		 */
		setInit_query(words);
		
		List<String> result = new ArrayList<String>();
		List<String> tmp = new ArrayList<String>();
		
		/**
		 * chunking the query into all possible n-grams, skipping the Boolean operators
		 */
		int size = words.size();
		for (int x = 0; x < size; x++){
			String cur = words.get(x);
			if (! spec_symbols.contains(cur))
				tmp.add(cur);
			else
				{
				result.addAll(chunk(tmp));
				tmp.clear();
				}
			if ((!tmp.isEmpty()) && (x == size - 1))
				result.addAll(chunk(tmp));
		}
		query_terms = result;
		return result;
	}
	
	/** 
	 * convert String[] to List<String>, removing repeated elements
	 * @param arr
	 * @return
	 */
	public List<String> array2listNotDuplicate (String[] arr){
		List<String> list = new ArrayList<String>();
		int len = arr.length;
		for(int i = 0; i <len; i++){
			if (! list.contains(arr[i]))
				list.add(arr[i]);
		}
		return list;
	}
	
	/**
	 * Query expansion. Changes init_query by adding expansion terms after found terms and joining found phrase terms 
	 * hrase - current part of the query
	 * found_terms - terms found in ontologies
	 * searcher.SearchIndexOntocat(String phrase, List<Strings> ontologies) searches the phrase in index files of ontologies, returns (term:syn1;syn2;...;child1;child2;...)
	 * expansion - synonyms + children
	 * found all - found_terms + expansion
	 * @param ontologiesToUse
	 */
	public void expand(List<String> ontologiesToUse) {
		/*
		 * phrase - current part of the query
		* found_terms - terms found in ontologies
		* searcher.SearchIndexOntocat(String phrase, List<Strings> ontologies) searches the phrase in index files of ontologies, returns (term:syn1;syn2;...;child1;child2;...)
		* expansion - synonyms + children
		* found all - found_terms + expansion
		*/
		
		int i = 0;
		int z = 0;

		while (z < query_terms.size()) {
			if (query_terms.isEmpty())
				break;
			
			String phrase = query_terms.get(z);
			OntoCatIndexPlugin searcher = new OntoCatIndexPlugin("x",null);
			
			/**
			 * searching the phrase in ontologies
			 */
			List<String> found_terms = new ArrayList<String>();
			for( String str : searcher.SearchIndexOntocat(phrase, ontologiesToUse).split(":"))
				if (str != "")
					found_terms.add(str);
			
			z++;

			if (!found_terms.isEmpty()) {
				i = query_terms.indexOf(phrase);
						
				List<String> found_all = new ArrayList<String>();
				List<String> expansion = new ArrayList<String>();
				
				/** adding the phrase and expansion terms */
				found_all.add(phrase.toLowerCase());
				if (!found_all.contains(found_terms.get(0)))
					expansion.add(found_terms.get(0));
				System.out.println("found terms: " + found_terms); 
				//System.out.println(found_terms.get(1));
				if (found_terms.get(0) != ""){
					if (found_terms.size()>1){
						expansion = array2listNotDuplicate(found_terms.get(1).split(";"));
					}
				}
				found_all.addAll(expansion);

				
				/** replacing the words in init_query, corresponding to the phrase, with the expanded phrase (found_all) */
				String[] spl = phrase.split(" ");
				String first_word = spl[0];
				String last_word = spl[spl.length - 1];
				int first_index = 0;
				int last_index = 0;

				for (String w : init_query) {
					if (w.equals(first_word))
						first_index = init_query.indexOf(w);
					if (w.equals(phrase)){
						first_index = init_query.indexOf(w);
						last_index = init_query.indexOf(w);
					}
					if (w.equals(last_word))
						last_index = init_query.indexOf(w);
					if ((first_index != 0) && (last_index != 0))
						break;

				}
				int to_delete_count = last_index - first_index;

				while (to_delete_count >= 0) {
					init_query.remove(first_index + to_delete_count);
					to_delete_count--;
				}

				if (!init_query.isEmpty())
					init_query.addAll(first_index, found_all);

				else
					init_query.addAll(found_all);

				
				/** 
				 * replacing phrases, containing words from the found phrase, from query terms 
				 * (to avoid duplicate expansions, to reduce the time spent on searching) 
				 */
				z = 0;
				List<String> new_query_terms = new ArrayList<String>();
				
				for (int j = i + 1; j < query_terms.size(); j++) {
					boolean contained = false;

					if (phrase.contains(query_terms.get(j))) { 
						contained = true;
						
					}
					if (!contained) {
						new_query_terms.add(query_terms.get(j));
					}
				}
				query_terms = new_query_terms;

				if (query_terms.isEmpty())
					break;
			}
		}
	}

	/**
	 * constructing the expanded query
	 * @param parsed
	 * @return
	 */
	//TODO: do it with the help of Lucene. OR isn't necessary, ' ' = OR
	public String output(List<String> parsed) {
		float boost_factor = 0.8f;
		String res_query = "";
		int i = 0;
		int size = init_query.size();

		for (String s : init_query) {
			if (i + 1 < size) {
				String next = init_query.get(i + 1);
				if ((parsed.contains(s)) || (spec_symbols.contains(s))) {
					/**
					 *  to avoid having stopwords in ""
					 */
					if (s.split(" ").length == 1)
						res_query += s + " ";
					else
						res_query += "\"" + s + "\"" + " ";
					if ((!parsed.contains(next)) && (!spec_symbols.contains(next)))
						res_query += "OR (";
	
				} 
				else {
					if ((!parsed.contains(next)) && (! spec_symbols.contains(next))) {
						res_query +=  s + "^" + boost_factor + " OR ";
					} else
						res_query += s + "^" + boost_factor + ")"
								+ " ";
				}
			} 
			else {
				if (size > 1)
					if ((parsed.contains(s)) || (spec_symbols.contains(s)))
						if (s.split(" ").length == 1)
							res_query += s;
						else
							res_query += "\"" + s + "\"";
					else
						res_query += "" + s + "^" + boost_factor + ")";
				else
					if (s.split(" ").length == 1)
						res_query += s;
					else
						res_query += "\"" + s + "\"";
				break;
			}
		
			i++;

		}
		return res_query.replaceAll(" *\\& *", " AND ").replaceAll(" *\\| *", " OR ");
	}

	public static void main(String[] args) throws OntologyServiceException {
		//String query = "never asthma AND (\"cystic lung disease\" OR (Parkinson Disease))";
		String query = "Butoconazole Nitrate";
		//String query = "hallux valgus";
		//String query = "\"vldl cholesterol\"";
		long start = System.currentTimeMillis();
		
		OntocatQueryExpansion_lucene q = new OntocatQueryExpansion_lucene();

		List<String> parsed = q.parseQuery(query);
		List <String> OntologiesForExpansion = new ArrayList<String>();
		//OntologiesForExpansion.add("Human Phenotype Ontology");
		OntologiesForExpansion.add("NCI Thesaurus");
		//OntologiesForExpansion.add("Human Disease");
		//OntologiesForExpansion.add("MeSH");
	    
		System.out.println("Expanding the query...");
		q.expand(OntologiesForExpansion);
 		System.out.println("\nThe expanded query: ");
	
		String res = q.output(parsed);
		
		System.out.println(res);
		System.out.println("Finished searching ");
		long end = System.currentTimeMillis();
		System.out.println("Execution time was "+(end-start)+" ms.");
	}
}