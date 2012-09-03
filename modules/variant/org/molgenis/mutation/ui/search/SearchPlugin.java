/* Date:        February 22, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.search;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.IntegratedPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
//import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.TextLineInput;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.MutationSearchCriteriaDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.mutation.service.CmsService;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.service.StatisticsService;
import org.molgenis.mutation.ui.HtmlFormWrapper;
import org.molgenis.mutation.ui.html.MBrowse;
import org.molgenis.mutation.ui.search.form.ExpertSearchForm;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public class SearchPlugin extends IntegratedPluginController<SearchModel>
{
	private static final long serialVersionUID = 651270609185006020L;

	public SearchPlugin(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new SearchModel(this));
		this.view = new FreemarkerView("init.ftl", getModel());
		this.getModel().setPatientPager("/mutation/patientPager.jsp");
		this.getModel().setMutationPager("/mutation/mutationPager.jsp");
		this.getModel().setPatientViewer("/org/molgenis/mutation/ui/search/patient.ftl");
		this.getModel().setMutationViewer("/org/molgenis/mutation/ui/search/mutation.ftl");
		this.getModel().setMbrowse(new MBrowse());
		this.getModel().getMbrowse().setTarget(this.getName());
		this.getModel().setExpertSearchFormWrapper(new HtmlFormWrapper(new ExpertSearchForm()));
	}
	
	private ScreenView view;
	
	public ScreenView getView()
	{
		return view;
	}
	
	public void setView(ScreenView view)
	{
		this.view = view;
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
	{
		try
		{
			if (StringUtils.isEmpty(request.getAction()))
				this.getModel().setAction("init");
			else
				this.getModel().setAction(request.getAction());

			if ("1".equals(request.getString("expertSearch")))
				this.getModel().getQueryParametersVO().setExpertSearch(true);
			else
				this.getModel().getQueryParametersVO().setExpertSearch(false);

			if (this.getModel().getAction().equals("findMutationsByTerm"))
			{
				this.handleFindMutationsByTerm(request);
			}
			else if (this.getModel().getAction().equals("findMutations"))
			{
				this.handleFindMutations(request);
			}
			else if (this.getModel().getAction().equals("findPatients"))
			{
				this.handleFindPatients(request);
			}
			else if (this.getModel().getAction().equals("listAllMutations"))
			{
				this.listAllMutations(request);
			}
			else if (this.getModel().getAction().equals("listAllPatients"))
			{
				this.listAllPatients(request);
			}
			else if (this.getModel().getAction().equals("showProteinDomain"))
			{
				this.handleShowProteinDomain(request);
			}
			else if (this.getModel().getAction().equals("showExon"))
			{
				this.handleShowExon(request);
			}
			else if (this.getModel().getAction().equals("showFirstExon"))
			{
				this.handleShowFirstExon(request);
			}
			else if (this.getModel().getAction().equals("showPrevExon"))
			{
				this.handleShowPrevExon(request);
			}
			else if (this.getModel().getAction().equals("showNextExon"))
			{
				this.handleShowNextExon(request);
			}
			else if (this.getModel().getAction().equals("showLastExon"))
			{
				this.handleShowLastExon(request);
			}
			else if (this.getModel().getAction().equals("showMutation"))
			{
				this.handleShowMutation(request);
			}
			else if (this.getModel().getAction().equals("showFirstMutation"))
			{
				this.handleShowFirstMutation(request);
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
				this.handleShowLastMutation(request);
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
			else
			{
				this.setView(new FreemarkerView("init.ftl", this.getModel()));
			}
			
			this.populateDisplayOptionsForm();
			
//			for (ScreenController<?> child : this.getChildren())
//			{
//				child.handleRequest(db, request, out);
//			}
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

	private void handleFindMutations(Tuple request)
	{
		this.getModel().setMutationSearchCriteriaVO(new MutationSearchCriteriaDTO());
		if (StringUtils.isNotEmpty(request.getString("variation")))
			this.getModel().getMutationSearchCriteriaVO().setVariation(request.getString("variation"));
		if (StringUtils.isNotEmpty(request.getString("consequence")))
			this.getModel().getMutationSearchCriteriaVO().setConsequence(request.getString("consequence"));
		if (StringUtils.isNotEmpty(request.getString("mutation_id")))
			this.getModel().getMutationSearchCriteriaVO().setMutationId(request.getInt("mutation_id"));
		if (StringUtils.isNotEmpty(request.getString("mid")))
			this.getModel().getMutationSearchCriteriaVO().setMid(request.getString("mid"));
		if (StringUtils.isNotEmpty(request.getString("nuclno")))
			this.getModel().getMutationSearchCriteriaVO().setCdnaPosition(request.getInt("nuclno"));
		if (StringUtils.isNotEmpty(request.getString("aano")))
			this.getModel().getMutationSearchCriteriaVO().setCodonChangeNumber(request.getInt("aano"));
		if (StringUtils.isNotEmpty(request.getString("exon_id")))
			this.getModel().getMutationSearchCriteriaVO().setExonId(request.getInt("exon_id"));
		if (StringUtils.isNotEmpty(request.getString("exon")))
			this.getModel().getMutationSearchCriteriaVO().setExonName(request.getString("exon"));
		if (StringUtils.isNotEmpty(request.getString("type")))
			this.getModel().getMutationSearchCriteriaVO().setType(request.getString("type"));
		if (StringUtils.isNotEmpty(request.getString("domain_id")))
			this.getModel().getMutationSearchCriteriaVO().setProteinDomainId(request.getInt("domain_id"));
		if (StringUtils.isNotEmpty(request.getString("phenotype")))
			this.getModel().getMutationSearchCriteriaVO().setPhenotypeName(request.getString("phenotype"));
		if (StringUtils.isNotEmpty(request.getString("inheritance")))
			this.getModel().getMutationSearchCriteriaVO().setInheritance(request.getString("inheritance"));
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.getModel().getMutationSearchCriteriaVO().setReportedAsSNP(false);

		SearchService searchService = ServiceLocator.instance().getSearchService();

		this.getModel().setMutationSummaryDTOList(searchService.findMutations(this.getModel().getMutationSearchCriteriaVO()));
		((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryDTOList", this.getModel().getMutationSummaryDTOList());
		this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));

		this.getModel().setHeader(this.getModel().getMutationSummaryDTOList().size() + " results.");
		
		this.setView(new FreemarkerView("included.ftl", this.getModel()));
	}

	private void handleShowMutation(Tuple request)
	{
		if (StringUtils.isNotEmpty(request.getString("mid")))
		{
			String mutationIdentifier = request.getString("mid");

//			if (StringUtils.isNotEmpty(request.getString("snpbool")))
//				if (request.getString("snpbool").equals("hide"))
//					this.getModel().getMutationSearchCriteriaVO().setReportedAsSNP(false);
	
			SearchService searchService = ServiceLocator.instance().getSearchService();
	
			MutationSummaryDTO mutationSummaryDTO = searchService.findMutationByIdentifier(mutationIdentifier);
	
			this.getModel().setMutationSummaryVO(mutationSummaryDTO);
	
			this.getModel().setPositionMutations(searchService.findPositionMutations(mutationSummaryDTO));
			this.getModel().setCodonMutations(searchService.findCodonMutations(mutationSummaryDTO));
	
			this.getModel().setHeader("Details for mutation " + mutationIdentifier);
			
			this.setView(new FreemarkerView(this.getModel().getMutationViewer(), this.getModel()));
		}
	}

	private void handleFindPatients(Tuple request)
	{
		if (StringUtils.isNotEmpty(request.getString("mid")))
		{
			String mutationIdentifier = request.getString("mid");

			SearchService searchService = ServiceLocator.instance().getSearchService();

			List<PatientSummaryDTO> patientSummaryVOList = searchService.findPatientsByMutationIdentifier(mutationIdentifier);

			((HttpServletRequestTuple) request).getRequest().setAttribute("patientSummaryVOs", patientSummaryVOList);
			this.getModel().setRawOutput(this.include(request, this.getModel().getPatientPager()));
			this.getModel().setHeader(patientSummaryVOList.size() + " results for " + mutationIdentifier);
			
			this.setView(new FreemarkerView("included.ftl", this.getModel()));
		}
	}

	private void handleFindMutationsByTerm(Tuple request)
	{
//		if (StringUtils.isNotEmpty(request.getString("term")) && request.getString("term").length() < 3)
//		throw new SearchException("Search term is too general. Please use a more specific one.");

		if (StringUtils.isNotEmpty(request.getString("result")))
			this.getModel().setResult(request.getString("result"));
		else
			this.getModel().setResult("mutations"); // Default: Show mutations
	
		this.getModel().setMutationSummaryVOHash(new HashMap<String, String>());
		this.getModel().setPatientSummaryVOHash(new HashMap<String, String>());

		SearchService searchService = ServiceLocator.instance().getSearchService();

		if (this.getModel().getResult().equals("patients"))
		{
			HashMap<String, List<PatientSummaryDTO>> result = searchService.findPatientsByTerm(request.getString("term"));
			
			int numPatients = 0;

			for (String key : result.keySet())
			{
				if (CollectionUtils.isNotEmpty(result.get(key)))
				{
					((HttpServletRequestTuple) request).getRequest().setAttribute("patientSummaryVOs", result.get(key));
					this.getModel().getPatientSummaryVOHash().put(" " + key + " ", this.include(request, this.getModel().getPatientPager()));
					numPatients += result.get(key).size();
				}
			}
			
			this.getModel().setHeader(numPatients + " patients found.");
		}
		else if (this.getModel().getResult().equals("mutations"))
		{
			HashMap<String, List<MutationSummaryDTO>> result = searchService.findMutationsByTerm(request.getString("term"));
			
			int numMutations = 0;

			for (String key : result.keySet())
			{
				if (CollectionUtils.isNotEmpty(result.get(key)))
				{
					((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryDTOList", result.get(key));
					this.getModel().getMutationSummaryVOHash().put(" " + key + " ", this.include(request, this.getModel().getMutationPager()));
					numMutations += result.get(key).size();
				}
			}
			
			this.getModel().setHeader(numMutations + " mutations found.");
		}
		
		this.setView(new FreemarkerView("freetext.ftl", this.getModel()));
	}

	private void handleShowNextMutation(Tuple request)
	{
		SearchService searchService           = ServiceLocator.instance().getSearchService();
		MutationSummaryDTO mutationSummaryDTO = searchService.findNextMutation(request.getString("mid"));
		request.set("mid", mutationSummaryDTO.getIdentifier());
		this.handleShowMutation(request);
	}

	private void handleShowPrevMutation(Tuple request)
	{
		SearchService searchService          = ServiceLocator.instance().getSearchService();
		MutationSummaryDTO mutationSummaryVO = searchService.findPrevMutation(request.getString("mid"));
		request.set("mid", mutationSummaryVO.getIdentifier());
		this.handleShowMutation(request);
	}

	private void handleShowLastMutation(Tuple request)
	{
		SearchService searchService          = ServiceLocator.instance().getSearchService();
		MutationSummaryDTO mutationSummaryVO = searchService.findLastMutation();
		request.set("mid", mutationSummaryVO.getIdentifier());
		this.handleShowMutation(request);
	}

	private void handleShowFirstMutation(Tuple request)
	{
		SearchService searchService          = ServiceLocator.instance().getSearchService();
		MutationSummaryDTO mutationSummaryVO = searchService.findFirstMutation();
		request.set("mid", mutationSummaryVO.getIdentifier());
		this.handleShowMutation(request);
	}

	private void listAllMutations(Tuple request)
	{
		SearchService searchService = ServiceLocator.instance().getSearchService();

		this.getModel().setMutationSummaryDTOList(searchService.findAllMutationSummaries());
		((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryDTOList", this.getModel().getMutationSummaryDTOList());
		this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));

		this.getModel().setHeader(this.getModel().getMutationSummaryDTOList().size() + " results for \"Display all mutations\".");
		this.setView(new FreemarkerView("included.ftl", getModel()));
	}

	private void handleShowPatient(Tuple request)
	{
		if (StringUtils.isNotEmpty(request.getString("pid")))
		{
			String patientIdentifier = request.getString("pid");

			SearchService searchService = ServiceLocator.instance().getSearchService();

			PatientSummaryDTO patientSummaryVO = searchService.findPatientByPatientIdentifier(patientIdentifier);

			this.getModel().setPatientSummaryVO(patientSummaryVO);

			this.getModel().setHeader("Details for patient " + patientIdentifier);
			
			this.setView(new FreemarkerView(this.getModel().getPatientViewer(), getModel()));
		}	
	}

	private void listAllPatients(Tuple request)
	{
//		MolgenisUser user = new MolgenisUser();
//		user.setId(this.getLogin().getUserId());
//		this.getModel().setPatientSummaryVOs(this.patientService.find(user));
		SearchService searchService = ServiceLocator.instance().getSearchService();

		List<PatientSummaryDTO> patientSummaryVOs = searchService.findAllPatientSummaries();
		this.getModel().setPatientSummaryVOs(patientSummaryVOs);
		((HttpServletRequestTuple) request).getRequest().setAttribute("patientSummaryVOs", this.getModel().getPatientSummaryVOs());
		this.getModel().setRawOutput(this.include(request, this.getModel().getPatientPager()));
		this.getModel().setHeader(this.getModel().getPatientSummaryVOs().size() + " results for \"Display all patients\".");

		this.setView(new FreemarkerView("included.ftl", this.getModel()));
	}

	private void handleShowPhenotypeDetails(Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("pid")))
		{
			String patientIdentifier = request.getString("pid");

			PhenoService phenoService   = ServiceLocator.instance().getPhenoService();
			SearchService searchService = ServiceLocator.instance().getSearchService();

			PatientSummaryDTO patientSummaryVO = searchService.findPatientByPatientIdentifier(patientIdentifier);

			this.getModel().setIndividualDTO(phenoService.findPhenotypeDetails(patientSummaryVO.getPatientId()));

			this.getModel().setHeader("Phenotypic details for Patient " + patientIdentifier);
			
			this.setView(new FreemarkerView("phenotypedetails.ftl", getModel()));
		}
	}

	private void handleShowProteinDomain(Tuple request) throws Exception
	{
		if (StringUtils.isNotEmpty(request.getString("domain_id")))
			this.getModel().getMutationSearchCriteriaVO().setProteinDomainId(request.getInt("domain_id"));
		if (StringUtils.isNotEmpty(request.getString("snpbool")))
			if (request.getString("snpbool").equals("hide"))
				this.getModel().getMutationSearchCriteriaVO().setReportedAsSNP(false);

		SearchService searchService = ServiceLocator.instance().getSearchService();

		this.getModel().setProteinDomainDTO(searchService.findProteinDomain(request.getInt("domain_id"), false));
		this.getModel().setMutationSummaryDTOList(searchService.findMutationsByDomainId(request.getInt("domain_id")));
		((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryDTOList", this.getModel().getMutationSummaryDTOList());
		this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));

		this.getModel().setHeader((this.getModel().getProteinDomainDTO() == null) ? "Unknown id." : "");

		this.getModel().getMbrowse().setProteinDomainDTO(this.getModel().getProteinDomainDTO());

		if (this.getModel().getMbrowse().getExonDTOList() == null)
			this.getModel().getMbrowse().setExonDTOList(searchService.findAllExons());

		this.setView(new FreemarkerView("proteindomain.ftl", getModel()));
	}

	private void handleShowExon(Tuple request)
	{
		SearchService searchService = ServiceLocator.instance().getSearchService();
		
		Integer exonId = request.getInt("exon_id");

		this.getModel().setExonDTO(searchService.findExonById(exonId));

		if (this.getModel().getQueryParametersVO().getShowMutations())
		{
			this.getModel().setMutationSummaryDTOList(searchService.findMutationsByExonId(exonId));
			((HttpServletRequestTuple) request).getRequest().setAttribute("mutationSummaryDTOList", this.getModel().getMutationSummaryDTOList());
			this.getModel().setRawOutput(this.include(request, this.getModel().getMutationPager()));
		}
		this.getModel().setHeader("");

		this.getModel().getMbrowse().setExonDTO(this.getModel().getExonDTO());
		this.getModel().getMbrowse().setMutationSummaryDTOList(this.getModel().getMutationSummaryDTOList());

		this.setView(new FreemarkerView("exon.ftl", getModel()));
	}

	private void handleShowNextExon(Tuple request)
	{
		SearchService searchService = ServiceLocator.instance().getSearchService();
		ExonDTO exonDTO = searchService.findNextExon(request.getInt("exon_id"));
		request.set("__action", "showExon");
		request.set("exon_id", exonDTO.getId());
		this.handleShowExon(request);
	}

	private void handleShowPrevExon(Tuple request)
	{
		SearchService searchService         = ServiceLocator.instance().getSearchService();
		ExonDTO exonDTO = searchService.findPrevExon(request.getInt("exon_id"));
		request.set("__action", "showExon");
		request.set("exon_id", exonDTO.getId());
		this.handleShowExon(request);
	}

	private void handleShowLastExon(Tuple request)
	{
		SearchService searchService = ServiceLocator.instance().getSearchService();
		ExonDTO exonDTO = searchService.findLastExon();
		request.set("exon_id", exonDTO.getId());
		this.handleShowExon(request);
	}

	private void handleShowFirstExon(Tuple request)
	{
		SearchService searchService = ServiceLocator.instance().getSearchService();
		ExonDTO exonDTO = searchService.findFirstExon();
		request.set("exon_id", exonDTO.getId());
		this.handleShowExon(request);
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			SearchService searchService = ServiceLocator.instance().getSearchService();
			this.getModel().setGeneDTO(searchService.findGene());

			if (this.getModel().getMbrowse().getIsVisible())
			{
				if (this.getModel().getMbrowse().getGeneDTO() == null)
					this.getModel().getMbrowse().setGeneDTO(this.getModel().getGeneDTO());
				
				if (this.getModel().getMbrowse().getProteinDomainDTOList() == null)
					this.getModel().getMbrowse().setProteinDomainDTOList(searchService.findAllProteinDomains());
			}

			CmsService cmsService = ServiceLocator.instance().getCmsService();

			this.getModel().setTextRemarks(cmsService.findContentByName("remarks"));
			this.getModel().setTextCollaborations(cmsService.findContentByName("collaborators"));

			if (this.getModel().getQueryParametersVO().getExpertSearch())
			{
				this.populateExpertSearchForm();
				this.populateShowMutationForm();
				this.populateToSimpleSearchForm();
			}
			else
			{
				this.getModel().setTextWelcome(cmsService.findContentByName("welcome"));
				this.getModel().setTextSearch(cmsService.findContentByName("search"));

				StatisticsService statisticsService = ServiceLocator.instance().getStatisticsService();

				this.getModel().setNumMutations(statisticsService.getNumMutations());
				this.getModel().setNumPatients(statisticsService.getNumPatients());
				this.getModel().setNumUnpublished(statisticsService.getNumUnpublishedPatients());
				this.getModel().setNumMutationsByPathogenicity(statisticsService.getNumMutationsByPathogenicity());
				this.getModel().setNumPatientsByPathogenicity(statisticsService.getNumPatientsByPathogenicity());

				this.populateSimpleSearchForm();
				this.populateListAllMutationsForm();
				this.populateListAllPatientsForm();
				this.populateToExpertSearchForm();
			}
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

	private void populateSimpleSearchForm()
	{
		this.getModel().getSimpleSearchForm().get("__target").setValue(this.getName());
		this.getModel().getSimpleSearchForm().get("select").setValue(this.getName());
		this.getModel().getSimpleSearchForm().get("result").setValue(this.getModel().getResult());
		this.getModel().getSimpleSearchForm().get("term").setValue(this.getModel().getSearchTerm());
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

	private void populateShowMutationForm()
	{
		SearchService searchService = ServiceLocator.instance().getSearchService();

		this.getModel().getShowMutationForm().get("__target").setValue(this.getName());
		this.getModel().getListAllMutationsForm().get("select").setValue(this.getName());
		List<ValueLabel> mutationIdOptions  = new ArrayList<ValueLabel>();
		mutationIdOptions.add(new ValueLabel("", "Select mutation"));
		for (VariantDTO variantDTO : searchService.getAllVariants())
			mutationIdOptions.add(new ValueLabel(variantDTO.getIdentifier(), variantDTO.getCdnaNotation() + " (" + variantDTO.getAaNotation() + ")"));
		((SelectInput) this.getModel().getShowMutationForm().get("mid")).setOptions(mutationIdOptions);
		((SelectInput) this.getModel().getShowMutationForm().get("mid")).setValue("Select mutation");
	}

	protected void populateExpertSearchForm()
	{
		PhenoService phenoService   = ServiceLocator.instance().getPhenoService();
		SearchService searchService = ServiceLocator.instance().getSearchService();

		Container expertSearchForm  = this.getModel().getExpertSearchFormWrapper().getForm();

		expertSearchForm.get("__target").setValue(this.getName());
		expertSearchForm.get("select").setValue(this.getName());

		if (this.getModel().getMutationSearchCriteriaVO().getVariation() != null)
			((TextLineInput) expertSearchForm.get("variation")).setValue(this.getModel().getMutationSearchCriteriaVO().getVariation());

		if (this.getModel().getMutationSearchCriteriaVO().getCdnaPosition() != null)
			((IntInput) expertSearchForm.get("nuclno")).setValue(this.getModel().getMutationSearchCriteriaVO().getCdnaPosition());

		if (this.getModel().getMutationSearchCriteriaVO().getCodonNumber() != null)
			((IntInput) expertSearchForm.get("aano")).setValue(this.getModel().getMutationSearchCriteriaVO().getCodonNumber());

		List<ValueLabel> exonIdOptions = new ArrayList<ValueLabel>();
		exonIdOptions.add(new ValueLabel("", "Select"));
		for (ExonDTO exonSummaryVO : searchService.findAllExons())
			exonIdOptions.add(new ValueLabel(exonSummaryVO.getId(), exonSummaryVO.getName()));
		((SelectInput) expertSearchForm.get("exon_id")).setOptions(exonIdOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getExonId() != null)
			((SelectInput) expertSearchForm.get("exon_id")).setValue(this.getModel().getMutationSearchCriteriaVO().getExonId());
		else
			((SelectInput) expertSearchForm.get("exon_id")).setValue("Select");

		List<ValueLabel> typeOptions = new ArrayList<ValueLabel>();
		typeOptions.add(0, new ValueLabel("", "Select"));
		for (String mutationType : phenoService.findObservedValues("Type of mutation"))
			typeOptions.add(new ValueLabel(mutationType, mutationType));
		((SelectInput) expertSearchForm.get("type")).setOptions(typeOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getType() != null)
			((SelectInput) expertSearchForm.get("type")).setValue(this.getModel().getMutationSearchCriteriaVO().getType());
		else
			((SelectInput) expertSearchForm.get("type")).setValue("Select");

		List<ValueLabel> consequenceOptions = new ArrayList<ValueLabel>();
		consequenceOptions.add(0, new ValueLabel("", "Select"));
		for (String consequence : phenoService.findObservedValues("consequence"))
			consequenceOptions.add(new ValueLabel(consequence, consequence));
		((SelectInput) expertSearchForm.get("consequence")).setOptions(consequenceOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getConsequence() != null)
			((SelectInput) expertSearchForm.get("consequence")).setValue(this.getModel().getMutationSearchCriteriaVO().getConsequence());
		else
			((SelectInput) expertSearchForm.get("consequence")).setValue("Select");

		List<ValueLabel> domainOptions = new ArrayList<ValueLabel>();
		domainOptions.add(new ValueLabel("", "Select"));
		for (ProteinDomainDTO domainVO : searchService.findAllProteinDomains())
			domainOptions.add(new ValueLabel(domainVO.getDomainId(), domainVO.getDomainName()));
		((SelectInput) expertSearchForm.get("domain_id")).setOptions(domainOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getProteinDomainId() != null)
			((SelectInput) expertSearchForm.get("domain_id")).setValue(this.getModel().getMutationSearchCriteriaVO().getProteinDomainId());
		else
			((SelectInput) expertSearchForm.get("domain_id")).setValue("Select");
		
		List<ValueLabel> phenotypeOptions = new ArrayList<ValueLabel>();
		phenotypeOptions.add(new ValueLabel("", "Select"));
		for (String phenotypeName : phenoService.findObservedValues("Phenotype"))
			phenotypeOptions.add(new ValueLabel(phenotypeName, phenotypeName));
		((SelectInput) expertSearchForm.get("phenotype")).setOptions(phenotypeOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getPhenotypeId() != null)
			((SelectInput) expertSearchForm.get("phenotype")).setValue(this.getModel().getMutationSearchCriteriaVO().getPhenotypeId());
		else
			((SelectInput) expertSearchForm.get("phenotype")).setValue("Select");

		List<ValueLabel> inheritanceOptions = new ArrayList<ValueLabel>();
		inheritanceOptions.add(0, new ValueLabel("", "Select"));
		for (String inheritance : phenoService.findObservedValues("Inheritance"))
			inheritanceOptions.add(new ValueLabel(inheritance, inheritance));
		((SelectInput) expertSearchForm.get("inheritance")).setOptions(inheritanceOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getInheritance() != null)
			((SelectInput) expertSearchForm.get("inheritance")).setValue(this.getModel().getMutationSearchCriteriaVO().getInheritance());
		else
			((SelectInput) expertSearchForm.get("inheritance")).setValue("Select");
	}
	
	private void populateDisplayOptionsForm()
	{
		this.getModel().getDisplayOptionsForm().get("__target").setValue(this.getName());
		this.getModel().getDisplayOptionsForm().get("__action").setValue(this.getModel().getAction());
		
		if (this.getModel().getMutationSearchCriteriaVO().getProteinDomainId() != null)
			this.getModel().getDisplayOptionsForm().get("domain_id").setValue(this.getModel().getMutationSearchCriteriaVO().getProteinDomainId());
		if (this.getModel().getMutationSearchCriteriaVO().getExonId() != null)
			this.getModel().getDisplayOptionsForm().get("exon_id").setValue(this.getModel().getMutationSearchCriteriaVO().getExonId());
		if (this.getModel().getMutationSearchCriteriaVO().getMid() != null)
			this.getModel().getDisplayOptionsForm().get("mid").setValue(this.getModel().getMutationSearchCriteriaVO().getMid());

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
}
