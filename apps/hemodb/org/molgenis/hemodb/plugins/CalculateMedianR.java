package org.molgenis.hemodb.plugins;

import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.RScript;
import org.molgenis.util.RScriptException;

public class CalculateMedianR {

	public static List<String> calculateMedian(Database db, String geneExp,
			List<String> sampleNamesGroup1, List<String> sampleNamesGroup2,
			double signifCutoff, List<String> allProbes) {

		try {
			// Put all the samples from the 2 groups together
			List<String> allSampleNames = new ArrayList<String>();
			allSampleNames.addAll(sampleNamesGroup1);
			allSampleNames.addAll(sampleNamesGroup2);

			// get data set
			Data dataSet = db.query(Data.class).eq(Data.NAME, geneExp).find()
					.get(0);

			// load matrix
			DataMatrixHandler dmh = new DataMatrixHandler(db);
			DataMatrixInstance instance = (BinaryDataMatrixInstance) dmh
					.createInstance(dataSet, db);

			// slice part out of dataset
			DataMatrixInstance selection = instance.getSubMatrix(allProbes,
					allSampleNames);

			// create RScript
			RScript script = new RScript();

			// execute
			script.append("1+2");
			script.append("data<-" + selection.getAsRobject(true));
			script.append("rowMeans(data)");
			script.execute();

			// do something with result
			System.out.println("result:" + script.getResult());

			return null;
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
