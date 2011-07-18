package servlets;

import generic.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/// Java Servlet implementation of a HTTP file server.
//<p>
//Implements the "GET" and "HEAD" methods for files and directories.
//Handles index.html, index.htm, default.htm, default.html.
//Redirects directory URLs that lack a trailing /.
//Handles If-Modified-Since.
//</p>

public class FileServlet extends Servlet {
	private static final long serialVersionUID = 1L;
	public static final String DEF_USE_COMPRESSION = "tjws.fileservlet.usecompression";
	static final String[] DEFAULTINDEXPAGES = { "index.html", "index.htm", "default.htm", "default.html" };
	static final DecimalFormat lengthftm = new DecimalFormat("#");
	private Method canExecute, getFreeSpace;


	// / Constructor.
	public FileServlet() {
		super();
		try {
			canExecute = File.class.getMethod("canExecute", Utils.EMPTY_CLASSES);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		try {
			getFreeSpace = File.class.getMethod("getFreeSpace", Utils.EMPTY_CLASSES);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		setUseCompression(System.getProperty(DEF_USE_COMPRESSION) != null);
	}
	
	// / Constructor.
	public FileServlet(String path) {
		this();
		setLocal_path(getLocal_path() + "/" + path);
	}
	
	// / Returns a string containing information about the author, version, and
	// copyright of the servlet.
	public String getServletInfo() {
		return "File servlet similar to httpd";
	}

	// / Services a single request from the client.
	// @param req the servlet request
	// @param req the servlet response
	// @exception ServletException when an exception has occurred
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		boolean headOnly;
		if (req.getMethod().equalsIgnoreCase("get") || req.getAttribute("javax.servlet.forward.request_uri") != null
				|| req.getAttribute("javax.servlet.include.request_uri") != null)
			headOnly = false;
		else if (req.getMethod().equalsIgnoreCase("head"))
			headOnly = true;
		else {
			res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
			return;
		}
		req.setCharacterEncoding(getCharSet());
		String path = Utils.canonicalizePath(req.getPathInfo());
		if(!getLocal_path().equals("")){
			if(path!=null){
				path = getLocal_path() + "/" + path;
			}else{
				path = getLocal_path() + "/";
			}
			
		}
		dispatchPathname(req, res, headOnly, path);
	}

	private void dispatchPathname(HttpServletRequest req, HttpServletResponse res, boolean headOnly, String path) throws IOException {
		String filename = req.getPathTranslated() != null ? req.getPathTranslated().replace('/', File.separatorChar) : path+"/";
		File file = new File(filename);
		if (isLogenabled()) Utils.console("retrieving '" + filename + "' for path " + req.getPathTranslated() + "<->" +  path);
		if (file.exists()) {
			if (!file.isDirectory()){
				serveFile(req, res, headOnly, file);
				Utils.console("Served file: " + file + " " +  file.length()/1024 + " Kb");
			} else {
				if (isLogenabled()) Utils.console("showing dir " + file);
				if (redirectDirectory(req, res, path, file) == false){
					showIdexFile(req, res, headOnly, path, filename);
				}
			}
		} else{
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
			
	}

	private void showIdexFile(HttpServletRequest req, HttpServletResponse res, boolean headOnly, String path,
			String parent) throws IOException {
		if (isLogenabled()) Utils.console("Showing index in directory " + parent);
		for (int i = 0; i < DEFAULTINDEXPAGES.length; i++) {
			File indexFile = new File(parent, DEFAULTINDEXPAGES[i]);
			if (indexFile.exists()) {
				serveFile(req, res, headOnly, indexFile);
				return;
			}
		}
		// index not found
		serveDirectory(req, res, headOnly, path, new File(parent));
	}



	private void serveDirectory(HttpServletRequest req, HttpServletResponse res, boolean headOnly, String path,	File file) throws IOException {
		if (isLogenabled()) Utils.console("Indexing directory: " + file);
		if (!file.canRead()) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html;charset=" + getCharSet());
		OutputStream out = res.getOutputStream();
		Long freespace =(long) 0;
		if (!headOnly) {
			String[] names = file.list();
			if (names == null) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN, "Can't access " + req.getRequestURI());
				return;
			}
			PrintStream p = new PrintStream(new BufferedOutputStream(out), false, getCharSet()); // 1.4
			p.println("<html><head>");
			p.println("<meta HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + getCharSet() + "\">");
			p.println("<title>Index of " + path + "</title>");
			p.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+req.getServletPath()+"/themes/default.css\"></style>");
			p.println("</head><body>");
			p.println("<div align=\"center\"><h1>Index of " + path + "</h1></div>");
			p.println("<pre>");
			p.println("mode         bytes  last-changed    name");
			p.println("<HR>");
			Arrays.sort(names);
			long total = 0;
			for (int i = 0; i < names.length; ++i) {
				File aFile = new File(file, names[i]);
				String aFileType;
				long aFileLen;
				if (aFile.isDirectory())
					aFileType = "d";
				else if (aFile.isFile())
					aFileType = "-";
				else
					aFileType = "?";
				String aFileRead = (aFile.canRead() ? "r" : "-");
				String aFileWrite = (aFile.canWrite() ? "w" : "-");
				String aFileExe = "-";
				if (canExecute != null)
					try {
						if (((Boolean) canExecute.invoke(aFile, Utils.EMPTY_OBJECTS)).booleanValue())
							aFileExe = "x";
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					} catch (InvocationTargetException e) {
					}
				if(getFreeSpace != null)
					try {
						freespace = ((Long) getFreeSpace.invoke(aFile, Utils.EMPTY_OBJECTS)).longValue() / 1024;
					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					} catch (InvocationTargetException e) {
					}
				String aFileSize = lengthftm.format(aFileLen = aFile.length());
				total += Math.round(((aFileLen) + 1023) / 1024); // 
				while (aFileSize.length() < 12)
					aFileSize = " " + aFileSize;
				String aFileDate = generic.Utils.lsDateStr(new Date(aFile.lastModified()));
				while (aFileDate.length() < 14)
					aFileDate += " ";
				String aFileDirsuf = (aFile.isDirectory() ? "/" : "");
				String aFileSuf = (aFile.isDirectory() ? "/" : "");
				p.println(aFileType + aFileRead + aFileWrite + aFileExe + "  " + aFileSize + "  " + aFileDate + "  "
						+ "<a href=\"" + URLEncoder.encode(names[i], getCharSet()) /* 1.4 */
						+ aFileDirsuf + "\"><font color=\"black\">" + names[i] + aFileSuf + "</font></a>");
			}
			p.println("Used: " + total + " KB of " + (freespace/1024) + " MB");
			p.println("</pre>");
			p.println("<hr><ul><li><a href=\"http://localhost:8080/\"><font color=\"black\">Back to index</font></a></li></ul>");
			p.println("</body></html>");
			p.flush();
		}
		out.close();
	}

	/**
	 * 
	 * @param req
	 *            http request
	 * @param res
	 *            http response
	 * @param path
	 *            web path
	 * @param file
	 *            file system path
	 * @return true if redirection required and happened
	 * @throws IOException
	 *             in redirection
	 */
	private boolean redirectDirectory(HttpServletRequest req, HttpServletResponse res, String path, File file)
			throws IOException {
		int pl = path.length();
		if (pl > 0 && path.charAt(pl - 1) != '/') {
			// relative redirect
			int sp = path.lastIndexOf('/');
			if (sp < 0)
				path += '/';
			else
				path = path.substring(sp + 1) + '/';
			if (isLogenabled()) Utils.console("Redirecting dir " + path);
			res.sendRedirect(path);
			return true;
		}
		return false;
	}
}