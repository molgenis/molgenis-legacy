package servlets.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import decorators.MolgenisFileHandler;

@Deprecated
public class viewfile extends app.servlet.MolgenisServlet {

	private static final long serialVersionUID = -6004230016846336249L;
	private static Logger logger = Logger.getLogger(viewfile.class);

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		boolean databaseIsAvailable = false;
		boolean paramsPresent = false;
		boolean fileFound = false;
		Database db = null;
		File file = null;
		MolgenisFile mf = null;
//		String type = null;
//		String investigationname = null;
		String name = null;

		try {
			db = this.createDatabase();
			databaseIsAvailable = true;
		} catch (Exception e) {
			PrintWriter out = response.getWriter();
			response.setContentType("text/plain");
			out.print("Database unavailable.");
			out.print("\n\n");
			e.printStackTrace(out);
			out.close();
		}

		if (databaseIsAvailable) {
			try {
				Tuple req = new HttpServletRequestTuple(request);
			
//				type = req.getString("type");
//				investigationname = req.getString("investigationname");
				name = req.getString("name");
		
//				if(type == null){
//					throw new NullPointerException("Not specified: 'type'");
//				}
				
				if(name == null){
					throw new NullPointerException("Not specified: 'name'");
				}
				
//				if(investigationname == null){
//					throw new NullPointerException("Not specified: 'investigationname'");
//				}

				paramsPresent = true;
				
			} catch (Exception e) {
				PrintWriter out = response.getWriter();
				response.setContentType("text/plain");
				displayUsage(out, db);
				out.print("\n\n");
				e.printStackTrace(out);
				out.close();
			}
		}
		
		if (paramsPresent) {
			try {
				
		//	file = new File("");
			
			MolgenisFileHandler mfh = new MolgenisFileHandler(db);
			//File storageDir = mfh.getValidatedFileDeployStorageLocationToFile();
			List<MolgenisFile> mfList = db.find(MolgenisFile.class, new QueryRule("name", Operator.EQUALS, name));
			
			if(mfList.size() == 0){
				throw new Exception("No file with name '"+name+"' found");
			}else if(mfList.size() > 1){
				throw new Exception("Severe error: multiple files found for name '"+name+"'");
			}
			
			mf = mfList.get(0);
			//file = FindBackend.getFileFor(db, mf);
			file = mfh.getFile(mf, db);

			if((int) file.length() > Integer.MAX_VALUE){
				throw new IOException("File too large! > Integer.MAX_VALUE");
			}
			
			fileFound = true;
			
			}catch (Exception e) {
				PrintWriter out = response.getWriter();
				response.setContentType("text/plain");
				displayUsage(out, db);
				out.print("\n\n");
				e.printStackTrace(out);
				out.close();
			}
		}

		if (fileFound) {
			OutputStream outFile = response.getOutputStream();
			try {
				URL localURL = file.toURI().toURL();
				URLConnection conn = localURL.openConnection();
				InputStream in = new BufferedInputStream(conn.getInputStream());
				ServletContext sc = getServletContext();
				response.setContentType(sc.getMimeType(mf.getExtension()));
				response.setContentLength((int) file.length());
				//response.setHeader("Content-disposition","attachment; filename=\""+mf.getName()+"."+mf.getExtension()+"\"");
				//response.setStatus(arg0)
				byte[] buffer = new byte[(int) file.length()];
				while (in.available() != 0) {
					in.read(buffer);
					outFile.write(buffer);
				//	in.skip(5); -> skip in case file length > MAX_INT
				}
				outFile.flush();
			} catch (Exception e) {
				logger.error(e);
			} finally {
				outFile.close();
			}
		}
	}

	public void displayUsage(PrintWriter out, Database db) {
		String usage = "To download file content, please specify 'name' (ie. downloadfile?name=myresultfile\n\n";
		out.print(usage);
	}

}
