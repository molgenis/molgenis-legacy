package plugins.rplot;

import java.io.File;

import org.molgenis.util.RScript;
import org.molgenis.util.RScriptException;
import plugins.rplot.PlotParameters;

public class ScriptInstance
{
	public ScriptInstance(Object[] plotThis, File tmpImg, PlotParameters params) throws RScriptException
	{
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");

		script.append("dataVector <- NULL;");
		script.append("dataVector <- rep(0," + plotThis.length + ");");

		boolean isDecimal = checkIfObjectIsDecimal(plotThis);

		for (int i = 0; i < plotThis.length; i++)
		{
			if (isDecimal)
			{
				script.append("dataVector[" + (i + 1) + "] <- " + (plotThis[i] == null ? "NA" : plotThis[i]));
			}
			else
			{
				script.append("dataVector[" + (i + 1) + "] <- "
						+ (plotThis[i] == null ? "NA" : "\"" + plotThis[i] + "\""));
			}
		}
		if (!isDecimal)
		{
			script.append("dataVector <- as.factor(dataVector)");
		}

		script.append("png(imagefile, width = " + params.getWidth() + ", height = " + params.getHeight() + ")");
		script.append(params.getFunction() + "(dataVector,main=\"" + params.getTitle() + "\",xlab=\""
				+ params.getxLabel() + "\",ylab=\"" + params.getyLabel() + "\",type=\"" + params.get__Type() + "\")");
		script.append("dev.off()");
		script.execute();
	}

	private boolean checkIfObjectIsDecimal(Object[] values)
	{
		//boolean doubleCastSucces = false;
		for (Object o : values)
		{
			if (o != null)
			{
				try
				{
					Double.parseDouble(o.toString());
				//	return true;
				}
				catch (NumberFormatException e)
				{
					// it's text :)
					return false;
				}
			}
		}
		return true;
	}

}
