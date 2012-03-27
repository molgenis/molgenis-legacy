/* Date:        February 23, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.upload;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.CsvToDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.pheno.Patient;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.MutationUploadDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.service.UploadService;
import org.molgenis.mutation.ui.upload.form.MutationForm;
import org.molgenis.mutation.ui.upload.form.PatientForm;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public abstract class Upload extends PluginModel<Entity>
{

	private static final long serialVersionUID   = -3499931124766785979L;
	private static final transient Logger logger = Logger.getLogger(Upload.class.getSimpleName());
	protected String GENENAME;
	private String action = "newBatch";
	private MutationUploadDTO mutationUploadVO;
	private PatientSummaryDTO patientSummaryVO;
	private int referer ; // referer for patient.mutation{1,2} => 1 or 2, 0 initially
	
	protected CsvToDatabase<Entity> uploadBatchCsvReader;

	private PatientForm patientForm    = new PatientForm();
	private MutationForm mutationForm  = new MutationForm();

//	private UploadPatient uploadPatient = new UploadPatient();

	public Upload(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private void initMutationUploadVO()
	{
		this.mutationUploadVO       = new MutationUploadDTO();
		UploadService uploadService = ServiceLocator.instance().getUploadService();
		uploadService.setDefaults(this.mutationUploadVO);
	}
	
	private void initPatientSummaryVO()
	{
		this.patientSummaryVO = new PatientSummaryDTO();
		this.referer          = 0;
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_mutation_ui_upload_Upload";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/mutation/ui/upload/Upload.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			this.setMessages();

			this.action = request.getAction();
			
			if (StringUtils.isEmpty(this.action) || StringUtils.endsWith(this.action, "Batch"))
				this.handleBatch(request);
			if (StringUtils.isEmpty(this.action) || StringUtils.endsWith(this.action, "Patient"))
				this.handlePatient(request);
			else if (StringUtils.endsWith(this.action, "Mutation"))
				this.handleMutation(request);
		}
		catch(Exception e)
		{
			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=Upload&__action=newBatch\">Return to home page</a>";
			this.getMessages().add(new ScreenMessage(message, false));
			logger.error(e.getMessage());
			for (StackTraceElement el : e.getStackTrace())
				logger.error(el.toString());
		}
	}

	private void handleBatch(Tuple request) throws Exception
	{
		if (this.action.equals("newBatch"))
		{
			// nothing
		}
		else if (this.action.equals("checkBatch"))
		{
			// TODO: implement check screen
		}
		else if (this.action.equals("insertBatch"))
		{
			File file = request.getFile("upload");
			UploadService uploadService = ServiceLocator.instance().getUploadService();
			int count = uploadService.insert(file, this.uploadBatchCsvReader);
			this.getMessages().add(new ScreenMessage("Successfully inserted " + count + " rows", true));
			this.initMutationUploadVO();
			this.initPatientSummaryVO();
		}
		else if (this.action.equals("emailBatch"))
		{
			File file = request.getFile("upload");

			//TODO: Remove absolute path's!!!!
			File dest = File.createTempFile("molgenis_upload", ".xls");
			FileUtils.copyFile(file, dest);
			
			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;
//			String uploadPath = "http://vm7.target.rug.nl" + rt.getRequest().getContextPath() + "/res/upload/" + destName;
			
			String emailContents = "New data upload by User: " + this.getLogin().getUserName() + "\n";
			//assuming: 'encoded' p.w. (setting deObf = true)
			this.getEmailService().email("New data upload for COL7A1", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
//			service.email("New data upload for COL7A1", emailContents, "robert.wagner42@gmail.com");
			this.getMessages().add(new ScreenMessage("Thank you for your submission. Your data has been successfully emailed to us.", true));
		}
	}

	private void handlePatient(Tuple request) throws Exception
	{
//		this.patientSummaryVO = this.toPatientSummaryVO(request);

		if (this.action.equals("newPatient"))
		{
			this.populatePatientForm();
		}
		else if (this.action.equals("insertPatient"))
		{
			//TODO: Insert and mark as uncurated
//			this.patientService.insert(this.patientSummaryVO);
			
			String emailContents = "New patient upload:\n" + this.patientSummaryVO.toString() + "\nUser: " + this.getLogin().getUserName() + "\n";
			//assuming: 'encoded' p.w. (setting deObf = true)
			this.getEmailService().email("New patient upload for COL7A1", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
//			service.email("New patient upload for COL7A1", emailContents, "robert.wagner42@gmail.com");

			this.getMessages().add(new ScreenMessage("Patient successfully inserted", true));

			this.initPatientSummaryVO();
		}
	}

	private void handleMutation(Tuple request) throws Exception
	{
//		this.toMutationUploadVO(request);

		if (this.action.equals("newMutation"))
		{
			this.populateMutationForm();
		}
		else if (this.action.equals("assignMutation"))
		{
			UploadService uploadService = ServiceLocator.instance().getUploadService();
			uploadService.assignValuesFromPosition(this.mutationUploadVO);
			this.populateMutationForm();
		}
		else if (this.action.equals("checkMutation"))
		{
			// TODO: implement check screen
		}
		else if (this.action.equals("insertMutation"))
		{
			//TODO: Insert and mark as uncurated
//			this.mutationService.insert(this.mutationUploadVO);

//			if (this.referer == 1)
//				this.patientSummaryVO.setMutation1(this.mutationUploadVO.getMutation());
//			else if (this.referer == 2)
//				this.patientSummaryVO.setMutation2(this.mutationUploadVO.getMutation());
			
			String emailContents = "New mutation upload:\n" + this.mutationUploadVO.toString() + "\nUser: " + this.getLogin().getUserName() + "\n";
			//assuming: 'encoded' p.w. (setting deObf = true)
			this.getEmailService().email("New mutation upload for COL7A1", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
//			service.email("New mutation upload for COL7A1", emailContents, "robert.wagner42@gmail.com");

			this.getMessages().add(new ScreenMessage("Mutation successfully inserted", true));

			this.initMutationUploadVO();
		}
	}

	private void populatePatientForm() throws Exception
	{
		SearchService searchService       = ServiceLocator.instance().getSearchService();

		List<ValueLabel> mutationOptions  = new ArrayList<ValueLabel>();
		for (VariantDTO variantDTO : searchService.getAllVariants())
			mutationOptions.add(new ValueLabel(variantDTO.getId(), variantDTO.getCdnaNotation()));

//		List<ValueLabel> phenotypeOptions = new ArrayList<ValueLabel>();
//		for (MutationPhenotype phenotype : searchService.getAllPhenotypes())
//			phenotypeOptions.add(new ValueLabel(phenotype.getId(), phenotype.getMajortype() + ", " + phenotype.getSubtype() + " (" + phenotype.getName() + ")"));

//		Vector<String> deceasedValue      = new Vector<String>();
//		deceasedValue.add(this.patientSummaryVO.getPatientDeceased());
//		
//		this.patientForm.get("age").setValue(this.patientSummaryVO.getPatientAge());
//		((SelectInput) this.patientForm.get("gender")).setOptions(new Patient().getGenderOptions());
//		this.patientForm.get("deceased").setValue(deceasedValue);
		this.patientForm.get("identifier").setValue(this.patientSummaryVO.getPatientIdentifier());
		((SelectInput) this.patientForm.get("mutation1")).setOptions(mutationOptions);
		if (this.patientSummaryVO.getVariantDTOList().size() > 0)
			this.patientForm.get("mutation1").setValue(this.patientSummaryVO.getVariantDTOList().get(0).getId());
		((SelectInput) this.patientForm.get("mutation2")).setOptions(mutationOptions);
		if (this.patientSummaryVO.getVariantDTOList().size() > 1)
			this.patientForm.get("mutation2").setValue(this.patientSummaryVO.getVariantDTOList().get(1).getId());
		this.patientForm.get("number").setValue(this.patientSummaryVO.getPatientLocalId());
//		((SelectInput) this.patientForm.get("phenotype")).setOptions(phenotypeOptions);
		this.patientForm.get("phenotype").setValue(this.patientSummaryVO.getPhenotypeId());
		((SelectInput) this.patientForm.get("phenotype")).setOnchange("toggleForm(this.value);");
		((SelectInput) this.patientForm.get("consent")).setOptions(new Patient().getConsentOptions());

		// Phenotype details
		
//		((SelectInput) this.patientForm.get("blistering")).setOptions(new PhenotypeDetails().getBlisteringOptions());
//		((SelectInput) this.patientForm.get("location")).setOptions(new PhenotypeDetails().getLocationOptions());
//		((SelectInput) this.patientForm.get("hands")).setOptions(new PhenotypeDetails().getHandsOptions());
//		((SelectInput) this.patientForm.get("feet")).setOptions(new PhenotypeDetails().getFeetOptions());
//		((SelectInput) this.patientForm.get("arms")).setOptions(new PhenotypeDetails().getArmsOptions());
//		((SelectInput) this.patientForm.get("legs")).setOptions(new PhenotypeDetails().getLegsOptions());
//		((SelectInput) this.patientForm.get("proximal_body_flexures")).setOptions(new PhenotypeDetails().getProximal_Body_FlexuresOptions());
//		((SelectInput) this.patientForm.get("trunk")).setOptions(new PhenotypeDetails().getTrunkOptions());
//		((SelectInput) this.patientForm.get("mucous_membranes")).setOptions(new PhenotypeDetails().getMucous_MembranesOptions());
//		((SelectInput) this.patientForm.get("skin_atrophy")).setOptions(new PhenotypeDetails().getSkin_AtrophyOptions());
//		((SelectInput) this.patientForm.get("milia")).setOptions(new PhenotypeDetails().getMiliaOptions());
//		((SelectInput) this.patientForm.get("nail_dystrophy")).setOptions(new PhenotypeDetails().getNail_DystrophyOptions());
//		((SelectInput) this.patientForm.get("albopapuloid_papules")).setOptions(new PhenotypeDetails().getAlbopapuloid_PapulesOptions());
//		((SelectInput) this.patientForm.get("pruritic_papules")).setOptions(new PhenotypeDetails().getPruritic_PapulesOptions());
//		((SelectInput) this.patientForm.get("alopecia")).setOptions(new PhenotypeDetails().getAlopeciaOptions());
//		((SelectInput) this.patientForm.get("squamous_cell_carcinomas")).setOptions(new PhenotypeDetails().getSquamous_Cell_CarcinomasOptions());
//		((SelectInput) this.patientForm.get("revertant_skin_patch")).setOptions(new PhenotypeDetails().getRevertant_Skin_PatchOptions());
//		((SelectInput) this.patientForm.get("flexion_contractures")).setOptions(new PhenotypeDetails().getFlexion_ContracturesOptions());
//		((SelectInput) this.patientForm.get("pseudosyndactyly_hands")).setOptions(new PhenotypeDetails().getPseudosyndactyly_HandsOptions());
//		((SelectInput) this.patientForm.get("microstomia")).setOptions(new PhenotypeDetails().getMicrostomiaOptions());
//		((SelectInput) this.patientForm.get("ankyloglossia")).setOptions(new PhenotypeDetails().getAnkyloglossiaOptions());
//		((SelectInput) this.patientForm.get("dysphagia")).setOptions(new PhenotypeDetails().getDysphagiaOptions());
//		((SelectInput) this.patientForm.get("growth_retardation")).setOptions(new PhenotypeDetails().getGrowth_RetardationOptions());
//		((SelectInput) this.patientForm.get("anemia")).setOptions(new PhenotypeDetails().getAnemiaOptions());
//		((SelectInput) this.patientForm.get("renal_failure")).setOptions(new PhenotypeDetails().getRenal_FailureOptions());
//		((SelectInput) this.patientForm.get("dilated_cardiomyopathy")).setOptions(new PhenotypeDetails().getDilated_CardiomyopathyOptions());
		
		// IF
//		((SelectInput) this.patientForm.get("if_value")).setOptions(new I_F().getValueOptions());
//		((SelectInput) this.patientForm.get("if_retention")).setOptions(new I_F().getRetentionOptions());
		
		// EM
//		((SelectInput) this.patientForm.get("em_fibrils")).setOptions(new E_M().getNumberOptions());
//		((SelectInput) this.patientForm.get("em_appearance")).setOptions(new E_M().getAppearanceOptions());
//		((SelectInput) this.patientForm.get("em_retention")).setOptions(new E_M().getRetentionOptions());
	}

	public PatientForm getPatientForm()
	{
		return this.patientForm;
	}

	private void populateMutationForm() throws DatabaseException, ParseException
	{
//		Vector<String> conservedValue         = new Vector<String>();
//		if (this.mutationUploadVO.getConservedAA() != null)
//			if (this.mutationUploadVO.getConservedAA())
//				conservedValue.add("conservedaa");
//			else
//				conservedValue.add("");
//
//		Vector<String> founderValue         = new Vector<String>();
//		if (this.mutationUploadVO.getFounderMutation() != null)
//			if (this.mutationUploadVO.getFounderMutation())
//				founderValue.add("foundermutation");
//			else
//				founderValue.add("");
//
//		Vector<String> snpValue         = new Vector<String>();
//		if (this.mutationUploadVO.getReportedSNP() != null)
//			if (this.mutationUploadVO.getReportedSNP())
//				snpValue.add("reportedsnp");
//			else
//				snpValue.add("");
//
//		List<ValueLabel> exonOptions  = new ArrayList<ValueLabel>();
//		SearchService searchService   = ServiceLocator.instance().getSearchService();
//		for (ExonDTO exonSummaryVO : searchService.getAllExons())
//			exonOptions.add(new ValueLabel(exonSummaryVO.getId(), exonSummaryVO.getName()));
//
//		this.mutationForm.get("gene").setValue(this.GENENAME);
//		this.mutationForm.get("refseq").setValue("NM_000094.3");
//		this.mutationForm.get("position").setValue(this.mutationUploadVO.getMutationPosition());
//		this.mutationForm.get("nt").setValue(this.mutationUploadVO.getNt());
//		((SelectInput) this.mutationForm.get("event")).setOptions(new Mutation().getEventOptions());
//		((SelectInput) this.mutationForm.get("event")).setOnchange("toggleForm(this.value);"); //TODO: Unhack this
//		this.mutationForm.get("event").setValue(this.mutationUploadVO.getEvent());
//		this.mutationForm.get("length").setValue(this.mutationUploadVO.getLength());
//		this.mutationForm.get("ntchange").setValue(this.mutationUploadVO.getNtchange());
//		this.mutationForm.get("conservedaa").setValue(conservedValue);
//		this.mutationForm.get("foundermutation").setValue(founderValue);
//		this.mutationForm.get("population").setValue(this.mutationUploadVO.getPopulation());
//		this.mutationForm.get("reportedsnp").setValue(snpValue);
//		((SelectInput) this.mutationForm.get("inheritance")).setOptions(new Mutation().getInheritanceOptions());
//		this.mutationForm.get("inheritance").setValue(this.mutationUploadVO.getInheritance());
//		this.mutationForm.get("readonly_pos").setValue(this.mutationUploadVO.getMutationPosition());
//		((SelectInput) this.mutationForm.get("exon")).setOptions(exonOptions);
//		if (this.mutationUploadVO.getExonId() != null)
//			this.mutationForm.get("exon").setValue(this.mutationUploadVO.getExonId());
//		this.mutationForm.get("nt_rep").setValue(this.mutationUploadVO.getNt());
//		this.mutationForm.get("readonly_ntchange").setValue(this.mutationUploadVO.getNtChange());
//		this.mutationForm.get("codon_number").setValue(this.mutationUploadVO.getAaPosition());
//		this.mutationForm.get("codon_number_rep").setValue(this.mutationUploadVO.getAaPosition());
//		this.mutationForm.get("codon").setValue(this.mutationUploadVO.getCodon());
//		this.mutationForm.get("codonchange").setValue(this.mutationUploadVO.getCodonChange());
//		this.mutationForm.get("aa").setValue(this.mutationUploadVO.getAa());
//		this.mutationForm.get("aachange").setValue(this.mutationUploadVO.getAachange());
//		this.mutationForm.get("cdna_notation").setValue(this.mutationUploadVO.getCdnaNotation());
//		this.mutationForm.get("gdna_notation").setValue(this.mutationUploadVO.getGdnaNotation());
//		this.mutationForm.get("aa_notation").setValue(this.mutationUploadVO.getAaNotation());
//		((SelectInput) this.mutationForm.get("consequence")).setOptions(new Mutation().getConsequenceOptions());
//		this.mutationForm.get("consequence").setValue(this.mutationUploadVO.getConsequence());
////		((SelectInput) this.mutationForm.get("type")).setOptions(new Mutation().getTypeOptions());
//		this.mutationForm.get("type").setValue(this.mutationUploadVO.getType());
	}
	
	public MutationForm getMutationForm()
	{
		return this.mutationForm;
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			if (this.mutationUploadVO == null)
				this.initMutationUploadVO();

			if (this.patientSummaryVO == null)
				this.initPatientSummaryVO();
		}
		catch (Exception e)
		{
			logger.error("Could not set default values");
		}
	}
	
	public String getAction()
	{
		return this.action;
	}

	public int getReferer()
	{
		return this.referer;
	}

	public org.molgenis.mutation.util.StringUtils getStringUtils()
	{
		return new org.molgenis.mutation.util.StringUtils();
	}
}