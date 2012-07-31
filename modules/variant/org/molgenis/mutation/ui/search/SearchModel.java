package org.molgenis.mutation.ui.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.GeneDTO;
import org.molgenis.mutation.dto.MutationSearchCriteriaDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;
import org.molgenis.mutation.dto.QueryParametersDTO;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.mutation.ui.HtmlFormWrapper;
import org.molgenis.mutation.ui.LimitOffsetPager;
import org.molgenis.mutation.ui.html.MBrowse;
import org.molgenis.mutation.ui.search.form.DisplayOptionsForm;
import org.molgenis.mutation.ui.search.form.ListAllMutationsForm;
import org.molgenis.mutation.ui.search.form.ListAllPatientsForm;
import org.molgenis.mutation.ui.search.form.ShowMutationForm;
import org.molgenis.mutation.ui.search.form.SimpleSearchForm;
import org.molgenis.mutation.ui.search.form.ToExpertSearchForm;
import org.molgenis.mutation.ui.search.form.ToSimpleSearchForm;
import org.molgenis.pheno.dto.IndividualDTO;

public class SearchModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L;
	private String patientPager;
	private String mutationPager;
	private String patientViewer;
	private String mutationViewer;
	private String action = "init";
	private String result = "mutations"; // Initially search/display mutations
	private String searchTerm = ""; // Initially search term is empty
	private String header;
	private int numPatients;
	private int numUnpublished;
	private int numMutations;
	private Map<String, Integer> numMutationsByPathogenicity;
	private Map<String, Integer> numPatientsByPathogenicity;
	private HtmlFormWrapper expertSearchFormWrapper;
	private SimpleSearchForm simpleSearchForm                 = new SimpleSearchForm();
	private ListAllMutationsForm listAllMutationsForm         = new ListAllMutationsForm();
	private ListAllPatientsForm listAllPatientsForm           = new ListAllPatientsForm();
	private ToExpertSearchForm toExpertSearchForm             = new ToExpertSearchForm();
	private ToSimpleSearchForm toSimpleSearchForm             = new ToSimpleSearchForm();
	private ShowMutationForm showMutationForm                 = new ShowMutationForm();
	private DisplayOptionsForm displayOptionsForm             = new DisplayOptionsForm();

	private MutationSearchCriteriaDTO mutationSearchCriteriaVO = new MutationSearchCriteriaDTO();
	private QueryParametersDTO queryParametersVO               = new QueryParametersDTO();

	private GeneDTO geneDTO;
	private ExonDTO exonDTO;
	private MutationSummaryDTO mutationSummaryVO;
	private List<MutationSummaryDTO> mutationSummaryDTOList    = new ArrayList<MutationSummaryDTO>();
	private Map<String, String> mutationSummaryVOHash;
	private List<PatientSummaryDTO> patientSummaryVOs          = new ArrayList<PatientSummaryDTO>();
	private ProteinDomainDTO proteinDomainDTO;
	private List<ProteinDomainDTO> proteinDomainList;
	private PatientSummaryDTO patientSummaryVO;
	private IndividualDTO individualDTO;
	private Map<String, String> patientSummaryVOHash;
	private String rawOutput; // for output from included sources

	private LimitOffsetPager<?> pager;

	private MBrowse mbrowse;
	
	private String textWelcome                                = "";
	private String textSearch                                 = "";
	private String textRemarks                                = "";
	private String textCollaborations                         = "";
	
	private List<VariantDTO> positionMutations;
	private List<VariantDTO> codonMutations;

	public String getPatientPager() {
		return patientPager;
	}

	public void setPatientPager(String patientPager) {
		this.patientPager = patientPager;
	}

	public String getMutationPager() {
		return mutationPager;
	}

	public void setMutationPager(String mutationPager) {
		this.mutationPager = mutationPager;
	}

	public String getPatientViewer() {
		return patientViewer;
	}

	public void setPatientViewer(String patientViewer) {
		this.patientViewer = patientViewer;
	}

	public String getMutationViewer() {
		return mutationViewer;
	}

	public void setMutationViewer(String mutationViewer) {
		this.mutationViewer = mutationViewer;
	}

	public String getAction()
	{
		return action;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getHeader()
	{
		return header;
	}

	public void setHeader(String header)
	{
		this.header = header;
	}

	public int getNumPatients()
	{
		return numPatients;
	}

	public void setNumPatients(int numPatients)
	{
		this.numPatients = numPatients;
	}

	public int getNumUnpublished()
	{
		return numUnpublished;
	}

	public void setNumUnpublished(int numUnpublished)
	{
		this.numUnpublished = numUnpublished;
	}

	public int getNumMutations()
	{
		return numMutations;
	}

	public void setNumMutations(int numMutations)
	{
		this.numMutations = numMutations;
	}

	public Integer getNumMutationsByPathogenicity(String pathogenicity) {
		if (this.numMutationsByPathogenicity.containsKey(pathogenicity))
			return this.numMutationsByPathogenicity.get(pathogenicity);
		else
			return 0;
	}

	public void setNumMutationsByPathogenicity(
			Map<String, Integer> numMutationsByPathogenicity) {
		this.numMutationsByPathogenicity = numMutationsByPathogenicity;
	}

	public Integer getNumPatientsByPathogenicity(String pathogenicity) {
		if (this.numPatientsByPathogenicity.containsKey(pathogenicity))
			return this.numPatientsByPathogenicity.get(pathogenicity);
		else
			return 0;
	}

	public void setNumPatientsByPathogenicity(Map<String, Integer> numPatientsByPathogenicity) {
		this.numPatientsByPathogenicity = numPatientsByPathogenicity;
	}

	public HtmlFormWrapper getExpertSearchFormWrapper() {
		return expertSearchFormWrapper;
	}

	public void setExpertSearchFormWrapper(HtmlFormWrapper expertSearchFormWrapper) {
		this.expertSearchFormWrapper = expertSearchFormWrapper;
	}

	public SimpleSearchForm getSimpleSearchForm()
	{
		return simpleSearchForm;
	}

	public void setSimpleSearchForm(SimpleSearchForm simpleSearchForm)
	{
		this.simpleSearchForm = simpleSearchForm;
	}

	public ListAllMutationsForm getListAllMutationsForm()
	{
		return listAllMutationsForm;
	}

	public void setListAllMutationsForm(ListAllMutationsForm listAllMutationsForm)
	{
		this.listAllMutationsForm = listAllMutationsForm;
	}

	public ListAllPatientsForm getListAllPatientsForm()
	{
		return listAllPatientsForm;
	}

	public void setListAllPatientsForm(ListAllPatientsForm listAllPatientsForm)
	{
		this.listAllPatientsForm = listAllPatientsForm;
	}

	public ToExpertSearchForm getToExpertSearchForm()
	{
		return toExpertSearchForm;
	}

	public void setToExpertSearchForm(ToExpertSearchForm toExpertSearchForm)
	{
		this.toExpertSearchForm = toExpertSearchForm;
	}

	public ToSimpleSearchForm getToSimpleSearchForm()
	{
		return toSimpleSearchForm;
	}

	public void setToSimpleSearchForm(ToSimpleSearchForm toSimpleSearchForm)
	{
		this.toSimpleSearchForm = toSimpleSearchForm;
	}

	public ShowMutationForm getShowMutationForm()
	{
		return showMutationForm;
	}

	public void setShowMutationForm(ShowMutationForm showMutationForm)
	{
		this.showMutationForm = showMutationForm;
	}

	public DisplayOptionsForm getDisplayOptionsForm()
	{
		return displayOptionsForm;
	}

	public void setDisplayOptionsForm(DisplayOptionsForm displayOptionsForm)
	{
		this.displayOptionsForm = displayOptionsForm;
	}

	public MutationSearchCriteriaDTO getMutationSearchCriteriaVO()
	{
		return mutationSearchCriteriaVO;
	}

	public void setMutationSearchCriteriaVO(
			MutationSearchCriteriaDTO mutationSearchCriteriaVO)
	{
		this.mutationSearchCriteriaVO = mutationSearchCriteriaVO;
	}

	public QueryParametersDTO getQueryParametersVO()
	{
		return queryParametersVO;
	}

	public void setQueryParametersVO(QueryParametersDTO queryParametersVO)
	{
		this.queryParametersVO = queryParametersVO;
	}

	public void setGeneDTO(GeneDTO geneDTO) {
		this.geneDTO = geneDTO;
	}

	public GeneDTO getGeneDTO() {
		return geneDTO;
	}

	public ExonDTO getExonDTO()
	{
		return exonDTO;
	}

	public void setExonDTO(ExonDTO exonDTO)
	{
		this.exonDTO = exonDTO;
	}

	public MutationSummaryDTO getMutationSummaryVO()
	{
		return mutationSummaryVO;
	}

	public void setMutationSummaryVO(MutationSummaryDTO mutationSummaryVO)
	{
		this.mutationSummaryVO = mutationSummaryVO;
	}

	public List<MutationSummaryDTO> getMutationSummaryDTOList()
	{
		return mutationSummaryDTOList;
	}

	public void setMutationSummaryDTOList(List<MutationSummaryDTO> mutationSummaryDTOList)
	{
		this.mutationSummaryDTOList = mutationSummaryDTOList;
	}

	public Map<String, String> getMutationSummaryVOHash()
	{
		return mutationSummaryVOHash;
	}

	public void setMutationSummaryVOHash(Map<String, String> mutationSummaryVOHash)
	{
		this.mutationSummaryVOHash = mutationSummaryVOHash;
	}

	public List<PatientSummaryDTO> getPatientSummaryVOs()
	{
		return patientSummaryVOs;
	}

	public void setPatientSummaryVOs(List<PatientSummaryDTO> patientSummaryVOs)
	{
		this.patientSummaryVOs = patientSummaryVOs;
	}

	public ProteinDomainDTO getProteinDomainDTO()
	{
		return proteinDomainDTO;
	}

	public void setProteinDomainDTO(ProteinDomainDTO proteinDomainDTO)
	{
		this.proteinDomainDTO = proteinDomainDTO;
	}

	public List<ProteinDomainDTO> getProteinDomainList()
	{
		return proteinDomainList;
	}

	public void setProteinDomainList(List<ProteinDomainDTO> proteinDomainList)
	{
		this.proteinDomainList = proteinDomainList;
	}

	public PatientSummaryDTO getPatientSummaryVO()
	{
		return patientSummaryVO;
	}

	public void setPatientSummaryVO(PatientSummaryDTO patientSummaryVO)
	{
		this.patientSummaryVO = patientSummaryVO;
	}

	public IndividualDTO getIndividualDTO() {
		return individualDTO;
	}

	public void setIndividualDTO(IndividualDTO individualDTO) {
		this.individualDTO = individualDTO;
	}

	public Map<String, String> getPatientSummaryVOHash() {
		return patientSummaryVOHash;
	}

	public void setPatientSummaryVOHash(Map<String, String> patientSummaryVOHash) {
		this.patientSummaryVOHash = patientSummaryVOHash;
	}

	public String getRawOutput() {
		return rawOutput;
	}

	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}

	public LimitOffsetPager<?> getPager()
	{
		return pager;
	}

	public void setPager(LimitOffsetPager<?> pager)
	{
		this.pager = pager;
	}

	public MBrowse getMbrowse() {
		return mbrowse;
	}

	public void setMbrowse(MBrowse mbrowse) {
		this.mbrowse = mbrowse;
	}

	public String getTextWelcome() {
		return textWelcome;
	}

	public void setTextWelcome(String textWelcome) {
		this.textWelcome = textWelcome;
	}

	public String getTextSearch() {
		return textSearch;
	}

	public void setTextSearch(String textSearch) {
		this.textSearch = textSearch;
	}

	public String getTextRemarks() {
		return textRemarks;
	}

	public void setTextRemarks(String textRemarks) {
		this.textRemarks = textRemarks;
	}

	public String getTextCollaborations() {
		return textCollaborations;
	}

	public void setTextCollaborations(String textCollaborations) {
		this.textCollaborations = textCollaborations;
	}

	public List<VariantDTO> getPositionMutations() {
		return positionMutations;
	}
	public void setPositionMutations(List<VariantDTO> positionMutations) {
		this.positionMutations = positionMutations;
	}
	public List<VariantDTO> getCodonMutations() {
		return codonMutations;
	}
	
	public void setCodonMutations(List<VariantDTO> codonMutations) {
		this.codonMutations = codonMutations;
	}

	public SearchModel(SearchPlugin controller) {
		super(controller);
		// TODO Auto-generated constructor stub
	}

}
