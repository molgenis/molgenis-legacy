/* Date:        September 15, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.LuceneIndex;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
//import org.apache.lucene.search.Weight;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Tuple;

import plugins.LuceneIndex.LuceneConfiguration;
import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;


/**
 * Indexes all ontologies specified in ontologyNamesMap. 
 * The ontologies should be downloaded on the computer in ONTOLOGIES_DIRECTORY
 * Searches through the index 
 * 
 * @param LUCENE_ONTOINDEX_DIRECTORY - an empty directory to store the index
 * @param ONTOLOGIES_DIRECTORY - the directory, where the ontologies are stored
 */

public class OntoCatIndexPlugin extends PluginModel<org.molgenis.util.Entity>
{
	private static final long serialVersionUID = 71L;
	private String Status = "";
	private String InputToken = "lung disease";
	//
//	static final String LUCENE_ONTOINDEX_DIRECTORY = "/Users/despoina/Documents/molgenis4phenotypeWorkspace/molgenis4phenotype/OntocatIndex";
//    static final String ONTOLOGIES_DIRECTORY = "/Users/despoina/Documents/workspace/biobank_search/ontologies/";

											

	public static final Map<String , String> ontologyNamesMap = new HashMap<String, String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		LuceneConfiguration LC = new LuceneConfiguration();

	{
        put(LC.GetLuceneConfiguration("ONTOLOGIES_DIRECTORY") + "human-phenotype-ontology_v1294.obo", "Human Phenotype Ontology");
        put(LC.GetLuceneConfiguration("ONTOLOGIES_DIRECTORY") + "human_disease_v1.251.obo", "Human Disease");
 		put(LC.GetLuceneConfiguration("ONTOLOGIES_DIRECTORY") + "Thesaurus_10_03.owl", "NCI Thesaurus");
 		put(LC.GetLuceneConfiguration("ONTOLOGIES_DIRECTORY") + "mesh.obo", "MeSH");
	}};


	public static void main(String[] args) throws Exception
	{
		
		OntoCatIndexPlugin p = new OntoCatIndexPlugin("x",null);
		

		p.buildIndexOntocat();
		List<String> ontologies = new ArrayList<String>();
		//ontologies.add("Human Phenotype Ontology");
		ontologies.add("Human Disease");
		ontologies.add("NCI Thesaurus");
		//ontologies.add("MeSH");
		
		p.setInputToken("cystic lung disease");
		p.setStatus("x");
		//p.SearchIndexOntocat("asthma", ontologies);
				
	}
	
	public String getCustomHtmlHeaders() {
		return "<script src=\"res/scripts/lib.js\" language=\"javascript\"></script>\n" +
		"<script src=\"Prototype/prototype.js\" language=\"javascript\"></script>\n" ;
	}
	
	public OntoCatIndexPlugin(String name, ScreenModel<org.molgenis.util.Entity> parent)
	{
		super(name, parent);
	}
	

	@Override
	public String getViewName()
	{
		return "plugin_LuceneIndex_OntoCatIndexPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/LuceneIndex/OntoCatIndexPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		LuceneConfiguration LC = new LuceneConfiguration();

		
		if ("SearchOntocatLuceneIndex".equals(request.getAction())) {
			if (!this.DirectoryhasContents(LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY"))) {		
				try {
					this.setInputToken(request.getString("InputToken"));
					this.setStatus("<h4> Starting search for  " + request.getString("InputToken") + "  in Ontocat index.</h4> ");
					String userQuery =this.getInputToken();
					List <String> ontologies = new ArrayList<String>();
					for (String o : ontologyNamesMap.values())
						ontologies.add(o);
					this.SearchIndexOntocat(userQuery, ontologies);
					this.setInputToken("");

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				this.setStatus("<h4> Cannot search for  " + request.getString("InputToken") + "  Please create index first.</h4> ");	
			}
		}
			

		
	}

	/**
	 * The function deletes the DB index . The path is retrieved through LuceneConfiguration index . 
	 * The variable in LuceneIndexConfiguration files defines if the program runs at mac or pc, in order to use the proper directory separators.  
	 */
	public void DeleteOntocatIndex() {
		String msWin;
		String OntoIndexDir;
		
		LuceneConfiguration LC = new LuceneConfiguration();
		System.out.println("coming from deleteOntocatIndex" + (LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY")));
		
		msWin = LC.GetLuceneConfiguration("msWin");
		OntoIndexDir = LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY");

		this.setStatus("<h4>About to delete the contents of the Ontocat index "+ OntoIndexDir +"</h4>");

		//browse to  the index directory
		deleteDirContents( OntoIndexDir, 0, msWin);
		
	   	
		//this.setStatus("<h4>Produces from DeleteLuceneIndex "+ LC.getINDX()+"</h4>");
		this.setStatus("<h4>Contents of index directory  "+ OntoIndexDir + " deleted </h4>");


	}


	private  void deleteDirContents(String fname, int deep, String msWin) {
		
		String DirSeparator = null;
		String FileName = null;
		
		File dir = new File(fname);
		String[] chld = dir.list();
		
		if (msWin.compareTo("\"false\"") ==  0){
			DirSeparator = "/";
			System.out.println("Hi, I am a mac");
		} else  if (msWin.compareTo("\"true\"") ==  0) {
			DirSeparator = "\\";	
			System.out.println("Hi, I am a pc");
		}
		
		if (dir.isFile()) {
			System.out.println("dirlist" + dir.getName());	
			return;
		} else if (dir.isDirectory()) {	
			System.out.println(fname.substring(fname.lastIndexOf(DirSeparator)));
			for (int i = 0; i < chld.length; i++) {
				FileName = fname + DirSeparator + chld[i];
				File subFile = new File(FileName);
				
				deleteDirContents(FileName ,0, msWin );
				System.out.println("deleting " + fname +  DirSeparator + chld[i] );
				//deleting every file
				if (!subFile.canWrite()) throw new IllegalArgumentException("Delete: write protected: " + FileName);
				else this.setStatus("<h4>I can delete "+FileName+ "</h4>");
				
				// If it is a directory, make sure it is empty - This shouldn't be reached : index does not contains directories.
			    if (subFile.isDirectory()) {
			      String[] files = subFile.list();
			      if (files.length > 0)  throw new IllegalArgumentException("Delete: directory not empty: " + FileName);
			    }
			    
			    // Attempt to delete it
			    boolean success = subFile.delete();

			    if (!success) throw new IllegalArgumentException("Delete: deletion failed");

			}
		}
			 
	}
	
	public void say(String whatTosay){
		System.out.println(whatTosay);
	}
	
	/*
	 * Analyzer is not used, because search should be exact
	 */
	public String SearchIndexOntocat(String query, List<String> ontologyLabels) {
		IndexReader reader = null;
		IndexSearcher searcher = null;
		TopScoreDocCollector collector = null;
		Query query2 = null;
		ScoreDoc[] hits = null;
		LuceneConfiguration LC = new LuceneConfiguration();

		String resultsTable="";
		String res = "";
		
		resultsTable =  "<p><table width=\"70%\" border=\"2\" bordercolor=\"#BDCDDA\" cellspacing=\"3\" cellpadding=\"3\">" +
				"<tr><td><b>Terms retrieved</b></td><td><b>Score</b></td></tr> ";
		  //"<td><b>Term frequency</b></td><td><b>IDF</b></td><td><b>Field weight</b></td></tr>";

		
		this.setStatus("<h4>Search for  " + query + "in OntoIndex started. </h4> ");
		System.out.println("Search for " + query + " in OntoIndex just started" );
		List<String> result = new ArrayList<String>();
		try {
			query = query.toLowerCase();
			File file  = new File(LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY"));	
			
			reader = IndexReader.open(FSDirectory.open(file), true);
			say("query="+ query);
			searcher = new IndexSearcher(reader);
			collector = TopScoreDocCollector.create(1000, true);
	
			/**
			 * making a boolean query to specify in which ontologies to search 
			 */
			BooleanQuery labelQuery = new BooleanQuery();
			BooleanQuery finalQuery = new BooleanQuery();
			
			for(String ontologyLabel : ontologyLabels){
				Query q = new TermQuery(new Term("ontologyLabel", ontologyLabel));
				labelQuery.add(q, BooleanClause.Occur.SHOULD);
			}
		
			/**
			 * the query to search the term in the field "term"
			 */
			query2 = new TermQuery(new Term("term", query));
			
			/**
			 * merging 2 queries together
			 */
			finalQuery.add(query2, BooleanClause.Occur.MUST);
			finalQuery.add(labelQuery, BooleanClause.Occur.MUST);
			
			System.out.println("finalQuery = " + finalQuery.toString());

			searcher.search(finalQuery,collector);
			hits  = collector.topDocs().scoreDocs;
			
			if (hits.length >0) { 
				List<String> expansion = new ArrayList<String>();

				
				for (int i=0; i<hits.length; i++) {
					int scoreId = hits[i].doc; 					
					Document document  =  searcher.doc(scoreId);
					
					Explanation explanation = searcher.explain(finalQuery, scoreId);
					
					//resultsTable+= "<tr>";
					
					if (res.isEmpty()) {
						res = (document.getField("term").stringValue()) + ":";
						resultsTable += "<tr><td>" +  res +  "</td></tr>"; 

					}
					String[] exp_spl = document.getField("expansion").stringValue().split(";");
					for (String exp : exp_spl){
						if (! expansion.contains(exp))
							{
							expansion.add(exp);
							res += ";" + exp;
							resultsTable += "<tr><td>"+ exp+ "</td>";
							resultsTable +=  "<td>" + explanation.toString().split("=")[0] + "</td></tr>";

							//resultsTable += "</tr><tr>";

							}
					}
					//say("label: " + document.getField("ontologyLabel").stringValue());
				}
				
				res = res.replace(":;", ":");
				
				resultsTable += "</table>";
				resultsTable += "<p>Number of hits   : " + hits;
				this.setStatus(resultsTable);
				//this.setStatus(res);
				
				System.out.println("RES:" + res);
				System.out.println("RESULTS table:" + resultsTable);
				
			} else {
				setStatus("<P>No records found for " + query + "in OntoINDEX</P>");
				say("<P>No records found for " + query + "in OntoINDEX</P>"); 
			}
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String s : result)
			say("s = " + s);
		
		return res;

	}

	/**
	 * The function creates an index on ontocat returned data. The data is ontology name, term, synonyms + children
	 * @throws OntologyServiceException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void buildIndexOntocat() throws Exception {
	     try {
	       
	    /**An IndexWriter creates and maintains an index.
	    * analyzer isn't used
	    */
	   LuceneConfiguration LC = new LuceneConfiguration();
	 
	   IndexWriter writer=null;
	   StandardAnalyzer analyzer = null;
	   File file = null;
	   try {
		   System.out.println("Start Indexing Ontocat results") ;
		   this.setStatus("Starting indexing Ontocat results in " + LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY"));
	
		   file = new File(LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY"));
		   analyzer = new StandardAnalyzer(Version.LUCENE_30);
		   writer = new IndexWriter(FSDirectory.open(file), analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
		    
		   for (String ontology_file: ontologyNamesMap.keySet()){
			   say("now " + writer.getReader().numDocs() + " terms indexed");
			
			   File file1 = new File(ontology_file);
			   OntologyService os = new FileOntologyService(file1.toURI());
			   
			   Ontology onto = os.getOntologies().get(0);
			   String label = ontologyNamesMap.get(ontology_file);
			   Set<OntologyTerm> all_terms = new HashSet<OntologyTerm>();
			   all_terms = os.getAllTerms(onto.getOntologyAccession());
			   
			   for(OntologyTerm term: all_terms)
			   {
				   /**
				    * getting the term with ontology label inside the index
				    * for each term we use a separate Document
				    */
				   
			       Document document = new Document();
			       Field termField = new Field("term", term.getLabel().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED);
			       document.add(termField);
			       //Field ontologyAccessionField = new Field("ontologyAccession", accss, Field.Store.YES, Field.Index.NOT_ANALYZED);
			       //document.add(ontologyAccessionField);
			       Field ontologyLabelField = new Field("ontologyLabel", label, Field.Store.YES, Field.Index.NOT_ANALYZED);
			       document.add(ontologyLabelField);
	
			       /**
			        * searching for synonyms and children in ontology, writing them to "expansion" with delimiters ";"
			        */
			       List<OntologyTerm> children = new ArrayList<OntologyTerm>();
			       List<String> syns = new ArrayList <String>();
			       String expansion = "";
			       
			       syns = os.getSynonyms(term);
			       //System.out.println("syns:\n" + syns);
			       for (String s : syns){
			    	   if (term.getLabel().toLowerCase() != s){ //if it doesn't already exists 
				    	   s = "\"" + s.toLowerCase() + "\"";
				    	   //System.out.println("syns: " + s);
				    	   if (! expansion.contains(s))
				    		   expansion += ";" + s;
			    	   }
			       }
		
			       children = os.getChildren(term);
			       for (OntologyTerm t : children){
			        //System.out.println("children: " + t);
			    	   String t_str = "\"" + t.getLabel().toLowerCase() + "\"";
			    	   if (! expansion.contains(t_str))
			    		   expansion += ";" + t_str;
			        }
			        //System.out.println("expansion: " + expansion.trim());	
			        Field expansionField = new Field("expansion", expansion.trim(),Field.Store.YES, Field.Index.NO);
			        document.add(expansionField);
			            
			        //adding a Document to a IndexWriter
			        writer.addDocument(document);	
			    } 
	
		    }
		    /**
		     * optimize the index
		     */
		    System.out.println(": Optimizing Index :" );
		    this.setStatus("Optimizing Ontocat Index" );
		    writer.optimize();	
	   } catch (Exception e) {
		   e.printStackTrace();
	   } finally {
		    try {
			    if(writer!=null)	
			    	System.out.println(writer.getReader().numDocs());
			    writer.close();
			    System.out.println("Finished indexing Ontocat") ;
			    this.setStatus("Ontocat Indexing finished") ;
			}catch(Exception ex){
			 	 ex.printStackTrace();
			}
	    }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
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
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		
		if(this.getLogin().isAuthenticated()){
			return true;
		}else
		{
			return false;
		}
	}


	public void setInputToken(String inputToken) {
		InputToken = inputToken;
	}

	public String getInputToken() {
		return InputToken;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}
	
}
