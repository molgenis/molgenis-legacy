package generic;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/// CommandExecutor - command executor class
//<p>
//Add the ability to any project to execute a-sync threaded system commands on local host
//</p>
//

public class CommandExecutor implements Runnable{
	private ArrayList<String> commands = new ArrayList<String>();
	public String res = "";
	
	public CommandExecutor(){
	}
	
	public CommandExecutor(String cmd){
		addCommand(cmd);
	}
	
	public CommandExecutor(ArrayList<String> cmd){
		setCommands(cmd);
	}

	@Override
	public void run() {
		for(String command : commands){
			//Utils.console(command);
			Process p = null;
			String os = System.getProperty("os.name").toLowerCase();
			
			try{
			if (os.indexOf("windows 9") > -1){
				p = Runtime.getRuntime().exec(new String[] { "command.com", "/c", command });
			}else if (os.indexOf("windows") > -1){
				p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", command });
			}else{
				p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
			}
	
			InputStream in_err = p.getErrorStream();
			InputStream in = p.getInputStream();
			BufferedInputStream inbuf = new BufferedInputStream(in);
			BufferedInputStream inbuferr = new BufferedInputStream(in_err);
			BufferedReader br_in = new BufferedReader(new InputStreamReader(inbuf));
			BufferedReader br_in_err = new BufferedReader(new InputStreamReader(inbuferr));
	
			try {
				String line;
				while ((line = br_in.readLine()) != null) {
					res += line + "\n";
				}
				Utils.console("Output of command received");
				if (p.waitFor() != 0) {
					Utils.log("Command: "+ command + " exit=" + p.exitValue(),System.err);
				}else{
					Utils.console("Command: "+ command +" succesfull");
				}
			} catch (Exception e) {
				Utils.log("Interupted: ",e);
			} finally {
				// Close the InputStreams to the application
				br_in.close();
				br_in_err.close();
				inbuferr.close();
				inbuf.close();
				in.close();
				in_err.close();
			}
			}catch(Exception e){
				Utils.log("General execution exception: ",e);
			}
		}
	}

	public void setCommands(ArrayList<String> commands) {
		this.commands = commands;
	}
	
	public void addCommand(String command) {
		this.commands.add(command);
	}

	public ArrayList<String> getCommands() {
		return commands;
	}
	
}
