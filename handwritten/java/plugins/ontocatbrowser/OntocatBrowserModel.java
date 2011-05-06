package plugins.ontocatbrowser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.SimpleScreenModel;
import org.molgenis.organization.Investigation;

public class OntocatBrowserModel extends SimpleScreenModel {

	public OntocatBrowserModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}
	String path;
	List<OntologyTerm> storedTerms;
	LinkedHashMap<String, String> browserTerms;
	
	LinkedHashMap<String, String> browserTermsState;
	
	String selectedOntology;
	String selectedBrowserTerm;
	Integer selectedStoredTerm;
	
	String selectedExploreTerm;
	String exploreMode;
	ExploreTerm explored;
	
	LinkedHashMap<String, String> searchResult;
	String searchThis;
	String searchSpace;
	String jumpToAccession;
	
	List<Investigation> studyList = new ArrayList<Investigation>();
	String selectedStudy;
	
	private ScreenMessage message = null;	
	
	
	public String getSelectedStudy()
	{
		return selectedStudy;
	}
	public void setSelectedStudy(String selectedStudy)
	{
		this.selectedStudy = selectedStudy;
	}
	public List<Investigation> getStudyList()
	{
		return studyList;
	}
	public void setStudyList(List<Investigation> investigationList)
	{
		this.studyList = investigationList;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<OntologyTerm> getStoredTerms() {
		return storedTerms;
	}
	public void setStoredTerms(List<OntologyTerm> storedTerms) {
		this.storedTerms = storedTerms;
	}
	public LinkedHashMap<String, String> getBrowserTerms() {
		return browserTerms;
	}
	public void setBrowserTerms(LinkedHashMap<String, String> browserTerms) {
		this.browserTerms = browserTerms;
	}
	public LinkedHashMap<String, String> getBrowserTermsState() {
		return browserTermsState;
	}
	public void setBrowserTermsState(LinkedHashMap<String, String> browserTermsState) {
		this.browserTermsState = browserTermsState;
	}
	public String getSelectedOntology() {
		return selectedOntology;
	}
	public void setSelectedOntology(String selectedOntology) {
		this.selectedOntology = selectedOntology;
	}
	public String getSelectedBrowserTerm() {
		return selectedBrowserTerm;
	}
	public void setSelectedBrowserTerm(String selectedBrowserTerm) {
		this.selectedBrowserTerm = selectedBrowserTerm;
	}
	public Integer getSelectedStoredTerm() {
		return selectedStoredTerm;
	}
	public void setSelectedStoredTerm(Integer selectedStoredTerm) {
		this.selectedStoredTerm = selectedStoredTerm;
	}
	public String getSelectedExploreTerm() {
		return selectedExploreTerm;
	}
	public void setSelectedExploreTerm(String selectedExploreTerm) {
		this.selectedExploreTerm = selectedExploreTerm;
	}
	public String getExploreMode() {
		return exploreMode;
	}
	public void setExploreMode(String exploreMode) {
		this.exploreMode = exploreMode;
	}
	public ExploreTerm getExplored() {
		return explored;
	}
	public void setExplored(ExploreTerm explored) {
		this.explored = explored;
	}
	public LinkedHashMap<String, String> getSearchResult() {
		return searchResult;
	}
	public void setSearchResult(LinkedHashMap<String, String> searchResult) {
		this.searchResult = searchResult;
	}
	public String getSearchThis() {
		return searchThis;
	}
	public void setSearchThis(String searchThis) {
		this.searchThis = searchThis;
	}
	public String getSearchSpace() {
		return searchSpace;
	}
	public void setSearchSpace(String searchSpace) {
		this.searchSpace = searchSpace;
	}
	public String getJumpToAccession() {
		return jumpToAccession;
	}
	public void setJumpToAccession(String jumpToAccession) {
		this.jumpToAccession = jumpToAccession;
	}
	public ScreenMessage getMessage() {
		return message;
	}
	public void setMessage(ScreenMessage message) {
		this.message = message;
	}
	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
