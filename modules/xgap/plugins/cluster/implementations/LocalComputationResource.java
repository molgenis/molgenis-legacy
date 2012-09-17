package plugins.cluster.implementations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.DetectOS;

import plugins.cluster.helper.Command;
import plugins.cluster.interfaces.ComputationResource;

/**
 * Local implementation of ComputationResource
 * @author joerivandervelde
 *
 */
public class LocalComputationResource implements ComputationResource {
	

	String defaultRepos = "http://cran.xl-mirror.nl/";
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

	@Override
	public boolean cleanupJob(int jobId) throws Exception {
		List<Command> commands = new ArrayList<Command>();
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		String OS = DetectOS.getOS();
		
		if(OS.startsWith("unix") || OS.equals("mac")){
			commands.add(new Command("rm -rf "+tmpDir.getAbsolutePath()+"/runmij" + jobId + ".*", true, false, false));
			commands.add(new Command("rm -rf "+tmpDir.getAbsolutePath()+"/run" + jobId, true, false, false));
		}else{
			commands.add(new Command("del /f /s /q "+tmpDir.getAbsolutePath()+"\\runmij" + jobId + ".*", true, false, false));
			commands.add(new Command("del /f /s /q "+tmpDir.getAbsolutePath()+"\\run" + jobId, true, false, false));
			commands.add(new Command("rd /s /q "+tmpDir.getAbsolutePath()+"\\run" + jobId, true, false, false));
		}
		if(!executeCommands(commands).equals("")){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public List<String> executeCommands(List<Command> commands) throws Exception
	{
		String OS = DetectOS.getOS();
		ArrayList<String> res = new ArrayList<String>();
		for (Command command : commands) {
			executeOSDependantCommand(command, OS);
			res.add(this.getResultLine());
			Thread.sleep(100);
		}
		return res;
	}

	@Override
	public String executeCommand(Command command) throws Exception {
		if(executeOSDependantCommand(command, DetectOS.getOS())){
			return getResultLine();
		}else{
			return getErrorLine();
		}
	}
	
	public boolean executeOSDependantCommand(Command command, String OS) throws IOException {
		String commandString = command.getCommand();
		System.out.println("EXECUTING: " + commandString);
		Process p = null;
		this.res="";
		this.err="";

		if (OS.startsWith("unix") || OS.equals("mac")) {
			p = Runtime.getRuntime().exec( new String[] { "/bin/sh", "-c", commandString });
		} else if (OS.equals("windows")) {
			p = Runtime.getRuntime().exec( new String[] { "cmd.exe", "/c", commandString });
		} else if (OS.equals("windowslegacy")) {
			p = Runtime.getRuntime().exec( new String[] { "command.com", "/c", commandString });
		}
		StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR", this);
		StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT",this);

		errorGobbler.start();
		outputGobbler.start();

		if(command.isWaitFor()){
			try {
				if (p.waitFor() != 0) {
					System.err.println("exit value = " + p.exitValue());
					return false;
				}
			} catch (InterruptedException e) {
				System.err.println(e);
				return false;
			}
		}
		return true;
	}

	/*
	private void installBiocPackage(String pkg, File usrHomeLibs, String OS) throws IOException{
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpFile = new File(tmpDir.getAbsoluteFile() + File.separator + "tmpRfile_" + System.nanoTime() + ".R");
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
	*/
	
	private void installRPackage(String pkg, String repos, File usrHomeLibs, String OS) throws Exception {
		System.out.println("Going to install: " + pkg);
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpFile = new File(tmpDir.getAbsoluteFile() + File.separator + "tmpRfile_" + System.nanoTime() + ".R");
		
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
				
		if(!executeOSDependantCommand(new Command(cmd, true, false, false), OS)){
			System.err.println("No install file could be generated");
			return;
		}
		if(!executeOSDependantCommand(new Command("R CMD BATCH " + tmpFile.getAbsolutePath(), true, false, false), OS)){
			System.err.println("Instalation of "+pkg+" failed");
			return;
		}
		tmpFile.delete();
		System.out.println("Done installing " + pkg);
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
		installWrapper("qtl");
//		File usrHomeLibs = new File(System.getProperty("user.home")	+ File.separator + "libs");
//		usrHomeLibs.mkdir();
//		String OS = DetectOS.getOS();
//		installRPackage("qtl", defaultRepos, usrHomeLibs, OS);
	}
	
	/**
	 * Convenience method for external use
	 * @throws Exception
	 */
	public void installRCurl() throws Exception{
		installWrapper("RCurl");
//		File usrHomeLibs = new File(System.getProperty("user.home")	+ File.separator + "libs");
//		usrHomeLibs.mkdir();
//		String OS = DetectOS.getOS();
//		//installBiocPackage("RCurl", usrHomeLibs, OS);
//		installRPackage("RCurl", defaultRepos, usrHomeLibs, OS);
	}
	
	public void installQtlbim() throws Exception{
		installWrapper("qtlbim");
	}
	
	/**
	 * Convenience method for external use
	 * @throws Exception
	 */
	public void installBitops() throws Exception{
		installWrapper("bitops");
//		File usrHomeLibs = new File(System.getProperty("user.home")	+ File.separator + "libs");
//		usrHomeLibs.mkdir();
//		String OS = DetectOS.getOS();
//		installRPackage("bitops", defaultRepos, usrHomeLibs, OS);
	}
	
	public void installWrapper(String installMe) throws Exception
	{
		File usrHomeLibs = new File(System.getProperty("user.home")	+ File.separator + "libs");
		usrHomeLibs.mkdir();
		String OS = DetectOS.getOS();
		installRPackage(installMe, defaultRepos, usrHomeLibs, OS);
	}
	
	@Override
	public boolean installDependencies() throws Exception {
		File usrHomeLibs = new File(System.getProperty("user.home")	+ File.separator + "libs");
		File xgapRsources = new File(this.getClass().getResource("../R").getFile());
		usrHomeLibs.mkdir();
		
		System.out.println("User home libs = " + usrHomeLibs.getAbsolutePath());
		System.out.println("XGAP resources = " + xgapRsources.getAbsolutePath());

		boolean installBitops = false;
		boolean installQtl = false;
		boolean installRCurl = false;
		File bitopsDir = new File(usrHomeLibs.getAbsolutePath()	+ File.separator + "bitops");
		File qtlDir = new File(usrHomeLibs.getAbsolutePath() + File.separator + "qtl");
		File rcurlDir = new File(usrHomeLibs.getAbsolutePath() + File.separator + "RCurl");

		if (!usrHomeLibs.exists()) {
			usrHomeLibs.mkdir();
			installBitops = true;
			installQtl = true;
			installRCurl = true;
		} else {
			System.out.println("Location of bitops = " + bitopsDir.getAbsolutePath());
			System.out.println("Location of R/qtl = " + qtlDir.getAbsolutePath());
			System.out.println("Location of R/Curl = " + rcurlDir.getAbsolutePath());
			if (!bitopsDir.exists()) installBitops = true;
			if (!qtlDir.exists()) installQtl = true;
			if (!rcurlDir.exists()) installRCurl = true;
		}

		String OS = DetectOS.getOS();
		System.out.println("Starting installation on " + OS + "...");

		if (installBitops) {
			installRPackage("bitops", defaultRepos, usrHomeLibs, OS);
		}

		if (installRCurl) {
			//installBiocPackage("RCurl", usrHomeLibs, OS);
			installRPackage("RCurl", defaultRepos, usrHomeLibs, OS);
		}

		if (installQtl) {
			installRPackage("qtl", defaultRepos, usrHomeLibs, OS);
		}

		//Again we check for missing libraries, and install then using tar.gz packages
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
		System.out.println("Finished local installation of R-packages");
		return true;
	}

}
