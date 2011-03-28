package plugins.cluster.implementations;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.DetectOS;

import plugins.cluster.helper.Command;
import plugins.cluster.interfaces.ComputationResource;
import filehandling.generic.MolgenisFileHandler;

/**
 * Local implementation of ComputationResource
 * @author joerivandervelde
 *
 */
public class LocalComputationResource implements ComputationResource {
	
	MolgenisFileHandler xlfh;
	String defaultRepos = "http://cran.xl-mirror.nl/";

	public LocalComputationResource(MolgenisFileHandler xlfh) {
		this.xlfh = xlfh;
	}

	@Override
	public boolean cleanupJob(int jobId) throws Exception {
		
		List<Command> commands = new ArrayList<Command>();
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		// delete it
		commands = new ArrayList<Command>();
		
		String OS = DetectOS.getOS();
		
		if(OS.startsWith("unix") || OS.equals("mac")){
			commands.add(new Command("rm -rf "+tmpDir.getAbsolutePath()+"/runmij" + jobId + ".*", true, false, false));
			commands.add(new Command("rm -rf "+tmpDir.getAbsolutePath()+"/run" + jobId, true, false, false));
		}else{
			commands.add(new Command("del /f /s /q "+tmpDir.getAbsolutePath()+"\\runmij" + jobId + ".*", true, false, false));
			commands.add(new Command("del /f /s /q "+tmpDir.getAbsolutePath()+"\\run" + jobId, true, false, false));
			commands.add(new Command("rd /s /q "+tmpDir.getAbsolutePath()+"\\run" + jobId, true, false, false));
		}
		this.executeCommands(commands);
		
		return false;
	}

//	@Override
//	public void executeSimpleCommand(String command) throws Exception {
//		String OS = DetectOS.getOS();
//		execute(command, OS, true);
//	}
	
	@Override
	public List<String> executeCommands(List<Command> commands) throws Exception
	{
		String OS = DetectOS.getOS();
		ArrayList<String> res = new ArrayList<String>();
		for (Command command : commands) {
			res.add(executeOSDependantCommand(command, OS));
			Thread.sleep(100);
		}
		return res;
	}

	@Override
	public String executeCommand(Command command) throws Exception {
		String OS = DetectOS.getOS();
		String res = executeOSDependantCommand(command, OS);
		return res;
	}
	
	

	private String executeOSDependantCommand(Command command, String OS)
			throws IOException {

		String res = ""; // TODO: put results in!

		String commandString = command.getCommand();
		
		//evil!
//		if(command.isTmpDirExecute()){
//			String browseToTmpDir = "cd " + System.getProperty("java.io.tmpdir");
//			commandString = browseToTmpDir +" && "+ commandString;
//		}
		
		System.out.println("EXECUTING: " + commandString);
		
		Process p = null;
		
	//	System.out.println("cd " +  System.getProperty("java.io.tmpdir"));
		
		// for unix, unixlike, mac
		if (OS.startsWith("unix") || OS.equals("mac")) {
			p = Runtime.getRuntime().exec(
					new String[] { "/bin/sh", "-c", commandString });
		} else if (OS.equals("windows")) {
			p = Runtime.getRuntime().exec(
					new String[] { "cmd.exe", "/c", commandString });
		} else if (OS.equals("windowslegacy")) {
			p = Runtime.getRuntime().exec(
					new String[] { "command.com", "/c", commandString });
		}

		if (command.isWaitFor() && (OS.startsWith("unix") || OS.equals("mac"))) {
			InputStream in = p.getInputStream();
			BufferedInputStream buf = new BufferedInputStream(in);
			InputStreamReader inread = new InputStreamReader(buf);
			BufferedReader bufferedreader = new BufferedReader(inread);

			// Read the ls output
			String line;
			while ((line = bufferedreader.readLine()) != null) {
				System.out.println(line);
			}

			// Check for ls failure
			try {
				if (p.waitFor() != 0) {
					System.err.println("exit value = " + p.exitValue());
				}
			} catch (InterruptedException e) {
				System.err.println(e);
			} finally {
				// Close the InputStream
				bufferedreader.close();
				inread.close();
				buf.close();
				in.close();
			}
			// must always "wait" for windows
		} else if (OS.startsWith("windows")) {

			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(),
					"ERROR");

			// any output?
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(),
					"OUTPUT");

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			// any error???
			if(command.isWaitFor()){
				try {
					if (p.waitFor() != 0) {
						System.err.println("exit value = " + p.exitValue());
					}
				} catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		} else {
			System.out.println("Not waiting for process '" + command
					+ "' on OS " + OS);
		}
		return res;
	}

	private void installBiocPackage(String pkg, File usrHomeLibs,
			String OS) throws IOException{
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpFile = new File(tmpDir.getAbsoluteFile() + File.separator
				+ "tmpRfile_" + System.nanoTime() + ".R");
		String cmd;
		
		if(OS.startsWith("windows")){
			cmd = "echo source(\"http://bioconductor.org/biocLite.R\");biocLite(\""+pkg+"\",lib=\""
			+ usrHomeLibs.getAbsolutePath().replace("\\", "/")
			+ "\");q(\"no\") > " + tmpFile.getAbsolutePath();
		}else{
			cmd = "echo source\\(\\\"http://bioconductor.org/biocLite.R\\\"\\)\\;biocLite\\(\\\""+pkg+"\\\",lib=\\\""
			+ usrHomeLibs.getAbsolutePath().replace("\\", "/")
			+ "\\\"\\)\\;q\\(\\\"no\\\"\\) > " + tmpFile.getAbsolutePath();
		}
		executeOSDependantCommand(new Command(cmd, true, false, false), OS);
		executeOSDependantCommand(new Command("R CMD BATCH " + tmpFile.getAbsolutePath(), true, false, false), OS);
		tmpFile.delete();
	}
	
	private void installRPackage(String pkg, String repos, File usrHomeLibs,
			String OS) throws Exception {

		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpFile = new File(tmpDir.getAbsoluteFile() + File.separator
				+ "tmpRfile_" + System.nanoTime() + ".R");
		
		String cmd;
		
		if(OS.startsWith("windows")){
			cmd = "echo install.packages(\"" + pkg + "\",repos=\"" + repos + "\",lib=\""
			+ usrHomeLibs.getAbsolutePath().replace("\\", "/")
			+ "\");q(\"no\") > " + tmpFile.getAbsolutePath();
		}else{
			cmd = "echo install.packages\\(\\\"" + pkg + "\\\",repos=\\\"" + repos + "\\\",lib=\\\""
			+ usrHomeLibs.getAbsolutePath().replace("\\", "/")
			+ "\\\"\\)\\;q\\(\\\"no\\\"\\) > " + tmpFile.getAbsolutePath();
		}
				
		executeOSDependantCommand(new Command(cmd, true, false, false), OS);
		executeOSDependantCommand(new Command("R CMD BATCH " + tmpFile.getAbsolutePath(), true, false, false), OS);
		tmpFile.delete();

	}
	
	private String msWindowsSafePath(String path){
		String res = path.replace("\\", "\"/\"");
		res = "\"" + res + "\"";
		return res;
	}
	
	
	/**
	 * Convenience method for external use
	 * @throws Exception
	 */
	public void installQtl() throws Exception{
		File usrHomeLibs = new File(System.getProperty("user.home")
				+ File.separator + "libs");
		String OS = DetectOS.getOS();
		installRPackage("qtl", defaultRepos, usrHomeLibs, OS);
	}
	
	/**
	 * Convenience method for external use
	 * @throws Exception
	 */
	public void installRCurl() throws Exception{
		File usrHomeLibs = new File(System.getProperty("user.home")
				+ File.separator + "libs");
		String OS = DetectOS.getOS();
		installBiocPackage("RCurl", usrHomeLibs, OS);
	}
	
	/**
	 * Convenience method for external use
	 * @throws Exception
	 */
	public void installBitops() throws Exception{
		File usrHomeLibs = new File(System.getProperty("user.home")
				+ File.separator + "libs");
		String OS = DetectOS.getOS();
		installRPackage("bitops", defaultRepos, usrHomeLibs, OS);
	}
	
	/**
	 * Convenience method for external use
	 * NO LONGER NEEDED - files are sourced in R api
	 * @throws Exception
	 */
	@Deprecated
	public void installClusterJobs() throws Exception{
		File usrHomeLibs = new File(System.getProperty("user.home")
				+ File.separator + "libs");
		String OS = DetectOS.getOS();
		File xgapRsources = new File(this.getClass().getResource("../R")
				.getFile());
		executeOSDependantCommand(new Command("R CMD INSTALL " + xgapRsources.getAbsolutePath()
				+ File.separator + "ClusterJobs --library="
				+ msWindowsSafePath(usrHomeLibs.getAbsolutePath()) + " --vanilla", true, false, false), OS);
	}

	@Override
	public boolean installDependencies() throws Exception {

		File usrHomeLibs = new File(System.getProperty("user.home")
				+ File.separator + "libs");
		File xgapRsources = new File(this.getClass().getResource("../R")
				.getFile());

		System.out.println("user home libs = " + usrHomeLibs.getAbsolutePath());

		boolean installBitops = false;
		boolean installQtl = false;
		boolean installRCurl = false;
		File bitopsDir = new File(usrHomeLibs.getAbsolutePath()
				+ File.separator + "bitops");
		File qtlDir = new File(usrHomeLibs.getAbsolutePath()
				+ File.separator + "qtl");
		File rcurlDir = new File(usrHomeLibs.getAbsolutePath()
				+ File.separator + "RCurl");


		if (!usrHomeLibs.exists()) {
			usrHomeLibs.mkdir();
			installBitops = true;
			installQtl = true;
			installRCurl = true;
		} else {
			System.out.println("bitopdir = " + bitopsDir.getAbsolutePath());
			if (!bitopsDir.exists()) {
				installBitops = true;
			}

			if (!qtlDir.exists()) {
				installQtl = true;
			}
			if (!rcurlDir.exists()) {
				installRCurl = true;
			}
		}

		String OS = DetectOS.getOS();

		System.out.println("starting installation...");

		if (installBitops) {
			installRPackage("bitops", defaultRepos, usrHomeLibs, OS);
			System.out.println("done installing bitops");
		}

		if (installRCurl) {
//			if (OS.startsWith("windows")) {
//				installRPackage("RCurl", rcurlWindowsRepos, usrHomeLibs, OS);
//				System.out.println("done installing RCurl");
//			} else {
//				installRPackage("RCurl", defaultRepos, usrHomeLibs, OS);
//				System.out.println("done installing RCurl");
//			}
			
			installBiocPackage("RCurl", usrHomeLibs, OS);
			System.out.println("done installing RCurl");

		}

		if (installQtl) {
			installRPackage("qtl", defaultRepos, usrHomeLibs, OS);
			System.out.println("done installing qtl");
		}

		// we ALWAYS reinstall Clusterjobs because this is our own custom code
		// and subject to bugfixes. notice the source is not compiled.
		// execute("cd " + xgapRsources.getAbsolutePath() + " && ");
		// '--library=" + usrHomeLibs.getAbsolutePath() +"'", OS, true);
		// System.out.println("done ClusterJobs");

		
		//if OS == unix
		//nogmaals lib check
		//if missing libs:
		if(OS.startsWith("unix") || OS.equals("mac")){
			if (!bitopsDir.exists()) {
				executeOSDependantCommand(new Command("R CMD INSTALL " + xgapRsources.getAbsolutePath()
						+ File.separator + "bitops_1.0-4.1.tar.gz --library="
						+ msWindowsSafePath(usrHomeLibs.getAbsolutePath()) + " --vanilla", true, false, false), OS);
			}

			if (!qtlDir.exists()) {
				System.out.println("SEVERE (but expected): OPNIEUW INSTALLEREN van QTL omdat iets mis");
				executeOSDependantCommand(new Command("R CMD INSTALL " + xgapRsources.getAbsolutePath()
						+ File.separator + "qtl_custom.tar.gz --library="
						+ msWindowsSafePath(usrHomeLibs.getAbsolutePath()) + " --vanilla", true, false, false), OS);

			}
			if (!rcurlDir.exists()) {
				System.out.println("SEVERE: OPNIEUW INSTALLEREN van RCurl omdat iets mis");
				executeOSDependantCommand(new Command("R CMD INSTALL " + xgapRsources.getAbsolutePath()
						+ File.separator + "RCurl_0.91-0.tar.gz --library="
						+ msWindowsSafePath(usrHomeLibs.getAbsolutePath()) + " --vanilla", true, false, false), OS);

			}
		}
		
		executeOSDependantCommand(new Command("R CMD INSTALL " + xgapRsources.getAbsolutePath()
				+ File.separator + "ClusterJobs --library="
				+ msWindowsSafePath(usrHomeLibs.getAbsolutePath()) + " --vanilla", true, false, false), OS);
		

		// installRPackage("ClusterJobs",
		// xgapRsources.getAbsolutePath().replace("\\", "/"),usrHomeLibs, OS);
		System.out.println("done ClusterJobs");

		System.out.println("...finished installation");

		return true;
	}



}
