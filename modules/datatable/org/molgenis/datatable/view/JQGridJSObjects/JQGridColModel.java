package org.molgenis.datatable.view.JQGridJSObjects;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.model.elements.Field;

public class JQGridColModel {

	public static class SearchOptions {
		public boolean required = true;
		public boolean searchhidden = true;
		public String stype = "text";
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

	public final String name;
	public final String index;
	public int width = 100;
	public boolean sortable = false;
	public boolean search = true;
	public SearchOptions searchoptions;
	public SearchRule searchrules;
	public boolean editable;
	public String onclickSubmit = "function() {alert(\"hello world!\");}";
	// for tree view
	public final String title;
	public final boolean isFolder = false;
	public final String path;

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
	}
}