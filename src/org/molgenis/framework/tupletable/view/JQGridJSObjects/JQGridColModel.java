package org.molgenis.framework.tupletable.view.JQGridJSObjects;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.model.elements.Field;

public class JQGridColModel {

	public static class SearchOptions {
		public boolean required = true;
		public boolean searchhidden = true;
		public String value;
		public String[] sopt = new String[] { "eq", "ne", "bw", "bn", "ew",
				"en", "cn", "nc" };

		public String dataInit = "function(elem){ $(elem).datepicker({dateFormat:\"mm/dd/yyyy\"});}}";

		public SearchOptions() {
		}

		public SearchOptions(String[] sopt) {
			this.sopt = sopt;
		}

		public static SearchOptions create(FieldTypeEnum fte) {
			switch (fte) {
			case INT:
			case LONG:
			case DECIMAL:
			case DATE:
			case DATE_TIME:
				return new SearchOptions(new String[] { "eq", "ne", "lt", "le",
						"gt", "ge" });

			default:
				return new SearchOptions();
			}
		}
	}

	public static class SearchRule {
		// public boolean required = false;
		public boolean number = false;
		public boolean integer = false;
		public boolean email = false;
		public boolean date = false;
		public boolean time = false;

		public static SearchRule createSearchRule(FieldTypeEnum fte) {
			switch (fte) {
			case INT:
			case LONG: {
				final SearchRule rule = new SearchRule();
				rule.integer = true;
				return rule;
			}
			case DECIMAL: {
				final SearchRule rule = new SearchRule();
				rule.number = true;
				return rule;
			}
			case DATE: {
				final SearchRule rule = new SearchRule();
				rule.date = true;
				return rule;
			}

			default:
				return new SearchRule();
			}
		}
	}

	public static class EditOptions {
		// public boolean required = false;
		public String value;
		public String disabled;
		public String style;
		public String name;

		public static EditOptions createEditOptions(String actualValue) {
			final EditOptions editOptions = new EditOptions();
			editOptions.value = actualValue;

			return editOptions;
		}

		// make Pa_Id field disabled and lightgrey
		public static EditOptions createEditOptions() {
			final EditOptions editOptions = new EditOptions();

			return editOptions;
		}
	}

	public final String name;
	public final String index;
	public int width = 100;
	public String stype;
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

	public JQGridColModel(String f) {
		this.name = f;
		this.index = f;
		this.title = f;
		this.path = f;
		this.editable = false;
		this.sortable = false;
	}

	public JQGridColModel(Field f) {
		this.name = f.getSqlName();
		this.index = f.getSqlName();
		this.title = name;
		this.path = title;
		this.editable = true;
		this.sortable = false;

		this.searchoptions = SearchOptions.create(f.getType().getEnumType());
		this.searchrules = SearchRule.createSearchRule(f.getType()
				.getEnumType());

		try {

			if (!name.equals("Pa_Id")) {

				String temporary = "";

				if ("enum".equals(f.getType().toString())) {

					for (String category : f.getEnumOptions()) {

						String code = category.split("\\.")[0];

						temporary += code + ":" + category + ";";
					}

					temporary = temporary.substring(0, temporary.length() - 1);

					edittype = "select";

					editoptions = EditOptions.createEditOptions(":;"
							+ temporary);
					searchoptions.sopt = new String[] { "eq", "ne" };
					this.stype = "select";
					searchoptions.value = temporary;

				} else if ("bool".equals(f.getType().toString())) {
					edittype = "select";

				} else if ("datetime".equals(f.getType().toString())) {
					datetype = "datetype";
				}

			} else {


				editoptions = EditOptions.createEditOptions();
				editoptions.disabled = "disabled";
				editoptions.style = "width:100px;background:lightgrey";
				fixed = true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}