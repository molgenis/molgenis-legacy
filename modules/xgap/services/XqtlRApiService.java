package services;

import generic.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.molgenis.cluster.RScript;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.FrontControllerAuthenticator;
import org.molgenis.framework.server.FrontControllerAuthenticator.LoginStatus;
import org.molgenis.framework.server.FrontControllerAuthenticator.LogoutStatus;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.framework.server.MolgenisServiceAuthenticationHelper;

import decorators.MolgenisFileHandler;

/** Use seperate servlet because of the custom R script that needs to be added */
public class XqtlRApiService implements MolgenisService
{

	private MolgenisContext mc;

	public XqtlRApiService(MolgenisContext mc)
	{
		this.mc = mc;
	}

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{

		// as used in /molgenis/src/org/molgenis/generators/R/RApiGen.R.ftl,
		// must match!
		String pwdString = MolgenisServiceAuthenticationHelper.LOGIN_PASSWORD;
		String usrString = MolgenisServiceAuthenticationHelper.LOGIN_USER_NAME;

		// Utils.console("starting RApiServlet");
		OutputStream outs = response.getResponse().getOutputStream();
		PrintStream out = new PrintStream(new BufferedOutputStream(outs), false, "UTF8"); // 1.4

		if (request.getString(usrString) != null && request.getString(pwdString) != null)
		{
			String usr = request.getString(usrString);
			String pwd = request.getString(pwdString);

			LoginStatus login = FrontControllerAuthenticator.login(request, usr, pwd);

			String responseLine;
			if (login == LoginStatus.ALREADY_LOGGED_IN)
			{
				responseLine = "You are already logged in. Log out first.";
			}
			else if (login == LoginStatus.SUCCESSFULLY_LOGGED_IN)
			{
				responseLine = "Welcome, " + usr + "!";
			}
			else if (login == LoginStatus.AUTHENTICATION_FAILURE)
			{
				responseLine = "User or password unknown.";
			}
			else if (login == LoginStatus.EXCEPTION_THROWN)
			{
				responseLine = "An error occurred. Contact your administrator.";
			}
			else
			{
				throw new IOException("Unknown login status: " + login);
			}

			writeResponse(response, responseLine, out);
			return;
		}

		if (request.getString("logout") != null && request.getString("logout").equals("logout"))
		{

			LogoutStatus logout = FrontControllerAuthenticator.logout(request);

			String responseLine;
			if (logout == LogoutStatus.ALREADY_LOGGED_OUT)
			{
				responseLine = "You are already logged out. Log in first.";
			}
			else if (logout == LogoutStatus.SUCCESSFULLY_LOGGED_OUT)
			{
				responseLine = "You are successfully logged out.";
			}
			else if (logout == LogoutStatus.EXCEPTION_THROWN)
			{
				responseLine = "An error occurred. Contact your administrator.";
			}
			else
			{
				throw new IOException("Unknown logout status: " + logout);
			}

			writeResponse(response, responseLine, out);
			return;
		}

		// Utils.console("filename is now: " + filename);
		String filename = request.getRequestPath().substring(request.getServicePath().length());

		String s = "";

		if (filename.startsWith("/"))
		{
			filename = filename.substring(1);
		}
		// if R file exists, return that
		if (!filename.equals("") && !filename.endsWith(".R"))
		{
			// Utils.console("bad request: no R extension");
			s += "you can only load .R files\n";
		}
		else if (filename.equals(""))
		{

			// Utils.console("getting default file");
			String rSource = request.getAppLocation() + request.getRequestPath();
			rSource = rSource.endsWith("/") ? rSource : rSource + "/";
			// getRequestURL omits port!
			s += ("#first time only: install RCurl and bitops\n");
			s += ("#install.packages(\"RCurl\", lib=\"~/libs\")\n");
			s += ("#install.packages(\"bitops\", lib=\"~/libs\")\n");
			s += ("\n");
			s += ("#load RCurl and bitops\n");
			s += ("library(bitops, lib.loc=\"~/libs\")\n");
			s += ("library(RCurl, lib.loc=\"~/libs\")\n");
			s += ("\n");
			s += ("#robust sourcing function for URLs\n");
			s += ("msource <- function(murl = \"http://127.0.0.1:8080/xqtl/api/R/\", verbose = TRUE){\n");
			s += ("  if(verbose) cat(\"Creating connection\",murl,\"\\n\")\n");
			s += ("  data <- getURLContent(murl)\n");
			s += ("  t <- tempfile()\n");
			s += ("  writeLines(data, con=t)\n");
			s += ("  sys.source(t,globalenv())\n");
			s += ("  unlink(t)\n");
			s += ("}\n");
			s += ("\n");
			s += ("#location of this R API and the application itself\n");
			s += ("r_api_location <- paste(\"" + rSource + "\")\n");
			s += ("app_location <- paste(\"" + request.getAppLocation() + "\")\n");
			s += ("\n");
			s += ("#load autogenerated R interfaces\n");
			s += ("msource(\"" + rSource + "source.R\")\n");
			s += ("\n");
			s += ("#load XGAP specific extension to use R/qtl\n");
			s += ("msource(\"" + rSource + "xgap/R/RqtlTools.R\")\n");
			s += ("\n");
			s += ("#load XGAP specific extension to ease use of the Data <- DataElement structure as matrices\n");
			s += ("msource(\"" + rSource + "xgap/R/DataMatrix.R\")\n");
			s += ("\n");
			s += ("#load cluster calculation scripts\n");

			File[] listing = new File((this.getClass().getResource("../plugins/cluster/R/ClusterJobs/R")).getFile())
					.listFiles();
			if (listing != null)
			{
				for (File f : listing)
				{
					s += ("msource(\"" + rSource + "plugins/cluster/R/ClusterJobs/R/" + f.getName() + "\")\n");

				}
			}
			else
			{
				s = ("#No R files seem available; did you generate R?");
			}
			s += ("\n");

			// quick addition for demo purposes
			s += ("#loading user defined scripts\n");
			try
			{
				List<RScript> scripts = request.getDatabase().find(RScript.class);
				for (RScript script : scripts)
				{
					s += ("msource(\"" + rSource + "userscripts/" + script.getName() + ".R\")\n");
				}
				s += ("\n");
				s += ("#connect to the server\n");
				s += ("MOLGENIS.connect()\n");
				s += ("\n");
				s += ("#--> login/logout using:\n");
				s += ("# MOLGENIS.login(\"username\",\"password\")\n");
				s += ("# MOLGENIS.logout()\n");
				s += ("\n");
			}
			catch (Exception e)
			{
				s += "#No database connection available to handle R-api";
				// throw new IOException(e);
			}

			// quick addition for demo purposes
		}
		else if (filename.startsWith("userscripts/"))
		{
			try
			{
				Database db = request.getDatabase();
				String name = filename.substring(12, filename.length() - 2);
				// Utils.console("getting '"+name+".r'");
				QueryRule q = new QueryRule("name", Operator.EQUALS, name);
				RScript script = db.find(RScript.class, q).get(0);
				MolgenisFileHandler mfh = new MolgenisFileHandler(db);
				File source = mfh.getFile(script, db);
				Utils.console("printing file: '" + source.getAbsolutePath() + "'");
				String str = this.printUserScript(source.toURI().toURL(), "", name);
				s += (str);
			}
			catch (Exception e)
			{
				throw new IOException(e);
			}

		}
		else
		{
			// otherwise return the default R code to source all
			// Utils.console("getting specific R file");
			filename = filename.replace(".", "/");
			filename = filename.substring(0, filename.length() - 2) + ".R";
			// map to hard drive, minus path app/servlet
			File root = new File(app.servlet.FrontController.class.getResource("source.R").getFile()).getParentFile()
					.getParentFile().getParentFile();

			if (filename.equals("source.R"))
			{
				root = new File(root.getAbsolutePath() + "/app/servlet");
			}
			File source = new File(root.getAbsolutePath() + "/" + filename);

			// up to root of app
			// Utils.console("trying to load R file: " + filename +
			// " from path " + source);
			if (source.exists())
			{
				String str = this.printScript(source.toURI().toURL(), "");
				s += (str);
			}
			else
			{
				s += ("File '" + filename + "' not found\n");
			}
			// Utils.console("done getting specific R file");
		}
		writeResponse(response, s, out);
		// Utils.console("closed & flushed");

	}

	private void writeResponse(MolgenisResponse response, String responseLine, PrintStream out) throws IOException
	{
		response.getResponse().setStatus(HttpServletResponse.SC_OK);
		response.getResponse().setContentLength(responseLine.length());
		response.getResponse().setCharacterEncoding("UTF8");
		response.getResponse().setContentType("text/plain");
		out.print(responseLine);
		out.flush();
		out.close();
		response.getResponse().flushBuffer();
	}

	private String printScript(URL source, String out) throws IOException
	{
		// Utils.console("reading file to be outputted");
		BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
		String sourceLine;
		while ((sourceLine = reader.readLine()) != null)
		{
			out += sourceLine + "\n";
		}
		reader.close();
		// Utils.console("done reading");
		return out;
	}

	private String printUserScript(URL source, String out, String scriptName) throws IOException
	{
		// Utils.console("reading file to be outputted");
		BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
		out += "run_"
				+ scriptName
				+ " <- function(dbpath, subjob, item, jobid, outname, myanalysisfile, jobparams, investigationname, libraryloc){\n";
		String sourceLine;
		while ((sourceLine = reader.readLine()) != null)
		{
			if (!sourceLine.trim().equals(""))
			{
				if (!sourceLine.trim().endsWith("{") && !sourceLine.trim().endsWith("}"))
				{
					out += "cat(Generate_Statement(\"" + sourceLine.replace("\"", "'")
							+ "\"),file=myanalysisfile,append=T)\n";
				}
				else
				{
					out += "cat(\"" + sourceLine.replace("\"", "'") + "\n\",file=myanalysisfile,append=T)\n";
				}
			}
			else
			{
				// Utils.console("Removing empty line");
			}
		}
		out += "}";
		reader.close();
		// Utils.console("done reading");
		return out;
	}
}