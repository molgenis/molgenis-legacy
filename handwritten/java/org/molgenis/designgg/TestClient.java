/**
 * @author Gonzalo Vera
 *
 */
package org.molgenis.designgg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.ui.ApplicationController;


public class TestClient {

	/**
	 * 
	 */
	public TestClient() {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        try {
        	String name = "designGG";
					
        	ApplicationController ui = new ApplicationController(null);
        	CalculateDesignScreen s = new CalculateDesignScreen("test",ui);
			
			List<String> rscript = new ArrayList<String>();
			
			rscript.add("#example1");
			rscript.add("library(designGG)");
			rscript.add("genotype <- read.table(\"genotypes.txt\")");
			//rscript.add("data(genotype)");
			rscript.add("nEnvFactors <- 2");
			rscript.add("nLevels <- c(5,5)");
			rscript.add("nTuple <- 4");
			rscript.add("Level= list(c(1,2,3,4,5),(c(1,2,3,4,5)/2))");
			rscript.add("bTwoColorArray <- F");
			rscript.add("initial=NULL");
			rscript.add("optimality=\"A\"");
			rscript.add("method=\"SA\"");
			rscript.add("weight=1");
			rscript.add("region=NULL");
			rscript.add("#set nSlides or nTuple");
			rscript.add("nSlides<-NULL");
			rscript.add("startTemp <- 1");
			rscript.add("endTemp <- 1e-10");
			rscript.add("nIterations <- 100"); //10
			rscript.add("maxTempStep <- 0.9");
			rscript.add("n.search=2");
			rscript.add("plotScores=T");
			rscript.add("temp<-designGG ( nSlides=nSlides,nEnvFactors=nEnvFactors, nTuple=nTuple,nLevels=nLevels, Level=Level, genotype=genotype,");
			rscript.add("n.search=2,initial=NULL, optimality=\"A\", method=\"SA\", bTwoColorArray=F,");
			rscript.add("weight=1, region=NULL, nIterations=nIterations, endTemp=endTemp, startTemp=startTemp, maxTempStep=maxTempStep,");
			rscript.add("plotScores=T, writingProcess=T)");
			
			File f = new File("genotypes.txt");
			System.out.println("hallo? "+f.exists());
			
			byte[] data = Utils.getFile("genotypes.txt");
			Map<String, byte[]> inputAttachments = new TreeMap<String,byte[]>();
			inputAttachments.put("genotypes.txt",data);
			
			s.executeR(rscript, inputAttachments);
						
//			String sessionId = null;
//			if( args[0] != null ){
//				sessionId = args[0];
//			}			
//			
//			if( args[1] != null ){
//				File myFile = new File( args[1] );
//				byte[] myContents = Utils.getFile(myFile.getCanonicalPath());
//				Map<String, byte[]> myInput = new HashMap<String, byte[]>();
//				myInput.put("genotypes.txt", myContents);	
//				dGGi.designGG( sessionId, rscript, myInput );
//			}
//			else {
//				dGGi.designGG( sessionId, rscript, null );
//			}
//			
//			boolean bWaitMore = true;
//			String myStatus;
//			while( bWaitMore )
//			{				
//				myStatus = myStatus Utils.getFile(s.getOutputR());
//				if( myStatus.contains("DONE")){
//					bWaitMore = false;
//				}
//				else {
//					System.out.println("Current status: " + myStatus + ". Let's wait a bit.");
//					Thread.sleep(1000); // We wait 1 sec before asking again
//				}
//				
//			}
//			Map<String, byte[]> myResults = dGGi.getResults( sessionId );
//			for( String s : myResults.keySet()){
//				System.out.println("File downloaded: " + s );
//				Utils.setFile( s, (byte []) myResults.get(s) );
//			}
//			
//			System.out.println("TestClient Finalized!");	
		}
		catch( Exception e){
			System.err.println("TestClient for designGG exception!:");
			e.printStackTrace();
		}

	}

}
