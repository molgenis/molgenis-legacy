package plugins.listplugin;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;

import commonservice.CommonService;

/**
 * This is the OLD implementation of the Matrix interface for the List Plugin.
 * Not to be confused with the org.molgenis.matrix.component.PhenoMatrix!
 * 
 * @author erikroos
 *
 */
public class PhenoMatrix extends Matrix<ObservedValue> {

	private List<Integer> targetIdList;
	private List<Measurement> featureList;
	private List<Integer> featureIdList;
	private List<Measurement> allMeasurementList;
	private ObservedValue[][][] data;
	private int nrOfTargets;
	private int nrOfFeatures;
	private int totalNrOfMeasurements;
	private List<Integer> investigationIds;
	private CommonService cq = null;
	private boolean init = false;
	
	public void init(Database db, String targetType, String userName) throws DatabaseException, ParseException {
		this.setInit(true);
		
		cq = CommonService.getInstance();
		cq.setDatabase(db);
		
		if (targetType.equals("All")) {
			targetType = null;
		}
		investigationIds = cq.getAllUserInvestigationIds(userName);
		targetIdList = cq.getAllObservationTargetIds(targetType, false, investigationIds);
		nrOfTargets = targetIdList.size();
		
		featureList = new ArrayList<Measurement>();
		featureIdList = new ArrayList<Integer>();
		allMeasurementList = cq.getAllMeasurements(investigationIds);
		totalNrOfMeasurements = allMeasurementList.size();
		if (totalNrOfMeasurements == 0) {
			throw new DatabaseException("No measurements found in database");
		}
		nrOfFeatures = 0;
		
		data = new ObservedValue[nrOfTargets][totalNrOfMeasurements][];
	}
	
	public int addRemFeature(Database db, int featureId) throws DatabaseException, ParseException {
		cq.setDatabase(db);
		Measurement meas = cq.getMeasurementById(featureId);
		if (featureIdList.contains(featureId)) {
			int colNr = remCol(db, meas);
			return colNr;
		} else {
			addCol(db, meas);
			return -1;
		}
	}
	
	public void addCol(Database db, Measurement meas) throws DatabaseException, ParseException {
		cq.setDatabase(db);
		int measurementId = meas.getId();
		
		nrOfFeatures++;
		featureIdList.add(measurementId);
		featureList.add(meas);
		Integer[][] size = new Integer[nrOfTargets][totalNrOfMeasurements];
		int colNr = allMeasurementList.indexOf(meas);
		List<Integer> seenTargetLocs = new ArrayList<Integer>();
		List<ObservedValue> valueList = cq.getAllObservedValues(measurementId, investigationIds);
		if (valueList != null && valueList.size() > 0) {
			for (ObservedValue value : valueList) {
				int targetLoc = targetIdList.indexOf(value.getTarget_Id());
				if (targetLoc > -1) {
					if (seenTargetLocs.contains(targetLoc)) {
						data[targetLoc][colNr][size[targetLoc][colNr]] = value;
						size[targetLoc][colNr]++;
					} else {
						data[targetLoc][colNr] = new ObservedValue[100]; // TODO: get rid of this very nasty hardcoded array length!!!
						data[targetLoc][colNr][0] = value;
						size[targetLoc][colNr] = 1;
						seenTargetLocs.add(targetLoc);
					}
				}
			}
		}
	}
	
	public int remCol(Database db, Measurement feat) {
		cq.setDatabase(db);
		int colNr = featureList.indexOf(feat) + 1;
		int featNr = allMeasurementList.indexOf(feat);
		featureList.remove(feat);
		featureIdList.remove(feat.getId());
		for (int t = 0; t < nrOfTargets; t++) {
			data[t][featNr] = null;
		}
		nrOfFeatures--;
		return colNr;
	}
	
	public List<Integer> search(Database db, String term, boolean limitVal) {
		cq.setDatabase(db);
		
		boolean hit;
		List<Integer> returnList = new ArrayList<Integer>();
		
		// Split search term
		String[] terms = term.split("\\sOR\\s");
		for (int tid = 0; tid < targetIdList.size(); tid++) {
			hit = false;
			for (String currentTerm : terms) {
				// First, search in target label. If match, go to next.
				try {
					if (cq.getObservationTargetLabel(targetIdList.get(tid)).contains(currentTerm)) {
						returnList.add(tid);
					}
				} catch (Exception e) {
					// Impossible, ignore for now
				}
				
				for (Measurement feat : featureList) {
					int fid = allMeasurementList.indexOf(feat);
					ObservedValue[] valArray = data[tid][fid];
					if (valArray == null) {
						continue;
					}
					int valMax = valArray.length;
					if (limitVal == true) {
						valMax = 1;
					}
					for (int v = 0; v < valMax; v++) {
						ObservedValue currentValue = valArray[v];
						if (currentValue != null) {
							// Get the real value:
							String valueToCheck = currentValue.getValue();
							if (feat.getDataType().equals("xref")) {
								// If so, find the corresponding target name:
								valueToCheck = currentValue.getRelation_Name();
							}
							// The test itself:
							if (valueToCheck.contains(currentTerm)) {
								returnList.add(tid);
								hit = true;
								break;
							}
						}
					}
					if (hit == true) { // no need to look at the other features
						hit = false;
						break;
					}
				}
			}
		}
		return returnList;
	}
	
	public List<Integer> filterColumn(Database db, int colNr, String term, boolean limitVal) {
		cq.setDatabase(db);
		
		List<Integer> returnList = new ArrayList<Integer>();
		
		// Split filter terms
		String[] terms = term.split("\\sOR\\s");
		for (int tid = 0; tid < targetIdList.size(); tid++) {
			for (String currentTerm : terms) {
				if (colNr == 0) {
					// First, search in target label. If match, go to next.
					try {
						if (cq.getObservationTargetLabel(targetIdList.get(tid)).contains(currentTerm)) {
							returnList.add(tid);
						}
					} catch (Exception e) {
						// Impossible, ignore for now
					}
				} else {
					Measurement feat = featureList.get(colNr - 1);
					int fid = allMeasurementList.indexOf(feat);
					ObservedValue[] valArray = data[tid][fid];
					if (valArray == null) {
						continue;
					}
					int valMax = valArray.length;
					if (limitVal == true) {
						valMax = 1;
					}
					for (int v = 0; v < valMax; v++) {
						ObservedValue currentValue = valArray[v];
						if (currentValue != null) {
							// Get the real value:
							String valueToCheck = currentValue.getValue();
							if (feat.getDataType().equals("xref")) {
								// If so, find the corresponding target name:
								valueToCheck = currentValue.getRelation_Name();
							}
							// The test itself:
							if (valueToCheck != null && valueToCheck.contains(currentTerm)) {
								returnList.add(tid);
								break;
							}
						}
					}
				}
			}
		}
		return returnList;
	}
	
	@Override
	public File getAsFile() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObservedValue>[] getCol(int colIndex) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObservedValue> getElement(int rowIndex, int colIndex) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setElement(int rowIndex, int colIndex, List<ObservedValue> element) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<ObservedValue>[][] getElements() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservedValue[][] getRow(int rowIndex) throws Exception {
		return data[rowIndex];
	}
	
	public ObservedValue[][][] getRows(int[] rowIndices) throws Exception {
		ObservedValue[][][] returnData = new ObservedValue[rowIndices.length][nrOfFeatures][];
		for (int i = 0; i < rowIndices.length; i++) {
			if (rowIndices[i] >= nrOfTargets) {
				break;
			}
			int rpos = 0;
			for (int j = 0; j < nrOfFeatures; j++) {
				Measurement feat = featureList.get(j);
				int fpos = allMeasurementList.indexOf(feat);
				returnData[i][rpos] = data[rowIndices[i]][fpos];
				rpos++;
			}
		}
		return returnData;
	}

	@Override
	public Matrix<ObservedValue> getSubMatrix(int[] rowIndices, int[] colIndices)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix<ObservedValue> getSubMatrix(int row, int nRows, int col, int nCols)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public int getTotalNrOfFeatures() {
		return totalNrOfMeasurements;
	}

	public int getNrOfFeatures() {
		return nrOfFeatures;
	}
	
	public int getNrOfTargets() {
		return nrOfTargets;
	}
	
	public List<Measurement> getFeatureList() {
		return featureList;
	}

	public String[] getTargetNames(Database db, int[] idx) {
		cq.setDatabase(db);
		
		String[] returnData = new String[idx.length];
		
		try {
			// Get custom labels (or names, if there are none) for the targets that are to be shown
			for (int i = 0; i < idx.length; i++) {
				returnData[i] = cq.getObservationTargetLabel(targetIdList.get(idx[i]));
			}
		} catch (Exception e) {
			// Impossible, ignore for now
		}
		
		return returnData;
	}
	
	public Integer[] getTargetIds(int[] idx) {
		Integer[] idList = new Integer[idx.length];
		
		// Get ID's for targets that are to be shown
		for (int i = 0; i < idx.length; i++) {
			if (idx[i] < nrOfTargets && targetIdList.get(idx[i]) != null) {
				idList[i] = targetIdList.get(idx[i]);
			} else {
				break;
			}
		}
		
		return idList;
	}
	
	public List<Integer> getAllIndices() {
		List<Integer> returnList = new ArrayList<Integer>();
		for (int tid = 0; tid < nrOfTargets; tid++) {
			returnList.add(tid);
		}
		return returnList;
	}
	
	/**
	 * Removes all feature columns from the matrix
	 */
	public void remAllFeatures(Database db) {
		for (Measurement m : featureList) {
			remCol(db, m);
		}
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

}
