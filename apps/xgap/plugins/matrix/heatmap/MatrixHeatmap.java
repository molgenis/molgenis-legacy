/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix.heatmap;

import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.general.Importer;
import matrix.implementations.memory.MemoryDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

import plugins.matrix.manager.Browser;
import plugins.matrix.manager.MatrixManager;
import plugins.matrix.manager.OverlibText;

public class MatrixHeatmap extends PluginModel
{

	private MatrixHeatmapModel model = new MatrixHeatmapModel();
	private DataMatrixHandler dmh = null;

	public MatrixHeatmapModel getMyModel()
	{
		return model;
	}

	public MatrixHeatmap(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<script type=\"text/javascript\" src=\"res/scripts/range.js\"></script>\n"
				+ "<script type=\"text/javascript\" src=\"res/scripts/timer.js\"></script>\n"
				+ "<script type=\"text/javascript\" src=\"res/scripts/slider.js\"></script>\n"
				+ "<link type=\"text/css\" rel=\"StyleSheet\" href=\"res/css/bluecurve/bluecurve.css\" />\n";
				// moved overlib to molgenis core+ "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>\n";
	}

	@Override
	public String getViewName()
	{
		return "MatrixHeatmap";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/matrix/heatmap/MatrixHeatmap.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			try
			{
				if (this.model.isUploadMode())
				{
					// if(request.getString("inputTextArea") != null){
					//DON'T DO THIS: BAD FOR LARGE UPLOADS -> this.getModel().setUploadTextAreaContent(request.getString("inputTextArea"));
					// }
					Importer.performImport(request, this.model.getSelectedData(), db);
					// set to null to force backend check/creation of browser
					// instance
					this.model.setSelectedData(null);
				}
				else if (request.getString("__action").equals("addCustomScale"))
				{

					// try to parse start and stop
					double customStart = Double.parseDouble(request.getString("customScaleStart"));
					double customStop = Double.parseDouble(request.getString("customScaleStop"));

					// checks on start and stop
					if (customStart >= customStop)
					{
						throw new Exception("Start >= stop");
					}

					// if all checks out, set stuff to model
					getAndSetBrowserSettings(request);
					this.model.setCustomStart(customStart);
					this.model.setCustomStop(customStop);
					this.model.setAutoScale(false);

				}
				else if (request.getString("__action").equals("draw"))
				{
					getAndSetBrowserSettings(request);
					getAndSetDrawingColors(request);
				}
				else
				{
					getAndSetBrowserSettings(request);
					RequestHandler.handle(this.model, request.getString("__action"), null);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	private void getAndSetBrowserSettings(Tuple request)
	{
		int stepSize = request.getInt("stepSize") < 1 ? 1 : request.getInt("stepSize");
		int width = request.getInt("width") < 1 ? 1 : request.getInt("width");
		int height = request.getInt("height") < 1 ? 1 : request.getInt("height");

		this.model.getBrowser().getModel().setStepSize(stepSize);
		this.model.getBrowser().getModel().setWidth(width);
		this.model.getBrowser().getModel().setHeight(height);
	}

	private void getAndSetDrawingColors(Tuple request)
	{
		int rStartVal = request.getInt("__rStartVal");
		int gStartVal = request.getInt("__gStartVal");
		int bStartVal = request.getInt("__bStartVal");

		int rStopVal = request.getInt("__rStopVal");
		int gStopVal = request.getInt("__gStopVal");
		int bStopVal = request.getInt("__bStopVal");

		this.model.setStart(new RGB(rStartVal, gStartVal, bStartVal));
		this.model.setStop(new RGB(rStopVal, gStopVal, bStopVal));
	}

	private void createOverLibText(Database db) throws Exception
	{
		List<String> rowNames = this.model.getBrowser().getModel().getSubMatrix().getRowNames();
		List<String> colNames = this.model.getBrowser().getModel().getSubMatrix().getColNames();
		this.model.setRowObsElem((OverlibText.getObservationElements(db, rowNames, this.model.getSelectedData().getTargetType())));
		this.model.setColObsElem((OverlibText.getObservationElements(db, colNames, this.model.getSelectedData().getFeatureType())));
	}

	private void createHeaders()
	{
		this.model.setColHeader(this.model.getSelectedData().getFeatureType() + " "
				+ (this.model.getBrowser().getModel().getColStart() + 1) + "-"
				+ this.model.getBrowser().getModel().getColStop() + " of "
				+ this.model.getBrowser().getModel().getColMax());
		this.model.setRowHeader(this.model.getSelectedData().getTargetType() + "<br>"
				+ (this.model.getBrowser().getModel().getRowStart() + 1) + "-"
				+ this.model.getBrowser().getModel().getRowStop() + " of "
				+ this.model.getBrowser().getModel().getRowMax());
	}

	private void makeHeatmapSubmatrix() throws Exception
	{
		if (this.model.getStart() == null)
		{
			this.model.setStart(new RGB(255, 255, 127));
		}

		if (this.model.getStop() == null)
		{
			this.model.setStop(new RGB(255, 0, 0));
		}

		DataMatrixInstance oldSubMatrix = this.model.getBrowser().getModel().getSubMatrix();

		Double lowestVal = null;
		Double highestVal = null;
		boolean firstAssignment = true;

		Object[][] oldValsObj = oldSubMatrix.getElements();
		Double[][] oldVals = new Double[oldSubMatrix.getNumberOfRows()][oldSubMatrix.getNumberOfCols()];

		// if autoscale: need to determine highest and lowest value. slight
		// optimization for custom values where we don't do this.
		// by default, autoscale is not set. (null)
		if (this.model.getAutoScale() == null || this.model.getAutoScale() == true)
		{
			for (int rowIndex = 0; rowIndex < oldSubMatrix.getNumberOfRows(); rowIndex++)
			{
				for (int colIndex = 0; colIndex < oldSubMatrix.getNumberOfCols(); colIndex++)
				{
					if (oldValsObj[rowIndex][colIndex] != null)
					{
						boolean parsed = false;
						try
						{
							double val = Double.parseDouble(oldValsObj[rowIndex][colIndex].toString());
							oldVals[rowIndex][colIndex] = val;
							if (firstAssignment)
							{
								lowestVal = val;
								highestVal = val;
								firstAssignment = false;
							}
							else
							{
								if (val < lowestVal)
								{
									lowestVal = val;
								}
								if (val > highestVal)
								{
									highestVal = val;
								}
							}
							parsed = true;
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						if (!parsed)
						{
							throw new Exception("Value on index row " + rowIndex + ", col " + colIndex
									+ " is not null, or not a double: " + oldVals[rowIndex][colIndex]);
						}
					}
				}
			}
		}
		else
		{
			for (int rowIndex = 0; rowIndex < oldSubMatrix.getNumberOfRows(); rowIndex++)
			{
				for (int colIndex = 0; colIndex < oldSubMatrix.getNumberOfCols(); colIndex++)
				{
					if (oldValsObj[rowIndex][colIndex] != null)
					{
						boolean parsed = false;
						try
						{
							double val = Double.parseDouble(oldValsObj[rowIndex][colIndex].toString());
							oldVals[rowIndex][colIndex] = val;
							parsed = true;
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						if (!parsed)
						{
							throw new Exception("Value on index row " + rowIndex + ", col " + colIndex
									+ " is not null, or not a double: " + oldVals[rowIndex][colIndex]);
						}
					}
				}
			}
			lowestVal = this.model.getCustomStart();
			highestVal = this.model.getCustomStop();
		}

		Object[][] newVals = new Object[oldSubMatrix.getNumberOfRows()][oldSubMatrix.getNumberOfCols()];

		for (int rowIndex = 0; rowIndex < oldSubMatrix.getNumberOfRows(); rowIndex++)
		{
			for (int colIndex = 0; colIndex < oldSubMatrix.getNumberOfCols(); colIndex++)
			{
				if (oldVals[rowIndex][colIndex] != null)
				{
					newVals[rowIndex][colIndex] = oldVals[rowIndex][colIndex] + 1;

					newVals[rowIndex][colIndex] = getHeatmapHTML(oldVals[rowIndex][colIndex], highestVal, lowestVal,
							this.model.getStart(), this.getMyModel().getStop());
				}
			}
		}

		DataMatrixInstance heatMatrix = new MemoryDataMatrixInstance(oldSubMatrix.getRowNames(), oldSubMatrix.getColNames(), newVals, this.getMyModel().getSelectedData());

		this.model.setHeatMatrix(heatMatrix);
		this.model.setLowestVal(lowestVal);
		this.model.setHighestVal(highestVal);
	}

	private String getHeatmapHTML(double val, double high, double low, RGB start, RGB stop)
	{
		//if value is outside of high-low range, return empty
		if(val < low || val > high){
			return "<td>" + "" + "</td>";
		}

		// ie. 110-102, val 106
		// 110-102 = 8
		// 106-102 = 4
		// 4/8 = 0.5

		double highMinLow = high - low;
		double valMinLow = val - low;
		double ratio = valMinLow / highMinLow;

		// ie. 100-255, ratio 0.5
		// 255-100 = 155
		// 100+(155/2) = 177.5

		int scaledR = (int) Math.round(start.getR() + ((stop.getR() - start.getR()) * ratio));
		int scaledG = (int) Math.round(start.getG() + ((stop.getG() - start.getG()) * ratio));
		int scaledB = (int) Math.round(start.getB() + ((stop.getB() - start.getB()) * ratio));

		String hexR = Integer.toHexString(scaledR);
		String hexG = Integer.toHexString(scaledG);
		String hexB = Integer.toHexString(scaledB);

		hexR = hexR.length() == 1 ? "0" + hexR : hexR;
		hexG = hexG.length() == 1 ? "0" + hexG : hexG;
		hexB = hexB.length() == 1 ? "0" + hexB : hexB;

		return "<td style=\"background: #" + hexR + hexG + hexB + "\">" + "" + "</td>";

	}

	@Override
	public void reload(Database db)
	{

		if(dmh == null){
			dmh = new DataMatrixHandler(db);
		}
		
		// TODO: create refresh button
		// TODO: review this 'core' logic carefully :)

		ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
		FormModel<Data> parentForm = (FormModel<Data>) ((FormController)parentController).getModel();
		Data data = parentForm.getRecords().get(0);

		try
		{

			boolean newOrOtherData;
			// boolean createBrowserSuccess = true; //assume success, can be
			// false if a new instance is created but fails

			if (this.model.getSelectedData() == null)
			{
				newOrOtherData = true;
			}
			else
			{
				if (MatrixManager.dataHasChanged(this.model.getSelectedData(), data))
				{
					newOrOtherData = true;
				}
				else
				{
					newOrOtherData = false;
				}
			}

			this.model.setSelectedData(data);

			if (newOrOtherData)
			{
				logger.info("*** newOrOtherData");
				this.model.setHasBackend(dmh.isDataStoredIn(data, data.getStorage(), db));
				logger.info("hasBackend: " + this.model.isHasBackend());
				if (this.model.isHasBackend())
				{
					logger.info("*** creating browser instance");
					Browser br = MatrixManager.createBrowserInstance(db, data);
					this.model.setBrowser(br);
				}
			}

			if (this.model.isHasBackend())
			{
				this.model.setUploadMode(false);
				makeHeatmapSubmatrix();
				createOverLibText(db);
				createHeaders();
			}
			else
			{
				this.model.setUploadMode(true);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			this.model.setBrowser(null);
		}

	}

}
