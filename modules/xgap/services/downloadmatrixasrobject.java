package services;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.ui.ApplicationController;

import plugins.matrix.manager.MatrixManager;

/**
 * Serves static files such as images, css files and javascript from classpath.
 * This is servlet is used when serving from a Jar file in the Mortbay server.
 * Using tomcat the static serving is left to the container.
 */
public class downloadmatrixasrobject implements MolgenisService
{
	private static Logger logger = Logger.getLogger(downloadmatrixasrobject.class);

	private MolgenisContext mc;
	
	public downloadmatrixasrobject(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		// OutputStream out = response.getOutputStream();
		OutputStream out = response.getResponse().getOutputStream();
		PrintStream p = new PrintStream(new BufferedOutputStream(out), false, "UTF8");
		response.getResponse().setStatus(HttpServletResponse.SC_OK);
		
		boolean databaseIsAvailable = false;
		Database db = null;
		
		String content = "";

		try
		{
			db = request.getDatabase();
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
				
				//special exception for filtered content: get matrix instance from memory and do complete handle
				if(request.getString("id").equals("inmemory"))
				{
					ApplicationController molgenis = (ApplicationController) request.getRequest().getSession().getAttribute("application");
					content = ((DataMatrixInstance)molgenis.sessionVariables.get(MatrixManager.SESSION_MATRIX_DATA)).getAsRobject(false);
					response.getResponse().setContentLength(content.length());
					p.print(content);
					p.flush();
					p.close();
					return;
				}
				
				int matrixId = request.getInt("id");
				QueryRule q = new QueryRule("id", Operator.EQUALS, matrixId);
				Data data = db.find(Data.class, q).get(0);
				DataMatrixHandler dmh = new DataMatrixHandler(db);
				DataMatrixInstance instance = dmh.createInstance(data, db);

				if (request.getString("download").equals("all"))
				{
					content +=  instance.getAsRobject(false) ;
				}
				else if (request.getString("download").equals("some"))
				{
					int colOffset = request.getInt("coff");
					int colLimit = request.getInt("clim");
					int rowOffset = request.getInt("roff");
					int rowLimit = request.getInt("rlim");
					content += instance.getSubMatrixByOffset(rowOffset, rowLimit, colOffset, colLimit).getAsRobject(false);	
				}
				else
				{
					content += displayUsage(db);
				}

				logger.info("serving " + request.getRequest().getRequestURI());
			}
			catch (Exception e)
			{
				content += displayUsage(db);
				content += "\n\n";
				content += e.getStackTrace();
			}
		}
		response.getResponse().setContentLength(content.length());
		p.print(content);
		p.flush();
		p.close();
	}

	public String displayUsage(Database db)
	{
		String usage = "Usage:" + "\n\n" + "Full matrix:\n"
				+ "http://localhost:8080/xgap/downloadmatrixasrobject?id=58342&download=all" + "\n\n"
				+ "Only first element of matrix (top left):\n"
				+ "http://localhost:8080/xgap/downloadmatrixasrobject?id=58342&download=some&coff=0&clim=1&roff=0&rlim=1"
				+ "\n\n" + "6 by 15 submatrix with a row offset of 20 and a column offset of 5:\n"
				+ "http://localhost:8080/xgap/downloadmatrixasrobject?id=58342&download=some&coff=5&clim=6&roff=20&rlim=15"
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
