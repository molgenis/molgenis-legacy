package core.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import core.servlets.Servlet;

/**
 * Serves static files such as images, css files and javascript from classpath.
 * This is servlet is used when serving from a Jar file in the Mortbay server.
 * Using tomcat the static serving is left to the container.
 */
public class tmpfileservlet extends Servlet
{
	private static final long serialVersionUID = 8579428014673624684L;
	private static Logger logger = Logger.getLogger(tmpfileservlet.class);

	/**
	 * Get a resource from the jar and copy it the the response.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		
		String url = request.getRequestURI();
		String variant = url.substring(url.indexOf("/")+1,url.indexOf("/tmpfile"));
		OutputStream out = response.getOutputStream();
		try{
			InputStream in = null;
			
			//get filename from used URL, so this is the only 'parameter'
			String urlBase = variant + "/tmpfile/";
			String urlFile = url.substring(urlBase.length()+1); 
					
			File tmpDir = new File(System.getProperty("java.io.tmpdir"));
			File filePath =  new File(tmpDir.getAbsolutePath() + File.separatorChar + urlFile);
			
			URL localURL = filePath.toURI().toURL();
			URLConnection conn = localURL.openConnection();

			in = new BufferedInputStream(conn.getInputStream());
			
			String mimetype = new MimetypesFileTypeMap().getContentType(filePath);
			logger.debug("mimetype for " + localURL + ": " + mimetype);
			response.setContentType(mimetype);
			response.setContentLength((int)filePath.length());


			byte[] buffer = new byte[2048];
			for( ;; ){
				int nBytes = in.read(buffer);
				if( nBytes <= 0 )
					break;
				out.write(buffer, 0, nBytes);
			}
			out.flush();

			logger.info("serving " + request.getRequestURI());
		}catch(Exception e ){
			byte[] header = ("Temporary file "+variant+" location error:\n").getBytes();
			out.write(header,0,header.length);
			byte[] exception = ("loading of failed: " + e).getBytes();
			out.write(exception,0,exception.length);
			logger.error("loading of failed: " + e);
		}finally{
			out.close();
		}
		
	}
}
