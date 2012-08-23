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
import org.molgenis.framework.db.DatabaseException;
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
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.Locus;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Probe;

import plugins.qtlfinder.QTLInfo;
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
					//Entity e = db.findById(ObservationElement.class, shopMeId); cannot use this: problem with lists and java generics when load()
					List<? extends Entity> input = db.find(ObservationElement.class, new QueryRule(ObservationElement.NAME, Operator.EQUALS, shopMeName));
					input = db.load((Class)ObservationElement.class, input);
					this.model.getShoppingCart().put(shopMeName, input.get(0));
				}
				
				if (action.equals("unshop"))
				{
					String shopMeName = request.getString("__shopMeName");
					this.model.getShoppingCart().remove((shopMeName));
				}
				
				if (action.equals("gotoCart"))
				{
					this.model.setCartView(true);
				}
				
				if(action.equals("reset"))
				{
					this.model.setQuery(null);
					this.model.setHits(null);
					this.model.setShortenedQuery(null);
					this.model.setShoppingCart(null);
					this.model.setMultiplot(null);
					this.model.setReport(null);
					this.model.setQtls(null);
					this.model.setCartView(false);
				}
				
				if (action.equals("gotoSearch"))
				{
					this.model.setCartView(false);
					this.model.setMultiplot(null);
				}
				
				if(action.equals("shopAll"))
				{
					this.model.getShoppingCart().putAll(this.model.getHits());
				}
				
				if(action.equals("plotShoppingCart"))
				{
					if(this.model.getShoppingCart().size() == 0)
					{
						throw new Exception("Your shopping cart is empty!");
					}
					else if(this.model.getShoppingCart().size() > 500)
					{
						throw new Exception("You cannot plot more than 500 items.");
					}
					
					this.model.setCartView(false);
					
					QTLMultiPlotResult res = multiplot(new ArrayList<Entity>(this.model.getShoppingCart().values()), db);
					this.model.setReport(null);
					this.model.setMultiplot(res);
				}
				
				if(action.equals("emptyShoppingCart"))
				{
					this.model.setShoppingCart(null);
				}
				
				if(action.startsWith("__entity__report__for__"))
				{
					this.model.setCartView(false);
					
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
					
					List<QTLInfo> qtls = PlotHelper.createQTLReportFor(typedResults.get(0), plotWidth, plotHeight, db);
					this.model.setQtls(qtls);
					
				}
				
				if (action.equals("search"))
				{
					this.model.setCartView(false);
					this.model.setShortenedQuery(null);
					this.model.setMultiplot(null);
					this.model.setReport(null);
					this.model.setQtls(null);
					
					if(query == null)
					{
						throw new Exception("Please enter a search term");
					}
					
					if(query.length() > 25)
					{
						throw new Exception("Queries longer than 25 characters are not allowed");
					}
					
					if(query.length() < 2)
					{
						throw new Exception("Queries shorter than 2 characters are not allowed");
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
					
					
					Map<String, Entity> hits = query(entityClass, db, query, 100);
					
					System.out.println("initial number of hits: " + hits.size());
					
					hits = genesToProbes(db, 100, hits);
					
					System.out.println("after converting genes to probes, number of hits: " + hits.size());

					
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
	
	
	private Map<String, Entity> genesToProbes(Database db, int limit, Map<String, Entity> hits) throws DatabaseException{
		Map<String, Entity> result = new HashMap<String, Entity>();
		int nrOfResults = 0;
		
		outer:
		for(String name : hits.keySet())
		{
			Entity e = hits.get(name);
			
			//System.out.println("looking at " + e.get(Field.TYPE_FIELD).toString() + " " + e.get(ObservationElement.NAME).toString());
			
			if(e.get(Field.TYPE_FIELD).equals("Gene"))
			{
				QueryRule r = new QueryRule(Probe.REPORTSFOR_NAME, Operator.EQUALS, name);
				QueryRule o = new QueryRule(Operator.OR);
				QueryRule s = new QueryRule(Probe.SYMBOL, Operator.EQUALS, name);
				List<Probe> probes = db.find(Probe.class, r, o, s);
				
				//System.out.println("RESULT WITH JUST REPORTSFOR: " +db.find(Probe.class, r).size());
				//System.out.println("RESULT WITH JUST SYMBOL: " +db.find(Probe.class, s).size());
				
				for(Probe p : probes)
				{
					result.put(p.getName(), p);
					//System.out.println("GENE2PROBE SEARCH HIT: " +p.getName());
					nrOfResults++;
					if(nrOfResults == limit)
					{
						//System.out.println("breaking genesToProbes on adding probes");
						break outer;
					}
				}
			}
			else
			{
				result.put(e.get(ObservationElement.NAME).toString(), e);
				//System.out.println("GENE2PROBE ADDING ALSO: " +e.get(ObservationElement.NAME).toString());
				nrOfResults++;
				if(nrOfResults == limit)
				{
					//System.out.println("breaking genesToProbes on other entities");
					break outer;
				}
			}
		}
		
		return result;
	}
	
	private Map<String, Entity> query(Class entityClass, Database db, String query, int limit) throws DatabaseException
	{

		List<? extends Entity> result  = db.search(entityClass, query);
		result = db.load(entityClass, result);
		
		if(result.size() == 0)
		{
			boolean noResults = true;
			String shortenedQuery = query;
			
			while(noResults && shortenedQuery.length() > 0)
			{
				
				result  = db.search(entityClass, shortenedQuery);
				result = db.load(entityClass, result);
				
				if(result.size() > 0)
				{
					noResults = false;
					this.model.setShortenedQuery(shortenedQuery);
				}
				
				shortenedQuery = shortenedQuery.substring(0, shortenedQuery.length()-1);
			}
			
			if(noResults)
			{
				throw new DatabaseException("No results found");
			}
			
		}
		
		int nrOfResults = 0;
		Map<String, Entity> hits = new HashMap<String, Entity>();
		
		for(Entity e : result)
		{
			hits.put(e.get(ObservationElement.NAME).toString(), e);
			
			//System.out.println("REGULAR SEARCH HIT: " + e.get(ObservationElement.NAME));
			
			nrOfResults++;
			
			if(nrOfResults == limit)
			{
				break;
			}
		}
		
		return hits;
	}
	
	private QTLMultiPlotResult multiplot(List<Entity> entities, Database db) throws Exception
	{
		HashMap<String,Entity> matches = new HashMap<String,Entity>();
		HashMap<String, Entity> datasets =  new HashMap<String,Entity>();
		int totalAmountOfElementsInPlot = 0;
		int overallIndex = 1;
		List<Data> allData = db.find(Data.class);
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		Map<String, Chromosome> usedChromosomes = new HashMap<String, Chromosome>();
		
		//writer for data table
		File tmpData = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot_data_table_" + System.nanoTime() + ".txt");
		File cytoscapeNetwork = new File(System.getProperty("java.io.tmpdir") + File.separator + "cyto_qtl_network_" + System.nanoTime() + ".txt");
		File cytoscapeNodes = new File(System.getProperty("java.io.tmpdir") + File.separator + "cyto_node_attr_" + System.nanoTime() + ".txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpData));
		BufferedWriter bw_network = new BufferedWriter(new FileWriter(cytoscapeNetwork));
		BufferedWriter bw_nodes = new BufferedWriter(new FileWriter(cytoscapeNodes));
		List<String> nodesInFile = new ArrayList<String>();
		
		for(Data d : allData)
		{
			if(PlotHelper.isEffectSizeData(db, d))
			{
				continue;
			}
			
			//if something fails with this matrix, don't break the loop
			//e.g. backend file is missing
			try{
			
				//loop over Text data (can't be QTL)
				if(d.getValueType().equals("Text")){
					continue;
				}
				//check if one of the matrix dimensions if of type 'Marker'
				//TODO: limited, could also be SNP or another potential subclass
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
					for(Entity e : entities)
					{
						//skip markers, we are interested in traits here
						if(e.get(Field.TYPE_FIELD).equals("Marker")){
							continue;
						}
						
						//matrix may not have the trait type on rows AND columns (this is correlation data or so)
						if(d.getTargetType().equals(e.get(Field.TYPE_FIELD)) && d.getFeatureType().equals(e.get(Field.TYPE_FIELD)))
						{
							continue;
						}
						
						//one of the matrix dimensions must match the type of the trait
						if(d.getTargetType().equals(e.get(Field.TYPE_FIELD)) || d.getFeatureType().equals(e.get(Field.TYPE_FIELD)))
						{
							//if so, use this entity to 'query' the matrix and store the values
							String name = e.get(ObservableFeature.NAME).toString();
							
							//find out if the name is in the row or col names
							int rowIndex = rowNames.indexOf(name);
							int colIndex = colNames.indexOf(name);
							
							List<String> markerNames = null;
							Double[] Dvalues = null;
							
							//if its in row, do row stuff
							if(rowIndex != -1)
							{
								markerNames = colNames;
								Dvalues = Statistics.getAsDoubles(instance.getRow(rowIndex));
							}
							//if its in col, and not in row, do col stuff
							else if(rowIndex == -1 && colIndex != -1)
							{
								markerNames = rowNames;
								Dvalues = Statistics.getAsDoubles(instance.getCol(colIndex));
							}
							else
							{
								//this trait is not in the matrix.. skip
								continue;
							}
							
							//add entity and dataset to matches
							matches.put(name, e);
							datasets.put(d.getName(), d);
							totalAmountOfElementsInPlot++;
							
							//get the basepair position of the trait and chromosome name, if possible
							long locus;
							String traitChrName;
							if(e instanceof Locus)
							{
								locus = ((Locus)e).getBpStart();
								traitChrName = ((Locus)e).getChromosome_Name();
							}
							else
							{
								locus = -10000000;
								traitChrName = "-";
							}
							
							//rewrite name to include label
							name = e.get(ObservableFeature.NAME).toString() + (e.get(ObservableFeature.LABEL) != null ? "/" + e.get(ObservableFeature.LABEL).toString() : "");
							
							//iterate over the markers and write out input files for R / CytoScape
							for(int markerIndex = 0; markerIndex < markerNames.size(); markerIndex++)
							{
								if(nameToMarker.containsKey(markerNames.get(markerIndex)))
								{
									String chrId = nameToMarker.get(markerNames.get(markerIndex)).getChromosome_Id() != null ? nameToMarker.get(markerNames.get(markerIndex)).getChromosome_Id().toString() : "-";
									
									//query and store chromosome objects for this name if it's not there yet
									String chrName = nameToMarker.get(markerNames.get(markerIndex)).getChromosome_Name();
									if(!usedChromosomes.containsKey(chrName))
									{
										Chromosome c = db.find(Chromosome.class, new QueryRule(Chromosome.NAME, Operator.EQUALS, chrName)).get(0);
										usedChromosomes.put(chrName, c);
									}
									
									// index, marker, chrId, markerLoc, trait, traitLoc, LODscore, dataId
									bw.write(overallIndex + " \"" + markerNames.get(markerIndex) + "\" " + chrId + " " + nameToMarker.get(markerNames.get(markerIndex)).getBpStart() + " \"" + name + "\" " + locus + " " + Dvalues[markerIndex] + " " + d.getId());
									bw.newLine();
									
									if(Dvalues[markerIndex].doubleValue() > 3.5)
									{
										// trait, "QTL", marker, LODscore
										bw_network.write(name + "\t" + "QTL" + "\t" + markerNames.get(markerIndex) + "\t" + Dvalues[markerIndex]);
										bw_network.newLine();
										
										String marker_chrName = nameToMarker.get(markerNames.get(markerIndex)).getChromosome_Name() != null ? nameToMarker.get(markerNames.get(markerIndex)).getChromosome_Name() : "-";
										if(!nodesInFile.contains(name))
										{
											// trait, traitChr, traitLocus, dataName
											bw_nodes.write(name + "\t" + "trait" + "\t" + traitChrName + "\t" + locus + "\t" + d.getName());
											bw_nodes.newLine();
											nodesInFile.add(name);
										}
										if(!nodesInFile.contains(markerNames.get(markerIndex)))
										{
											// marker, markerChr, markerLocus, dataName
											bw_nodes.write(markerNames.get(markerIndex) + "\t" + "marker" + "\t" + marker_chrName + "\t" + nameToMarker.get(markerNames.get(markerIndex)).getBpStart() + "\t" + d.getName());
											bw_nodes.newLine();
											nodesInFile.add(markerNames.get(markerIndex));
										}
									}
									overallIndex++;
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
		bw_network.close();
		bw_nodes.close();
		
		int height = 200 + (totalAmountOfElementsInPlot * 20);
		
		//add chromosomes in strict order, starting at 1
		ArrayList<Chromosome> chromosomes = new ArrayList<Chromosome>();
		for(int orderNrIter = 1; orderNrIter <= usedChromosomes.size(); orderNrIter++ )
		{
			for(Chromosome c : usedChromosomes.values())
			{
				if(c.getOrderNr().intValue() == orderNrIter)
				{
					chromosomes.add(c);
					continue;
				}
			}
		}
		

		MakeRPlot m = new MakeRPlot();
		
		String plotTitle = "hits"; //this.model.getQuery()
		
		File plot = m.wormqtl_MultiPlot(tmpData, plotWidth, height, plotTitle, chromosomes);
		File cisTransplot = m.wormqtl_CisTransPlot(tmpData, plotWidth, plotHeight, plotTitle, chromosomes);
		File regularPlot = m.wormqtl_ProfilePlot(tmpData, plotWidth, height, plotTitle, chromosomes);
		
		QTLMultiPlotResult result = new QTLMultiPlotResult();
		result.setSrcData(tmpData.getName());
		result.setCytoNetwork(cytoscapeNetwork.getName());
		result.setCytoNodes(cytoscapeNodes.getName());
		result.setPlot(plot.getName());
		result.setRegularPlot(regularPlot.getName());
		result.setCisTransPlot(cisTransplot.getName());
		result.setMatches(matches);
		result.setDatasets(datasets);
		
		return result;
	}
	
	
	
	
	
	
	
	@Override
	public void reload(Database db)
	{

		try
		{
			if(model.getShoppingCart() == null)
			{
				Map<String, Entity> shoppingCart = new HashMap<String, Entity>();
				this.model.setShoppingCart(shoppingCart);
			}
			
			if(this.model.getCartView() == null)
			{
				this.model.setCartView(false);
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
