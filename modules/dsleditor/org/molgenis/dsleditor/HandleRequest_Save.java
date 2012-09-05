package org.molgenis.dsleditor;

import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.jaxb.Entity;
import org.molgenis.model.jaxb.Field;
import org.molgenis.util.Tuple;

public class HandleRequest_Save
{
	public static ScreenMessage handle(DSLEditorModel model, Tuple request) throws Exception
	{
		String action = request.getString("__action");
		if (action.equals("saveMolgenis"))
		{
			String molgenisName = request.getString("molgenisName");
			String molgenisLabel = request.getString("molgenisLabel");
			String molgenisVersion = request.getString("molgenisVersion");

			if (molgenisName == null)
			{
				throw new Exception("Please provide a name");
			}

			model.getMolgenisModel().setName(molgenisName);
			model.getMolgenisModel().setLabel(molgenisLabel);
			model.getMolgenisModel().setVersion(molgenisVersion);

			model.setSelectType("molgenis");
			model.setSelectName(molgenisName);

			return new ScreenMessage("molgenis '" + molgenisName + "' updated", true);
		}

		if (action.equals("saveModule"))
		{
			String moduleName = request.getString("moduleName");
			if (moduleName == null)
			{
				throw new Exception("Please provide a name");
			}
			else if (request.getString("__selectName").equals(moduleName))
			{
				return new ScreenMessage("Please enter a different name", false);
			}

			String removeDuplicate = Helper.renameIfDuplicateModule(moduleName, model.getMolgenisModel());
			model.getMolgenisModel().getModule(request.getString("__selectName")).setName(removeDuplicate);

			model.setSelectType("module");
			model.setSelectName(removeDuplicate);

			return new ScreenMessage("module '" + removeDuplicate + "' updated", true);
		}

		if (action.equals("saveEntity"))
		{
			String entityName = request.getString("entityName");
			String entityLabel = request.getString("entityLabel");
			String entityExtends = request.getString("entityExtends");
			String entityImplements = request.getString("entityImplements");
			String entityDecorator = request.getString("entityDecorator");
			Boolean _abstract = request.getBoolean("abstract");

			if (entityName == null)
			{
				throw new Exception("Please provide a name");
			}
			else if (!request.getString("__selectName").equals(entityName))
			{
				entityName = Helper.renameIfDuplicateEntity(entityName, model.getMolgenisModel());
			}

			Entity changeMe = model.getMolgenisModel().findEntity(request.getString("__selectName"));
			changeMe.setName(entityName);
			changeMe.setLabel(entityLabel);
			changeMe.setExtends(entityExtends);
			changeMe.setImplements(entityImplements);
			changeMe.setDecorator(entityDecorator);
			changeMe.setAbstract(_abstract);

			model.setSelectType("entity");
			model.setSelectName(entityName);

			return new ScreenMessage("entity '" + entityName + "' updated", true);
		}

		if (action.equals("saveField"))
		{

			String nameOfEntity = request.getString("__selectFieldEntity");
			int index = request.getInt("__selectFieldIndex");
			Field f = model.getMolgenisModel().findEntity(nameOfEntity).getFields().get(index);

			String fieldName = request.getString("fieldName");
			String fieldLabel = request.getString("fieldLabel");
			String fieldTypeSelect = request.getString("fieldTypeSelect");
			String xref_field = request.getString("xref_field");
			String xref_label = request.getString("xref_label");
			String enum_options = request.getString("enum_options");
			String fieldDescription = request.getString("fieldDescription");
			Integer fieldLength = request.getInt("fieldLength");
			Boolean _unique = request.getBoolean("fieldUnique");
			Boolean _nillable = request.getBoolean("fieldNillable");
			Boolean _readonly = request.getBoolean("fieldReadonly");

			// TODO: Boolean _xref_cascade = request.getBoolean("xref_cascade");

			// TODO: LAST ATTRIBUTE DESCRIBED
			// (http://www.molgenis.org/wiki/FieldElement)
			// default="Pre-filling": sets a default value for this field. This
			// value is automatically filled in for this field unless the user
			// decides otherwise.

			if (fieldName == null)
			{
				throw new Exception("Please provide a name");
			}
			else if (!f.getName().equals(fieldName))
			{
				fieldName = Helper.renameIfDuplicateField(fieldName, model.getSelectFieldEntity(),
						model.getMolgenisModel());
				System.out.println(fieldName);
			}

			f.setName(fieldName);
			f.setLabel(fieldLabel);
			f.setType(Field.Type.getType(fieldTypeSelect));
			f.setUnique(_unique);
			f.setNillable(_nillable);
			f.setReadonly(_readonly);
			f.setXrefField(xref_field);
			f.setXrefLabel(xref_label);
			f.setEnumoptions(enum_options);
			f.setDescription(fieldDescription);

			// length="n" (string only): limits the length of a string to 1 <= n
			// <= 255 (default: "255").
			if (fieldLength == null)
			{
				fieldLength = 255;
			}

			if ((fieldLength > 0 && fieldLength <= 255))
			{
				f.setLength(fieldLength);
			}
			else
			{
				// set default value and show message
				fieldLength = 255;
				throw new Exception("Enter a value from 1 to 255");
			}

			model.setSelectName(f.getName());
			model.setSelectType("field");

			return new ScreenMessage("field '" + fieldName + "' updated", true);

		}

		return new ScreenMessage("action '" + action + "'not recognized", false);
	}

}