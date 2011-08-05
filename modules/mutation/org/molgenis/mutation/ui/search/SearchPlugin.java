/* Date:        February 22, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.regexp.RESyntaxException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
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
import org.molgenis.mutation.vo.MutationSearchCriteriaVO;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.mutation.vo.PhenotypeDetailsVO;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;
import org.molgenis.mutation.vo.QueryParametersVO;
//import org.molgenis.news.service.NewsService;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public class SearchPlugin extends EasyPluginController<SearchModel>
{
	private static final long serialVersionUID                = 651270609185006020L;
	private ExonService exonService;
	private MutationService mutationService;
	private PatientService patientService;
	private PhenotypeService phenotypeService;
	private ProteinDomainService domainService;

	private ExonSearchCriteriaVO exonSearchCriteriaVO         = new ExonSearchCriteriaVO();
	private MutationSearchCriteriaVO mutationSearchCriteriaVO = new MutationSearchCriteriaVO();
	private PatientSearchCriteriaVO patientSearchCriteriaVO   = new PatientSearchCriteriaVO();

	public SearchPlugin(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new SearchModel(this));
		this.setView(new FreemarkerView("SearchPlugin.ftl", getModel()));
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.reload(db);

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

			if (StringUtils.isNotEmpty(request.getString("snpbool")))
				if (request.getString("snpbool").equals("show"))
					this.getModel().getQueryParametersVO().setShowSNP(true);
				else if (request.getString("snpbool").equals("hide"))
				{
					this.getModel().getQueryParametersVO().setShowSNP(false);
					this.exonSearchCriteriaVO.setIsIntron(false);
				}

			if (StringUtils.isNotEmpty(request.getString("showIntrons")))
				if (request.getString("showIntrons").equals("show"))
				{
					this.getModel().getQueryParametersVO().setShowIntrons(true);
					this.exonSearchCriteriaVO.setIsIntron(null);
				}
				else if (request.getString("showIntrons").equals("hide"))
				{
					this.getModel().getQueryParametersVO().setShowIntrons(false);
					this.exonSearchCriteriaVO.setIsIntron(false);
				}

			if (StringUtils.isNotEmpty(request.getString("showNames")))
				if (request.getString("showNames").equals("show"))
					this.getModel().getQueryParametersVO().setShowNames(true);
				else if (request.getString("showNames").equals("hide"))
					this.getModel().getQueryParametersVO().setShowNames(false);

			if (StringUtils.isNotEmpty(request.getString("showNumbering")))
				if (request.getString("showNumbering").equals("show"))
					this.getModel().getQueryParametersVO().setShowNumbering(true);
				else if (request.getString("showNumbering").equals("hide"))
					this.getModel().getQueryParametersVO().setShowNumbering(false);

			if (StringUtils.isNotEmpty(request.getString("showMutations")))
				if (request.getString("showMutations").equals("show"))
					this.getModel().getQueryParametersVO().setShowMutations(true);
				else if (request.getString("showMutations").equals("hide"))
					this.getModel().getQueryParametersVO().setShowMutations(false);

			if (this.getModel().getAction().equals("findMutationsByTerm"))
			{
				this.handleFindMutationsByTerm(request);
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
				if (StringUtils.isNotEmpty(request.getString("phenotype_id")))
					criteria.setPhenotypeId(request.getInt("phenotype_id"));
				if (StringUtils.isNotEmpty(request.getString("inheritance")))
					criteria.setInheritance(request.getString("inheritance"));
				if (StringUtils.isNotEmpty(request.getString("snpbool")))
					if (request.getString("snpbool").equals("hide"))
						criteria.setReportedAsSNP(false);

				this.getModel().setMutationSummaryVOs(this.mutationService.findMutations(criteria));
				this.getModel().setPager(new LimitOffsetPager<MutationSummaryVO>(this.getModel().getMutationSummaryVOs(), 10, 0));
				this.getModel().setHeader(this.getModel().getMutationSummaryVOs().size() + " results.");
			}
			else if (this.getModel().getAction().equals("findPatients"))
			{
				this.handleFindPatients(request);
			}
			else if (this.getModel().getAction().equals("listAllMutations"))
			{
				this.handleListAllMutations(request);
			}
			else if (this.getModel().getAction().equals("listAllPatients"))
			{
				this.handleListAllPatients(request);
			}
			else if (this.getModel().getAction().equals("showProteinDomain"))
			{
				this.handleShowProteinDomain(request);
			}
			else if (this.getModel().getAction().equals("showExon"))
			{
				this.handleShowExon(request);
			}
			else if (this.getModel().getAction().equals("showMutation"))
			{
				if (StringUtils.isNotEmpty(request.getString("mid")))
					this.mutationSearchCriteriaVO.setMid(request.getString("mid"));
				if (StringUtils.isNotEmpty(request.getString("snpbool")))
					if (request.getString("snpbool").equals("hide"))
						this.mutationSearchCriteriaVO.setReportedAsSNP(false);

				List<MutationSummaryVO> mutationSummaryVOs = this.mutationService.findMutations(mutationSearchCriteriaVO);
				if (mutationSummaryVOs.size() != 1)
					throw new Exception("Unknown mutation id.");
				this.getModel().setMutationSummaryVO(mutationSummaryVOs.get(0));
				this.getModel().setHeader("Details for mutation '" + this.mutationSearchCriteriaVO.getMid() + "'");
			}
			else if (this.getModel().getAction().equals("showFirstMutation"))
			{
				this.handleShowFirstMutation();
			}
			else if (this.getModel().getAction().equals("showPrevMutation"))
			{
				this.handleShowPrevMutation(request);
			}
			else if (this.getModel().getAction().equals("showNextMutation"))
			{
				this.handleShowNextMutation(request);
			}
			else if (this.getModel().getAction().equals("showLastMutation"))
			{
				this.handleShowLastMutation();
			}
			else if (this.getModel().getAction().equals("showPatient"))
			{
				this.handleShowPatient(request);
			}
			else if (this.getModel().getAction().equals("showPhenotypeDetails"))
			{
				this.handleShowPhenotypeDetails(request);
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
		
		for (ScreenController<?> child : this.getChildren())
			child.handleRequest(db, request);
	}

	private void handleFindPatients(Tuple request) throws DatabaseException, ParseException
	{
		if (StringUtils.isNotEmpty(request.getString("mid")))
			this.patientSearchCriteriaVO.setMid(request.getString("mid"));
		
		this.getModel().setPatientSummaryVOs(this.patientService.findPatients(this.patientSearchCriteriaVO));
		((HttpServletRequestTuple) request).getRequest().getSession().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getPatientPager()));
		this.getModel().setHeader(this.getModel().getPatientSummaryVOs().size() + " results for " + this.patientSearchCriteriaVO.toString());
	}

	private void handleFindMutationsByTerm(Tuple request) throws DatabaseException, ParseException, ServletException, IOException
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

		int numres = (this.getModel().getResult().equals("patients") ? this.getModel().getPatientSummaryVOHash().keySet().size() : this.getModel().getMutationSummaryVOHash().keySet().size());
		this.getModel().setHeader(numres + " results found.");
	}

	private void handleShowNextMutation(Tuple request) throws DatabaseException
	{
		this.getModel().setMutationSummaryVO(this.mutationService.getNextMutation(request.getString("identifier")));
	}

	private void handleShowPrevMutation(Tuple request) throws DatabaseException
	{
		this.getModel().setMutationSummaryVO(mutationService.getPrevMutation(request.getString("mid")));
	}

	private void handleShowLastMutation() throws DatabaseException
	{
		this.getModel().setMutationSummaryVO(mutationService.getLastMutation());
	}

	private void handleShowFirstMutation() throws DatabaseException
	{
		this.getModel().setMutationSummaryVO(mutationService.getFirstMutation());
	}

	private void handleListAllMutations(Tuple request) throws DatabaseException, ParseException
	{
//		HashMap<String, List<PatientSummaryVO>> patientSummaryVOs = new HashMap<String, List<PatientSummaryVO>>();
//		
//		List<PatientSummaryVO> tmp = this.patientService.getAllPatientSummaries();
//		for (PatientSummaryVO patientSummaryVO : tmp)
//		{
//			String key1 = patientSummaryVO.getVariantSummaryVOList().get(0).getIdentifier();
//
//			if (!patientSummaryVOs.containsKey(key1))
//				patientSummaryVOs.put(key1, new ArrayList<PatientSummaryVO>());
//			
//			patientSummaryVOs.get(key1).add(patientSummaryVO);
//			
//			if (patientSummaryVO.getVariantSummaryVOList().size() > 1)
//			{
//				String key2 = patientSummaryVO.getVariantSummaryVOList().get(1).getIdentifier();
//	
//				if (!patientSummaryVOs.containsKey(key2))
//					patientSummaryVOs.put(key2, new ArrayList<PatientSummaryVO>());
//				
//				patientSummaryVOs.get(key2).add(patientSummaryVO);
//			}
//		}
//		
//		this.getModel().setPatientSummaryVOs(this.patientService.getAllPatientSummaries());
		this.getModel().setMutationSummaryVOs(this.mutationService.getAllMutationSummaries());
//		this.getModel().setPager(new LimitOffsetPager<MutationSummaryVO>(this.getModel().getMutationSummaryVOs(), 10, 0));
		((HttpServletRequestTuple) request).getRequest().getSession().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));

		this.getModel().setHeader(this.getModel().getMutationSummaryVOs().size() + " results for \"Display all mutations\".");
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
			this.getModel().setPatientSummaryVO(patientSummaryVOs.get(0));
			this.getModel().setHeader("Details for patient '" + criteria.getPid() + "'");
		}	
	}

	private void handleListAllPatients(Tuple request) throws DatabaseException, ParseException
	{
//		MolgenisUser user = new MolgenisUser();
//		user.setId(this.getLogin().getUserId());
//		this.getModel().setPatientSummaryVOs(this.patientService.find(user));
		List<PatientSummaryVO> patientSummaryVOs = this.patientService.getAllPatientSummaries();
		this.getModel().setPatientSummaryVOs(patientSummaryVOs);
		((HttpServletRequestTuple) request).getRequest().getSession().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getPatientPager()));
		this.getModel().setHeader(this.getModel().getPatientSummaryVOs().size() + " results for \"Display all patients\".");
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
			this.getModel().setPatientSummaryVO(patientSummaryVOs.get(0));

			PhenotypeDetailsVO phenotypeDetailsVO = this.patientService.findPhenotypeDetails(request.getString("pid"));
			this.getModel().setPhenotypeDetailsVO(phenotypeDetailsVO);

			this.getModel().setHeader("Phenotypic details for patient '" + criteria.getPid() + "'");
		}
	}

	private void handleShowProteinDomain(Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("domain_id")))
			this.mutationSearchCriteriaVO.setProteinDomainId(request.getInt("domain_id"));
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.mutationSearchCriteriaVO.setReportedAsSNP(false);

		this.getModel().setProteinDomainSummaryVO(this.domainService.findProteinDomain(request.getInt("domain_id"), false));
		this.getModel().setMutationSummaryVOs(this.mutationService.findMutations(mutationSearchCriteriaVO));
		((HttpServletRequestTuple) request).getRequest().getSession().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));
//		this.getModel().setPager(new LimitOffsetPager<MutationSummaryVO>(this.getModel().getMutationSummaryVOs(), 10, 0));

		if (this.getModel().getProteinDomainSummaryVO() == null)
		{
			this.getModel().setHeader("Unknown id.");
		}
		else
		{
			if ("R".equals(this.getModel().getGene().getOrientation()))
				this.domainService.reverseExons(this.getModel().getProteinDomainSummaryVO());
			this.getModel().setHeader("");
		}
		if (this.getModel().getmBrowseVO().getExonList() == null)
		{
			List<Exon> exonList = exonService.getAllExons();
			if ("R".equals(this.getModel().getGene().getOrientation()))
				Collections.reverse(exonList);
			this.getModel().getmBrowseVO().setExonList(exonList);
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

		this.getModel().setExonSummaryVO(this.exonService.findExons(exonSearchCriteriaVO).get(0));

		if (this.getModel().getQueryParametersVO().getShowMutations())
		{
			this.getModel().setMutationSummaryVOs(this.mutationService.findMutations(mutationSearchCriteriaVO));
			((HttpServletRequestTuple) request).getRequest().getSession().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
			this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));
//			this.getModel().setPager(new LimitOffsetPager<MutationSummaryVO>(this.getModel().getMutationSummaryVOs(), 10, 0));
		}
		this.getModel().setHeader("");
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
//		this.newsService              = NewsService.getInstance(db);

		try
		{
			List<MutationGene> genes  = db.query(MutationGene.class).equals(MutationGene.NAME, this.getModel().getGeneName()).find();
			if (genes.size() > 0)
				this.getModel().setGene(genes.get(0));
			else
				this.getModel().setGene(new MutationGene());

			if (this.getModel().getmBrowseVO().getProteinDomainList() == null)
			{
				this.getModel().getmBrowseVO().setProteinDomainList(this.domainService.getAllProteinDomains());
				//TODO Move this to business logic
//				System.out.println(">>> exons before reverse: " + this.genomeBrowserVO.getProteinDomainList().get(0).getAllExons().toString());
				if ("R".equals(this.getModel().getGene().getOrientation()))
					this.domainService.reverseExons(this.getModel().getmBrowseVO().getProteinDomainList());
//				System.out.println(">>> exons after reverse: " + this.genomeBrowserVO.getProteinDomainList().get(0).getAllExons().toString());
			}

			this.getModel().setNumMutations(this.mutationService.getNumMutations());
			this.getModel().setNumPatients(this.patientService.getNumPatients());
			this.getModel().setNumUnpublished(this.patientService.getNumUnpublishedPatients());
//			this.getModel().setNews(this.newsService.getAllNews(5));

			this.populateGenePanel();
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

//	private String includePage(Tuple request) throws ServletException, IOException
//	{
//		HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
//		HttpServletRequest httpRequest   = rt.getRequest();
//		HttpServletResponse httpResponse = rt.getResponse();
//		HttpSession httpSession          = httpRequest.getSession();
//		RedirectTextWrapper respWrapper  = new RedirectTextWrapper(httpResponse);
//			
//		httpSession.setAttribute("patientSummaryVOs", searchPluginVO.getPatientSummaryVOs());
//			
//		// Call/include jsp
//		RequestDispatcher dispatcher = httpRequest.getRequestDispatcher("patientPager.jsp");
//		if (dispatcher != null)
//			dispatcher.include(httpRequest, respWrapper);
//
//		return respWrapper.getOutput();
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
	private void findAndAdd(Tuple request, MutationSearchCriteriaVO criteria, String key) throws DatabaseException, ParseException, ServletException, IOException
	{
		if (this.getModel().getResult().equals("patients"))
		{
			List<PatientSummaryVO> patientSummaryVOs = this.mutationService.findPatients(criteria);
			if (patientSummaryVOs.size() > 0)
			{
//				LimitOffsetPager<PatientSummaryVO> pager = new LimitOffsetPager<PatientSummaryVO>(patientSummaryVOs, 10, 0);

				this.getModel().setPatientSummaryVOs(patientSummaryVOs);
				((HttpServletRequestTuple) request).getRequest().getSession().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOs());
				this.getModel().getPatientSummaryVOHash().put(" " + key + " ", this.include(request, this.getModel().getPatientPager()));
			}
		}
		else
		{
			List<MutationSummaryVO> mutationSummaryVOs = this.mutationService.findMutations(criteria);
			if (mutationSummaryVOs.size() > 0)
			{
				LimitOffsetPager<MutationSummaryVO> pager = new LimitOffsetPager<MutationSummaryVO>(mutationSummaryVOs, 10, 0);
				
				this.getModel().setMutationSummaryVOs(mutationSummaryVOs);
				((HttpServletRequestTuple) request).getRequest().getSession().setAttribute("mutationSummaryVOList", this.getModel().getMutationSummaryVOs());
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

	private void populateShowMutationForm() throws DatabaseException, ParseException
	{
		this.getModel().getShowMutationForm().get("__target").setValue(this.getName());
		this.getModel().getListAllMutationsForm().get("select").setValue(this.getName());
		List<ValueLabel> mutationIdOptions  = new ArrayList<ValueLabel>();
		mutationIdOptions.add(new ValueLabel("", "Select mutation"));
		for (Mutation mutation : this.mutationService.getAllMutations())
			mutationIdOptions.add(new ValueLabel(mutation.getIdentifier(), mutation.getCdna_Notation() + " (" + mutation.getAa_Notation() + ")"));
		((SelectInput) this.getModel().getShowMutationForm().get("mid")).setOptions(mutationIdOptions);
		((SelectInput) this.getModel().getShowMutationForm().get("mid")).setValue("Select mutation");
	}

	private void populateExpertSearchForm() throws DatabaseException, ParseException
	{
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
		for (Exon exon : this.exonService.getAllExons())
			exonIdOptions.add(new ValueLabel(exon.getId(), exon.getName()));
		((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setOptions(exonIdOptions);
		if (this.mutationSearchCriteriaVO.getExonId() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setValue(this.mutationSearchCriteriaVO.getExonId());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setValue("Select exon/intron");
		
		List<ValueLabel> typeOptions        = new ArrayList<ValueLabel>();//.getTypeOptions();
		typeOptions.add(0, new ValueLabel("", "Select mutation type"));
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

		List<ValueLabel> domainOptions      = new ArrayList<ValueLabel>();
		domainOptions.add(new ValueLabel("", "Select protein domain"));
		for (ProteinDomainSummaryVO domainVO : this.domainService.getAllProteinDomains())
			domainOptions.add(new ValueLabel(domainVO.getProteinDomain().getId(), domainVO.getProteinDomain().getName()));
		((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setOptions(domainOptions);
		if (this.mutationSearchCriteriaVO.getProteinDomainId() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setValue(this.mutationSearchCriteriaVO.getProteinDomainId());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setValue("Select protein domain");
		
		List<ValueLabel> phenotypeOptions   = new ArrayList<ValueLabel>();
		phenotypeOptions.add(new ValueLabel("", "Select phenotype"));
		for (MutationPhenotype phenotype : this.phenotypeService.getAllPhenotypes())
			phenotypeOptions.add(new ValueLabel(phenotype.getId(), phenotype.getMajortype() + ", " + phenotype.getSubtype()));
		((SelectInput) this.getModel().getExpertSearchForm().get("phenotype_id")).setOptions(phenotypeOptions);
		if (this.mutationSearchCriteriaVO.getPhenotypeId() != null)
			((SelectInput) this.getModel().getExpertSearchForm().get("phenotype_id")).setValue(this.mutationSearchCriteriaVO.getPhenotypeId());
		else
			((SelectInput) this.getModel().getExpertSearchForm().get("phenotype_id")).setValue("Select phenotype");

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
		this.getModel().getmBrowseVO().getGenePanel().setScreenName(this.getName());
	}

	private void populateProteinDomainPanel()
	{
		this.getModel().getmBrowseVO().getProteinDomainPanel().setProteinDomainSummaryVO(this.getModel().getProteinDomainSummaryVO());
		this.getModel().getmBrowseVO().getProteinDomainPanel().setScreenName(this.getName());
	}

	private void populateExonIntronPanel()
	{
		this.getModel().getmBrowseVO().getExonIntronPanel().setExons(this.getModel().getmBrowseVO().getExonList());
		this.getModel().getmBrowseVO().getExonIntronPanel().setShowIntrons(this.getModel().getQueryParametersVO().getShowIntrons());
		this.getModel().getmBrowseVO().getExonIntronPanel().setScreenName(this.getName());
	}

	private void populateSequencePanel()
	{
		this.getModel().getmBrowseVO().getSequencePanel().setExonSummaryVO(this.getModel().getExonSummaryVO());
		this.getModel().getmBrowseVO().getSequencePanel().setMutationSummaryVOs(this.getModel().getMutationSummaryVOs());
		this.getModel().getmBrowseVO().getSequencePanel().setScreenName(this.getName());
	}

	//TODO: Move the following methods to SearchPluginVO

//	public int getResultSize()
//	{
//		int size = 0;
//		for (Entry<String, LimitOffsetPager<MutationSummaryVO>> entry : this.getModel().getMutationSummaryVOHash().entrySet())
//			size += entry.getValue().getEntities().size();
//
//		return size;
//	}

//	public int getNumPatients(List<MutationSummaryVO> mutationSummaryVOs)
//	{
//		int numPatients = 0;
//		for (MutationSummaryVO mutationSummaryVO : mutationSummaryVOs)
//			numPatients += mutationSummaryVO.getPatientSummaryVOList().size();
//		return numPatients;
//	}

//	public SearchPluginUtils getSearchPluginUtils()
//	{
//		return new SearchPluginUtils();
//	}
	
	public String include(Tuple request, String path)
	{
		HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
		HttpServletRequest httpRequest   = rt.getRequest();
		HttpServletResponse httpResponse = rt.getResponse();
//		HttpSession httpSession          = httpRequest.getSession();
		RedirectTextWrapper respWrapper  = new RedirectTextWrapper(httpResponse);
			
//		httpSession.setAttribute("patientSummaryVOs", searchPluginVO.getPatientSummaryVOs());
			
		// Call/include jsp
		try
		{
			RequestDispatcher dispatcher = httpRequest.getRequestDispatcher(path);
			if (dispatcher != null)
				dispatcher.include(httpRequest, respWrapper);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return respWrapper.getOutput();
	}
	
	private class RedirectTextWrapper extends HttpServletResponseWrapper
	{
		private PrintWriter printWriter;
		private StringWriter stringWriter;

		public RedirectTextWrapper(HttpServletResponse response)
		{
			super(response);
			this.stringWriter = new StringWriter();
			this.printWriter  = new PrintWriter(stringWriter);
		}

		@Override
		public PrintWriter getWriter()
		{
			return this.printWriter;
		}

		public String getOutput()
		{
			return this.stringWriter.toString();
		}
	}
}
