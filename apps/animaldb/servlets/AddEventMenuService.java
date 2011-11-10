package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.protocol.Protocol_Features;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

@Deprecated
public class AddEventMenuService extends app.servlet.MolgenisServlet {
	private static final long serialVersionUID = -9148847518626490722L;
	private static Logger logger = Logger.getLogger(AddEventMenuService.class);
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		try
		{
			Tuple req = new HttpServletRequestTuple(request);
			
			Database db = this.createDatabase();
			this.createLogin(db, request);
			
			int eventTypeId = req.getInt("etype");
			if (eventTypeId == 0) {
				out.print("");
			} else {
				boolean sepval = req.getBool("sepval");
				int nrOfAnimals = req.getInt("nrofan");
				
				List<Measurement> correspondingFeatures = null;
				Iterator<Measurement> correspondingFeatureIterator = null;;
				List<Protocol_Features> links = db.find(Protocol_Features.class, new QueryRule("Protocol", Operator.EQUALS, eventTypeId));
				if (links.size() > 0) {
					List<Integer> idList = new ArrayList<Integer>();
					for(Protocol_Features ef : links){
					    idList.add(ef.getFeatures_Id());
					}
					correspondingFeatures = db.find(Measurement.class, new QueryRule("id", Operator.IN, idList));
					correspondingFeatureIterator = correspondingFeatures.iterator();
				}
				
				int valueNr = 0;
				boolean keepLooping = true;		
				while (keepLooping == true) {
					
					Measurement currentFeature = null;
					
					String hideDiv = "";
					String dummyValue = "";
					String currentFeatureName;
					String currentFeatureDataType;
					int currentFeatureId;
					boolean currentFeatureFocal;
					if (correspondingFeatures == null) {
						// Event type without features...
						hideDiv = " style='display:none'";
						dummyValue = " value='Dummy'"; // could be anything!
						keepLooping = false;
						currentFeatureName = "Dummy";
						currentFeatureDataType = "dummy";
						currentFeatureId = 0;
						currentFeatureFocal = true;
					} else {
						currentFeature = correspondingFeatureIterator.next();
						currentFeatureName = currentFeature.getName();
						currentFeatureDataType = currentFeature.getDataType();
						currentFeatureId = currentFeature.getId();
						currentFeatureFocal = currentFeature.getTemporal();
					}
					
					boolean targetLink = false;
					boolean codeValues = false;
					List<Code> codeList = null;
					List<ObservationTarget> targetList = new ArrayList<ObservationTarget>();
					// Check if the unit is a TargetLink:
					if (currentFeature != null) {
						if (currentFeatureDataType.equals("xref")) {
							targetLink = true;
							// Observationtarget type and label fields are not in use anymore
							//if (currentFeature.getObservationtargettype() != null && currentFeature.getObservationtargettype().equals("Group") && currentFeature.getObservationtargetlabel() != null) {
							//	targetList = ct.getAllMarkedGroups(currentFeature.getObservationtargetlabel());
							//} else {
							//	targetList = db.find(ObservationTarget.class, new QueryRule(ObservationTarget.ONTOLOGYREFERENCE_NAME, Operator.EQUALS, currentFeature.getObservationtargettype()));
							//}
							targetList = db.find(ObservationTarget.class);
						} else {
							// Check if the feature has codes:
							Query<Code> q = db.query(Code.class);
							q.addRules(new QueryRule("feature", Operator.EQUALS, currentFeatureId));
							codeList = q.find();
							if (codeList.size() > 0) {
								codeValues = true;
							}
						}
					}
					
					int nrOfFeatValParts = 1;
					if (sepval) {
						nrOfFeatValParts = nrOfAnimals;
					}
					for (int animalNumber = 0; animalNumber < nrOfFeatValParts; animalNumber++) {
						if (sepval) {
							out.print("<p>ObservationTarget " + (animalNumber + 1) + "</p>");
							// Is there a way to know the name of this target here??
						}
						out.print("<div id='featurevalue_part" + animalNumber + "' class='row'" + hideDiv + ">");
						out.print("<label for='value" + animalNumber + "_" + valueNr + "'>" + currentFeatureName + " value: </label>");
						if (targetLink) {
							out.print("<select name='value" + animalNumber + "_" + valueNr + "' id='value" + animalNumber + "_" + valueNr + "'>");
							for (ObservationTarget t : targetList) {
								out.print("<option value='" + t.getId() + "'>" + t.getName() + "</option>");
							}
							out.print("</select>");
						} else {
							if (codeValues) {
								out.print("<select name='value" + animalNumber + "_" + valueNr + "' id='value" + animalNumber + "_" + valueNr + "'>");
								for (Code code : codeList) {
									out.print("<option value='" + code.getDescription() + "'>" + code.getCode_String() + " (" + code.getDescription() + ")" + "</option>");
								}
								out.print("</select>");
							} else {
								out.print("<input type='text' class='textbox' name='value" + animalNumber + "_" + valueNr + "' id='value" + animalNumber + "_" + valueNr + "' " + dummyValue + "/>");
							}
						}
						
						out.print("<em>(unit: " + currentFeatureDataType + ")</em>");
						out.print("<input type='hidden' name='feature" + animalNumber + "_" + valueNr + "' value='" + currentFeatureId + "' />");
						out.print("</div>");
						out.print("<div id='startdatetimevalue_part" + animalNumber + "' class='row'>");
						out.print("<label for='startdatetime" + animalNumber + "_" + valueNr + "'>(Start) date and time: </label>");
						out.print("<input type='text' class='textbox' id='startdatetime" + animalNumber + "_" + valueNr + "' name='startdatetime" + animalNumber + "_" + valueNr + "' value='' onclick='showDateInput(this,true)' autocomplete='off' />");
						out.print("</div>");
						if (currentFeatureFocal == false) {
							out.print("<div id='enddatetimevalue_part" + animalNumber + "' class='row'>");
							out.print("<label for='enddatetime" + animalNumber + "_" + valueNr + "'>End date and time (optional): </label>");
							out.print("<input type='text' class='textbox' id='enddatetime" + animalNumber + "_" + valueNr + "' name='enddatetime" + animalNumber + "_" + valueNr + "' value='' onclick='showDateInput(this,true)' autocomplete='off' />");
							out.print("</div>");
						}
						
						if (correspondingFeatureIterator != null && !correspondingFeatureIterator.hasNext()) {
							keepLooping = false;
						}
						
					} // end of loop through desired nr. of feature-value form parts
					valueNr++;
					
					// TODO? Idee van Morris
//					List<HtmlInput> iList = new ArrayList<HtmlInput>();
//					for()
//					{
//						DateInput i = new DateInput("feature"+valueNr, currentFeature.getId());
//						iList.add(i);
//					}
//					for(HtmlInput i: iList)
//					{
//						out.print(String.format("<tr><td>%s</td><td>%s</td></tr>", i.getLabel(), i.toHtml()));
//					}
				}
				out.print("<div id='buttons_part' class='row'><input type='submit' class='addbutton' value='Apply' onclick=\"__action.value='addEvent'\" /></div>");
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
}
