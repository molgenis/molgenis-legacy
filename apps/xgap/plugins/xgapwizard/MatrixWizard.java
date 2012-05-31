/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.xgapwizard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import matrix.general.DataMatrixHandler;
import matrix.general.Importer;

import org.molgenis.cluster.DataName;
import org.molgenis.cluster.DataSet;
import org.molgenis.cluster.DataValue;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class MatrixWizard extends PluginModel<Entity>
{
	private static final long serialVersionUID = -1928168826559410284L;

	private DataMatrixHandler dmh = null;
	
	private MatrixWizardModel model = new MatrixWizardModel();

	public MatrixWizardModel getMyModel()
	{
		return model;
	}

	public MatrixWizard(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

// moved overlib to molgenis core
//	@Override
//	public String getCustomHtmlHeaders()
//	{
//		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";
//
//	}

	@Override
	public String getViewName()
	{
		return "MatrixWizard";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/xgapwizard/MatrixWizard.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			try
			{
				
				if(request.getString("__action").equals("upload") || request.getString("__action").equals("uploadTextArea")){
					
					File importFile = null;
					int dataIdreq = request.getInt("__dataId");
					
					if(request.getString("__action").equals("uploadTextArea")){
						String content = request.getString("inputTextArea");
						File inputTextAreaContent = new File(System.getProperty("java.io.tmpdir") + File.separator + "tmpTextAreaInput" + System.nanoTime() + ".txt");
						BufferedWriter outWriter = new BufferedWriter(new FileWriter(inputTextAreaContent));
						outWriter.write(content);
						outWriter.close();
						importFile = inputTextAreaContent;
					}else{
						importFile = request.getFile("upload"+dataIdreq);
					}
					Data data = db.find(Data.class, new QueryRule("id", Operator.EQUALS, dataIdreq)).get(0);
					Importer.performImport(importFile, data, db);
				}else if(request.getString("__action").equals("showVerified")){
					this.getMyModel().setShowVerified(true);
				}
				else if(request.getString("__action").equals("hideVerified")){
					this.getMyModel().setShowVerified(false);
				}else if(request.getString("__action").equals("tag")){
					int dataIdreq = request.getInt("__dataId");
					String[] tagging = request.getString("tagging_"+dataIdreq).split(" -> ");
					String dataSetName = tagging[0];
					String dataNameName = tagging[1];
					
					Data data = db.find(Data.class, new QueryRule("id", Operator.EQUALS, dataIdreq)).get(0);
					
					DataSet dsRef = db.find(DataSet.class, new QueryRule("name", Operator.EQUALS, dataSetName)).get(0);
				
					Query<DataName> q = db.query(DataName.class);
					q.addRules(new QueryRule("name", Operator.EQUALS, dataNameName));
					q.addRules(new QueryRule("dataset", Operator.EQUALS, dsRef.getId()));
					DataName dnRef = q.find().get(0);
					
					DataValue dv = new DataValue();
					dv.setDataName(dnRef);
					dv.setValue(data);
					dv.setName(data.getInvestigation_Name() + "_" + data.getName());
					db.add(dv);
					
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{
		if(dmh == null){
			dmh = new DataMatrixHandler(db);
		}
		
		if(this.getMyModel().getShowVerified() == null){
			this.getMyModel().setShowVerified(true);
		}
		
		ArrayList<DataInfo> dataInfo = new ArrayList<DataInfo>();

		try
		{

			//pre-query data stuff
			List<Data> dataList = db.find(Data.class);
			List<DataSet> dataSetList = db.find(DataSet.class);
			List<DataName> dataNameList = db.find(DataName.class);
			List<DataValue> dataValueList = db.find(DataValue.class);
			
			//iterate through data and get info
			for (Data data : dataList)
			{
				String eds = dmh.findSource(data, db);

				//query the current matrix tags and add to info
				List<String> tags = new ArrayList<String>();
				
				//FIXME: not very pretty.....
				for(DataValue dv : dataValueList){
					//match reference to matrix
					if(dv.getValue_Id().equals(data.getId())){
						//find the DataName
						for(DataName dn : dataNameList){
							if(dv.getDataName_Id().equals(dn.getId())){
								//find the DataSet
								for(DataSet ds : dataSetList){
									if(dn.getDataSet_Id().equals(ds.getId())){
										tags.add(ds.getName() + " -> " + dn.getName());
									}
								}
							}
						}
					}
				}
				
				
				dataInfo.add(new DataInfo(data, eds, tags));
			}
			
			this.model.setDataInfo(dataInfo);

			//use possible tags and set as options
			ArrayList<String> combinations = new ArrayList<String>();
			for(DataSet ds : dataSetList){
				for(DataName dn : dataNameList){
					if(dn.getDataSet_Id().equals(ds.getId())){
						combinations.add(ds.getName() + " -> " + dn.getName());
					}
				}
			}
			this.model.setTagList(combinations);
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
