package generic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * \brief Execute command on the underlying OS<br>
 *
 * Add the ability to execute command on the underlying OS
 * bugs: none found<br>
 */
public class CommandExecutor implements Runnable{
  private static final long serialVersionUID = -2921281960160790513L;
  private ArrayList<String> commands = new ArrayList<String>();
  private String res = "";
  private String err = "";
  public int failed_commands=0;
  int succes_commands=0;
  public Boolean verbose = false;
  
  public CommandExecutor(){
  }

  public CommandExecutor(String cmd){
    addCommand(cmd);
  }
	
  public CommandExecutor(ArrayList<String> cmd){
    setCommands(cmd);
  }

  //Internal Reader class attached to the streams from the process
  class StreamGobbler extends Thread{
    InputStream is;
    String type;
    CommandExecutor reporter;
	    
    StreamGobbler(InputStream i, String t, CommandExecutor c){
      is = i;
      type = t;
      reporter=c;
    }
	    
    public void run(){
      try{
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line=null;
	    while ( (line = br.readLine()) != null){
	      if(verbose) System.out.println(type + ">" + line);
	      if(type.equals("OUTPUT")) reporter.addResultLine(line);
	      if(type.equals("ERROR")) reporter.addErrorLine(line);
	    }
	  } catch (IOException ioe){
	      ioe.printStackTrace();  
	  }
	}
  }
	
  public void addResultLine(String line){
    res += line + "\n";
  }
	
  public void addErrorLine(String line){
    err += line + "\n";
  }

  @Override
  public void run() {
    for(String command : commands){
      if(verbose)Utils.console(command);
      Process p = null;
	  String os = System.getProperty("os.name").toLowerCase();
      try{
        if (os.indexOf("windows 9") > -1){
          p = Runtime.getRuntime().exec(new String[] { "command.com", "/C", command });
        }else if (os.indexOf("windows") > -1){
          p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/C", command });
        }else{
          p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
        }
		try {
		  StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR", this);            
	      StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT",this);
          errorGobbler.start();
	      outputGobbler.start();
	      if (p.waitFor() != 0) {
	    	if(verbose) System.err.println("Command: "+ command + " exit=" + p.exitValue());
		    failed_commands++;
		  }else{
		  outputGobbler.join();
			if(verbose) System.out.println("Command: "+ command +" succesfull");
			succes_commands++;
		  }
		}catch(Exception e){
		  System.err.println("Interupted: ");
		  e.printStackTrace();
	    }
	  }catch(Exception e){
		System.err.println("General execution exception: ");
		e.printStackTrace();
	  }
    }
    System.out.println("INFO CommandExecutor: Finished after executing " + succes_commands + " commands");
    if(failed_commands > 0){
    	System.err.println("WARNING CommandExecutor: failed " + failed_commands + " commands");
    }
  }
	
  public String getResult(){
    return res;
  }
	
  public String getError(){
    return err;
  }

  public void setCommands(ArrayList<String> cmds) {
    commands = cmds;
  }
	
  public void addCommand(String cmd) {
    commands.add(cmd);
  }

  public ArrayList<String> getCommands() {
    return commands;
  }

  public void clearCommands() {
    commands.clear();  
    failed_commands=0;
    succes_commands=0;
  }
}
