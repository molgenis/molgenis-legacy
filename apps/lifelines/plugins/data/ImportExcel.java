package plugins.data;


import gcc.catalogue.GroupTheme;
import gcc.catalogue.ThemeProtocol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.springframework.ui.context.Theme;




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
		return "plugins_data_ImportExcel";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/ImportExcel.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

	
		
		if ("ImportDatashaperToPheno".equals(request.getAction())) {
			
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
	@SuppressWarnings("unchecked")
	public void loadDataFromExcel(Database db, Tuple request) throws BiffException, IOException, DatabaseException{
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));

		File file = new File(tmpDir+ "/DataShaperExcel.xls"); 
			
		if (file.exists()) {
			
			System.out.println("The excel file is being imported, please be patient");
			
			this.setStatus("The excel file is being imported, please be patient");
			
			Workbook workbook = Workbook.getWorkbook(file); 
			
			Sheet sheet = workbook.getSheet(0); 
				
			System.out.println(sheet.getCell(0, 0).getContents());
			
			List<Measurement> measurements = new ArrayList<Measurement>();
			
			List<Protocol> protocols = new ArrayList<Protocol>();
			
			List<ThemeProtocol> themeProtocols = new ArrayList<ThemeProtocol>();
			
			List<GroupTheme> groupThemes = new ArrayList<GroupTheme>();
			
			List<OntologyTerm> ontologyTerms = new ArrayList<OntologyTerm>();
			
			List<Code> codes = new ArrayList<Code>();
			
			int row = sheet.getRows();
			
			int column = sheet.getColumns();
			
			System.out.println(row);
			
			Measurement mea;
			
			Protocol prot;
			
			Code code;
			
			ThemeProtocol themeProtocol;
			
			GroupTheme groupTheme;
			
			List<String> ProtocolFeatures = new ArrayList<String>();
			
			List<String> ThemeProtocols = new ArrayList<String> ();
			
			List<String> GroupThemes = new ArrayList<String>();
			
			String protocolName = "";
			
			String ThemeProtocolname = "";
			
			String GroupThemeName = "";
			
			String measurementName = "";
			
			HashMap<String, List> linkProtocolMeasurement = new HashMap<String, List>();
			
			HashMap<String, List> linkThemeProtocol = new HashMap<String, List>();
			
			HashMap<String, List> linkGroupTheme = new HashMap<String, List>();
			
			HashMap<String, List> linkCodeMeasurement = new HashMap<String, List>();
			
			HashMap<String, String> linkUnitMeasurement = new HashMap<String, String>();
			
			for (int i = 1; i < row - 1; i++){
				
				mea = new Measurement();
				
				prot = new Protocol();
				
				themeProtocol = new ThemeProtocol();
				
				groupTheme = new GroupTheme();
				
				code = new Code();
				
				for(int j = 0; j < column; j++){
					
					if (j==0) { //theme is also a protocol 
						
						GroupThemeName = sheet.getCell(j,i).getContents().replace("'","");
						
						if (!linkGroupTheme.containsKey(GroupThemeName)) {
							
							GroupThemes = new ArrayList<String> ();
							linkGroupTheme.put(GroupThemeName, GroupThemes);
						}
						
						groupTheme.setName(GroupThemeName);
						
					}if (j==1) { //theme is also a protocol 
						
						ThemeProtocolname = sheet.getCell(j,i).getContents().replace("'","");
						
						if (!linkThemeProtocol.containsKey(ThemeProtocolname)) {
							
							ThemeProtocols = new ArrayList<String> ();
							linkThemeProtocol.put(ThemeProtocolname, ThemeProtocols);
						}
						
						themeProtocol.setName(ThemeProtocolname);
						
						List<String> temporaryHolder = linkGroupTheme.get(GroupThemeName);
						
						if(!temporaryHolder.contains(ThemeProtocolname)){
							temporaryHolder.add(ThemeProtocolname);
							linkGroupTheme.put(GroupThemeName, temporaryHolder);
						}
						
					}else if(j == 2){
					
						protocolName = sheet.getCell(j, i).getContents().replaceAll("'", "");
						
						if(!linkProtocolMeasurement.containsKey(protocolName)){
							ProtocolFeatures = new ArrayList<String>();
							linkProtocolMeasurement.put(protocolName, ProtocolFeatures);
						}
						
						prot.setName(sheet.getCell(j, i).getContents().replaceAll("'", ""));
						
						List<String> temporaryHolder = linkThemeProtocol.get(ThemeProtocolname);
						
						if(!temporaryHolder.contains(protocolName)){
							
							temporaryHolder.add(protocolName);
							linkThemeProtocol.put(ThemeProtocolname, temporaryHolder);
						}

					}else if(j == 3){
						
						measurementName = sheet.getCell(j, i).getContents();
						
						mea.setName(sheet.getCell(j, i).getContents());
						
						List<String> temporaryHolder = linkProtocolMeasurement.get(protocolName);
						
						if(!temporaryHolder.contains(protocolName)){
							
							temporaryHolder.add(measurementName);
							
							linkProtocolMeasurement.put(protocolName, temporaryHolder);
						}
							
					}else if (j == 4){
						
						mea.setDescription(sheet.getCell(j, i).getContents());
					
					}else if (j==5) {
						
						OntologyTerm unit = new OntologyTerm();
						String unitName = sheet.getCell(j,i).getContents().replace(" ", "_").replace("/", "_");
						unit.setName(unitName);
						
						if (unitName !="" && !ontologyTerms.contains(unit)) {
							ontologyTerms.add(unit);
							linkUnitMeasurement.put(measurementName, unitName);
						}
							
					}else if(j == 8){

						if(sheet.getCell(j, i).getContents().length() > 0 && sheet.getCell(j, i).getContents() != null){
							
							String [] codeString = sheet.getCell(j, i).getContents().split("\\|");
							
							for(int index = 0; index < codeString.length; index++){
								codeString[index] = codeString[index].trim();
							}
							//System.out.println(sheet.getCell(j, i).getContents());
							for(int k = 0; k < codeString.length; k++){
								
								code.setCode_String(codeString[k]);
								code.setDescription(sheet.getCell(j, i).getContents());
								
								if(linkCodeMeasurement.containsKey(codeString[k])){
									List<String> featuresCode = linkCodeMeasurement.get(codeString[k]);
									if(!featuresCode.contains(measurementName)){
										featuresCode.add(measurementName);
										linkCodeMeasurement.put(codeString[k], featuresCode);
									}
								}else{
									List<String> featuresCode = new ArrayList<String>();
									featuresCode.add(measurementName);
									linkCodeMeasurement.put(codeString[k], featuresCode);
								}
							}
							
							codes.add(code);
						}
					}
				}
				measurements.add(mea);
				
				protocols.add(prot);
				
				themeProtocols.add(themeProtocol);
				
				groupThemes.add(groupTheme);
			}
			
			List<Measurement> addedMeasurements = new ArrayList<Measurement>();
			
			List<Protocol> addedProtocols = new ArrayList<Protocol>();
			
			List<Code> addedCodes = new ArrayList<Code>();
			
			List<Protocol> addedThemes = new ArrayList<Protocol>();
			
			List<ThemeProtocol> addedThemeProtocols = new ArrayList<ThemeProtocol>();
			
			List<GroupTheme> addedGroupThemes = new ArrayList<GroupTheme>();
			
			for(Measurement measure : measurements){
				
				if(db.query(Measurement.class).eq(Measurement.NAME, measure.getName()).count() == 0){
					
					if(!addedMeasurements.contains(measure)){
						addedMeasurements.add(measure);
					}
				}
				
			}
			
			for( Protocol proto : protocols){
				
				if(db.query(Protocol.class).eq(Protocol.NAME, proto.getName()).count() == 0){
					if(!addedProtocols.contains(proto)){
						
						addedProtocols.add(proto);
					}
				}
			}
			
			for( GroupTheme group : groupThemes){
				
				if(db.query(GroupTheme.class).eq(GroupTheme.NAME, group.getName()).count() == 0){
					
					if(!addedGroupThemes.contains(group)){
						
						addedGroupThemes.add(group);
					}
				}
			}
			
			for( ThemeProtocol theme : themeProtocols){
				
				if(db.query(ThemeProtocol.class).eq(ThemeProtocol.NAME, theme.getName()).count() == 0){
					
					if(!addedThemeProtocols.contains(theme)){
						
						addedThemeProtocols.add(theme);
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
				
				db.add(ontologyTerms);
				
				//link Unit(ontologyTerm) to measurements 
				for (Measurement m: addedMeasurements) {
							
					String tmp = linkUnitMeasurement.get(m.getName());
					
					List<String> unitHolder = new ArrayList<String>();
					
					unitHolder.add(tmp);
					
					List<OntologyTerm> ontologyTermsList = db.find(OntologyTerm.class, new QueryRule(OntologyTerm.NAME, Operator.IN, unitHolder));
					
					for (OntologyTerm ot: ontologyTermsList) {
						m.setUnit_Id(ot.getId());
					}
				}
				
				db.add(addedMeasurements);
				
				// TEMPORARY FIX FOR MREF RESOLVE FOREIGN KEYS BUG
				for (Protocol p : addedProtocols) {
					
					if(linkProtocolMeasurement.containsKey(p.getName())){
						List<String> featureNames = linkProtocolMeasurement.get(p.getName());
						List<Measurement> measList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, featureNames));
						List<Integer> measIdList = new ArrayList<Integer>();
						for (Measurement m : measList) {
							measIdList.add(m.getId());
						}
						p.setFeatures_Id(measIdList);
					}
				}
				
				db.add(addedProtocols);
				
				for (ThemeProtocol theme : addedThemeProtocols) {
					
					if(linkThemeProtocol.containsKey(theme.getName())){
						List<String> protocoleNames = linkThemeProtocol.get(theme.getName());
						List<Protocol> protsList = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN, protocoleNames));
						List<Integer> protIdList = new ArrayList<Integer>();
						for (Protocol m : protsList) {
							protIdList.add(m.getId());
						}
						theme.setFeatures_Id(protIdList);
					}
				}
				db.add(addedThemeProtocols);
				
				for (GroupTheme group : addedGroupThemes) {
					
					if(linkGroupTheme.containsKey(group.getName())){
						List<String> themeNames = linkGroupTheme.get(group.getName());
						List<ThemeProtocol> themesList = db.find(ThemeProtocol.class, new QueryRule(ThemeProtocol.NAME, Operator.IN, themeNames));
						List<Integer> themeIdList = new ArrayList<Integer>();
						for (ThemeProtocol theme : themesList) {
							themeIdList.add(theme.getId());
						}
						group.setFeatures_Id(themeIdList);
					}
				}
				db.add(addedGroupThemes);
				
				for (Code c : addedCodes){
					List<String> featureNames = linkCodeMeasurement.get(c.getCode_String());
					List<Measurement> measList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, featureNames));
					List<Integer> meaIdList = new ArrayList<Integer>();
					for(Measurement m : measList){
						meaIdList.add(m.getId());
					}
					c.setFeature_Id(meaIdList);
				}
				
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