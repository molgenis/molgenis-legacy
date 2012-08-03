package org.molgenis.hemodb.plugins;

import java.util.Arrays;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.util.RScript;

import app.DatabaseFactory;

public class MyFirstR {

	public static void main(String[] args) throws Exception
	{
		Database db = DatabaseFactory.create("apps/hemodb/org/molgenis/hemodb/hemodb.properties");
		
		//get data set
		String dataName = "expDataLog2Quan";
		Data dataSet = db.query(Data.class).eq(Data.NAME, dataName).find().get(0);
		
		//load matrix
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		DataMatrixInstance instance = (BinaryDataMatrixInstance) dmh.createInstance(dataSet, db);
		
		//start with list of probes
		List<String> probes = Arrays.asList("p_2570615","p_6370619","p_2650615","p_5340672","p_2370438");
		List<String> samples = Arrays.asList("X5304185005_D");
		
		//slice part out of dataset
		DataMatrixInstance selection = instance.getSubMatrix(probes,samples);
		
		//create RScript
		RScript script = new RScript();
		
		//execute
		script.append("1+2");
		script.append("data<-"+selection.getAsRobject(true));
		script.append("rowMeans(data)");
		script.execute();
		
		//do something with result
		System.out.println("result:"+script.getResult());
	
	}
}
