/* Date:        September 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * Despoina Antonakaki <D.Antonakaki@rug.nl>
 * Dasha Zhernakova <rokko_@mail.ru>
 * 
 */

package plugins.LuceneIndex;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.molgenis.core.UseCase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;
import org.molgenis.model.elements.Entity;
import org.molgenis.util.Tuple;

/*
 * @param LUCENE_INDEX_DIRECTORY - an empty directory to store index files
 */


public class DBIndexPlugin extends PluginModel<org.molgenis.util.Entity>
{
	private static final long serialVersionUID = 71L;
	private String Status = "";
	private String InputToken = "Enter the query";   
	String result = "";
	String useOntologies = "true";

    /**
     * The NUM_OF_FIELDS is used in order to retrieve the number of existing fields in the configuration file , defined by the user . 
     * This are all the database fields that are going to be use in the creation of the index, and later in the search.   
     * The DB_FIELD as it is retrieved from the configuration file is build (dbfield1,dbfield2..) by the function SearchAllDBTablesIndex() 
     * and the real database fields are retrieved from the configuration file .  
     */
	
	static final String NUM_OF_FIELDS = "numberOfFields";
	static final String DB_FIELD = "dbfield";
	
	List<String> OntologiesForExpansion = null;
	
	public void setOntologiesForExpansion(List<String> ontologies){
		OntologiesForExpansion = new ArrayList<String>();	
		OntologiesForExpansion = ontologies;
	}
	
	public DBIndexPlugin(String name, ScreenModel<org.molgenis.util.Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_LuceneIndex_DBIndexPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/LuceneIndex/DBIndexPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		LuceneConfiguration LC = new LuceneConfiguration();
	
		/**
		 * Retrieve the option of including ontologies from configuration 
		 */
		
		String useOntologies = LC.GetLuceneConfiguration("USE_ONTOLOGIES");
		
		
		if (useOntologies.compareTo("\"false\"") ==  0) {
			System.out.println("---Not using ontologies---");
			
		} else {
			/**
			 * set the ontologies to use in query expansion
			 */
			List<String> ontologies = new ArrayList<String>();
					
			if (request.getString("HPO") != null)
				ontologies.add("Human Phenotype Ontology");
			if (request.getString("HD") != null)
				ontologies.add("Human Disease");
			if (request.getString("NCI") != null)
				ontologies.add("NCI Thesaurus");
			if (request.getString("MeSH") != null)
				ontologies.add("MeSH");
			/*
			if (request.getString("SelectAll") != null){
				System.out.println("All ontologies elected code reached");
 
					ontologies.add("Human Phenotype Ontology");
					ontologies.add("Human Disease");
					ontologies.add("NCI Thesaurus");
					ontologies.add("MeSH");
			}
			*/
			
			if (ontologies.isEmpty()){
				System.out.println("[Ontologies] is empty");
				this.setStatus("<h4>Choose the ontologies to use for query expansion</h4>");
			
				
			}
			
			setOntologiesForExpansion(ontologies);
			System.out.println("Ontologies : " + ontologies);
		}
		
		
		
		if ("SearchLuceneIndex".equals(request.getAction())) {
			// check if the index has been created, one way is to create a boolean value , or check if the index directory contains an index .
			this.DirectoryhasContents(LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"));

			if (!this.DirectoryhasContents(LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"))) {	
				this.setInputToken(request.getString("InputToken").trim());
				this.SearchAllDBTablesIndex(db);
				
			} else {
				this.setStatus("<h4> Cannot search for  " + request.getString("InputToken") + "  Please create index first.</h4> ");
			}
			

			this.SaveUseCase(request.getString("InputToken").trim(), db, "Simple");
			this.setInputToken("");


		}
			
		if ("ExpandQuery".equals(request.getAction())){
			
			if (useOntologies.compareTo("\"false\"") ==  0){
				this.setStatus("<h3> You cannot use expand query option. Please adjust your configuration file to include ontologies.</h3>");
			} else {
				this.setInputToken(request.getString("InputToken").trim());
				this.ExpandQuery(db);
			}
			
		}
		
		
	}
	
	private void SaveUseCase(String request, Database db, String SearchType) {
		
		//save request in useCase
		
		try {
			
			//Database db = new app.JDBCDatabase("molgenis.properties");
			//create a new entity instance for use Case and add the new search from the user
			UseCase nuc=  new UseCase();	
			//check if the use case is not too long for column 'name' : | name       | varchar(255) | NO   |     | NULL    | 
			if (request.length() > 255) {
			    request =  request.substring(0, 255);
			}
			
			nuc.setUseCaseName(request);
			nuc.setSearchType(SearchType);
			db.add(nuc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		//List<UseCase>
		
	}

	/**
	 * For all DB tables , for all entities in each field ,  call search to search each field. 
	 * @param db
	 */
	public void search(Database db, String[] ResultHeaders ){
		int hits = 0;

		result =  "<p><table width=\"100%\" border=\"2\" bordercolor=\"#BDCDDA\" cellspacing=\"3\" cellpadding=\"3\"><tr>" ;
		for (int i=0; i< ResultHeaders.length; i++) {
			result +=	"<td><b>"+ ResultHeaders[i] +"</b></td>" ;
		}
        result += "</tr> ";
				
		List <String> dbfields = new ArrayList<String>();
		
		try {
			for(Entity entity : db.getMetaData().getEntities()) {
				for(Field f : entity.getFields()) {
					System.out.println(">>>>>> db entities " + f.getName());
					if (!dbfields.contains(f.getName())) 	dbfields.add(f.getName());

				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (MolgenisModelException e) {
			e.printStackTrace();
		}
		
	
		for (int i=0; i<dbfields.size(); i++) {
			hits += this.SearchAllDBFieldIndex(db, dbfields.get(i));
		}
	
		result += "</table>";
		result += "<p>Number of hits   : " + hits;
		//if (hits==0) result = "<p>No records found in db index for the term " + this.getInputToken() + "</p>";
		this.setStatus(result);

		
	}
	
	/**
	 * For all DB tables , for all entities in each field ,  call SearchAllDBFiledIndex to search each field. 
	 * @param db
	 */

	public void SearchAllDBTablesIndex(Database db) {
		int hits = 0;

		result =  "<p><table width=\"100%\" border=\"2\" bordercolor=\"#BDCDDA\" cellspacing=\"3\" cellpadding=\"3\"><tr>" +
				//		"<td><b>Entity</b></td>" +
						"<td><b>Biobank</b></td>" + 
						"<td><b>Feature</b></td>" +
						"<td><b>Highlighted result</b></td>" +   
						"<td><b>Other Sources</b></td><td><b>Score</b></td></tr> ";
				 //"<td><b>Term frequency</b></td><td><b>IDF</b></td><td><b>Field weight</b></td></tr>"; //result = "<p>Number of hits   : " + hits.length + "<p><table width=\"100%\" border=\"2\" bordercolor=\"black\" cellspacing=\"3\" cellpadding=\"3\"><tr><td><b>Investigation</b></td><td><b>Investigation Name</b></td><td><b>Description</b></td><td><b>Score</b></td></tr>";
	
		/**alternative way to go through all database tables and fields. Apparently such a search adds an extra undesirable cost */
		/*
		 * 	try {
			for(Class<Entity> aClass: db.getEntityClasses()) {	
					for(Entity e: (List<Entity>)db.find(aClass)) {
						String dbtable  = aClass.getName();
						
						for(String fieldName: e.getFields()) {		
							if (e.get(fieldName) != null) {
								hits += this.SearchAllDBFiledIndex(db, fieldName, result);	
							}
						}
					}
			} } catch (DatabaseException e) {
					e.printStackTrace();			
		}
		* 
		*/	
	      
			List <String> dbfields = new ArrayList<String>();
	    		
	    		try {
					for(Entity entity : db.getMetaData().getEntities()) {
						for(Field f : entity.getFields()) {
							System.out.println(">>>>>> db entities " + f.getName());
							if (!dbfields.contains(f.getName())) 	dbfields.add(f.getName());

						}
					}
				} catch (DatabaseException e) {
					e.printStackTrace();
				} catch (MolgenisModelException e) {
					e.printStackTrace();
				}
	    		
			
			for (int i=0; i<dbfields.size(); i++) {
				hits += this.SearchAllDBFieldIndex(db, dbfields.get(i));
			}
			
	    	
		result += "</table>";
		result += "<p>Number of hits   : " + hits;
		//if (hits==0) result = "<p>No records found in db index for the term " + this.getInputToken() + "</p>";
		this.setStatus(result);


	}

	public static String removeChar(String s, char c) {

		   String r = "";
		   if (s==null) System.out.println(s + " is empty");
		   else {
			   for (int i = 0; i < s.length(); i ++) {
			      if (s.charAt(i) != c) r += s.charAt(i);
			   }
		   }
		   return r;
	}
	/**
	 * 	The main function where the search in index is performed. 
	 *  The DB table field is passed as argument. 
	 *  PorterStemAnalyzer is used. 
	 * @param db
	 * @param fieldName
	 * @param result
	 * @return
	 */
	public int SearchAllDBFieldIndex(Database db, String fieldName) {

		LuceneConfiguration LC = new LuceneConfiguration();


		String userQuery = this.getInputToken();
		this.setStatus("Starting search for " + userQuery + " (all)index in " + LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"));
		System.out.println("Starting search for " + userQuery + " (all)index in " + LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"));
		
		IndexReader reader = null;
		//StandardAnalyzer analyzer = null;
		PorterStemAnalyzer analyzer = null;
		IndexSearcher searcher = null;
		TopScoreDocCollector collector = null;
		QueryParser parser = null; 
		Query query = null;
		ScoreDoc[] hits = null;
		
		List<String> fieldnames = new ArrayList<String>();
		
		
		System.out.println("the fieldname in which we are searching " + fieldName);
		try {
			analyzer = new PorterStemAnalyzer();
			//Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
			File file = new File(LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"));
			reader = IndexReader.open(FSDirectory.open(file), true);
			searcher = new IndexSearcher(reader);
			Searchable[] indexes = new IndexSearcher[1];
			indexes[0] = searcher;
			
			collector = TopScoreDocCollector.create(1000, false);
			parser = new QueryParser(Version.LUCENE_30, fieldName, analyzer);      
			query = parser.parse(userQuery);
			
			//Search the query 
			searcher.search(query, collector);
			hits  = collector.topDocs().scoreDocs;
			
			System.out.println("Number of hits   : "+ hits.length);
			
			if (hits.length > 0 ) {
				System.out.println("Number of hits: "+ hits.length);
				

				for (int i=0; i<hits.length; i++) {
					int scoreId = hits[i].doc; 	
	
					
					Explanation explanation = searcher.explain(query, scoreId);
					Document document  =  searcher.doc(scoreId);
					String text = document.getField(fieldName).stringValue();
	
					
					//Highlighting the hits:
					String highlighted = text;
					TokenStream tokenStream = TokenSources.getTokenStream(fieldName, text, analyzer);
					Formatter f = new SimpleHTMLFormatter("<font style=\"background-color: yellow;\">","</font>");
					Highlighter highlighter = new Highlighter(f, new QueryScorer(query, fieldName));
					try {
						highlighted = highlighter.getBestFragments(tokenStream, text, 5, "...");
					} catch (InvalidTokenOffsetsException e) {
						e.printStackTrace();
					} 
					
					
					//adding db table field  and Lucene score to the result 
					
					//if (i==0) result += " <tr><td><b class=\"link\" id=\"anElement\" onclick=\"Javascript:toggleElement('line');\"> <img src=\"res/img/Orange_plus.png\" width=\"12\" height=\"12\" alt=\"plus\" /><td>";			
					
					//The field Entity is not interesting for the simple user
					//result += "<tr id='line'><td>" + document.getField("className").stringValue() +"</td>";
					result += "<tr id='line'>" ;

					
					if (document.getField("investigationNameField") !=null)  result += "<td>" + document.getField("investigationNameField").stringValue() +"</td>";
					else result += "<td> </td>";
					
					// Retrieve Id 
					
					if (document.getField("id")!= null) {
						String id  = document.getField("id").stringValue();
						// TODO Danny Use or Loose
						/*int aInt = */Integer.parseInt(id);
					
						//result += "<td> <a href=http://localhost:8080/pheno/molgenis.do?__target=main&select=Biobank&__action=filter_set&__filter_attribute=id&__filter_operator=EQUALS&__filter_value="+id+">" + fieldName + "</a><br/></td>";	
						if (!fieldnames.contains(fieldName)) fieldnames.add(fieldName);
						result += "<td> <a href=molgenis.do?__target=Biobanks&__action=filter_add&__filter_attribute=id&__filter_operator=EQUALS&__filter_value="+id+">" + fieldName + "</a><br/></td>";
						//result += "<td> <a href=http://localhost:8080/gcc/molgenis.do?__target=DataViews&__action=filter_set&__filter_attribute=id&__filter_operator=EQUALS&__filter_value="+id +">" + fieldName + "</a><br/></td>"; 

					}
					//working 
					//http://localhost:8080/pheno/molgenis.do?__target=Biobanks&__action=filter_set&__filter_attribute=id&__filter_operator=EQUALS&__filter_value=3

					//result += "<td>" + "<a href=http://localhost:8080/pheno/molgenis.do?__target=main&select=Biobanks&__action=filter_set&__filter_attribute=id&__filter_operator=EQUALS&__filter_value="+id+">" + id + "</a><br/></td>" ;
					//result += "<td>" + "<a href=http://localhost:8080/pheno/molgenis.do?__target=Biobank&__action=filter_set&__filter_attribute=id&__filter_operator=EQUALS&__filter_value="+id+">" + id + "</a><br/></td>" ;
					//http://localhost:8080/pheno/molgenis.do?__target=main&select=Biobanks&__action=filter_set&__filter_attribute=id&__filter_operator=EQUALS&__filter_value=3
					//http://gbicserver1.biol.rug.nl:8080/xgap4exampledatasets/molgenis.do?__target=molgenis_main_Investigations&__action=filter_set&__filter_attribute=id&__filter_operator=EQUALS&__filter_value=3	
  					//http://localhost:8080/pheno/molgenis.do?__target=Biobank&__action=filter_set&__filter_attribute=id&__filter_operator=equals&__filter_value=83
					
					//"molgenis.do?__target=Biobanks&__action=filter_add&__filter_attribute=id&__filter_operator=EQUALS&__filter_value="+id"
					
					//Retrieve term accession from OntologyTerm Entity from DB . This option requires the import of all concept wiki terms in DB . 
					//Query<OntologyTerm> q = db.query(OntologyTerm.class);
					/*
					org.molgenis.framework.db.Query<OntologyTerm> q = db.query(OntologyTerm.class);
					q.addRules(new QueryRule("term", Operator.EQUALS, fieldName));
				
					List<OntologyTerm> valueList =  q.find(); 
					if (valueList != null) { //todo : check the size 
					System.out.println("****Ontology term retrieved for "+fieldName +valueList);
					} else {
						System.out.println("***No Ontology term for "+fieldName );
						
					}
					**/
					result += "<td>"+ highlighted + "</td>";
					//Search the user term in concept wiki 
					//Ask Christina : the user term that will be searched in concept wiki : 
					// 1. Needs to be clean up ,Should this be done  by hand or need some specialist ?
					// 2. Some terms can be found capitaliazed e.g HIV , but not in hiv ..: currently capitaliazing the terms, 

					//One of the below is correct: can concept wiki return some kind of 505/404/...error so I can check that? 
					result += "<td><a href=http://conceptwiki.org/index.php/Term:"+ userQuery.toUpperCase() + ">ConceptWiki</a> " + "OR..." +
							  "<a href=http://conceptwiki.org/index.php/Term:"+ userQuery + ">ConceptWiki</a> </td>";
					
					
					//Score added 
					result +=  "<td>" + explanation.toString().split("=")[0] + "</td>";
					
					
					/**
					 *  There are these are more Lucene technical variables that can be added: Term frequency,  IDF, Field weight; **/
					 /*  result +=  "<td>" + explanation.toString().split("=")[1] + "</td>";  result +=  "<td>" + explanation.toString().split("=")[3] + "</td>"; result +=  "<td>" + explanation.toString().split("=")[4] + "</td>"; result +=  "<td>" + explanation.toString().split("=")[6] + "</td>"; result +=  "<td>" + explanation.toString().split("=")[7] + "</td>"; result +=  "<td>" + explanation.toString().split("=")[8] + "</td>"; result +=  "<td>" + explanation.toString().split("=")[9] + "</td></tr>";  result +=  "<td>" + explanation.toString() + "</td></tr>";		*/			
					 /*result += "<td>" + explanation.toHtml() + "</td>";  System.out.println("---------- EXPLANATION -------------");  System.out.println("DESCRIPTION = " + document.getField("description").stringValue()); System.out.println("explanation: " + explanation.toHtml()); */
					System.out.println(explanation.toString());
					System.out.println("explanation: " + explanation.toHtml());
				

				}
				//this.setStatus(result);
				System.out.println(result);

			} 
			//IndexReader.close();
			System.out.println("Try closing the  index reader ");
			reader.close();
			
		} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
		}
			System.out.println("Fieldnames: "+ fieldnames);
		if (hits != null) return hits.length;
		
		
		return 0;
	}



	public  boolean DirectoryhasContents(String directory) {
		File dir=new File(directory);
		boolean exists = dir.exists();
		
		if (exists==false)	{
			System.out.println("The directory "+directory+"does not exists. Creating directory. ");
			boolean success = (new File(directory)).mkdir();
		    if (success) {
		      System.out.println("Directory: " + directory + " created");
		    }
		}
		
		boolean isEmpty = false;

		System.out.println("checking " + dir.getAbsolutePath());
		System.out.println("isEmpty: " + isEmpty);
		if (dir.exists() && dir.isDirectory()) {
			if(dir.list().length == 0) {
				this.setStatus("<h4> The directory is empty</h4> ");
				System.out.println("The directory is empty.");
				return true;
			} else {
				//File[] files = dir.listFiles();
				this.setStatus("<h4> The directory is NOT empty or does not exists .</h4> ");
		    	System.out.println("The directory is NOT empty or does not exists .");
			}
		} else {
			return false;
		}
		return false;
	}
	

	@Override
	public void reload(Database db)
	{

	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		if (this.getLogin().isAuthenticated()){
			return true;
		} else {
			return false;
		}
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}

	public void setInputToken(String inputToken) {
		InputToken = inputToken;
	}

	public String getInputToken() {
		return InputToken;
	}
	
	public String getuseOntologies() {
		LuceneConfiguration LC = new LuceneConfiguration();

		String useOntologies = LC.GetLuceneConfiguration("USE_ONTOLOGIES");		
		
		if (useOntologies.compareTo("\"false\"") ==  0) {
			System.out.println("---From getuseOntologies() : Not using ontologies---");
			return "false" ;
		}
		return "true";
			
		
	}
	
	/**
	 * The function for query expansion. 
	 * Creates a new (empty) instance of OntocatQueryExpansion_lucene class 
	 * @param db
	 */
	public void ExpandQuery(Database db){

		OntocatQueryExpansion_lucene q = new OntocatQueryExpansion_lucene();
	
		List<String> parsed = q.parseQuery(getInputToken());
		
		if (! OntologiesForExpansion.isEmpty()){
			System.out.println("Expanding the query...");
			q.expand(OntologiesForExpansion);
			System.out.println("\nThe expanded query: ");
			for (String s : q.init_query)
				System.out.println(s);
			
			String res = q.output(parsed);
			System.out.println(res);
			System.out.println("Finished expanding... ");
			
			this.setInputToken(res);
		}
		this.SearchAllDBTablesIndex(db);
		this.SaveUseCase(getInputToken(), db, "Expanded");
		this.setInputToken("");


	
		System.out.println("Finished serching... ");
	
	}
	
	
	
  /*
	public static void main(String[] args){
		
		DBIndexPlugin p = new DBIndexPlugin("x", null);
		
		
	}*/
	
}
