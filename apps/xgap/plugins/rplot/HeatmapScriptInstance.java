package plugins.rplot;

import java.io.File;

import matrix.DataMatrixInstance;

import org.molgenis.util.RScript;

public class HeatmapScriptInstance
{
	public HeatmapScriptInstance(DataMatrixInstance plotThis, File tmpImg, PlotParameters params) throws Exception
	{
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		script.append(plotThis.getAsRobject(true));
		script.append("png(imagefile, width = " + params.getWidth() + ", height = " + params.getHeight() + ")");
	
		String clustering = "";
		if(params.get__Type().equals("rows"))
		{
			clustering = ", Colv=NA";
		}
		else if(params.get__Type().equals("cols"))
		{
			clustering = ", Rowv=NA";
		}
		else if(params.get__Type().equals("none"))
		{
			clustering = ", Rowv = NA, Colv=NA";
		}
			
		script.append("heatmap("+plotThis.getData().getName()+",main=\"" + params.getTitle() + "\",xlab=\""
				+ params.getxLabel() + "\",ylab=\"" + params.getyLabel() + "\"" + clustering + ", scale=\"none\")");
		script.append("dev.off()");
		script.execute();
	}
}
