package plugins.cluster.implementations;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2Preferences;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;
import com.mindbright.util.Util;

import plugins.cluster.helper.Command;
import plugins.cluster.helper.LoginSettings;
import plugins.cluster.interfaces.ComputationResource;

/**
 * PBS cluster implementation of ComputeResource
 * @author joerivandervelde
 *
 */
public class ClusterComputationResource implements ComputationResource
{

	public ClusterComputationResource(LoginSettings ls){
		this.ls = ls;
	}
	
	private LoginSettings ls;

	@Override
	public boolean cleanupJob(int jobId) throws Exception
	{

		//TODO: make advanced, do checks, etc.

		List<Command> commands = new ArrayList<Command>();

		// delete it
		commands = new ArrayList<Command>();
		commands.add(new Command("rm -rf ~/runmij" + jobId + ".*", true, false, false));
		commands.add(new Command("rm -rf ~/run" + jobId, true, false, false));
		this.executeCommands(commands);

		return true;
	}
	
	@Override
	/**
	 * Based on from 'BasicClient.java',
	 * http://www.appgate.com/downloads/MindTerm-3.4/mindterm_3.4-src.zip
	 */
	public List<String> executeCommands(List<Command> commands) throws Exception
	{
		ArrayList<String> results = new ArrayList<String>(); //TODO: put results in

		String host = ls.host;
		int port = getPort(ls.port);
		String user = ls.user;
		String pw = ls.password;

		port = Util.getPort(host, port);
		host = Util.getHost(host);

		SSH2Preferences prefs = new SSH2Preferences(new Properties());

		SecureRandomAndPad secureRandom = new SecureRandomAndPad(new SecureRandom(new RandomSeed().getBytesBlocking(20,
				false)));

		SSH2Transport transport = new SSH2Transport(new Socket(host, port), prefs, secureRandom);

		
		SSH2SimpleClient client = new SSH2SimpleClient(transport, user, pw);

		SSH2ConsoleRemote console = new SSH2ConsoleRemote(client.getConnection());
		
		for (Command command : commands)
		{
			console.command(command.getCommand());
			System.out.println("executing: "+ command.getCommand());
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
