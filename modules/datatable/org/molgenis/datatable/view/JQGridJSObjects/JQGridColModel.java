package org.molgenis.datatable.view.JQGridJSObjects;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;

import app.DatabaseFactory;

public class JQGridColModel
{

	public static class SearchOptions
	{
		public boolean required = true;
		public boolean searchhidden = true;
		public String stype = "text";
		public String[] sopt = new String[]
		{ "eq", "ne", "bw", "bn", "ew", "en", "cn", "nc" };

		public String dataInit = "function(elem){ $(elem).datepicker({dateFormat:\"mm/dd/yyyy\"});}}";

		public SearchOptions()
		{
		}

		public SearchOptions(String[] sopt)
		{
			this.sopt = sopt;
		}

		public static SearchOptions create(FieldTypeEnum fte)
		{
			switch (fte)
			{
				case INT:
				case LONG:
				case DECIMAL:
				case DATE:
				case DATE_TIME:
					return new SearchOptions(new String[]
					{ "eq", "ne", "lt", "le", "gt", "ge" });

				default:
					return new SearchOptions();
			}
		}
	}

	public static class SearchRule
	{
		// public boolean required = false;
		public boolean number = false;
		public boolean integer = false;
		public boolean email = false;
		public boolean date = false;
		public boolean time = false;

		public static SearchRule createSearchRule(FieldTypeEnum fte)
		{
			switch (fte)
			{
				case INT:
				case LONG:
				{
					final SearchRule rule = new SearchRule();
					rule.integer = true;
					return rule;
				}
				case DECIMAL:
				{
					final SearchRule rule = new SearchRule();
					rule.number = true;
					return rule;
				}
				case DATE:
				{
					final SearchRule rule = new SearchRule();
					rule.date = true;
					return rule;
				}

				default:
					return new SearchRule();
			}
		}
	}

	public static class EditOptions
	{
		// public boolean required = false;
		public String value;
		public String disabled;
		public String style;
		public String name;

		public static EditOptions createEditOptions(String actualValue)
		{
			final EditOptions editOptions = new EditOptions();
			editOptions.value = actualValue;

			return editOptions;
		}

		// make Pa_Id field disabled and lightgrey
		public static EditOptions createEditOptions()
		{
			final EditOptions editOptions = new EditOptions();

			return editOptions;
		}
	}

	public final String name;
	public final String index;
	public int width = 100;
	public boolean sortable = false;
	public boolean search = true;
	public boolean fixed = false;
	public SearchOptions searchoptions;
	public SearchRule searchrules;
	public EditOptions editoptions;
	public boolean editable;
	// for tree view
	public final String title;
	public final boolean isFolder = false;
	public final String path;
	public String edittype;
	public String add;
	public String datetype;

	public JQGridColModel(Field f)
	{
		this.name = f.getSqlName();
		this.index = f.getSqlName();
		this.title = name;
		this.path = title;
		this.editable = true;
		this.sortable = false;

		try
		{
			Database db = DatabaseFactory.create();

			if (!name.equals("Pa_Id"))
			{

				Measurement m = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, this.name))
						.get(0);

				String dataType = m.getDataType();

				String temporary = ":;";

				if ("categorical".equals(dataType))
				{

					for (Category c : db.find(Category.class,
							new QueryRule(Category.NAME, Operator.IN, m.getCategories_Name())))
					{
						temporary += c.getCode_String() + "." + c.getDescription() + ":" + c.getCode_String() + "."
								+ c.getDescription() + ";";
					}

					temporary = temporary.substring(0, temporary.length() - 1);

					edittype = "select";

					editoptions = EditOptions.createEditOptions(temporary);

				}
				else if ("bool".equals(dataType))
				{
					edittype = "select";

				}
				else if ("datetime".equals(dataType))
				{
					datetype = "datetype";
				}
			}
			else
			{

				editoptions = EditOptions.createEditOptions();
				editoptions.disabled = "disabled";
				editoptions.style = "width:100px;background:lightgrey";
				fixed = true;
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.searchoptions = SearchOptions.create(f.getType().getEnumType());
		this.searchrules = SearchRule.createSearchRule(f.getType().getEnumType());
	}
}