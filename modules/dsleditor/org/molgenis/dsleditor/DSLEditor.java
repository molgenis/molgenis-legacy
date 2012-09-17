/* Date:        April 22, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.dsleditor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.jaxb.Field;
import org.molgenis.model.jaxb.Field.Type;
import org.molgenis.model.jaxb.Model;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class DSLEditor<E extends Entity> extends PluginModel<E>
{
	/**
	 * MOLGENIS DSL Editor by Jacques and Joeri
	 * 
	 * Specific TODO for later: - on 'molgenis', add Description, Menu, Form,
	 * Plugin? - on 'module', add Description
	 * 
	 * 
	 * Feature TODO: - finish all screens and test - improve GUI layout - model
	 * validator (CAN'T BE DONE AT THE MOMENT) - load example button - save and
	 * load from file - 'batch' add field/module/entity (not 1, but N at a time)
	 * - marshaller error handling! - remove buttons next to elements in tree -
	 * save many model to database under a name - load models from database,
	 * 'workbench feel' - couple to generators for molgenis (config ap) - add
	 * GUI building / properties file config screen - save 'complete' apps to
	 * database and share them with others? :)
	 */
	private static final long serialVersionUID = 5163895580240779726L;

	public DSLEditor(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private DSLEditorModel model = new DSLEditorModel();

	public DSLEditorModel getVO()
	{
		return model;
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_dsleditor_DSLEditor";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/dsleditor/DSLEditor.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
			System.out.println("*** handleRequest __action: " + action);

			try
			{

				if (action.startsWith("edit"))
				{
					this.setMessages(HandleRequest_Edit.handle(model, request));
				}

				if (action.startsWith("save"))
				{
					this.setMessages(HandleRequest_Save.handle(model, request));
				}

				if (action.startsWith("add"))
				{
					this.setMessages(HandleRequest_Add.handle(model, request));
				}

				if (action.startsWith("remove"))
				{
					this.setMessages(HandleRequest_Remove.handle(model, request));
				}

				if (action.equals("toXml"))
				{
					String xml = Helper.modelToXml(model.getMolgenisModel());
					this.model.setXmlPreview(xml);
				}

				if (action.equals("fromXml"))
				{
					Model molgenisModel = Helper.xmlToModel(request.getString("xmlWindow"));

					// MolgenisModelValidator.validate(this.model.getMolgenisModel());
					// not yet possible!!! doesnt work for JAXB..

					this.model.setMolgenisModel(molgenisModel);

					this.model.setSelectType("molgenis");
					this.model.setSelectName(molgenisModel.getName());
				}

				if (action.equals("resetModel"))
				{
					this.model.setMolgenisModel(null);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{
		// if there is no molgenismodel, create one
		if (this.getVO().getMolgenisModel() == null)
		{

			Model molgenisModel = new Model();

			molgenisModel.setName("myMolgenis");
			molgenisModel.setLabel("My Molgenis application");
			molgenisModel.setVersion("0.0.1");

			this.model.setMolgenisModel(molgenisModel);
			this.model.setSelectType("molgenis");
			this.model.setSelectName("myMolgenis");
		}

		if (this.model.getFieldTypes() == null)
		{
			List<String> fieldTypes = new ArrayList<String>();
			for (Type t : Field.Type.values())
			{
				fieldTypes.add(t.toString());
			}
			this.model.setFieldTypes(fieldTypes);
		}

		String previewXml = null;
		try
		{
			previewXml = Helper.modelToXml(model.getMolgenisModel());
		}
		catch (JAXBException e)
		{

			e.printStackTrace();
		}
		this.model.setXmlPreview(previewXml);
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
}