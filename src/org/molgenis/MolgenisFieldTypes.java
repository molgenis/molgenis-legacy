package org.molgenis;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.molgenis.fieldtypes.BoolField;
import org.molgenis.fieldtypes.DateField;
import org.molgenis.fieldtypes.DateTimeField;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.EnumField;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.fieldtypes.FileField;
import org.molgenis.fieldtypes.HexaField;
import org.molgenis.fieldtypes.HyperlinkField;
import org.molgenis.fieldtypes.ImageField;
import org.molgenis.fieldtypes.IntField;
import org.molgenis.fieldtypes.LongField;
import org.molgenis.fieldtypes.MrefField;
import org.molgenis.fieldtypes.NSequenceField;
import org.molgenis.fieldtypes.OnOffField;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.fieldtypes.TextField;
import org.molgenis.fieldtypes.UnknownField;
import org.molgenis.fieldtypes.XrefField;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

/**
 * Singleton class that holds all known field types in MOLGENIS.
 * For each FieldType it can be defined how to behave in mysql, java, hsqldb, etc. <br>
 * 
 * @see FieldType interface
 */
public class MolgenisFieldTypes
{
	private static Map<String, FieldType> types = new TreeMap<String, FieldType>();
	private static Logger logger = Logger.getLogger(MolgenisFieldTypes.class);
	private static boolean init = false;
	
	/** Initialize default field types */
	private static void init()
	{
		if (!init)
		{
			addType(new BoolField());
			addType(new DateField());
			addType(new DateTimeField());
			addType(new DecimalField());
			addType(new EnumField());
			addType(new FileField());
			addType(new ImageField());
			addType(new HyperlinkField());
			addType(new LongField());
			addType(new MrefField());
			addType(new NSequenceField());
			addType(new OnOffField());
			addType(new StringField());
			addType(new TextField());
			addType(new XrefField());
			addType(new IntField());
			addType(new HexaField());

			init = true;
		}

	}

	public static void addType(FieldType ft)
	{
		types.put(ft.getClass().getSimpleName().toLowerCase(), ft);
		// ft.setRegistry();
	}
	
	public static HtmlInput createInput(String type, String name, String xrefEntityClassName, Database db) throws InstantiationException, IllegalAccessException
	{
		return getType(type).createInput(name, xrefEntityClassName, db);
	}

	public static FieldType getType(String name)
	{
		init();
		try
		{
			String fieldType = name + "field";
			FieldType ft = types.get(fieldType);
			ft = ft.getClass().newInstance();
			// ft.setField(f);
			// ft.setRegistry();
			return ft;
		}
		catch (Exception e)
		{
			logger.warn("couldn't get type for name '" + name + "'");
			return new UnknownField();
		}
	}

	public static FieldType get(Field f) throws MolgenisModelException
	{
		init();
		try
		{
			// String fieldType = f.getType().getClass()+ "field";
			// FieldType ft = types.get(fieldType);
			// ft = ft.getClass().newInstance();
			// ft.setField(f);
			// //ft.setRegistry();
			FieldType ft = f.getType().getClass().newInstance();
			ft.setField(f);
			return ft;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MolgenisModelException(e.getMessage());
		}
	}
}
