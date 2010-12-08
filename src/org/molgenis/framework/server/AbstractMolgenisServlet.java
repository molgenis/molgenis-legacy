package org.molgenis.framework.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.MolgenisOriginalStyle;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.UserInterface;
import org.molgenis.framework.ui.html.FileInput;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * Abstract MOLGENIS servlet. Implement abstract methods to get it to work.
 * TODO: make it use either context.xml or a properties file for configuration.
 */
public abstract class AbstractMolgenisServlet extends CXFNonSpringServlet
{
	/** the name of the datatype input */
	public static String INPUT_DATATYPE = "data_type_input";
	/** the name of the data input */
	public static String INPUT_DATA = "data_input";
	/** boolean indicating file upload */
	public static String INPUT_FILE = "data_file";
	/** the action input */
	public static String INPUT_ACTION = "data_action";
	/** the name of the submit button */
	public static String INPUT_SUBMIT = "submit_input";
	/** indicating wether uploads should return added data */
	public static String INPUT_SILENT = "data_silent";

	// get logger
	protected final transient Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	private static final long serialVersionUID = 3141439968743510237L;
	// whether cxf is already loaded
	private boolean cxfLoaded = false;

	/**
	 * You can override this method for alternative loading strategies.
	 * 
	 * @throws NamingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public abstract Database getDatabase() throws DatabaseException, NamingException, FileNotFoundException,
			IOException;

	/**
	 * Create a Login specific to the security scheme used in this MOLGENIS. You
	 * can override this to set a security mechanism.
	 */
	public abstract Login createLogin(Database db, HttpServletRequest request);

	/**
	 * Instantiate an application with the right root screen and optional file
	 * path...
	 */
	public abstract UserInterface createUserInterface(Login userLogin);

	/**
	 * @return package name of this molgenisvariant.
	 */
	// public static abstract String getMolgenisVariantID();

	/**
	 * @return the soap implementation
	 * @throws NamingException
	 * @throws DatabaseException
	 */
	public abstract Object getSoapImpl() throws DatabaseException, NamingException;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			this.service(request, response);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			this.service(request, response);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			this.service(request, response);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			this.service(request, response);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Frontcontroller of the MOLGENIS application. Based on paths, requests are
	 * delegated to the particular handlers.
	 * 
	 * @throws ServletException
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		// time the service
		long startTime = System.currentTimeMillis();
		logger.info("service started");

		// decide what handler to use based on request
		String path = request.getRequestURI();
		logger.debug("url: " + path);

		try
		{

			if (path != null && path.contains("/api/find"))
			{
				this.handleDownload(request, response);
			}
			else if (path != null && path.contains("/api/add"))
			{
				this.handleUpload(request, response);
			}
			else if (path != null && path.contains("/api/R"))
			{
				this.handleRAPIrequest(request, response);
			}
			else if (path != null && (path.contains("/api/soap")))
			{
				this.handleSOAPrequest(request, response);
			}
			else if (path != null && path.contains("/xref/find"))
			{
				this.handleXREFrequest(request, response);
			}
			else if (path != null && path.contains("/download/"))
			{
				this.handleDownloadFile(request, response);
			}
			// else if (path != null && path.contains("/rest/"))
			// {
			// // RestInterface.handleRequest(request, response,
			// // getDatabase());
			// this.handleSOAPrequest(request, response);
			// }
			else
			{
				this.handleGUIrequest(request, response);
			}
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NamingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// end timing the service
		logger.info("service completed in " + (System.currentTimeMillis() - startTime) + " milliseconds");
		logger.info("------------------------------");
	}

	private void handleDownloadFile(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		// setup the output-stream
		response.setBufferSize(10000);
		response.setContentType("text/html; charset=UTF-8");

		// get the relative path
		String filename = request.getRequestURI().substring(
				request.getServletPath().length() + request.getContextPath().length());
		logger.debug("THEFILETODOWNLOAD: " + filename);

		String tempDir = System.getProperty("java.io.tmpdir");
		logger.debug("TEMPDIR: " + tempDir);

		File file = new File(tempDir + "/" + filename);
		if (file.exists())
		{
			response.setContentType("application/x-download");
			response.setContentLength((int) file.length());

			FileInputStream in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();
			// Copy the contents of the file to the output stream
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0)
			{
				out.write(buf, 0, count);
			}
			in.close();
			out.close();
		}
		else
		{
			response.getWriter().write("file does not exist");
		}
	}

	private void handleSOAPrequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException, DatabaseException, NamingException
	{
		// delegate to CXF servlet
		// load the bus if needed
		if (this.cxfLoaded == false && (this.getSoapImpl() != null))// ||
		// this.getRestImpl()
		// != null))
		{
			super.loadBus(this.getServletConfig());

			Bus bus = this.getBus();

			// from samples/restfull directory

			// Build up the server factory bean
			// JaxRsServerFactoryBean sf = new JaxWsServerFactoryBean();
			// sf.setBus(bus);
			// sf.setServiceClass(this.getRestImpl().getClass());
			// // Use the HTTP Binding which understands the Java Rest
			// Annotations
			// sf.setBindingId(HttpBindingFactory.HTTP_BINDING_ID);
			// sf.setAddress("/rest/");
			// sf.getServiceFactory().setInvoker(new
			// BeanInvoker(this.getRestImpl()));
			// sf.getServiceFactory().setWrapped(false);

			// sf.create();

			BusFactory.setDefaultBus(bus);
			Endpoint.publish("/soap/", this.getSoapImpl());
			// Endpoint.publish("/rest/", this.getSoapImpl());

			// JAX-RS
			// JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
			// sf.setBus(bus);
			// sf.setResourceClasses(this.getRestImpl().getClass());
			// sf.setResourceProvider(this.getRestImpl().getClass(), new
			// SingletonResourceProvider(this.getRestImpl()));
			// sf.setAddress("/rest/");
			// sf.create();
			//			
			// JAX-WS
			// Endpoint.publish("/soap/", this.getSoapImpl());

			// setInterceptors(sf, this.getServletConfig(),
			// "jaxrs.inInterceptors");
			// setInterceptors(sf, this.getServletConfig(),
			// "jaxrs.outInterceptors");

			// JAX-WS
			// sf.setBindingId(HttpBindingFactory.);
			// JAXRSServerFactoryBean sf2 = new JAXRSServerFactoryBean();
			// sf2.setBus(bus);
			// sf2.setResourceClasses(this.getSoapImpl().getClass());
			// sf2.setResourceProvider(this.getSoapImpl().getClass(), new
			// SingletonResourceProvider(this.getSoapImpl()));
			// sf2.setAddress("/soap/");
			// sf2.create();

			// JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
			// //svrFactory.setBus(bus);
			// svrFactory.setServiceClass(this.getSoapImpl().getClass());
			// svrFactory.setAddress("/soap/");
			// svrFactory.setServiceBean(this.getSoapImpl());
			// svrFactory.create();

			// ServiceFactoryBean svrFactory = new CFactoryBean();
			// //svrFactory.setBus(bus);
			// svrFactory.setServiceClass(this.getSoapImpl().getClass());
			// svrFactory.setAddress("/soap/");
			// svrFactory.setServiceBean(this.getSoapImpl());
			// svrFactory.create();

			//			
			// //Ssf.setProvider(new AegisJSONProvider());

			this.cxfLoaded = true;
		}

		super.doPost(request, response);

	}

	public void handleGUIrequest(HttpServletRequest request, HttpServletResponse response) throws IOException,
			DatabaseException
	{
		// get database session (note: this shouldn't be in the tomcat
		// session!!!
		Database db = null;
		try
		{
			db = getDatabase();
			// db.beginTx(); DISCUSSION
			logger.info("created database " + db);
		}
		catch (Exception e)
		{
			logger.error("database creation failed: " + e);
			throw new DatabaseException(e);
		}

		// login/logout
		HttpSession session = request.getSession();
		Login userLogin = null;
		// Get appplication from session (or create one)
		ScreenModel molgenis = (UserInterface) session.getAttribute("application");
		if (molgenis == null)
		{
			userLogin = createLogin(db, request);
			if ((!userLogin.isAuthenticated() && userLogin.isLoginRequired())
					|| (request.getParameter("logout") != null && !session.isNew()))
			{
				response.setHeader("WWW-Authenticate", "BASIC realm=\"MOLGENIS\"");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				session.invalidate();
				return;
			}
			molgenis = createUserInterface(userLogin);
		}
		// ((UserInterface)molgenis).setDatabase(db);
		userLogin = ((UserInterface) molgenis).getLogin();
		db.setLogin(userLogin);

		// handle request
		try
		{
			Tuple requestTuple = new HttpServletRequestTuple(request, response);

			// action == download an attached file
			// FIXME move to form controllers handlerequest...
			if (FileInput.ACTION_DOWNLOAD.equals(requestTuple.getString(ScreenModel.INPUT_ACTION)))
			{
				logger.info(requestTuple);

				File file = new File(db.getFilesource() + "/"
						+ requestTuple.getString(FileInput.INPUT_CURRENT_DOWNLOAD));

				FileInputStream filestream = new FileInputStream(file);

				response.setContentType("application/x-download");
				response.setContentLength((int) file.length());
				response.setHeader("Content-Disposition", "attachment; filename="
						+ requestTuple.getString(FileInput.INPUT_CURRENT_DOWNLOAD));

				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				byte[] buffer = new byte[1024];
				int bytes_read;
				while ((bytes_read = filestream.read(buffer)) != -1)
				{
					out.write(buffer, 0, bytes_read);
				}
				filestream.close();
				out.flush();
				out.close();
			}

			// action == download, but now in a standard way, handled by
			// controller
			else if (ScreenModel.Show.SHOW_DOWNLOAD.equals(requestTuple.getString(FormModel.INPUT_SHOW)))
			{
				// get the screen that will hande the download request
				ScreenModel screen = molgenis.get(requestTuple.getString(ScreenModel.INPUT_TARGET));
				ScreenController controller = screen.getController();

				// set the headers for the download
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + screen.getName().toLowerCase()
						+ ".txt");

				// let the handleRequest produce the content
				PrintWriter out = new PrintWriter(response.getOutputStream());
				controller.handleRequest(db, requestTuple, out);
				out.flush();
				out.close();
			}

			// handle normal event and then write the response
			else
			{

				molgenis.getController().handleRequest(db, requestTuple);
				// handle request
				molgenis.getController().reload(db); // reload the application
				logger.debug("reloaded " + molgenis.getName() + " screen, rendering...");

				// ((UserInterface)molgenis).getDatabase().close();
				// ((UserInterface)molgenis).setDatabase(null);
				// session are automatically synchronized...
				session.setAttribute("application", molgenis);

				// prepare the response
				response.setContentType("text/html");
				// response.setBufferSize(10000);
				PrintWriter writer = response.getWriter();

				// Render result
				logger.info("create template");
				String templatefile = "Main.ftl";

				// Create a freemarker template
				logger.debug("trying to process template '" + templatefile + "'");
				Configuration freemarkerConf = this.getFreemarkerConfiguration(molgenis);
				Template freemarkerTemplate = freemarkerConf.getTemplate(templatefile);
				Map<String, Object> args = new TreeMap<String, Object>();
				args.put("title", molgenis.getLabel());
				args.put("application", molgenis);
				// args.put("username", userLogin.getUserName());
				
				
				/**
				 * Work in progress: mechanism to pass REST calls to underlying screens (ie. plugins! much needed!)
				for(int i = 0; i < requestTuple.size(); i ++){
					args.put(requestTuple.getFields().get(i), requestTuple.getString(i));
					//System.out.println("**** putting: " + requestTuple.getFields().get(i) + " TO " + requestTuple.getString(i));
				}
				*/

				// FIXME complex screen handling
				String show = requestTuple.getString(FormModel.INPUT_SHOW);
				if (show != null)
				{
					args.put("show", requestTuple.getString("__show"));

					// if dialog, only show target
					if (ScreenModel.Show.SHOW_DIALOG.equals(show))
					{
						ScreenModel target = molgenis.get(requestTuple.getString("__target"));
						args.put("target", target.getName());
						args.put("application", target);
						// args.put("show", "dialogue");
					}

					if (requestTuple.getString("__show").equals("massupdate"))
					{
						List<Object> massupdate = requestTuple.getList("massUpdate");
						// if empty list, create empty list
						if (massupdate == null) massupdate = new ArrayList<Object>();

						Vector<String> massupdate_updateable = new Vector<String>();
						Vector<String> massupdate_readonly = getVector(requestTuple.getObject("massUpdate_readonly"));

						for (Object id : massupdate)
						{
							if (!massupdate_readonly.contains(id)) massupdate_updateable.add(id.toString());
						}

						args.put("massupdate", massupdate_updateable);
					}
				}
				else
				{
					args.put("show", "root");
				}
				logger.info("applying layout template");
				
				// optional overlay of your HTML with linkouts to databases
				// TODO: make configurable
				boolean applyOverlayLinkouts = false;
				if(applyOverlayLinkouts){
					logger.info("applying linkout overlay");
					Writer result = new StringWriter();
					PrintWriter tmpWriter = new PrintWriter(result);
					freemarkerTemplate.process(args, tmpWriter);
					String templated = result.toString();
					LinkOutOverlay linker = new LinkOutOverlay();
					String afterOverlay = linker.addlinkouts(templated);
					writer.write(afterOverlay);
				}else{
					freemarkerTemplate.process(args, writer);
				}
				writer.close();
			}
			// db.commitTx(); DISCUSSION
		}
		catch (Exception e)
		{
			// response.getOutputStream().print(e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
			// unrecoverable error? does this take down the whole server?
			// throw new RuntimeException(e);
		}

	}

	/**
	 * Delegate to handle request for the R api.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void handleRAPIrequest(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();

		String filename = request.getRequestURI().substring(
				request.getServletPath().length() + request.getContextPath().length());
		logger.info("getting file: " + filename);
		logger.info("url: " + request.getRequestURL());
		logger.info("port: " + request.getLocalPort());
		logger.info("port: " + request.getLocalAddr());
		if (filename.startsWith("/")) filename = filename.substring(1);

		// if R file exists, return that
		if (!filename.equals("") && !filename.endsWith(".R"))
		{
			out.println("you can only load .R files");
		}
		else if (filename.equals(""))
		{
			logger.info("getting default file");
			// String server =
			// this.getMolgenisHostName()+request.getContextPath();
			String localName = request.getLocalName();
			if(localName.equals("0.0.0.0")) localName = "localhost";
			String server = "http://" + localName + ":" + request.getLocalPort()
					+ request.getContextPath();
			String rSource = server + "/api/R/";
			// getRequestURL omits port!
			out.println("#step1: (first time only) install RCurl package from omegahat or bioconductor, i.e. <br>");
			out.println("#source(\"http://bioconductor.org/biocLite.R\")<br>");
			out.println("#biocLite(\"RCurl\")<br>");
			out.println();
			out.println("#step2: source this file to use the MOLGENIS R interface, i.e. <br>");
			out.println("#source(\"" + rSource + "\")<br>");
			out.println();
			out.println("molgenispath <- paste(\"" + rSource + "\")");
			out.println();
			out.println("serverpath <- paste(\"" + server + "\")");
			out.println();
			out.println("#load autogenerated R interfaces<br>");
			out.println("source(\"" + rSource + "source.R\")");
			out.println();
			// out.println("#load R/qtl to XGAP functions<br>");
			// out.println("source(\"" + rSource + "rqtl.R\")");
			out.println();
			// out.println(
			// "#load dbGG specific extension to ease use of the Data <- DataElement structure as matrices<br>"
			// );
			// out.println("source(\"" + rSource + this.getMolgenisVariantID() +
			// "/R/DataMatrix.R\")");
			out.println();
			out.println("#connect to the server<br>");
			out.println("MOLGENIS.connect(\"" + server + "\")");
			// chdir=T means temporarily change working directory
		}
		// otherwise return the default R code to source all
		else
		{
			// the path may contain package name, e.g.
			// package.name.path/R/myclass.R
			filename = filename.replace(".", "/");
			filename = filename.substring(0, filename.length() - 2) + ".R";
			// map to hard drive, minus path papp/servlet
			File root = new File(this.getClass().getResource("source.R").getFile()).getParentFile().getParentFile()
					.getParentFile();

			if (filename.equals("source.R"))
			{
				root = new File(root.getAbsolutePath() + "/app/servlet");
			}
			File source = new File(root.getAbsolutePath() + "/" + filename);

			// up to root of app
			logger.info("trying to load R file: " + filename + " from path " + source);
			if (source.exists())
			{
				this.writeURLtoOutput(source.toURL(), out);
			}
			else
			{
				out.write("File '" + filename + "' not found");
			}
		}

		out.close();
	}

	/**
	 * Handle use of the download API.
	 * 
	 * @param request
	 * @param response
	 */
	public void handleDownload(HttpServletRequest request, HttpServletResponse response)
	{
		// setup the output-stream
		response.setBufferSize(10000);
		response.setContentType("text/html; charset=UTF-8");

		logger.info("starting download " + request.getPathInfo());
		long start_time = System.currentTimeMillis();

		HttpSession session = request.getSession();

		Database db = null;
		try
		{
			db = this.getDatabase();
			db.setLogin(new org.molgenis.framework.security.SimpleLogin());

			PrintWriter out = response.getWriter();

			try
			{

				// check whether a class is chosen
				if (request.getPathInfo() == null || request.getPathInfo().equals("/find"))
				{
					logger.debug("show 'choose entity' dialogue");
					out.println("<html><body>");
					out.println("You can download data:<br>");

					for (String className : db.getEntityNames())
					{

						if (request.getPathInfo() == null) out.println("<a href=\"find/" + className
								+ "?__showQueryDialogue=true\">" + className + "</a><br>");
						else
							out.println("<a href=\"" + className + "\">" + className + "</a><br>");
					}

					out.println("</body></html>");

					logger.debug("done");
					return;
				}
				String entityName = request.getPathInfo().substring(1);

				// check whether a querystring has to build
				if (request.getQueryString() != null && request.getQueryString().equals("__showQueryDialogue=true"))
				{
					logger.debug("show 'set filters' dialogue");
					out.println("<html><body><form>");
					out
							.println("You choose to download '"
									+ entityName
									+ "' data. (<a href=\"../api/find\">back</a>)<br><br> Here you can have to set at least one filter:<br>");
					out.println("<table>");
					for (String field : ((Entity) Class.forName(entityName).newInstance()).getFields())
					{
						out.println("<tr><td>" + field + "</td><td>=</td><td><input name=\"" + field
								+ "\" type=\"text\"></td><tr/>");
					}
					out.println("</table>");
					out.println("<SCRIPT>" + "function createFilterURL(fields)" + "{	" + "	var query = '';"
							+ "	var count = 0;" + "	for (i = 0; i < fields.length; i++) " + "	{"
							+ "		if (fields[i].value != '' && fields[i].name != '__submitbutton')" + "		{"
							+ "			if(count > 0)" + "				query +='&';"
							+ "			query += fields[i].name + '=' + fields[i].value;" + "			count++;" + "		}" + "	}"
							+ "	return query" + "}" + "</SCRIPT>");

					// with security break out.println( "<input
					// name=\"__submitbutton\" type=\"submit\" value=\"download
					// tab delimited file\"
					// onclick=\"if(createFilterURL(this.form.elements) != '')
					// {window.location.href = 'http://' + window.location.host
					// + window.location.pathname +
					// '?'+createFilterURL(this.form.elements); }return
					// false;\"><br>" );
					out
							.println("<input name=\"__submitbutton\" type=\"submit\" value=\"download tab delimited file\" onclick=\""
									+ "window.location.href = 'http://' + window.location.host + window.location.pathname + '?'+createFilterURL(this.form.elements);\"><br>");
					out.println("TIP: notice how the url is bookmarkeable for future downloads!");
					out.println("TIP: click 'save as...' and name it as '.txt' file.");
					out.println("</form></body></html>");
					return;
				}

				// create query rules
				List<QueryRule> rulesList = new ArrayList<QueryRule>();

				// use get
				if (request.getQueryString() != null)
				{
					logger.debug("handle find query via http-get: " + request.getQueryString());
					String[] ruleStrings = request.getQueryString().split("&");

					for (String rule : ruleStrings)
					{
						String[] ruleElements = rule.split("=");

						if (ruleElements.length != 2)
						{
							// throw new Exception( "cannot understand
							// querystring " + rule );
						}
						else if (ruleElements[1].startsWith("["))
						{
							ruleElements[1] = ruleElements[1].replace("%20", " ");
							String[] values = ruleElements[1].substring(1, ruleElements[1].indexOf("]")).split(",");
							rulesList.add(new QueryRule(ruleElements[0], QueryRule.Operator.IN, values));
						}
						else
						{
							if (ruleElements[1] != "" && !"__submitbutton".equals(ruleElements[0])) rulesList
									.add(new QueryRule(ruleElements[0], QueryRule.Operator.EQUALS, ruleElements[1]));
						}
					}
				}
				// use post
				else
				{
					Tuple requestTuple = new HttpServletRequestTuple(request);
					logger.debug("handle find query via http-post with parameters: " + requestTuple.getFields());
					for (String name : requestTuple.getFields())
					{
						if (requestTuple.getString(name).startsWith("["))
						{
							String[] values = requestTuple.getString(name).substring(1,
									requestTuple.getString(name).indexOf("]")).split(",");
							rulesList.add(new QueryRule(name, QueryRule.Operator.IN, values));
						}
						else
						{
							rulesList.add(new QueryRule(name, QueryRule.Operator.EQUALS, requestTuple.getString(name)));
						}
					}
				}

				// execute query
				CsvWriter writer = new CsvWriter(out);
				// CsvWriter writer = new CsvFileWriter( new
				// File("c:/testout.txt") );
				db.find((Class<Entity>) Class.forName(entityName), writer, rulesList.toArray(new QueryRule[rulesList
						.size()]));
			}
			catch (Exception e)
			{
				out.println(e + "<br>");
				e.printStackTrace();
				throw e;
			}
			finally
			{
				db.close();
			}

			out.close();
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		logger.info("servlet took: " + (System.currentTimeMillis() - start_time));
		logger.info("------------");
	}

	public void handleUpload(HttpServletRequest request, HttpServletResponse response)
	{
		// setup the output-stream
		response.setBufferSize(10000);
		response.setContentType("text/html; charset=UTF-8");

		logger.info("upload service started");
		long start_time = System.currentTimeMillis();

		try
		{
			Tuple requestTuple = new HttpServletRequestTuple(request);
			logger.info("parameters: " + requestTuple.getFields());
			// log paramaters
			// for( String s : r.getFields() )
			// {
			// logger.info("'" + s + "'=" + r.getObject(s));
			// }

			PrintWriter out = response.getWriter();
			// EntityReaderFactory readerFactory = new CsvReaderFactory();

			// if no type selected: show data type choice
			if (requestTuple.getString(INPUT_DATATYPE) == null)
			{
				try
				{
					out.println("<html><body><form method=\"post\" enctype=\"multipart/form-data\">");
					out.println("<h1>Data upload (step 1)</h1>");
					out.println("Choose your data type.");
					out.println("<table><tr><td><label>Data type:</label></td><td><select name=\"" + INPUT_DATATYPE
							+ "\">");

					for (Class c : this.getDatabase().getEntityClasses())
					{
						// write to screen
						out.println("<option value=\"" + c.getName() + "\">" + c.getName() + "</option>");
					}
					out.println("</select></td></tr>");
					out.println("<tr><td></td><td><input type=\"submit\" name=\"" + INPUT_SUBMIT
							+ "\" value=\"Submit\"></td></tr>");
					out.println("</table></form></body></html>");
				}
				catch (Exception e)
				{
					out.println("Upload failed: " + e.getMessage() + "");
					e.printStackTrace();
					throw e;
				}

			}
			// if no data provided, show csv input form
			else if (requestTuple.getObject(INPUT_DATA) == null && requestTuple.getObject(INPUT_FILE) == null)
			{
				try
				{
					String clazzName = requestTuple.getString(INPUT_DATATYPE);
					Class entityClass = Class.forName(clazzName);
					Entity template = (Entity) entityClass.newInstance();
				}
				catch (Exception e)
				{
					out.println("Upload failed: " + e.getMessage() + "");
					e.printStackTrace();
					throw e;
				}
			}
			// process request
			else
			{
				NumberFormat formatter = NumberFormat.getInstance(Locale.US);
				logger.info("processing add/update/delete");
				String action = null; // ADD, UPDATE, REMOVE
				Class entityClass = null;

				try
				{
					String clazzName = requestTuple.getString(INPUT_DATATYPE);
					entityClass = Class.forName(clazzName);

					// get the constants
					Tuple constants = new SimpleTuple();
					for (String column : requestTuple.getFields())
					{
						if (!column.equals(INPUT_DATATYPE) && !column.equals(INPUT_DATA)
								&& !column.equals(INPUT_ACTION) && !column.equals(INPUT_SUBMIT)
								&& !requestTuple.getString(column).equals(""))
						{
							constants.set(column, requestTuple.getObject(column));
						}
					}
					action = requestTuple.getString(INPUT_ACTION);
					// //out.println("Defaults: " + constants);

					// create a database
					Database db = this.getDatabase();

					// write to the database
					// logger.info("Adding parsed data to database:\n" +
					// r.getString(INPUT_DATA));

					int nRowsChanged = 0;
					if (action.equals("ADD"))
					{
						File temp = File.createTempFile("molgenis", "tab");
						CsvWriter writer = new CsvWriter(new PrintWriter(new BufferedWriter(new FileWriter(temp))));
						if (requestTuple.getObject(INPUT_SILENT) != null && requestTuple.getBool(INPUT_SILENT) == true)
						{
							writer.close();
							writer = null;
						}
						// List entities = db.toList(entityClass, new
						// CsvStringReader(r.getString(INPUT_DATA));

						if (requestTuple.getObject(INPUT_DATA) != null)
						{
							logger.info("processing textarea upload...");
							nRowsChanged = db.add(entityClass, new CsvStringReader(requestTuple.getString(INPUT_DATA)),
									writer);
						}
						else if (requestTuple.getObject(INPUT_FILE) != null)
						{
							logger.info("processing file upload...");
							nRowsChanged = db.add(entityClass, new CsvFileReader(requestTuple.getFile(INPUT_FILE)),
									writer);
						}
						else
						{
							logger.error("no input data or input file provided.");
							out.print("ERROR: no input data or input file provided.");
						}
						out.print("Uploaded " + formatter.format(nRowsChanged) + " rows of "
								+ entityClass.getCanonicalName() + "\n");

						if (writer != null) writer.close();
						BufferedReader reader = new BufferedReader(new FileReader(temp));
						String line = null;
						while ((line = reader.readLine()) != null)
						{
							out.println(line);
						}
						temp.delete();
					}
					else if (action.equals("UPDATE"))
					{
						if (requestTuple.getObject(INPUT_DATA) != null)
						{
							nRowsChanged = db.update(entityClass, new CsvStringReader(requestTuple
									.getString(INPUT_DATA)));
							out.print("Updated " + formatter.format(nRowsChanged) + " rows of "
									+ entityClass.getCanonicalName() + "\n");
						}
						else if (requestTuple.getObject(INPUT_FILE) != null)
						{
							nRowsChanged = db.update(entityClass, new CsvFileReader(requestTuple.getFile(INPUT_FILE)));
							out.print("Updated " + formatter.format(nRowsChanged) + " rows of "
									+ entityClass.getCanonicalName() + "\n");
						}
					}
					else if (action.equals("REMOVE"))
					{
						if (requestTuple.getObject(INPUT_DATA) != null)
						{
							nRowsChanged = db.remove(entityClass, new CsvStringReader(requestTuple
									.getString(INPUT_DATA)));
							out.print("Removed " + formatter.format(nRowsChanged) + " rows of "
									+ entityClass.getCanonicalName() + "\n");
						}
						else if (requestTuple.getObject(INPUT_FILE) != null)
						{
							nRowsChanged = db.remove(entityClass, new CsvFileReader(requestTuple.getFile(INPUT_FILE)));
							out.print("Removed " + formatter.format(nRowsChanged) + " rows of "
									+ entityClass.getCanonicalName() + "\n");
						}
					}
					else
					{
						throw new Exception("Unknown action " + action);
					}
				}
				catch (Exception e)
				{
					out.print("Failed to " + action + " " + entityClass.getName() + ": " + e.getMessage() + "");
					e.printStackTrace();
					throw e;
				}

			}

			out.close();
		}
		catch (Exception e)
		{
			logger.error(e);
			e.printStackTrace();
		}
		logger.info("servlet took: " + (System.currentTimeMillis() - start_time) + " ms");
		logger.info("------------");
	}

	// FIXME move away
	@SuppressWarnings("unchecked")
	public Vector<String> getVector(Object object)
	{

		Vector<String> vector = new Vector<String>();
		if (object != null)
		{
			if (object.getClass() == Vector.class)
			{
				vector = (Vector<String>) object;
			}
			else
			// one shift only
			{
				vector.addElement(object.toString());
			}
		}
		return vector;
	}

	/**
	 * Helper function to write an URL to an outputstream. E.g. used to pass
	 * files that are stored elsewhere as proxy.
	 * 
	 * @param source
	 * @param out
	 * @throws IOException
	 */
	private void writeURLtoOutput(URL source, PrintWriter out) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
		String sourceLine;
		while ((sourceLine = reader.readLine()) != null)
		{
			out.println(sourceLine);
		}
		reader.close();
	}

	/**
	 * get a freemarker configuration for rendering
	 * 
	 * @throws IOException
	 */
	private Configuration getFreemarkerConfiguration(ScreenModel userInterface) throws IOException
	{

		Configuration conf = (Configuration) this.getServletContext().getAttribute("freemarker");
		if (conf == null)
		{
			conf = new Configuration();
			// set the template loading paths
			conf.setObjectWrapper(new DefaultObjectWrapper());

			ClassTemplateLoader molgenistl = new ClassTemplateLoader(MolgenisOriginalStyle.class, "");
			ClassTemplateLoader plugins = new ClassTemplateLoader();
			// load templates from molgenis 'style' directory
			/*
			 * FileTemplateLoader plugintl; try { File f = new File(
			 * this.getServletContext().getRealPath( ("target/classes")));
			 * if(!f.exists()) throw new IOException(); plugintl = new
			 * FileTemplateLoader( f ); logger.debug("path target/classes does
			 * exist???"); } catch(IOException e) {
			 * logger.error("getFreemarkerConfiguration failed: path to
			 * target/classes doesn't exist"); throw e; }
			 */
			// load templates from the classpath (typically used for plugins)
			TemplateLoader[] loaders = new TemplateLoader[]
			{ molgenistl, plugins };
			MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
			conf.setTemplateLoader(mtl);
			// bring them all together as multiple loaders in freemarker

			// Walk trought the tree of user interface screens to find out which
			// template files to autoinclude
			if (userInterface.getViewTemplate() != null) conf.addAutoInclude(userInterface.getViewTemplate());
			for (ScreenModel screen : userInterface.getAllChildren())
			{
				if (screen.getViewTemplate() != null)
				{
					String path = screen.getViewTemplate();
					logger.debug("loading plugin template '" + path + "'");
					conf.addAutoInclude(path);
				}
			}

			this.getServletContext().setAttribute("freemarker", conf);
		}

		return conf;
	}

	public void handleXREFrequest(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		try
		{
			response.setHeader("Cache-Control", "max-age=0"); // allow no client
			response.setContentType("application/json");
			// side caching
			// .../xref/find?xref_entity=xxx&xref_field=yyyy&xref_label=zzzz&
			// filter=aaa
			// .../xref/find?xref_entity=xgap.data.types.Marker&xref_field=id&
			// xref_label=name&xref_filter=PV

			// alternatief => here the 'field' is the xref input itself
			// .../xref/find?entity=xxx&field=zzz&filter=aaaa

			Tuple req = new HttpServletRequestTuple(request);
			logger.debug("handling XREF request "+req);

			Class xref_entity = Class.forName(req.getString("xref_entity"));
			String xref_field = req.getString("xref_field");
			//get the xref labels from the string
			List<String> xref_labels = new ArrayList<String>();
			for (String label : req.getString("xref_label").split(","))
			{
				xref_labels.add(label.toString());
			}

			//List<QueryRule> xref_filters = QueryRuleUtil.fromRESTstring(req.getString("xref_filters"));
			String xref_label_search = req.getString("xref_label_search");

			logger.debug(xref_entity + " " + xref_field + " " + xref_labels + " " + xref_label_search);
			//List<String> queryFields = new ArrayList<String>();
			//queryFields.add(xref_field);
//			for (String xref_label : xref_labels)
//			{
//				queryFields.add(xref_label);
//			}
			
			//create a query on xref_entity
			Database db = getDatabase();
			
			//get the user interface and find the login
			HttpSession session = request.getSession();
			ScreenModel molgenis = (UserInterface) session.getAttribute("application");
			Login login = molgenis.getRootScreen().getLogin();
			db.setLogin(login);
			Query q = db.query(xref_entity);
			

			// create a CustomQuery
//			JoinQuery q = getDatabase().query(queryFields);
//			//q.addRules(xref_filters);
			if (xref_label_search != null && xref_label_search != "")
			{
				for (String xref_label : xref_labels)
				{
					q.like(xref_label, "%" + xref_label_search + "%");
					q.or();
					q.sortASC(xref_label);
				}
			}
			q.limit(100);

			List<Entity> result = q.find();

			// transform in JSON (JavaScript Object Notation

			String json = "{";
			for (int i = 0; i < result.size(); i++)
			{
				// logger.debug("using: " + result.get(i));
				if (i > 0) 
				{
					json += ",";

				}
				
				//write the xref key as set in xref_field
				json += result.get(i).get(xref_field).toString() + ":\"";
				
				//write the label(s) as set in xref_label
				for (int j = 0; j < xref_labels.size(); j++)
				{
					//hack
					if(j > 0) json += "|";
					json += result.get(i).get(xref_labels.get(j)).toString();
				}
				json += "\"";
				// logger.debug(result.get(i).get(xref_field) + ":\""
				// + result.get(i).get(xref_label) + "\"");
			}
			json += "}";
			
			logger.debug(json);
			
			//write out
			PrintWriter out = response.getWriter();
			out.print(json);
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	// public abstract Object getRestImpl() throws DatabaseException,
	// NamingException;

	@SuppressWarnings("unchecked")
	protected void setInterceptors(JAXRSServerFactoryBean bean, ServletConfig servletConfig, String paramName)
	{
		String value = servletConfig.getInitParameter(paramName);
		if (value == null)
		{
			return;
		}
		String[] values = value.split(" ");
		List<Interceptor> list = new ArrayList<Interceptor>();
		for (String interceptorVal : values)
		{
			String theValue = interceptorVal.trim();
			if (theValue.length() != 0)
			{
				try
				{
					Class<?> intClass = ClassLoaderUtils.loadClass(theValue, CXFNonSpringJaxrsServlet.class);
					list.add((Interceptor<? extends Message>) intClass.newInstance());
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		if (list.size() > 0)
		{
			if ("jaxrs.outInterceptors".equals(paramName))
			{
				bean.setOutInterceptors(list);
			}
			else
			{
				bean.setInInterceptors(list);
			}
		}
	}

}
