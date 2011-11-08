
package plugins.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.cxf.binding.corba.wsdl.Array;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import decorators.MeasurementDecorator;



public class ImportExcel extends PluginModel<Entity>
{
	private String Status = "";

	
	private static final long serialVersionUID = 6149846107377048848L;
	
	public ImportExcel(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getViewName()
	{
		return "plugins_data_importOwlToPheno";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/importOwlToPheno.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

	
		
		if ("ImportOwlToPheno".equals(request.getAction())) {
			
			System.out.println("----------------->");
			
			System.out.println(db.query(Investigation.class).eq(Investigation.NAME, " DataShaper").count());
			
			if(db.query(Investigation.class).eq(Investigation.NAME, "DataShaper").count() == 0){
				
				Investigation inv = new Investigation();
				inv.setName("DataShaper");
				db.add(inv);
				
			}
			loadDataFromExcel(db, request);

		}
		
	}
	public void loadDataFromExcel(Database db, Tuple request) throws BiffException, IOException, DatabaseException{
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		File file = new File(tmpDir+ "DataShaperExcel.xls"); 
		
		if (file.exists()) {
			
			System.out.println("The excel file is being imported, please be patient");
			this.setStatus("The excel file is being imported, please be patient");
			
			Workbook workbook = Workbook.getWorkbook(file); 
			
			Sheet sheet = workbook.getSheet(0); 
				
			System.out.println(sheet.getCell(0, 0).getContents());
			
			List<Measurement> measurements = new ArrayList<Measurement>();
			
			List<Protocol> protocols = new ArrayList<Protocol>();
			
			List<Code> codes = new ArrayList<Code>();
			
			int row = sheet.getRows();
			
			int column = sheet.getColumns();
			
			System.out.println(row);
			
			Measurement mea;
			
			Protocol prot;
			
			Code code;
			
			List<String> ProtocolFeatures = new ArrayList<String>();
			
			String Protocolname = "";
			
			HashMap<String, List> linkProtocolMeasurement = new HashMap<String, List>();
			
			for (int i = 1; i < row - 1; i++){
				
				mea = new Measurement();
				
				prot = new Protocol();
				
				code = new Code();
				
				for(int j = 0; j < column; j++){
					
					if(j == 2){
					
						Protocolname = sheet.getCell(j, i).getContents().replaceAll("'", "");
						
						if(!linkProtocolMeasurement.containsKey(Protocolname)){
							ProtocolFeatures.clear();
							linkProtocolMeasurement.put(Protocolname, ProtocolFeatures);
						}
						
						prot.setName(sheet.getCell(j, i).getContents().replaceAll("'", ""));
						
//						if(Protocolname.equals(sheet.getCell(j, i).getContents())){
//							ProtocolFeatures.add(sheet.getCell(j + 1, i).getContents());
//						}else{
//							System.out.println("--------------------->"+sheet.getCell(j, i).getContents());
//							prot.setName(sheet.getCell(j, i).getContents().replaceAll("'", ""));
//							prot.setFeatures_Name(ProtocolFeatures);
//							Protocolname = sheet.getCell(j, i).getContents();
//							ProtocolFeatures.clear();
//						}
					}else if(j == 3){
						
						mea.setName(sheet.getCell(j, i).getContents());
						
						List<String> temporaryHolder = linkProtocolMeasurement.get(Protocolname);
						
						temporaryHolder.add(sheet.getCell(j, i).getContents());
						
						linkProtocolMeasurement.put(Protocolname, temporaryHolder);
						
					}else if (j == 4){
						
						mea.setDescription(sheet.getCell(j, i).getContents());
					
					}else if(j == 8){

						if(sheet.getCell(j, i).getContents().length() > 0 && sheet.getCell(j, i).getContents() != null){
							
							String [] codeString = sheet.getCell(j, i).getContents().split("\\|");
							//System.out.println(sheet.getCell(j, i).getContents());
							for(int k = 0; k < codeString.length; k++){
								code.setCode_String(codeString[k]);
								//System.out.println("--------------------------->!!!!!!!!!!!!" + codeString[k]);
								code.setDescription(sheet.getCell(j, i).getContents());
							}
							codes.add(code);
						}
					}
				}
				measurements.add(mea);
				protocols.add(prot);
			}
			
			List<Measurement> addedMeasurements = new ArrayList<Measurement>();
			
			List<Protocol> addedProtocols = new ArrayList<Protocol>();
			
			List<Code> addedCodes = new ArrayList<Code>();
			
			for(Measurement measure : measurements){
				
				if(db.query(Measurement.class).eq(Measurement.NAME, measure.getName()).count() == 0){
					
					if(!addedMeasurements.contains(measure)){
						addedMeasurements.add(measure);
					}
				}
				
			}
			
			for( Protocol proto : protocols){
				proto.setFeatures_Name(linkProtocolMeasurement.get(proto.getName()));
				if(db.query(Protocol.class).eq(Protocol.NAME, proto.getName()).count() == 0){
					if(!addedProtocols.contains(proto)){
						
						addedProtocols.add(proto);
					}
				}
			}
			
			for( Code cod : codes){
				if(db.query(Code.class).eq(Code.CODE_STRING, cod.getCode_String()).count() == 0){
					if(!addedCodes.contains(cod)){
						addedCodes.add(cod);
					}
				}
			}
			try {
				db.add(addedMeasurements);
				db.add(addedProtocols);
				db.add(addedCodes);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("The file" + tmpDir + " was imported successfully");
			this.setStatus("The file" + tmpDir + " was imported successfully");
		} else {
			System.out.println("The excel file should be located here :"+ tmpDir + " and the name of the file should be DataShaperExcel.xls");
			this.setStatus("The excel file should be located here :"+ tmpDir + " and the name of the file should be DataShaperExcel.xls");
			
		}
		
	}
	@Override
	public void reload(Database db)	{
	}


	public void setStatus(String status) {
		Status = status;
	}


	public String getStatus() {
		return Status;
	}
}