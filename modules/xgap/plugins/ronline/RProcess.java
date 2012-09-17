package plugins.ronline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.DetectOS;

public class RProcess implements Runnable {

	private File outputFile;
	private long outputFileLength;
	private long timeSinceLastResponse; // seconds
	private long timeOut; // seconds
	private BufferedReader bis;
	private BufferedOutputStream bos;
	private boolean quit;
	private List<String> startupMessage;
	private BufferedInputStream bisError;
	InputStream isError;

	String requestDone = "\\\\r3qu35tc0mpl3t3\\\\";
	String requestDoneOutputMarker = "[1] \"\\\\r3qu35tc0mpl3t3\\\\\"";
	String requestDonePrint = "print(\"" + requestDone
			+ "\");\n";

	public List<String> getStartupMessage() {
		return startupMessage;
	}

	/**
	 * Instantiates a new RProcess with timeOut in seconds. If there is no
	 * output activity for this period of time, the process will quit.
	 * 
	 * Usage example:
	 * 
	 * RProcess rp = new RProcess(1); new Thread(rp).start();
	 * rp.execute("1+pi"); rp.quit();
	 * 
	 * @param timeOut
	 * @throws Exception
	 */
	public RProcess(long timeOut) throws Exception {
		timeSinceLastResponse = 0;
		this.timeOut = timeOut;
		quit = false;
		createOutputFile();
		Process process = Runtime.getRuntime().exec(startupCommand());
		OutputStream os = process.getOutputStream();
		isError = process.getErrorStream(); // !!!!
		bisError = new BufferedInputStream(isError); // !!!!
		bos = new BufferedOutputStream(os);
		FileReader is = new FileReader(outputFile);
		bis = new BufferedReader(is);
		outputFileLength = outputFile.length();

		List<String> startupMessage = retrieveRawResults("Type 'q()' to quit R.");

		this.startupMessage = startupMessage;

		initErrorHandling();
		
		retrieveRawResults(requestDoneOutputMarker);
	}

	/**
	 * Initiate error handling functions, needed to keep the pipe from breaking.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void initErrorHandling() throws IOException, InterruptedException {
		List<String> commands = new ArrayList<String>();
		commands.add("merrorfun <- function(ex) {\n");
		commands.add("	cat(\"Error: \", ex[[1]], \"\\n\", sep=\"\")\n");
		commands.add("}\n");
		commands.add("\n");
		commands.add("mfinallyfun <- function(ex) {\n");
		commands.add("	cat(\"Prevented pipebreak\\n\")\n");
		commands.add("}\n");
		commands.add(requestDonePrint);

		for (String command : commands) {
			bos.write(command.getBytes());
		}
		bos.flush();

	}

	private List<String> retrieveRawResults(String requestEndMarker) throws InterruptedException,
			IOException {

		List<String> results = new ArrayList<String>();
		boolean requestCompleted = false;

		while (!requestCompleted) {
			while (outputFile.length() == outputFileLength) {
				Thread.sleep(15);
//				System.out.print(".");
			}
			outputFileLength = outputFile.length();
			String line;
			while ((line = bis.readLine()) != null) {
				if (line.equals(requestEndMarker)) {
					requestCompleted = true;
					break;
				} else {
					results.add(line);
				}
			}

		}
		return results;

	}

	/**
	 * Helper function to create a suitable temporary file.
	 * 
	 * @throws Exception
	 */
	private void createOutputFile() throws Exception {
		outputFile = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + "r_output_tmp_" + System.nanoTime() + ".txt");
		if (outputFile.exists()) {
			boolean delete = outputFile.delete();
			if (!delete) {
				throw new Exception("Deletion of tmp file "
						+ outputFile.getAbsolutePath() + " failed");
			}
		}
		boolean create = outputFile.createNewFile();
		if (!create) {
			throw new Exception("Creation of tmp file "
					+ outputFile.getAbsolutePath() + " failed");
		}
	}

	/**
	 * Helper function to perform startup depending on the underlying OS.
	 * 
	 * @return
	 * @throws Exception
	 */
	private String[] startupCommand() throws Exception {
		String osD = DetectOS.getOS();

		if (osD.equals("mac") || osD.equals("unix")) {
			String command = "R --vanilla >> " + outputFile.getAbsolutePath();
			String[] cmd = { "/bin/sh", "-c", command };
			return cmd;
		} else if (osD.equals("windowslegacy")) {
			String command = "R --vanilla >> " + outputFile.getAbsolutePath()
					+ "";
			String[] cmd = { "command.com /c set", command };
			return cmd;
		} else if (osD.equals("windows")) {
			String command = "R --vanilla >> " + outputFile.getAbsolutePath()
					+ "";
			// String[] cmd = { "cmd.exe /c set", command };
			String[] cmd = { command }; // zegt danny
			return cmd;
		} else {
			throw new Exception("Operating system '"
					+ System.getProperty("os.name") + "' is not supported");
		}
	}

	/**
	 * Run the process and keep it alive until quit() is called or timeOut is
	 * reached.
	 */
	@Override
	public void run() {
		while (!quit) {
			try {
				Thread.sleep(1000);
				timeSinceLastResponse += 1;
				if (timeSinceLastResponse > timeOut) {
					System.out
							.println("RProcess timeout ("
									+ timeSinceLastResponse
									+ " seconds passed since last response, timeout set to "
									+ timeOut + " seconds), quitting");
					quit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Execute multiple commands
	 * 
	 * @param commands
	 * @return
	 * @throws Exception
	 */
	public List<String> executeMulti(List<String> commands) throws Exception {
		List<String> result = new ArrayList<String>();
		
		for(String command : commands)
		{
			result.addAll(execute(command));
		}
		
		return result;
	}

	/**
	 * Execute a single command
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public List<String> execute(String command) throws Exception {

		if (quit) {
			throw new Exception(
					"RProcess is no longer running and as such, no longer accepting new commands.");
		}
		
		// escape \ to \\
		command = command.replace("\\", "\\\\");
		
		//escape " to \"
		command = command.replace("\"", "\\\"");

		command = "tryCatch({eval(parse(text=\"" + command
				+ "\"))}, error = merrorfun, finally = mfinallyfun );\n";
		command += "print(\"" + requestDone + "\");\n";

		bos.write(command.getBytes());
		bos.flush();

		List<String> result = retrieveRawResults(requestDoneOutputMarker);

		if (result.size() > 2) {
			// Result looks like: command, answer(s), prompt. Get answer(s) as
			// response.
			result = result.subList(1, result.size() - 1);
		} else if (result.size() == 2) {
			// Result looks like: command, prompt. Empty list as response.
			result = new ArrayList<String>();
		} else {
			// Bad result. Empty list as response and throw error.
			result = new ArrayList<String>();
			String error = checkForErrors();
			if (error.length() > 0) {
				throw new Exception("Bad result: " + error);
			}
		}

		return result;
	}

	private String checkForErrors() throws IOException, Exception {
		if (bisError.available() > 0) {
			byte[] buff = new byte[bisError.available()];
			bisError.read(buff);
			String error = new String(buff);
			return error;
		}
		return "";
	}

	/**
	 * Allows the outside to see if the process is still running or not.
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return !quit;
	}

	/**
	 * Quits the thread. The in- and outputstream close() and file deletion
	 * statements are attempted after the quit is set to true, because it's more
	 * important to end the thread than to properly close the streams. Ofcourse,
	 * an exception is always thrown when something goes wrong. (ie. trying to
	 * close the streams)
	 * 
	 * @throws Exception
	 */
	public void quit() throws Exception {
		quit = true;
		bis.close();
		bisError.close();
		bos.close();
		boolean delete = outputFile.delete();
		if (!delete) {
			throw new Exception("Deletion of tmp file "
					+ outputFile.getAbsolutePath() + " failed");

		}
	}

}
