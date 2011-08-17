package org.molgenis.framework.ui.commands;

import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.EntityForm;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.ActionInput.Type;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * The command to add a new record
 */
public class AddXrefCommand<E extends Entity> extends AddCommand<E>
{
	private static final long serialVersionUID = 1512493344265778285L;
	private E xrefEntity;
	private EntityForm<?> xrefForm;

	public AddXrefCommand(ScreenController<?>  parent)
	{
		this("", parent, null, null);
	}

	public AddXrefCommand(String name, ScreenController<?> parent, E xrefEntity, EntityForm<?> xrefForm)
	{
		super(name, parent);
		this.xrefEntity = xrefEntity;
		this.xrefForm   = xrefForm;
		this.setLabel("Add " + xrefEntity.getClass().getSimpleName());
		this.setIcon("generated-res/img/new.png");
		this.setDialog(true);
		this.setMenu("Edit");
		this.setToolbar(false);
	}

	@Override
	public List<ActionInput> getActions()
	{
		List<ActionInput> actions = super.getActions();
		
		// Override functionality of standard save button:
		// postData(): post data via rest api, retrieve object including id back
		// setXrefOption(): set newly inserted object as current option
		for (int i = 0; i < actions.size(); i++)
			if (actions.get(i).getType() == Type.SAVE)
				actions.get(i).setJavaScriptAction("if( validateForm(molgenis_popup,molgenis_required) ) { if( window.opener.name == '' ){ window.opener.name = 'molgenis'+Math.random();} var entity = postData(document.forms[0].entity_name.value); window.opener.setXrefOption(document.forms[0].__action.value, document.forms[0].id_field.value, document.forms[0].label_field.value, entity); window.close();} else return false;");
		
		return actions;
	}

	@Override
	public List<HtmlInput<?>> getInputs() throws DatabaseException
	{
		List<HtmlInput<?>> inputs = this.xrefForm.getInputs();
		// add two hidden fields for javascript to know id field and label field
		inputs.add(new HiddenInput("entity_name", StringUtils.uncapitalise(xrefEntity.getClass().getSimpleName())));
		inputs.add(new HiddenInput("id_field", this.xrefEntity.getIdField()));
		inputs.add(new HiddenInput("label_field", this.xrefEntity.getLabelFields().get(0)));
		return inputs;
	}

	@Override
	public boolean isVisible()
	{
		return false; 
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, OutputStream downloadStream) throws Exception
	{
		if (request.getString(FormModel.INPUT_SHOW) == null)
		{
			ScreenMessage msg = null;
			try
			{
				db.beginTx();
				xrefEntity.set(request);
				int updatedRows = db.add(xrefEntity);
				db.commitTx();
				msg = new ScreenMessage("ADD SUCCESS: affected " + updatedRows,	null, true);
			}
			catch (Exception e)
			{
				msg = new ScreenMessage("ADD FAILED: " + e.getMessage(), null, false);
				e.printStackTrace();
				if (db.inTx())
					db.rollbackTx();
			}
			((FormController<?>) this.getController()).getModel().getMessages().add(msg);
		}
		return ScreenModel.Show.SHOW_MAIN;
	}
}