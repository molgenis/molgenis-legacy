package servlets.matrix;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

public class getmatrixinfo extends app.servlet.MolgenisServlet {

	private static final long serialVersionUID = -6004240016846336249L;
	private static Logger logger = Logger.getLogger(getmatrixinfo.class);
	
	private DataMatrixHandler dmh;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		boolean databaseIsAvailable = false;
		boolean setupSuccess = false;
		JDBCDatabase db = null;
		AbstractDataMatrixInstance instance = null;
		
		try {
			db = (JDBCDatabase) this.getDatabase();
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
				int matrixId = req.getInt("id");
				QueryRule q = new QueryRule("id", Operator.EQUALS, matrixId);
				List<Data> dataList = db.find(Data.class, q);
				if (dataList.size() != 1) {
					throw new Exception("Datamatrix for ID " + matrixId
							+ " was not found.");
				}
				Data data = dataList.get(0);
				dmh = new DataMatrixHandler(db);
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

		if (setupSuccess) {
			PrintWriter out = response.getWriter();
			response.setContentType("text/plain");
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

	public void displayUsage(PrintWriter out, JDBCDatabase db) {
		String usage = "Downloadable  matrices available in this database:\n\n"
				+ matricesFromDb(db) + "\n";
		out.print(usage);
	}

	public String matricesFromDb(JDBCDatabase db) {
		String res = "";
		try {
			List<Data> dataList = db.find(Data.class);
			for (Data data : dataList) {
				if (!dmh.findSource(data)
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
