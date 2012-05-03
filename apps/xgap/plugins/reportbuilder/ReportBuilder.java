/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.reportbuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
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

import plugins.rplot.MakeRPlot;
import app.JDBCMetaDatabase;

public class ReportBuilder extends PluginModel
{

	private ReportBuilderModel model = new ReportBuilderModel();
	private DataMatrixHandler dmh = null;

	public ReportBuilderModel getMyModel()
	{
		return model;
	}

	public ReportBuilder(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	// moved overlib to molgenis core
	// @Override
	// public String getCustomHtmlHeaders()
	// {
	// return
	// "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";
	//
	// }

	@Override
	public String getViewName()
	{
		return "ReportBuilder";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/reportbuilder/ReportBuilder.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			String action = request.getString("__action");

			try
			{

				if (action.equals("buildReport"))
				{
					this.model.setDisambiguate(null);
					this.model.setReport(null);
					
					String dataType = request.getString("dataTypeSelect");
					String entityName = request.getString("entityName");
					this.model.setSelectedAnnotationTypeAndNr(dataType);
					this.model.setSelectedName(entityName);
					
					Class<? extends Entity> entityClass = db.getClassForName(dataType);
					List<? extends Entity> result = db.find(entityClass, new QueryRule(ObservationElement.NAME, Operator.LIKE, entityName));
					
					if(result.size() == 0)
					{
						throw new Exception("No results found for " + dataType + " '"+entityName+"'");
					}
					
					else if(result.size() == 1)
					{
						//build report!
						model.setReport(makeReport(result.get(0), db));
					}
					else
					{
						//disambiguate
						this.model.setDisambiguate(result);
						
					}
					
				}
				else if(action.startsWith("disambig_"))
				{
					this.model.setDisambiguate(null);
					this.model.setReport(null);
					
					String dataType = this.model.getSelectedAnnotationTypeAndNr();
					String entityName = action.substring("disambig_".length());
					this.model.setSelectedName(entityName);
					
					Class<? extends Entity> entityClass = db.getClassForName(dataType);
					List<? extends Entity> result = db.find(entityClass, new QueryRule(ObservationElement.NAME, Operator.EQUALS, entityName));
					
					if(result.size() == 1)
					{
						//build report!
						model.setReport(makeReport(result.get(0), db));
					}else
					{
						throw new Exception("Error when querying for " + dataType + " '" + entityName + "', data may have been deleted/modified/corrupted since the last time it was requested");
					}
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	public static Report makeReport(Entity entity, Database db) throws Exception
	{
		Report r = new Report(entity);
		
		List<Data> allData = db.find(Data.class);
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		
		List<MatrixLocation> matrixLocations = new ArrayList<MatrixLocation>();
		for(Data d : allData)
		{
			if(d.getTargetType().equals(entity.get(Field.TYPE_FIELD)) || d.getFeatureType().equals(entity.get(Field.TYPE_FIELD))){
				DataMatrixInstance instance = dmh.createInstance(d, db);
				String name = entity.get(ObservationElement.NAME).toString();
				
				int rowIndex = instance.getRowNames().indexOf(name);
				int colIndex = instance.getColNames().indexOf(name);
				
				
				
				if(rowIndex != -1 || colIndex != -1)
				{
					int totalRows = instance.getNumberOfRows();
					int totalCols = instance.getNumberOfCols();
					MatrixLocation ml = new MatrixLocation(d, rowIndex, colIndex, totalRows, totalCols);
					
					String plotType = d.getValueType().equals("Text") ? "o" : "boxplot";
					
					if(rowIndex != -1 && totalCols < 10000)
					{
						//make a plot
						try{
							File img = MakeRPlot.plot(d, instance, name, null, "row", plotType, 800, 600);
							ml.setRowImg(img.getName());
						}catch(Exception e)
						{
							e.printStackTrace();
							//too bad, image failed
						}
						
						//also do some correlation
						if(totalRows * totalCols < 1000000){
							TreeMap<String, Double> corr = Statistics.getSpearManCorr(instance, name, true);
							ml.setRowCorr(corr);
						}
					}
					
					if(colIndex != -1 && totalRows < 10000)
					{
						//make a plot
						try{
							File img = MakeRPlot.plot(d, instance, null, name, "col", plotType, 800, 600);
							ml.setColImg(img.getName());
						}catch(Exception e)
						{
							e.printStackTrace();
							//too bad, image failed
						}
						
						//also do some correlation
						if(totalRows * totalCols < 1000000){
							TreeMap<String, Double> corr = Statistics.getSpearManCorr(instance, name, false);
							ml.setColCorr(corr);
						}
					}
					
					matrixLocations.add(ml);
				}

			}
		}
		r.setMatrices(matrixLocations);
		
		//File img = MakeRPlot.plot(screenModel.getSelectedData(), instance, rowName, colName, action, type, width, height);
		
		return r;
	}
	



	@Override
	public void reload(Database db)
	{

		try
		{
			if (model.getAnnotationTypeAndNr() == null)
			{

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
								}
							}
							break;
						}
					}
				}
				model.setAnnotationTypeAndNr(annotationTypeAndNr);
			}
			
			
			

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
