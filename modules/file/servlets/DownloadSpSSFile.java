/* Date:        February 10, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
Despoina Antonakaki 
*/

package servlets;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
	
@Deprecated
public class DownloadSpSSFile extends app.servlet.MolgenisServlet
 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7004240016846336249L;

	
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Create object to assist a servlet in sending a response to the client 
		OutputStream out = response.getOutputStream();
		//    	PrintWriter out = response.getWriter();
		response.setContentType("application/spss");
	    response.setHeader("Content-disposition", "attachment; filename=Matrix.sav");
	       
		PrintStream p = new PrintStream(new BufferedOutputStream(out), false, "UTF8");
		response.setStatus(HttpServletResponse.SC_OK);
	
		
		
		String content = "";

		//In case we retrieve the data form DB 
		/*
		boolean databaseIsAvailable = false;

		JDBCDatabase db = null;
		try  {
			db = (JDBCDatabase) this.getDatabase();
			databaseIsAvailable = true;
		}
		catch (Exception e) {
			content += "Database unavailable.";
			content += e.getStackTrace();
		}
		
		if (databaseIsAvailable) {
			try {
			} catch (Exception e) {
				content += displayUsage(db);
				content += "\n\n";
				content += e.getStackTrace();
			}

        */
		
		
		try {
			Tuple req = new HttpServletRequestTuple(request);  //the HttpServletRequestTuple should be defined in another servlet ? In ordert to actually return the matrix contents .  
			//Get the requested id from the user & and get data from DB 
			int matrixId = req.getInt("id");
			//TODO: Danny OLD code ??
			/*QueryRule q = */new QueryRule("id", Operator.EQUALS, matrixId);
			//Data data = db.find(Data.class, q).get(0);
			content += "TEST write in spss file .";
			
			response.setContentLength(content.length());
			p.print(content);
			p.flush();
			p.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		
    	response.setContentType("application/spss");
        response.setHeader("Content-disposition", "attachment; filename=Matrix.sav");
		
		System.out.println("SPSSExport begin....");
   
	       
	    //exportToSpss(getMatrix(), out);

	       /* flush it to HTTP */

	      	
    	
        out.flush();
    	out.close();
		System.out.println("SPSSExport end....");

    }
	
	public String displayUsage(Database db)
	{
		String usage = "Usage:" + "\n\n" + "SPSS file download:\n"
				+ "http://localhost:8080/gcc/downloadspssfile?id=58342&download=all&stream=true" + "\n\n"
				+ "SPSS file download:\n"
				+ "http://localhost:8080/xgap/downloadspssfile?id=58342&download=all&stream=false" + "\n\n";
		return usage;
	}

//TODO: Danny:  Unused functions ??
//
//	private void exportToSpss(Object matrix, PrintWriter out) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private Object getMatrix() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
	
}
