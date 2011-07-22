package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class TerminateAnimalsServlet extends app.servlet.MolgenisServlet {
	private static final long serialVersionUID = -5860101269122494304L;
	private static Logger logger = Logger.getLogger(TerminateAnimalsServlet.class);
	private CommonService ct = null;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ct = CommonService.getInstance();

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
				Calendar calendar = Calendar.getInstance();
				Date now = calendar.getTime();
				int featureId = ct.getMeasurementId("Experiment");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
				q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS_EQUAL, now));
				q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
				List<ObservedValue> valueList = q.find();
				if (valueList.size() == 1) {
					// Generate selectbox for Actual Discomfort
					featureId = ct.getMeasurementId("ActualDiscomfort");
					Query<Code> codeQuery = db.query(Code.class);
					codeQuery.addRules(new QueryRule("feature", Operator.EQUALS, featureId));
					List<Code> codeList = codeQuery.find();
					out.print("<div class='row'>");
					out.print("<label for='discomfort'>Actual discomfort:</label>");
					out.print("<select name='discomfort' id='discomfort'>");
					for (Code code : codeList) {
						out.print("<option value='" + code.getDescription() + "'>" + code.getCode_String() + " (" + code.getDescription() + ")" + "</option>");
					}
					out.print("</select>");
					out.print("</div>");
						
					// Generate selectbox for ActualAnimalEndStatus
					featureId = ct.getMeasurementId("ActualAnimalEndStatus");
					codeQuery = db.query(Code.class);
					codeQuery.addRules(new QueryRule("feature", Operator.EQUALS, featureId));
					codeList = codeQuery.find();
					out.print("<div class='row'>");
					out.print("<label for='endstatus'>Actual animal end status:</label>");
					out.print("<select name='endstatus' id='endstatus'>");
					for (Code code : codeList) {
						out.print("<option value='" + code.getDescription() + "'>" + code.getCode_String() + " (" + code.getDescription() + ")" + "</option>");
					}
					out.print("</select>");
					out.print("</div>");
						
				} else if (valueList.size() > 1) {
					out.print("<p>Animal in more than one experiment, something seems to be wrong.</p>");
				} // if size = 0, animal is not in experiment, so do not generate any fields
				
				// Always generate action button
				out.print("<div id='buttons_part' class='row'><input type='submit' class='addbutton' value='Apply' onclick=\"__action.value='applyDeath'\" /></div>");
			}
			
			out.flush();

			logger.info("serving " + request.getRequestURI());
		} catch (Exception e) {
			e.printStackTrace(out);
		} finally {
			out.close();
		}
	}
}
