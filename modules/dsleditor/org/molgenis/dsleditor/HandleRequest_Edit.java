package org.molgenis.dsleditor;

import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

public class HandleRequest_Edit
{
	public static ScreenMessage handle(DSLEditorModel model, Tuple request) throws Exception
	{
		String action = request.getString("__action");
		if (action.equals("editMolgenis"))
		{
			model.setSelectType("molgenis");
			model.setSelectName(request.getString("__selectName"));
			return new ScreenMessage("now editing 'molgenis'", true);
		}
		if (action.equals("editModule"))
		{
			model.setSelectType("module");
			model.setSelectName(request.getString("__selectName"));
			return new ScreenMessage("now editing 'module'", true);
		}
		if (action.equals("editEntity"))
		{
			model.setSelectType("entity");
			model.setSelectName(request.getString("__selectName"));
			return new ScreenMessage("now editing 'entity'", true);
		}
		if (action.equals("editField"))
		{
			model.setSelectType("field");
			model.setSelectName(request.getString("__selectName"));
			model.setSelectFieldIndex(request.getInt("__selectFieldIndex"));
			model.setSelectFieldEntity(request.getString("__selectFieldEntity"));
			return new ScreenMessage("now editing 'field'", true);
		}

		return new ScreenMessage("action '" + action + "'not reckognized", false);
	}
}