package core.servlets;

import generic.CommandExecutor;
import generic.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/// Servlet to serve CGI script from the cgi-bin directory
//<p>
//The languages tested are:
//<ul>
//<li>Perl</li>
//<li>PHP</li>
//<li>Python</li>
//</ul>
//Python and PHP receive the input as a single string in the ARGV[1],
//parameters separated by ; then p=v for an example to parse these out see cgi-bin/test<br/>
//Perl users can 'use CGI' and then parse the param(), also an example in cgi-bin/test.<br/>
//Files not matching the known extensions are served via the common serveFile function using the mime.properties
//</p>

public class CGIServlet extends Servlet {
	private static final long serialVersionUID = 1L;
	String[][] extensions = new String[][]{
			{"pl","cgi","php","php3","py"},
			{"perl -X ","perl -X ","php -c php.ini -f ","php -c php.ini -f ","python -u "}
	};
	String mainpage = "index.cgi";
	
	String matchExtension(String ext){
		String r = "";
		int cnt  = 0;
		for(String s : extensions[0]){
			if(s.equals(ext)){
				r = extensions[1][cnt];
			}
			cnt++;
		}
		return r;
	}
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		CommandExecutor myCommandExe = new CommandExecutor();
		Thread myInterpreter;
		String arguments = "";
		String tempstring = "";
		long length = 0;
		String filename = "";
		String extension = "";
		String command = "";
		Utils.console(req.getMethod());
		filename = req.getPathTranslated() != null ? req.getPathTranslated().replace('/', File.separatorChar) : "";
		if(!filename.contains(""+File.separatorChar)){
			filename = ((filename.substring(0).equals("") || filename.substring(1).equals(""))? File.separator + mainpage : filename.substring(0));
		}else{
			filename = filename.substring(filename.indexOf(File.separatorChar));
		}
		extension = filename.substring(filename.indexOf(".", 2)+1);
		filename = "." + File.separator + "WebContent" + File.separator + "cgi-bin" + filename;
		Utils.console(filename);
		for (Enumeration<?> e = req.getParameterNames() ; e.hasMoreElements() ;) {
			if((tempstring = (String) e.nextElement()) !=  null){
				String param = req.getParameter(tempstring);
				param.replace(" ", "%20");
				param.replace("\t", "%20%20");
				param.replace("\n", "<br>");
				arguments += tempstring + "=" + param + ";";
			}
	    }

		File file = new File(filename);
		if(!file.exists()){
			Utils.console("No Such File: \n");
			arguments = "error=Page%20not%20found;page=" + filename + ";";
			file = new File("." + File.separator +  "WebContent" + File.separator + "cgi-bin"+ File.separator + mainpage);
			extension = mainpage.substring(mainpage.indexOf(".", 2)+1);
		}
		if(!(command = matchExtension(extension)).equals("")){
			Utils.console("Creating command: " + command + " " + file.getCanonicalPath() + " " + arguments);
			myCommandExe.addCommand(command + " " + file.getCanonicalPath() + " " + arguments);
		}else{
			Utils.log("No interpreter for: " + extension,System.err);
			serveFile(req, res,false, file);
		}
		myInterpreter = new Thread(myCommandExe);
		myInterpreter.start();
		try {
			myInterpreter.join(3000);
		} catch (InterruptedException e) {
			Utils.log("Interpretation of " + file.getAbsolutePath() + " didn't finish correctly",System.err);
		}
		OutputStream o = res.getOutputStream();
		tempstring = "" + myCommandExe.getResult();
		boolean contenttype = false;
		for(String s : tempstring.split("\n")){
			if(s != null && !s.equals("")){
				if(!contenttype && s.toLowerCase().startsWith("content-type:")){
					if(isLogenabled())Utils.console("Possible content type: " + s.split(": ")[1]);
					res.setContentType(s.split(": ")[1]);
					contenttype=true;
				}else{
					if(contenttype){
						o.write((s += "\n").getBytes());
						o.flush();
						length += (s.getBytes().length + 1);
					}
				}
			}
		}
		if(!contenttype){
			Utils.log("Unexpected output when serving file: " + file + ", no page", System.err);
			String err= "Unexpected output when serving file: " + file + ", no page generated<br/>";
			err+= "To check if an interpreter for the extension" + extension + "is available start a command shell and paste:<br/>";
			err+= command + " " + file.getCanonicalPath() + " " + arguments;
			o.write(err.getBytes());
			o.flush();
		}else{
			Utils.console("Served file: " + file + " " +  length + " bytes");
		}
		o.close();
	}

}
