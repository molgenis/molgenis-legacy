package plugins.cluster.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import plugins.cluster.interfaces.ComputationResource;

/**
 * Helper class to execute commands under MS Windows
 * @author joerivandervelde
 *
 */
class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    ComputationResource reporter;
    boolean verbose = false;
    
    StreamGobbler(InputStream is, String type,ComputationResource reporter){
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
