/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.qtlfinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Gene;
import org.molgenis.xgap.Locus;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Probe;

import plugins.reportbuilder.Statistics;
import plugins.rplot.MakeRPlot;

public class QtlFinder extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1L;

	private QtlFinderModel model = new QtlFinderModel();
	//private DataMatrixHandler dmh = null;
	
	private int plotWidth = 1024;
	private int plotHeight = 768;

	public QtlFinderModel getMyModel()
	{
		return model;
	}

	public QtlFinder(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "QtlFinder";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/qtlfinder/QtlFinder.ftl";
	}

	private List<String> getInput(String query) throws Exception
	{
		//refresh results
		this.model.setResultSet(new HashMap<String, Result>());
		this.model.setQmpr(null);

		//works for unix and windows (http://stackoverflow.com/questions/454908/split-java-string-by-new-line)
		String lines[] = query.split("\\r?\\n");
		
		List<String> uniqueInputs = new ArrayList<String>();

		//checks
		for(String findMe : lines)
		{
			findMe = findMe.trim();
			
			if(findMe.equals(""))
			{
				throw new Exception("Empty input line is not allowed");
			}
			
			if(!uniqueInputs.contains(findMe))
			{
				uniqueInputs.add(findMe);
			}
			else
			{
				throw new Exception("Non unique input string '"+findMe+"' not allowed");
			}
		}
		
		return uniqueInputs;
	}
	
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
			try
			{
				
				//on all actions: set threshold, query and data filter
				Double threshold = request.getDouble("threshold");
				this.model.setThreshold(threshold);
				
				String query = request.getString("findme");
				this.model.setQuery(query);
				
				QueryRule dataFilter = makeDataFilter(request); //also sets tickboxes
				
				//NEW: multiple QTL's in a single plot
				if (action.equals("findQtlMulti"))
				{
					//get inputs from box
					List<String> uniqueInputs = getInput(query);
					
					//match all inputs to database and create 1 plot with everything
					List<Entity> entities = new ArrayList<Entity>();
					for(String findMe : uniqueInputs)
					{
						entities.addAll(findInGenesAndProbes(findMe, db));
					}
					
					createQTLMultiPlotReportFor(entities, threshold, dataFilter, query, db);
					
					
				}
				
				//old style: 1 QTL per plot
				if (action.equals("findQtl"))
				{
				
					//get inputs from box
					List<String> uniqueInputs = getInput(query);
					
					//for each input, create a QTL report or disambiguation view
					for(String findMe : uniqueInputs)
					{

						Result r = new Result();
						
						r.setSelectedName(findMe);
						
						List<Entity> result = findInGenesAndProbes(findMe, db);
						
						if(result.size() == 0)
						{
							r.setNoResultsFound(true);
						}
						else if(result.size() == 1)
						{
							r.setResult(result.get(0));
							
							List<QTLInfo> qtls = createQTLReportFor(result.get(0), threshold, dataFilter, db);
							r.setQtlsFound(qtls);
						}
						else
						{
							r.setDisambiguate(result);
						}
						this.model.getResultSet().put(findMe, r);
					}

				}
				else if(action.equals("disambig"))
				{	
					//disambiguate one resultset, but with multi input
					String key = request.getString("__key"); //which resultset
	
					//name and type in a map
					//obtained by splitting input for '@', e.g. 'disambig_option_Y37E31@Gene'
					//OK because '@' are not allowed in names
					HashMap<String,String> chosenItems = new HashMap<String,String>();
					List<String> reqFields = request.getFields();
					for(String s : reqFields)
					{
						if(s.startsWith("disambig_option_"))
						{
							String[] split = s.split("@");
							chosenItems.put(split[0].substring("disambig_option_".length()), split[1]);
						}
					}
					
					//disambiguate terms based on name and type, create all reports and results
					for(String name : chosenItems.keySet())
					{
						Result rs = new Result();
						rs.setSelectedName(name);
						
						Class<? extends Entity> entityClass = db.getClassForName(chosenItems.get(name));
						List<? extends Entity> result = db.find(entityClass, new QueryRule(ObservableFeature.NAME, Operator.EQUALS, name));

						rs.setResult(result.get(0));
						
						List<QTLInfo> qtls = createQTLReportFor(result.get(0), threshold, dataFilter, db);
						rs.setQtlsFound(qtls);
						this.model.getResultSet().put(name, rs);
					}
					
					//remove the old ambiguous set
					this.model.getResultSet().remove(key);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}
	
	private QueryRule makeDataFilter(Tuple request)
	{
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ArrayList<String> names = new ArrayList<String>();
		for(Data d : this.model.getDataSets())
		{
			if(request.getString("dataset_filter_" + d.getId()) != null)
			{
				ids.add(d.getId());
				names.add(d.getName());
			}
		}
		if(ids.size() > 0)
		{
			this.model.setTickedDataSets(names);
			return new QueryRule(Data.ID, Operator.IN, ids);
		}else
		{
			return null;
		}
	}

	private List<QTLInfo> createQTLReportFor(Entity entity, Double threshold, QueryRule dataFilter, Database db) throws Exception
	{
		List<QTLInfo> result = new ArrayList<QTLInfo>();
		
		List<Data> allData;
		if(dataFilter == null)
		{
			allData = db.find(Data.class);
		}
		else
		{
			allData = db.find(Data.class, dataFilter);
		}
		
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
					int maxIndex = Statistics.getIndexOfMax(Dvalues);
					double peakDouble = Dvalues[maxIndex];
					
					if(threshold != null &&peakDouble < threshold.doubleValue())
					{
						continue;
					}
					
					String peakMarker = colNames.get(maxIndex);
					List<Double> DvaluesList = Arrays.asList(Dvalues);
					
					QTLInfo qtl = new QTLInfo(d, peakMarker, peakDouble, colNames, DvaluesList);
					
					HashMap<String, Marker> markerInfo = getMarkerInfo(colNames, db);
					qtl.setMarkerAnnotations(markerInfo);
					
					try{
						File img;
						
						//find out if we can do QTL plot
						//RIGHT NOW THIS IS ALWAYS TRUE
						//MISSING VALUES ARE 'HANDLED' BY THE QTL PLOT
						if(qtlInformationIsComplete(markerInfo, colNames))
						{
							TreeMap<Long, QtlPlotDataPoint> data = sortQtlPlotData(colNames, DvaluesList, markerInfo);
						//	img = MakeRPlot.qtlPlot(name, data, locus, plotWidth, plotHeight, "LOD score", "qtl");
						}
						else
						{
							img = MakeRPlot.plot(d, instance, name, null, "row", "o", plotWidth, plotHeight);
						}
				//		qtl.setPlot(img.getName());
						
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
					int maxIndex = Statistics.getIndexOfMax(Dvalues);
					double peakDouble = Dvalues[maxIndex];
					
					if(threshold != null && peakDouble < threshold.doubleValue())
					{
						continue;
					}
					
					String peakMarker = rowNames.get(maxIndex);
					List<Double> DvaluesList = Arrays.asList(Dvalues);
					
					QTLInfo qtl = new QTLInfo(d, peakMarker, peakDouble, rowNames, DvaluesList);
					
					HashMap<String, Marker> markerInfo = getMarkerInfo(rowNames, db);
					qtl.setMarkerAnnotations(markerInfo);
					
					try{
						File img;
						
						//find out if we can do QTL plot
						//RIGHT NOW THIS IS ALWAYS TRUE
						//MISSING VALUES ARE 'HANDLED' BY THE QTL PLOT
						if(qtlInformationIsComplete(markerInfo, rowNames))
						{
							TreeMap<Long, QtlPlotDataPoint> data = sortQtlPlotData(rowNames, DvaluesList, markerInfo);
					//		img = MakeRPlot.qtlPlot(name, data, locus, plotWidth, plotHeight, "LOD score", "qtl");
						}
						else
						{
							img = MakeRPlot.plot(d, instance, null, name, "col", "o", plotWidth, plotHeight);
						}
						
				//		qtl.setPlot(img.getName());
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
	
	
	
	
	private QTLMultiPlotResult createQTLMultiPlotReportFor(List<Entity> entities, Double threshold, QueryRule dataFilter, String query, Database db) throws Exception
	{
		
		HashMap<String,Entity> matches = new HashMap<String,Entity>();
		
		 // valnr - markername - markerbp - traitname - traitloc - value
		// 1 PVV4 3422 Y34G3 3456 5.424
		List<String> valueListForR = new ArrayList<String>();
		int overallIndex = 1;
		
		List<Data> allData;
		if(dataFilter == null)
		{
			allData = db.find(Data.class);
		}
		else
		{
			allData = db.find(Data.class, dataFilter);
		}
		
		List<String> entityTypes = new ArrayList<String>();
		for(Entity e : entities)
		{
			entityTypes.add(e.get(Field.TYPE_FIELD).toString());
		}
		
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		
		//writer for data table
		File tmpData = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot_data_table_" + System.nanoTime() + ".txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpData));
		
		for(Data d : allData)
		{
			//if something fails with this matrix, don't break the loop
			//e.g. backend file is missing
			try{
			
				//loop over Text data (can't be QTL)
				if(d.getValueType().equals("Text")){
					continue;
				}
				//check if datamatrix target/features matches any entity type
				//and one of the dimensions is Marker
				if(entityTypes.contains(d.getTargetType()) || entityTypes.contains(d.getFeatureType())
					&&
					(d.getTargetType().equals("Marker") || d.getFeatureType().equals("Marker")))
				{
					
					//create instance and get name of the row/col we want
					DataMatrixInstance instance = dmh.createInstance(d, db);
					List<String> rowNames = instance.getRowNames();
					List<String> colNames = instance.getColNames();
					
					//get the markers
					List<Marker> markers = db.find(Marker.class, new QueryRule(Marker.NAME, Operator.IN, d.getFeatureType().equals("Marker") ? colNames : rowNames));
					HashMap<String,Marker> nameToMarker = new HashMap<String,Marker>();
					for(Marker m : markers)
					{
						nameToMarker.put(m.getName(), m);
					}
					
					//for each entity, see if the types match to the matrix
					for(Entity e : entities)
					{
						if(d.getTargetType().equals(e.get(Field.TYPE_FIELD)) || d.getFeatureType().equals(e.get(Field.TYPE_FIELD)))
						{
							//if so, use this entity to 'query' the matrix and store the values
							String name = e.get(ObservableFeature.NAME).toString();
							
							//find out if the name is in the row or col names
							int rowIndex = rowNames.indexOf(name);
							int colIndex = colNames.indexOf(name);
						
							//get trait bp loc
							long locus;
							if(e instanceof Locus)
							{
								locus = ((Locus)e).getBpStart();
							}
							else
							{
								locus = 0;
							}
	
							//if its in row, do row stuff
							if(rowIndex != -1)
							{
								Double[] Dvalues = Statistics.getAsDoubles(instance.getRow(rowIndex));
								int maxIndex = Statistics.getIndexOfMax(Dvalues);
								double peakDouble = Dvalues[maxIndex];
								
								if(threshold != null && peakDouble < threshold.doubleValue())
								{
									continue;
								}
								
								matches.put(name, e);
								
								for(int markerIndex = 0; markerIndex < colNames.size(); markerIndex++)
								{
									if(nameToMarker.containsKey(colNames.get(markerIndex)))
									{
										//String line = "plotMe <- rbind(plotMe, c(" + overallIndex + ", \"" + colNames.get(markerIndex) + "\", " + nameToMarker.get(colNames.get(markerIndex)).getBpStart() + ", \"" + name + "\", " + locus + ", " + Dvalues[markerIndex] + "))";
										//valueListForR.add(line);
										String chrId = nameToMarker.get(colNames.get(markerIndex)).getChromosome_Id() != null ? nameToMarker.get(colNames.get(markerIndex)).getChromosome_Id().toString() : "-";
										bw.write(overallIndex + " \"" + colNames.get(markerIndex) + "\" " + chrId + " " + nameToMarker.get(colNames.get(markerIndex)).getBpStart() + " \"" + name + "\" " + locus + " " + Dvalues[markerIndex]);
										bw.newLine();
										overallIndex++;
									}
								}
							}
							
							//if its in col, and not in row, do col stuff
							//we assume its not in row and col at the same time, then its not QTL data but correlations or so
							if(rowIndex == -1 && colIndex != -1)
							{
								Double[] Dvalues = Statistics.getAsDoubles(instance.getCol(colIndex));
								int maxIndex = Statistics.getIndexOfMax(Dvalues);
								double peakDouble = Dvalues[maxIndex];
	
								if(threshold != null && peakDouble < threshold.doubleValue())
								{
									continue;
								}
								
								matches.put(name, e);
								
								for(int markerIndex = 0; markerIndex < rowNames.size(); markerIndex++)
								{
									if(nameToMarker.containsKey(rowNames.get(markerIndex)))
									{
										//String line = "plotMe <- rbind(plotMe, c(" + overallIndex + ", \"" + rowNames.get(markerIndex) + "\", " + nameToMarker.get(rowNames.get(markerIndex)).getBpStart() + ", \"" + name + "\", " + locus + ", " + Dvalues[markerIndex] + "))";
										//valueListForR.add(line);
										String chrId = nameToMarker.get(rowNames.get(markerIndex)).getChromosome_Id() != null ? nameToMarker.get(rowNames.get(markerIndex)).getChromosome_Id().toString() : "-";
										bw.write(overallIndex + " \"" + rowNames.get(markerIndex) + "\" " + chrId + " " + nameToMarker.get(rowNames.get(markerIndex)).getBpStart() + " \"" + name + "\" " + locus + " " + Dvalues[markerIndex]);
										bw.newLine();
										overallIndex++;
									}
								}
							}
							
							if(matches.size() > 1000)
							{
								break;
							}
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//too bad, data matrix failed
			}
			
			if(matches.size() > 1000)
			{
				throw new Exception("More than 1000 matches to your search query. Please be more specific.");
			}
		}
		
		bw.close();
		
		//TODO: clean up?
//		File plot = MakeRPlot.qtlMultiPlot(tmpData, plotWidth, (matches.size() * 10), query);
//		File cisTransplot = MakeRPlot.qtlCisTransPlot(tmpData, plotWidth, plotHeight, query);
		
		
		QTLMultiPlotResult result = new QTLMultiPlotResult();
//		result.setPlot(plot.getName());
//		result.setCisTransPlot(cisTransplot.getName());
//		result.setMatches(matches);
		
		this.model.setQmpr(result);
		
		return result;
	}
	
	//sort the datapoints to bp position to be plottable
	//use bppos as index to get automatic natural sorting!
	public static TreeMap<Long, QtlPlotDataPoint> sortQtlPlotData(List<String> markers, List<Double> lodscores, HashMap<String, Marker> markerInfo)
	{
		TreeMap<Long, QtlPlotDataPoint> res = new TreeMap<Long, QtlPlotDataPoint>();
		for(int i=0; i < markers.size(); i++)
		{
			long bpPos = markerInfo.get(markers.get(i)).getBpStart();
			String chr = markerInfo.get(markers.get(i)).getChromosome_Name();
			
			QtlPlotDataPoint qd = new QtlPlotDataPoint(lodscores.get(i).doubleValue(), bpPos, chr);
			
			res.put(new Long(bpPos), qd);
		}
		
		return res;
	}
	
	private boolean qtlInformationIsComplete(HashMap<String, Marker> markerInfo, List<String> colNames)
	{
		// TODO Auto-generated method stub
		return true;
	}

	public static HashMap<String, Marker> getMarkerInfo(List<String> colNames, Database db)
	{
		HashMap<String, Marker> result = new HashMap<String, Marker>();
		
		try{
			List<Marker> dbFind = db.find(Marker.class, new QueryRule(Marker.NAME, Operator.IN, colNames));
			for(Marker m : dbFind)
			{
				result.put(m.getName(), m);
			}
		}
		catch(Exception e)
		{
			//too bad, no marker annotations
			e.printStackTrace();
		}
		return result;
	}

	private List<Entity> findInGenesAndProbes(String findMe, Database db) throws DatabaseException
	{
		List<Entity> result = new ArrayList<Entity>();
		for(Gene entity : db.find(Gene.class))
		{
			for (String field : entity.getFields())
			{
				if (entity.get(field) != null && entity.get(field).toString().toLowerCase().contains(findMe.toLowerCase()))
				{
					result.add(entity);
					break;
				}
			}
		}
		for(Probe entity : db.find(Probe.class))
		{
			for (String field : entity.getFields())
			{
				if (entity.get(field) != null && entity.get(field).toString().toLowerCase().contains(findMe.toLowerCase()))
				{
					result.add(entity);
					break;
				}
			}
		}
		return result;	
	}

	@Override
	public void reload(Database db)
	{

		try
		{
			List<Data> dataSets = db.find(Data.class);
			this.model.setDataSets(dataSets);
			
			if(this.model.getResultSet() == null)
			{
				this.model.setResultSet(new HashMap<String, Result>());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
