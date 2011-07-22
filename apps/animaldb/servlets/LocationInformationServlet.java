package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class LocationInformationServlet extends app.servlet.MolgenisServlet {
	private static final long serialVersionUID = -5115596071747077428L;
	private static Logger logger = Logger.getLogger(LocationInformationServlet.class);
	private CommonService ct = null;

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ct = CommonService.getInstance();
		
		PrintWriter out = response.getWriter();
		try
		{
			Tuple req = new HttpServletRequestTuple(request);
			
			Database db = getDatabase();
			ct.setDatabase(db);
			
			String tmpString = req.getString("loc").replace(".", "");
			tmpString = tmpString.replace(",", "");
			int locid = Integer.parseInt(tmpString);
			if (locid == 0) {
				out.print("");
			} else {
				ObservationTarget currentLocation = ct.getObservationTargetById(locid);	
				out.print(currentLocation.getName());
				ObservationTarget superloc = new ObservationTarget();
				boolean firstTime = true;
				while (true) {
					superloc = getSuperloc(db, locid);
					if (superloc != null){
						if (firstTime) {
							firstTime = false;
						} else {
							out.print(", which");
						}
						out.print(" is a sublocation of " + superloc.getName());
						locid = superloc.getId();
					} else {
						break;
					}
				}
			}
			out.flush();

			logger.info("serving " + request.getRequestURI());
		}
		catch (Exception e)
		{
			e.printStackTrace(out);
		}
		finally
		{
			out.close();
		}
	}
	
	ObservationTarget getSuperloc(Database db, int locid) throws DatabaseException, ParseException {
		
		ObservationTarget emptyLocation = null;
		
		int etid = ct.getProtocolId("SetSublocationOf");
		List<ProtocolApplication> eventList = ct.getAllProtocolApplicationsByType(etid);
		
		for (ProtocolApplication e : eventList) {
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, e.getId()));
			q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, locid));
			q.addRules(new QueryRule(Operator.SORTDESC, "time"));
			// TODO: check for endtime?
			List<ObservedValue> valueList = new ArrayList<ObservedValue>();
			try {
				valueList = q.find();
			} catch (Exception ex) {
				ex.printStackTrace();
				return emptyLocation;
			}
			if (valueList.size() > 0) {
				ObservedValue currentValue = valueList.get(0);
				if (currentValue != null) {
					int superlocid = 0;
					if (currentValue.getRelation() != null) {
						superlocid = currentValue.getRelation_Id();
					}
					if (superlocid > 0) {
						ObservationTarget superloc = ct.getObservationTargetById(superlocid);
						return superloc;
					}
				}
			}
		}
		return emptyLocation;
	}
	
}
