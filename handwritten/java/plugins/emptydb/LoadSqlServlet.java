package plugins.emptydb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.organization.Investigation;

import app.JDBCDatabase;

public class LoadSqlServlet extends app.servlet.MolgenisServlet {

	private static final long serialVersionUID = -6004240016846336249L;
	//private static Logger logger = Logger.getLogger(LoadDatamodelServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		boolean databaseIsAvailable = false;
		boolean databaseIsEmpty = false;
		JDBCDatabase db = null;
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		try {
			db = (JDBCDatabase) this.getDatabase();
			databaseIsAvailable = true;
		} catch (Exception e) {
			out.print("Database unavailable.");
			out.print("\n\n");
			e.printStackTrace(out);
		}

		if (databaseIsAvailable) {
			try {
				List<Investigation> invList = db.find(Investigation.class);
				if(invList.size() == 0){
					databaseIsEmpty = true;
				}else{
					out.print("Study(s) present in the database, will not continue to overwrite datamodel.");
				}
			} catch (Exception e) {
				//TODO: is this okay / valid behaviour?
				out.print("Error while querying for investigations, maybe database table does not exists? Attempting to load datamodel anyway.");
				out.print("\n\n");
				e.printStackTrace(out);
				databaseIsEmpty = true;	
			}
		}
		
		if(databaseIsEmpty){
			try {
				new emptyDatabase((JDBCDatabase)db, false);
				out.print("New datamodel succesfully loaded.");
			}catch (Exception e) {
				out.print("Error while trying to overwrite datamodel.");
				out.print("\n\n");
				e.printStackTrace(out);
			}
		}
		out.close();
	}
}
