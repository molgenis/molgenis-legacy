package plugins.emeasure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;


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

	public void convert(List<Measurement> selectedMeasurements) throws Exception{
		out.append("<QualityMeasureDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"REPC_MT000100UV01.Organizer\" xsi:schemaLocation=\"urn:hl7-org:v3 multicacheschemas/REPC_MT000100UV01.xsd\" classCode=\"CONTAINER\" moodCode=\"DEF\" xmlns=\"urn:hl7-org:v3\">");
		
		
		for(Measurement m : selectedMeasurements){
			EMeasureMeasurement e = new EMeasureMeasurement();
			
			e.addXML(m,out,db);
		}
		
		
		out.append("</QualityMeasureDocument>");
		out.close();
	}

	public String getFilePath() {
		return filePath;
	}
}
