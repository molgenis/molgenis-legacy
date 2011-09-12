package servlets.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class getlogs extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014645654684L;

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		PrintWriter out = response.getWriter();

		out.println("<html><head></head><body>");
		out.println("<div style=\"font-family: Courier, 'Courier New', monospace\">");
		
		try
		{
			Tuple req = new HttpServletRequestTuple(request);

			if (req.getString("file") == null)
			{
				if (req.getInt("job") == null)
				{
					// show root dir listing
					File workingDir = new File(".");
					listDir("", workingDir, out, -1);
				}
				else
				{
					//special: filter root dir with job id
					File workingDir = new File(".");
					listDir("", workingDir, out, req.getInt("job").intValue());
				}
			}
			else
			{
				String filePath = req.getString("file");

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
			if (listJob != -1)
			{
				if (f.getName().startsWith("runmij"+listJob) || f.getName().startsWith("subjob") || f.getName().equals("download.Rout")|| f.getName().equals("ESTtime.Rout"))
				{
					out.println("FILE: <a href=\"/" + getMolgenisVariantID() + "/getlogs?file=" + path + f.getName() + "\">"
							+ f.getName() + "</a><br><br>");
				}
				else if ((f.isDirectory() && f.getName().startsWith("run"+listJob)))
				{
					out.println("DIR > <a href=\"/" + getMolgenisVariantID() + "/getlogs?file=" + path + f.getName()
							+ "/" + "\">" + f.getName() + "</a><br><br>");
				}
			}
			else
			{
				if (f.getName().endsWith(".Rout") || f.getName().endsWith(".R") || f.getName().endsWith(".sh")
						|| f.getName().endsWith(".RData"))
				{
					out.println("FILE: <a href=\"/" + getMolgenisVariantID() + "/getlogs?file=" + path + f.getName() + "\">"
							+ f.getName() + "</a><br><br>");
				}
				else if ((f.isDirectory() && f.getName().startsWith("run")))
				{
					out.println("DIR > <a href=\"/" + getMolgenisVariantID() + "/getlogs?file=" + path + f.getName()
							+ "/" + "\">" + f.getName() + "</a><br><br>");
				}
			}
		}
	}

}
