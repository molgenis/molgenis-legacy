package org.molgenis.designgg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper class to execute commands under MS Windows
 * @author joerivandervelde
 *
 */
class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    LocalComputationResource reporter;
    boolean verbose = false;
    
    StreamGobbler(InputStream is, String type, LocalComputationResource reporter){
        this.is = is;
        this.type = type;
        this.reporter=reporter;
    }
    
    public void run(){
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null){
      	      if(verbose){
      	    	  System.out.println(type + ">" + line);
      	      }
  	      	  if(type.equals("OUTPUT")){
  	      		  reporter.addResultLine(line);
  	      	  }
  	      	  if(type.equals("ERROR")){
  	      		  reporter.addErrorLine(line);
  	      	  }
            }
        } catch (IOException ioe){
            ioe.printStackTrace();  
        }
    }
}
