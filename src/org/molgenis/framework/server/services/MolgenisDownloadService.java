package org.molgenis.framework.server.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.MolgenisServiceAuthenticationHelper;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.TupleWriter;


public class MolgenisDownloadService implements MolgenisService
{
	Logger logger = Logger.getLogger(MolgenisDownloadService.class);
	
	private MolgenisContext mc;
	
	public MolgenisDownloadService(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	/**
	 * Handle use of the download API.
	 * 
	 * TODO: this method is horrible and should be properly refactored,
	 * documented and tested!
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse res)
			throws ParseException, DatabaseException, IOException
	{
		logger.info("starting download " + req.getRequest().getPathInfo());
		long start_time = System.currentTimeMillis();
	
		res.getResponse().setBufferSize(10000);
		res.getResponse().setContentType("text/html; charset=UTF-8");
		
		PrintWriter out = res.getResponse().getWriter();
		Database db = req.getDatabase();
		
		try
		{

			boolean showTheApi = MolgenisServiceAuthenticationHelper.handleAuthentication(req, out);
			
			if (showTheApi)
			{
	
				String entityName = req.getRequest().getPathInfo().substring(req.getServicePath().length());
	
				if (entityName.startsWith("/"))
				{
					entityName = entityName.substring(1);
				}
	
				if (entityName.equals(""))
				{
					out.println("<html><body>");
					if(req.getDatabase().getSecurity().isAuthenticated())
					{
						out.println(MolgenisServiceAuthenticationHelper.displayLogoutForm());
					}
					showAvailableDownloads(out, db, req);
					out.println("</body></html>");
				}
				else if (req.getRequest().getQueryString() != null
						&& req.getRequest().getQueryString().equals("__showQueryDialogue=true"))
				{
					out.println("<html><body>");
					if(req.getDatabase().getSecurity().isAuthenticated())
					{
						out.println(MolgenisServiceAuthenticationHelper.displayLogoutForm());
					}
					showFilterableDownload(out, entityName);
					out.println("</body></html>");
				}
				else
				{
					executeQuery(out, req, db, entityName);
				}
			}
		}
		catch (Exception e)
		{
			out.println(e.getMessage());
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		finally
		{
			out.flush();
			out.close();
			db.close();
		}
		
		logger.info("servlet took: "
				+ (System.currentTimeMillis() - start_time));
		logger.info("------------");


	}
	
	private void showFilterableDownload(PrintWriter out, String entityName) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		//System.out.println("show 'set filters' dialogue");
		out.println("<form>");
		out.println("You choose to download '"
				+ entityName //FIXME: bad, hardcoded location!
				+ "' data. (<a href=\"../find\">back</a>)<br><br> Here you can set filters before downloading:<br>");
		out.println("<table>");
		for (String field : ((Entity) Class.forName(entityName)
				.newInstance()).getFields())
		{
			out.println("<tr><td>" + field
					+ "</td><td>=</td><td><input name=\"" + field
					+ "\" type=\"text\"></td><tr/>");
		}
		out.println("</table>");
		out.println("<SCRIPT>"
				+ "function createFilterURL(fields)"
				+ "{	"
				+ "	var query = '';"
				+ "	var count = 0;"
				+ "	for (i = 0; i < fields.length; i++) "
				+ "	{"
				+ "		if (fields[i].value != '' && fields[i].name != '__submitbutton')"
				+ "		{"
				+ "			if(count > 0)"
				+ "				query +='&';"
				+ "			query += fields[i].name + '=' + fields[i].value;"
				+ "			count++;" + "		}" + "	}" + "	return query"
				+ "}" + "</SCRIPT>");

		out.println("<input name=\"__submitbutton\" type=\"submit\" value=\"Download tab delimited file\" onclick=\""
				+ "window.location.href = 'http://' + window.location.host + window.location.pathname + '?'+createFilterURL(this.form.elements);\"><br><br>");
		out.println("TIP: notice how the url is bookmarkeable for future downloads!<br>");
		out.println("TIP: click 'save as...' and name it as '.txt' file.");
		out.println("</form>");
	}
	
	private void showAvailableDownloads(PrintWriter out, Database db, MolgenisRequest req) throws DatabaseException
	{
		//print message to indicate your are anonymous
		if(!db.getSecurity().isAuthenticated())
		{
			out.println("You are currently browsing as anonymous.<br>");
		}
		
		out.println("You can download these data:<br>");
		out.println("<table>");
		
		for (String className : db.getEntityNames())
		{
			String simpleEntityName = className.substring(className.lastIndexOf('.')+1);
			Class<? extends Entity> klazz = db.getClassForName(simpleEntityName);
			
			//hide entities without read permission
			if(db.getSecurity().canRead(klazz))
			{
				//hide auth entities
				if(!(simpleEntityName.equals("MolgenisGroup") || simpleEntityName.equals("MolgenisPermission") || simpleEntityName.equals("MolgenisRole") || simpleEntityName.equals("MolgenisRoleGroupLink") || simpleEntityName.equals("MolgenisUser")))
				{
					out.println("<tr>");
					out.println("<td><a href=\"" + (req.getRequest().getPathInfo().endsWith("/") ? "" :  "/" + mc.getVariant() + req.getServicePath() + "/") + className + "\">"
						+ className + "</a></td>");
					out.println("<td><a href=\"" + (req.getRequest().getPathInfo().endsWith("/") ? "" : "/" + mc.getVariant() + req.getServicePath() + "/") + className
						+ "?__showQueryDialogue=true\">"
						+ "filter" + "</a></td>");
					out.println("</tr>");
				}
				
				
			}
		}
		out.println("</table>");
	}

	private List<QueryRule> createQueryRules(MolgenisRequest req) throws Exception
	{
		List<QueryRule> rulesList = new ArrayList<QueryRule>();
		
		// use get
		if (req.getRequest().getQueryString() != null)
		{
			//System.out.println("handle find query via http-get: " + request.getQueryString());
			String[] ruleStrings = req.getRequest().getQueryString().split("&");

			for (String rule : ruleStrings)
			{
				String[] ruleElements = rule.split("=");

				if (ruleElements.length != 2)
				{
					//this is OK: just a not-filled-in queryrule
					//throw new Exception("cannot understand querystring " + rule + ", does not have 2 elements");
				}
				else if (ruleElements[1].startsWith("["))
				{
					ruleElements[1] = ruleElements[1].replace("%20",
							" ");
					String[] values = ruleElements[1].substring(1,
							ruleElements[1].indexOf("]")).split(",");
					rulesList.add(new QueryRule(ruleElements[0],
							QueryRule.Operator.IN, values));
				}
				else
				{
					if (ruleElements[1] != ""
							&& !"__submitbutton"
									.equals(ruleElements[0])) rulesList
							.add(new QueryRule(ruleElements[0],
									QueryRule.Operator.EQUALS,
									ruleElements[1]));
				}
			}
		}
		// use post
		else
		{
			//System.out.println("handle find query via http-post with parameters: " + req.getFields());
			for (String name : req.getFields())
			{
				if (req.getString(name).startsWith("["))
				{
					String[] values = req
							.getString(name)
							.substring(
									1,
									req.getString(name)
											.indexOf("]")).split(",");
					rulesList.add(new QueryRule(name,
							QueryRule.Operator.IN, values));
				}
				else
				{
					rulesList.add(new QueryRule(name,
							QueryRule.Operator.EQUALS, req
									.getString(name)));
				}
			}
		}
		return rulesList;
	}
	
	private void executeQuery(PrintWriter out, MolgenisRequest req, Database db, String entityName) throws Exception
	{
		// create query rules
		List<QueryRule> rulesList = createQueryRules(req);

		// execute query
		TupleWriter writer = new CsvWriter(out);
		
		String simpleEntityName = entityName.substring(entityName.lastIndexOf('.')+1);
		
		Class<? extends Entity> klazz = db.getClassForName(simpleEntityName);
		
		db.find(klazz, writer,
				rulesList.toArray(new QueryRule[rulesList.size()]));
	}

}
