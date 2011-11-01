package org.molgenis.framework.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleWriter;


public class MolgenisDownloadService implements MolgenisService
{
	Logger logger = Logger.getLogger(MolgenisDownloadService.class);
	
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
			HttpServletRequest request = req.getRequest();
			HttpServletResponse response = res.getResponse();
			
			// setup the output-stream
			response.setBufferSize(10000);
			response.setContentType("text/html; charset=UTF-8");
			logger.info("starting download " + request.getPathInfo());
			long start_time = System.currentTimeMillis();

			// HttpSession session = request.getSession();
			PrintWriter out = null;
			try
			{
				out = response.getWriter();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			Database db = null;
			try
			{
				db = req.getDatabase();
				//db.setLogin(new org.molgenis.framework.security.SimpleLogin(db));
				try
				{

					// check whether a class is chosen
					if (request.getPathInfo() == null
							|| request.getPathInfo().equals("/find"))
					{
						logger.debug("show 'choose entity' dialogue");
						out.println("<html><body>");
						out.println("You can download data:<br>");

						for (String className : db.getEntityNames())
						{

							if (request.getPathInfo() == null) out
									.println("<a href=\"find/" + className
											+ "?__showQueryDialogue=true\">"
											+ className + "</a><br>");
							else
								out.println("<a href=\"" + className + "\">"
										+ className + "</a><br>");
						}

						out.println("</body></html>");

						logger.debug("done");
						return;
					}
					String entityName = request.getPathInfo().substring(1);

					// check whether a querystring has to build
					if (request.getQueryString() != null
							&& request.getQueryString().equals(
									"__showQueryDialogue=true"))
					{
						logger.debug("show 'set filters' dialogue");
						out.println("<html><body><form>");
						out.println("You choose to download '"
								+ entityName
								+ "' data. (<a href=\"../api/find\">back</a>)<br><br> Here you can have to set at least one filter:<br>");
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

						// with security break out.println( "<input
						// name=\"__submitbutton\" type=\"submit\" value=\"download
						// tab delimited file\"
						// onclick=\"if(createFilterURL(this.form.elements) != '')
						// {window.location.href = 'http://' + window.location.host
						// + window.location.pathname +
						// '?'+createFilterURL(this.form.elements); }return
						// false;\"><br>" );
						out.println("<input name=\"__submitbutton\" type=\"submit\" value=\"download tab delimited file\" onclick=\""
								+ "window.location.href = 'http://' + window.location.host + window.location.pathname + '?'+createFilterURL(this.form.elements);\"><br>");
						out.println("TIP: notice how the url is bookmarkeable for future downloads!");
						out.println("TIP: click 'save as...' and name it as '.txt' file.");
						out.println("</form></body></html>");
						return;
					}

					// create query rules
					List<QueryRule> rulesList = new ArrayList<QueryRule>();

					// use get
					if (request.getQueryString() != null)
					{
						logger.debug("handle find query via http-get: "
								+ request.getQueryString());
						String[] ruleStrings = request.getQueryString().split("&");

						for (String rule : ruleStrings)
						{
							String[] ruleElements = rule.split("=");

							if (ruleElements.length != 2)
							{
								// throw new Exception( "cannot understand
								// querystring " + rule );
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
						Tuple requestTuple = new HttpServletRequestTuple(request);
						logger.debug("handle find query via http-post with parameters: "
								+ requestTuple.getFields());
						for (String name : requestTuple.getFields())
						{
							if (requestTuple.getString(name).startsWith("["))
							{
								String[] values = requestTuple
										.getString(name)
										.substring(
												1,
												requestTuple.getString(name)
														.indexOf("]")).split(",");
								rulesList.add(new QueryRule(name,
										QueryRule.Operator.IN, values));
							}
							else
							{
								rulesList.add(new QueryRule(name,
										QueryRule.Operator.EQUALS, requestTuple
												.getString(name)));
							}
						}
					}

					// TODO: when is this reached??

					// execute query
					TupleWriter writer = new CsvWriter(out);
					// CsvWriter writer = new CsvFileWriter( new
					// File("c:/testout.txt") );
					db.find(db.getClassForName(entityName), writer,
							rulesList.toArray(new QueryRule[rulesList.size()]));
				}
				catch (Exception e)
				{
					out.println("<div class='errormessage'>" + e.getMessage()
							+ "</div>");
					// e.printStackTrace();
					// throw e;
				}
				finally
				{
					db.close();
				}

				out.close();
			}
			catch (Exception e)
			{
				out.println("<div class='errormessage'>No database available to query: "
						+ e.getMessage() + "</div>");
				logger.error(e.getMessage());
			}
			logger.info("servlet took: "
					+ (System.currentTimeMillis() - start_time));
			logger.info("------------");
	}

}
