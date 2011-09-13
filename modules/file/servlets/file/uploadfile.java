package servlets.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import filehandling.generic.PerformUpload;

public class uploadfile extends app.servlet.MolgenisServlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	//TODO: Danny: unused, but i guess we do want to use it
	//private static Logger logger = Logger.getLogger(uploadfile.class);

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
			Database db = getDatabase();
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
	
			PerformUpload.doUpload(db, true, fileName, fileType, fileContent, extraFields, false);

			out.println("upload successful");

		}
		catch (Exception e)
		{
			out.println("Upload failed.");
			out.println("Usage: see https://stat.ethz.ch/pipermail/bioconductor/2010-March/032550.html");
			out.println("With minimal arguments: 'name', 'type', 'file', where:");
			out.println("\tname = The file name plus extension to be put in the database.");
			out.println("\ttype = The type of file, use 'MolgenisFile' for minimal upload. Corresponds to a subclass of MolgenisFile. Other examples: 'RScript', 'InvestigationFile', 'BinaryDataMatrix'.");
			out.println("\tfile = The file (location) you wish to upload the contents of.");
			out.println();
			out.println("Example for RCurl when uploading an 'InvestigationFile':");
			out.println("library(\"bitops\", lib.loc=\"/Users/joerivandervelde/libs\")");
			out.println("library(\"RCurl\", lib.loc=\"/Users/joerivandervelde/libs\")");
			out.println("uri <- \"http://mydomain.org/xqtl/uploadfile\");");
			out.println("postForm(uri,");
			out.println("\tname = \"harry.png\",");
			out.println("\tinvestigation_name = \"ClusterDemo\",");
			out.println("\ttype = \"InvestigationFile\",");
			out.println("\tfile = fileUpload(filename = \"usr/home/danny/harry.png\"),");
			out.println("\tstyle = \"HTTPPOST\"");
			out.println(")");
			out.println();
			out.println("Example for commandline Curl when uploading an 'InvestigationFile':");
			out
					.println("curl -F \"file=@/pictures/mypicture.jpg\" -F \"name=mypicture.jpg\" -F \"investigation_name=ClusterDemo\" -F \"type=InvestigationFile\" http://mydomain.org/xqtl/uploadfile");
			out.println();
			e.printStackTrace(out);
		}
		finally
		{
			out.close();
		}
	}
}
