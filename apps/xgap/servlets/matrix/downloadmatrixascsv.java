package servlets.matrix;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import plugins.matrix.manager.Browser;

/**
 * Serves static files such as images, css files and javascript from classpath.
 * This is servlet is used when serving from a Jar file in the Mortbay server.
 * Using tomcat the static serving is left to the container.
 */
public class downloadmatrixascsv extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	private static Logger logger = Logger.getLogger(downloadmatrixascsv.class);

	/**
	 * Get a resource from the jar and copy it the the response.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		// OutputStream out = response.getOutputStream();
		OutputStream out = response.getOutputStream();
		PrintStream p = new PrintStream(new BufferedOutputStream(out), false, "UTF8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		boolean databaseIsAvailable = false;
		Database db = null;
		
		String content = "";

		try
		{
			db = this.getDatabase();
			databaseIsAvailable = true;
		}
		catch (Exception e)
		{
			content += "Database unavailable.";
			content += e.getStackTrace();
		}

		if (databaseIsAvailable)
		{
			try
			{
				Tuple req = new HttpServletRequestTuple(request);
				
				//special exception for filtered content: get matrix instance from memory and do complete handle
				if(req.getString("id").equals("inmemory"))
				{
					content = Browser.inmemory.toString();
					response.setContentLength(content.length());
					p.print(content);
					p.flush();
					p.close();
					return;
				}
				
				int matrixId = req.getInt("id");
				QueryRule q = new QueryRule("id", Operator.EQUALS, matrixId);
				Data data = db.find(Data.class, q).get(0);
				DataMatrixHandler dmh = new DataMatrixHandler(db);
				AbstractDataMatrixInstance<Object> instance = dmh.createInstance(data);

				if (req.getString("download").equals("all"))
				{
					if (req.getString("stream").equals("true"))	{
						//content += instance.toString();
						instance.toPrintStream(p);
						p.close();
						return;
					}
					else if (req.getString("stream").equals("false"))
					{
						content +=  instance.toString() ;
					}
					else
					{
						content += displayUsage(db);
					}
				}
				else if (req.getString("download").equals("some"))
				{
					int colOffset = req.getInt("coff");
					int colLimit = req.getInt("clim");
					int rowOffset = req.getInt("roff");
					int rowLimit = req.getInt("rlim");
					if (req.getString("stream").equals("true"))
					{
						content += instance.getSubMatrixByOffset(rowOffset, rowLimit, colOffset, colLimit).toString();
					}
					else if (req.getString("stream").equals("false"))
					{
						content += instance.getSubMatrixByOffset(rowOffset, rowLimit, colOffset, colLimit).toString();
					}
					else
					{
						content += displayUsage(db);
					}
				}
				else
				{
					content += displayUsage(db);
				}

				logger.info("serving " + request.getRequestURI());
			}
			catch (Exception e)
			{
				content += displayUsage(db);
				content += "\n\n";
				content += e.getStackTrace();
			}
		}
		response.setContentLength(content.length());
		p.print(content);
		p.flush();
		p.close();
	}

	public String displayUsage(Database db)
	{
		String usage = "Usage:" + "\n\n" + "Full matrix, streamed output:\n"
				+ "http://localhost:8080/xgap/downloadmatrixascsv?id=58342&download=all&stream=true" + "\n\n"
				+ "Full matrix, buffered output:\n"
				+ "http://localhost:8080/xgap/downloadmatrixascsv?id=58342&download=all&stream=false" + "\n\n"
				+ "Only first element of matrix (top left):\n"
				+ "http://localhost:8080/xgap/downloadmatrixascsv?id=58342&download=some&coff=0&clim=1&roff=0&rlim=1&stream=true"
				+ "\n\n" + "6 by 15 submatrix with a row offset of 20 and a column offset of 5:\n"
				+ "http://localhost:8080/xgap/downloadmatrixascsv?id=58342&download=some&coff=5&clim=6&roff=20&rlim=15&stream=true"
				+ "\n\n" + "Matrices available in this database:\n\n" + matricesFromDb(db) + "\n";
		return usage;
	}

	public String matricesFromDb(Database db)
	{
		String res = "";
		try
		{
			List<Data> dataList = db.find(Data.class);
			for (Data d : dataList)
			{
				res += d.toString() + "\n";
			}
		}
		catch (Exception e)
		{
			res += "Trying to get matrix information, but database is unavailable.";
			res += e.getStackTrace();
		}
		return res;
	}
}
