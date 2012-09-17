package org.molgenis.pheno.ui;

import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.dto.FeatureDTO;
import org.molgenis.pheno.dto.IndividualDTO;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.pheno.dto.ProtocolApplicationDTO;
import org.molgenis.pheno.dto.ProtocolDTO;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.pheno.ui.form.ApplyProtocolForm;
import org.molgenis.pheno.ui.form.IndividualForm;
import org.molgenis.pheno.ui.form.SelectProtocolForm;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public class IndividualViewer extends EasyPluginController<IndividualViewerModel>
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 8333436730236052413L;

	private ScreenView view;
	
	public IndividualViewer(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new IndividualViewerModel(this));
		this.getModel().setAction("show");
		this.setView(new FreemarkerView("show.ftl", getModel()));
	}
	
	public void setView(ScreenView view)
	{
		this.view = view;
	}
	
	public ScreenView getView()
	{
		return view;
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
				this.setView(new FreemarkerView("show.ftl", getModel()));
			}
			else if ("select".equals(request.getAction()))
			{
				this.setView(new FreemarkerView("select.ftl", getModel()));
				this.handleSelect(db, request);
			}
			else if ("add".equals(request.getAction()))
			{
				this.setView(new FreemarkerView("add.ftl", getModel()));
				this.handleAdd(db, request);
			}
			else if ("edit".equals(request.getAction()))
			{
				this.setView(new FreemarkerView("edit.ftl", getModel()));
			}
			else if ("insert".equals(request.getAction()))
			{
				this.handleInsert(db, request);
			}
			else if ("update".equals(request.getAction()))
			{
				this.setView(new FreemarkerView("edit.ftl", getModel()));
				this.handleUpdate(db, request);
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
	 * Select a Protocol to be applied
	 * @param db
	 * @param request
	 * @throws HtmlInputException 
	 */
	private void handleSelect(Database db, Tuple request) throws HtmlInputException
	{
		PhenoService phenoService         = new PhenoService(db);
		List<ProtocolDTO> protocolDTOList = phenoService.findProtocols();

		SelectProtocolForm form           = new SelectProtocolForm();
		this.populateSelectProtocolForm(form, protocolDTOList);
	}

	/**
	 * Add a new ProtocolApplication aka "Apply Protocol"
	 * @param db
	 * @param request
	 * @throws HtmlInputException 
	 */
	private void handleAdd(Database db, Tuple request) throws HtmlInputException
	{
		PhenoService phenoService = new PhenoService(db);
		ProtocolDTO protocolDTO   = phenoService.findProtocol(request.getInt("Protocol"));

		ApplyProtocolForm form    = new ApplyProtocolForm();
		this.populateApplyProtocolForm(form, protocolDTO);
		
		this.getModel().setProtocolDTO(protocolDTO);
		this.getModel().setApplyProtocolForm(form);
	}

	private void populateSelectProtocolForm(SelectProtocolForm selectProtocolForm, List<ProtocolDTO> protocolDTOList)
	{
		selectProtocolForm.get("__target").setValue(this.getName());

		List<ValueLabel> protocolOptions = new ArrayList<ValueLabel>();
		protocolOptions.add(new ValueLabel("", "Select a Protocol"));
		for (ProtocolDTO protocolDTO : protocolDTOList)
			protocolOptions.add(new ValueLabel(protocolDTO.getProtocolId(), protocolDTO.getProtocolName()));
		((SelectInput) selectProtocolForm.get("Protocol")).setOptions(protocolOptions);
				
		this.getModel().setSelectProtocolForm(selectProtocolForm);
	}

	private void populateApplyProtocolForm(ApplyProtocolForm applyProtocolForm, ProtocolDTO protocolDTO) throws HtmlInputException
	{
		applyProtocolForm.get("__target").setValue(this.getName());

		for (FeatureDTO featureDTO : protocolDTO.getFeatureDTOList())
		{
			String fieldType = featureDTO.getFeatureType();
			String fieldName = featureDTO.getFeatureKey();
			applyProtocolForm.add(MolgenisFieldTypes.createInput(fieldType, fieldName, ""));
		}

		this.getModel().setProtocolDTO(protocolDTO);
		this.getModel().setApplyProtocolForm(applyProtocolForm);
	}

	/**
	 * Insert a new ProtocolApplication
	 * @param db
	 * @param request
	 * @throws ParseException 
	 */
	private void handleInsert(Database db, Tuple request) throws ParseException
	{
		this.setView(new FreemarkerView("show.ftl", getModel()));

		this.loadIndividualDetailsVO(db);

		List<ObservedValueDTO> insertList = new ArrayList<ObservedValueDTO>();
		List<String> parameterNameList    = request.getFields();

		PhenoService phenoService         = new PhenoService(db);

		ProtocolApplicationDTO paDTO      = new ProtocolApplicationDTO();
		paDTO.setName(request.getString("paName"));
		paDTO.setTime(request.getDate("paTime"));
		paDTO.setProtocolId(this.getModel().getProtocolDTO().getProtocolId());
		List<Integer> performerIdList     = new ArrayList<Integer>();
		for (String s : request.getStringList("paPerformer"))
		{
			performerIdList.add(Integer.parseInt(s));
		}
		paDTO.setPerformerIdList(performerIdList);

		Integer paId                      = phenoService.insert(paDTO);

		for (String parameterName : parameterNameList)
		{
			if (parameterName.startsWith("Feature"))
			{
				// insert a new ObservedValue
//				String[] parameterNameParts        = StringUtils.split(parameterName, ".");
//				String protocolApplicationIdString = StringUtils.removeStartIgnoreCase(parameterNameParts[0], "ProtocolApplication");
				String featureIdString             = StringUtils.removeStartIgnoreCase(parameterName, "Feature");

				ObservedValueDTO observedValueDTO  = new ObservedValueDTO();
				observedValueDTO.setValue(request.getString(parameterName));
				observedValueDTO.setTargetId(this.getModel().getIndividualDTO().getIndividualId());
				observedValueDTO.setProtocolApplicationId(paId);
				FeatureDTO featureDTO              = new FeatureDTO();
				featureDTO.setFeatureId(Integer.parseInt(featureIdString));
				observedValueDTO.setFeatureDTO(featureDTO);
				insertList.add(observedValueDTO);
			}
		}

		phenoService.insert(insertList);

		this.getModel().getMessages().add(new ScreenMessage("Save successful", true));
	}

	/**
	 * Update ObservedValues of an Individual
	 * @param db
	 * @param request
	 */
	private void handleUpdate(Database db, Tuple request)
	{
		this.setView(new FreemarkerView("edit.ftl", getModel()));

		this.loadIndividualDetailsVO(db);

		List<ObservedValueDTO> updateList = new ArrayList<ObservedValueDTO>();
		List<String> parameterNameList    = request.getFields();

		PhenoService phenoService = new PhenoService(db);

		for (String parameterName : parameterNameList)
		{
			if (parameterName.startsWith("ObservedValue"))
			{
				String observedValueIdString      = StringUtils.removeStartIgnoreCase(parameterName, "ObservedValue");
				ObservedValueDTO observedValueDTO = phenoService.findObservedValue(Integer.parseInt(observedValueIdString));
				observedValueDTO.setValue(request.getString(parameterName));
				updateList.add(observedValueDTO);
			}
		}

		phenoService.update(updateList);

		this.getModel().getMessages().add(new ScreenMessage("Save successful", true));
	}

	/**
	 * Load details for Individual in parent form
	 * @param db
	 */
	private IndividualDTO loadIndividualDetailsVO(Database db)
	{
		ScreenController<?> parentController = this.getParent();
		FormModel<Individual> parentForm     = (FormModel<Individual>) ((FormController) parentController).getModel();
		
		if (parentForm.getRecords().size() == 0)
			return null;

		Individual individual                = parentForm.getRecords().get(0);
		
		PhenoService phenoService            = new PhenoService(db);
		IndividualDTO individualDetailsVO    = phenoService.findPhenotypeDetails(individual.getId());
		
		return individualDetailsVO;
	}

	/**
	 * Populate the IndividualForm with widgets for the measurements
	 * @param individualForm
	 * @param individualDetailsDTO
	 * @throws HtmlInputException
	 */
	private void populateIndividualForm(IndividualForm individualForm, IndividualDTO individualDetailsDTO) throws HtmlInputException
	{
		individualForm.get("__target").setValue(this.getName());

		for (ProtocolDTO protocolDTO : individualDetailsDTO.getProtocolList())
		{
			// create form input widgets
			// name of each widget conforms with "Protocol" + Protocol.id + ".Feature" + Measurement.id
//			for (FeatureDTO featureDTO : protocolDTO.getFeatureDTOList())
//			{
//				String fieldType = featureDTO.getFeatureType();
//				String fieldKey  = featureDTO.getFeatureKey();
//				
//				individualForm.add(MolgenisFieldTypes.createInput(fieldType, fieldKey, ""));
//			}
			
			// set values for the input widgets
			// if present, name is changed to "ObservedValue" + ObservedValue.id
			// key of the widget inside the form remains unchanged
			if (individualDetailsDTO.getObservedValues().containsKey(protocolDTO.getProtocolKey()))
			{
				List<String> protocolApplicationKeyList = Arrays.asList(individualDetailsDTO.getObservedValues().get(protocolDTO.getProtocolKey()).keySet().toArray(new String[0]));
				
				for (String protocolApplicationKey : protocolApplicationKeyList)
				{
					List<ObservedValueDTO> observedValueDTOList = individualDetailsDTO.getObservedValues().get(protocolDTO.getProtocolKey()).get(protocolApplicationKey);
					
					for (ObservedValueDTO observedValueDTO : observedValueDTOList)
					{
						String fieldType = observedValueDTO.getFeatureDTO().getFeatureType();
//						String fieldKey  = protocolApplicationKey + "." + observedValueDTO.getFeatureDTO().getFeatureKey();
						String fieldName = "ObservedValue" + observedValueDTO.getObservedValueId();
						individualForm.add(MolgenisFieldTypes.createInput(fieldType, fieldName, ""));
						individualForm.get(fieldName).setName(fieldName);
						individualForm.get(fieldName).setValue(observedValueDTO.getValue());
					}
				}
			}
		}

		this.getModel().setIndividualForm(individualForm);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		IndividualDTO individualDetailsVO = this.loadIndividualDetailsVO(db);
		
		if (individualDetailsVO != null)
		{
			IndividualForm individualForm     = new IndividualForm();
			this.populateIndividualForm(individualForm, individualDetailsVO);
		
			this.getModel().setIndividualDTO(individualDetailsVO);
			this.getModel().setIndividualForm(individualForm);
			
			PhenoService phenoService         = new PhenoService(db);
			this.getModel().setProtocolDTOList(phenoService.findProtocols());
		}
	}
}
