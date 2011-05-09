package plugins.listplugin;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

import commonservice.CommonService;

public class PhenoMatrix extends Matrix<ObservedValue> {

	private Database db = null;
	private List<ObservationTarget> targetList;
	private List<Integer> targetIdList;
	private List<Measurement> featureList;
	private List<Integer> featureIdList;
	private List<Measurement> allFeatureList;
	private ObservedValue[][][] data;
	private int nrOfTargets;
	private int nrOfFeatures;
	private int totalNrOfFeatures;
	private CommonService cq = CommonService.getInstance();
	
	public Database getDatabase() {
		return db;
	}
	
	public void init(Database db, String targetType) throws DatabaseException, ParseException {
		this.db = db;
		cq.setDatabase(db);
		
		Query<ObservationTarget> q = db.query(ObservationTarget.class);
		if (!targetType.equals("All")) {
			q.addRules(new QueryRule(ObservationTarget.__TYPE, Operator.EQUALS, targetType));
		}
		targetList = q.find();
		targetIdList = new ArrayList<Integer>();
		for (ObservationTarget target : targetList) {
			targetIdList.add(target.getId());
		}
		featureList = new ArrayList<Measurement>();
		featureIdList = new ArrayList<Integer>();
		allFeatureList = db.query(Measurement.class).find();
		totalNrOfFeatures = allFeatureList.size();
		if (totalNrOfFeatures == 0) {
			throw new DatabaseException("No features found in database");
		}
		nrOfTargets = targetList.size();
		nrOfFeatures = 0;
		data = new ObservedValue[nrOfTargets][totalNrOfFeatures][];
	}
	
	public int addRemFeature(int featureId) throws DatabaseException, ParseException {
		Query<Measurement> q = db.query(Measurement.class);
		q.addRules(new QueryRule("id", Operator.EQUALS, featureId));
		List<Measurement> featList = q.find();
		Measurement feat = featList.get(0);
		if (featureIdList.contains(featureId)) {
			int colNr = remCol(feat);
			return colNr;
		} else {
			addCol(feat);
			return -1;
		}
	}
	
	public void addCol(Measurement feat) throws DatabaseException, ParseException {
		nrOfFeatures++;
		featureIdList.add(feat.getId());
		featureList.add(feat);
		Integer[][] size = new Integer[nrOfTargets][totalNrOfFeatures];
		int colNr = allFeatureList.indexOf(feat);
		List<Integer> seenTargetLocs = new ArrayList<Integer>();
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, feat.getId()));
		q.addRules(new QueryRule(Operator.SORTDESC, "time"));
		List<ObservedValue> valueList = q.find();
		if (valueList != null && valueList.size() > 0) {
			for (ObservedValue value : valueList) {
				int targetLoc = targetIdList.indexOf(value.getTarget());
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
	
	public int remCol(Measurement feat) {
		int colNr = featureList.indexOf(feat) + 1;
		int featNr = allFeatureList.indexOf(feat);
		featureList.remove(feat);
		featureIdList.remove(feat.getId());
		for (int t = 0; t < nrOfTargets; t++) {
			data[t][featNr] = null;
		}
		nrOfFeatures--;
		return colNr;
	}
	
	public List<Integer> search(String term, boolean limitVal) {
		boolean hit;
		List<Integer> returnList = new ArrayList<Integer>();
		
		// Split search term
		String[] terms = term.split("\\sOR\\s");
		for (int tid = 0; tid < targetList.size(); tid++) {
			hit = false;
			for (String currentTerm : terms) {
				// First, search in target label. If match, go to next.
				try {
					if (cq.getObservationTargetLabel(targetIdList.get(tid)).contains(currentTerm)) {
						returnList.add(tid);
					}
				} catch (Exception e) {
					if (targetList.get(tid).getName().contains(currentTerm)) {
						returnList.add(tid);
					}
				}
				
				for (Measurement feat : featureList) {
					int fid = allFeatureList.indexOf(feat);
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
	
	public List<Integer> filterColumn(int colNr, String term, boolean limitVal) {
		List<Integer> returnList = new ArrayList<Integer>();
		
		// Split filter terms
		String[] terms = term.split("\\sOR\\s");
		for (int tid = 0; tid < targetList.size(); tid++) {
			for (String currentTerm : terms) {
				if (colNr == 0) {
					// First, search in target label. If match, go to next.
					try {
						if (cq.getObservationTargetLabel(targetIdList.get(tid)).contains(currentTerm)) {
							returnList.add(tid);
						}
					} catch (Exception e) {
						if (targetList.get(tid).getName().contains(currentTerm)) {
							returnList.add(tid);
						}
					}
				} else {
					Measurement feat = featureList.get(colNr - 1);
					int fid = allFeatureList.indexOf(feat);
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
				int fpos = allFeatureList.indexOf(feat);
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
		return totalNrOfFeatures;
	}

	public int getNrOfFeatures() {
		return nrOfFeatures;
	}
	
	public List<Measurement> getFeatureList() {
		return featureList;
	}

	public String[] getTargetNames(int[] idx) {
		String[] returnData = new String[idx.length];
		List<Integer> idList = new ArrayList<Integer>();
		
		// Get ID's for targets that are to be shown
		for (int i = 0; i < idx.length; i++) {
			if (idx[i] < nrOfTargets && targetList.get(idx[i]) != null) {
				idList.add(targetIdList.get(idx[i]));
			} else {
				break;
			}
		}
		
		try {
			// Get custom labels (or names, if there are none) for the targets
			for (int i = 0; i < idx.length; i++) {
				returnData[i] = cq.getObservationTargetLabel(targetIdList.get(idx[i]));
			}
		} catch (Exception e) {
			// On failure (unlikely!), use normal names instead
			for (int i = 0; i < idx.length; i++) {
				returnData[i] = targetList.get(idx[i]).getName();
			}
		}
		
		return returnData;
	}
	
	public List<Integer> getAllIndices() {
		List<Integer> returnList = new ArrayList<Integer>();
		for (int tid = 0; tid < targetList.size(); tid++) {
			returnList.add(tid);
		}
		return returnList;
	}
	
	/**
	 * Removes all feature columns from the matrix
	 */
	public void remAllFeatures() {
		for (Measurement m : featureList) {
			remCol(m);
		}
	}

}
