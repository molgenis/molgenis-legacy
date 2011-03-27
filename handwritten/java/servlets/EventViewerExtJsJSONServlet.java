package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;

import plugins.listplugin.PhenoMatrix;

import commonservice.CommonService;

public class EventViewerExtJsJSONServlet extends app.servlet.MolgenisServlet {
	private static final long serialVersionUID = -5860101269122494304L;
	private static Logger logger = Logger.getLogger(EventViewerExtJsJSONServlet.class);
	
	private static PhenoMatrix pm = new PhenoMatrix();
	private CommonService ct = CommonService.getInstance();
	
	//private static int storedTargetStart;
	//private static int storedTargetLength;
	private static String storedTargetType = "Animal";
	//private static int totalNrOfFeatures = 0;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		try {
			//Tuple req = new HttpServletRequestTuple(request);
			
//			if (req.getInt("reset") != null) {
//				if (req.getInt("reset") == 1) {
//					pm = new PhenoMatrix();
//					storedTargetStart = 0;
//					storedTargetLength = 0;
//					storedTargetType = "Animal";
//					totalNrOfFeatures = 0;
//					out.print("Reset JSON servlet");
//					out.flush();
//					System.out.print("   ***DEBUG Reset JSON servlet");
//					return;
//				}
			
//			}
			
			boolean printValInfo = false;
			boolean limitVal = true;
			
			Database db = getDatabase();
			ct.setDatabase(db);
			if (pm.getDatabase() == null) {
				pm.init(db, storedTargetType);
				//totalNrOfFeatures = pm.getTotalNrOfFeatures();
			}
			
			// Targets
			int nrOfTargets = 25;
			int totalSize = nrOfTargets;
			
			// get the paging info from the client
			//int start = req.getInt("start");
			//int length = req.getInt("length");
			
			
			int[] idx = new int[nrOfTargets];
			List<Integer> allIndices = pm.getAllIndices();
			for (int i = 0; i < nrOfTargets; i++) {
				idx[i] = allIndices.get(i);
			}
			String[] targetNames = pm.getTargetNames(idx);
			
			// Features - values
			int featureId = ct.getMeasurementId("Species");
			pm.addRemFeature(featureId);
			featureId = ct.getMeasurementId("Sex");
			pm.addRemFeature(featureId);
			List<Measurement> featList = pm.getFeatureList();
			//int nrOfFeatures = featList.size();
			
			// Get submatrix
			ObservedValue [][][] matrixPart = pm.getRows(idx);
			
			String featureName = "";
			
			String header = "{success:true,results:'"+ Integer.toString(totalSize)+ "',rows:[";
			String body = "";
			// Ouput body:
			for (int targetCounter = 0; targetCounter < nrOfTargets; targetCounter++) {
				String currentLine;
				currentLine = "{";
				currentLine =
				currentLine += ("\"Target\":\"" + targetNames[targetCounter] + "\"");
				int featureCounter = 0;
				Iterator<Measurement> featIt = featList.iterator();
				
				while (featIt.hasNext()) {
					Measurement currentFeature = featIt.next();
					// get the feature name
					featureName =  currentFeature.getName();
					// Find out what the unit is:
					String dataType = currentFeature.getDataType();
					currentLine += ",\"";
					ObservedValue[] valueArray = matrixPart[targetCounter][featureCounter];
					if (valueArray != null) {
						if (valueArray.length > 0) {
							for (int valueCounter = 0; valueCounter < valueArray.length; valueCounter++) {
								ObservedValue currentValue = valueArray[valueCounter];
								// Get the real value:
								String valueToAdd = currentValue.getValue();
								if (dataType.equals("xref")) {
									// If so, find the corresponding target name:
									valueToAdd = currentValue.getRelation_Name();		
								}
								// Make neat output:
								
								currentLine += featureName + "\":\"" + valueToAdd;
								if (printValInfo == true) {
									currentLine += (" (valid from: " + currentValue.getTime());
									if (currentValue.getEndtime() != null) {
										currentLine += (" through: " + currentValue.getEndtime());
									}
									currentLine += ")";
								}
								//currentLine += "<br />";
								
								if (limitVal) {
									break; // show only most recent
								}
							}
						}
					}
					currentLine += "\"";
					featureCounter++;
				}
				currentLine += "},";
				body += currentLine;
			}  
			// End output:
			String tmpBody = "";
			if (body.length() > 0) tmpBody = body.substring(0, body.length() - 1);
			body = tmpBody + "]}";
			
			out.print(header + body);
			
			//String paging = "pagingstart "+ Integer.toString(start) + ", paginglength: " + Integer.toString(length);
			//System.out.print("  ----> DEBUG  " + paging);
			
			System.out.print("   ***DEBUG JSON output: " + header + body);
			
			out.flush();
			
			logger.info("serving " + request.getRequestURI());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
