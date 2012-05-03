/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.genenetwork;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

public class GeneNetwork extends PluginModel
{
	private GeneNetworkModel model = new GeneNetworkModel();
	String baseurl = "http://www.genenetwork.org/webqtl/main.py";

	public GeneNetworkModel getMyModel()
	{
		return model;
	}

	public GeneNetwork(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "GeneNetwork";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/genenetwork/GeneNetwork.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			String action = request.getString("__action");

			try
			{
				if (action.equals("postInput"))
				{

					model.setDb(request.getString("db"));
					model.setFormat(request.getString("format"));
					model.setProbe(request.getString("probe"));
					model.setProbeset(request.getString("probeset"));

					ArrayList res = wrapGet(model.getProbeset(), model.getDb(), model.getProbe(), model.getFormat());

					model.setResult(res);

				}
				if (action.equals("submitFile"))
				{

					String resp = postData(request.getFile("theFile"));
					
					model.setUploadResponse(resp);
					
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{

	}

	ArrayList<String> wrapGet(String probeset, String db, String probe, String format)
	{
		ArrayList<String> ret = new ArrayList<String>();
		String cmd = "?cmd=";
		cmd += "get&probeset=" + probeset;
		cmd += "&db=" + db;
		cmd += "&probe=" + probe;
		cmd += "&format=" + format;
		String urlstring = baseurl + cmd;
		// http://www.genenetwork.org/webqtl/main.py?cmd=get&probeset=98332_at&db=bra08-03MAS5&probe=119637&format=col
		try
		{
			URL url = new URL(urlstring);
			URLConnection conn = url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null)
			{
				ret.add(line);
			}
			rd.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	private String readFromFile(File theFile) throws IOException
	{
		String content = "";

		// Open the file that is the first
		// command line parameter
		FileInputStream fstream = new FileInputStream(theFile);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		// Read File Line By Line
		while ((strLine = br.readLine()) != null)
		{
			// Print the content on the console
			content += strLine + "\n";
		}
		// Close the input stream
		in.close();

		return content;

	}

	private String postData(File theFile) throws IOException
	{
		String response = "";
		String data = URLEncoder.encode("RISet", "UTF-8") + "=" + URLEncoder.encode("BXD", "UTF-8");
		
		data += "&" + URLEncoder.encode("batchdatafile", "UTF-8") + "=" + URLEncoder.encode(readFromFile(theFile), "UTF-8");
		data += "&" + URLEncoder.encode("FormID", "UTF-8") + "=" + URLEncoder.encode("batSubmitResult", "UTF-8");
	//	data += "&" + URLEncoder.encode("Default_Name", "UTF-8") );
		
		
		URL url = new URL(baseurl);
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null)
		{
			// Process line...
			response += line;
		}
		wr.close();
		rd.close();
		return response;
	}

}
