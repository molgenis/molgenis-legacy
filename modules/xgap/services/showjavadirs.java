package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import matrix.general.DataMatrixHandler;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;

public class showjavadirs implements MolgenisService
{
	//TODO: Danny: unused, but i guess we do want to use it
	//private static Logger logger = Logger.getLogger(showjavadirs.class);

	private DataMatrixHandler dmh;
	
	private MolgenisContext mc;
	
	public showjavadirs(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		PrintWriter out = response.getResponse().getWriter();

		try
		{
			out.println("java.io.tmpdir = "+System.getProperty("java.io.tmpdir"));
			out.println("user.dir = " + System.getProperty("user.dir"));
		}
		catch (Exception e)
		{
			e.printStackTrace(out);
		}
		finally
		{
			out.close();
		}
	}
}

