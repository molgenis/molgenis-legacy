/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.matrix.ui.manager;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.TargetFeatureMemoryMatrix;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Tuple;

public class MatrixManager extends PluginModel
{

	private MatrixManagerModel model = new MatrixManagerModel();

	public MatrixManagerModel getMyModel()
	{
		return model;
	}

	public MatrixManager(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";

	}

	@Override
	public String getViewName()
	{
		return "MatrixManager";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/matrix/ui/manager/MatrixManager.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		System.out.println("*** handleRequest WRAPPER __action: " + request.getString("__action"));
		this.handleRequest(db, request, null);
	}

	public void handleRequest(Database db, Tuple request, PrintWriter out)
	{
		if (request.getString("__action") != null)
		{

			System.out.println("*** handleRequest __action: " + request.getString("__action"));

			try
			{
				if (this.model.isUploadMode())
				{
					// if(request.getString("inputTextArea") != null){
					//DON'T DO THIS: BAD FOR LARGE UPLOADS -> this.getModel().setUploadTextAreaContent(request.getString("inputTextArea"));
					// }
					//Importer.performImport(request, this.model.getSelectedData(), db);
					// set to null to force backend check/creation of browser
					// instance
					//this.model.setSelectedData(null);
				}
				else
				{
					int stepSize = request.getInt("stepSize") < 1 ? 1 : request.getInt("stepSize");
					int width = request.getInt("width") < 1 ? 1 : request.getInt("width");
					int height = request.getInt("height") < 1 ? 1 : request.getInt("height");

					this.model.getBrowser().getModel().setStepSize(stepSize);
					this.model.getBrowser().getModel().setWidth(width);
					this.model.getBrowser().getModel().setHeight(height);

					RequestHandler.handle(this.model, request, out);
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

	public static Browser createBrowserInstance(Database db) throws Exception
	{
		TargetFeatureMemoryMatrix tfmm = new TargetFeatureMemoryMatrix(db);
		return new Browser(tfmm);
	}

	private void createOverLibText(Database db) throws Exception
	{
		// System.out.println("*** createOverLibText");
		
		List<String> rowNames = new ArrayList<String>();
		List<ObservationTarget> tmpTargetList = this.model.getBrowser().getModel().getSubMatrix().getRowNames();
		for (ObservationTarget tmpTarget : tmpTargetList) {
			rowNames.add(tmpTarget.getName());
		}
		
		List<String> colNames = new ArrayList<String>();
		List<ObservableFeature> tmpFeatureList = this.model.getBrowser().getModel().getSubMatrix().getColNames();
		for (ObservableFeature tmpFeature : tmpFeatureList) {
			colNames.add(tmpFeature.getName());
		}
		
		this.model.setOverlibText(OverlibText.getOverlibText(db, rowNames, colNames));
	}

	public void clearMessage()
	{
		this.setMessages();
	}

	private void createHeaders()
	{
		this.model.setColHeader((this.model.getBrowser().getModel().getColStart() + 1) + "-"
				+ this.model.getBrowser().getModel().getColStop() + " of "
				+ this.model.getBrowser().getModel().getColMax());
		this.model.setRowHeader((this.model.getBrowser().getModel().getRowStart() + 1) + "-"
				+ this.model.getBrowser().getModel().getRowStop() + " of "
				+ this.model.getBrowser().getModel().getRowMax());
	}

	@Override
	public void reload(Database db)
	{

		// TODO: create refresh button
		// TODO: review this 'core' logic carefully :)
		
		if (this.model.getBrowser() == null) {
			try
			{
				logger.info("*** creating browser instance");
				Browser br = createBrowserInstance(db);
				this.model.setBrowser(br);
				createOverLibText(db);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
				this.model.setBrowser(null);
			}
		}
		
		// Always create headers, so they remain up-to-date after paging etc.
		createHeaders();
	}

}
