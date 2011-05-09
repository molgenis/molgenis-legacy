package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ViewEventsServlet extends app.servlet.MolgenisServlet {
	private static final long serialVersionUID = -5860101269122494304L;
	private static Logger logger = Logger.getLogger(ViewEventsServlet.class);
	private CommonService ct = CommonService.getInstance();

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		try {
			Tuple req = new HttpServletRequestTuple(request);

			Database db = getDatabase();
			ct.setDatabase(db);

			String tmpString = req.getString("animal").replace(".", "");
			tmpString = tmpString.replace(",", "");
			int animalId = Integer.parseInt(tmpString);
			if (animalId == 0) {
				out.print("");
			} else {
				List<ObservedValue> valList = db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				if (!valList.isEmpty()) {
					out.print("<table class='listtable'>");
					int rowCount = 0;
					for (ObservedValue currentValue : valList) {
						// Get the corresponding event (type):
						int eventId = currentValue.getProtocolApplication();
						ProtocolApplication currentEvent = ct.getProtocolApplicationById(eventId);
						if (rowCount % 2 == 0) {
							out.print("<tr class='form_listrow0'>");
						} else {
							out.print("<tr class='form_listrow1'>");
						}
						// Protocol name and app dates
						out.print("<td>" + currentEvent.getProtocol_Name() + "<br />");
						if (currentValue.getTime() != null) out.print(" Valid from " + currentValue.getTime().toString());
						if (currentValue.getEndtime() != null) out.print(" through " + currentValue.getEndtime().toString());
						out.print("</td>");
						// Feature name
						String featureName = currentValue.getFeature_Name();
						if (featureName == null) {
							out.print("<td></td><td></td>");
						} else {
							out.print("<td>" + featureName + "</td>");
							// The actual value
							String currentValueContents = "";
							// Find out what the unit is:
							int featureId = currentValue.getFeature_Id();
							Measurement currentFeature = ct.getMeasurementById(featureId);
							if (currentFeature.getDataType().equals("xref")) {
								try {
									currentValueContents = currentValue.getRelation_Name();
								} catch(Exception e) {
									int targetId = currentValue.getRelation_Id();
									currentValueContents = "Value (Target id " + targetId + ") no longer in database";
								}
							} else {
								currentValueContents = currentValue.getValue();
							}
							out.print("<td>" + currentValueContents + "</td>");
						}
						
						out.print("</tr>");
						
						rowCount++;
					}
					out.print("</table>");
				}
			}

			out.flush();

			logger.info("serving " + request.getRequestURI());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
