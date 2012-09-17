/* Date:        February 22, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.regexp.RESyntaxException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.IntegratedPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.service.ExonService;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.vo.ExonSearchCriteriaVO;
import org.molgenis.mutation.vo.ExonSummaryVO;
import org.molgenis.mutation.vo.MutationSearchCriteriaVO;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.mutation.vo.PhenotypeDetailsVO;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;
import org.molgenis.mutation.vo.QueryParametersVO;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public abstract class SearchPlugin extends IntegratedPluginController<SearchModel>
{
	private static final long serialVersionUID                = 651270609185006020L;
//	private ExonService exonService;
//	private MutationService mutationService;
//	private PatientService patientService;
//	private SearchService searchService;

	private ExonSearchCriteriaVO exonSearchCriteriaVO         = new ExonSearchCriteriaVO();
	private MutationSearchCriteriaVO mutationSearchCriteriaVO = new MutationSearchCriteriaVO();
	private PatientSearchCriteriaVO patientSearchCriteriaVO   = new PatientSearchCriteriaVO();

	public SearchPlugin(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new SearchModel(this));
		this.setView(new FreemarkerView("init.ftl", getModel()));
		this.getModel().setPatientPager("res/mutation/patientPager.jsp");
		this.getModel().setMutationPager("res/mutation/mutationPager.jsp");
		this.getModel().setPatientViewer("/org/molgenis/mutation/ui/search/patient.ftl");
	}
	
	private ScreenView view = null;
	
	public void setView(ScreenView view)
	{
		this.view = view;
	}
	
	public ScreenView getView()
	{
		if(view == null)
		{
			this.view = new FreemarkerView("MyMutation.ftl", getModel());
		}
		return view;
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
	{
//		this.reload(db);

		try
		{
			this.mutationSearchCriteriaVO = new MutationSearchCriteriaVO();

			if (StringUtils.isEmpty(request.getAction()))
				this.getModel().setAction("init");
			else
				this.getModel().setAction(request.getAction());

			this.getModel().setQueryParametersVO(new QueryParametersVO());

			if ("1".equals(request.getString("expertSearch")))
				this.getModel().getQueryParametersVO().setExpertSearch(true);
			else
				this.getModel().getQueryParametersVO().setExpertSearch(false);

//			if (StringUtils.isNotEmpty(request.getString("snpbool")))
//				if (request.getString("snpbool").equals("show"))
//					this.getModel().getQueryParametersVO().setShowSNP(true);
//				else if (request.getString("snpbool").equals("hide"))
//				{
//					this.getModel().getQueryParametersVO().setShowSNP(false);
//					this.exonSearchCriteriaVO.setIsIntron(false);
//				}
//
//			if (StringUtils.isNotEmpty(request.getString("showIntrons")))
//				if (request.getString("showIntrons").equals("show"))
//				{
//					this.getModel().getQueryParametersVO().setShowIntrons(true);
//					this.exonSearchCriteriaVO.setIsIntron(null);
//				}
//				else if (request.getString("showIntrons").equals("hide"))
//				{
//					this.getModel().getQueryParametersVO().setShowIntrons(false);
//					this.exonSearchCriteriaVO.setIsIntron(false);
//				}
//
//			if (StringUtils.isNotEmpty(request.getString("showNames")))
//				if (request.getString("showNames").equals("show"))
//					this.getModel().getQueryParametersVO().setShowNames(true);
//				else if (request.getString("showNames").equals("hide"))
//					this.getModel().getQueryParametersVO().setShowNames(false);
//
//			if (StringUtils.isNotEmpty(request.getString("showNumbering")))
//				if (request.getString("showNumbering").equals("show"))
//					this.getModel().getQueryParametersVO().setShowNumbering(true);
//				else if (request.getString("showNumbering").equals("hide"))
//					this.getModel().getQueryParametersVO().setShowNumbering(false);
//
//			if (StringUtils.isNotEmpty(request.getString("showMutations")))
//				if (request.getString("showMutations").equals("show"))
//					this.getModel().getQueryParametersVO().setShowMutations(true);
//				else if (request.getString("showMutations").equals("hide"))
//					this.getModel().getQueryParametersVO().setShowMutations(false);

			if (this.getModel().getAction().equals("findMutationsByTerm"))
			{
				this.handleFindMutationsByTerm(db, request);
			}
			else if (this.getModel().getAction().equals("findMutations"))
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
				if (StringUtils.isNotEmpty(request.getString("phenotype")))
					criteria.setPhenotypeName(request.getString("phenotype"));
				if (StringUtils.isNotEmpty(request.getString("inheritance")))
					criteria.setInheritance(request.getString("inheritance"));
				if (StringUtils.isNotEmpty(request.getString("snpbool")))
					if (request.getString("snpbool").equals("hide"))
						criteria.setReportedAsSNP(false);

				MutationService mutationService = new MutationService();
				mutationService.setDatabase(db);
				this.getModel().setMutationSummaryVOs(mutationService.findMutations(criteria));
				((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
				this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));

				this.getModel().setHeader(this.getModel().getMutationSummaryVOs().size() + " results.");
				
				this.setView(new FreemarkerView("included.ftl", this.getModel()));
			}
			else if (this.getModel().getAction().equals("findPatients"))
			{
				this.handleFindPatients(db, request);
			}
			else if (this.getModel().getAction().equals("listAllMutations"))
			{
				this.handleListAllMutations(db, request);
			}
			else if (this.getModel().getAction().equals("listAllPatients"))
			{
				this.handleListAllPatients(db, request);
			}
			else if (this.getModel().getAction().equals("showProteinDomain"))
			{
				this.handleShowProteinDomain(db, request);
			}
			else if (this.getModel().getAction().equals("showExon"))
			{
				this.handleShowExon(db, request);
			}
			else if (this.getModel().getAction().equals("showMutation"))
			{
				this.handleShowMutation(db, request);
			}
			else if (this.getModel().getAction().equals("showFirstMutation"))
			{
				this.handleShowFirstMutation(db);
			}
			else if (this.getModel().getAction().equals("showPrevMutation"))
			{
				this.handleShowPrevMutation(db, request);
			}
			else if (this.getModel().getAction().equals("showNextMutation"))
			{
				this.handleShowNextMutation(db, request);
			}
			else if (this.getModel().getAction().equals("showLastMutation"))
			{
				this.handleShowLastMutation(db);
			}
			else if (this.getModel().getAction().equals("showPatient"))
			{
				this.handleShowPatient(db, request);
			}
			else if (this.getModel().getAction().equals("showPhenotypeDetails"))
			{
				this.handleShowPhenotypeDetails(db, request);
			}
			else if (this.getModel().getAction().startsWith("mutationsFirstPage"))
			{
				this.getModel().getPager().first();
			}
			else if (this.getModel().getAction().startsWith("mutationsPrevPage"))
			{
				this.getModel().getPager().prev();
			}
			else if (this.getModel().getAction().startsWith("mutationsNextPage"))
			{
				this.getModel().getPager().next();
			}
			else if (this.getModel().getAction().startsWith("mutationsLastPage"))
			{
				this.getModel().getPager().last();
			}
			else
			{
				this.setView(new FreemarkerView("init.ftl", this.getModel()));
			}
			
			this.populateDisplayOptionsForm();
		}
		catch (Exception e)
		{
			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=SearchPlugin&__action=init&expertSearch=0\">Return to home page</a>";
			this.getModel().getMessages().add(new ScreenMessage(message, false));
			for (StackTraceElement el : e.getStackTrace())
				logger.error(el.toString());
//				this.getMessages().add(new ScreenMessage(el.toString(), false));
		}

		return Show.SHOW_MAIN;
	}

	private void handleShowMutation(Database db, Tuple request) throws DatabaseException
	{
		if (StringUtils.isNotEmpty(request.getString("mid")))
			this.mutationSearchCriteriaVO.setMid(request.getString("mid"));
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);

		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		List<MutationSummaryVO> mutationSummaryVOs = mutationService.findMutations(mutationSearchCriteriaVO);
		if (mutationSummaryVOs.size() != 1)
			throw new DatabaseException("Unknown mutation id.");
		this.getModel().setMutationSummaryVO(mutationSummaryVOs.get(0));

		this.getModel().setPositionMutations(mutationService.findPositionMutations(mutationSummaryVOs.get(0)));
		this.getModel().setCodonMutations(mutationService.findCodonMutations(mutationSummaryVOs.get(0)));

		this.getModel().setHeader("Details for mutation '" + this.mutationSearchCriteriaVO.getMid() + "'");
		
		this.setView(new FreemarkerView("mutation.ftl", getModel()));
	}

	private void handleFindPatients(Database db, Tuple request) throws DatabaseException
	{
		if (StringUtils.isNotEmpty(request.getString("mid")))
			this.patientSearchCriteriaVO.setMid(request.getString("mid"));
		
		PatientService patientService = new PatientService();
		patientService.setDatabase(db);

		this.getModel().setPatientSummaryVOs(patientService.findPatients(this.patientSearchCriteriaVO));
		((HttpServletRequestTuple) request).getRequest().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getPatientPager()));
		this.getModel().setHeader(this.getModel().getPatientSummaryVOs().size() + " results for " + this.patientSearchCriteriaVO.toString());
		
		this.setView(new FreemarkerView("included.ftl", this.getModel()));
	}

	private void handleFindMutationsByTerm(Database db, Tuple request) throws DatabaseException, ParseException, ServletException, IOException
	{
//		if (StringUtils.isNotEmpty(request.getString("term")) && request.getString("term").length() < 3)
//		throw new SearchException("Search term is too general. Please use a more specific one.");

		if (StringUtils.isNotEmpty(request.getString("result")))
			this.getModel().setResult(request.getString("result"));
		else
			this.getModel().setResult("mutations"); // Default: Show mutations

		if (StringUtils.isNotEmpty(request.getString("term")))
			this.mutationSearchCriteriaVO.setSearchTerm(request.getString("term"));
	
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);
	
		this.getModel().setMutationSummaryVOHash(new HashMap<String, String>());
		this.getModel().setPatientSummaryVOHash(new HashMap<String, String>());

		MutationSearchCriteriaVO criteria = new MutationSearchCriteriaVO();
		criteria.setVariation(request.getString("term"));
		this.findAndAdd(db, request, criteria, "variation");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setMid(request.getString("term"));
		this.findAndAdd(db, request, criteria, "MID");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setInheritance(request.getString("term"));
		this.findAndAdd(db, request, criteria, "inheritance");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setConsequence(request.getString("term"));
		this.findAndAdd(db, request, criteria, "consequence");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setPhenotypeName(request.getString("term"));
		this.findAndAdd(db, request, criteria, "phenotype");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setPid(request.getString("term"));
		this.findAndAdd(db, request, criteria, "PID");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setPublication(request.getString("term"));
		this.findAndAdd(db, request, criteria, "publication");
	
		criteria = new MutationSearchCriteriaVO();
		criteria.setType(request.getString("term"));
		this.findAndAdd(db, request, criteria, "mutation type");
	
		if (NumberUtils.isNumber(request.getString("term")))
		{
			criteria = new MutationSearchCriteriaVO();
			criteria.setExonNumber(request.getInt("term"));
			this.findAndAdd(db, request, criteria, "exon number");
			
			criteria = new MutationSearchCriteriaVO();
			criteria.setCdnaPosition(request.getInt("term"));
			this.findAndAdd(db, request, criteria, "nucleotide position");
	
			criteria = new MutationSearchCriteriaVO();
			criteria.setCodonChangeNumber(request.getInt("term"));
			this.findAndAdd(db, request, criteria, "protein position");
		}

		int numres = (this.getModel().getResult().equals("patients") ? this.getModel().getPatientSummaryVOHash().keySet().size() : this.getModel().getMutationSummaryVOHash().keySet().size());
		this.getModel().setHeader(numres + " results found.");
		
		this.setView(new FreemarkerView("freetext.ftl", this.getModel()));
	}

	private void handleShowNextMutation(Database db, Tuple request) throws DatabaseException
	{
		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		this.getModel().setMutationSummaryVO(mutationService.getNextMutation(request.getString("identifier")));
		this.setView(new FreemarkerView("mutation.ftl", getModel()));
	}

	private void handleShowPrevMutation(Database db, Tuple request) throws DatabaseException
	{
		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		this.getModel().setMutationSummaryVO(mutationService.getPrevMutation(request.getString("mid")));
		this.setView(new FreemarkerView("mutation.ftl", getModel()));
	}

	private void handleShowLastMutation(Database db) throws DatabaseException
	{
		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		this.getModel().setMutationSummaryVO(mutationService.getLastMutation());
		this.setView(new FreemarkerView("mutation.ftl", getModel()));
	}

	private void handleShowFirstMutation(Database db) throws DatabaseException
	{
		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		this.getModel().setMutationSummaryVO(mutationService.getFirstMutation());
		this.setView(new FreemarkerView("mutation.ftl", getModel()));
	}

	private void handleListAllMutations(Database db, Tuple request) throws DatabaseException, ParseException
	{
		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		this.getModel().setMutationSummaryVOs(mutationService.getAllMutationSummaries());
		((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));

		this.getModel().setHeader(this.getModel().getMutationSummaryVOs().size() + " results for \"Display all mutations\".");
		this.setView(new FreemarkerView("included.ftl", getModel()));
	}

	private void handleShowPatient(Database db, Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("pid")))
		{
			PatientService patientService = new PatientService();
			patientService.setDatabase(db);

			PatientSearchCriteriaVO criteria = new PatientSearchCriteriaVO();
			criteria.setPid(request.getString("pid"));
			List<PatientSummaryVO> patientSummaryVOs = patientService.findPatients(criteria);
			if (patientSummaryVOs.size() != 1)
				throw new Exception("Unknown patient id.");
			this.getModel().setPatientSummaryVO(patientSummaryVOs.get(0));
			this.getModel().setHeader("Details for patient '" + criteria.getPid() + "'");
			
			this.setView(new FreemarkerView(this.getModel().getPatientViewer(), getModel()));
		}	
	}

	private void handleListAllPatients(Database db, Tuple request) throws DatabaseException
	{
//		MolgenisUser user = new MolgenisUser();
//		user.setId(this.getLogin().getUserId());
//		this.getModel().setPatientSummaryVOs(this.patientService.find(user));
		PatientService patientService = new PatientService();
		patientService.setDatabase(db);

		List<PatientSummaryVO> patientSummaryVOs = patientService.getAllPatientSummaries();
		this.getModel().setPatientSummaryVOs(patientSummaryVOs);
		((HttpServletRequestTuple) request).getRequest().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getPatientPager()));
		this.getModel().setHeader(this.getModel().getPatientSummaryVOs().size() + " results for \"Display all patients\".");
		
		this.setView(new FreemarkerView("included.ftl", this.getModel()));
	}

	private void handleShowPhenotypeDetails(Database db, Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("pid")))
		{
			PatientService patientService = new PatientService();
			patientService.setDatabase(db);

			PatientSearchCriteriaVO criteria = new PatientSearchCriteriaVO();
			criteria.setPid(request.getString("pid"));
			criteria.setConsent(true);
			List<PatientSummaryVO> patientSummaryVOs = patientService.findPatients(criteria);
			if (patientSummaryVOs.size() != 1)
				throw new Exception("Unknown patient id.");
			this.getModel().setPatientSummaryVO(patientSummaryVOs.get(0));

			PhenotypeDetailsVO phenotypeDetailsVO = patientService.findPhenotypeDetails(request.getString("pid"));
			this.getModel().setPhenotypeDetailsVO(phenotypeDetailsVO);

			this.getModel().setHeader("Phenotypic details for patient '" + criteria.getPid() + "'");
			
			this.setView(new FreemarkerView("phenotypedetails.ftl", getModel()));
		}
	}

	private void handleShowProteinDomain(Database db, Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("domain_id")))
			this.mutationSearchCriteriaVO.setProteinDomainId(request.getInt("domain_id"));
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);

		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		SearchService searchService = new SearchService();
		searchService.setDatabase(db);

		this.getModel().setProteinDomainSummaryVO(searchService.findProteinDomain(request.getInt("domain_id"), false));
		this.getModel().setMutationSummaryVOs(mutationService.findMutations(mutationSearchCriteriaVO));
		((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));

		if (this.getModel().getProteinDomainSummaryVO() == null)
		{
			this.getModel().setHeader("Unknown id.");
		}
		else
		{
			if ("R".equals(this.getModel().getGene().getOrientation()))
				this.reverseExons(this.getModel().getProteinDomainSummaryVO());
			this.getModel().setHeader("");
		}
		if (this.getModel().getmBrowseVO().getExonList() == null)
		{
			ExonService exonService = new ExonService();
			exonService.setDatabase(db);

			List<ExonSummaryVO> exonList = exonService.getAllExons();
			if ("R".equals(this.getModel().getGene().getOrientation()))
				Collections.reverse(exonList);
			this.getModel().getmBrowseVO().setExonList(exonList);
		}
		
		this.populateProteinDomainPanel();
		this.populateExonIntronPanel();
		
		this.setView(new FreemarkerView("proteindomain.ftl", getModel()));
	}

	private void handleShowExon(Database db, Tuple request) throws DatabaseException, RESyntaxException
	{
		if (StringUtils.isNotEmpty(request.getString("exon_id")))
		{
			this.exonSearchCriteriaVO.setExonId(request.getInt("exon_id"));
			this.mutationSearchCriteriaVO.setExonId(request.getInt("exon_id"));
		}
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);

		ExonService exonService = new ExonService();
		exonService.setDatabase(db);

		this.getModel().setExonSummaryVO(exonService.findExons(exonSearchCriteriaVO).get(0));

		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		if (this.getModel().getQueryParametersVO().getShowMutations())
		{
			this.getModel().setMutationSummaryVOs(mutationService.findMutations(mutationSearchCriteriaVO));
			((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
			this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));
		}
		this.getModel().setHeader("");
		this.populateSequencePanel();
		
		this.setView(new FreemarkerView("exon.ftl", getModel()));
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			List<MutationGene> genes  = db.query(MutationGene.class).equals(MutationGene.NAME, this.getModel().getGeneName()).find();
			if (genes.size() > 0)
				this.getModel().setGene(genes.get(0));
			else
				this.getModel().setGene(new MutationGene());

			if (this.getModel().getmBrowseVO().getProteinDomainList() == null)
			{
				SearchService searchService = new SearchService();
				searchService.setDatabase(db);

				this.getModel().getmBrowseVO().setProteinDomainList(searchService.getAllProteinDomains());
				//TODO Move this to business logic
//				System.out.println(">>> exons before reverse: " + this.genomeBrowserVO.getProteinDomainList().get(0).getAllExons().toString());
				if ("R".equals(this.getModel().getGene().getOrientation()))
					this.reverseExons(this.getModel().getmBrowseVO().getProteinDomainList());
//				System.out.println(">>> exons after reverse: " + this.genomeBrowserVO.getProteinDomainList().get(0).getAllExons().toString());
			}

			MutationService mutationService = new MutationService();
			mutationService.setDatabase(db);
			PatientService patientService   = new PatientService();
			patientService.setDatabase(db);

			this.getModel().setNumMutations(mutationService.getNumMutations());
			this.getModel().setNumPatients(patientService.getNumPatients());
			this.getModel().setNumUnpublished(patientService.getNumUnpublishedPatients());
//			this.getModel().setNews(this.newsService.getAllNews(5));

			this.populateGenePanel();
			this.populateSimpleSearchForm();
			this.populateListAllMutationsForm();
			this.populateListAllPatientsForm();
			this.populateToSimpleSearchForm();
			this.populateToExpertSearchForm();
			this.populateShowMutationForm(db);
			this.populateExpertSearchForm(db);
		}
		catch (Exception e)
		{
			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=SearchPlugin&select=SearchPlugin&__action=init&expertSearch=0\">Return to home page</a>";
			this.getModel().getMessages().add(new ScreenMessage(message, false));
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

	/**
	 * Find mutations and add to MutationSummaryVOHash
	 * @param criteria
	 * @param key
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private void findAndAdd(Database db, Tuple request, MutationSearchCriteriaVO criteria, String key) throws DatabaseException, ParseException, ServletException, IOException
	{
		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);

		if (this.getModel().getResult().equals("patients"))
		{
			List<PatientSummaryVO> patientSummaryVOs = mutationService.findPatients(criteria);
			if (patientSummaryVOs.size() > 0)
			{
				this.getModel().setPatientSummaryVOs(patientSummaryVOs);
				((HttpServletRequestTuple) request).getRequest().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOs());
				this.getModel().getPatientSummaryVOHash().put(" " + key + " ", this.include(request, this.getModel().getPatientPager()));
			}
		}
		else
		{
			List<MutationSummaryVO> mutationSummaryVOs = mutationService.findMutations(criteria);
			if (mutationSummaryVOs.size() > 0)
			{
				this.getModel().setMutationSummaryVOs(mutationSummaryVOs);
				((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
				this.getModel().getMutationSummaryVOHash().put(" " + key + " ", this.include(request, this.getModel().getMutationPager()));
			}
		}
	}

	private void populateSimpleSearchForm()
	{
		this.getModel().getSimpleSearchForm().get("__target").setValue(this.getName());
		this.getModel().getSimpleSearchForm().get("select").setValue(this.getName());
		this.getModel().getSimpleSearchForm().get("result").setValue(this.getModel().getResult());
		this.getModel().getSimpleSearchForm().get("term").setValue(this.mutationSearchCriteriaVO.getSearchTerm());
	}

	private void populateListAllMutationsForm()
	{
		this.getModel().getListAllMutationsForm().get("__target").setValue(this.getName());
		this.getModel().getListAllMutationsForm().get("select").setValue(this.getName());
	}

	private void populateListAllPatientsForm()
	{
		this.getModel().getListAllPatientsForm().get("__target").setValue(this.getName());
		this.getModel().getListAllMutationsForm().get("select").setValue(this.getName());
	}

	private void populateToSimpleSearchForm()
	{
		this.getModel().getToSimpleSearchForm().get("__target").setValue(this.getName());
	}

	private void populateToExpertSearchForm()
	{
		this.getModel().getToExpertSearchForm().get("__target").setValue(this.getName());
	}

	private void populateShowMutationForm(Database db) throws DatabaseException, ParseException
	{
		SearchService searchService = new SearchService();
		searchService.setDatabase(db);

		this.getModel().getShowMutationForm().get("__target").setValue(this.getName());
		this.getModel().getListAllMutationsForm().get("select").setValue(this.getName());
		List<ValueLabel> mutationIdOptions  = new ArrayList<ValueLabel>();
		mutationIdOptions.add(new ValueLabel("", "Select mutation"));
		for (Mutation mutation : searchService.getAllMutations())
			mutationIdOptions.add(new ValueLabel(mutation.getIdentifier(), mutation.getCdna_Notation() + " (" + mutation.getAa_Notation() + ")"));
		((SelectInput) this.getModel().getShowMutationForm().get("mid")).setOptions(mutationIdOptions);
		((SelectInput) this.getModel().getShowMutationForm().get("mid")).setValue("Select mutation");
	}

	private void populateExpertSearchForm(Database db) throws DatabaseException, ParseException, SQLException
	{
		ExonService exonService = new ExonService();
		exonService.setDatabase(db);
		MutationService mutationService = new MutationService();
		mutationService.setDatabase(db);
		SearchService searchService = new SearchService();
		searchService.setDatabase(db);

		Mutation template = new Mutation();
		
		this.getModel().getExpertSearchForm().get("__target").setValue(this.getName());
		this.getModel().getExpertSearchForm().get("select").setValue(this.getName());

		if (this.mutationSearchCriteriaVO.getVariation() != null)
			((StringInput) this.getModel().getExpertSearchForm().get("variation")).setValue(this.mutationSearchCriteriaVO.getVariation());

		if (this.mutationSearchCriteriaVO.getCdnaPosition() != null)
			((IntInput) this.getModel().getExpertSearchForm().get("nuclno")).setValue(this.mutationSearchCriteriaVO.getCdnaPosition());

		if (this.mutationSearchCriteriaVO.getCodonNumber() != null)
			((IntInput) this.getModel().getExpertSearchForm().get("aano")).setValue(this.mutationSearchCriteriaVO.getCodonNumber());

		List<ValueLabel> exonIdOptions      = new ArrayList<ValueLabel>();
		exonIdOptions.add(new ValueLabel("", "Select exon/intron"));
		for (ExonSummaryVO exonSummaryVO : exonService.getAllExons())
			exonIdOptions.add(new ValueLabel(exonSummaryVO.getExon().getId(), exonSummaryVO.getExon().getName()));
		((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setOptions(exonIdOptions);
		if (this.mutationSearchCriteriaVO.getExonId() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setValue(this.mutationSearchCriteriaVO.getExonId());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setValue("Select exon/intron");

		List<ValueLabel> typeOptions        = new ArrayList<ValueLabel>();
		typeOptions.add(0, new ValueLabel("", "Select mutation type"));
		for (String mutationType : mutationService.getMutationTypes())
			typeOptions.add(new ValueLabel(mutationType, mutationType));
		((SelectInput) this.getModel().getExpertSearchForm().get("type")).setOptions(typeOptions);
		if (this.mutationSearchCriteriaVO.getType() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("type")).setValue(this.mutationSearchCriteriaVO.getType());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("type")).setValue("Select mutation type");

		List<ValueLabel> consequenceOptions = template.getConsequenceOptions();
		consequenceOptions.add(0, new ValueLabel("", "Select consequence"));
		((SelectInput) this.getModel().getExpertSearchForm().get("consequence")).setOptions(consequenceOptions);
		if (this.mutationSearchCriteriaVO.getConsequence() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("consequence")).setValue(this.mutationSearchCriteriaVO.getConsequence());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("consequence")).setValue("Select consequence");

		List<MutationGene> genes  = db.query(MutationGene.class).equals(MutationGene.NAME, this.getModel().getGeneName()).find();
		if (genes.size() != 1)
			throw new DatabaseException("Not exactly one gene found for " + this.getModel().getGeneName());

		List<ValueLabel> domainOptions      = new ArrayList<ValueLabel>();
		domainOptions.add(new ValueLabel("", "Select protein domain"));
		for (ProteinDomainSummaryVO domainVO : searchService.getAllProteinDomains())
			domainOptions.add(new ValueLabel(domainVO.getProteinDomain().getId(), domainVO.getProteinDomain().getName()));
		((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setOptions(domainOptions);
		if (this.mutationSearchCriteriaVO.getProteinDomainId() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setValue(this.mutationSearchCriteriaVO.getProteinDomainId());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setValue("Select protein domain");
		
		List<ValueLabel> phenotypeOptions   = new ArrayList<ValueLabel>();
		phenotypeOptions.add(new ValueLabel("", "Select phenotype"));
		for (String phenotypeName : searchService.getAllPhenotypes())
			phenotypeOptions.add(new ValueLabel(phenotypeName, phenotypeName));
		((SelectInput) this.getModel().getExpertSearchForm().get("phenotype")).setOptions(phenotypeOptions);
		if (this.mutationSearchCriteriaVO.getPhenotypeId() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("phenotype")).setValue(this.mutationSearchCriteriaVO.getPhenotypeId());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("phenotype")).setValue("Select phenotype");

		List<ValueLabel> inheritanceOptions   = template.getInheritanceOptions();
		inheritanceOptions.add(0, new ValueLabel("", "Select inheritance"));
		((SelectInput) this.getModel().getExpertSearchForm().get("inheritance")).setOptions(inheritanceOptions);
		if (this.mutationSearchCriteriaVO.getInheritance() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("inheritance")).setValue(this.mutationSearchCriteriaVO.getInheritance());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("inheritance")).setValue("Select inheritance");
	}
	
	private void populateDisplayOptionsForm()
	{
		this.getModel().getDisplayOptionsForm().get("__target").setValue(this.getName());
		this.getModel().getDisplayOptionsForm().get("__action").setValue(this.getModel().getAction());
		
		if (this.mutationSearchCriteriaVO.getProteinDomainId() != null)
			this.getModel().getDisplayOptionsForm().get("domain_id").setValue(this.mutationSearchCriteriaVO.getProteinDomainId());
		if (this.mutationSearchCriteriaVO.getExonId() != null)
			this.getModel().getDisplayOptionsForm().get("exon_id").setValue(this.mutationSearchCriteriaVO.getExonId());
		if (this.mutationSearchCriteriaVO.getMid() != null)
			this.getModel().getDisplayOptionsForm().get("mid").setValue(this.mutationSearchCriteriaVO.getMid());

		if (this.getModel().getQueryParametersVO().getShowSNP())
			((SelectInput) this.getModel().getDisplayOptionsForm().get("snpbool")).setValue("show");
		else
			((SelectInput) this.getModel().getDisplayOptionsForm().get("snpbool")).setValue("hide");

		if (this.getModel().getQueryParametersVO().getShowIntrons())
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showIntrons")).setValue("show");
		else
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showIntrons")).setValue("hide");

		if (this.getModel().getQueryParametersVO().getShowNames())
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showNames")).setValue("show");
		else
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showNames")).setValue("hide");

		if (this.getModel().getQueryParametersVO().getShowNumbering())
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showNumbering")).setValue("show");
		else
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showNumbering")).setValue("hide");

		if (this.getModel().getQueryParametersVO().getShowMutations())
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showMutations")).setValue("show");
		else
			((SelectInput) this.getModel().getDisplayOptionsForm().get("showMutations")).setValue("hide");
	}

	private void populateGenePanel()
	{
		this.getModel().getmBrowseVO().getGenePanel().setProteinDomainSummaryVOList(this.getModel().getmBrowseVO().getProteinDomainList());
		this.getModel().getmBrowseVO().getGenePanel().setBaseUrl("molgenis.do?__target=" + this.getName() + "&select=" + this.getName() + "&__action=showProteinDomain&domain_id=&snpbool=1#exon");
	}

	private void populateProteinDomainPanel()
	{
		this.getModel().getmBrowseVO().getProteinDomainPanel().setProteinDomainSummaryVO(this.getModel().getProteinDomainSummaryVO());
		this.getModel().getmBrowseVO().getProteinDomainPanel().setBaseUrl("molgenis.do?__target=" + this.getName() + "&select=" + this.getName() + "&__action=showProteinDomain&domain_id=&snpbool=1#exon");
	}

	private void populateExonIntronPanel()
	{
		this.getModel().getmBrowseVO().getExonIntronPanel().setExons(this.getModel().getmBrowseVO().getExonList());
		this.getModel().getmBrowseVO().getExonIntronPanel().setShowIntrons(this.getModel().getQueryParametersVO().getShowIntrons());
		this.getModel().getmBrowseVO().getExonIntronPanel().setBaseUrl("molgenis.do?__target=" + this.getName() + "&select=" + this.getName() + "&__action=showExon&exon_id=#results");
	}

	private void populateSequencePanel()
	{
		this.getModel().getmBrowseVO().getSequencePanel().setExonSummaryVO(this.getModel().getExonSummaryVO());
		this.getModel().getmBrowseVO().getSequencePanel().setMutationSummaryVOs(this.getModel().getMutationSummaryVOs());
		this.getModel().getmBrowseVO().getSequencePanel().setBaseUrl("molgenis.do?__target=" + this.getName() + "&select=" + this.getName() + "&__action=showMutation&mid=#results");
	}
	
	private void reverseExons(List<ProteinDomainSummaryVO> proteinDomainList)
	{
		Iterator<ProteinDomainSummaryVO> it = proteinDomainList.iterator();
		while (it.hasNext())
		{
			ProteinDomainSummaryVO proteinDomainSummaryVO = it.next();
			this.reverseExons(proteinDomainSummaryVO);
		}
	}
	
	private void reverseExons(ProteinDomainSummaryVO proteinDomainSummaryVO)
	{
		Collections.reverse(proteinDomainSummaryVO.getExons());
	}
}
