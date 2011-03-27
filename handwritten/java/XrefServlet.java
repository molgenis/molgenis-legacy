//import java.io.PrintWriter;
//import java.util.List;
//
//import javax.naming.NamingException;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.Query;
//import org.molgenis.util.Entity;
//import org.molgenis.util.HttpServletRequestTuple;
//import org.molgenis.util.Tuple;
//
//
///**
// * Servlet implementation class xrefServlet
// */
//public class XrefServlet extends app.servlet.MolgenisServlet {
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * @see HttpServlet#HttpServlet()
//	 */
//	public XrefServlet() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @throws NamingException
//	 * @throws DatabaseException
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//		try {
//			// .../xref/find?xref_entity=xxx&xref_field=yyyy&xref_label=zzzz&
//			// filter=aaa
//			// .../xref/find?xref_entity=xgap_1_3_distro.data.types.Marker&xref_field=id&
//			// xref_label=name&xref_filter=PV
//
//			// alternatief => here the 'field' is the xref input itself
//			// .../xref/find?entity=xxx&field=zzz&filter=aaaa
//
//			Tuple req = new HttpServletRequestTuple(request);
//
//			String xref_entity = req.getString("xref_entity");
//			String xref_field = req.getString("xref_field");
//			String xref_label = req.getString("xref_label");
//			String xref_filter = req.getString("xref_filter");
//			
//			logger.debug(xref_entity + " "+xref_field+ " "+xref_label+" "+xref_filter);
//
//			Database db = getDatabase();
//			Query<Entity> q = db.query((Class<Entity>) Class.forName(xref_entity));
//			if(xref_filter != null)
//				q.like(xref_label, xref_filter + "%");
//			q.sortASC(xref_label);
//			q.limit(10);
//
//			List<Entity> result = q.find();
//
//			// transform in JSON (JavaScript Object Notation)
//			PrintWriter out = response.getWriter();
//
//			out.write("{");
//			for (int i = 0; i < result.size(); i++) {
//				if (i > 0)
//					out.write(",");
//				out.write(result.get(i).get(xref_field) + ":\"" + result.get(i).get(xref_label) + "\"");
//			}
//			out.write("}");
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ServletException(e.getMessage());
//		}
//	}
//
//}
