package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.ui.LimitOffsetPager;
import org.molgenis.mutation.ui.search.form.DisplayOptionsForm;
import org.molgenis.mutation.ui.search.form.ExpertSearchForm;
import org.molgenis.mutation.ui.search.form.ListAllMutationsForm;
import org.molgenis.mutation.ui.search.form.ListAllPatientsForm;
import org.molgenis.mutation.ui.search.form.ShowMutationForm;
import org.molgenis.mutation.ui.search.form.SimpleSearchForm;
import org.molgenis.mutation.ui.search.form.ToExpertSearchForm;
import org.molgenis.mutation.ui.search.form.ToSimpleSearchForm;
import org.molgenis.news.MolgenisNews;


public class SearchPluginVO implements Serializable
{
	private static final long serialVersionUID = 8321193926556324386L;
	private String action = "init";
	private String result = "mutations"; // Initially search/display mutations
	private String header;
	private int numPatients;
	private int numUnpublished;
	private int numMutations;
	private SimpleSearchForm simpleSearchForm                 = new SimpleSearchForm();
	private ListAllMutationsForm listAllMutationsForm         = new ListAllMutationsForm();
	private ListAllPatientsForm listAllPatientsForm           = new ListAllPatientsForm();
	private ToExpertSearchForm toExpertSearchForm             = new ToExpertSearchForm();
	private ToSimpleSearchForm toSimpleSearchForm             = new ToSimpleSearchForm();
	private ExpertSearchForm expertSearchForm                 = new ExpertSearchForm();
	private ShowMutationForm showMutationForm                 = new ShowMutationForm();
	private DisplayOptionsForm displayOptionsForm             = new DisplayOptionsForm();

	private ExonSearchCriteriaVO exonSearchCriteriaVO         = new ExonSearchCriteriaVO();
	private MutationSearchCriteriaVO mutationSearchCriteriaVO = new MutationSearchCriteriaVO();
	private PatientSearchCriteriaVO patientSearchCriteriaVO   = new PatientSearchCriteriaVO();
	private QueryParametersVO queryParametersVO               = new QueryParametersVO();

	private MutationGene gene;
	private ExonSummaryVO exonSummaryVO;
	private MutationSummaryVO mutationSummaryVO;
	private List<MutationSummaryVO> mutationSummaryVOs        = new ArrayList<MutationSummaryVO>();
	private HashMap<String, LimitOffsetPager<MutationSummaryVO>> mutationSummaryVOHash;
	private List<PatientSummaryVO> patientSummaryVOs          = new ArrayList<PatientSummaryVO>();
	private ProteinDomainSummaryVO proteinDomainSummaryVO;
	private List<ProteinDomainSummaryVO> proteinDomainList;
	private PatientSummaryVO patientSummaryVO;
//	private List<PatientDetailsVO> patientDetailsVO;
	private PhenotypeDetailsVO phenotypeDetailsVO;
	private HashMap<String, LimitOffsetPager<PatientSummaryVO>> patientSummaryVOHash;
	private String rawOutput; // for output from included sources

	private List<MolgenisNews> news;
	private LimitOffsetPager<?> pager;

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

	public ExpertSearchForm getExpertSearchForm()
	{
		return expertSearchForm;
	}

	public void setExpertSearchForm(ExpertSearchForm expertSearchForm)
	{
		this.expertSearchForm = expertSearchForm;
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

	public ExonSearchCriteriaVO getExonSearchCriteriaVO()
	{
		return exonSearchCriteriaVO;
	}

	public void setExonSearchCriteriaVO(ExonSearchCriteriaVO exonSearchCriteriaVO)
	{
		this.exonSearchCriteriaVO = exonSearchCriteriaVO;
	}

	public MutationSearchCriteriaVO getMutationSearchCriteriaVO()
	{
		return mutationSearchCriteriaVO;
	}

	public void setMutationSearchCriteriaVO(
			MutationSearchCriteriaVO mutationSearchCriteriaVO)
	{
		this.mutationSearchCriteriaVO = mutationSearchCriteriaVO;
	}

	public PatientSearchCriteriaVO getPatientSearchCriteriaVO()
	{
		return patientSearchCriteriaVO;
	}

	public void setPatientSearchCriteriaVO(
			PatientSearchCriteriaVO patientSearchCriteriaVO)
	{
		this.patientSearchCriteriaVO = patientSearchCriteriaVO;
	}

	public QueryParametersVO getQueryParametersVO()
	{
		return queryParametersVO;
	}

	public void setQueryParametersVO(QueryParametersVO queryParametersVO)
	{
		this.queryParametersVO = queryParametersVO;
	}

	public void setGene(MutationGene gene) {
		this.gene = gene;
	}

	public MutationGene getGene() {
		return gene;
	}

	public ExonSummaryVO getExonSummaryVO()
	{
		return exonSummaryVO;
	}

	public void setExonSummaryVO(ExonSummaryVO exonSummaryVO)
	{
		this.exonSummaryVO = exonSummaryVO;
	}

	public MutationSummaryVO getMutationSummaryVO()
	{
		return mutationSummaryVO;
	}

	public void setMutationSummaryVO(MutationSummaryVO mutationSummaryVO)
	{
		this.mutationSummaryVO = mutationSummaryVO;
	}

	public List<MutationSummaryVO> getMutationSummaryVOs()
	{
		return mutationSummaryVOs;
	}

	public void setMutationSummaryVOs(List<MutationSummaryVO> mutationSummaryVOs)
	{
		this.mutationSummaryVOs = mutationSummaryVOs;
	}

	public HashMap<String, LimitOffsetPager<MutationSummaryVO>> getMutationSummaryVOHash()
	{
		return mutationSummaryVOHash;
	}

	public void setMutationSummaryVOHash(
			HashMap<String, LimitOffsetPager<MutationSummaryVO>> mutationSummaryVOHash)
	{
		this.mutationSummaryVOHash = mutationSummaryVOHash;
	}

	public List<PatientSummaryVO> getPatientSummaryVOs()
	{
		return patientSummaryVOs;
	}

	public void setPatientSummaryVOs(List<PatientSummaryVO> patientSummaryVOs)
	{
		this.patientSummaryVOs = patientSummaryVOs;
	}

//	public List<PatientDetailsVO> getPatientDetailsVO() {
//		return patientDetailsVO;
//	}
//
//	public void setPatientDetailsVO(List<PatientDetailsVO> patientDetailsVO) {
//		this.patientDetailsVO = patientDetailsVO;
//	}

	public ProteinDomainSummaryVO getProteinDomainSummaryVO()
	{
		return proteinDomainSummaryVO;
	}

	public void setProteinDomainSummaryVO(
			ProteinDomainSummaryVO proteinDomainSummaryVO)
	{
		this.proteinDomainSummaryVO = proteinDomainSummaryVO;
	}

	public List<ProteinDomainSummaryVO> getProteinDomainList()
	{
		return proteinDomainList;
	}

	public void setProteinDomainList(List<ProteinDomainSummaryVO> proteinDomainList)
	{
		this.proteinDomainList = proteinDomainList;
	}

	public PatientSummaryVO getPatientSummaryVO()
	{
		return patientSummaryVO;
	}

	public void setPatientSummaryVO(PatientSummaryVO patientSummaryVO)
	{
		this.patientSummaryVO = patientSummaryVO;
	}

	public PhenotypeDetailsVO getPhenotypeDetailsVO()
	{
		return phenotypeDetailsVO;
	}

	public void setPhenotypeDetailsVO(PhenotypeDetailsVO phenotypeDetailsVO)
	{
		this.phenotypeDetailsVO = phenotypeDetailsVO;
	}

	public HashMap<String, LimitOffsetPager<PatientSummaryVO>> getPatientSummaryVOHash() {
		return patientSummaryVOHash;
	}

	public void setPatientSummaryVOHash(
			HashMap<String, LimitOffsetPager<PatientSummaryVO>> patientSummaryVOHash) {
		this.patientSummaryVOHash = patientSummaryVOHash;
	}

	public String getRawOutput() {
		return rawOutput;
	}

	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}

	public List<MolgenisNews> getNews() {
		return news;
	}

	public void setNews(List<MolgenisNews> news) {
		this.news = news;
	}

	public LimitOffsetPager<?> getPager()
	{
		return pager;
	}

	public void setPager(LimitOffsetPager<?> pager)
	{
		this.pager = pager;
	}
}
