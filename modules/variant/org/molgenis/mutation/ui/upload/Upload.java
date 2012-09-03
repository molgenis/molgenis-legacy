/* Date:        February 23, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.upload;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.service.UploadService;
import org.molgenis.mutation.ui.upload.form.BatchForm;
import org.molgenis.variant.Patient;
import org.molgenis.util.SimpleEmailService.EmailException;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public class Upload extends EasyPluginController<UploadModel>
{

	private static final long serialVersionUID = -3499931124766785979L;
	private final transient Logger logger      = Logger.getLogger(Upload.class.getSimpleName());

	public Upload(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new UploadModel(this));
		this.view = new FreemarkerView("uploadBatch.ftl", getModel());
		this.populateBatchForm();
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
			String action = request.getAction();
			
			if (StringUtils.equals(action, "newBatch"))
			{
				this.handleNewBatch(request);
			}
//			else if (StringUtils.equals(action, "checkBatch"))
//			{
//				this.handleCheckBatch(request);
//			}
			else if (StringUtils.equals(action, "insertBatch"))
			{
				this.handleInsertBatch(request);
			}
			else if (StringUtils.equals(action, "emailBatch"))
			{
				this.handleEmailBatch(request);
			}
			else if (StringUtils.equals(action, "newPatient"))
			{
				this.handleNewPatient(request);
			}
			else if (StringUtils.equals(action, "insertPatient"))
			{
				this.handleInsertPatient(request);
			}
			else if (StringUtils.equals(action, "newMutation"))
			{
				this.handleNewMutation(request);
			}
			else if (StringUtils.equals(action, "assignMutation"))
			{
				this.handleAssignMutation(request);
			}
			else if (StringUtils.equals(action, "checkMutation"))
			{
				this.handleCheckMutation(request);
			}
			else if (StringUtils.equals(action, "insertMutation"))
			{
				this.handleInsertMutation(request);
			}
			else if (StringUtils.equals(action, "reindex"))
			{
				this.handleReindex();
			}
		}
		catch(Exception e)
		{
//			String message = "Oops, an error occurred. We apologize and will work on fixing it as soon as possible. <a href=\"molgenis.do?__target=Upload&__action=newBatch\">Return to home page</a>";
			this.getModel().getMessages().add(new ScreenMessage(e.getMessage(), false));
			logger.error(e.getMessage());
			for (StackTraceElement el : e.getStackTrace())
				logger.error(el.toString());
		}
		return Show.SHOW_MAIN;
	}

	private void handleNewBatch(Tuple request)
	{
		this.populateBatchForm();

		this.setView(new FreemarkerView("uploadBatch.ftl", getModel()));
	}

	private void handleInsertMutation(Tuple request) throws EmailException
	{
		//TODO: Insert and mark as uncurated
//		this.mutationService.insert(this.getModel().getMutationUploadVO());

//		if (this.referer == 1)
//			this.getModel().getPatientSummaryVO().setMutation1(this.getModel().getMutationUploadVO().getMutation());
//		else if (this.referer == 2)
//			this.getModel().getPatientSummaryVO().setMutation2(this.getModel().getMutationUploadVO().getMutation());
		
		String emailContents = "New mutation upload:\n" + this.getModel().getMutationUploadVO().toString() + "\nUser: " + this.getApplicationController().getLogin().getUserName() + "\n";
		//assuming: 'encoded' p.w. (setting deObf = true)
		this.getEmailService().email("New mutation upload for COL7A1", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
//		service.email("New mutation upload for COL7A1", emailContents, "robert.wagner42@gmail.com");

		this.getModel().getMessages().add(new ScreenMessage("Mutation successfully inserted", true));
	}

	private void handleCheckMutation(Tuple request)
	{
		// TODO: implement check screen
	}

	private void handleAssignMutation(Tuple request)
	{
		UploadService uploadService = ServiceLocator.instance().getUploadService();
		uploadService.assignValuesFromPosition(this.getModel().getMutationUploadVO());
		this.populateMutationForm();
	}

	private void handleNewMutation(Tuple request)
	{
		this.populateMutationForm();
	}

	private void handleInsertPatient(Tuple request) throws EmailException
	{
		//TODO: Insert and mark as uncurated
//		this.patientService.insert(this.getModel().getPatientSummaryVO());
		
		String emailContents = "New patient upload:\n" + this.getModel().getPatientSummaryVO().toString() + "\nUser: " + this.getApplicationController().getLogin().getUserName() + "\n";
		//assuming: 'encoded' p.w. (setting deObf = true)
		this.getEmailService().email("New patient upload for COL7A1", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
		this.getModel().getMessages().add(new ScreenMessage("Patient successfully inserted", true));
	}

	private void handleNewPatient(Tuple request)
	{
		this.populatePatientForm();
	}

	private void handleEmailBatch(Tuple request) throws EmailException, IOException
	{
		File file = request.getFile("upload");

		//TODO: Remove absolute path's!!!!
		File dest = File.createTempFile("molgenis_upload", ".xls");
		FileUtils.copyFile(file, dest);
		
		String emailContents = "New data upload by User: " + this.getApplicationController().getLogin().getUserName() + "\n";
		//assuming: 'encoded' p.w. (setting deObf = true)
		this.getEmailService().email("New data upload for COL7A1", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
		this.getModel().getMessages().add(new ScreenMessage("Thank you for your submission. Your data has been successfully emailed to us.", true));
		
		this.setView(new FreemarkerView("uploadBatch.ftl", getModel()));
	}

	private void handleInsertBatch(Tuple request)
	{
		UploadService uploadService = ServiceLocator.instance().getUploadService();
		int count                   = uploadService.insert(request.getFile("filefor_upload"));
		this.getModel().getMessages().add(new ScreenMessage("Successfully inserted " + count + " rows", true));
		
		this.setView(new FreemarkerView("uploadBatch.ftl", getModel()));
	}

	private void handleReindex()
	{
		UploadService uploadService = ServiceLocator.instance().getUploadService();
		uploadService.reindex();
		this.getModel().getMessages().add(new ScreenMessage("Successfully rebuilt the full text index", true));
	}

	private void populateBatchForm()
	{
		this.getModel().setBatchForm(new BatchForm());
		this.getModel().getBatchForm().get("__target").setValue(this.getName());
		this.getModel().getBatchForm().get("select").setValue(this.getName());
	}

	private void populatePatientForm()
	{
		SearchService searchService       = ServiceLocator.instance().getSearchService();

		List<ValueLabel> mutationOptions  = new ArrayList<ValueLabel>();
		for (VariantDTO variantDTO : searchService.getAllVariants())
			mutationOptions.add(new ValueLabel(variantDTO.getId(), variantDTO.getCdnaNotation()));

		this.getModel().getPatientForm().get("identifier").setValue(this.getModel().getPatientSummaryVO().getPatientIdentifier());
		((SelectInput) this.getModel().getPatientForm().get("mutation1")).setOptions(mutationOptions);
		if (this.getModel().getPatientSummaryVO().getVariantDTOList().size() > 0)
			this.getModel().getPatientForm().get("mutation1").setValue(this.getModel().getPatientSummaryVO().getVariantDTOList().get(0).getId());
		((SelectInput) this.getModel().getPatientForm().get("mutation2")).setOptions(mutationOptions);
		if (this.getModel().getPatientSummaryVO().getVariantDTOList().size() > 1)
			this.getModel().getPatientForm().get("mutation2").setValue(this.getModel().getPatientSummaryVO().getVariantDTOList().get(1).getId());
		this.getModel().getPatientForm().get("number").setValue(this.getModel().getPatientSummaryVO().getPatientLocalId());
//		((SelectInput) this.getModel().getPatientForm().get("phenotype")).setOptions(phenotypeOptions);
		this.getModel().getPatientForm().get("phenotype").setValue(this.getModel().getPatientSummaryVO().getPhenotypeId());
		((SelectInput) this.getModel().getPatientForm().get("phenotype")).setOnchange("toggleForm(this.value);");
		((SelectInput) this.getModel().getPatientForm().get("consent")).setOptions(new Patient().getConsentOptions());
	}

	private void populateMutationForm()
	{
		//TODO: implement
	}

	@Override
	public void reload(Database db)
	{
		//documented empty block
	}
}