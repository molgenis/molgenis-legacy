/* Date:        October 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.mazeexperiment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.maze.MazeData;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class rawdataimporter extends PluginModel<Entity>
{
	/**
	 * @author A.S. Boerema
	 */
	private static final long serialVersionUID = 3045772744420484691L;
	private boolean success;
	//private String fileName;
	private List<String> fileNames = new ArrayList<String>();
	private List<String> origFileNames = new ArrayList<String>();
	private int pcId;
	private String fileList;
	//private int duplicateCtr;


	public String getFileList() {
		return fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}

	/**
	 * the method that takes care of loading a data file. 
	 * 
	 * @author A.S. Boerema
	 * @param Database
	 * @param pc Id
	 * @param unit Id
	 * @return boolean succes
	 * 	 
	 */
	private boolean loadDataFile(final Database db, int year, int month, int day, final String fileName, final int pc, final int unit) throws Exception{
		try{

			//check existing records for the current day:

			Query<MazeData> q; 
			q = db.query(MazeData.class);
			q.addRules(new QueryRule("year", Operator.EQUALS, year));
			q.addRules(new QueryRule("month", Operator.EQUALS, month));
			q.addRules(new QueryRule("day", Operator.EQUALS, day));
			q.addRules(new QueryRule(Operator.SORTASC,"hour, minute, second, milisecond"));
			//List<MazeData> existingData = q.find()day;
			final int existingRows = q.count();



			/*db.beginTx();
			Query<BinaryChannelData> bcdq;
			bcdq = db.query(BinaryChannelData.class);
			bcdq.addRules(new QueryRule("channelid", Operator.EQUALS, this.currChlId));
			bcdq.addRules(new QueryRule(Operator.SORTASC,"timestamp"));
			List<BinaryChannelData> channelSwitchData = bcdq.find();
			db.commitTx();
			 */


			final ArrayList<MazeData> newRecords = new ArrayList<MazeData>(); // create a list for the new records

			File file = new File(fileName);

			CsvFileReader reader = new CsvFileReader(file);
			// set headers manually because they are not provided in the fiel
			// set them on fake numbers to handle the variable amount of spaces used to separted fields. This result in incorrect field detection and needs to be corrected later. 
			List<String> fakeFields =  Arrays.asList("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","21","22","23","24","25","26","27","28","29","30","31");
			//List<String> realFields =  Arrays.asList("year","month","day","hour","minute","second,","milisecond","port0","port1","port2","port3","port4","port5");

			reader.setColnames(fakeFields);
			reader.disableHeader(false);
			reader.parse(new CsvReaderListener()			
			{
				int lineCtr = 0;
				public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
				{
					//System.out.println("parsed line " + line_number + ": " + tuple.toString());
					lineCtr++;
					if(lineCtr > existingRows){
						//iterate over the fields and check for null.
						MazeData newRow = new MazeData();
						int fieldSequence=0;
						String field="";
						for (int i=0; i<32; i++){
							field = tuple.getString("UNNAMED"+Integer.toString(i));
							//logger.debug("field "+ Integer.toString(i) + " " +field);

							if(field != null){
								fieldSequence++;
								switch(fieldSequence){
								// year
								case 1: newRow.setYear(Integer.parseInt( field )); 
								//logger.info("Set Year = " + field);
								break;

								//month
								case 2: newRow.setMonth(Integer.parseInt( field ));
								//logger.info("Set Month = " + field);
								break;
								//day
								case 3: newRow.setDay(Integer.parseInt( field )); 
								//logger.info("Set Day = " + field);
								break;
								//hour
								case 4: newRow.setHour(Integer.parseInt( field )); 
								//logger.info("Set Hour = " + field);
								break;
								//minute
								case 5: newRow.setMinute(Integer.parseInt( field )); 
								//logger.info("Set Minute =  " + field);
								break;
								//second
								case 6: newRow.setSecond(Integer.parseInt( field )); 
								//logger.info("Set second = " + field);
								break;

								//millisecond
								case 7: newRow.setMilisecond(Integer.parseInt( field )); 
								//logger.info("Set millisecond = " + field);
								break;
								//Port0
								case 8: newRow.setPort0(Integer.parseInt( field ));
								//logger.info("Set port0 = " + field);
								break;

								//Port1
								case 9: newRow.setPort1(Integer.parseInt( field )); break;

								//Port2
								case 10: newRow.setPort2(Integer.parseInt( field )); break;

								//Port3
								case 11: newRow.setPort3(Integer.parseInt( field )); break;

								//Port4
								case 12: newRow.setPort4(Integer.parseInt( field )); break;

								//Port5
								case 13: newRow.setPort5(Integer.parseInt( field ));
								//logger.info("Set port5 = " + field);
								break;

								default: logger.debug("actually this switch should never reach default..."); break;

								}//end switch

							}//endif

						}//end for

						// add the extra fields:
						newRow.setPcid(pc);  			
						newRow.setUnitid(unit);
						newRow.setConversiontype(0); 	// Set conversiontype to 0 (is not converted) upon import.
						//add to the db

						newRecords.add(newRow);
					}
				}
			});

			// add all new rows to the database
			db.add(newRecords);
			return true;
		}	
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public rawdataimporter(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugin_animaldb_mazeexperiment_rawdataimporter";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/animaldb/mazeexperiment/rawdataimporter.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// clear the old messages
		ScreenMessage msg = null;
		this.setMessages(new Vector<ScreenMessage>()); // clear messsages

		try 
		{
			String action = request.getString("__action");

			if( action.equals("cancel") )
			{
				this.setFileList(null);
				this.fileNames.clear();
				this.origFileNames.clear();

			}
			if (action.equals("loadMazeDataFile1")) {

				// show a list of the files to be imported
				// and add them to a listobject for further processing

				// get the first file
				if(request.getString("mazedatafile").equals(null)) {

					msg = new ScreenMessage("You did not enter a filename",null,false);
				} else {

					//FIXME: you sure this is not OriginalFileName  ?
					
					this.fileNames.add(0,request.getString("mazedatafile"));
					this.origFileNames.add(0,request.getString("mazedatafileOriginalfilename"));

					// get the rest of the files if present
					List<String> fields = request.getFields();
					int requestSize = request.size();
					int fileCtr = 1;

					for (int i = 1; i <= requestSize; i++) {

						String mazedatafile = "mazedatafile" + Integer.toString(fileCtr);
						String mazedatafileOriginalfilename = "mazedatafile"+ Integer.toString(fileCtr) + "Originalfilename";

						if(fields.contains(mazedatafile)){

							this.fileNames.add(fileCtr,request.getString(mazedatafile));
							this.origFileNames.add(fileCtr,request.getString(mazedatafileOriginalfilename));

						}
						fileCtr++;
					}

					String fileList ="<p>you uploaded the following files:</p><p><ol>";
					for (String file : this.origFileNames ) {
						fileList = fileList +"<li>" + file + "</li>";
					}
					fileList = fileList+ "</ol></p><p> Do you want to import the data in the database? </p>";
					this.setFileList(fileList);

				}
			}

			if( action.equals("loadMazeDataFile2") )
			{	
				int fileCtr = 0;
				for (String file: this.fileNames) {
					String fileName = this.origFileNames.get(fileCtr);
					int extensionStart = fileName.indexOf(".");
					int unitId = Integer.parseInt(fileName.substring(extensionStart-1, extensionStart));
					int year =  Integer.parseInt(fileName.substring(0,4));
					int month = Integer.parseInt(fileName.substring(4,6));
					int day = Integer.parseInt(fileName.substring(6,8));

					this.success = this.loadDataFile(db, year, month, day, file, this.pcId, unitId);
					if (this.success) {

						msg = new ScreenMessage("The file \"" + this.origFileNames.get(fileCtr) +"\" Is succesfully imported",null,true);
						this.getMessages().add(msg);

					}else {

						msg = new ScreenMessage("The file \"" + this.origFileNames.get(fileCtr) +"\" could not be imported",null,false);
						this.getMessages().add(msg);

					}
					fileCtr++;
				}

				// prepare for next upload
				this.setFileList(null);
				this.fileNames.clear();
				this.origFileNames.clear();
			}

		} catch(Exception e)
		{
			e.printStackTrace();
			msg = new ScreenMessage("Something went horribly wrong.. ",null,false);
			this.getMessages().add(msg);
			//e.g. show a message in your form
		}
		// add status messages to the screen




	}

	@Override
	public void reload(Database db)
	{

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
}
