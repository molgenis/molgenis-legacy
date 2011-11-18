package org.molgenis.framework.server.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.MolgenisServiceAuthenticationHelper;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleWriter;

public class MolgenisUploadService implements MolgenisService
{
	Logger logger = Logger.getLogger(MolgenisDownloadService.class);
	
	/** the name of the datatype input */
	public static String INPUT_DATATYPE = "data_type_input";
	/** the name of the data input */
	public static String INPUT_DATA = "data_input";
	/** boolean indicating file upload */
	public static String INPUT_FILE = "data_file";
	/** the name of the submit button */
	public static String INPUT_SUBMIT = "submit_input";
	/** the action input */
	public static String INPUT_ACTION = "data_action";
	/** indicating wether uploads should return added data */
	public static String INPUT_SILENT = "data_silent";
	
	private MolgenisContext mc;
	
	public MolgenisUploadService(MolgenisContext mc)
	{
		this.mc = mc;
	}
	@Override
	public void handleRequest(MolgenisRequest req, MolgenisResponse res)
			throws ParseException, DatabaseException, IOException
	{
		if(MolgenisServiceAuthenticationHelper.handleAuthentication(req, res) == false)
		{
			return;
		}
		
			Tuple requestTuple = req;
			HttpServletResponse response = res.getResponse();
			
			System.out.println("**** " + req.toString());;
			
			// setup the output-stream
			response.setBufferSize(10000);
			response.setContentType("text/html; charset=UTF-8");

			logger.info("upload service started");
			long start_time = System.currentTimeMillis();

			try
			{
				//Tuple requestTuple = new HttpServletRequestTuple(request);
				//logger.info("parameters: " + requestTuple.getFields());
				// log paramaters
				// for( String s : r.getFields() )
				// {
				// logger.info("'" + s + "'=" + r.getObject(s));
				// }

				PrintWriter out = response.getWriter();
				// EntityReaderFactory readerFactory = new CsvReaderFactory();

				
				
				// if no type selected: show data type choice
				if (requestTuple.getString(INPUT_DATATYPE) == null)
				{
					try
					{
						out.println("<html><body>");
						out.println(MolgenisServiceAuthenticationHelper.displayLogoutForm());		
						out.println("<form method=\"post\" enctype=\"multipart/form-data\">");
						out.println("<h1>Data upload (step 1)</h1>");
						out.println("Choose your data type.");
						out.println("<table><tr><td><label>Data type:</label></td><td><select name=\""
								+ INPUT_DATATYPE + "\">");

						for (Class<? extends Entity> c : req.getDatabase()
								.getEntityClasses())
						{
							// write to screen
							out.println("<option value=\"" + c.getName() + "\">"
									+ c.getName() + "</option>");
						}
						out.println("</select></td></tr>");
						out.println("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\""
								+ INPUT_SUBMIT + "\" value=\"Submit\"></td></tr>");
						out.println("</table></form></body></html>");
					}
					catch (Exception e)
					{
						out.println("Upload failed: " + e.getMessage() + "");
						e.printStackTrace();
						throw e;
					}

				}
				// if no data provided, show csv input form
				else if (requestTuple.getObject(INPUT_DATA) == null
						&& requestTuple.getObject(INPUT_FILE) == null)
				{
					try
					{
						 String clazzName =
						 requestTuple.getString(INPUT_DATATYPE);
						 
						 String simpleEntityName = clazzName.substring(clazzName.lastIndexOf('.')+1);
						 
						 Class<? extends Entity> entityClass = req.getDatabase().getClassForName(simpleEntityName);
						 Entity template = entityClass.newInstance();
						
						 out.println("<html><body><form method=\"post\" enctype=\"multipart/form-data\">");
						 out.println("<h1>Data upload (step 2)</h1>");
						 out.println("Enter your data as CSV.");
						 
						 //FIXME: where is the CSV input form?
						 
//						 out
//						 .println("<table><tr><td><label>Data type:</label></td><td><select name=\""
//						 + INPUT_DATATYPE + "\">");
//						
//						 for (Class<? extends Entity> c : req.getDatabase().getEntityClasses())
//						 {
//						 // write to screen
//						 out.println("<option value=\"" + c.getName() + "\">"
//						 + c.getName() + "</option>");
//						 }
//						 out.println("</select></td></tr>");
//						 out
//						 .println("<tr><td></td><td><input type=\"submit\" name=\""
//						 + INPUT_SUBMIT
//						 + "\" value=\"Submit\"></td></tr>");
//						 out.println("</table></form></body></html>");
					}
					catch (Exception e)
					{
						out.println("Upload failed: " + e.getMessage() + "");
						e.printStackTrace();
						throw e;
					}
				}
				// process request
				else
				{
					NumberFormat formatter = NumberFormat.getInstance(Locale.US);
					logger.info("processing add/update/delete");
					String action = null; // ADD, UPDATE, REMOVE
					Class<? extends Entity> entityClass = null;

					try
					{
						String clazzName = requestTuple.getString(INPUT_DATATYPE);
						entityClass = req.getDatabase().getClassForName(clazzName);

						// get the constants
						Tuple constants = new SimpleTuple();
						for (String column : requestTuple.getFields())
						{
							if (!column.equals(INPUT_DATATYPE)
									&& !column.equals(INPUT_DATA)
									&& !column.equals(INPUT_ACTION)
									&& !column.equals(INPUT_SUBMIT)
									&& !requestTuple.getString(column).equals(""))
							{
								constants.set(column,
										requestTuple.getObject(column));
							}
						}
						action = requestTuple.getString(INPUT_ACTION);
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
							TupleWriter writer = new CsvWriter(new PrintWriter(
									new BufferedWriter(new FileWriter(temp))));
							if (requestTuple.getObject(INPUT_SILENT) != null
									&& requestTuple.getBool(INPUT_SILENT) == true)
							{
								writer.close();
								writer = null;
							}
							// List entities = db.toList(entityClass, new
							// CsvStringReader(r.getString(INPUT_DATA));

							if (requestTuple.getObject(INPUT_DATA) != null)
							{
								logger.info("processing textarea upload...");
								nRowsChanged = db.add(entityClass, new CsvStringReader(requestTuple.getString(INPUT_DATA)), writer);
							}
							else if (requestTuple.getObject(INPUT_FILE) != null)
							{
								logger.info("processing file upload...");
								nRowsChanged = db.add(
										entityClass,
										new CsvFileReader(requestTuple
												.getFile(INPUT_FILE)), writer);
							}
							else
							{
								logger.error("no input data or input file provided.");
								out.print("ERROR: no input data or input file provided.");
							}
							out.print("Uploaded " + formatter.format(nRowsChanged)
									+ " rows of " + entityClass.getCanonicalName()
									+ "\n");

							if (writer != null) writer.close();
							BufferedReader reader = new BufferedReader(
									new FileReader(temp));
							String line = null;
							while ((line = reader.readLine()) != null)
							{
								out.println(line);
							}
							temp.delete();
						}
						else if (action.equals("UPDATE"))
						{
							if (requestTuple.getObject(INPUT_DATA) != null)
							{
								nRowsChanged = db.update(
										entityClass,
										new CsvStringReader(requestTuple
												.getString(INPUT_DATA)));
								out.print("Updated "
										+ formatter.format(nRowsChanged)
										+ " rows of "
										+ entityClass.getCanonicalName() + "\n");
							}
							else if (requestTuple.getObject(INPUT_FILE) != null)
							{
								nRowsChanged = db.update(
										entityClass,
										new CsvFileReader(requestTuple
												.getFile(INPUT_FILE)));
								out.print("Updated "
										+ formatter.format(nRowsChanged)
										+ " rows of "
										+ entityClass.getCanonicalName() + "\n");
							}
						}
						else if (action.equals("REMOVE"))
						{
							if (requestTuple.getObject(INPUT_DATA) != null)
							{
								nRowsChanged = db.remove(
										entityClass,
										new CsvStringReader(requestTuple
												.getString(INPUT_DATA)));
								out.print("Removed "
										+ formatter.format(nRowsChanged)
										+ " rows of "
										+ entityClass.getCanonicalName() + "\n");
							}
							else if (requestTuple.getObject(INPUT_FILE) != null)
							{
								nRowsChanged = db.remove(
										entityClass,
										new CsvFileReader(requestTuple
												.getFile(INPUT_FILE)));
								out.print("Removed "
										+ formatter.format(nRowsChanged)
										+ " rows of "
										+ entityClass.getCanonicalName() + "\n");
							}
						}
						else
						{
							throw new Exception("Unknown action " + action);
						}
					}
					catch (Exception e)
					{
						out.print("Failed to " + action + " "
								+ entityClass.getName() + ": " + e.getMessage()
								+ "");
						e.printStackTrace();
						throw e;
					}

				}

				out.close();
			}
			catch (Exception e)
			{
				logger.error(e);
				e.printStackTrace();
			}
			logger.info("servlet took: "
					+ (System.currentTimeMillis() - start_time) + " ms");
			logger.info("------------");
			
	}

}
