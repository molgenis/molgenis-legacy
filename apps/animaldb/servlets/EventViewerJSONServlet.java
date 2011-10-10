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
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import plugins.listplugin.PhenoMatrix;

public class EventViewerJSONServlet extends app.servlet.MolgenisServlet {
	private static final long serialVersionUID = -5860101269122494304L;
	private static Logger logger = Logger.getLogger(EventViewerJSONServlet.class);
	
	private static PhenoMatrix pm;
	private static int storedTargetStart;
	private static int storedTargetLength;
	private static String storedTargetType = "All";
	private static int totalNrOfFeatures = 0;
	private static int userId;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		try {
			Tuple req = new HttpServletRequestTuple(request);
			
			if (req.getInt("reset") != null) {
				if (req.getInt("reset") == 1) {
					pm = new PhenoMatrix();
					storedTargetStart = 0;
					storedTargetLength = 0;
					storedTargetType = "All";
					totalNrOfFeatures = 0;
					logger.debug("Reset JSON servlet");
					return;
				}
			}
			
			/*
			 * Parameters sent to the server
				The following information is sent to the server for each draw request. Your server-side script must use this information to obtain the data required for the draw.

				Type	Name	Info
				int	iDisplayStart	Display start point
				int	iDisplayLength	Number of records to display
				int	iColumns	Number of columns being displayed (useful for getting individual column search info)
				string	sSearch	Global search field
				boolean	bEscapeRegex	Global search is regex or not
				boolean	bSortable_(int)	Indicator for if a column is flagged as sortable or not on the client-side
				boolean	bSearchable_(int)	Indicator for if a column is flagged as searchable or not on the client-side
				string	sSearch_(int)	Individual column filter
				boolean	bEscapeRegex_(int)	Individual column filter is regex or not
				int	iSortingCols	Number of columns to sort on
				int	iSortCol_(int)	Column being sorted on (you will need to decode this number for your database)
				string	sSortDir_(int)	Direction to be sorted - "desc" or "asc". Note that the prefix for this variable is wrong in 1.5.x where iSortDir_(int) was used)
				string	sEcho	Information for DataTables to use for rendering
			 * 
			 */
			
			// Get user ID
			userId = req.getInt("userId");
			
			// Get database and login
			Database db = this.createDatabase();
			this.createLogin(db, request);
			
			// Init OLD pheno matrix (not to be confused with the new matrix component)
			if (pm.getDatabase() == null) { // if matrix has no DB yet, initialize it first
				pm.init(db, storedTargetType, userId);
				totalNrOfFeatures = pm.getTotalNrOfFeatures();
			}
			
			// Type of target changed?
			boolean targetTypeChanged = false;
			if (req.getString("targetType") != null) {
				if (!req.getString("targetType").equals(storedTargetType)) {
					targetTypeChanged = true;
					storedTargetType = req.getString("targetType");
					// Reinit matrix
					pm.init(db, storedTargetType, userId);
					totalNrOfFeatures = pm.getTotalNrOfFeatures();
				}
			}
			
			// Standard params
			String echo = req.getString("sEcho");
			int totalSize = pm.getNrOfTargets();
			int displaySize = totalSize;
			int start = req.getInt("iDisplayStart");
			int length = req.getInt("iDisplayLength");
			
			// Custom params
			boolean printValInfo = false;
			if (req.getString("printValInfo") != null) {
				if (req.getString("printValInfo").equals("true")) {
					printValInfo = true;
				}
			}
			boolean limitVal = false;
			if (req.getString("limitVal") != null) {
				if (req.getString("limitVal").equals("true")) {
					limitVal = true;
				}
			}
			
			// Filter terms
			List<String> filterTermList = new ArrayList<String>();
			for (int s = 1; s <= totalNrOfFeatures + 1; s++) {
				if (req.getString("sSearch_" + s) != null && !req.getString("sSearch_" + s).equals("")) {
					filterTermList.add(req.getString("sSearch_" + s));
				} else {
					filterTermList.add("");
				}
			}
			
			// Feature added/removed?
			boolean addFeature = false;
			int featureId = -1;
			if (req.getString("feature") != null) {
				String tmpString = req.getString("feature").replace(".", "");
				tmpString = tmpString.replace(",", ""); // FIXME: fix in locale independent way
				featureId = Integer.parseInt(tmpString);
				if (featureId >= 0) {
					// Add or remove one feature...
					int colNr = pm.addRemFeature(featureId);
					if (colNr >= 0) {
						// Get rid of search term on column that was just removed
						filterTermList.remove(colNr);
						filterTermList.add("");
					} else {
						addFeature = true;
					}
				} else {
					// Remove all features...
					pm.remAllFeatures();
				}
				start = storedTargetStart;
				length = storedTargetLength;
				totalNrOfFeatures = pm.getTotalNrOfFeatures();
			}
			
			// Show size changed?
			if (length != storedTargetLength) {
				start = storedTargetStart;
				storedTargetLength = length;
			}
			
			// Target type changed?
			if (targetTypeChanged) {
				start = 0;
				storedTargetStart = 0;
				storedTargetLength = length;
			}
			
			// Prepare array with indices of targets to pass back to list viewer
			ObservedValue [][][] matrixPart = null;
			int resultSize = length;
			if (start + length > totalSize) {
				resultSize = length - ((start + length) - totalSize);
			}
			int[] idx = new int[resultSize];
			
			// Searching?
			List<Integer> searchIdx = pm.getAllIndices();
			boolean searching = false;
			if (req.getString("sSearch") != null && !req.getString("sSearch").equals("")) {
				searching = true;
				String searchTerm = req.getString("sSearch");
				searchIdx = pm.search(searchTerm, limitVal);
			}	
			// Filtering?
			int s = 0;
			for (String filterTerm : filterTermList) {
				if (!filterTerm.equals("")) {
					searching = true;
					List<Integer> filterIdx = pm.filterColumn(s, filterTerm, limitVal);
					// Further restrict search results, if any:
					List<Integer> removeIdx = new ArrayList<Integer>();
					for (Integer sidx : searchIdx) {
						if (!filterIdx.contains(sidx)) {
							removeIdx.add(sidx);
						}
					}
					searchIdx.removeAll(removeIdx); // to avoid concurrent editing of search index array
				}
				s++;
			}
			// Fill the array with target indices
			if (searching) {
				displaySize = searchIdx.size();
				if (start + length > displaySize) {
					resultSize = length - ((start + length) - displaySize);
				}
				for (int i = 0; i < resultSize; i++) {
					if ((start + i) >= displaySize) {
						break;
					}
					idx[i] = searchIdx.get(start + i);
				}
			} else {
				// Just paging
				storedTargetStart = start;
				storedTargetLength = length;
				for (int i = 0; i < resultSize; i++) {
					idx[i] = start + i;
				}
			}
			
			matrixPart = pm.getRows(idx);
			int nrOfFeatures = 0;
			List<Measurement> featList = pm.getFeatureList();
			if (featList != null) {
				nrOfFeatures = featList.size();
			}
			String[] targetNames = pm.getTargetNames(idx);
			Integer[] targetIds = pm.getTargetIds(idx);
			
			// Begin output:
			String header = "";
			header += "{";
			header += ("\"sEcho\": " + echo + ",");
			header += ("\"iTotalRecords\": " + totalSize + ",");
			header += "\"iTotalDisplayRecords\": " + displaySize + ",";
			header += "\"asFeatureNames\": [";
			header += ("\"" + storedTargetType + "\",");
			if (featList.size() > 0) {
				Iterator<Measurement> featIt = featList.iterator();
				while (featIt.hasNext()) {
					header += ("\"" + featIt.next().getName() + "\"");
					header += ",";
				}
			}
			header = header.substring(0, header.length() - 1);
			header += "],";
			header += "\"aiFeatureIds\": [";
			header += ("\"0\",");
			if (featList.size() > 0) {
				Iterator<Measurement> featIt = featList.iterator();
				while (featIt.hasNext()) {
					header += ("\"" + featIt.next().getId() + "\"");
					header += ",";
				}
			}
			header = header.substring(0, header.length() - 1);
			header += "],";
			header += "\"asFilterTerms\": [";
			if (filterTermList.size() > 0) {
				for (String term : filterTermList) {
					header += ("\"" + term + "\"");
					header += ",";
				}
				header = header.substring(0, header.length() - 1);
			}
			header += "],";
			
			header += "\"bAddFeature\": " + addFeature + ",";
			header += "\"iFeatureCol\": " + (nrOfFeatures + 1) + ",";
			header += "\"iDisplayStart\": " + start + ",";
			header += "\"iDisplayLength\": " + length + ",";
			header += "\"aaData\": [";
			
			String body = "";
			// Ouput body:
			for (int targetCounter = 0; targetCounter < resultSize; targetCounter++) {
				String currentLine;
				currentLine = "[";
				currentLine += ("\"" + targetIds[targetCounter] + "\",");
				currentLine += ("\"" + targetNames[targetCounter] + "\"");
				int featureCounter = 0;
				Iterator<Measurement> featIt = featList.iterator();
				while (featIt.hasNext()) {
					Measurement currentFeature = featIt.next();
					// Find out what the unit is:
					String dataType = currentFeature.getDataType();
					currentLine += ",\"";
					ObservedValue[] valueArray = matrixPart[targetCounter][featureCounter];
					if (valueArray != null) {
						if (valueArray.length > 0) {
							for (int valueCounter = 0; valueCounter < valueArray.length; valueCounter++) {
								ObservedValue currentValue = valueArray[valueCounter];
								if (currentValue != null) {
									// Get the real value:
									String valueToAdd = currentValue.getValue();
									if (dataType != null && dataType.equals("xref")) {
										// If so, find the corresponding target name:
										valueToAdd = currentValue.getRelation_Name();
									}
									// Make neat output:
									currentLine += valueToAdd;
									if (printValInfo == true) {
										currentLine += (" (valid from: " + currentValue.getTime());
										if (currentValue.getEndtime() != null) {
											currentLine += (" through: " + currentValue.getEndtime());
										}
										currentLine += ")";
									}
									currentLine += "<br />";
									
									if (limitVal) {
										break; // show only most recent
									}
								}
							}
						}
					}
					currentLine += "\"";
					featureCounter++;
				}
				
				for (int i = featureCounter; i < totalNrOfFeatures; i++) {
					currentLine += ",\"\"";
				}
				currentLine += "],";
				body += currentLine;
			}  
			// End output:
			String tmpBody = "";
			if (body.length() > 0) tmpBody = body.substring(0, body.length() - 1);
			body = tmpBody + "]}";
			
			out.print(header + body);
			out.flush();
			
			logger.debug("JSON output: " + header + body);
			logger.info("serving " + request.getRequestURI());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
