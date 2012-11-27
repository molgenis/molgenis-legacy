package org.molgenis.framework.server.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.AuthStatus;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.MolgenisServiceAuthenticationHelper;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleWriter;

public class MolgenisUploadService implements MolgenisService
{
	private static final Logger logger = Logger.getLogger(MolgenisDownloadService.class);

	/** the name of the datatype input */
	public static final String INPUT_DATATYPE = "data_type_input";
	/** the name of the data input */
	public static final String INPUT_DATA = "data_input";
	/** boolean indicating file upload */
	public static final String INPUT_FILE = "data_file";
	/** the name of the submit button */
	public static final String INPUT_SUBMIT = "submit_input";
	/** the action input */
	public static final String INPUT_ACTION = "data_action";
	/** indicating wether uploads should return added data */
	public static final String INPUT_SILENT = "data_silent";

	public MolgenisUploadService(MolgenisContext mc)
	{
	}

	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse res) throws ParseException, DatabaseException,
			IOException
	{
		logger.info("starting upload " + req.getRequest().getPathInfo());
		long start_time = System.currentTimeMillis();

		res.getResponse().setBufferSize(10000);
		res.getResponse().setContentType("text/html; charset=UTF-8");

		PrintWriter out = res.getResponse().getWriter();
		Database db = req.getDatabase();

		try
		{

			AuthStatus authStatus = MolgenisServiceAuthenticationHelper.handleAuthentication(req, out);

			if (!authStatus.isShowApi())
			{
				out.println("<html><body>");
				out.println(authStatus.getPrintMe());
				out.println("</body></html>");
			}
			else
			{
				// if no type selected: show data type choice
				if (req.getString(INPUT_DATATYPE) == null)
				{
					out.println("<html><body>");
					authStatus.getPrintMe();
					if (req.getDatabase().getLogin().isAuthenticated())
					{
						out.println(MolgenisServiceAuthenticationHelper.displayLogoutForm());
					}
					showDataTypeChoice(out, req);
					out.println("</body></html>");
				}
				// if no data provided, show csv input form
				else if (req.getObject(INPUT_DATA) == null && req.getObject(INPUT_FILE) == null)
				{
					out.println("<html><body>");
					authStatus.getPrintMe();
					if (req.getDatabase().getLogin().isAuthenticated())
					{
						out.println(MolgenisServiceAuthenticationHelper.displayLogoutForm());
					}
					showCsvInputForm(out, req);
					out.println("</body></html>");
				}
				// process request
				else
				{
					processRequest(req, out);
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

		logger.info("servlet took: " + (System.currentTimeMillis() - start_time) + " ms");
		logger.info("------------");

	}

	private void showDataTypeChoice(PrintWriter out, MolgenisRequest req)
	{
		out.println("<form method=\"post\" enctype=\"multipart/form-data\">");
		out.println("<h1>Data upload (step 1)</h1>");
		out.println("Choose your data type.");
		out.println("<table><tr><td><label>Data type:</label></td><td><select name=\"" + INPUT_DATATYPE + "\">");

		for (Class<? extends Entity> c : req.getDatabase().getEntityClasses())
		{
			// write to screen
			out.println("<option value=\"" + c.getName() + "\">" + c.getName() + "</option>");
		}
		out.println("</select></td></tr>");
		out.println("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"" + INPUT_SUBMIT
				+ "\" value=\"Submit\"></td></tr>");
		out.println("</table></form>");
	}

	private void showCsvInputForm(PrintWriter out, MolgenisRequest req) throws InstantiationException,
			IllegalAccessException
	{
		// String clazzName =
		req.getString(INPUT_DATATYPE);

		// String simpleEntityName =
		// clazzName.substring(clazzName.lastIndexOf('.')+1);

		// Class<? extends Entity> entityClass =
		// req.getDatabase().getClassForName(simpleEntityName);
		// Entity template = entityClass.newInstance();

		out.println("<html><body><form method=\"post\" enctype=\"multipart/form-data\">");
		out.println("<h1>Data upload (step 2)</h1>");
		out.println("Enter your data as CSV.");

		// FIXME: where is the CSV input form?

		// out
		// .println("<table><tr><td><label>Data type:</label></td><td><select name=\""
		// + INPUT_DATATYPE + "\">");
		//
		// for (Class<? extends Entity> c :
		// req.getDatabase().getEntityClasses())
		// {
		// // write to screen
		// out.println("<option value=\"" + c.getName() + "\">"
		// + c.getName() + "</option>");
		// }
		// out.println("</select></td></tr>");
		// out
		// .println("<tr><td></td><td><input type=\"submit\" name=\""
		// + INPUT_SUBMIT
		// + "\" value=\"Submit\"></td></tr>");
		// out.println("</table></form></body></html>");

	}

	private void processRequest(MolgenisRequest req, PrintWriter out) throws Exception
	{
		NumberFormat formatter = NumberFormat.getInstance(Locale.US);
		logger.info("processing add/update/delete");
		String action = null; // ADD, UPDATE, REMOVE
		Class<? extends Entity> entityClass = null;

		String clazzName = req.getString(INPUT_DATATYPE);
		String simpleEntityName = clazzName.substring(clazzName.lastIndexOf('.') + 1);
		entityClass = req.getDatabase().getClassForName(simpleEntityName);

		// get the constants
		Tuple constants = new SimpleTuple();
		for (String column : req.getFields())
		{
			if (!column.equals(INPUT_DATATYPE) && !column.equals(INPUT_DATA) && !column.equals(INPUT_ACTION)
					&& !column.equals(INPUT_SUBMIT) && !req.getString(column).equals(""))
			{
				constants.set(column, req.getObject(column));
			}
		}
		action = req.getString(INPUT_ACTION);
		// //out.println("Defaults: " + constants);

		// create a database
		Database db = req.getDatabase();

		// write to the database
		// logger.info("Adding parsed data to database:\n" +
		// r.getString(INPUT_DATA));

		int nRowsChanged = 0;
		if (action.equals("ADD"))
		{
			File temp = File.createTempFile("molgenis", "tab");
			TupleWriter writer = new CsvWriter(new FileWriter(temp));
			if (req.getObject(INPUT_SILENT) != null && req.getBool(INPUT_SILENT) == true)
			{
				writer.close();
				writer = null;
			}
			// List entities = db.toList(entityClass, new
			// CsvStringReader(r.getString(INPUT_DATA));

			if (req.getObject(INPUT_DATA) != null)
			{
				logger.info("processing textarea upload...");
				nRowsChanged = db.add(entityClass, new CsvStringReader(req.getString(INPUT_DATA)), writer);
			}
			else if (req.getObject(INPUT_FILE) != null)
			{
				logger.info("processing file upload...");
				nRowsChanged = db.add(entityClass, new CsvFileReader(req.getFile(INPUT_FILE)), writer);
			}
			else
			{
				logger.error("no input data or input file provided.");
				out.print("ERROR: no input data or input file provided.");
			}
			out.print("Uploaded " + formatter.format(nRowsChanged) + " rows of " + entityClass.getCanonicalName()
					+ "\n");

			if (writer != null) writer.close();
			BufferedReader reader = new BufferedReader(new FileReader(temp));
			try
			{
				String line = null;
				while ((line = reader.readLine()) != null)
				{
					out.println(line);
				}
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
			temp.delete();
		}
		else if (action.equals("UPDATE"))
		{
			if (req.getObject(INPUT_DATA) != null)
			{
				nRowsChanged = db.update(entityClass, new CsvStringReader(req.getString(INPUT_DATA)));
				out.print("Updated " + formatter.format(nRowsChanged) + " rows of " + entityClass.getCanonicalName()
						+ "\n");
			}
			else if (req.getObject(INPUT_FILE) != null)
			{
				nRowsChanged = db.update(entityClass, new CsvFileReader(req.getFile(INPUT_FILE)));
				out.print("Updated " + formatter.format(nRowsChanged) + " rows of " + entityClass.getCanonicalName()
						+ "\n");
			}
		}
		else if (action.equals("REMOVE"))
		{
			if (req.getObject(INPUT_DATA) != null)
			{
				nRowsChanged = db.remove(entityClass, new CsvStringReader(req.getString(INPUT_DATA)));
				out.print("Removed " + formatter.format(nRowsChanged) + " rows of " + entityClass.getCanonicalName()
						+ "\n");
			}
			else if (req.getObject(INPUT_FILE) != null)
			{
				nRowsChanged = db.remove(entityClass, new CsvFileReader(req.getFile(INPUT_FILE)));
				out.print("Removed " + formatter.format(nRowsChanged) + " rows of " + entityClass.getCanonicalName()
						+ "\n");
			}
		}
		else
		{
			throw new Exception("Unknown action " + action);
		}
	}

}
