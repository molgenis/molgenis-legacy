package services;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class getmatrixinfo  implements MolgenisService {

	//TODO: Danny: unused, but i guess we do want to use it
	//private static Logger logger = Logger.getLogger(getmatrixinfo.class);
	
	private DataMatrixHandler dmh;
	
	private MolgenisContext mc;
	
	public getmatrixinfo(MolgenisContext mc)
	{
		this.mc = mc;
	}
	
	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		boolean databaseIsAvailable = false;
		boolean setupSuccess = false;
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
				int matrixId = request.getInt("id");
				QueryRule q = new QueryRule("id", Operator.EQUALS, matrixId);
				List<Data> dataList = db.find(Data.class, q);
				if (dataList.size() != 1) {
					throw new Exception("Datamatrix for ID " + matrixId
							+ " was not found.");
				}
				Data data = dataList.get(0);
				dmh = new DataMatrixHandler(db);
				instance = dmh.createInstance(data, db);
				setupSuccess = true;
			} catch (Exception e) {
				PrintWriter out = response.getResponse().getWriter();
				response.getResponse().setContentType("text/plain");
				displayUsage(out, db);
				out.print("\n\n");
				e.printStackTrace(out);
				out.close();
			}
		}

		if (setupSuccess) {
			PrintWriter out = response.getResponse().getWriter();
			response.getResponse().setContentType("text/plain");
			try {
				
				out.println("matrix info");
				out.println("name = " + instance.getData().getName());
				out.println("numberofcols = " + instance.getNumberOfCols());
				out.println("numberofrows = " + instance.getNumberOfRows());
				out.println("colnames = " + "TODO");
				out.println("rownames = " + "TODO");
				out.print("\n\n");
				out.close();
			} catch (Exception e) {
				displayUsage(out, db);
				out.print("\n\n");
				e.printStackTrace(out);
			} finally {
				out.close();
			}
		}
	}

	public void displayUsage(PrintWriter out, Database db) {
		String usage = "Downloadable  matrices available in this database:\n\n"
				+ matricesFromDb(db) + "\n";
		out.print(usage);
	}

	public String matricesFromDb(Database db) {
		String res = "";
		try {
			List<Data> dataList = db.find(Data.class);
			for (Data data : dataList) {
				if (!dmh.findSource(data, db)
						.equals("null")) {
					res += data.toString() + "\n";
				}
			}
		} catch (Exception e) {
			res += "An error occurred when retrieving matrix information:\n\n";
			res += e.getMessage();
		}
		return res;
	}

}
