package converters.dbgap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import converters.dbgap.jaxb.Study;
import converters.dbgap.jaxb.data_dict.Data_Dict;
import converters.dbgap.jaxb.var_report.Var_Report;


/**
 * Used primarily from the DbGapToPheno to access dbGaP ftp and download data
 */
public class DbGapService
{
	private Document document;
	private boolean debug = false;
	private List<Study> studyCache = null;
	private List<Data_Dict> dictionaryCache = null;
	private List<Var_Report> reportCache = null;
	private File fileCache = null;

	public DbGapService(URL url, File cache) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document = builder.parse(url.openStream());
		if(cache != null && !cache.exists()) throw new IOException("cache folder "+cache+" doesn't exist");
		fileCache = cache;
	}

	/**
	 * List all studies in dbGaP by browsing the FTP_Table_of_Contents.xml file.
	 * Alternative versions are listed as separated studies.
	 */
	public List<Study> listStudies() throws ParserConfigurationException, SAXException, IOException
	{
		if (studyCache == null)
		{
			Map<String, Study> studies = new TreeMap<String, Study>();

			for (Data_Dict d : listDictionaries())
			{
				if (studies.get(d.study_id) == null)
				{
					Study s = new Study();
					s.id = d.study_id;
					s.version = d.study_version;

					// find description
					NodeList dirs = document.getElementsByTagName("directory");
					for (int i = 0; i < dirs.getLength(); i++)
					{
						String name = dirs.item(i).getAttributes().getNamedItem("name").getNodeValue();
						if (name.startsWith(s.id))
						{
							if (dirs.item(i).getAttributes().getNamedItem("comment") != null) s.description = dirs
									.item(i).getAttributes().getNamedItem("comment").getNodeValue();
						}

					}
					studies.put(d.study_id + "." + d.study_version, s);
				}
			}

			System.out.println("listStudies: " + studies.values().size());
			studyCache = new ArrayList<Study>(studies.values());
		}
		return studyCache;
	}

	public List<Data_Dict> listDictionaries() throws SAXException, IOException, ParserConfigurationException
	{
		if (dictionaryCache == null)
		{
			List<Data_Dict> result = new ArrayList<Data_Dict>();

			// find files and filter on *data_dict*.xml
			NodeList files = document.getElementsByTagName("file");
			for (int i = 0; i < files.getLength(); i++)
			{
				String name = files.item(i).getAttributes().getNamedItem("name").getNodeValue();
				String link = files.item(i).getAttributes().getNamedItem("link").getNodeValue();
				String comment = null;
				if (files.item(i).getAttributes().getNamedItem("comment") != null) comment = files.item(i)
						.getAttributes().getNamedItem("comment").getNodeValue();

				if (name.contains("data_dict") && name.endsWith(".xml") && !link.contains("Archive"))
				{
					Data_Dict dd = new Data_Dict();
					String[] ids = name.split("\\.");
					dd.description = comment;
					dd.study_id = ids[0];
					dd.study_version = ids[1];
					dd.id = ids[2];
					dd.version = ids[3];
					dd.url = new URL(link);
					result.add(dd);
				}
			}
			System.out.println("listDictionaries: " + result.size());
			dictionaryCache = result;
		}
		return dictionaryCache;
	}

	public List<Var_Report> listVariableReports() throws SAXException, IOException, ParserConfigurationException
	{
		if (reportCache == null)
		{
			List<Var_Report> result = new ArrayList<Var_Report>();

			// find files and filter on *var_report*.xml
			NodeList files = document.getElementsByTagName("file");
			for (int i = 0; i < files.getLength(); i++)
			{
				String name = files.item(i).getAttributes().getNamedItem("name").getNodeValue();
				String link = files.item(i).getAttributes().getNamedItem("link").getNodeValue();
				String comment = null;
				if (files.item(i).getAttributes().getNamedItem("comment") != null) comment = files.item(i)
						.getAttributes().getNamedItem("comment").getNodeValue();

				if (name.contains("var_report") && name.endsWith(".xml") && !link.contains("Archive"))
				{
					Var_Report vr = new Var_Report();
					String[] ids = name.split("\\.");
					vr.description = comment;
					vr.study_id = ids[0];
					vr.study_version = ids[1];
					vr.dataset_id = ids[2];
					vr.version = ids[3];
					vr.url = new URL(link);
					result.add(vr);
				}
			}
			System.out.println("listVariableReports: " + result.size());
			reportCache = result;
		}
		return reportCache;
	}

	public void loadDictionaries(Study s) throws JAXBException, IOException, SAXException,
			ParserConfigurationException, InterruptedException
	{
		for (Data_Dict d : listDictionaries())
		{
			//System.out.println("testing "+ d.study_id +" = "+s.id + " having "+d.url);
			if (s.id.equals(d.study_id) && s.version.equals(d.study_version))
			{
				Data_Dict loaded = loadDictionary(d);
				loaded.description = d.description;
				loaded.url = d.url;
				loaded.study_id = d.study_id;
				s.dictionaries.add(loaded);
			}
		}
		System.out.println("loadDictionaries: " + s.dictionaries.size() + " loaded");

	}

	public void loadVariableReports(Study s) throws JAXBException, IOException, SAXException,
			ParserConfigurationException, InterruptedException
	{
		for (Var_Report r : listVariableReports())
		{
			//System.out.println("testing "+ r.study_id +" againsts "+s.id);
			if (r.study_id.equals(s.id) &&  s.version.equals(r.study_version))
			{
				Var_Report loaded = loadVariableReport(r);
				loaded.description = r.description;
				loaded.url = r.url;
				loaded.study_id = r.study_id;
				s.reports.add(loaded);

				if (debug) break;
			}
		}
		System.out.println("loadVariableReports: " + s.reports.size() + " loaded");

	}

	// public void loadSummaries(Study s) throws JAXBException, IOException,
	// SAXException, ParserConfigurationException,
	// InterruptedException
	// {
	// for (Data_Dict d : listVariableReports())
	// {
	// if (d.study_id.equals(s.id))
	// {
	// Data_Dict loaded = loadDictionary(d.url);
	// loaded.description = d.description;
	// loaded.url = d.url;
	// loaded.study_id = d.study_id;
	// s.dictionaries.add(loaded);
	// Thread.sleep(1000);
	// }
	// }
	// System.out.println("loadSummaries(): " + s.dictionaries.size());
	//
	// }

	public Var_Report loadVariableReport(Var_Report r) throws JAXBException, IOException
	{
		System.out.println("loadVariableReport from " + r.url);
		URL url = r.url;
		if(fileCache != null)
		{
			File cachedFile = new File(fileCache.getAbsolutePath() +"\\" + new File(new File(r.url.getFile()).getName()));
			if(!cachedFile.exists()) downloadFile(r.url, cachedFile);
			url = cachedFile.toURI().toURL();
			
		}
		JAXBContext jaxbContext = JAXBContext.newInstance("converters.dbgap.jaxb.var_report");
		Unmarshaller m = jaxbContext.createUnmarshaller();
		return (Var_Report) m.unmarshal(url.openStream());
	}

	public Data_Dict loadDictionary(Data_Dict d) throws JAXBException, IOException
	{
		System.out.println("loadDictionary from " + d.url);
		URL url = d.url;
		if(fileCache != null)
		{
			File cachedFile = new File(fileCache.getAbsolutePath() +"\\" + new File(new File(d.url.getFile()).getName()));
			if(!cachedFile.exists()) downloadFile(d.url, cachedFile);
			url = cachedFile.toURI().toURL();
		}
		JAXBContext jaxbContext = JAXBContext.newInstance("converters.dbgap.jaxb.data_dict");
		Unmarshaller m = jaxbContext.createUnmarshaller();
		return (Data_Dict) m.unmarshal(url.openStream());

	}
	
	public static void downloadFile(URL url, File destination) throws IOException
	{
		System.out.println("downloading " + url + " to " + destination);
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try
		{
			URLConnection urlc = url.openConnection();

			in = new BufferedInputStream(urlc.getInputStream());
			out = new BufferedOutputStream(new FileOutputStream(destination));

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}

		}
		finally
		{
			if (in != null) try
			{
				in.close();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			if (out != null) try
			{
				out.close();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		
		//because of NCBI abuse rules
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}


	}

}
