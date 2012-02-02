/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.qtlfinder2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Locus;
import org.molgenis.xgap.Marker;

import plugins.qtlfinder.QTLMultiPlotResult;
import plugins.reportbuilder.Report;
import plugins.reportbuilder.ReportBuilder;
import plugins.reportbuilder.Statistics;
import plugins.rplot.MakeRPlot;

import app.JDBCMetaDatabase;

public class QtlFinder2 extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1L;

	private QtlFinderModel2 model = new QtlFinderModel2();
	
	
	int searchResultLimit = 100;
	
	static String __ALL__DATATYPES__SEARCH__KEY = "__ALL__DATATYPES__SEARCH__KEY";
	
	private int plotWidth = 1024;
	private int plotHeight = 768;

	public QtlFinderModel2 getMyModel()
	{
		return model;
	}

	public QtlFinder2(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "QtlFinder2";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/qtlfinder2/QtlFinder2.ftl";
	}
	
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
			try
			{
				String query = request.getString("query");
				this.model.setQuery(query);
				
				String dataType = request.getString("dataTypeSelect");
				this.model.setSelectedAnnotationTypeAndNr(dataType);
				
				if (action.equals("shop"))
				{
					String shopMeName = request.getString("__shopMeName");
					int shopMeId = request.getInt("__shopMeId");
					this.model.getShoppingCart().put(shopMeName, shopMeId);
				}
				
				if (action.equals("unshop"))
				{
					String shopMeName = request.getString("__shopMeName");
					this.model.getShoppingCart().remove((shopMeName));
				}
				
				if(action.equals("plotShoppingCart"))
				{
					QTLMultiPlotResult res = multiplot(new ArrayList<String>(this.model.getShoppingCart().keySet()), db);
					this.model.setReport(null);
					this.model.setMultiplot(res);
				}
				
				if(action.equals("emptyShoppingCart"))
				{
					this.model.setShoppingCart(null);
				}
				
				if(action.startsWith("__entity__report__for__"))
				{
					System.out.println("action.startsWith(\"__entity__report__for__\")");
					
					String name = action.substring("__entity__report__for__".length());
					
					QueryRule nameQuery = new QueryRule(ObservationElement.NAME, Operator.EQUALS, name);
					
					//we expect 1 hit exactly: ObservationElement names are unique, and since they have already been found, it should exist (unless deleted in the meantime..)
					ObservationElement o = db.find(ObservationElement.class, nameQuery).get(0);
			
					String entityClassString = o.get(Field.TYPE_FIELD).toString();
					Class<? extends Entity> entityClass = db.getClassForName(entityClassString);
					
					//now get the properly typed result entity
					List<? extends Entity> typedResults = db.find(entityClass, nameQuery);
					
					Report report = ReportBuilder.makeReport(typedResults.get(0), db);
					this.model.setReport(report);
				}
				
				if (action.equals("search"))
				{
					this.model.setShortenedQuery(null);
					this.model.setMultiplot(null);
					this.model.setReport(null);
					
					if(query == null)
					{
						throw new Exception("Please enter a search term");
					}
					
					Class<? extends Entity> entityClass;
					if(dataType.equals(__ALL__DATATYPES__SEARCH__KEY))
					{
						entityClass =  db.getClassForName("ObservationElement"); // more broad than "ObservableFeature", but OK
					}
					else
					{
						entityClass = db.getClassForName(dataType);
					}
					
					Query<?> q = db.query(entityClass);
					
					List<? extends Entity> result = allFieldMatch(q.find(), query, searchResultLimit);
					
					if(result.size() == 0)
					{
						boolean noResults = true;
						String shortenedQuery = query;
						
						while(noResults && shortenedQuery.length() > 0)
						{
							shortenedQuery = shortenedQuery.substring(0, shortenedQuery.length()-1);
							System.out.println("TRYING: " + shortenedQuery);
							
							result = allFieldMatch(q.find(), shortenedQuery, searchResultLimit);
							System.out.println("RESULTS: " + result.size());
							
							if(result.size() > 0)
							{
								noResults = false;
								this.model.setShortenedQuery(shortenedQuery);
							}
						}
						
						if(noResults)
						{
							throw new Exception("No results found");
						}
						
					}
					
					int nrOfResults = 0;
					Map<String, Entity> hits = new HashMap<String, Entity>();
					
					for(Entity e : result)
					{
						hits.put(e.get(ObservationElement.NAME).toString(), e);
						
						nrOfResults++;
						
						if(nrOfResults == searchResultLimit)
						{
							break;
						}
					}
					
					this.model.setHits(hits);
					
					
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}
	
	private List<Entity> allFieldMatch(List<? extends Entity> entities, String searchTerm, int limit)
	{
		List<Entity> result = new ArrayList<Entity>();
		for(Entity e : entities)
		{
			for(String field : e.getFields())
			{
				if(field.equals(Field.TYPE_FIELD)){
					continue;
				}
				Object content = e.get(field);
				if(content != null && !content.toString().equals("[]") && content.toString().toLowerCase().contains(searchTerm.toLowerCase()))
				{
					result.add(e);
					if(result.size() == limit)
					{
						return result;
					}
					break;
				}
			}
		}
		return result;
	}
	
	
	
	
	private QTLMultiPlotResult multiplot(List<String> names, Database db) throws Exception
	{
		//need to requery... not efficient at all :( fixme
		List<ObservationElement> entities = db.find(ObservationElement.class, new QueryRule(ObservationElement.NAME, Operator.IN, names));
		HashMap<String,Entity> matches = new HashMap<String,Entity>();
		int overallIndex = 1;
		List<Data> allData = db.find(Data.class);
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
				if((d.getTargetType().equals("Marker") || d.getFeatureType().equals("Marker")))
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
					for(ObservationElement e : entities)
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
		
								matches.put(name, e);
								
								for(int markerIndex = 0; markerIndex < colNames.size(); markerIndex++)
								{
									if(nameToMarker.containsKey(colNames.get(markerIndex)))
									{
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
								
								matches.put(name, e);
								
								for(int markerIndex = 0; markerIndex < rowNames.size(); markerIndex++)
								{
									if(nameToMarker.containsKey(rowNames.get(markerIndex)))
									{
										String chrId = nameToMarker.get(rowNames.get(markerIndex)).getChromosome_Id() != null ? nameToMarker.get(rowNames.get(markerIndex)).getChromosome_Id().toString() : "-";
										bw.write(overallIndex + " \"" + rowNames.get(markerIndex) + "\" " + chrId + " " + nameToMarker.get(rowNames.get(markerIndex)).getBpStart() + " \"" + name + "\" " + locus + " " + Dvalues[markerIndex]);
										bw.newLine();
										overallIndex++;
									}
								}
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

		}
		
		bw.close();
		
		int height = (matches.size() * 10) > 300 ? (matches.size() * 10) : 300;
		
		File plot = MakeRPlot.qtlMultiPlot(tmpData, plotWidth, height, this.model.getQuery());
		File cisTransplot = MakeRPlot.qtlCisTransPlot(tmpData, plotWidth, plotHeight, this.model.getQuery());
		
		
		QTLMultiPlotResult result = new QTLMultiPlotResult();
		result.setPlot(plot.getName());
		result.setCisTransPlot(cisTransplot.getName());
		result.setMatches(matches);
		
		return result;
	}
	
	
	
	
	
	
	
	@Override
	public void reload(Database db)
	{

		try
		{
			if(model.getShoppingCart() == null)
			{
				Map<String, Integer> shoppingCart = new HashMap<String, Integer>();
				this.model.setShoppingCart(shoppingCart);
			}
			
			
			if (model.getAnnotationTypeAndNr() == null)
			{
				int totalAmount = 0;
				
				JDBCMetaDatabase metadb = new JDBCMetaDatabase();
				Map<String, Integer> annotationTypeAndNr = new HashMap<String, Integer>();
				Vector<org.molgenis.model.elements.Entity> entityList = metadb.getEntities();

				// iterate over all entity types
				for (org.molgenis.model.elements.Entity entityType : entityList)
				{
					// get the ancestors for one such entity
					for (org.molgenis.model.elements.Entity e : entityType.getAllAncestors())
					{
						// if one of the ancestors is ObservationElement..
						if (e.getName().equals(ObservationElement.class.getSimpleName()))
						{
							Class<? extends Entity> entityClass = db.getClassForName(entityType.getName());
							// and the class is not ObservableFeature or
							// ObservationTarget..
							if (!entityClass.getSimpleName().equals(ObservableFeature.class.getSimpleName())
									&& !entityClass.getSimpleName().equals(ObservationTarget.class.getSimpleName()))
							{
								// count the number of entities in the database
								int count = db.count(entityClass);
								if (count > 0)
								{
									annotationTypeAndNr.put(entityType.getName(), count);
									totalAmount += count;
								}
							}
							break;
						}
					}
				}
				annotationTypeAndNr.put(__ALL__DATATYPES__SEARCH__KEY, totalAmount);
				model.setAnnotationTypeAndNr(annotationTypeAndNr);
			}
			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/qtlfinder.css\">" + "\n" +
				"<script type=\"text/javascript\" src=\"etc/js/clear-default-text.js\"></script>" ;
	}

}
