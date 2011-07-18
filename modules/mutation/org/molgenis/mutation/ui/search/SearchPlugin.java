/* Date:        February 22, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.regexp.RESyntaxException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.service.ExonService;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;
import org.molgenis.mutation.service.PhenotypeService;
import org.molgenis.mutation.service.ProteinDomainService;
import org.molgenis.mutation.ui.LimitOffsetPager;
import org.molgenis.mutation.vo.ExonSearchCriteriaVO;
import org.molgenis.mutation.vo.MBrowseVO;
import org.molgenis.mutation.vo.MutationSearchCriteriaVO;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.mutation.vo.PhenotypeDetailsVO;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;
import org.molgenis.mutation.vo.QueryParametersVO;
import org.molgenis.mutation.vo.SearchPluginVO;
import org.molgenis.news.service.NewsService;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public abstract class SearchPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID               = 651270609185006020L;
	protected String GENENAME;
	private ExonService exonService;
	private MutationService mutationService;
	private PatientService patientService;
	private PhenotypeService phenotypeService;
	private ProteinDomainService domainService;
	private NewsService newsService;

	private SearchPluginVO searchPluginVO                     = new SearchPluginVO();
	private MBrowseVO mBrowseVO                               = new MBrowseVO();

	private ExonSearchCriteriaVO exonSearchCriteriaVO         = new ExonSearchCriteriaVO();
	private MutationSearchCriteriaVO mutationSearchCriteriaVO = new MutationSearchCriteriaVO();
	private PatientSearchCriteriaVO patientSearchCriteriaVO   = new PatientSearchCriteriaVO();

	public SearchPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		//new Sub(this.getName() + "_sub1", this);
		this.GENENAME = "COL7A1";
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_mutation_ui_search_SearchPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/mutation/ui/search/SearchPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.reload(db);
		this.reset();

		try
		{
			this.setMessages();

			this.mutationSearchCriteriaVO = new MutationSearchCriteriaVO();

			if (StringUtils.isEmpty(request.getAction()))
				this.searchPluginVO.setAction("init");
			else
				this.searchPluginVO.setAction(request.getAction());

			this.searchPluginVO.setQueryParametersVO(new QueryParametersVO());

			if ("1".equals(request.getString("expertSearch")))
				this.searchPluginVO.getQueryParametersVO().setExpertSearch(true);
			else
				this.searchPluginVO.getQueryParametersVO().setExpertSearch(false);

			if (StringUtils.isNotEmpty(request.getString("snpbool")))
				if (request.getString("snpbool").equals("show"))
					this.searchPluginVO.getQueryParametersVO().setShowSNP(true);
				else if (request.getString("snpbool").equals("hide"))
				{
					this.searchPluginVO.getQueryParametersVO().setShowSNP(false);
					this.exonSearchCriteriaVO.setIsIntron(false);
				}

			if (StringUtils.isNotEmpty(request.getString("showIntrons")))
				if (request.getString("showIntrons").equals("show"))
				{
					this.searchPluginVO.getQueryParametersVO().setShowIntrons(true);
					this.exonSearchCriteriaVO.setIsIntron(null);
				}
				else if (request.getString("showIntrons").equals("hide"))
				{
					this.searchPluginVO.getQueryParametersVO().setShowIntrons(false);
					this.exonSearchCriteriaVO.setIsIntron(false);
				}

			if (StringUtils.isNotEmpty(request.getString("showNames")))
				if (request.getString("showNames").equals("show"))
					this.searchPluginVO.getQueryParametersVO().setShowNames(true);
				else if (request.getString("showNames").equals("hide"))
					this.searchPluginVO.getQueryParametersVO().setShowNames(false);

			if (StringUtils.isNotEmpty(request.getString("showNumbering")))
				if (request.getString("showNumbering").equals("show"))
					this.searchPluginVO.getQueryParametersVO().setShowNumbering(true);
				else if (request.getString("showNumbering").equals("hide"))
					this.searchPluginVO.getQueryParametersVO().setShowNumbering(false);

			if (StringUtils.isNotEmpty(request.getString("showMutations")))
				if (request.getString("showMutations").equals("show"))
					this.searchPluginVO.getQueryParametersVO().setShowMutations(true);
				else if (request.getString("showMutations").equals("hide"))
					this.searchPluginVO.getQueryParametersVO().setShowMutations(false);

			if (this.searchPluginVO.getAction().equals("findMutationsByTerm"))
			{
				this.handleFindMutationsByTerm(request);
			}
			else if (this.searchPluginVO.getAction().equals("findMutations"))
			{
				MutationSearchCriteriaVO criteria = new MutationSearchCriteriaVO();
				if (StringUtils.isNotEmpty(request.getString("variation")))
					criteria.setVariation(request.getString("variation"));
				if (StringUtils.isNotEmpty(request.getString("consequence")))
					criteria.setConsequence(request.getString("consequence"));
				if (StringUtils.isNotEmpty(request.getString("mutation_id")))
					criteria.setMutationId(request.getInt("mutation_id"));
				if (StringUtils.isNotEmpty(request.getString("mid")))
					criteria.setMid(request.getString("mid"));
				if (StringUtils.isNotEmpty(request.getString("nuclno")))
					criteria.setCdnaPosition(request.getInt("nuclno"));
				if (StringUtils.isNotEmpty(request.getString("aano")))
					criteria.setCodonChangeNumber(request.getInt("aano"));
				if (StringUtils.isNotEmpty(request.getString("exon_id")))
					criteria.setExonId(request.getInt("exon_id"));
				if (StringUtils.isNotEmpty(request.getString("exon")))
					criteria.setExonName(request.getString("exon"));
				if (StringUtils.isNotEmpty(request.getString("type")))
					criteria.setType(request.getString("type"));
				if (StringUtils.isNotEmpty(request.getString("domain_id")))
					criteria.setProteinDomainId(request.getInt("domain_id"));
				if (StringUtils.isNotEmpty(request.getString("phenotype_id")))
					criteria.setPhenotypeId(request.getInt("phenotype_id"));
				if (StringUtils.isNotEmpty(request.getString("inheritance")))
					criteria.setInheritance(request.getString("inheritance"));
				if (StringUtils.isNotEmpty(request.getString("snpbool")))
					if (request.getString("snpbool").equals("hide"))
						criteria.setReportedAsSNP(false);

				this.searchPluginVO.setMutationSummaryVOs(this.mutationService.findMutations(criteria));
				this.searchPluginVO.setPager(new LimitOffsetPager<MutationSummaryVO>(this.searchPluginVO.getMutationSummaryVOs(), 10, 0));
				this.searchPluginVO.setHeader(this.searchPluginVO.getMutationSummaryVOs().size() + " results.");
			}
			else if (this.searchPluginVO.getAction().equals("findPatients"))
			{
				this.handleFindPatients(request);
			}
			else if (this.searchPluginVO.getAction().equals("listAllMutations"))
			{
				this.handleListAllMutations();
			}
			else if (this.searchPluginVO.getAction().equals("listAllPatients"))
			{
				this.handleListAllPatients(request);
			}
			else if (this.searchPluginVO.getAction().equals("showProteinDomain"))
			{
				this.handleShowProteinDomain(request);
			}
			else if (this.searchPluginVO.getAction().equals("showExon"))
			{
				this.handleShowExon(request);
			}
			else if (this.searchPluginVO.getAction().equals("showMutation"))
			{
				if (StringUtils.isNotEmpty(request.getString("mid")))
					this.mutationSearchCriteriaVO.setMid(request.getString("mid"));
				if (StringUtils.isNotEmpty(request.getString("snpbool")))
					if (request.getString("snpbool").equals("hide"))
						this.mutationSearchCriteriaVO.setReportedAsSNP(false);

				List<MutationSummaryVO> mutationSummaryVOs = this.mutationService.findMutations(mutationSearchCriteriaVO);
				if (mutationSummaryVOs.size() != 1)
					throw new Exception("Unknown mutation id.");
				this.searchPluginVO.setMutationSummaryVO(mutationSummaryVOs.get(0));
				this.searchPluginVO.setHeader("Details for mutation '" + this.mutationSearchCriteriaVO.getMid() + "'");
			}
			else if (this.searchPluginVO.getAction().equals("showFirstMutation"))
			{
				this.handleShowFirstMutation();
			}
			else if (this.searchPluginVO.getAction().equals("showPrevMutation"))
			{
				this.handleShowPrevMutation(request);
			}
			else if (this.searchPluginVO.getAction().equals("showNextMutation"))
			{
				this.handleShowNextMutation(request);
			}
			else if (this.searchPluginVO.getAction().equals("showLastMutation"))
			{
				this.handleShowLastMutation();
			}
			else if (this.searchPluginVO.getAction().equals("showPatient"))
			{
				this.handleShowPatient(request);
			}
			else if (this.searchPluginVO.getAction().equals("showPhenotypeDetails"))
			{
				this.handleShowPhenotypeDetails(request);
			}
			else if (this.searchPluginVO.getAction().startsWith("mutationsFirstPage"))
			{
				this.searchPluginVO.getPager().first();
			}
			else if (this.searchPluginVO.getAction().startsWith("mutationsPrevPage"))
			{
				this.searchPluginVO.getPager().prev();
			}
			else if (this.searchPluginVO.getAction().startsWith("mutationsNextPage"))
			{
				this.searchPluginVO.getPager().next();
			}
			else if (this.searchPluginVO.getAction().startsWith("mutationsLastPage"))
			{
				this.searchPluginVO.getPager().last();
			}
			
			this.populateDisplayOptionsForm();
		}
		catch (Exception e)
		{
			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=SearchPlugin&__action=init&expertSearch=0\">Return to home page</a>";
			this.getMessages().add(new ScreenMessage(message, false));
			for (StackTraceElement el : e.getStackTrace())
				logger.error(el.toString());
//				this.getMessages().add(new ScreenMessage(el.toString(), false));
		}
		
		for (ScreenController<?> child : this.getController().getChildren())
			child.handleRequest(db, request);
	}

	private void handleFindPatients(Tuple request) throws DatabaseException, ParseException, ServletException, IOException
	{
		if (StringUtils.isNotEmpty(request.getString("mid")))
			this.patientSearchCriteriaVO.setMid(request.getString("mid"));
		
		this.searchPluginVO.setPatientSummaryVOs(this.patientService.findPatients(this.patientSearchCriteriaVO));
		this.searchPluginVO.setRawOutput(this.includePage(request));
		this.searchPluginVO.setHeader(this.searchPluginVO.getPatientSummaryVOs().size() + " results for " + this.patientSearchCriteriaVO.toString());
	}

	private void handleFindMutationsByTerm(Tuple request) throws DatabaseException, ParseException, ServletException, IOException
	{
//		if (StringUtils.isNotEmpty(request.getString("term")) && request.getString("term").length() < 3)
//		throw new SearchException("Search term is too general. Please use a more specific one.");

		if (StringUtils.isNotEmpty(request.getString("result")))
			this.searchPluginVO.setResult(request.getString("result"));
		else
			this.searchPluginVO.setResult("mutations"); // Default: Show mutations

		if (StringUtils.isNotEmpty(request.getString("term")))
			this.mutationSearchCriteriaVO.setSearchTerm(request.getString("term"));
	
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);
	
		this.searchPluginVO.setMutationSummaryVOHash(new HashMap<String, LimitOffsetPager<MutationSummaryVO>>());
		this.searchPluginVO.setPatientSummaryVOHash(new HashMap<String, String>());

		MutationSearchCriteriaVO criteria = new MutationSearchCriteriaVO();
		criteria.setVariation(request.getString("term"));
		this.findAndAdd(request, criteria, "variation");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setMid(request.getString("term"));
		this.findAndAdd(request, criteria, "MID");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setInheritance(request.getString("term"));
		this.findAndAdd(request, criteria, "inheritance");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setConsequence(request.getString("term"));
		this.findAndAdd(request, criteria, "consequence");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setPhenotypeName(request.getString("term"));
		this.findAndAdd(request, criteria, "phenotype");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setPid(request.getString("term"));
		this.findAndAdd(request, criteria, "PID");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setPublication(request.getString("term"));
		this.findAndAdd(request, criteria, "publication");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setType(request.getString("term"));
		this.findAndAdd(request, criteria, "mutation type");
	
		if (NumberUtils.isNumber(request.getString("term")))
		{
			criteria = new MutationSearchCriteriaVO();
			criteria.setExonNumber(request.getInt("term"));
			this.findAndAdd(request, criteria, "exon number");
			
			criteria = new MutationSearchCriteriaVO();
			criteria.setCdnaPosition(request.getInt("term"));
			this.findAndAdd(request, criteria, "nucleotide position");
	
			criteria = new MutationSearchCriteriaVO();
			criteria.setCodonChangeNumber(request.getInt("term"));
			this.findAndAdd(request, criteria, "protein position");
		}

		int numres = (this.searchPluginVO.getResult().equals("patients") ? this.searchPluginVO.getPatientSummaryVOHash().keySet().size() : this.searchPluginVO.getMutationSummaryVOHash().keySet().size());
		this.searchPluginVO.setHeader(numres + " results found.");
	}

	private void handleShowNextMutation(Tuple request) throws DatabaseException, ParseException
	{
		this.searchPluginVO.setMutationSummaryVO(this.mutationService.getNextMutation(request.getString("identifier")));
	}

	private void handleShowPrevMutation(Tuple request) throws DatabaseException, ParseException
	{
		this.searchPluginVO.setMutationSummaryVO(mutationService.getPrevMutation(request.getString("mid")));
	}

	private void handleShowLastMutation() throws DatabaseException, ParseException
	{
		this.searchPluginVO.setMutationSummaryVO(mutationService.getLastMutation());
	}

	private void handleShowFirstMutation() throws DatabaseException, ParseException
	{
		this.searchPluginVO.setMutationSummaryVO(mutationService.getFirstMutation());
	}

	private void handleListAllMutations() throws DatabaseException, ParseException
	{
		HashMap<String, List<PatientSummaryVO>> patientSummaryVOs = new HashMap<String, List<PatientSummaryVO>>();
		
		List<PatientSummaryVO> tmp = this.patientService.getAllPatientSummaries();
		for (PatientSummaryVO patientSummaryVO : tmp)
		{
			String key1 = patientSummaryVO.getVariantSummaryVOList().get(0).getIdentifier();

			if (!patientSummaryVOs.containsKey(key1))
				patientSummaryVOs.put(key1, new ArrayList<PatientSummaryVO>());
			
			patientSummaryVOs.get(key1).add(patientSummaryVO);
			
			if (patientSummaryVO.getVariantSummaryVOList().size() > 1)
			{
				String key2 = patientSummaryVO.getVariantSummaryVOList().get(1).getIdentifier();
	
				if (!patientSummaryVOs.containsKey(key2))
					patientSummaryVOs.put(key2, new ArrayList<PatientSummaryVO>());
				
				patientSummaryVOs.get(key2).add(patientSummaryVO);
			}
		}
		
		this.searchPluginVO.setPatientSummaryVOs(this.patientService.getAllPatientSummaries());
		this.searchPluginVO.setMutationSummaryVOs(this.mutationService.getAllMutationSummaries());
		this.searchPluginVO.setPager(new LimitOffsetPager<MutationSummaryVO>(this.searchPluginVO.getMutationSummaryVOs(), 10, 0));
		this.searchPluginVO.setHeader(this.searchPluginVO.getMutationSummaryVOs().size() + " results for \"Display all mutations\".");
	}

	private void handleShowPatient(Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("pid")))
		{
			PatientSearchCriteriaVO criteria = new PatientSearchCriteriaVO();
			criteria.setPid(request.getString("pid"));
			List<PatientSummaryVO> patientSummaryVOs = this.patientService.findPatients(criteria);
			if (patientSummaryVOs.size() != 1)
				throw new Exception("Unknown patient id.");
			this.searchPluginVO.setPatientSummaryVO(patientSummaryVOs.get(0));
			this.searchPluginVO.setHeader("Details for patient '" + criteria.getPid() + "'");
		}	
	}

	private void handleListAllPatients(Tuple request) throws DatabaseException, ParseException, ServletException, IOException
	{
//		MolgenisUser user = new MolgenisUser();
//		user.setId(this.getLogin().getUserId());
//		this.searchPluginVO.setPatientSummaryVOs(this.patientService.find(user));
		List<PatientSummaryVO> patientSummaryVOs = this.patientService.getAllPatientSummaries();
		this.searchPluginVO.setPatientSummaryVOs(patientSummaryVOs);
		this.searchPluginVO.setRawOutput(this.includePage(request));
		this.searchPluginVO.setHeader(this.searchPluginVO.getPatientSummaryVOs().size() + " results for \"Display all patients\".");
	}

	private void handleShowPhenotypeDetails(Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("pid")))
		{
			PatientSearchCriteriaVO criteria = new PatientSearchCriteriaVO();
			criteria.setPid(request.getString("pid"));
			criteria.setConsent(true);
			List<PatientSummaryVO> patientSummaryVOs = this.patientService.findPatients(criteria);
			if (patientSummaryVOs.size() != 1)
				throw new Exception("Unknown patient id.");
			this.searchPluginVO.setPatientSummaryVO(patientSummaryVOs.get(0));

			PhenotypeDetailsVO phenotypeDetailsVO = this.patientService.findPhenotypeDetails(request.getString("pid"));
			this.searchPluginVO.setPhenotypeDetailsVO(phenotypeDetailsVO);

			this.searchPluginVO.setHeader("Phenotypic details for patient '" + criteria.getPid() + "'");
		}
	}

	private void handleShowProteinDomain(Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("domain_id")))
			this.mutationSearchCriteriaVO.setProteinDomainId(request.getInt("domain_id"));
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);

		this.searchPluginVO.setProteinDomainSummaryVO(this.domainService.findProteinDomain(request.getInt("domain_id"), false));
		this.searchPluginVO.setMutationSummaryVOs(this.mutationService.findMutations(mutationSearchCriteriaVO));
		this.searchPluginVO.setPager(new LimitOffsetPager<MutationSummaryVO>(this.searchPluginVO.getMutationSummaryVOs(), 10, 0));

		if (this.searchPluginVO.getProteinDomainSummaryVO() == null)
		{
			this.searchPluginVO.setHeader("Unknown id.");
		}
		else
		{
			if ("R".equals(this.searchPluginVO.getGene().getOrientation()))
				this.domainService.reverseExons(this.searchPluginVO.getProteinDomainSummaryVO());
			this.searchPluginVO.setHeader("");
		}
		if (this.mBrowseVO.getExonList() == null)
		{
			List<Exon> exonList = exonService.getAllExons();
			if ("R".equals(this.searchPluginVO.getGene().getOrientation()))
				Collections.reverse(exonList);
			this.mBrowseVO.setExonList(exonList);
		}
		
		this.populateProteinDomainPanel();
		this.populateExonIntronPanel();
	}

	private void handleShowExon(Tuple request) throws DatabaseException, ParseException, RESyntaxException
	{
		if (StringUtils.isNotEmpty(request.getString("exon_id")))
		{
			this.exonSearchCriteriaVO.setExonId(request.getInt("exon_id"));
			this.mutationSearchCriteriaVO.setExonId(request.getInt("exon_id"));
		}
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);

		this.searchPluginVO.setExonSummaryVO(this.exonService.findExons(exonSearchCriteriaVO).get(0));

		if (this.searchPluginVO.getQueryParametersVO().getShowMutations())
		{
			this.searchPluginVO.setMutationSummaryVOs(this.mutationService.findMutations(mutationSearchCriteriaVO));
			this.searchPluginVO.setPager(new LimitOffsetPager<MutationSummaryVO>(this.searchPluginVO.getMutationSummaryVOs(), 10, 0));
		}
		this.searchPluginVO.setHeader("");
		this.populateSequencePanel();
	}

	@Override
	public void reload(Database db)
	{
		this.exonService              = ExonService.getInstance(db);
		this.mutationService          = MutationService.getInstance(db);
		this.patientService           = PatientService.getInstance(db);
		this.phenotypeService         = PhenotypeService.getInstance(db);
		this.domainService            = ProteinDomainService.getInstance(db);
		this.newsService              = NewsService.getInstance(db);

		try
		{
			List<MutationGene> genes  = db.query(MutationGene.class).equals(MutationGene.NAME, this.GENENAME).find();
			if (genes.size() > 0)
				this.searchPluginVO.setGene(genes.get(0));
			else
				this.searchPluginVO.setGene(new MutationGene());

			if (this.mBrowseVO.getProteinDomainList() == null)
			{
				this.mBrowseVO.setProteinDomainList(this.domainService.getAllProteinDomains());
				//TODO Move this to business logic
//				System.out.println(">>> exons before reverse: " + this.genomeBrowserVO.getProteinDomainList().get(0).getAllExons().toString());
				if ("R".equals(this.searchPluginVO.getGene().getOrientation()))
					this.domainService.reverseExons(this.mBrowseVO.getProteinDomainList());
//				System.out.println(">>> exons after reverse: " + this.genomeBrowserVO.getProteinDomainList().get(0).getAllExons().toString());
			}

			this.searchPluginVO.setNumMutations(this.mutationService.getNumMutations());
			this.searchPluginVO.setNumPatients(this.patientService.getNumPatients());
			this.searchPluginVO.setNumUnpublished(this.patientService.getNumUnpublishedPatients());
			this.searchPluginVO.setNews(this.newsService.getAllNews(5));

			this.populateSimpleSearchForm();
			this.populateListAllMutationsForm();
			this.populateListAllPatientsForm();
			this.populateToSimpleSearchForm();
			this.populateToExpertSearchForm();
			this.populateShowMutationForm();
			this.populateExpertSearchForm();
		}
		catch (Exception e)
		{
			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=SearchPlugin&select=SearchPlugin&__action=init&expertSearch=0\">Return to home page</a>";
			this.getMessages().add(new ScreenMessage(message, false));
			for (StackTraceElement el : e.getStackTrace())
				logger.error(el.toString());
		}
		
		for (ScreenController<?> child : this.getChildren())
			try
			{
				child.reload(db);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
//	private void convert2eav(Database db) throws DatabaseException, ParseException
//	{
//		List<Patient> patients = db.query(Patient.class).find();
//		for (Patient patient : patients)
//		{
//			PhenotypeDetails details = db.findById(PhenotypeDetails.class, patient.getPhenotype_Details_Id());
//			for (String field : details.getFields())
//			{
//				if ("id".equals(field))
//					continue;
//				if (details.get(field) == null)
//					continue;
//				String value = details.get(field).toString();
//				System.out.println("INSERT INTO ObservedValue (Investigation, Feature, Target, __Type, value) SELECT 1, f.id, " + patient.getId() + ", 'ObservedValue', '" + value + "' FROM ObservationElement f WHERE name = '" + field + "';");
//			}
//
//			List<I_F> ifs = db.query(I_F.class).equals(I_F.PATIENT, patient.getId()).find();
//			for (I_F if_ : ifs)
//			{
//				String field = "Amount of type VII collagen";
//				String value = if_.getValue();
//				System.out.println("INSERT INTO ObservedValue (Investigation, Feature, Target, __Type, value) SELECT 1, f.id, " + patient.getId() + ", 'ObservedValue', '" + value + "' FROM ObservationElement f WHERE name = '" + field + "';");
//				String field2 = "IF Retention of type VII Collagen in basal cells";
//				String value2 = if_.getRetention();
//				System.out.println("INSERT INTO ObservedValue (Investigation, Feature, Target, __Type, value) SELECT 1, f.id, " + patient.getId() + ", 'ObservedValue', '" + value2 + "' FROM ObservationElement f WHERE name = '" + field2 + "';");
//			}
//			
//			List<E_M> ems = db.query(E_M.class).equals(E_M.PATIENT, patient.getId()).find();
//			for (E_M em_ : ems)
//			{
//				String field = "Anchoring fibrils Number";
//				String value = em_.getNumber();
//				System.out.println("INSERT INTO ObservedValue (Investigation, Feature, Target, __Type, value) SELECT 1, f.id, " + patient.getId() + ", 'ObservedValue', '" + value + "' FROM ObservationElement f WHERE name = '" + field + "';");
//				String field2 = "Anchoring fibrils Ultrastructure";
//				String value2 = em_.getAppearance();
//				System.out.println("INSERT INTO ObservedValue (Investigation, Feature, Target, __Type, value) SELECT 1, f.id, " + patient.getId() + ", 'ObservedValue', '" + value2 + "' FROM ObservationElement f WHERE name = '" + field2 + "';");
//				String field3 = "EM Retention of type VII Collagen in basal cells";
//				String value3 = em_.getRetention();
//				System.out.println("INSERT INTO ObservedValue (Investigation, Feature, Target, __Type, value) SELECT 1, f.id, " + patient.getId() + ", 'ObservedValue', '" + value3 + "' FROM ObservationElement f WHERE name = '" + field3 + "';");
//			}
//		}
//		
//	}

	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
	
	public SearchPluginVO getSearchPluginVO()
	{
		return this.searchPluginVO;
	}

	public MBrowseVO getMBrowseVO()
	{
		return this.mBrowseVO;
	}

	private String includePage(Tuple request) throws ServletException, IOException
	{
		HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
		HttpServletRequest httpRequest   = rt.getRequest();
		HttpServletResponse httpResponse = rt.getResponse();
		HttpSession httpSession          = httpRequest.getSession();
		RedirectTextWrapper respWrapper  = new RedirectTextWrapper(httpResponse);
			
		httpSession.setAttribute("patientSummaryVOs", searchPluginVO.getPatientSummaryVOs());
			
		// Call/include jsp
		RequestDispatcher dispatcher = httpRequest.getRequestDispatcher("patientPager.jsp");
		if (dispatcher != null)
			dispatcher.include(httpRequest, respWrapper);

		return respWrapper.getOutput();
	}
	/**
	 * Find mutations and add to MutationSummaryVOHash
	 * @param criteria
	 * @param key
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void findAndAdd(Tuple request, MutationSearchCriteriaVO criteria, String key) throws DatabaseException, ParseException, ServletException, IOException
	{
		if (this.searchPluginVO.getResult().equals("patients"))
		{
			List<PatientSummaryVO> patientSummaryVOs = this.mutationService.findPatients(criteria);
			if (patientSummaryVOs.size() > 0)
			{
//				LimitOffsetPager<PatientSummaryVO> pager = new LimitOffsetPager<PatientSummaryVO>(patientSummaryVOs, 10, 0);

				this.searchPluginVO.setPatientSummaryVOs(patientSummaryVOs);
				String rawOutput = this.includePage(request);
				this.searchPluginVO.getPatientSummaryVOHash().put(" " + key + " ", rawOutput);
			}
		}
		else
		{
			List<MutationSummaryVO> mutationSummaryVOs = this.mutationService.findMutations(criteria);
			if (mutationSummaryVOs.size() > 0)
			{
				LimitOffsetPager<MutationSummaryVO> pager = new LimitOffsetPager<MutationSummaryVO>(mutationSummaryVOs, 10, 0);
				this.searchPluginVO.getMutationSummaryVOHash().put(" " + key + " ", pager);
			}
		}
	}

	private void populateSimpleSearchForm()
	{
		this.searchPluginVO.getSimpleSearchForm().get("__target").setValue(this.getController().getName());
		this.searchPluginVO.getSimpleSearchForm().get("select").setValue(this.getController().getName());
		this.searchPluginVO.getSimpleSearchForm().get("result").setValue(this.searchPluginVO.getResult());
		this.searchPluginVO.getSimpleSearchForm().get("term").setValue(this.mutationSearchCriteriaVO.getSearchTerm());
	}

	private void populateListAllMutationsForm()
	{
		this.searchPluginVO.getListAllMutationsForm().get("__target").setValue(this.getController().getName());
		this.searchPluginVO.getListAllMutationsForm().get("select").setValue(this.getController().getName());
	}

	private void populateListAllPatientsForm()
	{
		this.searchPluginVO.getListAllPatientsForm().get("__target").setValue(this.getController().getName());
		this.searchPluginVO.getListAllMutationsForm().get("select").setValue(this.getController().getName());
	}

	private void populateToSimpleSearchForm()
	{
		this.searchPluginVO.getToSimpleSearchForm().get("__target").setValue(this.getController().getName());
	}

	private void populateToExpertSearchForm()
	{
		this.searchPluginVO.getToExpertSearchForm().get("__target").setValue(this.getController().getName());
	}

	private void populateShowMutationForm() throws DatabaseException, ParseException
	{
		this.searchPluginVO.getShowMutationForm().get("__target").setValue(this.getController().getName());
		this.searchPluginVO.getListAllMutationsForm().get("select").setValue(this.getController().getName());
		List<ValueLabel> mutationIdOptions  = new ArrayList<ValueLabel>();
		mutationIdOptions.add(new ValueLabel("", "Select mutation"));
		for (Mutation mutation : this.mutationService.getAllMutations())
			mutationIdOptions.add(new ValueLabel(mutation.getIdentifier(), mutation.getCdna_Notation() + " (" + mutation.getAa_Notation() + ")"));
		((SelectInput) this.searchPluginVO.getShowMutationForm().get("mid")).setOptions(mutationIdOptions);
		((SelectInput) this.searchPluginVO.getShowMutationForm().get("mid")).setValue("Select mutation");
	}

	private void populateExpertSearchForm() throws DatabaseException, ParseException
	{
		Mutation template = new Mutation();
		
		this.searchPluginVO.getExpertSearchForm().get("__target").setValue(this.getController().getName());
		this.searchPluginVO.getExpertSearchForm().get("select").setValue(this.getController().getName());

		if (this.mutationSearchCriteriaVO.getVariation() != null)
			((StringInput) this.searchPluginVO.getExpertSearchForm().get("variation")).setValue(this.mutationSearchCriteriaVO.getVariation());

		if (this.mutationSearchCriteriaVO.getCdnaPosition() != null)
			((IntInput) this.searchPluginVO.getExpertSearchForm().get("nuclno")).setValue(this.mutationSearchCriteriaVO.getCdnaPosition());

		if (this.mutationSearchCriteriaVO.getCodonNumber() != null)
			((IntInput) this.searchPluginVO.getExpertSearchForm().get("aano")).setValue(this.mutationSearchCriteriaVO.getCodonNumber());

		List<ValueLabel> exonIdOptions      = new ArrayList<ValueLabel>();
		exonIdOptions.add(new ValueLabel("", "Select exon/intron"));
		for (Exon exon : this.exonService.getAllExons())
			exonIdOptions.add(new ValueLabel(exon.getId(), exon.getName()));
		((SelectInput) this.searchPluginVO.getExpertSearchForm().get("exon_id")).setOptions(exonIdOptions);
		if (this.mutationSearchCriteriaVO.getExonId() != null)
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("exon_id")).setValue(this.mutationSearchCriteriaVO.getExonId());
		else
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("exon_id")).setValue("Select exon/intron");
		
		List<ValueLabel> typeOptions        = template.getTypeOptions();
		typeOptions.add(0, new ValueLabel("", "Select mutation type"));
		((SelectInput) this.searchPluginVO.getExpertSearchForm().get("type")).setOptions(typeOptions);
		if (this.mutationSearchCriteriaVO.getType() != null)
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("type")).setValue(this.mutationSearchCriteriaVO.getType());
		else
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("type")).setValue("Select mutation type");

		List<ValueLabel> consequenceOptions = template.getConsequenceOptions();
		consequenceOptions.add(0, new ValueLabel("", "Select consequence"));
		((SelectInput) this.searchPluginVO.getExpertSearchForm().get("consequence")).setOptions(consequenceOptions);
		if (this.mutationSearchCriteriaVO.getConsequence() != null)
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("consequence")).setValue(this.mutationSearchCriteriaVO.getConsequence());
		else
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("consequence")).setValue("Select consequence");

		List<ValueLabel> domainOptions      = new ArrayList<ValueLabel>();
		domainOptions.add(new ValueLabel("", "Select protein domain"));
		for (ProteinDomainSummaryVO domainVO : this.domainService.getAllProteinDomains())
			domainOptions.add(new ValueLabel(domainVO.getProteinDomain().getId(), domainVO.getProteinDomain().getName()));
		((SelectInput) this.searchPluginVO.getExpertSearchForm().get("domain_id")).setOptions(domainOptions);
		if (this.mutationSearchCriteriaVO.getProteinDomainId() != null)
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("domain_id")).setValue(this.mutationSearchCriteriaVO.getProteinDomainId());
		else
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("domain_id")).setValue("Select protein domain");
		
		List<ValueLabel> phenotypeOptions   = new ArrayList<ValueLabel>();
		phenotypeOptions.add(new ValueLabel("", "Select phenotype"));
		for (MutationPhenotype phenotype : this.phenotypeService.getAllPhenotypes())
			phenotypeOptions.add(new ValueLabel(phenotype.getId(), phenotype.getMajortype() + ", " + phenotype.getSubtype()));
		((SelectInput) this.searchPluginVO.getExpertSearchForm().get("phenotype_id")).setOptions(phenotypeOptions);
		if (this.mutationSearchCriteriaVO.getPhenotypeId() != null)
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("phenotype_id")).setValue(this.mutationSearchCriteriaVO.getPhenotypeId());
		else
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("phenotype_id")).setValue("Select phenotype");

		List<ValueLabel> inheritanceOptions   = template.getInheritanceOptions();
		inheritanceOptions.add(0, new ValueLabel("", "Select inheritance"));
		((SelectInput) this.searchPluginVO.getExpertSearchForm().get("inheritance")).setOptions(inheritanceOptions);
		if (this.mutationSearchCriteriaVO.getInheritance() != null)
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("inheritance")).setValue(this.mutationSearchCriteriaVO.getInheritance());
		else
			((SelectInput) this.searchPluginVO.getExpertSearchForm().get("inheritance")).setValue("Select inheritance");
	}
	
	private void populateDisplayOptionsForm()
	{
		this.searchPluginVO.getDisplayOptionsForm().get("__target").setValue(this.getController().getName());
		this.searchPluginVO.getDisplayOptionsForm().get("__action").setValue(this.searchPluginVO.getAction());
		
		if (this.mutationSearchCriteriaVO.getProteinDomainId() != null)
			this.searchPluginVO.getDisplayOptionsForm().get("domain_id").setValue(this.mutationSearchCriteriaVO.getProteinDomainId());
		if (this.mutationSearchCriteriaVO.getExonId() != null)
			this.searchPluginVO.getDisplayOptionsForm().get("exon_id").setValue(this.mutationSearchCriteriaVO.getExonId());
		if (this.mutationSearchCriteriaVO.getMid() != null)
			this.searchPluginVO.getDisplayOptionsForm().get("mid").setValue(this.mutationSearchCriteriaVO.getMid());

		if (this.searchPluginVO.getQueryParametersVO().getShowSNP())
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("snpbool")).setValue("show");
		else
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("snpbool")).setValue("hide");

		if (this.searchPluginVO.getQueryParametersVO().getShowIntrons())
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showIntrons")).setValue("show");
		else
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showIntrons")).setValue("hide");

		if (this.searchPluginVO.getQueryParametersVO().getShowNames())
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showNames")).setValue("show");
		else
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showNames")).setValue("hide");

		if (this.searchPluginVO.getQueryParametersVO().getShowNumbering())
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showNumbering")).setValue("show");
		else
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showNumbering")).setValue("hide");

		if (this.searchPluginVO.getQueryParametersVO().getShowMutations())
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showMutations")).setValue("show");
		else
			((SelectInput) this.searchPluginVO.getDisplayOptionsForm().get("showMutations")).setValue("hide");
	}

	private void populateProteinDomainPanel()
	{
		this.mBrowseVO.getProteinDomainPanel().setProteinDomainSummaryVO(this.searchPluginVO.getProteinDomainSummaryVO());
		this.mBrowseVO.getProteinDomainPanel().setScreenName(this.getController().getName());
	}

	private void populateExonIntronPanel()
	{
		this.mBrowseVO.getExonIntronPanel().setExons(this.mBrowseVO.getExonList());
		this.mBrowseVO.getExonIntronPanel().setShowIntrons(this.searchPluginVO.getQueryParametersVO().getShowIntrons());
		this.mBrowseVO.getExonIntronPanel().setScreenName(this.getController().getName());
	}

	private void populateSequencePanel()
	{
		this.mBrowseVO.getSequencePanel().setExonSummaryVO(this.searchPluginVO.getExonSummaryVO());
		this.mBrowseVO.getSequencePanel().setMutationSummaryVOs(this.searchPluginVO.getMutationSummaryVOs());
		this.mBrowseVO.getSequencePanel().setScreenName(this.getController().getName());
	}

	//TODO: Move the following methods to SearchPluginVO

	public int getResultSize()
	{
		int size = 0;
		for (Entry<String, LimitOffsetPager<MutationSummaryVO>> entry : this.searchPluginVO.getMutationSummaryVOHash().entrySet())
			size += entry.getValue().getEntities().size();

		return size;
	}

	public int getNumPatients(List<MutationSummaryVO> mutationSummaryVOs)
	{
		int numPatients = 0;
		for (MutationSummaryVO mutationSummaryVO : mutationSummaryVOs)
			numPatients += mutationSummaryVO.getPatientSummaryVOList().size();
		return numPatients;
	}

	public SearchPluginUtils getSearchPluginUtils()
	{
		return new SearchPluginUtils();
	}
}
