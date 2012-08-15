package services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

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

public class downloadmatrixasexcel implements MolgenisService {

	private MolgenisContext mc;
	
	public downloadmatrixasexcel(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		boolean databaseIsAvailable = false;
		boolean setupSuccess = false;
		boolean argumentsAreCorrect = false;
		
		Database db = null;
		DataMatrixInstance instance = null;

		try {
			db = request.getDatabase();
			databaseIsAvailable = true;
		} catch (Exception e) {
			PrintWriter out = response.getResponse().getWriter();
			response.getResponse().setContentType("text/plain");
			out.print("Database unavailable.");
			out.print("\n\n");
			e.printStackTrace(out);
			out.close();
		}

		if (databaseIsAvailable) {
			try {
				
				//special exception for filtered content: get matrix instance from memory and do complete handle
				if(request.getString("id").equals("inmemory"))
				{
					ApplicationController molgenis = (ApplicationController) request.getRequest().getSession().getAttribute("application");
					DataMatrixInstance dm = ((DataMatrixInstance)molgenis.sessionVariables.get(MatrixManager.SESSION_MATRIX_DATA));
					if(dm.getNumberOfCols() > 256)
					{
						throw new Exception("Too many columns selected. The maximum for Excel is 256.");
					}
					if(dm.getNumberOfRows() > 100000)
					{
						throw new Exception("Too many rows selected. The maximum is limited to 100k.");
					}
					File excelFile = dm.getAsExcelFile();
					OutputStream outSpecial = response.getResponse().getOutputStream();
					URL localURL = excelFile.toURI().toURL();
					URLConnection conn = localURL.openConnection();
					InputStream in = new BufferedInputStream(conn.getInputStream());
					response.getResponse().setContentType("application/vnd.ms-excel");
					response.getResponse().setContentLength((int) excelFile.length());
					response.getResponse().setHeader("Content-disposition","attachment; filename=\""+dm.getData().getName()+"_"+"some"+".xls"+"\"");
					byte[] buffer = new byte[2048];
					for (;;) {
						int nBytes = in.read(buffer);
						if (nBytes <= 0)
							break;
						outSpecial.write(buffer, 0, nBytes);
					}
					outSpecial.flush();
					outSpecial.close();
					return;
				}
				
				int matrixId = request.getInt("id");
				QueryRule q = new QueryRule("id", Operator.EQUALS, matrixId);
				List<Data> dataList = db.find(Data.class, q);
				if (dataList.size() != 1) {
					throw new Exception("Datamatrix for ID " + matrixId
							+ " was not found.");
				}
				Data data = dataList.get(0);
				DataMatrixHandler dmh = new DataMatrixHandler(db);
				instance = dmh.createInstance(data, db);
				if(instance.getNumberOfCols() > 256)
				{
					throw new Exception("Too many columns selected. The maximum for Excel is 256.");
				}
				if(instance.getNumberOfRows() > 100000)
				{
					throw new Exception("Too many rows selected. The maximum is limited to 100k.");
				}
				setupSuccess = true;
			} catch (Exception e) {
				PrintWriter out = response.getResponse().getWriter();
				e.printStackTrace(out);
				out.print("\n\n");
				response.getResponse().setContentType("text/plain");
				displayUsage(out, db);
				out.close();
			}
		}
		
		if(setupSuccess){
			try {
				if (request.getString("download").equals("all"))
				{
					//correct
				}else if (request.getString("download").equals("some"))
				{
					request.getInt("coff");
					request.getInt("clim");
					request.getInt("roff");
					request.getInt("rlim");
					//correct
				}else{
					throw new Exception("Bad arguments.");
				}
				argumentsAreCorrect = true;
			}catch (Exception e) {
				PrintWriter out = response.getResponse().getWriter();
				response.getResponse().setContentType("text/plain");
				displayUsage(out, db);
				out.print("\n\n");
				e.printStackTrace(out);
				out.close();
			}
		}

		if (argumentsAreCorrect) {
			OutputStream outFile = response.getResponse().getOutputStream();
			try {
				File excelFile = null;
				String download = request.getString("download");
				if (download.equals("all"))
				{
					excelFile = instance.getAsExcelFile();
				}else if (download.equals("some"))
				{
					int colOffset = request.getInt("coff");
					int colLimit = request.getInt("clim");
					int rowOffset = request.getInt("roff");
					int rowLimit = request.getInt("rlim");
					excelFile = instance.getSubMatrixByOffset(rowOffset, rowLimit, colOffset, colLimit).getAsExcelFile();
				}
				URL localURL = excelFile.toURI().toURL();
				URLConnection conn = localURL.openConnection();
				InputStream in = new BufferedInputStream(conn.getInputStream());
				response.getResponse().setContentType("application/vnd.ms-excel");
				response.getResponse().setContentLength((int) excelFile.length());
				response.getResponse().setHeader("Content-disposition","attachment; filename=\""+instance.getData().getName()+"_"+download+".xls"+"\"");
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
