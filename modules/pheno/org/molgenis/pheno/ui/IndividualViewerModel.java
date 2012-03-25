package org.molgenis.pheno.ui;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.framework.ui.html.Input;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.dto.IndividualDTO;
import org.molgenis.pheno.ui.form.IndividualForm;

public class IndividualViewerModel extends EasyPluginModel
{
	private static final long serialVersionUID = 2545649822397395528L;
	private String action;
	private Integer id;
	private IndividualDTO individualDTO;
	private IndividualForm individualForm;

	public IndividualViewerModel(IndividualViewer controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public IndividualDTO getIndividualDTO()
	{
		return individualDTO;
	}

	public void setIndividualDTO(IndividualDTO individualDTO)
	{
		this.individualDTO = individualDTO;
	}

	public IndividualForm getIndividualForm()
	{
		return individualForm;
	}

	public void setIndividualForm(IndividualForm individualForm)
	{
		this.individualForm = individualForm;
	}

	public Input<?> createInput(String name)
	{
		return this.individualForm.get(name);
	}

	public boolean isEditable()
	{
		try
		{
			return this.getController().getApplicationController().getLogin().canWrite(ObservedValue.class);
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
		return false;
	}

//
//	@Override
//	public boolean isVisible()
//	{
//		return true;
//	}
}
