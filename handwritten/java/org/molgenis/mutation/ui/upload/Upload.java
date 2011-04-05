/* Date:        February 23, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.upload;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.core.Publication;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.mutation.E_M;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.I_F;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.Patient;
import org.molgenis.mutation.PhenotypeDetails;
import org.molgenis.mutation.service.ExonService;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;
import org.molgenis.mutation.service.PhenotypeService;
import org.molgenis.mutation.ui.upload.form.MutationForm;
import org.molgenis.mutation.ui.upload.form.PatientForm;
import org.molgenis.mutation.vo.MutationUploadVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public class Upload extends PluginModel<Entity>
{

	private static final long serialVersionUID = -3499931124766785979L;
	private static final transient Logger logger = Logger.getLogger(Upload.class.getSimpleName());
	private String action = "newBatch";
	private ExonService exonService;
	private MutationService mutationService;
	private PatientService patientService;
	private PhenotypeService phenotypeService;
	private MutationUploadVO mutationUploadVO;
	private PatientSummaryVO patientSummaryVO;
	private int referer ; // referer for patient.mutation{1,2} => 1 or 2, 0 initially
	
	private PatientForm patientForm    = new PatientForm();
	private MutationForm mutationForm  = new MutationForm();

//	private UploadPatient uploadPatient = new UploadPatient();

	public Upload(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	private void initMutationUploadVO() throws DatabaseException, ParseException
	{
		this.mutationUploadVO = new MutationUploadVO();
		this.mutationService.setDefaults(this.mutationUploadVO);
	}
	
	private void initPatientSummaryVO() throws DatabaseException, ParseException, MalformedURLException, JAXBException, IOException
	{
		this.patientSummaryVO = new PatientSummaryVO();
		this.patientService.setDefaults(this.patientSummaryVO);
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
				this.handleBatch(db, request);
			if (StringUtils.isEmpty(this.action) || StringUtils.endsWith(this.action, "Patient"))
				this.handlePatient(db, request);
			else if (StringUtils.endsWith(this.action, "Mutation"))
				this.handleMutation(db, request);
//			else if (StringUtils.endsWith(this.action, "Mutations"))
//				;
//			logger.debug(">>> handleRequest: messages==" + this.getMessages().size() + ".");
		}
		catch(Exception e)
		{
			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=Upload&__action=newBatch\">Return to home page</a>";
			this.getMessages().add(new ScreenMessage(message, false));
			for (StackTraceElement el : e.getStackTrace())
				logger.error(el.toString());
		}
	}

	private void handleBatch(Database db, Tuple request) throws Exception
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
			int count = this.patientService.insertBatch(file);
			this.getMessages().add(new ScreenMessage("Successfully inserted " + count + " patients", true));
			this.initMutationUploadVO();
			this.initPatientSummaryVO();
		}
		else if (this.action.equals("emailBatch"))
		{
			File file = request.getFile("upload");

			//TODO: Remove absolute path's!!!!
			Calendar cal    = Calendar.getInstance();
			Date now        = cal.getTime();
			String destName = Integer.toString(Math.abs(now.hashCode())) + ".xls";
			String destPath = "/home/rwagner/col7a1db/webapps/col7a1/res/upload/" + destName;
			File dest       = new File(destPath);
			FileUtils.copyFile(file, dest);
			
			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;
			String uploadPath = "http://vm7.target.rug.nl" + rt.getRequest().getContextPath() + "/res/upload/" + destName;
			
			String emailContents = "New data upload: " + uploadPath + "\n User: " + this.getLogin().getUserName() + "\n";
			//assuming: 'encoded' p.w. (setting deObf = true)
			this.getEmailService().email("New data upload for COL7A1", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
//			service.email("New data upload for COL7A1", emailContents, "robert.wagner42@gmail.com");
			this.getMessages().add(new ScreenMessage("Thank you for your submission. Your data has been successfully emailed to us.", true));
		}
	}

	private void handlePatient(Database db, Tuple request) throws Exception
	{
		this.patientSummaryVO = this.toPatientSummaryVO(request);

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

	private void handleMutation(Database db, Tuple request) throws Exception
	{
		this.toMutationUploadVO(db, request);

		if (this.action.equals("newMutation"))
		{
			this.populateMutationForm();
		}
		else if (this.action.equals("assignMutation"))
		{
			this.mutationService.assignValuesFromPosition(this.mutationUploadVO);
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

			if (this.referer == 1)
				this.patientSummaryVO.setMutation1(this.mutationUploadVO.getMutation());
			else if (this.referer == 2)
				this.patientSummaryVO.setMutation2(this.mutationUploadVO.getMutation());
			
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
		List<ValueLabel> mutationOptions  = new ArrayList<ValueLabel>();
		for (Mutation mutation : this.mutationService.getAllMutations())
			mutationOptions.add(new ValueLabel(mutation.getId(), mutation.getCdna_Notation()));

		List<ValueLabel> phenotypeOptions = new ArrayList<ValueLabel>();
		for (MutationPhenotype phenotype : this.phenotypeService.getAllPhenotypes())
			phenotypeOptions.add(new ValueLabel(phenotype.getId(), phenotype.getMajortype() + ", " + phenotype.getSubtype() + " (" + phenotype.getName() + ")"));

		Vector<String> deceasedValue      = new Vector<String>();
		deceasedValue.add(this.patientSummaryVO.getPatient().getDeceased());
		
		this.patientForm.get("age").setValue(this.patientSummaryVO.getPatient().getAge());
		((SelectInput) this.patientForm.get("gender")).setOptions(new Patient().getGenderOptions());
		this.patientForm.get("deceased").setValue(deceasedValue);
		this.patientForm.get("identifier").setValue(this.patientSummaryVO.getPatient().getIdentifier());
		((SelectInput) this.patientForm.get("mutation1")).setOptions(mutationOptions);
		this.patientForm.get("mutation1").setValue(this.patientSummaryVO.getMutation1().getId());
		((SelectInput) this.patientForm.get("mutation2")).setOptions(mutationOptions);
		this.patientForm.get("mutation2").setValue(this.patientSummaryVO.getMutation2().getId());
		this.patientForm.get("number").setValue(this.patientSummaryVO.getPatient().getNumber());
//		this.patientForm.get("pdf").setValue(this.patientSummaryVO.getPublications().get(0).getPdf());
		((SelectInput) this.patientForm.get("phenotype")).setOptions(phenotypeOptions);
		this.patientForm.get("phenotype").setValue(this.patientSummaryVO.getPhenotype().getId());
		((SelectInput) this.patientForm.get("phenotype")).setOnchange("toggleForm(this.value);");
		((SelectInput) this.patientForm.get("consent")).setOptions(new Patient().getConsentOptions());
//		this.patientForm.get("pubmed").setValue(this.patientSummaryVO.getPublications().get(0).getPubmed());

		// Phenotype details
		
		((SelectInput) this.patientForm.get("blistering")).setOptions(new PhenotypeDetails().getBlisteringOptions());
		((SelectInput) this.patientForm.get("location")).setOptions(new PhenotypeDetails().getLocationOptions());
		((SelectInput) this.patientForm.get("hands")).setOptions(new PhenotypeDetails().getHandsOptions());
		((SelectInput) this.patientForm.get("feet")).setOptions(new PhenotypeDetails().getFeetOptions());
		((SelectInput) this.patientForm.get("arms")).setOptions(new PhenotypeDetails().getArmsOptions());
		((SelectInput) this.patientForm.get("legs")).setOptions(new PhenotypeDetails().getLegsOptions());
		((SelectInput) this.patientForm.get("proximal_body_flexures")).setOptions(new PhenotypeDetails().getProximal_Body_FlexuresOptions());
		((SelectInput) this.patientForm.get("trunk")).setOptions(new PhenotypeDetails().getTrunkOptions());
		((SelectInput) this.patientForm.get("mucous_membranes")).setOptions(new PhenotypeDetails().getMucous_MembranesOptions());
		((SelectInput) this.patientForm.get("skin_atrophy")).setOptions(new PhenotypeDetails().getSkin_AtrophyOptions());
		((SelectInput) this.patientForm.get("milia")).setOptions(new PhenotypeDetails().getMiliaOptions());
		((SelectInput) this.patientForm.get("nail_dystrophy")).setOptions(new PhenotypeDetails().getNail_DystrophyOptions());
		((SelectInput) this.patientForm.get("albopapuloid_papules")).setOptions(new PhenotypeDetails().getAlbopapuloid_PapulesOptions());
		((SelectInput) this.patientForm.get("pruritic_papules")).setOptions(new PhenotypeDetails().getPruritic_PapulesOptions());
		((SelectInput) this.patientForm.get("alopecia")).setOptions(new PhenotypeDetails().getAlopeciaOptions());
		((SelectInput) this.patientForm.get("squamous_cell_carcinomas")).setOptions(new PhenotypeDetails().getSquamous_Cell_CarcinomasOptions());
		((SelectInput) this.patientForm.get("revertant_skin_patch")).setOptions(new PhenotypeDetails().getRevertant_Skin_PatchOptions());
		((SelectInput) this.patientForm.get("flexion_contractures")).setOptions(new PhenotypeDetails().getFlexion_ContracturesOptions());
		((SelectInput) this.patientForm.get("pseudosyndactyly_hands")).setOptions(new PhenotypeDetails().getPseudosyndactyly_HandsOptions());
		((SelectInput) this.patientForm.get("microstomia")).setOptions(new PhenotypeDetails().getMicrostomiaOptions());
		((SelectInput) this.patientForm.get("ankyloglossia")).setOptions(new PhenotypeDetails().getAnkyloglossiaOptions());
		((SelectInput) this.patientForm.get("dysphagia")).setOptions(new PhenotypeDetails().getDysphagiaOptions());
		((SelectInput) this.patientForm.get("growth_retardation")).setOptions(new PhenotypeDetails().getGrowth_RetardationOptions());
		((SelectInput) this.patientForm.get("anemia")).setOptions(new PhenotypeDetails().getAnemiaOptions());
		((SelectInput) this.patientForm.get("renal_failure")).setOptions(new PhenotypeDetails().getRenal_FailureOptions());
		((SelectInput) this.patientForm.get("dilated_cardiomyopathy")).setOptions(new PhenotypeDetails().getDilated_CardiomyopathyOptions());
		
		// IF
		((SelectInput) this.patientForm.get("if_value")).setOptions(new I_F().getValueOptions());
		((SelectInput) this.patientForm.get("if_retention")).setOptions(new I_F().getRetentionOptions());
		
		// EM
		((SelectInput) this.patientForm.get("em_fibrils")).setOptions(new E_M().getNumberOptions());
		((SelectInput) this.patientForm.get("em_appearance")).setOptions(new E_M().getAppearanceOptions());
		((SelectInput) this.patientForm.get("em_retention")).setOptions(new E_M().getRetentionOptions());
	}

	public PatientForm getPatientForm()
	{
		return this.patientForm;
	}

	private void populateMutationForm() throws DatabaseException, ParseException
	{
		Vector<String> conservedValue         = new Vector<String>();
		if (this.mutationUploadVO.getMutation().getConservedAA() != null)
			if (this.mutationUploadVO.getMutation().getConservedAA())
				conservedValue.add("conservedaa");
			else
				conservedValue.add("");

		Vector<String> founderValue         = new Vector<String>();
		if (this.mutationUploadVO.getMutation().getFounderMutation() != null)
			if (this.mutationUploadVO.getMutation().getFounderMutation())
				founderValue.add("foundermutation");
			else
				founderValue.add("");

		Vector<String> snpValue         = new Vector<String>();
		if (this.mutationUploadVO.getMutation().getReportedSNP() != null)
			if (this.mutationUploadVO.getMutation().getReportedSNP())
				snpValue.add("reportedsnp");
			else
				snpValue.add("");

		List<ValueLabel> exonOptions  = new ArrayList<ValueLabel>();
		for (Exon exon : this.exonService.getAllExons())
			exonOptions.add(new ValueLabel(exon.getId(), exon.getName()));

		this.mutationForm.get("gene").setValue("COL7A1");
		this.mutationForm.get("refseq").setValue("NM_000094.3");
		this.mutationForm.get("position").setValue(this.mutationUploadVO.getMutation().getPosition());
		this.mutationForm.get("nt").setValue(this.mutationUploadVO.getNt());
		((SelectInput) this.mutationForm.get("event")).setOptions(new Mutation().getEventOptions());
		((SelectInput) this.mutationForm.get("event")).setOnchange("toggleForm(this.value);"); //TODO: Unhack this
		this.mutationForm.get("event").setValue(this.mutationUploadVO.getMutation().getEvent());
		this.mutationForm.get("length").setValue(this.mutationUploadVO.getMutation().getLength());
		this.mutationForm.get("ntchange").setValue(this.mutationUploadVO.getMutation().getNtchange());
		this.mutationForm.get("conservedaa").setValue(conservedValue);
		this.mutationForm.get("foundermutation").setValue(founderValue);
		this.mutationForm.get("population").setValue(this.mutationUploadVO.getMutation().getPopulation());
		this.mutationForm.get("reportedsnp").setValue(snpValue);
		((SelectInput) this.mutationForm.get("inheritance")).setOptions(new Mutation().getInheritanceOptions());
		this.mutationForm.get("inheritance").setValue(this.mutationUploadVO.getMutation().getInheritance());
		this.mutationForm.get("readonly_pos").setValue(this.mutationUploadVO.getMutation().getPosition());
		((SelectInput) this.mutationForm.get("exon")).setOptions(exonOptions);
		if (this.mutationUploadVO.getExon() != null)
			this.mutationForm.get("exon").setValue(this.mutationUploadVO.getExon().getId());
		this.mutationForm.get("nt_rep").setValue(this.mutationUploadVO.getNt());
		this.mutationForm.get("readonly_ntchange").setValue(this.mutationUploadVO.getMutation().getNtchange());
		this.mutationForm.get("codon_number").setValue(this.mutationUploadVO.getMutation().getAa_Position());
		this.mutationForm.get("codon_number_rep").setValue(this.mutationUploadVO.getMutation().getAa_Position());
		this.mutationForm.get("codon").setValue(this.mutationUploadVO.getCodon());
		this.mutationForm.get("codonchange").setValue(this.mutationUploadVO.getMutation().getCodonchange());
		this.mutationForm.get("aa").setValue(this.mutationUploadVO.getAa());
		this.mutationForm.get("aachange").setValue(this.mutationUploadVO.getAachange());
		this.mutationForm.get("cdna_notation").setValue(this.mutationUploadVO.getMutation().getCdna_Notation());
		this.mutationForm.get("gdna_notation").setValue(this.mutationUploadVO.getMutation().getGdna_Notation());
		this.mutationForm.get("aa_notation").setValue(this.mutationUploadVO.getMutation().getAa_Notation());
		((SelectInput) this.mutationForm.get("consequence")).setOptions(new Mutation().getConsequenceOptions());
		this.mutationForm.get("consequence").setValue(this.mutationUploadVO.getMutation().getConsequence());
		((SelectInput) this.mutationForm.get("type")).setOptions(new Mutation().getTypeOptions());
		this.mutationForm.get("type").setValue(this.mutationUploadVO.getMutation().getType());
	}
	
	public MutationForm getMutationForm()
	{
		return this.mutationForm;
	}

	private PatientSummaryVO toPatientSummaryVO(Tuple request)
	{
		PatientSummaryVO patientSummaryVO = new PatientSummaryVO();

		patientSummaryVO.setPatient(new Patient());

		if (StringUtils.isNotEmpty(request.getString("number")))
			patientSummaryVO.getPatient().setNumber(request.getString("number"));
		
		patientSummaryVO.setMutation1(new Mutation());

		if (StringUtils.isNotEmpty(request.getString("mutation1")))
			patientSummaryVO.getMutation1().setId(request.getInt("mutation1"));

		patientSummaryVO.setMutation2(new Mutation());

		if (StringUtils.isNotEmpty(request.getString("mutation2")))
			patientSummaryVO.getMutation2().setId(request.getInt("mutation2"));

		patientSummaryVO.setPhenotype(new MutationPhenotype());

		if (StringUtils.isNotEmpty(request.getString("phenotype")))
			patientSummaryVO.getPhenotype().setId(request.getInt("phenotype"));

		patientSummaryVO.setPublications(new ArrayList<Publication>());

		if (StringUtils.isNotEmpty(request.getString("pubmed")) || StringUtils.isNotEmpty(request.getString("pdf")))
		{
			Publication publication = new Publication();
//			publication.setAuthors(request.getString("pubmed"));
//			publication.setEndPage(request.getString("pubmed"));
//			publication.setIssue(request.getString("pubmed"));
//			publication.setJournal(request.getString("pubmed"));
//			publication.setPdf(request.getString("pdf"));
			publication.setPubmedID_Name(request.getString("pubmed"));
//			publication.setStartPage(request.getString("pubmed"));
			publication.setTitle(request.getString("pubmed"));
//			publication.setVolume(request.getString("pubmed"));
//			publication.setYear(0);

			patientSummaryVO.getPublications().add(publication);
		}

		if (StringUtils.isNotEmpty(request.getString("age")))
			patientSummaryVO.getPatient().setAge(request.getString("age"));
		
		if (StringUtils.isNotEmpty(request.getString("gender")))
			patientSummaryVO.getPatient().setGender(request.getString("gender"));

		if (StringUtils.isNotEmpty(request.getString("ethnicity")))
			patientSummaryVO.getPatient().setEthnicity(request.getString("ethnicity"));

		if (StringUtils.isNotEmpty(request.getString("deceased")))
			patientSummaryVO.getPatient().setDeceased(request.getString("deceased"));
		
		if (StringUtils.isNotEmpty(request.getString("deatch_cause")))
			patientSummaryVO.getPatient().setDeath_Cause(request.getString("death_cause"));

		if (StringUtils.isNotEmpty(request.getString("mmp1_allele1")))
			patientSummaryVO.getPatient().setMmp1_Allele1(request.getString("mmp1_allele1"));

		if (StringUtils.isNotEmpty(request.getString("mmp1_allele2")))
			patientSummaryVO.getPatient().setMmp1_Allele2(request.getString("mmp1_allele2"));

		if (StringUtils.isNotEmpty(request.getString("consent")))
			patientSummaryVO.getPatient().setConsent(request.getString("consent"));

		// PhenotypeDetails
		
		if (StringUtils.isNotEmpty(request.getString("blistering")))
			patientSummaryVO.getPhenotypeDetails().setBlistering(request.getString("blistering"));
		if (StringUtils.isNotEmpty(request.getString("location")))
			patientSummaryVO.getPhenotypeDetails().setLocation(request.getString("location"));
		if (StringUtils.isNotEmpty(request.getString("hands")))
			patientSummaryVO.getPhenotypeDetails().setHands(request.getString("hands"));
		if (StringUtils.isNotEmpty(request.getString("feet")))
			patientSummaryVO.getPhenotypeDetails().setFeet(request.getString("feet"));
		if (StringUtils.isNotEmpty(request.getString("arms")))
			patientSummaryVO.getPhenotypeDetails().setArms(request.getString("arms"));
		if (StringUtils.isNotEmpty(request.getString("legs")))
			patientSummaryVO.getPhenotypeDetails().setLegs(request.getString("legs"));
		if (StringUtils.isNotEmpty(request.getString("proximal_body_flexures")))
			patientSummaryVO.getPhenotypeDetails().setProximal_Body_Flexures(request.getString("proximal_body_flexures"));
		if (StringUtils.isNotEmpty(request.getString("trunk")))
			patientSummaryVO.getPhenotypeDetails().setTrunk(request.getString("trunk"));
		if (StringUtils.isNotEmpty(request.getString("mucous_membranes")))
			patientSummaryVO.getPhenotypeDetails().setMucous_Membranes(request.getString("mucous_membranes"));
		if (StringUtils.isNotEmpty(request.getString("skin_atrophy")))
			patientSummaryVO.getPhenotypeDetails().setSkin_Atrophy(request.getString("skin_atrophy"));
		if (StringUtils.isNotEmpty(request.getString("milia")))
			patientSummaryVO.getPhenotypeDetails().setMilia(request.getString("milia"));
		if (StringUtils.isNotEmpty(request.getString("nail_dystrophy")))
			patientSummaryVO.getPhenotypeDetails().setNail_Dystrophy(request.getString("nail_dystrophy"));
		if (StringUtils.isNotEmpty(request.getString("albopapuloid_papules")))
			patientSummaryVO.getPhenotypeDetails().setAlbopapuloid_Papules(request.getString("albopapuloid_papules"));
		if (StringUtils.isNotEmpty(request.getString("pruritic_papules")))
			patientSummaryVO.getPhenotypeDetails().setPruritic_Papules(request.getString("pruritic_papules"));
		if (StringUtils.isNotEmpty(request.getString("alopecia")))
			patientSummaryVO.getPhenotypeDetails().setAlopecia(request.getString("alopecia"));
		if (StringUtils.isNotEmpty(request.getString("squamous_cell_carcinomas")))
			patientSummaryVO.getPhenotypeDetails().setSquamous_Cell_Carcinomas(request.getString("squamous_cell_carcinomas"));
		if (StringUtils.isNotEmpty(request.getString("revertant_skin_patch")))
			patientSummaryVO.getPhenotypeDetails().setRevertant_Skin_Patch(request.getString("revertant_skin_patch"));
		if (StringUtils.isNotEmpty(request.getString("mechanism")))
			patientSummaryVO.getPhenotypeDetails().setMechanism(request.getString("mechanism"));
		if (StringUtils.isNotEmpty(request.getString("flexion_contractures")))
			patientSummaryVO.getPhenotypeDetails().setFlexion_Contractures(request.getString("flexion_contractures"));
		if (StringUtils.isNotEmpty(request.getString("pseudosyndactyly_hands")))
			patientSummaryVO.getPhenotypeDetails().setPseudosyndactyly_Hands(request.getString("pseudosyndactyly_hands"));
		if (StringUtils.isNotEmpty(request.getString("microstomia")))
			patientSummaryVO.getPhenotypeDetails().setMicrostomia(request.getString("microstomia"));
		if (StringUtils.isNotEmpty(request.getString("ankyloglossia")))
			patientSummaryVO.getPhenotypeDetails().setAnkyloglossia(request.getString("ankyloglossia"));
		if (StringUtils.isNotEmpty(request.getString("dysphagia")))
			patientSummaryVO.getPhenotypeDetails().setDysphagia(request.getString("dysphagia"));
		if (StringUtils.isNotEmpty(request.getString("growth_retardation")))
			patientSummaryVO.getPhenotypeDetails().setGrowth_Retardation(request.getString("growth_retardation"));
		if (StringUtils.isNotEmpty(request.getString("anemia")))
			patientSummaryVO.getPhenotypeDetails().setAnemia(request.getString("anemia"));
		if (StringUtils.isNotEmpty(request.getString("renal_failure")))
			patientSummaryVO.getPhenotypeDetails().setRenal_Failure(request.getString("renal_failure"));
		if (StringUtils.isNotEmpty(request.getString("dilated_cardiomyopathy")))
			patientSummaryVO.getPhenotypeDetails().setDilated_Cardiomyopathy(request.getString("dilated_cardiomyopathy"));
		if (StringUtils.isNotEmpty(request.getString("other")))
			patientSummaryVO.getPhenotypeDetails().setOther(request.getString("other"));
		
		// IF
		if (StringUtils.isNotEmpty(request.getString("if_value")))
			patientSummaryVO.getIf_().setValue(request.getString("if_value"));
		if (StringUtils.isNotEmpty(request.getString("if_retention")))
			patientSummaryVO.getIf_().setRetention(request.getString("if_retention"));
		if (StringUtils.isNotEmpty(request.getString("if_description")))
			patientSummaryVO.getIf_().setDescription(request.getString("if_description"));
		
		// EM
		if (StringUtils.isNotEmpty(request.getString("em_fibrils")))
			patientSummaryVO.getEm_().setNumber(request.getString("em_fibrils"));
		if (StringUtils.isNotEmpty(request.getString("em_appearance")))
			patientSummaryVO.getEm_().setAppearance(request.getString("em_appearance"));
		if (StringUtils.isNotEmpty(request.getString("em_retention")))
			patientSummaryVO.getEm_().setRetention(request.getString("em_retention"));
		if (StringUtils.isNotEmpty(request.getString("em_description")))
			patientSummaryVO.getEm_().setDescription(request.getString("em_description"));
		
		return patientSummaryVO;
	}

	private void toMutationUploadVO(Database db, Tuple request)
	{
		if (StringUtils.isNotEmpty(request.getString("referer")))
			this.referer = request.getInt("referer");

		if (StringUtils.isNotEmpty(request.getString("consequence")))
			this.mutationUploadVO.getMutation().setConsequence(request.getString("consequence"));

		if (StringUtils.isNotEmpty(request.getString("inheritance")))
			this.mutationUploadVO.getMutation().setInheritance(request.getString("inheritance"));

		if (StringUtils.isNotEmpty(request.getString("conservedaa")))
			if (StringUtils.equals(request.getString("conservedaa"), "conservedaa"))
				this.mutationUploadVO.getMutation().setConservedAA(true);
			else
				this.mutationUploadVO.getMutation().setConservedAA(false);

		if (StringUtils.isNotEmpty(request.getString("effectonsplicing")))
			if (StringUtils.equals(request.getString("effectonsplicing"), "effectonsplicing"))
				this.mutationUploadVO.getMutation().setEffectOnSplicing(true);
			else
				this.mutationUploadVO.getMutation().setEffectOnSplicing(false);

		if (StringUtils.isNotEmpty(request.getString("event")))
			this.mutationUploadVO.getMutation().setEvent(request.getString("event"));

		if (StringUtils.isNotEmpty(request.getString("foundermutation")))
			if (StringUtils.equals(request.getString("foundermutation"), "foundermutation"))
				this.mutationUploadVO.getMutation().setFounderMutation(true);
			else
				this.mutationUploadVO.getMutation().setFounderMutation(false);

		MutationGene gene = new MutationGene();
		gene.setName(ObjectUtils.toString(request.getString("gene"), ""));
		this.mutationUploadVO.setGene(gene);

		if (StringUtils.isNotEmpty(request.getString("length")))
			this.mutationUploadVO.getMutation().setLength(request.getInt("length"));

		if (StringUtils.isNotEmpty(request.getString("ntchange")))
			this.mutationUploadVO.getMutation().setNtchange(request.getString("ntchange").toUpperCase());

		if (StringUtils.isNotEmpty(request.getString("population")))
			this.mutationUploadVO.getMutation().setPopulation(request.getString("population"));

		if (StringUtils.isNotEmpty(request.getString("position")))
			this.mutationUploadVO.getMutation().setPosition(request.getString("position"));
		else
			this.mutationUploadVO.getMutation().setPosition("");
			//throw new Exception("Please enter the position.");

		if (StringUtils.isNotEmpty(request.getString("reportedsnp")))
			if (StringUtils.equals(request.getString("reportedsnp"), "reportedsnp"))
				this.mutationUploadVO.getMutation().setReportedSNP(true);
			else
				;
		else
			this.mutationUploadVO.getMutation().setReportedSNP(false);

		if (StringUtils.isNotEmpty(request.getString("aachange")))
			this.mutationUploadVO.setAachange(request.getString("aachange"));

		if (StringUtils.isNotEmpty(request.getString("type")))
			this.mutationUploadVO.getMutation().setType(request.getString("type"));
	}

	@Override
	public void reload(Database db)
	{
		this.exonService      = ExonService.getInstance(db);
		this.mutationService  = MutationService.getInstance(db);
		this.patientService   = PatientService.getInstance(db);
		this.phenotypeService = PhenotypeService.getInstance(db);

		try
		{
			if (mutationUploadVO == null)
				this.initMutationUploadVO();
			
			if (patientSummaryVO == null)
				this.initPatientSummaryVO();
		}
		catch (Exception e)
		{
			logger.error("Could not set default values");
		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
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

///**
// * Send a multipart message
// * @param subject
// * @param body
// * @param attachment
// * @param toEmail
// * @return true if sending was successful
// * @throws EmailException
// */
//public boolean email(String subject, String body, File attachment, String toEmail) throws EmailException
//{
//	try
//	{
//		// Create the attachment
//		EmailAttachment emailAttachment = new EmailAttachment();
//		emailAttachment.setPath(attachment.getPath());
//		emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
//		emailAttachment.setName(attachment.getName());
//
//		// Create the email message
//		MultiPartEmail email = new MultiPartEmail();
//		email.setHostName(smtpHostname);
//		if (smtpProtocol.equals("smtps"))
//		{
//			email.setSmtpPort(smtpPort);
//			email.setSslSmtpPort(smtpPort.toString());
//			email.setSSL(true);
//			email.setTLS(true);
//		}
//		email.setAuthentication(smtpUser, smtpPassword);
//		email.addTo(toEmail);
//		email.setFrom(smtpFromAddress);
//		email.setSubject(subject);
//		email.setMsg(body);
//
//		// add the attachment
//		email.attach(emailAttachment);
//
//		// send the email
//		email.send();
//
//		return true;
//	}
//	catch (Exception e)
//	{
//		e.printStackTrace();
//		throw new EmailException(e);
//	}
//}
