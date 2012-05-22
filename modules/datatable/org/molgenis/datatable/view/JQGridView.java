package org.molgenis.datatable.view;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.model.elements.Field;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class JQGridView extends HtmlWidget
{
	public JQGridView(String name)
	{
		super(name);
	}




	private TupleTable table;

	public JQGridView(String id, TupleTable table)
	{
		super(id);
		this.table = table;
	}
	

	
	
	@Override
	public String toHtml() {
		try
		{
			final Map<String, Object> args = new HashMap<String, Object>();
			final List<JQGridColumn> gridColumns = new ArrayList<JQGridColumn>();
			for (Field field : table.getColumns())
			{
				gridColumns.add(new JQGridColumn(field));				
			}
			
			args.put("tableId", getId());
			args.put("columns", gridColumns);
			args.put("dataSourceUrl", "jqGridService.do");
			args.put("sortName", table.getColumns().get(0).getName());
			
			final Configuration cfg = new Configuration();
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setClassForTemplateLoading(JQGridView.class, "");
			final Template template = cfg.getTemplate(JQGridView.class.getSimpleName() + ".ftl");
			final Writer out = new StringWriter();
			template.process(args, out);
			out.flush();  
			return out.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
