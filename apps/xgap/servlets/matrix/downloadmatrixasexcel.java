package servlets.matrix;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import plugins.matrix.manager.Browser;

public class downloadmatrixasexcel extends app.servlet.MolgenisServlet {

	private static final long serialVersionUID = -6004240016846336249L;
	
	//TODO: Danny: unused, but i guess we do want to use it
	//private static Logger logger = Logger.getLogger(downloadmatrixasexcel.class);

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Tuple req = null;
		boolean databaseIsAvailable = false;
		boolean setupSuccess = false;
		boolean argumentsAreCorrect = false;
		
		Database db = null;
		AbstractDataMatrixInstance<Object> instance = null;

		try {
			db = this.getDatabase();
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
				req = new HttpServletRequestTuple(request);
				
				//special exception for filtered content: get matrix instance from memory and do complete handle
				if(req.getString("id").equals("inmemory"))
				{
					OutputStream outSpecial = response.getOutputStream();
					File excelFile = Browser.inmemory.getAsExcelFile();
					URL localURL = excelFile.toURI().toURL();
					URLConnection conn = localURL.openConnection();
					InputStream in = new BufferedInputStream(conn.getInputStream());
					response.setContentType("application/vnd.ms-excel");
					response.setContentLength((int) excelFile.length());
					response.setHeader("Content-disposition","attachment; filename=\""+Browser.inmemory.getData().getName()+"_"+"some"+".xls"+"\"");
					byte[] buffer = new byte[2048];
					for (;;) {
						int nBytes = in.read(buffer);
						if (nBytes <= 0)
							break;
						outSpecial.write(buffer, 0, nBytes);
					}
					outSpecial.flush();
					outSpecial.close();
				}
				
				int matrixId = req.getInt("id");
				QueryRule q = new QueryRule("id", Operator.EQUALS, matrixId);
				List<Data> dataList = db.find(Data.class, q);
				if (dataList.size() != 1) {
					throw new Exception("Datamatrix for ID " + matrixId
							+ " was not found.");
				}
				Data data = dataList.get(0);
				DataMatrixHandler dmh = new DataMatrixHandler(db);
				instance = dmh.createInstance(data);
				setupSuccess = true;
			} catch (Exception e) {
				PrintWriter out = response.getWriter();
				response.setContentType("text/plain");
				displayUsage(out, db);
				out.print("\n\n");
				e.printStackTrace(out);
				out.close();
			}
		}
		
		if(setupSuccess){
			try {
				if (req.getString("download").equals("all"))
				{
					//correct
				}else if (req.getString("download").equals("some"))
				{
					req.getInt("coff");
					req.getInt("clim");
					req.getInt("roff");
					req.getInt("rlim");
					//correct
				}else{
					throw new Exception("Bad arguments.");
				}
				argumentsAreCorrect = true;
			}catch (Exception e) {
				PrintWriter out = response.getWriter();
				response.setContentType("text/plain");
				displayUsage(out, db);
				out.print("\n\n");
				e.printStackTrace(out);
				out.close();
			}
		}

		if (argumentsAreCorrect) {
			OutputStream outFile = response.getOutputStream();
			try {
				File excelFile = null;
				String download = req.getString("download");
				if (download.equals("all"))
				{
					excelFile = instance.getAsExcelFile();
				}else if (download.equals("some"))
				{
					int colOffset = req.getInt("coff");
					int colLimit = req.getInt("clim");
					int rowOffset = req.getInt("roff");
					int rowLimit = req.getInt("rlim");
					excelFile = instance.getSubMatrixByOffset(rowOffset, rowLimit, colOffset, colLimit).getAsExcelFile();
				}
				URL localURL = excelFile.toURI().toURL();
				URLConnection conn = localURL.openConnection();
				InputStream in = new BufferedInputStream(conn.getInputStream());
				response.setContentType("application/vnd.ms-excel");
				response.setContentLength((int) excelFile.length());
				response.setHeader("Content-disposition","attachment; filename=\""+instance.getData().getName()+"_"+download+".xls"+"\"");
				byte[] buffer = new byte[2048];
				for (;;) {
					int nBytes = in.read(buffer);
					if (nBytes <= 0)
						break;
					outFile.write(buffer, 0, nBytes);
				}
				outFile.flush();
			} catch (Exception e) {
				e.printStackTrace();
				//logger.error(e);
			} finally {
				outFile.close();
			}
		}
	}

	public void displayUsage(PrintWriter out, Database db) {
		String usage = "Potentially downloadable matrices available in this database:\n\n" + matricesFromDb(db) + "\n";
		out.print(usage);
	}

	public String matricesFromDb(Database db) {
		String res = "";
		try {
			List<Data> dataList = db.find(Data.class);
//			MolgenisFileHandler mfh = new MolgenisFileHandler(db);
			for (Data data : dataList) {
//				try{
//					mfh.findFile(data).equals("null")
//				}
				
					res += data.toString() + "\n";
				
			}
		} catch (Exception e) {
			res += "An error occurred when retrieving matrix information:\n\n";
			res += e.getMessage();
		}
		return res;
	}

}
