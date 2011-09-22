package org.molgenis.framework.ui.html;

public class AutocompleteInput<E> extends HtmlInput<E>
{
	private String entityClass;
	private String entityField;

	public AutocompleteInput(String name, String label, String entityClass, String entityField, String description)
	{
		super(name, label, null, true, false, description);
		this.entityClass = entityClass;
		this.entityField = entityField;
	}
	
	@Override
	public String toHtml()
	{
		return
				"<input id=\"" + this.getId() + "\" type=\"text\"/>" +
				"<script type=\"text/javascript\">\n" +
				"$(function() {\n" +
				"	$(\"#" + this.getId() + "\").autocomplete({\n" +
				"		source: function(req, resp) {\n" +
				"			var url         = \"xref/find?xref_entity=" + this.entityClass + "&xref_field=" + this.entityField + "&xref_label=" + this.entityField + "&xref_label_search=\" + document.getElementById(\"" + this.getId() + "\").value;" +
				"			var suggestions = [];\n" +
				"			successFunction = function(data, textStatus) {\n" +
				"				$.each(data, function(key, val) { suggestions.push(key); });\n" +
				"				return suggestions;\n" +
				"			};\n" +
				"			jQuery.ajax({ url: url, dataType: \"json\", async: false, success: successFunction });\n" +
				"			resp(suggestions);\n" +
				"		},\n" +
				"		select: function(e, ui) { },\n" +
				"		change: function() { }\n" +
				"	});\n" +
				"});\n" +
				"</script>\n";
	}
}
