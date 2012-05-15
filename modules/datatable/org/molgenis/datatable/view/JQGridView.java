package org.molgenis.datatable.view;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.ui.html.HtmlWidget;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class JQGridView extends HtmlWidget implements MolgenisService
{
	private final TupleTable table;

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
	
			args.put("tableId", getId());
			args.put("columns", table.getColumns());
			args.put("dataSourceUrl", "jqGridService.do");
			args.put("sortName", table.getColumns().get(0));
			
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

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{
		
	}
}
