package plugins.listplugin;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;


public class PhenoMatrix2 extends Matrix<ObservedValue> {

	private Database db = null;
	private List<ObservationTarget> targetList;
	private List<Measurement> featureList;
	private List<Measurement> allFeatureList;
	private List<Integer> featureIdList;
	private ObservedValue[][][] data;
	private int nrOfTargets;
	private int nrOfFeatures;
	private int totalNrOfFeatures;
	
	public Database getDatabase() {
		return db;
	}
	
	public void init(Database db, String targetType) throws DatabaseException, ParseException {
		this.db = db;
		
		Query<ObservationTarget> q = db.query(ObservationTarget.class);
		q.addRules(new QueryRule(ObservationTarget.ONTOLOGYREFERENCE_NAME, Operator.EQUALS, targetType));
		targetList = q.find();
		featureList = new ArrayList<Measurement>();
		featureIdList = new ArrayList<Integer>();
		allFeatureList = db.query(Measurement.class).find();
		totalNrOfFeatures = allFeatureList.size();
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
		int colNr = allFeatureList.indexOf(feat);
		for (int t = 0; t < nrOfTargets; t++) {
			ObservationTarget currentTarget = targetList.get(t);
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, currentTarget.getId()));
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, feat.getId()));
			q.addRules(new QueryRule(Operator.SORTDESC, "time"));
			List<ObservedValue> valueList = q.find();
			if (valueList != null && valueList.size() > 0) {
				Iterator<ObservedValue> valueIterator = valueList.iterator();
				data[t][colNr] = new ObservedValue[valueList.size()];
				int v = 0;
				while (valueIterator.hasNext()) {
					data[t][colNr][v] = valueIterator.next();
					v++;
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
	
	public List<Integer> search(String term) {
		boolean hit;
		List<Integer> returnList = new ArrayList<Integer>();
		for (int tid = 0; tid < targetList.size(); tid++) {
			hit = false;
			// First, search in target name. If match, go to next.
			if (targetList.get(tid).getName().contains(term)) {
				returnList.add(tid);
				continue;
			}
			for (Measurement feat : featureList) {
				int fid = allFeatureList.indexOf(feat);
				ObservedValue[] valArray = data[tid][fid];
				if (valArray == null) {
					continue;
				}
				for (int v = 0; v < valArray.length; v++) {
					ObservedValue currentValue = valArray[v];
					// Get the real value:
					String valueToCheck = currentValue.getValue();
					if (feat.getDataType().equals("xref")) {
						// If so, find the corresponding target name:
						valueToCheck = currentValue.getRelation_Name();
					}
					// The test itself:
					String[] terms = term.split("\\sOR\\s");
					for (String currentTerm : terms) {
						if (valueToCheck.contains(currentTerm)) {
							returnList.add(tid);
							hit = true;
							break;
						}
					}
					if (hit == true) break;
				}
				if (hit == true) { // no need to look at the other features
					hit = false;
					break;
				}
			}
			
		}
		return returnList;
	}
	
	public List<Integer> filterColumn(int colNr, String term) {
		List<Integer> returnList = new ArrayList<Integer>();
		for (int tid = 0; tid < targetList.size(); tid++) {
			if (colNr == 0) {
				// Search target name. If match, go to next.
				if (targetList.get(tid).getName().contains(term)) {
					returnList.add(tid);
				}
				continue;
			}
			Measurement feat = featureList.get(colNr - 1);
			int fid = allFeatureList.indexOf(feat);
			ObservedValue[] valArray = data[tid][fid];
			if (valArray == null) {
				continue;
			}
			for (int v = 0; v < valArray.length; v++) {
				ObservedValue currentValue = valArray[v];
				// Get the real value:
				String valueToCheck = currentValue.getValue();
				if (feat.getDataType().equals("xref")) {
					// If so, find the corresponding target name:
					valueToCheck = currentValue.getRelation_Name();
				}
				// The test itself:
				String[] terms = term.split("\\sOR\\s");
				for (String currentTerm : terms) {
					if (valueToCheck.contains(currentTerm)) {
						returnList.add(tid);
						break;
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
		for (int i = 0; i < idx.length; i++) {
			if (idx[i] < nrOfTargets && targetList.get(idx[i]) != null) {
				returnData[i] = targetList.get(idx[i]).getName();
			} else {
				break;
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

}
