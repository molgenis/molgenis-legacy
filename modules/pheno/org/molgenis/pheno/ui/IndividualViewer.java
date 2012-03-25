package org.molgenis.pheno.ui;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.dto.IndividualDTO;
import org.molgenis.pheno.dto.FeatureDTO;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.pheno.dto.ProtocolDTO;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.pheno.ui.form.IndividualForm;
import org.molgenis.util.Tuple;

public class IndividualViewer extends EasyPluginController<IndividualViewerModel>
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 8333436730236052413L;

	public IndividualViewer(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new IndividualViewerModel(this));
		this.getModel().setAction("show");
		this.setView(new FreemarkerView("show.ftl", getModel()));
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
	{
//		if (StringUtils.isNotEmpty(request.getAction()))
//			this.getModel().setAction(request.getAction());

		try
		{
			if ("show".equals(request.getAction()))
			{
				this.getModel().setAction("show");
				this.setView(new FreemarkerView("show.ftl", getModel()));
			}
			else if ("edit".equals(request.getAction()))
			{
//				IndividualDetailsVO individualDetailsVO = this.loadIndividualDetailsVO(db);
//				IndividualForm individualForm           = new IndividualForm();
//				this.populateIndividualForm(individualForm, individualDetailsVO);
				
				this.getModel().setAction("edit");
//				this.getModel().setIndividualDetailsVO(individualDetailsVO);
//				this.getModel().setIndividualForm(individualForm);
				this.setView(new FreemarkerView("edit.ftl", getModel()));
				
			}
			else if ("save".equals(request.getAction()))
			{
//				IndividualDetailsVO individualDetailsVO = this.loadIndividualDetailsVO(db);
//				IndividualForm individualForm           = new IndividualForm();
//				this.populateIndividualForm(individualForm, individualDetailsVO);
				this.handleSave(db, request);
				
				this.getModel().setAction("save");
//				this.getModel().setIndividualDetailsVO(individualDetailsVO);
//				this.getModel().setIndividualForm(individualForm);
				this.setView(new FreemarkerView("edit.ftl", getModel()));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getModel().getMessages().add(new ScreenMessage(e.getMessage(), false));
		}
		return Show.SHOW_DIALOG;
	}

	/**
	 * Save user changes to database
	 * @param db
	 * @param request
	 */
	private void handleSave(Database db, Tuple request)
	{
		this.loadIndividualDetailsVO(db);

		List<ObservedValueDTO> insertList = new ArrayList<ObservedValueDTO>();
		List<ObservedValueDTO> updateList = new ArrayList<ObservedValueDTO>();
		List<String> parameterNameList   = request.getFields();

		PhenoService phenoService = new PhenoService(db);

		for (String parameterName : parameterNameList)
		{
			if (parameterName.startsWith("Protocol"))
			{
				// insert a new ObservedValue
				String[] parameterNameParts       = StringUtils.split(parameterName, ".");
				String protocolIdString           = StringUtils.removeStartIgnoreCase(parameterNameParts[0], "Protocol");
				String featureIdString            = StringUtils.removeStartIgnoreCase(parameterNameParts[1], "Feature");

				ObservedValueDTO observedValueDTO = new ObservedValueDTO();
				observedValueDTO.setValue(request.getString(parameterName));
				observedValueDTO.setTargetId(this.getModel().getIndividualDTO().getIndividualId());
//				observedValueVO.setProtocolId(Integer.parseInt(protocolIdString));
				FeatureDTO featureDTO             = new FeatureDTO();
				featureDTO.setFeatureId(Integer.parseInt(featureIdString));
				observedValueDTO.setFeatureDTO(featureDTO);
				insertList.add(observedValueDTO);
			}
			else if (parameterName.startsWith("ObservedValue"))
			{
				// update the existing ObservedValue
				String observedValueIdString    = StringUtils.removeStartIgnoreCase(parameterName, "ObservedValue");
				ObservedValueDTO observedValueVO = phenoService.findObservedValue(Integer.parseInt(observedValueIdString));
				observedValueVO.setValue(request.getString(parameterName));
				updateList.add(observedValueVO);
			}
		}

		phenoService.insert(insertList);
		phenoService.update(updateList);

		this.getModel().getMessages().add(new ScreenMessage("Save successful", true));
	}

	/**
	 * Load deatils for Individual in parent form
	 * @param db
	 */
	private IndividualDTO loadIndividualDetailsVO(Database db)
	{
		ScreenController<?> parentController    = this.getParent();
		FormModel<Individual> parentForm        = (FormModel<Individual>) ((FormController) parentController).getModel();
		Individual individual                   = parentForm.getRecords().get(0);
		
		PhenoService phenoService               = new PhenoService(db);
		IndividualDTO individualDetailsVO = phenoService.findPhenotypeDetails(individual.getId());
		
		return individualDetailsVO;
	}

	/**
	 * Populate the IndividualForm with widgets for the measurements
	 * @param individualForm
	 * @param individualDetailsVO
	 * @throws HtmlInputException
	 */
	private void populateIndividualForm(IndividualForm individualForm, IndividualDTO individualDetailsVO) throws HtmlInputException
	{
		individualForm.get("__target").setValue(this.getName());

		for (ProtocolDTO protocolVO : individualDetailsVO.getProtocolList())
		{
			// create form input widgets
			// name of each widget conforms with "Protocol" + Protocol.id + ".Feature" + Measurement.id
			for (FeatureDTO featureDTO : protocolVO.getFeatureDTOList())
			{
				String fieldType = featureDTO.getFeatureType();
				String fieldKey  = featureDTO.getFeatureKey();
				
				individualForm.add(MolgenisFieldTypes.createInput(fieldType, fieldKey, ""));
			}
			
			// set values for the input widgets
			// if present, name is changed to "ObservedValue" + ObservedValue.id
			// key of the widget inside the form remains unchanged
			if (individualDetailsVO.getObservedValues().containsKey(protocolVO.getProtocolKey()))
			{
				List<ObservedValueDTO> observedValueVOList = individualDetailsVO.getObservedValues().get(protocolVO.getProtocolKey());

				for (ObservedValueDTO observedValueDTO : observedValueVOList)
				{
					String fieldKey  = protocolVO.getProtocolKey() + "." + observedValueDTO.getFeatureDTO().getFeatureKey();
					String fieldName = "ObservedValue" + observedValueDTO.getObservedValueId();
					individualForm.get(fieldKey).setName(fieldName);
					individualForm.get(fieldKey).setValue(observedValueDTO.getValue());
				}
			}
		}

		this.getModel().setIndividualForm(individualForm);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		IndividualDTO individualDetailsVO = this.loadIndividualDetailsVO(db);
		IndividualForm individualForm           = new IndividualForm();
		this.populateIndividualForm(individualForm, individualDetailsVO);
		
		this.getModel().setIndividualDTO(individualDetailsVO);
		this.getModel().setIndividualForm(individualForm);
	}
}
