package plugins.rplot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import matrix.DataMatrixInstance;

import org.molgenis.util.RScript;

public class HeatmapScriptInstance
{
	public HeatmapScriptInstance(DataMatrixInstance plotThis, File tmpImg, PlotParameters params) throws Exception
	{
		File tmpData = new File(System.getProperty("java.io.tmpdir") + File.separator + "heatmap_data_"
				+ System.nanoTime() + ".txt");
		PrintStream p = new PrintStream(new BufferedOutputStream(new FileOutputStream(tmpData)), false, "UTF8");
		plotThis.toPrintStream(p);

		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		// script.append(plotThis.getAsRobject(true));
		script.append("png(imagefile, width = " + params.getWidth() + ", height = " + params.getHeight() + ")");

		script.append("heatmapdata <- read.table(\"" + tmpData.getAbsolutePath()
				+ "\",sep=\"\\t\",header=T,row.names=1,colClasses=c(\"character\"),check.names=FALSE,na=\"0\")");
		script.append("heatmapdata <- as.matrix(heatmapdata)");
		script.append("colnames <- colnames(heatmapdata)");
		script.append("rownames <- rownames(heatmapdata)");

		if (plotThis.getData().getValueType().equals("Decimal"))
		{
			script.append("heatmapdata <- matrix(as.numeric(as.matrix(heatmapdata)),c(dim(heatmapdata)[1],dim(heatmapdata)[2]))");
		}
		else
		{
			script.append("heatmapdata <- matrix(as.numeric(as.factor(heatmapdata)),c(dim(heatmapdata)[1],dim(heatmapdata)[2]))");
		}

		script.append("colnames(heatmapdata) <- colnames");
		script.append("rownames(heatmapdata) <- rownames");
		script.append("heatmapdata[is.na(heatmapdata)] <- 0");

		String clustering = "";
		if (params.get__Type().equals("rows"))
		{
			clustering = ", Colv=NA";
		}
		else if (params.get__Type().equals("cols"))
		{
			clustering = ", Rowv=NA";
		}
		else if (params.get__Type().equals("none"))
		{
			clustering = ", Rowv = NA, Colv=NA";
		}

		script.append("heatmap(heatmapdata,main=\"" + params.getTitle() + "\",xlab=\"" + params.getxLabel()
				+ "\",ylab=\"" + params.getyLabel() + "\"" + clustering + ", scale=\"none\")");
		script.append("dev.off()");
		script.execute();
	}
}
