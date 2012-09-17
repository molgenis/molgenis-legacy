package plugins.mazeexperiment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.maze.BinaryChannelData;
import org.molgenis.maze.ChannelMapping;
import org.molgenis.maze.MazeData;

import commonservice.CommonService;

public class ConvertRawToBinaryData {

	private Database db;
	private CommonService ct;
	private List<MazeData> Mdlist;					// the resulting list containing the selected raw data
	//private BinaryChannelData[] AllPreviousSwitchEvents	= new BinaryChannelData[500];	// a list of switchevents to be aggregated within a second (arraysize depends on th # of channels present, for now fix on 500)
	//private BinaryChannelData[] SwitchEvents = new BinaryChannelData[53000];	//(size = ports x ms = 48000 + size of query batch (5000))
	private BinaryChannelData[] AllPreviousSwitchEvents; 
	private BinaryChannelData[] SwitchEvents; 
	private MazeData[] mazeDataRowUpdates;
	private int ImportCtr;
	private int RowCtr;
	private int rowUpdateCtr;
	private String statusmessage;

	public ConvertRawToBinaryData(Database db) throws Exception {
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
	}
	public String getStatusmessage() {
		return statusmessage;
	}

	public void setStatusmessage(String statusmessage) {
		this.statusmessage = statusmessage;
	}

	public void getData(String pcid, String unitid, String year, String month, String day, String hour, String minute, String second, String milisecond) throws DatabaseException, ParseException, IOException {

		//Calendar calendar = Calendar.getInstance();
		//Date now = calendar.getTime();
		Query<MazeData> q;
		q = db.query(MazeData.class);

		// build the select query based on arguments
		if (pcid != "*" ) {
			q.addRules(new QueryRule("pcid", Operator.EQUALS, pcid));
		}
		if (unitid != "*" ) {
			q.addRules(new QueryRule("unitid", Operator.EQUALS, unitid));
		}
		if (year != "*" ) {
			q.addRules(new QueryRule("year", Operator.EQUALS, year));
		}
		if (month != "*" ) {
			q.addRules(new QueryRule("month", Operator.EQUALS, month));
		}
		if (day != "*" ) {
			q.addRules(new QueryRule("day", Operator.EQUALS, day));
		}
		if (hour != "*" ) {
			q.addRules(new QueryRule("hour", Operator.EQUALS, hour));
		}
		if (minute != "*" ) {
			q.addRules(new QueryRule("minute", Operator.EQUALS, minute));
		}
		if (second != "*" ) {
			q.addRules(new QueryRule("second", Operator.EQUALS, second));
		}
		if (milisecond != "*" ) {
			q.addRules(new QueryRule("milisecond", Operator.EQUALS, milisecond));
		}		

		// select only events that are not yet converted.
		q.addRules(new QueryRule("conversiontype",Operator.EQUALS, 0));
		// for now add default sorting order (ascending by date)
		q.addRules(new QueryRule(Operator.SORTASC,"year"));
		q.addRules(new QueryRule(Operator.SORTASC,"day"));
		q.addRules(new QueryRule(Operator.SORTASC,"month"));
		q.addRules(new QueryRule(Operator.SORTASC,"hour"));
		q.addRules(new QueryRule(Operator.SORTASC,"minute"));
		q.addRules(new QueryRule(Operator.SORTASC,"second"));
		q.addRules(new QueryRule(Operator.SORTASC,"milisecond"));


		// query the raw data table	
		this.Mdlist = q.find();

		//return Mdlist;
	}


	public String printData() throws Exception {

		String result = "";
		int datacounter = 0;

		if (this.Mdlist.size() > 0){

			Iterator<MazeData> MdIterator = this.Mdlist.iterator();
			while (MdIterator.hasNext()) {
				datacounter++;
				MazeData currentrow = MdIterator.next();

				//int pcid = currentrow.getPcid();
				//int unitid = currentrow.getUnitid();
				int year = currentrow.getYear();
				int month = currentrow.getMonth();
				int day = currentrow.getDay();
				int hour = currentrow.getHour();
				int minute = currentrow.getMinute();
				int second = currentrow.getSecond();
				int milisecond = currentrow.getMilisecond();
				int port0 = currentrow.getPort0();			
				int port1 = currentrow.getPort1();
				int port2 = currentrow.getPort2();
				int port3 = currentrow.getPort3();
				int port4 = currentrow.getPort4();
				int port5 = currentrow.getPort5();

				String binstr = Integer.toBinaryString(256 + (255-port0));
				String binport0 = binstr.substring(binstr.length() -8);
				binstr = Integer.toBinaryString(256 + (255-port1));
				String binport1 = binstr.substring(binstr.length() -8);
				binstr = Integer.toBinaryString(256 + (255-port2));
				String binport2 = binstr.substring(binstr.length() -8);
				binstr = Integer.toBinaryString(256 + (255-port3));
				String binport3 = binstr.substring(binstr.length() -8);
				binstr = Integer.toBinaryString(256 + (255-port4));
				String binport4 = binstr.substring(binstr.length() -8);
				binstr = Integer.toBinaryString(256 + (255-port5));
				String binport5 = binstr.substring(binstr.length() -8);

				result = result + " " + binport0 + " " + binport1 +" " + binport2 + " " + binport3 + " " + binport4 + " " + binport5
				+ " --> " +Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day)  
				+ " " + Integer.toString(hour) + "-" + Integer.toString(minute) + "-" + Integer.toString(second) + "." + Integer.toString(milisecond) + "<br>";

				//this.dbval = result;
				this.statusmessage = " <p> succesfully printed the data </p>";
			}
		}
		else
		{
			this.statusmessage = " <p> <b>there is no data in the list </b> </p>";
		}

		return result;
	}

	private void convertPort(int port,int rowid, int pcid, int unitid, int portid, boolean SameSecond, Date switchdate ) throws DatabaseException, ParseException, IOException {
		Logger logger = Logger.getLogger(getClass().getSimpleName());

		//logger.debug("args: " + Integer.toString(port) +", " + Integer.toString(pcid) +", " + Integer.toString(unitid) +", " + SameSecond + ", "+ switchdate);
		//port0
		if(port == 255){
			//logger.info("** skip: portstate is: "+ Integer.toString(port) + "port="+ Integer.toString(portid)+ " rownr=" + Integer.toString(this.RowCtr));
			
		}else{


			//logger.info("**process: portstate is: "+ Integer.toString(port) + "port="+ Integer.toString(portid)+ " rownr=" + Integer.toString(this.RowCtr));

			String byteStr = Integer.toBinaryString(256 + (255-port));  // invert the bits (255 indicates no switch events therefore all bits 0, not 1
			// add 256 and remove it later to add leading zero's 
			byteStr = byteStr.substring(byteStr.length() -8);
			//logger.debug("** port val = "+ byteStr);
			int value;
			List<ChannelMapping> ChmpList;
			// split the byte
			for (int bit=0 ; bit<8; bit++) {
				value = Integer.parseInt(byteStr.substring(bit, bit+1));
				if (value > 0 ){
					// get the channelid
					Query<ChannelMapping> chq;
					chq = db.query(ChannelMapping.class);
					chq.addRules(new QueryRule("pcnumber", Operator.EQUALS, pcid));
					chq.addRules(new QueryRule("unitnumber", Operator.EQUALS, unitid));
					chq.addRules(new QueryRule("portnumber", Operator.EQUALS, portid));
					chq.addRules(new QueryRule("bitnumber", Operator.EQUALS, bit));
					ChmpList = chq.find();
					//logger.debug("***after dbquery");
					logger.debug(chq);
					int chn = ChmpList.get(0).getChannelnumber();
					int chid = ChmpList.get(0).getId();
					//logger.debug("***after chn chid");


					//check for previous switchevent in the same second

					if (SameSecond) {

						//logger.debug("***** same second: adding potential events");

						//update the existing switchevent for this port if it is present
						//PrevPortSwitchStates[port] = PrevPortSwitchStates[port] + 1 ;
						//logger.debug("***before allprev");
						if(this.AllPreviousSwitchEvents[chn] != null ){
							//logger.debug("***after before allprev");
							// get the switch state from the existing event
							int prevss = this.AllPreviousSwitchEvents[chn].getSwitchstate();
							this.AllPreviousSwitchEvents[chn].setSwitchstate(prevss + 1);
							this.AllPreviousSwitchEvents[chn].setRecordId(rowid);
							//logger.debug("****** update prevswitchstate (" + Integer.toString(prevss)+") with 1");
						}else {
							//logger.debug("****** prev se does not exist");

							//set the values on switchevent
							BinaryChannelData SwitchEvent = new BinaryChannelData();
							SwitchEvent.setTimestamp(switchdate);
							SwitchEvent.setChannelid(chid);
							SwitchEvent.setSwitchstate(1);
							SwitchEvent.setRecordId(rowid);
							// set the first switch event for this port
							this.AllPreviousSwitchEvents[chn] = SwitchEvent;
							//String debugstring = "***event fields: " 
							//	+ ", " + SwitchEvent.getTimestamp()
							//	+ ", " + Integer.toString(SwitchEvent.getChannelid())
							//	+ ", " + Integer.toString(SwitchEvent.getSwitchstate());
							//logger.debug("******  creating event with fields:" + debugstring);

						}



					}else {
						//logger.debug("***** not the same second: creating event");
						//logger.info("** NO previous switch event present");

						// create a new previousswitchevent for this port

						//BinaryChannelData switchEvent = new BinaryChannelData();
						BinaryChannelData SwitchEvent = new BinaryChannelData();
						SwitchEvent.setTimestamp(switchdate);
						SwitchEvent.setSwitchstate(1);
						SwitchEvent.setChannelid(chid);
						SwitchEvent.setRecordId(rowid);
						//SwitchEvent.setChannelid_channelnumber(chn);

						// add the switchevent to the previousswitcheventarraylist
						this.AllPreviousSwitchEvents[chn] = SwitchEvent;

						//String debugstring = "***event fields: "
						//	+ ", " + SwitchEvent.getTimestamp()
						//	+ ", " + Integer.toString(SwitchEvent.getChannelid())
						//	+ ", " + Integer.toString(SwitchEvent.getSwitchstate());
						//logger.debug("***** not the same second: creating event with fields:" + debugstring);
					}


				}//endif
			}//endfor
		}//endif (port == 255)


	}

	public void convertData( ) throws DatabaseException, ParseException, IOException {

		//only add the channels where something has changed to the binary table.

		String previousDateString = null;
		String dateString = null;
		Date switchdate;
		int QueryCounter = 0;
		this.AllPreviousSwitchEvents = new BinaryChannelData[500];	// a list of switchevents to be aggregated within a second (arraysize depends on th # of channels present, for now fix on 500)
		this.SwitchEvents = new BinaryChannelData[53000];	//(size = ports x ms = 48000 + size of query batch (5000))
		Arrays.fill(this.AllPreviousSwitchEvents, null);
		Arrays.fill(this.SwitchEvents, null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		boolean SameSecond = false;	 
		boolean lastloop = false;
		//int rowoffset = 1;

		Logger logger = Logger.getLogger(getClass().getSimpleName());

		//this.SkipImportCtr = 0;
		this.ImportCtr = 0;
		this.RowCtr = 0;
		this.rowUpdateCtr = 0;

		Iterator<MazeData> MdIterator = this.Mdlist.iterator();

		int nrofrows = this.Mdlist.size();
		int stoploop = nrofrows+1;
		this.mazeDataRowUpdates = new MazeData[nrofrows+1];
		Arrays.fill(this.mazeDataRowUpdates, null);
		
		if (nrofrows > 0){
			while (this.RowCtr <= stoploop  || lastloop) {
				//logger.debug("* While start, rowctr = " +Integer.toString(this.RowCtr));
				
				//if ( this.RowCtr >= stoploop){ 
				//	lastloop = true;
				//}

				// update the database in batches of 1000 queries to improve speed
				// do not start the batch if aggregation over a second is still busy
				if (QueryCounter >= 5000  || lastloop){
					// fire queries to the db
					//logger.debug("** QC > 1000 : QC = " + Integer.toString(QueryCounter));
					try {

						try { 
							// try to commit the previous transachtion if one is still open
							db.commitTx();
						}
						catch(DatabaseException e) {
							logger.debug(e);
						}

						logger.info("******* starting new transaction ");
						db.beginTx();

						logger.info("******* batch adding events ");
						// add Switch events to the binary channeldata table
						for (BinaryChannelData event : this.SwitchEvents) {
							if(event != null){
								//Date timestamp = event.getTimestamp();
								//int State = Switch;
								this.ImportCtr++; 
								db.add(event);
								//String debugstring = "***event fields: "
									//+ ", " + event.getTimestamp()
									//+ ", " + Integer.toString(event.getChannelid())
									//+ ", " + Integer.toString(event.getSwitchstate());

								//logger.info("*** adding switchEvent [" + Integer.toString(this.ImportCtr) + " "+ debugstring );
							}

						}
						Arrays.fill(this.SwitchEvents, null);
						//logger.info("******* batch added events ");      
						
						logger.info("******* setting row changed ");
						
						for (MazeData updateevent : this.mazeDataRowUpdates){
							if(updateevent != null){
								this.rowUpdateCtr++;
								db.update(updateevent);
								//logger.info("*** updated row " + Integer.toString(this.rowUpdateCtr));
							}
								
						}
						Arrays.fill(this.mazeDataRowUpdates, null);
						//logger.info("******* batch updated row conversion field ");
						db.commitTx();
						QueryCounter = 0;
						if(lastloop){break;}
					}
					catch(Exception e){
						logger.debug("Exception in adding events, rolling back transaction: "+ e); 
						db.rollbackTx();
					}

				}
				/*
				 * Loop over the the rows to add swithc events to the batch update list or /aggregate switch events within a second
				 * before adding the compound event to the batchlist.
				 */

				else{
					// process rows 
					if(!MdIterator.hasNext()){
						
						lastloop = true;	//check for end of list, set boolean last loop te permit one more cycle to update the db with the batchlist
						logger.info("** lastloop = true ");
					}
					else {
						//logger.info("** get next mazedata row ");
						MazeData currentrow = MdIterator.next();    // get the next datarow
						this.RowCtr++; 								// increase the rowcounter
						//logger.debug("Increased rowcounter to: "+ Integer.toString(this.RowCtr));
						//logger.info("******* processing row [" + Integer.toString(this.RowCtr)+ "]");

						// get the datavalues
						try{
							int rowid = currentrow.getId();
							int pcid = currentrow.getPcid();
							int unitid = currentrow.getUnitid();				
							int year = currentrow.getYear();
							int month = currentrow.getMonth();
							int day = currentrow.getDay();
							int hour = currentrow.getHour();
							int minute = currentrow.getMinute();
							int second = currentrow.getSecond();
							//int milisecond = currentrow.getMilisecond();
							int port0 = currentrow.getPort0();			
							int port1 = currentrow.getPort1();
							int port2 = currentrow.getPort2();
							int port3 = currentrow.getPort3();
							int port4 = currentrow.getPort4();
							int port5 = currentrow.getPort5();

							// create date from separate values
							dateString = "";
							if(month < 10){
								dateString = dateString + "0" + Integer.toString(month) + "-";
							}else
							{
								dateString = dateString + Integer.toString(month) + "-";
							}

							if(day < 10){
								dateString = dateString + "0" + Integer.toString(day) + " ";
							}else
							{
								dateString = dateString + Integer.toString(day) + " ";
							}

							if(hour < 10){
								dateString = dateString + "0" + Integer.toString(hour) + ":";
							}else
							{
								dateString = dateString + Integer.toString(hour) + ":";
							}

							if(minute < 10){
								dateString = dateString + "0" + Integer.toString(minute) + ":";
							}else
							{
								dateString = dateString + Integer.toString(minute) + ":";
							}

							if(second < 10){
								dateString = dateString + "0" + Integer.toString(second) ;

							}else
							{
								dateString = dateString + Integer.toString(second) ;
							}




							// set the previous date before the current on the first loop iteration
							if (this.RowCtr == 1) {
								//logger.info("** first iteration");
								previousDateString = Integer.toString((year-1)) + "-" + dateString;
							}

							dateString = Integer.toString(year) + "-" + dateString;
							switchdate = sdf.parse(dateString);
							//previousswitchdate = sdf.parse(previousDateString);
							//logger.debug("date: " + dateString + " prevdate: " + previousDateString);



							// check if the current event is in the same second
							if (dateString.equals(previousDateString)) {
								SameSecond =true;
								//logger.info("** Set SameSecond = true");
							}else {
								SameSecond = false;
							//	logger.info("** Set SameSecond = false");

								for (BinaryChannelData event : this.AllPreviousSwitchEvents) {
									if(event !=null) {
										this.SwitchEvents[QueryCounter] = event;
									//	logger.info("*** Querycounter = " + Integer.toString(QueryCounter));
									//	String debugstring = "***event fields: "
									//		+ ", " + event.getTimestamp()
									//		+ ", " + Integer.toString(event.getChannelid())
									//		+ ", " + Integer.toString(event.getSwitchstate());
									//	logger.info("*** adding switchEvent [" + Integer.toString(this.ImportCtr) + " "+ debugstring + " to batchlist");

										//logger.info("*** adding event "+ Integer.toString(QueryCounter) + " to batchlist");
										QueryCounter++;
									}

								}
								Arrays.fill(this.AllPreviousSwitchEvents, null);

							}


							// convert the ports

							convertPort(port0,rowid,pcid,unitid,0, SameSecond, switchdate );
							convertPort(port1,rowid,pcid,unitid,1, SameSecond, switchdate );
							convertPort(port2,rowid,pcid,unitid,2, SameSecond, switchdate );
							convertPort(port3,rowid,pcid,unitid,3, SameSecond, switchdate );
							convertPort(port4,rowid,pcid,unitid,4, SameSecond, switchdate );
							convertPort(port5,rowid,pcid,unitid,5, SameSecond, switchdate );
							
							
						}catch(Exception e){
							logger.debug("oops " + e);
						}

						//add the processed row to the row update list 
						currentrow.setConversiontype(1);
						this.mazeDataRowUpdates[this.RowCtr] = currentrow;
						

						previousDateString = dateString;
						//logger.debug("* prev ds = ds" + previousDateString);



					}//end if(mditerator.hasnext())

				}

			}// end while (rowctr <= nrofrows)


		}else {
			this.statusmessage = " there is no data in the list";
			logger.debug("***** No data in list");

		}// endif emptylist check			
	}
}

