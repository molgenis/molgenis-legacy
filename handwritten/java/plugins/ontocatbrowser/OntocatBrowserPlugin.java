package plugins.ontocatbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.ols.OlsOntologyService;
import app.JDBCDatabase;
import decorators.NameConvention;

public class OntocatBrowserPlugin extends PluginModel<Entity> {
	/**
	 * EbiOlsBrowserPlugin
	 */
	private static final long serialVersionUID = 8092415619774443643L;
	private OntocatBrowserModel screenModel = new OntocatBrowserModel();

	// Database
	//JDBCDatabase db;

	// Webservice API
//	QueryService locator;
//	Query qs;
	
	// Instantiate OLS service
	 OntologyService os;


	// Used to seperate the term name from the term ID when 'flattening' the
	// tree
	static String seper = "~";
	static String rootName = "newOntologyTree";
	OntologyTree browserTerms;
	int id;

	LinkedHashMap<String, String> path = new LinkedHashMap<String, String>();
	ArrayList<String> childlessTerms = new ArrayList<String>();
	HashMap<String, String> nameCache = new HashMap<String, String>();

	// Base URL for EBI lookups
	String lookupURI = "http://www.ebi.ac.uk/ontology-lookup/?termId=";

	public OntocatBrowserPlugin(String name, ScreenModel<Entity> parent) {
		super(name, parent);

		// Try to make connections to the database and the webservice
		try {
	//		this.db = (JDBCDatabase) this.getDatabase();
//			this.locator = new QueryServiceLocator();
//			this.qs = locator.getOntologyQuery();
			this.browserTerms = new OntologyTree(seper + rootName, null);
			this.id = 0;
			os = new OlsOntologyService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	enum Action {
		BrowseBack, BrowseForth, Add, Remove, ExploreFromOLS, ExploreFromDB, RefreshDB, Reset, ReturnToOntolOverview, ChangeName, search, jump, removeSearchResult, skipToSearchTerm, ChangeCategory;
	};

	public OntocatBrowserModel getModel() {
		return screenModel;
	}

	@Override
	public String getViewName() {
		return "OntocatBrowser";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/ontocatbrowser/OntocatBrowserPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		logger.debug("handeling request in the OntocatBrowserPlugin");

		screenModel.setSelectedBrowserTerm(request.getString("selectedBrowserTerm"));
		screenModel.setSelectedStoredTerm(request.getInt("selectedStoredTerm"));
//		screenModel.setSelectedInvestigation(request.getString("selectInvestigation"));
		
		screenModel.setSearchThis(request.getString("searchThis"));
		screenModel.setSearchSpace(request.getString("searchSpace"));
		screenModel.setJumpToAccession(request.getString("jumpToAccession"));

		// Specific move actions.
		if (request.getObject(ScreenModel.INPUT_ACTION) != null) {
			switch (Action.valueOf(request.getString(ScreenModel.INPUT_ACTION))) {
			case BrowseBack:
				BrowseBack();
				break;
			case BrowseForth:
				if (screenModel.getSelectedOntology() == null || screenModel.getSelectedBrowserTerm().equals(screenModel.getSelectedOntology())) {
					BrowseForth(true, true); // root term YES, last term MAYBE
				} else {
					BrowseForth(false, true); // root term NO, last term MAYBE
				}

				break;
			case Add:
				int newItem = storeOntologyTerm(db, request);
				screenModel.setSelectedExploreTerm(null);
				screenModel.setSelectedStoredTerm(newItem);
				if (newItem != -1) {
					ExploreFromDB(db, true);
				}
				break;
			case Remove:
				Remove(db);
				break;
			case ExploreFromOLS:
				screenModel.setSelectedExploreTerm(request.getString("selectedExploreTerm"));
				screenModel.setSelectedStoredTerm(null);
				ExploreFromOLS("normalExplore");
				break;
			case ExploreFromDB:
				screenModel.setSelectedExploreTerm(null);
				ExploreFromDB(db, false);
				break;
			case RefreshDB:
				RefreshDB(db);
				break;
			case Reset:
				ResetAll();
				break;
			case ReturnToOntolOverview:
				returnToOntolOverview();
				break;
			case ChangeName:
				int changeNameID = ChangeName(db, request);
				screenModel.setSelectedStoredTerm(changeNameID);
				break;
//			case ChangeCategory:
//				int changeCategoryID = ChangeCategory(db, request);
//				screenModel.setSelectedStoredTerm(changeCategoryID);
//				break;
			case search:
				search(request);
				break;
			case jump:
				boolean makeJump = jump(request);
				if (makeJump) {
					String success1 = buildTreeFromPath(findSearchTermPath());
					ExploreFromOLS(success1);
				} else {
					screenModel.setMessage(new ScreenMessage("Accession for jump not found.", false));
				}
				break;
			case removeSearchResult:
				removeSearchResult();
				break;
			case skipToSearchTerm:
				screenModel.setSelectedExploreTerm(request.getString("selectedSearchResultTerm"));
				screenModel.setSelectedOntology(request.getString("selectedSearchResultTerm").split(":")[0]);
				String success = buildTreeFromPath(findSearchTermPath());
				ExploreFromOLS(success);
				break;
			default:
				break;
			}
		}
	}

	private boolean jump(Tuple request) {
		boolean makeJump = false;
		try {
			String jumpTerm = screenModel.getJumpToAccession();

			String onto = jumpTerm.split(":")[0];

			String term;
			if (nameCache.containsKey(jumpTerm)) {
				term = nameCache.get(jumpTerm);
			} else {
				
				term = os.getTerm(jumpTerm).getLabel();
			//	term = qs.getTermById(jumpTerm, onto);
				
				if (!term.equals(jumpTerm)) { // if nothing found, qs returns
					// the input!
					nameCache.put(jumpTerm, term);
				}
			}

			if (term.equals(jumpTerm)) {
				// nothing found, assume not a correct or existing ID
			} else {
				screenModel.setSelectedExploreTerm(jumpTerm);
				screenModel.setSelectedOntology(onto);
				makeJump = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return makeJump;
	}

	private String buildTreeFromPath(ArrayList<String> path) {
		System.out.println("buildTreeFromPath() called");
		String success = "true";

		browserTerms = new OntologyTree(seper + rootName, null);

		int path_index = 0;
		boolean keepGoing = true;

		for (String s : path) {
			if (keepGoing == true) {
				screenModel.setSelectedBrowserTerm(s);

				System.out.println("buildTreeFromPath() setSelectedBrowserTerm: " + s);

				// als zeker is dat het NIET de laatste term is, vul FALSE in
				// zodat als er TOCH children zijn, er een error kan komen

				if (path_index == 0 && path.size() == 1) {
					keepGoing = BrowseForth(true, true); // root term YES, last
					// term YES
				} else if (path_index == 0 && path.size() > 1) {
					keepGoing = BrowseForth(true, false); // root term YES, last
					// term NO
				} else if (path_index > 0 && path_index == path.size() - 1) {
					keepGoing = BrowseForth(false, true); // root term NO, last
					// term YES
				} else if (path_index > 0 && path_index < path.size() - 1) {
					keepGoing = BrowseForth(false, false); // root term NO, last
					// term NO
				} else {
					logger.error("buildTreeFromPath: NO SUITABLE IF-ELSE CONDITION FOUND");
				}

				System.out.println("BrowseForth() completed");

				path_index++;
			} else {
				success = "false";
			}
		}

		screenModel.setBrowserTerms(FlattenTree());
		screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));

		System.out.println("buildTreeFromPath() done");
		return success;

	}

	private ArrayList<String> findSearchTermPath() {
		System.out.println("findSearchTermPath() called");
		ArrayList<String> path = new ArrayList<String>();
		try {
			// logger.shutdown();
			String termAcc = screenModel.getSelectedExploreTerm();
			String parent = "";

			path.add(termAcc); // include searched acc in path

			boolean done = false;
			int iteration = 0;
			while (done == false) {

			//	LinkedHashMap<String, String> possibleParents = new LinkedHashMap(qs.getTermParents(termAcc, screenModel.getSelectedOntology()));
				LinkedHashMap<String, String> possibleParents = new LinkedHashMap<String, String>();
				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.getParents(os.getTerm(termAcc));
				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
					possibleParents.put(o.getAccession(), o.getLabel());
				}
				
				if (possibleParents.size() == 1) {
					parent = possibleParents.keySet().toArray()[0].toString();
					if (!path.contains(parent)) {
						path.add(parent);
						System.out.println("parent added: " + parent);
					} else {
						System.out.println("ERROR: parent present in list, infinite loop might form");
						screenModel.setMessage(new ScreenMessage("", false));
						done = true;
					}

				} else {
					boolean parentFound = false;
					for (String tryParent : possibleParents.keySet()) {
						//HashMap<String, String> possibleChildren = qs.getTermChildren(tryParent, screenModel.getSelectedOntology(), 1, null);
						LinkedHashMap<String, String> possibleChildren = new LinkedHashMap<String, String>();
						on2 = os.getChildren(os.getTerm(tryParent));
						for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
							possibleChildren.put(o.getAccession(), o.getLabel());
						}
						
						if (possibleChildren.keySet().contains(termAcc)) {
							parent = possibleParents.keySet().toArray()[0].toString();
							parentFound = true;
						}
					}
					if (parentFound == false) {
						path.add(screenModel.getSelectedOntology());
						done = true;
						System.out.println("no parent found, added " + screenModel.getSelectedOntology());
					} else {
						if (!path.contains(parent)) {
							path.add(parent);
							System.out.println("parent added: " + parent);
						} else {
							System.out.println("ERROR: parent present in list, infinite loop might form");
							done = true;
						}
					}
				}
				termAcc = parent;

				iteration++;
				if (iteration > 100) {
					logger.error("findSearchTermPath(): TOO MANY ITERATIONS (" + iteration + "x)");
					done = true;
				}
			}

			Collections.reverse(path);
			System.out.println("findSearchTermPath() done, path = " + path);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	private void removeSearchResult() {
		screenModel.setSearchResult(null);
		screenModel.setMessage(new ScreenMessage("Search result removed.", true));
	}

	private void search(Tuple request) {
		try {
			if (screenModel.getSearchSpace().equals("this")) {

				//HashMap<String, String> resultSet = qs.getTermsByName(screenModel.getSearchThis(), screenModel.getSelectedOntology(), false);
				HashMap<String, String> resultSet = new HashMap<String, String>();
				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.searchOntology(screenModel.getSearchThis(), screenModel.getSelectedOntology());
				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
					resultSet.put(o.getAccession(), o.getLabel());
				}
				if (resultSet.size() != 0) {
					screenModel.setSearchResult(new LinkedHashMap<String, String>(resultSet));
				} else {
					LinkedHashMap<String, String> searchResult = new LinkedHashMap<String, String>();
					searchResult.put("-", "empty");
					screenModel.setSearchResult(searchResult);
				}

			} else {
				//HashMap<String, String> resultSet = qs.getPrefixedTermsByName(screenModel.getSearchThis(), false);
				HashMap<String, String> resultSet = new HashMap<String, String>();
				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.searchAll(screenModel.getSearchThis());
				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
					resultSet.put(o.getAccession(), o.getLabel());
				}
				if (resultSet.size() != 0) {
					screenModel.setSearchResult(new LinkedHashMap<String, String>(resultSet));
				} else {
					LinkedHashMap<String, String> searchResult = new LinkedHashMap<String, String>();
					searchResult.put("-", "empty");
					screenModel.setSearchResult(searchResult);
				}
			}

			screenModel.setMessage(new ScreenMessage("Search successful.", true));

		} catch (Exception e) {
			e.printStackTrace();
			screenModel.setMessage(new ScreenMessage("Search failed: ", false));
		}

	}

	/**
	 * TODO: update ipv add/remove, maar werkt niet!
	 * 
	 * @param request
	 */
	private int ChangeName(Database db, Tuple request) {
		int ID = -1;
		try {
			db.beginTx();

			OntologyTerm ot = db.find(OntologyTerm.class, new QueryRule("termAccession", Operator.EQUALS, screenModel.getExplored().getAccession())).get(0);

			if (!ot.getName().equals(request.getString("UserDefName"))) {
				// System.out.println("old name: "+ ot.getName());
				ot.setName(request.getString("UserDefName"));
				// System.out.println("UPDATING : name set to " +
				// request.getString("UserDefName"));

				//db.remove(ot);
				//db.add(ot);

				db.update(ot);

				screenModel.getExplored().setName(request.getString("UserDefName"));

				ID = ot.getId();
			}

			db.commitTx();

			screenModel.setMessage(new ScreenMessage("Name change successful.", true));
		} catch (Exception e) {
			try {
				db.rollbackTx();
				screenModel.setMessage(new ScreenMessage("Name change failed: rollback successful.", false));
			} catch (DatabaseException e1) {
				logger.error("DB ROLLBACK FAILED: " + e1.getMessage());
				screenModel.setMessage(new ScreenMessage("Name change failed: rollback FAILED.", false));
			}
			e.printStackTrace();
		}
		return ID;
	}

	public void returnToOntolOverview() {
		screenModel.setBrowserTerms(null);
		screenModel.setSelectedBrowserTerm(null);
		screenModel.setSelectedOntology(null);
		screenModel.setPath(null);
		// see ResetAll() for comments
	}

	public void ResetAll() {
		// screenModel.setStoredTerms(null); set in Reload()
		// screenModel.setCategories(null); set in Reload()
		screenModel.setBrowserTerms(null); // return to ontology overview
		screenModel.setSelectedBrowserTerm(null); // not shown in first screen -
		// but prevents an 'old' selection from coming up again
		screenModel.setSelectedOntology(null); // not sure if needed but logical
		// screenModel.setSelectedStoredTerm(null); // empty selected DB term
		// screenModel.setExplored(null); // empty Explore field
		// screenModel.setExploreMode(null); // not sure if needed but feels
		// logical
		screenModel.setPath(null); // clear path
		screenModel.setJumpToAccession(null); // clear jump field
		screenModel.setSearchThis(null); // clear search field
		screenModel.setSearchResult(null); // remove search result
		// screenModel.setSearchSpace(null) not needed: 'all' in overview due to
		// disabled 'this', and set to 'this' when opening an ontology
	}

	// TODO: weghalen, uitbreiden, of zo laten ?
	// TODO: update hier ook kapot???
	private void RefreshDB(Database db) {
		try {
			db.beginTx();

			// Get all OntologyTerms from DB
			List<OntologyTerm> allTerms = db.find(OntologyTerm.class);
			for (OntologyTerm ot : allTerms) {
				// Use term accession to request fresh term name from webservice
				// and set to existing term object
				String term;
				if (nameCache.containsKey(ot.getTermAccession())) {
					term = nameCache.get(ot.getTermAccession());
				} else {
					//term = qs.getTermById(ot.getTermAccession(), ot.getTermAccession().split(":")[0]);
					term = os.getTerm(ot.getTermAccession()).getLabel();
					nameCache.put(ot.getTermAccession(), term);
					// TODO: add "if not equals"?
				}
				ot.setName(term);
				// Update term in the database
				db.update(ot);
				//db.remove(ot);
				//db.add(ot);
			}

			db.commitTx();
		} catch (Exception e) {
			try {
				db.rollbackTx();
			} catch (DatabaseException e1) {
				logger.error("DB ROLLBACK FAILED: " + e1.getMessage());
			}
			e.printStackTrace();
		}

	}

	private void ExploreFromDB(Database db, boolean justBeenSaved) {
		// TODO: ook al zo'n grote lelijke functie. alles direct naar hashmap in
		// de screen?
		try {
			// Used to temporarily hold data
			HashMap<String, String> map = new HashMap<String, String>();

			// Retrieve the selected term from the database
			QueryRule findTerm = new QueryRule("id", QueryRule.Operator.EQUALS, screenModel.getSelectedStoredTerm());
			OntologyTerm ot = db.find(OntologyTerm.class, findTerm).get(0);

			// TODO: split gebruiken om de ontology code te achterhalen...
			// Quick variables
			String accs = ot.getTermAccession();
			String onto = ot.getTermAccession().split(":")[0];

			// Check if term accession and term name are the same.
			if (accs.equals(ot.getName())) {
				// If they are, it is an ontology. Do not explore.
			} else {
				// If they are not, it is a term, and is allowed to explored.

				// Create new ExploreTerm object
				ExploreTerm et = new ExploreTerm();

				// Set accession - already stored in DB
				et.setAccession(accs);

				// Set term - already stored in DB
				et.setTerm(ot.getName());

				// Set name - already stored in DB
				// TODO: term / termname / name, beetje verwarrende naamgeving.
				et.setName(ot.getName());

				// Set metaData
				List<String> metaData = new ArrayList<String>();
				//map = qs.getTermMetadata(accs, onto);
				map = new HashMap<String, String>();
				Map<String, List<String>> os1 = os.getAnnotations(os.getTerm(accs));
				for(String key : os1.keySet()){
					List<String> valArr = os1.get(key);
					if(valArr != null){
						String concatVal = "";
						for(String subVal : valArr){
							concatVal += subVal + ", ";
						}
						concatVal = concatVal.substring(0, concatVal.length()-2);
						map.put(key, concatVal);
					}
				}
				for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
					String key = (String) i.next();
					String add = key + ": " + map.get(key);
					if (add.equals("definition: null")) {
						metaData.add("N/A");
					} else {
						metaData.add(add);
					}
				}
				et.setMetaData(metaData);

				// Set xRefs
				List<String> refs = new ArrayList<String>();
				//map = qs.getTermXrefs(accs, onto);
				map = new HashMap<String, String>();
				os1 = os.getRelations(os.getTerm(accs));
				for(String key : os1.keySet()){
						List<String> valArr = os1.get(key);
						String concatVal = "";
						for(String subVal : valArr){
							concatVal += subVal + ", ";
						}
						concatVal = concatVal.substring(0, concatVal.length()-2);
						map.put(key, concatVal);
				}
				for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
					String key = (String) i.next();
					if (map.get(key).split(":").length == 2 && map.get(key).length() < 15) {
						// Probably 'hyperlinkable'
						refs.add(key + ": " + makeLookupHyperlink(map.get(key)));
					} else {
						// Not 'hyperlinkable'
						refs.add(key + ": " + map.get(key));
					}
				}
				et.setXRefs(refs);

				// Set parents
				List<String> parents = new ArrayList<String>();
				//map = qs.getTermParents(accs, onto);
				map = new HashMap<String, String>();
				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.getParents(os.getTerm(accs));
				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
					map.put(o.getAccession(), o.getLabel());
				}
				for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
					String key = (String) i.next();
					parents.add(map.get(key) + " [" + makeLookupHyperlink(key) + "]");
				}
				et.setParents(parents);

				// Set relations
				List<String> relations = new ArrayList<String>();
				//map = qs.getTermRelations(accs, onto);
				map = new HashMap<String, String>();
				os1 = os.getRelations(os.getTerm(accs));
				for(String key : os1.keySet()){
					List<String> valArr = os1.get(key);
					String concatVal = "";
					for(String subVal : valArr){
						concatVal += subVal + ", ";
					}
					concatVal = concatVal.substring(0, concatVal.length()-2);
					map.put(key, concatVal);
				}
	
				for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
					String key = (String) i.next();
					relations.add(makeLookupHyperlink(key) + " " + map.get(key) + " <i>this</i>");
				}
				et.setRelations(relations);

				// Set path
				et.setPath(ot.getTermPath());

				// Set category
//				et.setCategory(ot.getTermCategory());
				
				// Set investigation
//				System.out.println("setting investigation");
//				et.setInvestigation(db.find(Investigation.class, new QueryRule("id", Operator.EQUALS, ot.getInvestigation())).get(0));
//				System.out.println("et.getInvestigation().getName(): " + et.getInvestigation().getName());
				
				// Set graph URI
				String uri = "http://www.ebi.ac.uk/ontology-lookup/displayTermImage.do?termId=" + accs + "&termName=" + ot.getName() + "&ontologyName=" + onto + "&graphType=root";
				et.setGraphURI("<a href=\"" + uri + "\" target=\"_blank\">Click to view</a>");

				// Set final ExploreTerm to screen
				screenModel.setExplored(et);

				screenModel.setExploreMode("db");

				if (justBeenSaved) {
					screenModel.setMessage(new ScreenMessage("Term added to database. Term explored (database).", true));
				} else {
					screenModel.setMessage(new ScreenMessage("Term explored (database).", true));
				}

			}
		} catch (Exception e) {
			if (justBeenSaved) {
				screenModel.setMessage(new ScreenMessage("Term added to database. Term explore (database) failed: " + e.getMessage(), false));
			} else {
				screenModel.setMessage(new ScreenMessage("Term explore (database) failed: " + e.getMessage(), false));
			}

			e.printStackTrace();
		}
	}

	private void ExploreFromOLS(String browseWasSuccess) {
		try {

			// TODO: grote, lelijke functie! hoe verbeteren?
			// Used to temporarily hold data
			HashMap<String, String> map = new HashMap<String, String>();

			// Quick variables
			String accs = screenModel.getSelectedExploreTerm();
			String onto = screenModel.getSelectedOntology();

			// // Check if term accession and term name are the same.
			// String checkTerm;
			// if(nameCache.containsKey(accs)){
			// checkTerm = nameCache.get(accs);
			// }else{
			// checkTerm = qs.getTermById(accs, onto);
			// nameCache.put(accs, checkTerm);
			// }
			// if (accs.equals(checkTerm)) {
			// // If they are, it is an ontology. Do not explore.
			// } else {
			// // If they are not, it is a term, and is allowed to explored.

			// Create new ExploreTerm object
			ExploreTerm et = new ExploreTerm();

			// Set accession
			et.setAccession(accs);

			// Set term
			String term;
			if (nameCache.containsKey(accs)) {
				term = nameCache.get(accs);
			} else {
				//term = qs.getTermById(accs, onto);
				term = os.getTerm(accs).getLabel();
				nameCache.put(accs, term);
			}
			et.setTerm(term);

			// Set name
			et.setName(et.getTerm());

			// Set metaData
			List<String> metaData = new ArrayList<String>();

			//map = qs.getTermMetadata(accs, onto);
			map = new HashMap<String, String>();
			Map<String, List<String>> os1 = os.getAnnotations(os.getTerm("APO:0000002"));
			for(String key : os1.keySet()){
				List<String> valArr = os1.get(key);
				String concatVal = "";
				for(String subVal : valArr){
					concatVal += subVal + ", ";
				}
				concatVal = concatVal.substring(0, concatVal.length()-2);
				map.put(key, concatVal);
			}
			for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				String add = key + ": " + map.get(key);
				if (add.equals("definition: null")) {
					metaData.add("N/A");
				} else {
					metaData.add(add);
				}
			}
			et.setMetaData(metaData);

			// Set xRefs
			List<String> refs = new ArrayList<String>();
			//map = qs.getTermXrefs(accs, onto);
			map = new HashMap<String, String>();
			os1 = os.getAnnotations(os.getTerm(accs));
			for(String key : os1.keySet()){
					String concatVal = "";
					for(String subVal : os1.get(key)){
						concatVal += subVal + ", ";
					}
					concatVal = concatVal.substring(0, concatVal.length()-2);
					map.put(key, concatVal);
			}
			
			for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				// 15 is arbitrary, probably 'hyperlinkable'
				if (map.get(key).split(":").length == 2 && map.get(key).length() < 15) {
					refs.add(key + ": " + makeLookupHyperlink(map.get(key)));
				} else {
					// Not 'hyperlinkable'
					refs.add(key + ": " + map.get(key));
				}
			}
			et.setXRefs(refs);

			// Set parents
			List<String> parents = new ArrayList<String>();
			//map = qs.getTermParents(accs, onto);
			map = new HashMap<String, String>();
			List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.getParents(os.getTerm(accs));
			for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
				map.put(o.getAccession(), o.getLabel());
			}
			for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				parents.add(map.get(key) + " [" + makeLookupHyperlink(key) + "]");
			}
			et.setParents(parents);

			// Set relations
			List<String> relations = new ArrayList<String>();
			//map = qs.getTermRelations(accs, onto);
			map = new HashMap<String, String>();
			os1 = os.getRelations(os.getTerm(accs));
			for(String key : os1.keySet()){
					List<String> valArr = os1.get(key);
					String concatVal = "";
					for(String subVal : valArr){
						concatVal += subVal + ", ";
					}
					concatVal = concatVal.substring(0, concatVal.length()-2);
					map.put(key, concatVal);
			}
			
			for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				relations.add(makeLookupHyperlink(key) + " " + map.get(key) + " <i>this</i>");
			}
			et.setRelations(relations);

			// Set path
			try {
				et.setPath(browserTerms.get(et.getTerm() + seper + screenModel.getSelectedExploreTerm()).getParent().getPath(" / "));
			} catch (NullPointerException e) {
				et.setPath("ERROR");
				logger.error("et.setPath FAILED");
			}

			// Set category (N/A)
			// et.setCategory();

			// Set graph URI
			// http://www.ebi.ac.uk/ontology-lookup/displayTermImage.do?termId=
			String uri = "http://www.ebi.ac.uk/ontology-lookup/displayTermImage.do?termId=" + accs + "&termName=" + term + "&ontologyName=" + onto + "&graphType=root";
			et.setGraphURI("<a href=\"" + uri + "\" target=\"_blank\">Click to view</a>");

			// Set final ExploreTerm to screen
			screenModel.setExplored(et);
			screenModel.setExploreMode("ols");

			if (browseWasSuccess.equals("true")) {
				screenModel.setMessage(new ScreenMessage("Term explored (online), and building tree was successful.", true));
			} else if (browseWasSuccess.equals("false")) {
				screenModel.setMessage(new ScreenMessage("Term explored (online), but building tree has failed.", false));
			} else {
				screenModel.setMessage(new ScreenMessage("Term explored (online).", true));
			}

			// }
		} catch (Exception e) {
			e.printStackTrace();
			screenModel.setMessage(new ScreenMessage("Term explore failed.", false));
		}

	}

	public String makeLookupHyperlink(String in) {
		return "<a href=\"" + lookupURI + in + "\" target=\"_blank\">" + in + "</a>";
	}

	private void Remove(Database db) {
		try {
			db.beginTx();

			if (screenModel.getSelectedStoredTerm() != -1) {
				// Find the OntologyTerm we want to delete.
				QueryRule findTerm = new QueryRule("id", QueryRule.Operator.EQUALS, screenModel.getSelectedStoredTerm());
				OntologyTerm ot = db.find(OntologyTerm.class, findTerm).get(0);

				// Retrieve (the reference to) the OntologySource we also wish
				// to delete.

			//	QueryRule findSource = new QueryRule("id", QueryRule.Operator.EQUALS, ot.getOntologySource());

			//	OntologySource os = db.find(OntologySource.class, findSource).get(0);
				
				// Remove both, Source first because Term refers to it.
				// WEIRD - term first apparently?
				
				db.remove(ot);
			//	db.remove(os);

				screenModel.setSelectedStoredTerm(null);
				screenModel.setExplored(null);
				screenModel.setExploreMode(null);

				db.commitTx();

				screenModel.setMessage(new ScreenMessage("Term removed from database.", true));
			}
		} catch (Exception e) {
			try {
				db.rollbackTx();
				screenModel.setMessage(new ScreenMessage("Term remove from database failed: rollback successful.", false));
			} catch (DatabaseException e1) {
				screenModel.setMessage(new ScreenMessage("Term remove from database failed: rollback FAILED.", false));
				logger.error("DB ROLLBACK FAILED: " + e1.getMessage());
			}
			e.printStackTrace();
		}

	}

	private int storeOntologyTerm(Database db, Tuple request) {
		int newItemId = -1;
		if (screenModel.getExplored().getTerm() != null) {
			try {
				db.beginTx();

				List<OntologyTerm> tryToFind = db.find(OntologyTerm.class, new QueryRule("termAccession", Operator.EQUALS, screenModel.getExplored().getAccession()));
				if (tryToFind.size() == 0) {

					String term = screenModel.getExplored().getTerm();
					String termAccession = screenModel.getExplored().getAccession();

					// Create and add OntologySource.
			//		OntologySource os = new OntologySource();
			//		os.setName(NameConvention.escapeEntityName("URL_to_" + term));
			//		os.setOntologyURI(lookupURI + termAccession);
			//		os.setInvestigation(db.find(Investigation.class,new QueryRule("name", Operator.EQUALS, screenModel.getSelectedInvestigation())).get(0));

				
			//		db.add(os);

					// Create and add OntologyTerm.
					OntologyTerm ot = new OntologyTerm();

					ot.setName(NameConvention.escapeEntityNameStrict(term));
					//ot.setName(term);
					ot.setTermAccession(termAccession);
					ot.setTermPath(screenModel.getExplored().getPath());
				//	ot.setOntologySource(os.getId());
//					ot.setInvestigation(db.find(Investigation.class,new QueryRule("name", Operator.EQUALS, screenModel.getSelectedInvestigation())).get(0));
					
					db.add(ot);

					newItemId = ot.getId();

				} else {
					// Term present, but need to set ID
					newItemId = tryToFind.get(0).getId();
				}

				db.commitTx();

				screenModel.setMessage(new ScreenMessage("Term added to database.", true));
			} catch (Exception e) {
				try {
					db.rollbackTx();
					screenModel.setMessage(new ScreenMessage("Term add to database failed: rollback successful.", false));
				} catch (DatabaseException e1) {
					screenModel.setMessage(new ScreenMessage("Term add to database failed: rollback FAILED.", false));
					logger.error("DB ROLLBACK FAILED: " + e1.getMessage());
				}
				e.printStackTrace();
			}
		}
		return newItemId;
	}


	private boolean BrowseForth(boolean rootTerm, boolean lastTerm) {
		String bt = screenModel.getSelectedBrowserTerm();
		//TODO: Danny: Use or Loose
		/*String ot = */screenModel.getSelectedOntology();
		logger.info("BrowseForth() : called");

		boolean keepGoing = true;
		try {
			// als term mogelijk een 'root' is, haal de rootmap op
			LinkedHashMap<String, String> rootmap;
			if (rootTerm == true) {
				//rootmap = new LinkedHashMap<String, String>(qs.getRootTerms(bt));
				rootmap = new LinkedHashMap<String, String>();
				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.getRootTerms(bt);
				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
					rootmap.put(o.getAccession(), o.getLabel());
				}
				
			} else {
				rootmap = new LinkedHashMap<String, String>();
			}

			// lege rootmap, probeer nu dan de childs op te halen van deze term
			if (rootmap.size() == 0) {
				logger.debug("ROOT MAP: EMPTY");
				// haal eerst de relaties op
				//Map<String, String> relations = qs.getTermRelations(bt, ot);
				LinkedHashMap<String, String> relations = new LinkedHashMap<String, String>();
				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.getChildren(os.getTerm(bt));
				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
					relations.put(o.getAccession(), o.getLabel());
				}

				// er zijn relaties, probeer dan ook de childs op te halen
				if (relations.size() > 0) {
					logger.debug("RELATION MAP: FILLED");
					// parent name = naam van deze accession, gebruikt om childs
					// toe te wijzen aan de tree
					String parentName;
					if (nameCache.containsKey(bt)) {
						parentName = nameCache.get(bt);
						logger.debug("name in cache");
					} else {
						//parentName = qs.getTermById(bt, ot);
						parentName = os.getTerm(bt).getLabel();
						nameCache.put(bt, parentName);
						logger.debug("name NOT in cache");
					}

					//Map<String, String> children = qs.getTermChildren(bt, ot, 1, null);
					LinkedHashMap<String, String> children = new LinkedHashMap<String, String>();
					on2 = os.getChildren(os.getTerm(bt));
					for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
						children.put(o.getAccession(), o.getLabel());
					}


					// er zijn net zoveel (of meer!) relaties als childs,
					// situatie is oké, voer 'standaardcode' uit
					logger.debug("relation size = " + relations.size());
					logger.debug("children size = " + children.size());
					if (relations.size() >= children.size()) {
						
						try {
							logger.debug("try");
							screenModel.setPath(browserTerms.get(parentName + seper + bt).getPath(" / "));
						} catch (NullPointerException e) {
							logger.debug("catch");
							logger.error("ELEMENT " + parentName + seper + bt + " NOT PRESENT IN TREE");
							keepGoing = false;
							screenModel.setMessage(new ScreenMessage("Building tree from search term failed: element \"" + parentName + " (" + bt + ")\" not assigned as child by webservice.", false));
						}
						
						if (keepGoing == true) {
							logger.debug("keepGoing == true");
							for (String key : children.keySet()) {
								if (browserTerms.get(children.get(key) + seper + key + seper + "DONTSHOW") != null) {
									browserTerms.get(children.get(key) + seper + key + seper + "DONTSHOW").setName(children.get(key) + seper + key);
								} else {
									OntologyTree child = new OntologyTree("" + id++, browserTerms.get(parentName + seper + bt));
									child.setName(children.get(key) + seper + key);
								}
							}
						}
					}

					// als er MEER relaties dan childs zijn moet er nog iets
					// extras gebeuren: voer difference uit, en voeg missende
					// term toe aan children
					if (relations.size() > children.size()) {
						logger.debug("MORE RELATIONS THAN CHILDREN!");

						for (String key : relations.keySet()) {
							if (!children.keySet().contains(key)) {
								
								String name;
								if (nameCache.containsKey(key)) {
									name = nameCache.get(key);
								} else {
									
									//name = qs.getTermById(key, ot);
									name = os.getTerm(key).getLabel();
									
									nameCache.put(key, name);
								}
								logger.debug("adding extra child: " + name + "(" + key + ")");
								
								if (browserTerms.get(name + seper + key + seper + "DONTSHOW") != null) {
									browserTerms.get(name + seper + key + seper + "DONTSHOW").setName(name + seper + key);
								} else {
									OntologyTree child = new OntologyTree("" + id++, browserTerms.get(parentName + seper + bt));
									child.setName(name + seper + key);
								}
							}
						}

						// als er MINDER relaties dan kinderen zijn klopt er
						// iets niet helemaal...
					} else if (relations.size() < children.size()) {
						logger.debug("ERROR: LESS RELATIONS THAN CHILDREN");
					} else {
						// komt niet voor
					}
					screenModel.setBrowserTerms(FlattenTree());
					screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));
					screenModel.setMessage(new ScreenMessage("Term children retrieved.", true));

					// er zijn geen relaties, en dus ook geen childs (behalve
					// bij een bug...)
				} else {
					logger.debug("RELATION MAP: EMPTY");
					// voer alsnog een check uit, die kan leiden tot een error?

					// add to list of known childless
					if (!childlessTerms.contains(bt)) {
						childlessTerms.add(bt);
					}

					// set appropriate stuff to screen
					screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));
					screenModel.setMessage(new ScreenMessage("Term discovered to be childless.", true));

					// special cases
					if (rootTerm == true) {
						screenModel.setMessage(new ScreenMessage("Root term is childless. Please reset OLS Browser.", false));
					}
					if (lastTerm == false) {
						keepGoing = false;
						screenModel.setMessage(new ScreenMessage("Building tree from search term error: No children found for term " + bt + ", but it is not the last element of a path.", false));
					}
				}
			}

			// term is een root
			else {
				logger.debug("ROOT MAP: FILLED");
				// maak en vul nieuwe tree
				browserTerms = new OntologyTree("" + id++, browserTerms.get(seper + "newOntologyTree"));
				browserTerms.setName(bt + seper + bt);
				for (String key : rootmap.keySet()) {
					OntologyTree child = new OntologyTree("" + id++, browserTerms.get(bt + seper + bt));
					child.setName(rootmap.get(key) + seper + key);
				}

				// set appropriate stuff to screen
				screenModel.setSelectedOntology(bt);
				screenModel.setSearchSpace("this");
				screenModel.setPath(browserTerms.get(bt + seper + bt).getPath(" / "));
				screenModel.setBrowserTerms(FlattenTree());
				screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));
				screenModel.setMessage(new ScreenMessage("Ontology root term accessed.", true));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keepGoing;
	}

//TODO: Danny: Use or loose
//	private boolean BrowseForthOLD(boolean rootTerm, boolean lastTerm) {
//		logger.info("BrowseForth() : called");
//		boolean keepGoing = true;
//		try {
//			LinkedHashMap<String, String> map;
//			if (rootTerm == true) {
//				//map = new LinkedHashMap<String, String>(qs.getRootTerms(screenModel.getSelectedBrowserTerm()));
//				map = new LinkedHashMap<String, String>();
//				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.getRootTerms(screenModel.getSelectedBrowserTerm());
//				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
//					map.put(o.getAccession(), o.getLabel());
//				}
//				
//			} else {
//				map = new LinkedHashMap<String, String>();
//			}
//
//			if (map.size() == 0) {
//				//map = new LinkedHashMap<String, String>(qs.getTermChildren(screenModel.getSelectedBrowserTerm(), screenModel.getSelectedOntology(), 1, new int[] { 2 }));
//				
//				map = new LinkedHashMap<String, String>();
//				List<uk.ac.ebi.ontocat.OntologyTerm> on2 = os.getChildren(os.getTerm(screenModel.getSelectedBrowserTerm()));
//				for(uk.ac.ebi.ontocat.OntologyTerm o : on2){
//					map.put(o.getAccession(), o.getLabel());
//				}
//				
//				logger.debug("'" + screenModel.getSelectedBrowserTerm() + "' has children " + map.size());
//
//				// get the relationships
//				//Map<String, String> relations = qs.getTermRelations(screenModel.getSelectedBrowserTerm(), screenModel.getSelectedOntology());
//				Map<String, String> relations = new LinkedHashMap<String, String>();
//				Map<String, List<String>> os1 = os.getRelations(os.getTerm(screenModel.getSelectedBrowserTerm()));
//				for(String key : os1.keySet()){
//						List<String> valArr = os1.get(key);
//						String concatVal = "";
//						for(String subVal : valArr){
//							concatVal += subVal + ", ";
//						}
//						concatVal = concatVal.substring(0, concatVal.length()-2);
//						relations.put(key, concatVal);
//				}
//				
//				
//				
//				
//				
//				for (String key : relations.keySet()) {
//					logger.debug(key + ":" + relations.get(key));
//				}
//
//				if (map.size() != 0) {
//
//					String parentName;
//					if (nameCache.containsKey(screenModel.getSelectedBrowserTerm())) {
//						parentName = nameCache.get(screenModel.getSelectedBrowserTerm());
//					} else {
//						
//					//	parentName = qs.getTermById(screenModel.getSelectedBrowserTerm(), screenModel.getSelectedOntology());
//						parentName = os.getTerm(screenModel.getSelectedBrowserTerm()).getLabel();
//						
//						nameCache.put(screenModel.getSelectedBrowserTerm(), parentName);
//					}
//
//					try {
//						screenModel.setPath(browserTerms.get(parentName + seper + screenModel.getSelectedBrowserTerm()).getPath(" / "));
//					} catch (NullPointerException e) {
//						logger.error("ELEMENT " + parentName + seper + screenModel.getSelectedBrowserTerm() + " NOT PRESENT IN TREE");
//
//						// morris doet moeilijk
//						for (OntologyTree element : browserTerms.getAllChildren()) {
//							// if(element.getName().contains(screenModel.
//							// getSelectedBrowserTerm()))
//							// {
//							// logger.debug("huh?");
//							logger.debug(element.getName());
//							// }
//						}
//
//						keepGoing = false;
//						screenModel.setMessage(new ScreenMessage("Building tree from search term failed: element \"" + parentName + " (" + screenModel.getSelectedBrowserTerm()
//								+ ")\" not assigned as child by webservice.", false));
//					}
//
//					if (keepGoing == true) {
//
//						for (String key : map.keySet()) {
//
//							if (browserTerms.get(map.get(key) + seper + key + seper + "DONTSHOW") != null) {
//								browserTerms.get(map.get(key) + seper + key + seper + "DONTSHOW").setName(map.get(key) + seper + key);
//							} else {
//
//								OntologyTree child = new OntologyTree("" + id++, browserTerms.get(parentName + seper + screenModel.getSelectedBrowserTerm()));
//								child.setName(map.get(key) + seper + key);
//							}
//						}
//
//						screenModel.setBrowserTerms(FlattenTree());
//						screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));
//
//						screenModel.setMessage(new ScreenMessage("Term children retrieved.", true));
//					}
//				} else {
//					// if this map is empty to, the term has no children
//					// normally do nothing, but now we want to modify the
//					// 'state' of this term so it does not display a
//					// [+] button anymore
//
//					if (!childlessTerms.contains(screenModel.getSelectedBrowserTerm())) {
//						childlessTerms.add(screenModel.getSelectedBrowserTerm());
//					}
//					screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));
//
//					screenModel.setMessage(new ScreenMessage("Term discovered to be childless.", true));
//
//					if (rootTerm == true) {
//						screenModel.setMessage(new ScreenMessage("Root term is childless. Please reset OLS Browser.", false));
//					}
//
//					if (lastTerm == false) {
//						keepGoing = false;
//						logger.error("NOT LAST TERM - BUT NO CHILDREN FOUND FOR " + screenModel.getSelectedBrowserTerm());
//						screenModel.setMessage(new ScreenMessage("Building tree from search term error: No children found for term " + screenModel.getSelectedBrowserTerm()
//								+ ", but it is not the last element of a path.", false));
//					}
//
//				}
//			} else {
//				// Looks a bit dangerous but should be okay
//				screenModel.setSelectedOntology(screenModel.getSelectedBrowserTerm());
//
//				browserTerms = new OntologyTree("" + id++, browserTerms.get(seper + "newOntologyTree"));
//				browserTerms.setName(screenModel.getSelectedBrowserTerm() + seper + screenModel.getSelectedBrowserTerm());
//
//				// String parentName = qs.getTermById(screenModel
//				// .getSelectedBrowserTerm(), screenModel
//				// .getSelectedOntology());
//
//				for (String key : map.keySet()) {
//
//					OntologyTree child = new OntologyTree("" + id++,
//					// browserTerms.get(parentName + seper
//							browserTerms.get(screenModel.getSelectedBrowserTerm() + seper + screenModel.getSelectedBrowserTerm()));
//					child.setName(map.get(key) + seper + key);
//				}
//
//				screenModel.setSearchSpace("this");
//
//				screenModel.setPath(browserTerms.get(
//				// parentName + seper
//						screenModel.getSelectedBrowserTerm() + seper + screenModel.getSelectedBrowserTerm()).getPath(" / "));
//
//				screenModel.setBrowserTerms(FlattenTree());
//				screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));
//
//				screenModel.setMessage(new ScreenMessage("Ontology root term accessed.", true));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return keepGoing;
//	}

	private LinkedHashMap<String, String> newBrowserTermState(LinkedHashMap<String, String> flattenTree) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		for (int i = 0; i < flattenTree.size() - 1; i++) {

			if (!childlessTerms.contains(flattenTree.keySet().toArray()[i].toString())) {

				int oldLayer = countStartChar(flattenTree.values().toArray()[i].toString(), "-");
				int newLayer = countStartChar(flattenTree.values().toArray()[i + 1].toString(), "-");
				if (newLayer > oldLayer) {
					map.put(flattenTree.keySet().toArray()[i].toString(), "MIN");
				} else {
					map.put(flattenTree.keySet().toArray()[i].toString(), "PLUS");

				}
			} else {
				map.put(flattenTree.keySet().toArray()[i].toString(), "CHILDLESS");
			}

		}
		// Last one is special, because it cannot be compared to the next one in
		// line. It can therefore never be MINUS, but it can be CHILDLESS.
		if (!childlessTerms.contains(flattenTree.keySet().toArray()[flattenTree.size() - 1].toString())) {
			map.put(flattenTree.keySet().toArray()[flattenTree.size() - 1].toString(), "PLUS");
		} else {
			map.put(flattenTree.keySet().toArray()[flattenTree.size() - 1].toString(), "CHILDLESS");
		}

		return map;
	}

	private int countStartChar(String in, String whatChar) {
		boolean stop = false;
		int index = 0;
		while (stop == false) {
			if (index + 1 < in.length()) {
				if (!in.substring(index, index + 1).equals(whatChar)) {
					stop = true;
				}
				index++;
			} else {
				stop = true;
			}

		}
		return index;
	}

	private LinkedHashMap<String, String> FlattenTree() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (String s : browserTerms.toString(true).split("\n")) {

			boolean show = true;
			try {
				if (s.split(seper)[2].replace(",", "").equals("DONTSHOW")) {
					show = false;
				}
			} catch (Exception e) {
			}
			if (show == true) {
				String key = s.split(seper)[1].replace(",", "");
				String value = s.split(seper)[0].replace("    ", "--");
				map.put(key, value);
			}
		}
		return map;
	}

	private void BrowseBack() {

		try {
			String selectedName;
			if (nameCache.containsKey(screenModel.getSelectedBrowserTerm())) {
				selectedName = nameCache.get(screenModel.getSelectedBrowserTerm());
			} else {
				
			//	selectedName = qs.getTermById(screenModel.getSelectedBrowserTerm(), screenModel.getSelectedOntology());
				selectedName = os.getTerm(screenModel.getSelectedBrowserTerm()).getLabel();
				
				nameCache.put(screenModel.getSelectedBrowserTerm(), selectedName);
			}

			String selectedTerm = selectedName + seper + screenModel.getSelectedBrowserTerm();

			OntologyTree overrideTerm = new OntologyTree("" + id++, browserTerms.get(selectedTerm).getParent());
			overrideTerm.setName(selectedTerm);

			for (OntologyTree child : overrideTerm.getChildren()) {
				if (!child.getName().endsWith("DONTSHOW")) {
					child.setName(child.getName() + seper + "DONTSHOW");

				}
			}

			screenModel.setPath(browserTerms.get(selectedName + seper + screenModel.getSelectedBrowserTerm()).getParent().getPath(" / "));

			screenModel.setBrowserTerms(FlattenTree());
			screenModel.setBrowserTermsState(newBrowserTermState(screenModel.getBrowserTerms()));

			screenModel.setMessage(new ScreenMessage("Moved to a previous term.", true));
		} catch (Exception e) {
			e.printStackTrace();
			screenModel.setMessage(new ScreenMessage("Move to a previous term failed.", false));
		}
	}

	public void checkIfTermsExistInDB(Database db) {
		try {
			LinkedHashMap<String, String> enhancedBrowserTerms = new LinkedHashMap<String, String>();

			// Make list of accessions
			ArrayList<String> accessions = new ArrayList<String>();
			for (String key : screenModel.getBrowserTerms().keySet()) {
				accessions.add(key);
			}

			// Match these accessions to the ones stored in the ontologyterms in
			// the database. Get list of the ontologyterms that match one of
			// these accessions.
			QueryRule accessionMatch = new QueryRule("termaccession", Operator.IN, accessions);
			List<OntologyTerm> matchingTerms = db.find(OntologyTerm.class, accessionMatch);

			// Iterate the keys (= accessions) of the browserterms and match to
			// the ontologyterm objects.

			// In short: MATCHES ON UNIQUE IDENTIFIER (ACCESSION)
			for (String key : screenModel.getBrowserTerms().keySet()) {
				boolean match = false;
				for (OntologyTerm term : matchingTerms) {
					if (key.equals(term.getTermAccession())) {
						match = true;
					}
				}

				String term = screenModel.getBrowserTerms().get(key);
				String symbol = " <b>@</b>";

				boolean symbolPresent;

				if (term.length() > symbol.length()) {
					symbolPresent = term.substring(term.length() - symbol.length(), term.length()).equals(symbol);
				}
				// if symbol length > term length, its not there in any case!
				// though note that "--"+ is now part of the term...
				else {
					symbolPresent = false;
				}

				if (match == true && symbolPresent == true) {
					enhancedBrowserTerms.put(key, term);
				} else if (match == true && symbolPresent == false) {
					enhancedBrowserTerms.put(key, term + symbol);
				} else if (match == false && symbolPresent == true) {
					enhancedBrowserTerms.put(key, term.substring(0, term.length() - symbol.length()));
				} else { // match == false && symbolPresent == false
					enhancedBrowserTerms.put(key, term);
				}
			}

			screenModel.setBrowserTerms(enhancedBrowserTerms);

		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void reload(Database db) {
		logger.debug("reloading the EbiOlsBrowserPlugin");
		try {
			// First time call, load list of available ontologies
			// FIXME: change to "selectedOntology" or so? this is okay too due
			// to 'sticky' selections but doesn't feel as logical
			if (screenModel.getSelectedBrowserTerm() == null) {
				LinkedHashMap<String, String> browserTerms = new LinkedHashMap<String, String>();
				boolean success = false;
				try {
					
					List<Ontology> ontos = os.getOntologies();
					HashMap<String, String> map = new HashMap<String, String>();
					for(Ontology o : ontos){
						map.put(o.getOntologyAccession(), o.getLabel());
					}
					
				//	HashMap<String, String> map = qs.getOntologyNames();
					for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
						String key = (String) i.next();
						browserTerms.put(key, map.get(key) + " [" + key + "]");
					}
					success = true;
				} catch (Exception e) {
					e.printStackTrace();
					screenModel.setMessage(new ScreenMessage("Ontology overview load failed: " + e.getMessage(), false));
				}
				screenModel.setBrowserTerms(browserTerms);
				screenModel.setBrowserTermsState(null);

				if (success == true) {
					screenModel.setMessage(new ScreenMessage("Ontology overview succesfully loaded.", true));
				}
			}

			try {
				checkIfTermsExistInDB(db);

				db = (JDBCDatabase) db;

				List<OntologyTerm> storedTerms = db.find(OntologyTerm.class);
				screenModel.setStoredTerms(storedTerms);

//				List<Category> categories = db.find(Category.class);
//				screenModel.setCategories(categories);
				
//				List<Investigation> investigations = db.find(Investigation.class);
//				screenModel.setInvestigationList(investigations);

			} catch (Exception e) {
				screenModel.setMessage(new ScreenMessage("Database load failed: " + e.getMessage() + ". Attempting reset, please refresh your screen.", false));
				this.ResetAll();
			}

		} catch (Exception e) {
			e.printStackTrace();
			screenModel.setMessage(new ScreenMessage("Ontology overview load failed: " + e.getMessage(), false));
		}
	}
}
