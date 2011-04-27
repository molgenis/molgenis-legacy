/* Date:        November 30, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.LuceneIndex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.organization.InvestigationElement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.ui.CoordinatorsFormModel;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;


public class AdminIndexes extends PluginModel<org.molgenis.util.Entity>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Status = "";
	LuceneConfiguration LC = new LuceneConfiguration();
	private String InputToken = "lung disease";


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
	
	
	public AdminIndexes(String name, ScreenModel<org.molgenis.util.Entity> parent)
	{
		
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_LuceneIndex_AdminIndexes";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/LuceneIndex/AdminIndexes.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		
		LuceneConfiguration LC = new LuceneConfiguration();

		if ("CreateLuceneIndex".equals(request.getAction())) {
			
			if  (!this.DirectoryhasContents(LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"))) {
				this.setStatus("<h4> Index already exists in " + LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY")  + "</h4>" ) ;

			} else {
				this.CreateLuceneIndex(db);
			}		
		}
		if ("DeleteLuceneIndex".equals(request.getAction())) {
			this.DeleteLuceneIndex();
		}
		
		/** Unfortunately most of the times this option is not successful through the UI 
		 *  In order to build the index check (Properties) the run Configurations of this file- set -Xms1024M -Xmx1024M
		 *  and run in server. Pleasy also check the terms on which the search in the ontologies is build. By default two ontologies are inserted 
		 *  	ontologies.add("Human Disease"); and 	ontologies.add("NCI Thesaurus");
		 *  and the input is p.setInputToken("cystic lung disease"); 
		 * */
		if ("CreateOntocatLuceneIndex".equals(request.getAction())) {
			if  (!this.DirectoryhasContents(LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY"))) {
				this.setStatus("<h4> Index on Ontocat already created in directory " + LC.GetLuceneConfiguration("LUCENE_ONTOINDEX_DIRECTORY") + "</h4>");
			} 
			else {
					try {
						this.buildIndexOntocat();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
		
		if ("DeleteOntocatIndex".equals(request.getAction())) {
			this.DeleteOntocatIndex();
		}
		
	}

	
	public void buildIndexAllTables(Database db) throws Exception {
		
		IndexWriter writer=null;
		//StandardAnalyzer analyzer = null;
		PorterStemAnalyzer analyzer = null;
		File file = null; 

		
		try{
			System.out.println("Start indexing ... ");
			/**
			 * get a reference to index directory file
			 */
			
			LuceneConfiguration LC = new LuceneConfiguration();
			file = new File(LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"));
			
			analyzer = new PorterStemAnalyzer();
			//analyzer = new StandardAnalyzer(Version.LUCENE_30);
			writer = new IndexWriter( FSDirectory.open(file), analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
			
			String fullText = null ;

			List<Class<? extends Entity>> classList = db.getEntityClasses();
			for (Class<? extends Entity> aClass : classList) {	
				for(Entity e: (List<? extends Entity>)db.find(aClass)) {
		
					Document document1 = null;
					document1 = new Document(); 
					fullText  = aClass.getName();
					
					//System.out.println("[DEBUG]"+fullText);
					
					//TRICK
					for(String fieldName: e.getFields()) {
	
						Field ClassName = new Field("className", aClass.getName().toString(), Field.Store.YES, Field.Index.ANALYZED);
						
						document1.add(ClassName);
					    System.out.println("the classes that are included in the index " + aClass.getName().toString());
						
						if (e.get(fieldName) != null) {
							
							//search works with Field.index.ANALYZED
							Field InsertFieldValue = new Field(fieldName, e.get(fieldName).toString(), Field.Store.YES, Field.Index.ANALYZED);
							
							if(e instanceof InvestigationElement)
							{

								if (((InvestigationElement)e).getInvestigation_Name()==null) {
									System.out.println("Investigation Element is null");
								} else {
									Field investigationNameField = new Field("investigationNameField", ((InvestigationElement)e).getInvestigation_Name(), Field.Store.YES, Field.Index.NO);
									document1.add(investigationNameField);
								}
							}
							
							document1.add(InsertFieldValue);
							System.out.println("All : 1st (" + fieldName + ")as InsertFieldValue inserted in Index" + InsertFieldValue.toString());
	
							//this is the same as InsertFieldValue. Though if you remove it , the field is not included and search does not work.
							fullText = fullText + " " +  e.get(fieldName).toString();
	
							Field fullTextField = new Field("fulltext", fullText, Field.Store.NO, Field.Index.ANALYZED);
							document1.add(fullTextField);
							System.out.println("All : FULLTEXT ( fulltext as anotherField inserted in Index" + fullTextField.toString());		
						}
						//writer.addDocument(document1); //this produces multiple entries in the index
					}		
					writer.addDocument(document1);
				}			
			}
			//optimize the index
			System.out.println("Optimizing index");
			writer.optimize();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if (writer != null) {
					writer.close();
					System.out.println("Closed writer");
				}
				System.out.println("Finished indexing");
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
   }

	/**
     * Add one document to the Lucene index
	 * @param <E>
     */
    public static <E extends org.molgenis.bbmri.Biobank> void AddDBIndexRecord(List<E> entities ){
        /**
         * reopen the index in order to add new db record
         */
    	
    	
		IndexWriter writer=null;
		PorterStemAnalyzer analyzer = null;
		File file = null; 

			System.out.println("Start updating index ... ");
			/**
			 * get a reference to index directory file
			 */
			
			LuceneConfiguration LC = new LuceneConfiguration();
			file = new File(LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"));

			// Either  public StandardAnalyzer(Version matchVersion, File stopwords) can be used in order to add a STOP WORD file
			//analyzer = new StandardAnalyzer(Version.LUCENE_30);
			analyzer = new PorterStemAnalyzer();
			try {
					writer = new IndexWriter( FSDirectory.open(file), analyzer, true, IndexWriter.MaxFieldLength.LIMITED);
				
	
					String fullText = null ;
					Document document1 = null;
					document1 = new Document(); 
					
					Class<? extends Entity> aClass =  (Class<? extends Entity>) entities.getClass();
					
					System.out.println("AddDBIndexRecors" + aClass);
				
					fullText  = aClass.getName();  
					
					ListIterator<E> l  = entities.listIterator();
					
					while (l.hasNext()) {
				
				}
				//The entities are in the form : [elementData][0][_gwaPlatform]
				//for (i=0; i< entities[elementData].length() )
				//for(String fieldName: ((Biobank) entities).getFields()) { //produces exception : java.util.ArrayList? cannot be cast to org.molgenis.bbmri.Biobank[edit] 
				while (l.hasNext()) {
					Vector<String> VfieldName = l.next().getFields();
					System.out.println("@Entities"+l.next().get__Type().toString());

					for (String fieldName : VfieldName) {
						System.out.println("@VfieldName"+fieldName);

						Field ClassName = new Field("className", aClass.getName().toString(), Field.Store.YES, Field.Index.ANALYZED);
						
						document1.add(ClassName);					 
						System.out.println("The new classes that are included in the index by the decorator  " + aClass.getName().toString());
			
						if (fieldName != null) {
							
							Field InsertFieldValue = new Field(fieldName, fieldName.toString(), Field.Store.YES, Field.Index.ANALYZED);
			
							if(entities instanceof InvestigationElement) {
								if (((InvestigationElement)entities).getInvestigation_Name()==null) {
									System.out.println("Investigation Element is null");
								} else {
									Field investigationNameField = new Field("investigationNameField", ((InvestigationElement)entities).getInvestigation_Name(), Field.Store.YES, Field.Index.NO);
									document1.add(investigationNameField);
								}
							}	
							document1.add(InsertFieldValue);
							System.out.println("from DBUpdateDecorator  All : 1st (" + fieldName + ")as InsertFieldValue inserted in Index" + InsertFieldValue.toString());
							//this is the same as InsertFieldValue. Though if you remove it , the field is not included and search does not work.
							fullText = fullText + " " +  fieldName.toString();
							
							Field fullTextField = new Field("fulltext", fullText, Field.Store.NO, Field.Index.ANALYZED);
							document1.add(fullTextField);
							System.out.println("All : FULLTEXT ( fulltext as anotherField inserted in Index" + fullTextField.toString());
		
						}
					}
				
					writer.addDocument(document1);
					System.out.println("Optimizing index");
					writer.optimize();
				}
			} catch (CorruptIndexException e1) {
				e1.printStackTrace();
			} catch (LockObtainFailedException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    }
    
		
	public void CreateLuceneIndex(Database db) {
		this.setStatus("Start indexing ");
		try {
			this.buildIndexAllTables(db);
		} catch (Exception e) {
			e.printStackTrace();	
		}		
		LuceneConfiguration LC = new LuceneConfiguration();
		
		this.setStatus("<h4>Finished indexing. Index created in </h4>" + LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY"));
	}

	
	@Override
	public void reload(Database db){
		this.setStatus("");

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
	
	/**
	 * The function deletes the DB index . The path is retrieved through LuceneConfiguration index . 
	 * The variable in LuceneIndexConfiguration files defines if the program runs at mac or pc, in order to use the proper directory separators.  
	 */
	public void DeleteLuceneIndex() {
		String msWin;
		String indexDir;
		
		LuceneConfiguration LC = new LuceneConfiguration();
		System.out.println("coming from deleteLuceneIndex" + (LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY")));
		
		msWin = LC.GetLuceneConfiguration("msWin");
		indexDir = LC.GetLuceneConfiguration("LUCENE_INDEX_DIRECTORY");

		this.setStatus("<h4>About to delete the contents of the DB index "+ indexDir +"</h4>");

		//browse to  the index directory
		deleteDirContents( indexDir, 0, msWin);
		
	   	
		this.setStatus("<h4>Contents of index directory  "+ indexDir + " deleted </h4>");

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
		
		this.setStatus("<h4>Contents of index directory  "+ OntoIndexDir + " deleted </h4>");


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
			       for (String s : syns){
			    	   if (term.getLabel().toLowerCase() != s){ //if it doesn't already exists 
				    	   s = "\"" + s.toLowerCase() + "\"";
				    	   if (! expansion.contains(s))
				    		   expansion += ";" + s;
			    	   }
			       }
		
			       children = os.getChildren(term);
			       for (OntologyTerm t : children){
			    	   String t_str = "\"" + t.getLabel().toLowerCase() + "\"";
			    	   if (! expansion.contains(t_str))
			    		   expansion += ";" + t_str;
			        }
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
	
	public void say(String whatTosay){
		System.out.println(whatTosay);
	}
	
	
	
}
