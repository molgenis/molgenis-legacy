/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.qtlfinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Gene;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Probe;

import plugins.reportbuilder.Statistics;
import plugins.rplot.MakeRPlot;

public class QtlFinder extends PluginModel
{

	private QtlFinderModel model = new QtlFinderModel();
	private DataMatrixHandler dmh = null;

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

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			System.out.println("*** request: " + request.toString());
			String action = request.getString("__action");

			try
			{

			//	model.setResultSet(null);
			//	model.setDisambiguate(null);
			//	model.setNoResultsFound(null);
				
				//on all actions: set threshold, query and data filter
				Double threshold = request.getDouble("threshold");
				this.model.setThreshold(threshold);
				
				String query = request.getString("findme");
				this.model.setQuery(query);
				
				QueryRule dataFilter = makeDataFilter(request); //also sets tickboxes
				
				if (action.equals("findQtl"))
				{
					//refresh results
					this.model.setResultSet(new HashMap<String, Result>());

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
					
					//for each input, create a QTL report or disambiguation view
					for(String findMe : uniqueInputs)
					{
						System.out.println("find : " + findMe);
						
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
				else if(action.startsWith("disambig_"))
				{	
					//disambiguate a single input
					String disambigt = action.substring("disambig_".length());
					String type = request.getString("__type");
					String key = request.getString("__key");
					
					Result r = this.model.getResultSet().get(key);
					
					r.setSelectedName(disambigt);
					
					Class<? extends Entity> entityClass = db.getClassForName(type);
					List<? extends Entity> result = db.find(entityClass, new QueryRule(ObservableFeature.NAME, Operator.EQUALS, disambigt));

					r.setResult(result.get(0));
					
					List<QTLInfo> qtls = createQTLReportFor(result.get(0), threshold, dataFilter, db);
					r.setQtlsFound(qtls);
					r.setDisambiguate(null);
				}

				this.setMessages();
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
			if((d.getTargetType().equals(entity.get(ObservableFeature.__TYPE)) || d.getFeatureType().equals(entity.get(ObservableFeature.__TYPE)))
				&&
				(d.getTargetType().equals("Marker") || d.getFeatureType().equals("Marker")))
			{
				
				//create instance and get name of the row/col we want
				DataMatrixInstance instance = dmh.createInstance(d, db);
				String name = entity.get(ObservationElement.NAME).toString();
				
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
					
					try{
						File img = MakeRPlot.plot(d, instance, name, null, "row", "o", 800, 600);
						qtl.setPlot(img.getName());
					}catch(Exception e)
					{
						e.printStackTrace();
						//too bad, image failed
					}
					
					HashMap<String, Marker> markerInfo = getMarkerInfo(colNames, db);
					qtl.setMarkerAnnotations(markerInfo);
					
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
					
					try{
						File img = MakeRPlot.plot(d, instance, null, name, "col", "o", 800, 600);
						qtl.setPlot(img.getName());
					}catch(Exception e)
					{
						e.printStackTrace();
						//too bad, image failed
					}
					
					HashMap<String, Marker> markerInfo = getMarkerInfo(rowNames, db);
					qtl.setMarkerAnnotations(markerInfo);
					
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

	private HashMap<String, Marker> getMarkerInfo(List<String> colNames, Database db)
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
