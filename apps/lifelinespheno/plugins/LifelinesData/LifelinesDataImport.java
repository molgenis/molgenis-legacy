/* Date:        February 7, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * Despoina Antonakaki , Erik Roos
 */

package plugins.LifelinesData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class LifelinesDataImport extends PluginModel<Entity>
{

	private static final long serialVersionUID = -4524281787934817724L;

	public LifelinesDataImport(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_LifelinesData_LifelinesDataImport";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/LifelinesData/LifelinesDataImport.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		int paIdx = 0; //name for protocal Application

		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
		if ("ImportLifelinesDataToPhenoModel".equals(request.getAction())) {
			System.out.println("start import Lifelines data .....");
			
				
			Query<Investigation> q = db.query(Investigation.class);

			//TODO : REMOVE HARDCODED !!!!!!!
			q.addRules(new QueryRule(Investigation.NAME, Operator.EQUALS,"Life Lines Study Dev1"));
            try {
				List<Investigation> valueList = q.find();
				Investigation investigation = null; 
				if (!valueList.isEmpty()) investigation = valueList.get(0);  // TODO : we can do better than this !!
				
				HashMap<String, Integer> fkMis = new HashMap<String, Integer>();
				//TODO : OMG REMOVE HARDCODED !!!!!!!

				File dir = new File("/Users/despoina/Documents/LifelinesData/data_dictionary/data");
                String dataSetName = "Top10";

                for (File file : dir.listFiles()) {
                    if(file.isHidden())
                        continue;                      // TODO : Can we do better that this ?? GOTO ???
                    
                    String fileName = file.getName();
					String tableName = fileName.substring(0, fileName.length() - 4);		
                    
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String headerLine = reader.readLine();
					
					// Get the column headers, clean them up and make corresponding features
					String[] columns = headerLine.split(",");
					Measurement[] features = new Measurement[columns.length];
					int i = 0;
					for (String column : columns) {
						if (column.contains("(") ) {
							columns[i] = column.substring(column.indexOf("(")+1, column.indexOf(")")).trim();
                        }
						
						List<Measurement> fList = db.find(Measurement.class, new QueryRule("name", Operator.EQUALS, tableName + "." + columns[i].toLowerCase().trim()));
						if (fList.size() > 0) {
							features[i] = fList.get(0);
						}
						
						i++;
					}
	
					List<Protocol> protocols = db.find(Protocol.class, new QueryRule("name",Operator.EQUALS, dataSetName));				
					
					while (reader.ready()) {
	                    db.beginTx();
	
						String line = reader.readLine();
						String[] values = line.split(",");
						
						ObservationTarget target = null;
						for (i = 0; i < values.length; ++i) {
							if(columns[i].equals("PA_ID")) { //TODO : OMG HARDCODED FIELDS OF THE FILE !!!! REMOVE -- config file .blepe lucene index fields  !!!
								//ObservationTarget example = new ObservationTarget();
								//example.setName(values[i]);
								//example.setInvestigation(investigation);
								
								//List<ObservationTarget> exampleTargets = db.findByExample(example); //latest exception thrown here ! 
								
								//if(exampleTargets.size() > 0) {
								//	target = exampleTargets.get(0);
								//}
								
								if(target == null) {
									target = new ObservationTarget();
									target.setInvestigation(investigation);
									target.setName(values[i]);
									db.add(target);
								}
							} else if (!values[i].trim().isEmpty() && features[i] != null) { 
								if(target != null) {
									ObservedValue ov = new ObservedValue();
									ov.setFeature(features[i]);
									ov.setTime(new Date());
									
									Measurement feature = features[i];
									//TODO: Danny: Use or Loose
									/*String type = */feature.getDataType();
								
									String value = values[i].trim();
									if(value.isEmpty()) {
										ov.setValue("empty");
									} else { //if (type.equals("string")) {
										ov.setValue(value);
									}
									
									ProtocolApplication pa = new ProtocolApplication();
									pa.setInvestigation(investigation);
									pa.setName("" + paIdx++);
									
									pa.setProtocol(protocols.get(0));
									pa.setTime(new Date());
									
									db.add(pa);
									
									ov.setProtocolApplication(pa);
									ov.setTarget(target);
									ov.setInvestigation(investigation);
		
									db.add(ov);
								}
							//Thread.sleep(1000);
							} else {
								if(fkMis.get(tableName) == null) {
									fkMis.put(tableName, 1);
								} else {
									fkMis.put(tableName, fkMis.get(tableName)+1);
								}
									
							}
						}
                        db.commitTx();
                        //db.getEntityManager().clear();  //TODO !
					}                               
                }
                
                System.out.println("Missing Features:");
        		for(String key : fkMis.keySet()) {
        			System.err.println(key + " " + fkMis.get(key));                    
                }
        		System.err.println("End!");		
				
				
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		
	}

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}

}
