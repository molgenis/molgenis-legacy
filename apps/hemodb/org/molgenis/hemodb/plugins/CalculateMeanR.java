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

public class CalculateMeanR {

	public static List<String> calculateMean(Database db, String geneExp,
			List<String> sampleNamesGroup1, List<String> sampleNamesGroup2,
			double signifCutoff, List<String> allProbes) {

		try {

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
			DataMatrixInstance group1Selection = instance.getSubMatrix(
					allProbes, sampleNamesGroup1);
			DataMatrixInstance group2Selection = instance.getSubMatrix(
					allProbes, sampleNamesGroup2);

			// create RScript
			RScript script = new RScript();

			// execute
			script.append("compareExpression <- function(data, col1, col2){compare <- data[,col1]-data[,col2]");
			script.append("return(compare)}");
			script.append("geneExpressionDataSetGroupOne <-"
					+ group1Selection.getAsRobject(true));
			script.append("geneExpressionDataSetGroupTwo <-"
					+ group2Selection.getAsRobject(true));

			script.append("rowMeansGroupOne <- as.matrix(rowMeans(geneExpressionDataSetGroupOne))");
			script.append("rowMeansGroupTwo <- as.matrix(rowMeans(geneExpressionDataSetGroupTwo))");

			script.append("meanDataSet <- cbind(rowMeansGroupOne,rowMeansGroupTwo)");
			script.append("rownames(meanDataSet) <- row.names(geneExpressionDataSetGroupOne)");

			script.append("geneExpressionTest <- as.matrix(compareExpression(meanDataSet,1,2))");
			script.append("rownames(geneExpressionTest <- row.names(geneExpressionDataSetGroupOne)");

			script.append("significance <- geneExpressionTest >="
					+ signifCutoff + " | geneExpressionTest <= -"
					+ signifCutoff);
			script.append("significantProbes <- as.matrix(geneExpressionTest[significance])");

			script.append("index <- which(significance[,1]==TRUE)");
			script.append("result <- significance[index,]");

			script.append("probeNames <- names(result)");
			script.append("return(probeNames)");

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
