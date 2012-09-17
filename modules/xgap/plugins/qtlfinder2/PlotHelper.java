package plugins.qtlfinder2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.cluster.DataName;
import org.molgenis.cluster.DataSet;
import org.molgenis.cluster.DataValue;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.util.Entity;
import org.molgenis.xgap.Locus;
import org.molgenis.xgap.Marker;

import plugins.qtlfinder.QTLInfo;
import plugins.qtlfinder.QtlFinder;
import plugins.qtlfinder.QtlPlotDataPoint;
import plugins.reportbuilder.Statistics;
import plugins.rplot.MakeRPlot;

public class PlotHelper
{

	public static List<QTLInfo> createQTLReportFor(Entity entity, int plotWidth, int plotHeight, Database db) throws Exception
	{
		List<QTLInfo> result = new ArrayList<QTLInfo>();
		
		List<Data> allData = db.find(Data.class);
		
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		
		//List<MatrixLocation> matrixLocations = new ArrayList<MatrixLocation>();
		for(Data d : allData)
		{
			
			//if something fails with this matrix, don't break the loop
			//e.g. backend file is missing
			try{

			
			//loop over Text data (can't be QTL)
			if(d.getValueType().equals("Text")){
				continue;
			}
			//match row/col type of the matrix to the type of entity queried
			//and one of the dimensions is Marker
			if((d.getTargetType().equals(entity.get(Field.TYPE_FIELD)) || d.getFeatureType().equals(entity.get(Field.TYPE_FIELD)))
				&&
				(d.getTargetType().equals("Marker") || d.getFeatureType().equals("Marker")))
			{
				
				//create instance and get name of the row/col we want
				DataMatrixInstance instance = dmh.createInstance(d, db);
				String name = entity.get(ObservableFeature.NAME).toString();
			
				long locus;
				if(entity instanceof Locus)
				{
					locus = ((Locus)entity).getBpStart();
				}
				else
				{
					locus = 0;
				}
				
				//find out if the name is in the row or col names
				List<String> rowNames = instance.getRowNames();
				List<String> colNames = instance.getColNames();
				int rowIndex = rowNames.indexOf(name);
				int colIndex = colNames.indexOf(name);
				
				//if its in row, do row stuff
				if(rowIndex != -1)
				{
					Double[] Dvalues = Statistics.getAsDoubles(instance.getRow(rowIndex));
					Double[] absDvalues = Statistics.getAsAbsDoubles(instance.getRow(rowIndex));
					int maxIndex = Statistics.getIndexOfMax(absDvalues);
					double peakDouble = Dvalues[maxIndex];
					
					String peakMarker = colNames.get(maxIndex);
					List<Double> DvaluesList = Arrays.asList(Dvalues);
					
					QTLInfo qtl = new QTLInfo(d, peakMarker, peakDouble, colNames, DvaluesList);
					
					HashMap<String, Marker> markerInfo = QtlFinder.getMarkerInfo(colNames, db);
					qtl.setMarkerAnnotations(markerInfo);
					
					try{
						File img;
						TreeMap<Long, QtlPlotDataPoint> data = QtlFinder.sortQtlPlotData(colNames, DvaluesList, markerInfo);
					
						if(isEffectSizeData(db, d))
						{
							img = new MakeRPlot().qtlPlot(name, data, locus, plotWidth, plotHeight,"Effect size", "eff");
						}
						else
						{
							img = new MakeRPlot().qtlPlot(name, data, locus, plotWidth, plotHeight,"LOD score", "qtl");
						}
						
						qtl.setPlot(img.getName());	
					}
					catch(Exception e)
					{
						e.printStackTrace();
						//too bad, image failed
					}
					
					result.add(qtl);
					
				}
				
				//if its in col, and not in row, do col stuff
				//we assume its not in row and col at the same time, then its not QTL data but correlations or so
				if(rowIndex == -1 && colIndex != -1)
				{
					Double[] Dvalues = Statistics.getAsDoubles(instance.getCol(colIndex));
					Double[] absDvalues = Statistics.getAsAbsDoubles(instance.getCol(colIndex));
					int maxIndex = Statistics.getIndexOfMax(absDvalues);
					double peakDouble = Dvalues[maxIndex];
					
					String peakMarker = rowNames.get(maxIndex);
					List<Double> DvaluesList = Arrays.asList(Dvalues);
					
					QTLInfo qtl = new QTLInfo(d, peakMarker, peakDouble, rowNames, DvaluesList);
					
					HashMap<String, Marker> markerInfo = QtlFinder.getMarkerInfo(rowNames, db);
					qtl.setMarkerAnnotations(markerInfo);
					
					try{
						File img;
						TreeMap<Long, QtlPlotDataPoint> data = QtlFinder.sortQtlPlotData(rowNames, DvaluesList, markerInfo);
						
						if(isEffectSizeData(db, d))
						{
							img = new MakeRPlot().qtlPlot(name, data, locus, plotWidth, plotHeight,"Effect size", "eff");
						}
						else
						{
							img = new MakeRPlot().qtlPlot(name, data, locus, plotWidth, plotHeight,"LOD score", "qtl");
						}
						
						qtl.setPlot(img.getName());
					}
					catch(Exception e)
					{
						e.printStackTrace();
						//too bad, image failed
					}
					
					result.add(qtl);
					
				}
			}
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//too bad, data matrix failed
			}
			
			
			
		}
		
		return result;
	}
	
	public static boolean isEffectSizeData(Database db, Data data) throws DatabaseException
	{
		List<DataSet> dsRef = db.find(DataSet.class, new QueryRule(DataSet.NAME, Operator.EQUALS, "Default_tags"));
		
		if(dsRef.size() != 1)
		{
			throw new DatabaseException("0 or >1 results when querying dataset");
		}
		
		Query<DataName> q = db.query(DataName.class);
		q.addRules(new QueryRule(DataName.NAME, Operator.EQUALS, "Effect_size"));
		q.addRules(new QueryRule(DataName.DATASET, Operator.EQUALS, dsRef.get(0).getId()));
		List<DataName> dnRef = q.find();
		
		if(dnRef.size() != 1)
		{
			throw new DatabaseException("0 or >1 results when querying dataname");
		}
		
		Query<DataValue> q2 = db.query(DataValue.class);
		q2.addRules(new QueryRule(DataValue.DATANAME, Operator.EQUALS, dnRef.get(0).getId()));
		q2.addRules(new QueryRule(DataValue.VALUE, Operator.EQUALS, data.getId()));
		List<DataValue> dvRef = q2.find();
		
		if(dvRef.size() == 0)
		{
			return false;
		}
		else{
			return true;
		}
	}
	
}
