package plugins.cluster.implementations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import plugins.cluster.helper.Command;
import plugins.cluster.helper.LoginSettings;
import plugins.cluster.interfaces.ComputationResource;

import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2HostKeyVerifier;
import com.mindbright.ssh2.SSH2Preferences;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;
import com.mindbright.util.Util;

/**
 * PBS cluster implementation of ComputeResource
 * @author joerivandervelde
 *
 */
public class ClusterComputationResource implements ComputationResource
{
	private boolean verbose = true;
	private String res;
	private String err;
	
	@Override
	public void addResultLine(String line){
		res += line + "\n";
	}
	
	@Override
	public void addErrorLine(String line){
		err += line + "\n";
	}
	
	@Override
	public String getResultLine() {
		return res;
	}

	@Override
	public String getErrorLine() {
		return err;
	}
	
	
	public ClusterComputationResource(LoginSettings ls){
		this.ls = ls;
	}
	
	private LoginSettings ls;

	@Override
	public boolean cleanupJob(int jobId) throws Exception
	{

		//TODO: make advanced, do checks, etc.

		List<Command> commands = new ArrayList<Command>();

		commands.add(new Command("rm -rf ~/runmij" + jobId + ".*", true, false, false));
		commands.add(new Command("rm -rf ~/run" + jobId, true, false, false));
		executeCommands(commands);

		return true;
	}
	
	@Override
	/**
	 * Based on from 'BasicClient.java',
	 * http://www.appgate.com/downloads/MindTerm-3.4/mindterm_3.4-src.zip
	 */
	public List<String> executeCommands(List<Command> commands) throws Exception
	{
		SSH2Transport      transport;
	    SSH2SimpleClient   sshclient;
	    SSH2ConsoleRemote  console;
	    Properties         props = new Properties();
		ArrayList<String> results = new ArrayList<String>(); //TODO: put results in
		RandomSeed seed = new RandomSeed();
		String host = ls.host;
		int port = getPort(ls.port);
		String user = ls.user;
		String pw = ls.password;

		port = Util.getPort(host, port);
		host = Util.getHost(host);
		SSH2Preferences prefs = new SSH2Preferences(props);
        SecureRandomAndPad secureRandom = new SecureRandomAndPad(new SecureRandom(seed.getBytesBlocking(20, false)));

        transport = new SSH2Transport(new Socket(host, port), prefs, secureRandom);
        String fingerprint = props.getProperty("fingerprint." + host + "." + port);
        if(fingerprint != null) {
            transport.setEventHandler(new SSH2HostKeyVerifier(fingerprint));
        }
        sshclient = new SSH2SimpleClient(transport, user, pw);
        console = new SSH2ConsoleRemote(sshclient.getConnection());
		for (Command cmd : commands){
			console.command(cmd.getCommand());
			System.out.println("executing: "+ cmd.getCommand());
			BufferedReader in = new BufferedReader(new InputStreamReader(console.getStdOut()));
			String line;
	        while ((line = in.readLine()) != null) {
	        	if(verbose){
	        		System.out.println(line);
	         	}
	        	addResultLine(line);
	        	results.add(line);
	        }
			Thread.sleep(100);
		}
		console.close();
		return results;
	}

	/**
	 * Taken from 'BasicClient.java',
	 * http://www.appgate.com/downloads/MindTerm-3.4/mindterm_3.4-src.zip
	 * 
	 * Get the port number of the ssh server stored in the string. Defaults to
	 * 22, the ssh standard port, if none is specified.
	 */
	private static int getPort(String port)
	{
		int p;
		try
		{
			p = Integer.parseInt(port);
		}
		catch (Exception e)
		{
			p = 22;
		}
		return p;
	}

	@Override
	public boolean installDependencies()
	{
		//not yet possible: we don't know how long each command will take.
		//doable by enhancing SSH communication with Cluster to be able to 'talk back'
		//so we know when to execute the next command.
		//ALSO possible to make this into a 'Job'!
		//for now: execute manuall, see: http://www.xgap.org/wiki/RqtlCluster
		
//		List<Command> commands = new ArrayList<Command>();
//		commands.add(new Command("wget -r -l2 http://www.xgap.org/svn/xgap_1_4_distro/handwritten/java/plugins/cluster/R/ClusterJobs/", false, false));
//		commands.add(new Command("cd www.xgap.org/svn/xgap_1_4_distro/handwritten/java/plugins/cluster/R/", false, false));
//		commands.add(new Command("R CMD INSTALL ClusterJobs --library=~/libs", false, false));	
//		
		return false;
	}

	@Override
	public String executeCommand(Command command) throws Exception
	{
		ArrayList<Command> commandToList = new ArrayList<Command>();
		commandToList.add(command);
		return executeCommands(commandToList).get(0);
	}

}
