package plugins.rplot;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import matrix.DataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.util.RScript;
import org.molgenis.util.RScriptException;

import plugins.qtlfinder.QtlPlotDataPoint;

public class MakeRPlot
{
	public static File plot(Data data, DataMatrixInstance instance, String rowName, String colName, String action, String type, int width, int height) throws Exception
	{
		String rowType = data.getTargetType(); //shorthand
		String colType = data.getFeatureType(); //shorthand
		//Data data = model.getSelectedData(); //shorthand
		
		Object[] plotThis = null;
		PlotParameters params = new PlotParameters();
		
		if (action.endsWith("row"))
		{
			if (data.getValueType().equals("Text"))
			{
				params.setTitle(rowType + " " + rowName);
				params.setxLabel("Type of " + colType);
				params.setyLabel("# of " + colType);
			}else if (data.getValueType().equals("Decimal"))
			{
				params.setTitle(rowType + " " + rowName);
				params.setxLabel(colType);
				params.setyLabel(rowType + " value");
			}
			plotThis = instance.getRow(rowName);
		}
		
		else if(action.endsWith("col"))
		{
			if (data.getValueType().equals("Text"))
			{
				params.setTitle(colType + " " + colName);
				params.setxLabel("Type of " + rowType);
				params.setyLabel("# of " + rowType);

			}
			else if (data.getValueType().equals("Decimal"))
			{
				params.setTitle(colType + " " + colName);
				params.setxLabel(rowType);
				params.setyLabel(colType + " value");
			}
			plotThis = instance.getCol(colName);
		}
		else if(action.endsWith("heatmap"))
		{
			params.setTitle(instance.getData().getName());
			params.setxLabel("");
			params.setyLabel("");
		}
		else
		{
			throw new Exception("unrecognized action: " + action);
		}
		

			File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot"
					+ System.nanoTime() + ".png");
			params.setType(type);
			params.setWidth(width);
			params.setHeight(height);

			if(action.endsWith("col") || action.endsWith("row"))
			{
				if (type.equals("boxplot"))
				{
					params.setFunction("boxplot");
				}
				else
				{
					params.setFunction("plot");
				}

				new ScriptInstance(plotThis, tmpImg, params);
			}
			else if(action.endsWith("heatmap"))
			{
				new HeatmapScriptInstance(instance, tmpImg, params);
			}
			
			return tmpImg;
		
	}
	
	public static File qtlPlot(String plotName, TreeMap<Long, QtlPlotDataPoint> data, long genePos, int width, int height,String ylab, String filePrefix) throws RScriptException
	{
		double[] lodscores = new double[data.size()];
		long[] bplocs = new long[data.size()];
		String[] chromosomes = new String[data.size()];
		
		int index = 0;
		for(Long key : data.keySet())
		{
			lodscores[index] = data.get(key).getLodScore();
			bplocs[index] = data.get(key).getBpLoc();
			chromosomes[index] = data.get(key).getChromosome();
			index++;
		}
		return qtlPlot(plotName, lodscores, bplocs, chromosomes, genePos, width, height, ylab, filePrefix);
	}
	
	public static File qtlMultiPlotV2(File dataPoints, int width, int height, String title) throws RScriptException
	{
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot" + System.nanoTime() + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		
		//lines input example:
		//plotMe <- rbind(plotMe, c(807, "C5M5_2", 66810758, "A_12_P172557", 15184683, 0.0127419530093572))
		script.append("plotMe <- read.table(\""+dataPoints.getAbsolutePath().replace("\\", "/")+"\")");
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		script.append("png(imagefile, width = " + width + ", height = " + height + ")");

		script.append("plot.plotMe <- function(plotMe,chrs,LOD.low,LOD.up,plot.title){");
		script.append(" #### set defaults for missing function settings");
		script.append(" if (missing(chrs)){ chrs <- c(\"I\",\"II\",\"III\",\"IV\",\"V\",\"X\") }     ############# <------------------- chromosome selection not implemented yet!!!!");
		script.append(" if (missing(LOD.low)) { LOD.low <- 0 }");
		script.append(" if (missing(LOD.up)) { LOD.up <- 10 }");
		script.append(" if (missing(plot.title)) { plot.title <- date() }");
		script.append(" ### chromosome position");
		script.append(" chr.pos <- c(15072423,15279345,13783700,17493793,20924149,17718866)");
		script.append(" ## ramp <- colorRamp(c(\"black\",\"black\",\"purple\",\"blue\",\"green\",\"yellow\",\"orange\",\"red\"))    ###### set color range for LOD indication");
		script.append(" ramp <- colorRamp(c(\"white\",\"lightblue\",\"blue\",\"darkblue\"))    ###### set color range for LOD indication");
		script.append(" use.col <- rgb( ramp(seq(0, 1, length = 100)), max = 255)");
		script.append(" #### selected the info for determining the number of pannels");
		script.append(" matX.names <- as.character(unique(plotMe[,8]))                                 ##### selects the matriX names");
		script.append(" # if( length(matX.names) > 5){ matX.names <- matX.names[11:16]   }                  ##### sets the maximum number of pannels to 4");
		script.append(" matX.nr <- length(matX.names)                                                  ##### number of matiXes to plot (== number of pannels)");
		script.append(" probes.per.pannel <- NULL");
		script.append(" for ( i in 1:matX.nr){");
		script.append(" probes.per.pannel[i] <- length(unique(plotMe[plotMe[,8] == unique(plotMe[,8])[i],5]))");
		script.append(" }");
		script.append(" total.probes <- sum(probes.per.pannel)");
		script.append(" bot.y  <- cumsum(probes.per.pannel)/total.probes");
		script.append(" top.y <- c(0,bot.y[1:(matX.nr-1)])");
		script.append(" par(fig=c(0,1,0,1),new=F,omi=c(0.5,1,0,0.5))");
		script.append(" plot(0,0,type=\"n\",axes=F,xlab=\"\",ylab=\"\")");
		script.append(" #### pannel plotting loop ####");
		script.append(" for ( i in 1:matX.nr) {");
		script.append(" #### make matrix for pannel from plotMe");
		script.append(" matX.selc <- plotMe[,8] == matX.names[i]");
		script.append(" trait.names <- as.character(unique(plotMe[matX.selc,5]))");
		script.append(" trait.nr <- length(trait.names)");
		script.append(" marker.pos <- as.numeric(unique(plotMe[matX.selc,4]))");
		script.append(" marker.pos <- marker.pos[order(marker.pos)]");
		script.append(" marker.nr <- length(marker.pos)");
		script.append(" matX.lod <- matrix(NA,trait.nr,marker.nr)");
		script.append(" probe.pos <- NULL");
		script.append(" for ( k in 1:trait.nr ) {");
		script.append(" probe.pos[k] <- unique(as.numeric(plotMe[matX.selc & plotMe[,5] == trait.names[k],6]))");
		script.append(" matX.lod[k,] <- as.numeric(plotMe[matX.selc & plotMe[,5] == trait.names[k] ,7])");
		script.append(" }");
		script.append(" ");
		script.append(" par(mar=c(1,1,0,1),fig=c(0,0.95,top.y[i],bot.y[i]),new=T,mgp=c(2,0.5,0))");
		script.append(" plot(0,0,type=\"n\",xlim=c(0,sum(chr.pos)/1e6),ylim=c(0.5,trait.nr+0.5),axes=F,xaxs=\"i\",yaxs=\"i\",main=\"\",ylab=\"Trait\")");
		script.append(" mtext(matX.names[i],2,4,cex=2,font=2,las=2)");
		script.append(" chr.borders <- c(cumsum(chr.pos)/1e6)");
		script.append(" axis(1,at=c(0,5,10,15,chr.borders,chr.borders+5,chr.borders+10,chr.borders[c(1,3,4,5)]+15),labels=F)");
		script.append(" if( i == 1 ){  axis(1,at=c(0,5,10,15,chr.borders,chr.borders+5,chr.borders+10,chr.borders[c(1,2,4,5)]+15),labels=c(0,5,10,15,rep(0,6),rep(5,6),rep(10,6),rep(15,4)))");
		script.append("                mtext(\"Marker position (Mbp)\",1,1.5,cex=1.5,font=2)   }");
		script.append(" axis(2,at=c(1:trait.nr),labels=trait.names,las=2,cex.axis=0.5)");
		script.append(" for( k in 1:trait.nr){");
		script.append(" LOD.to.plot <- matX.lod[k,]");
		script.append(" LOD.to.plot[LOD.to.plot> LOD.up] <- LOD.up");
		script.append(" LOD.to.plot[LOD.to.plot < LOD.low] <- LOD.low");
		script.append(" for( j in 1:marker.nr){");
		script.append(" x.left <- mean(marker.pos[(j-1):j],na.rm=T)/1e6");
		script.append(" x.right <- mean(marker.pos[j:(j+1)],na.rm=T)/1e6");
		script.append(" rec.col <- use.col[max(round(LOD.to.plot[j]*10,0),1)]");
		script.append(" rect(x.left,k-0.5,x.right,k+0.5,col=rec.col,bg=rec.col,border=F)");
		script.append(" }");
		script.append(" points(probe.pos[k]/1e6,k,pch=19,col=\"red\",lwd=1)           ###### plots probe position");
		script.append(" }");
		script.append(" abline(v=chr.borders[1:5],col=\"grey\",lwd=3)                   ###### plots chromosome borders");
		script.append(" abline(v=chr.borders[1:5],col=\"red\",lwd=1)                    ###### plots chromosome borders");
		script.append(" box()");
		script.append(" }");
		script.append(" ####### make LOD legend");
		script.append(" par(fig=c(0.95,1,0,1),new=T)");
		script.append(" image(t(cbind(0:100,0:100)),col=use.col,axes=F,ylab=\"\",xlab=\"\")");
		script.append(" mtext(\"LOD score\",4,2,cex=1.5)");
		script.append("  axis(4,at=0:10/10,labels=c(0:9,\">10\"),las=2,font=2)");
		script.append("  box()");
		script.append("  }");
		script.append(" ");
		script.append(" plot.plotMe(plotMe)");
		
		//print to file
		script.append("dev.off()");
		script.execute();
				
		return tmpImg;
	}
	
	public static File qtlMultiPlot(File dataPoints, int width, int height, String title) throws RScriptException
	{
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot" + System.nanoTime() + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		
		//lines input example:
		//plotMe <- rbind(plotMe, c(807, "C5M5_2", 66810758, "A_12_P172557", 15184683, 0.0127419530093572))
		script.append("plotMe <- read.table(\""+dataPoints.getAbsolutePath().replace("\\", "/")+"\")");
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		script.append("png(imagefile, width = " + width + ", height = " + height + ")");
		script.append("op <- par(mai=c(1,2,1,1))");		
		script.append("plot(as.numeric(plotMe[,4]),as.numeric(as.factor(plotMe[,5])),type='n',main=\""+title+"\", yaxt='n',ylab=\"\", xlab=\"Basepair location\")");
		script.append("points(as.numeric(plotMe[,4]),as.numeric(as.factor(plotMe[,5])),cex=as.numeric(plotMe[,7]), col=as.numeric(plotMe[,3]),pch=20)");
		script.append("axis(2,at=as.numeric(unique(as.factor(plotMe[,5]))),unique(as.factor(plotMe[,5])),las=1,cex.axis=0.7)");
		script.append("sub <- plotMe[-which(duplicated(as.factor(plotMe[,5]))),]");
		script.append("points(sub[,6],unique(as.factor(plotMe[,5])),pch=13,cex=2,lwd=2)");
		script.append("points(sub[,6],unique(as.factor(plotMe[,5])),pch=13,cex=1,col=\"red\")");
		
		//print to file
		script.append("dev.off()");
		script.execute();
				
		return tmpImg;
	}
	
	public static File qtlCisTransPlot(File dataPoints, int width, int height, String title) throws RScriptException
	{
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot" + System.nanoTime() + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		
		//lines input example:
		//plotMe <- rbind(plotMe, c(807, "C5M5_2", 66810758, "A_12_P172557", 15184683, 0.0127419530093572))
		script.append("plotMe <- read.table(\""+dataPoints.getAbsolutePath().replace("\\", "/")+"\")");
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		script.append("png(imagefile, width = " + width + ", height = " + height + ")");
		
		script.append("op <- par(mai=c(1,2,1,1))");		
		script.append("min.qtl <- 2");
		script.append("my.scale <- 4");
		script.append("my.offset <- 1000000");
		script.append("plot(c(0,max(as.numeric(plotMe[,4]))),c(0,max(as.numeric(plotMe[,6]))),type='n',main=\"CisTrans for "+title+"\",ylab=\"\", xlab=\"Basepair location\")");
		script.append("points(as.numeric(plotMe[,4]),as.numeric(plotMe[,6]),cex=(plotMe[,7]/my.scale) * as.numeric(plotMe[,7] >= min.qtl), col=as.numeric(plotMe[,3]),pch=20)");
		script.append("abline(-my.offset,1,col=\"green\",lty=2)");
		script.append("abline(my.offset,1,col=\"green\",lty=2)");
		script.append("abline(0,1,col=\"black\",lty=2)");
		
		//print to file
		script.append("dev.off()");
		script.execute();
				
		return tmpImg;
	}
			

	//all inputs must be sorted and of equal length!!
	//create QTL plot scaling by incrementing basepair position
	//give markers colours based on their chromosome
	//no missing values allowed!
	public static File qtlPlot(String plotName, double[] lodscores, long[] bplocs, String[] chromosomes, long genePos, int width, int height,String ylab, String filePrefix) throws RScriptException
	{
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + filePrefix + "_rplot" + System.nanoTime() + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");

		script.append("dataVector <- rep(0," + lodscores.length + ");");
		script.append("locs <- rep(0," + lodscores.length + ");");
		script.append("chrs <- rep(0," + lodscores.length + ");");

		for (int i = 0; i < lodscores.length; i++)
		{
			script.append("dataVector[" + (i + 1) + "] <- " + lodscores[i]);
			script.append("locs[" + (i + 1) + "] <- " + bplocs[i]);
			script.append("chrs[" + (i + 1) + "] <- \"" + chromosomes[i] + "\"");
		}
		script.append("chrs <- as.numeric(as.factor(chrs))+1");
		script.append("pos <- " + genePos);
		script.append("png(imagefile, width = " + width + ", height = " + height + ")");
		
		//start plotting: black line
		script.append("plot(y=dataVector,x=locs,col=\"black\",main=\"" + plotName + "\",xlab=\""
				+ "Basepair position" + "\",ylab=\"" + ylab + "\",type=\"" + "l" + "\",pch=20,cex=2,lwd=2)");
		
		//now add coloured balls
		script.append("points(y=dataVector,x=locs,col=chrs,type=\"" + "p" + "\",pch=20,cex=2)");
		
		//now add vertical coloured lines
		script.append("points(y=dataVector,x=locs,col=chrs,type=\"" + "h" + "\",lwd=2)");
		
		//add transcript positions
		script.append("axis(1,pos,\"Transcript\",line=1)");
		script.append("abline(v=pos,lty=2,col='black')");
		
		//print to file
		script.append("dev.off()");
		script.execute();
		
		return tmpImg;
	}
	
	public static void main(String []args) throws RScriptException
	{
		File res = qtlPlot("henkie", new double[]{3,4,3,6,7,2,4,6}, new long[]{1,2,4,30,8,15,20,5}, new String[]{"I", "I", "I", "IV", "II", "III", "IV", "II"},25, 800, 600,"LOD score", "qtl");
		System.out.println("RES @ " + res);
	}
}
