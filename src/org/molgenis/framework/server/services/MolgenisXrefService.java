package org.molgenis.framework.server.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.ui.ApplicationController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;

public class MolgenisXrefService implements MolgenisService
{
	Logger logger = Logger.getLogger(MolgenisXrefService.class);

	private MolgenisContext mc;

	public MolgenisXrefService(MolgenisContext mc)
	{
		this.mc = mc;
	}

	/**
	 * Handle use of the XREF API.
	 * 
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse res)
			throws ParseException, DatabaseException, IOException
	{
		HttpServletRequest request = req.getRequest();
		HttpServletResponse response = res.getResponse();

		try
		{
			response.setHeader("Cache-Control", "max-age=0"); // allow no client
			response.setContentType("application/json");
			// side caching
			// .../xref/find?xref_entity=xxx&xref_field=yyyy&xref_label=zzzz&
			// filter=aaa
			// .../xref/find?xref_entity=xgap.data.types.Marker&xref_field=id&
			// xref_label=name&xref_filter=PV

			// alternatief => here the 'field' is the xref input itself
			// .../xref/find?entity=xxx&field=zzz&filter=aaaa
			
			logger.debug("handling XREF request " + req);

			Class<? extends Entity> xref_entity = getClassForName(req
					.getString("xref_entity"));
			String xref_field = req.getString("xref_field");
			// get the xref labels from the string
			List<String> xref_labels = new ArrayList<String>();
			for (String label : req.getString("xref_label").split(","))
			{
				xref_labels.add(label.toString());
			}

			// List<QueryRule> xref_filters =
			// QueryRuleUtil.fromRESTstring(req.getString("xref_filters"));
			String xref_label_search = req.getString("xref_label_search");

			logger.debug(xref_entity + " " + xref_field + " " + xref_labels
					+ " " + xref_label_search);
			// List<String> queryFields = new ArrayList<String>();
			// queryFields.add(xref_field);
			// for (String xref_label : xref_labels)
			// {
			// queryFields.add(xref_label);
			// }

			// create a query on xref_entity
			Database db = req.getDatabase();

			// get the user interface and find the login
			HttpSession session = request.getSession();
			ScreenController<?> molgenis = (ApplicationController) session
					.getAttribute("application");
			// Login login = molgenis.getApplicationController().getLogin();
			// db.setLogin(login);
			Query<?> q = db.query(xref_entity);

			// create a CustomQuery
			// JoinQuery q = getDatabase().query(queryFields);
			// //q.addRules(xref_filters);
			if (xref_label_search != null && xref_label_search != "")
			{
				for (String xref_label : xref_labels)
				{
					q.like(xref_label, "%" + xref_label_search + "%");
					q.or();
					q.sortASC(xref_label);
				}
			}
			q.limit(100);

			List<? extends Entity> result = q.find();

			// transform in JSON (JavaScript Object Notation

			String json = "{";
			for (int i = 0; i < result.size(); i++)
			{
				// logger.debug("using: " + result.get(i));
				if (i > 0)
				{
					json += ",";

				}

				// write the xref key as set in xref_field
				json += "\"" + result.get(i).get(xref_field).toString()
						+ "\":\"";

				// write the label(s) as set in xref_label
				for (int j = 0; j < xref_labels.size(); j++)
				{
					// hack
					if (j > 0) json += "|";
					json += StringEscapeUtils.escapeJavaScript(result.get(i)
							.get(xref_labels.get(j)).toString());
				}
				json += "\"";
				// logger.debug(result.get(i).get(xref_field) + ":\""
				// + result.get(i).get(xref_label) + "\"");
			}
			json += "}";

			logger.debug(json);

			// write out
			PrintWriter out = response.getWriter();
			out.print(json);
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}
	
	private Class<? extends Entity> getClassForName(String entityName)
			throws ClassNotFoundException
	{
		return (Class<? extends Entity>) Class.forName(entityName);
	}

}
