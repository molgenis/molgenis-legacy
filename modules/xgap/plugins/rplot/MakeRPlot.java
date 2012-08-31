package plugins.rplot;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import matrix.DataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.util.RScript;
import org.molgenis.util.RScriptException;
import org.molgenis.xgap.Chromosome;

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
	
	public File qtlPlot(String plotName, TreeMap<Long, QtlPlotDataPoint> data, long genePos, int width, int height,String ylab, String filePrefix) throws RScriptException
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
	
	/**
	 * Bad: specific for WormQTL...
	 * @param dataPoints
	 * @param width
	 * @param height
	 * @param title
	 * @param chromosomes
	 * @return
	 */
	public File wormqtl_ProfilePlot(File dataPoints, int width, int height, String title, ArrayList<Chromosome> chromosomes)
	{
		
		String chrNamesAppend = "";
		for(int i = 0; i < chromosomes.size(); i++)
		{
			chrNamesAppend += "\""+chromosomes.get(i).getName()+"\"";
			if(i != chromosomes.size()-1){ chrNamesAppend += ","; }
		}
		
		String chrLengthsAppend = "";
		for(int i = 0; i < chromosomes.size(); i++)
		{
			chrLengthsAppend += chromosomes.get(i).getBpLength();
			if(i != chromosomes.size()-1){ chrLengthsAppend += ","; }
		}
		
		long time = System.nanoTime();
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "qtl_multiplot_" + time + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		
		script.append("drawChrRect<-function()");
		script.append("{");
		script.append("  par(new=TRUE)");
		script.append("  chr.lengths<-c("+chrLengthsAppend+")/10^6");
		script.append("  x.text <-  diffinv(chr.lengths)[-length(diffinv(chr.lengths))]+chr.lengths/2");
		script.append("  y.text <- max(as.numeric(plotMe[,7]))");
		script.append(" ");
		script.append("  for(i in 1:length(chr.lengths))");
		script.append("  {");
		script.append("  if ( i %in% c(1,3,5,7) ){ rect(sum(chr.lengths[1:i])-sum(chr.lengths[i]),0,sum(chr.lengths[1:i]),2*y.text,col=\"grey\",border=F)}");
		script.append("  if( i %in% 7){ lines(c(sum(chr.lengths),sum(chr.lengths)),c(0,2*y.text),lwd=3,col=\"grey\") }");
		script.append("  }");
		script.append("  text( x.text,y.text,c("+chrNamesAppend+"),cex=2,font=2) ");
		script.append("  par(new=TRUE)");
		script.append("}");
		script.append(" ");
		
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		script.append("png(imagefile, width = 2024, height = 1024)");
		script.append("plotMe <- read.table(\""+dataPoints.getAbsolutePath().replace("\\", "/")+"\")");
		
		script.append(" ");
		script.append("probenames                      <- paste(plotMe[,5],plotMe[,8],sep=\":\")");
		script.append("plotMe[,5] <- probenames");
		script.append("  par(fig=c(0,0.85,0,1),mgp=c(4,2,0),mai=c(1,1.5,1,0.05))");
		script.append("plot(0,0,type=\"n\",xlim=c(0,102),ylim=c(0,max(as.numeric(plotMe[,7]))),cex.lab=3,cex.axis=3,main=\"Profile plot for "+title+"\",");
		script.append("      xlab=\"Genome (MB)\", ylab=\"LOD\",cex.main=4)");
		script.append("drawChrRect()");
		script.append("nr.of.lines <- length(unique(probenames))");
		script.append("for( i in 1:nr.of.lines){");
		script.append("x <- plotMe[plotMe[,5] == unique(probenames)[i],4]/1e6");
		script.append("y <- plotMe[plotMe[,5] == unique(probenames)[i],7]");
		script.append("lines(x,y,lwd=3,col=rainbow(nr.of.lines)[i])");
		script.append("}");
		script.append("box()");
		script.append(" ");
		script.append("par(fig=c(0.85,1,0.2,1),new=T,mar=c(0,0,2,0))");
		script.append("matrix.id <- c(7,10,11,14,17,18,21,36,37,38)");
		script.append("matrix.name <- c(\"7: Age group 1\",");
		script.append("                 \"10: Interaction Age 1&2\",");
		script.append("                 \"11: Constitutive Age 1&2\",");
		script.append("                 \"14: Age group 2\",");
		script.append("                 \"17: Interaction Age 2&3\",");
		script.append("                 \"18: Constitutive Age 2&3\",");
		script.append("                 \"21: Age group 3\",");
		script.append("                 \"36: Rockman etal 2010\",");
		script.append("                 \"37: Grown at 16oC\",");
		script.append("                 \"38: Grown at 24oC\")");
		script.append("plot(0,0,type=\"n\",xlim=c(0,10),ylim=c(0,nr.of.lines),axes=F,xlab=\"\",ylab=\"\",main=\"Legend\",cex.main=2)");
		script.append("for( i in 1:nr.of.lines){");
		script.append("lines(c(1,9),c(i,i),lwd=3,col=rainbow(nr.of.lines)[i])");
		script.append("txt.nr <- plotMe[plotMe[,5] == unique(probenames)[i],8][1]");
		script.append("text(5,i-0.1,unique(probenames)[i],font=2,cex=1.3)");
		script.append("text(5,i-0.4,matrix.name[matrix.id == txt.nr],cex=1.3)");
		script.append("}");
		script.append("box()");
		script.append("par(fig=c(0.85,1,0,0.2),new=T,mar=c(0,0,0,0))");
		script.append("plot(0,0,type=\"n\",xlim=c(0,10),ylim=c(0,4),axes=F,xlab=\"\",ylab=\"\")");
		script.append("text(0,3.75,\"7, 10, 11, 14, 17, 18, 21:\",adj=0,cex=2)");
		script.append("text(0,3.25,\"Vinuela & snoek etal 2010\",adj=0,cex=2)");
		script.append("text(0,2.5,\"36:\",adj=0,cex=2)");
		script.append("text(0,2,\"Rockman etal 2010\",adj=0,cex=2)");
		script.append("text(0,1.25,\"37, 38:\",adj=0,cex=2)");
		script.append("text(0,0.75,\"Li etal 2006\",adj=0,cex=2)");
		script.append("box()");
		script.append(" ");
		
		//print to file
		script.append("dev.off()");
		
		//may not fail and crash the other plots
		try
		{
			script.execute(System.getProperty("java.io.tmpdir") + File.separator + "qtl_multiplot_"+time+".R");
		}
		catch (RScriptException e)
		{
			e.printStackTrace();
		}
				
		return tmpImg;
		
	}
	
	
	public File wormqtl_MultiPlot(File dataPoints, int width, int height, String title, ArrayList<Chromosome> chromosomes)
	{
		
		//TODO: make dynamic using the chromosomes..
		String chrNamesAppend = "";
		for(int i = 0; i < chromosomes.size(); i++)
		{
			chrNamesAppend += "\""+chromosomes.get(i).getName()+"\"";
			if(i != chromosomes.size()-1){ chrNamesAppend += ","; }
		}
		
		long cumulative = 0;
		String cumuChrLengthsAppend = "";
		for(int i = 0; i < chromosomes.size(); i++)
		{
			if(i == 0)
			{
				cumulative = 0;
			}
			else if(i == chromosomes.size()-1)
			{
				cumulative += chromosomes.get(i-1).getBpLength();
				cumuChrLengthsAppend += cumulative + "";
			}
			else
			{
				cumulative += chromosomes.get(i-1).getBpLength();
				cumuChrLengthsAppend += cumulative + ",";
			}
//			for(int cumu = 0; cumu < i; cumu ++)
//			{
//				
//			}
			
		//	chrLengthsAppend += chromosomes.get(i).getBpLength();
		//	if(i != chromosomes.size()-1){ chrLengthsAppend += ","; }
		}
		
		long time = System.nanoTime();
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "qtl_multiplot_" + time + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		
		//lines input example:
		//plotMe <- rbind(plotMe, c(807, "C5M5_2", 66810758, "A_12_P172557", 15184683, 0.0127419530093572))
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		script.append("png(imagefile, width = " + width + ", height = " + height + ")");
		
		script.append("plotMe <- read.table(\""+dataPoints.getAbsolutePath().replace("\\", "/")+"\")");
		
		//FIXME: worm specific!!! bad!!
		//script.append("chr_startpos <- c(15072423,15072423+15279345,15072423+15279345+13783700,15072423+15279345+13783700+17493793,15072423+15279345+13783700+17493793+20924149)");
		script.append("chr_startpos <- c(" + cumuChrLengthsAppend + ")");
		script.append("lodscores <- as.numeric(plotMe[,7])");
		script.append("lodscores[which(lodscores > 10)] <- 10");
		script.append("lodscores[which(lodscores < 0)] <- 0");
		script.append("probenames <- paste(plotMe[,5],plotMe[,8],sep=\":\")");
		script.append("plotMe[,5] <- probenames");
		script.append("ramp <- colorRamp(c(\"lightgray\",\"blue\",\"black\"))");
		script.append("use.col <- rgb( ramp(seq(0, 1, length = 100)), max = 255)");
		script.append("datasetids <- unlist(lapply(strsplit(unique(as.character(plotMe[,5])),\":\"),\"[\",2))");
		script.append("p <- datasetids[1]");
		script.append("cl <- 0");
		script.append("cnt <- 1");
		script.append("for(x in datasetids){");
		script.append("  if(x != p) cl <- c(cl,cnt)");
		script.append("  p <- x");
		script.append("  cnt <- cnt+1");
		script.append("}");
		script.append("cl <- c(cl,length(datasetids))");
		script.append("op <- par(mai=c(1,2,1,1))");
		script.append("plot(c(0, max(as.numeric(plotMe[,4])/1000000)),c(1,length(unique(probenames))),type='n',main=\"Heat plot for "+title+"\", yaxt='n',ylab=\"\", xlab=\"Location (cumulative Mbp)\")");
		script.append("cnt <- 1");
		script.append("for(x in unique(probenames)){");
		script.append("  toplot <- which(plotMe[,5]==x)");
		script.append("  points(as.numeric(plotMe[toplot,4])/1000000, rep(cnt,length(toplot)),cex=1, col=use.col[round(lodscores[toplot]*9)+1],pch=15)");
		script.append("  points(as.numeric(plotMe[toplot[1],6])/1000000, cnt, pch=18, cex=1.5, col=\"red\")");
		script.append("  cnt <- cnt + 1");
		script.append("}");
		//script.append("axis(2,at=1:length(unique(probenames)),unlist(lapply(strsplit(unique(probenames),\":\"),\"[\",1)),las=1,cex.axis=0.7)"); //removes dataset id
		script.append("axis(2,at=1:length(unique(probenames)),unique(probenames),las=1,cex.axis=1)");
		script.append("abline(v=(chr_startpos/1000000)+.5,col='red')");
		script.append("dl <- cl-1");
		script.append("dl[1] <- cl[1]");
		script.append("dl[length(cl)] <- cl[length(cl)]");
		script.append("abline(h=dl+0.5,col=\"black\")");
		script.append("cnt <- 1");
		script.append("p <- 1");
		script.append("cc <- 1");
		script.append("for(x in cl[-1]){");
		script.append("  text(x=-1.5, cc + (x-p)/2, labels=unique(datasetids)[cnt],cex=1.3)");
		script.append("  cnt <- cnt + 1");
		script.append("  cc <- cc+((x-p))");
		script.append("  p <- x");
		script.append("}");
		
		//print to file
		script.append("dev.off()");
		
		//may not fail and crash the other plots
		try
		{
			script.execute(System.getProperty("java.io.tmpdir") + File.separator + "qtl_multiplot_"+time+".R");
		}
		catch (RScriptException e)
		{
			e.printStackTrace();
		}
				
		return tmpImg;
	}
	
	public File wormqtl_ProfilePlot_prev(File dataPoints, int width, int height, String title, ArrayList<Chromosome> chromosomes)
	{
		
		long time = System.nanoTime();
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "qtl_regular_" + time + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
		
		appendDrawChromosomes(script, chromosomes);
		
		script.append("plotMe <- read.table(\""+dataPoints.getAbsolutePath().replace("\\", "/")+"\")");
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");

		script.append("lodscores                       <- as.numeric(plotMe[,7])");
		script.append("lodscores[which(lodscores < 0)] <- 0");
		script.append("probenames                      <- paste(plotMe[,5],plotMe[,8],sep=\":\")");
		script.append("plotMe[,5]                      <- probenames");

		script.append("if(length(unique(probenames))<=7){");
		script.append("  png(imagefile, width = 2024, height = 2024)");
		script.append("  op      <- par(mai=c(1,1,1,1))");
		script.append("  par(mfrow=c(length(unique(probenames)),1)) ");
		script.append("  for(x in unique(probenames)){");
		script.append("     toplot <- which(plotMe[,5]==x)");
		script.append("     plot( as.numeric(plotMe[toplot,4])/1000000, lodscores[toplot],type=\"n\",xlab=\"\",ylab=\"\",cex.axis=2.5)");
		script.append("     drawChrStrips()");
		script.append("     plot( as.numeric(plotMe[toplot,4])/1000000, lodscores[toplot],type=\"l\",col=\"blue\",lwd=2,ylab=\"LOD\",xlab=\"\",main=x,cex.main=2.5,cex.lab=2.5,cex.axis=2.5)   ");
		script.append("     points(as.numeric(plotMe[toplot[1],6])/1000000, 0, pch=25, cex=3, col=\"red\")     ");
		script.append("  }  ");
		script.append("}else{");
		script.append("  png(imagefile, width = 2024, height = 1024)");
		script.append("  op      <- par(mai=c(1,1,1,1))");
		script.append("  qtl.all <- NULL");
		script.append("  for(x in unique(probenames)){");
		script.append("    toplot <- which(plotMe[,5]==x)");
		script.append("    qtl.all<- rbind(qtl.all,lodscores[toplot])");
		script.append("  }");
		script.append("   matplot( as.numeric(plotMe[toplot,4])/1000000, t(qtl.all),type=\"n\",xlab=\"\",ylab=\"\",cex.axis=2.5)");
		script.append("   drawChrStrips()      ");
		script.append("   matplot( as.numeric(plotMe[toplot,4])/1000000, t(qtl.all),type=\"l\", lty=rep(1,length(unique(probenames))),col=1:length(unique(probenames)),lwd=2,ylab=\"LOD\",xlab=\"Genome (Mb)\",main=\"QTLs for "+title+"\",cex.main=2.5,cex.lab=2.5,cex.axis=2.5)   ");
		script.append("}");
		//print to file
		script.append("dev.off()");
		
		//may not fail and crash the other plots
		try
		{
			script.execute(System.getProperty("java.io.tmpdir") + File.separator + "qtl_regular_"+time+".R");
		}
		catch (RScriptException e)
		{
			e.printStackTrace();
		}
				
		return tmpImg;

	}
	
	public void appendDrawChromosomes(RScript script, ArrayList<Chromosome> chromosomes)
	{
		String chrNamesAppend = "";
		for(int i = 0; i < chromosomes.size(); i++)
		{
			chrNamesAppend += "\""+chromosomes.get(i).getName()+"\"";
			if(i != chromosomes.size()-1){ chrNamesAppend += ","; }
		}
		
		String chrLengthsAppend = "";
		for(int i = 0; i < chromosomes.size(); i++)
		{
			chrLengthsAppend += chromosomes.get(i).getBpLength();
			if(i != chromosomes.size()-1){ chrLengthsAppend += ","; }
		}
		
		script.append("drawChrStrips<-function()");
		script.append("{");
		script.append("  par(new=TRUE)");
		script.append("  chr.lengths<-c("+chrLengthsAppend+")/10^6");
		script.append("  nn  <- 100");
		script.append("  chrStrips<-seq(0,0,length=100)");
		script.append("  x.text <-  diffinv(chr.lengths)[-length(diffinv(chr.lengths))]+chr.lengths/2");
		script.append("  y.text <- 102");
		script.append("  text( x.text,y.text,c("+chrNamesAppend+"),cex=1.5)");
		script.append("  for(i in 1:length(chr.lengths))");
		script.append("  {");
		script.append("  if(i%%2==0){ next; }");
		script.append("        lim <- c( diffinv(chr.lengths)[i],  diffinv(chr.lengths)[i+1])");
		script.append("        for (j in 1: nn)");
		script.append("        {");
		script.append("          lines(c(lim[1]+(j*(lim[2]-lim[1])/nn), lim[1]+(j*(lim[2]-lim[1])/nn)), c(0, 100), col=grey(0.95),lwd=2)");
		script.append("        }");
		script.append("  }");
		script.append("  par(new=TRUE)");
		script.append("}");
	}
	
	public File wormqtl_CisTransPlot(File dataPoints, int width, int height, String title, ArrayList<Chromosome> chromosomes)
	{
		
		long time = System.nanoTime();
		File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "qtl_cistrans_" + time + ".png");
		
		RScript script = new RScript();
		RScript.R_COMMAND = "R CMD BATCH --vanilla --slave";
				
		script.append("plotMe <- read.table(\""+dataPoints.getAbsolutePath().replace("\\", "/")+"\")");
		script.append("imagefile <- \"" + tmpImg.getAbsolutePath().replace("\\", "/") + "\";");
		script.append("png(imagefile, width = " + width + ", height = " + height + ")");
		script.append("op <- par(mai=c(1,2,1,1))");
		script.append("min.qtl <- 2");
		script.append("my.scale <- 4");
		appendDrawChromosomes(script, chromosomes);
		script.append("my.offset <- 1000000/10^6");
		script.append("plot(c(0,max(as.numeric(plotMe[,4]))),c(0,max(as.numeric(plotMe[,6]))),type='n',main=\"Cis-trans plot for "+title+"\",ylab=\"Probe position (Mb)\", xlab=\"Marker position (Mb)\",xlim=c(0,101),ylim=c(0,101),cex.lab=1.5,cex.main=1.5,cex.axis=1.5)");
		script.append("drawChrStrips()");
		script.append("points(as.numeric(plotMe[,4])/10^6,as.numeric(plotMe[,6])/10^6,cex=(plotMe[,7]/my.scale) * as.numeric(plotMe[,7] >= min.qtl), col=as.numeric(plotMe[,3]),pch=20)");
		script.append("abline(-my.offset,1,col=\"gray\",lty=2)");
		script.append("abline(my.offset,1,col=\"gray\",lty=2)");
		script.append("abline(0,1,col=\"black\",lty=1)");
		
		//print to file
		script.append("dev.off()");
		
		//may not fail and crash the other plots
		try
		{
			script.execute(System.getProperty("java.io.tmpdir") + File.separator + "qtl_cistrans_"+time+".R");
		}
		catch (RScriptException e)
		{
			e.printStackTrace();
		}
				
		return tmpImg;
	}

	//all inputs must be sorted and of equal length!!
	//create QTL plot scaling by incrementing basepair position
	//give markers colours based on their chromosome
	//no missing values allowed!
	public File qtlPlot(String plotName, double[] lodscores, long[] bplocs, String[] chromosomes, long genePos, int width, int height,String ylab, String filePrefix) throws RScriptException
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
		File res = new MakeRPlot().qtlPlot("henkie", new double[]{3,4,3,6,7,2,4,6}, new long[]{1,2,4,30,8,15,20,5}, new String[]{"I", "I", "I", "IV", "II", "III", "IV", "II"},25, 800, 600,"LOD score", "qtl");
		System.out.println("RES @ " + res);
	}
}
