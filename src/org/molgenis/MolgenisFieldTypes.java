package org.molgenis;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.molgenis.fieldtypes.BoolField;
import org.molgenis.fieldtypes.DateField;
import org.molgenis.fieldtypes.DatetimeField;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.EnumField;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.fieldtypes.FileField;
import org.molgenis.fieldtypes.FreemarkerField;
import org.molgenis.fieldtypes.HexaField;
import org.molgenis.fieldtypes.RichtextField;
import org.molgenis.fieldtypes.HyperlinkField;
import org.molgenis.fieldtypes.ImageField;
import org.molgenis.fieldtypes.IntField;
import org.molgenis.fieldtypes.LongField;
import org.molgenis.fieldtypes.MrefField;
import org.molgenis.fieldtypes.NSequenceField;
import org.molgenis.fieldtypes.OnoffField;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.fieldtypes.TextField;
import org.molgenis.fieldtypes.UnknownField;
import org.molgenis.fieldtypes.XrefField;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
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
	
	public enum FieldTypeEnum {
		BOOL,
		CHAR,
		DATE,
		DATE_TIME,
		DECIMAL,
		ENUM,
		FILE,
		FREEMARKER,
		HEXA,
		HYPERLINK,
		IMAGE,
		INT,
		LIST,
		LONG,
		MREF,
		NSEQUENCE,
		ON_OFF,
		RICHTEXT,
		STRING,
		TEXT,
		XREF,
		UNKNOWN
	}
	
	/** Initialize default field types */
	private static void init()
	{
		if (!init)
		{
			addType(new BoolField());
			addType(new DateField());
			addType(new DatetimeField());
			addType(new DecimalField());
			addType(new EnumField());
			addType(new FileField());
			addType(new ImageField());
			addType(new HyperlinkField());
			addType(new LongField());
			addType(new MrefField());
			addType(new NSequenceField());
			addType(new OnoffField());
			addType(new StringField());
			addType(new TextField());
			addType(new XrefField());
			addType(new IntField());
			addType(new HexaField());
			addType(new RichtextField());
			addType(new FreemarkerField());

			init = true;
		}

	}

	public static void addType(FieldType ft)
	{
		types.put(ft.getClass().getSimpleName().toLowerCase(), ft);
	}
	
	public static HtmlInput<?> createInput(String type, String name, String xrefEntityClassName) throws HtmlInputException
	{
		return getType(type).createInput(name, xrefEntityClassName);
	}

	public static FieldType getType(String name)
	{
		init();
		try
		{
			return types.get(name + "field").getClass().newInstance();
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
