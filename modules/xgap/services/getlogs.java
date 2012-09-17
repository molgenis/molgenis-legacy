package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import app.servlet.MolgenisServlet;

public class getlogs implements MolgenisService
{

	private MolgenisContext mc;
	
	public getlogs(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		PrintWriter out = response.getResponse().getWriter();
		response.getResponse().setContentType("text/html");

		out.println("<html><head></head><body>");
		out.println("<div style=\"font-family: Courier, 'Courier New', monospace\">");
		
		try
		{

			if (request.getString("file") == null)
			{
				if (request.getInt("job") == null)
				{
					// show root dir listing
					File workingDir = new File(".");
					listDir("", workingDir, out, -1);
				}
				else
				{
					//special: filter root dir with job id
					File workingDir = new File(".");
					listDir("", workingDir, out, request.getInt("job").intValue());
				}
			}
			else
			{
				String filePath = request.getString("file");

				if (filePath.endsWith("/"))
				{
					// show dir listing
					String path = filePath.substring(0, (filePath.lastIndexOf('/') + 1));
					File listDir = new File(filePath.substring(0, filePath.length() - 1));
					listDir(path, listDir, out, -1);
				}
				else
				{
					// show file
					BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
					String sCurrentLine;
					while ((sCurrentLine = br.readLine()) != null)
					{
						out.println(sCurrentLine + "<br>");
					}
				}
			}
			
			out.println("</div>");
			out.println("</body></html>");
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

	public void listDir(String path, File dir, PrintWriter out, int listJob)
	{
		for (File f : dir.listFiles())
		{
			Date lastMod = new Date(f.lastModified());
			if (listJob != -1)
			{
				if (f.getName().startsWith("runmij"+listJob) || f.getName().startsWith("subjob") || f.getName().equals("download.Rout")|| f.getName().equals("ESTtime.Rout"))
				{
					out.println("file: <a href=\"/" + MolgenisServlet.getMolgenisVariantID() + "/getlogs?file=" + path + f.getName() + "\">"
							+ f.getName() + "</a> / size (bytes): " + f.length() + " / lastmod: "+lastMod+"<br><br>");
				}
				else if ((f.isDirectory() && f.getName().startsWith("run"+listJob)))
				{
					out.println("dir > <a href=\"/" + MolgenisServlet.getMolgenisVariantID() + "/getlogs?file=" + path + f.getName()
							+ "/" + "\">" + f.getName() + "</a> / size (bytes): " + f.length() + " / lastmod: "+lastMod+"<br><br>");
				}
			}
			else
			{
				if (f.getName().endsWith(".Rout") || f.getName().endsWith(".R") || f.getName().endsWith(".sh")
						|| f.getName().endsWith(".RData"))
				{
					out.println("file: <a href=\"/" + MolgenisServlet.getMolgenisVariantID() + "/getlogs?file=" + path + f.getName() + "\">"
							+ f.getName() + "</a> / size (bytes): " + f.length() + " / lastmod: "+lastMod+"<br><br>");
				}
				else if ((f.isDirectory() && f.getName().startsWith("run")))
				{
					out.println("dir > <a href=\"/" + MolgenisServlet.getMolgenisVariantID() + "/getlogs?file=" + path + f.getName()
							+ "/" + "\">" + f.getName() + "</a> / size (bytes): " + f.length() + " / lastmod: "+lastMod+"<br><br>");
				}
			}
		}
	}

}
