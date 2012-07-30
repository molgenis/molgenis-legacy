package plugins.emeasure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class EMeasure {
	
	PrintWriter out;
	Protocol protocol;
	Measurement measurement;
	Investigation investigation;
	Database db;
	
	private String filePath="";
	
	public EMeasure(Database db, String fileName) throws Exception {
		this.db = db;
		System.out.println(fileName);
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		
		out = new PrintWriter(new FileOutputStream(tmpDir + File.separator + fileName + ".xml"));
		
		filePath = tmpDir + File.separator + fileName + ".xml";
	}

	public String convert(List<Measurement> selectedMeasurements) throws Exception{
		StringBuffer out = new StringBuffer();
		
		out.append("<QualityMeasureDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"REPC_MT000100UV01.Organizer\" xsi:schemaLocation=\"urn:hl7-org:v3 multicacheschemas/REPC_MT000100UV01.xsd\" classCode=\"CONTAINER\" moodCode=\"DEF\" xmlns=\"urn:hl7-org:v3\">");
		
		
		for(Measurement m : selectedMeasurements){
			EMeasureMeasurement e = new EMeasureMeasurement();
			
			out.append(e.addXML(m,db));
		}
		
		
		out.append("</QualityMeasureDocument>");
		
		return this.format(out.toString());
	}

	public String getFilePath() {
		return filePath;
	}
	
	 public String format(String unformattedXml) {
	        try {
	            final Document document = parseXmlFile(unformattedXml);

	            OutputFormat format = new OutputFormat(document);
	            format.setLineWidth(65);
	            format.setIndenting(true);
	            format.setIndent(2);
	            Writer out = new StringWriter();
	            XMLSerializer serializer = new XMLSerializer(out, format);
	            serializer.serialize(document);

	            return out.toString();
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    private Document parseXmlFile(String in) {
	        try {
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            InputSource is = new InputSource(new StringReader(in));
	            return db.parse(is);
	        } catch (ParserConfigurationException e) {
	            throw new RuntimeException(e);
	        } catch (SAXException e) {
	            throw new RuntimeException(e);
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

}
