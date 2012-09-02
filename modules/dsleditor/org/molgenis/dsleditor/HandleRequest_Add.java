package org.molgenis.dsleditor;

import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.jaxb.Entity;
import org.molgenis.model.jaxb.Field;
import org.molgenis.model.jaxb.Module;
import org.molgenis.util.Tuple;

public class HandleRequest_Add
{
	public static ScreenMessage handle(DSLEditorModel model, Tuple request) throws Exception
	{
		String action = request.getString("__action");

		if (action.equals("addModuleToMolgenis"))
		{
			Module m = new Module();
			m.setName(Helper.renameIfDuplicateModule("newModule", model.getMolgenisModel()));
			model.getMolgenisModel().addModule(m);

			model.setSelectType("module");
			model.setSelectName(m.getName());

			return new ScreenMessage("added module to molgenis '" + model.getMolgenisModel().getName() + "'", true);
		}

		if (action.equals("addEntityToMolgenis"))
		{
			Entity e = new Entity();
			e.setName(Helper.renameIfDuplicateEntity("newEntity", model.getMolgenisModel()));
			model.getMolgenisModel().addEntity(e);

			model.setSelectType("entity");
			model.setSelectName(e.getName());

			return new ScreenMessage("added entity to molgenis '" + model.getMolgenisModel().getName() + "'", true);
		}

		if (action.equals("addEntityToModule"))
		{
			Entity e = new Entity();
			e.setName(Helper.renameIfDuplicateEntity("newEntity", model.getMolgenisModel()));
			model.getMolgenisModel().getModule(request.getString("__selectName")).addEntity(e);

			model.setSelectType("entity");
			model.setSelectName(e.getName());

			return new ScreenMessage("added entity to module '" + request.getString("__selectName") + "'", true);
		}

		if (action.equals("addFieldToEntity"))
		{
			String entityName = request.getString("__selectName");
			Field f = new Field();
			f.setName(Helper.renameIfDuplicateField("newField", entityName, model.getMolgenisModel()));
			model.getMolgenisModel().findEntity(entityName).addField(f);
			model.setSelectType("field");
			model.setSelectName(f.getName());
			model.setSelectFieldEntity(entityName);
			model.setSelectFieldIndex(model.getMolgenisModel().findEntity(entityName).getFields().indexOf(f));

			return new ScreenMessage("added field to entity '" + entityName + "'", true);
		}

		return new ScreenMessage("action '" + action + "'not reckognized", false);
	}

}