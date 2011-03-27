package servlets.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import filehandling.generic.PerformUpload;


import app.JDBCDatabase;

public class uploadfile extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	private static Logger logger = Logger.getLogger(uploadfile.class);

	/**
	 * File upload service. Callable by Curl, RCurl, and other post services.
	 * Currently used to images, expandable to all other files.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		try
		{
			JDBCDatabase db = (JDBCDatabase) getDatabase();
			Tuple req = new HttpServletRequestTuple(request);

			String fileName = req.getString("name"); // the 'real' file name
			String fileType = req.getString("type"); // file type, must correspond to a subclass of MolgenisFile
			File fileContent = req.getFile("file"); // has a tmp name, so content only
			
			HashMap<String, String> extraFields = new HashMap<String, String>();
		
			for(int cIndex = 0; cIndex < req.size(); cIndex++){
				String colName = req.getColName(cIndex);
				if(colName.equals("name") || colName.equals("type") || colName.equals("file")){
					//already handled
				}else{
					extraFields.put(colName, req.getString(colName));
					System.out.println("uploadfile -> adding extra field '" + colName + "' with value '" + req.getSet(colName) +"'");
				}
			}
	
			PerformUpload.doUpload(db, true, fileName, fileType, fileContent, extraFields);

			out.println("upload successful");

		}
		catch (Exception e)
		{
			out.println("upload fail");
			out.println("usage: see https://stat.ethz.ch/pipermail/bioconductor/2010-March/032550.html");
			out.println("with arguments:'name', 'investigationid', 'type', 'file'");
			out.println();
			out.println("Example for RCurl:");
			out.println("library(\"bitops\", lib.loc=\"/Users/joerivandervelde/libs\")");
			out.println("library(\"RCurl\", lib.loc=\"/Users/joerivandervelde/libs\")");
			out.println("uri <- \"http://255.255.255.255:8080/xgap_1_4_distro/uploadfile\");");
			out.println("postForm(uri,");
			out.println("\tname = \"harry.png\",");
			out.println("\tinvestigation_name = \"ClusterDemo\",");
			out.println("\ttype = \"Image\",");
			out.println("\tfile = fileUpload(filename = \"/danny/harry.png\"),");
			out.println("\tstyle = \"HTTPPOST\"");
			out.println(")");

			out.println();
			out.println("Example for Curl:");
			out
					.println("curl -F \"file=@/pictures/mypicture.jpg\" -F \"name=mypicture.jpg\" -F \"investigation_name=ClusterDemo\" -F \"type=Image\" http://255.255.255.255:8080/xgap_1_4_distro/uploadfile");

			out.println();
			e.printStackTrace(out);
		}
		finally
		{
			out.close();
		}
	}
}
