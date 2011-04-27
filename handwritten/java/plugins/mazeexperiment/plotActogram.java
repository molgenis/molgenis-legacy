/* Date:        October 17, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.mazeexperiment;



import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.maze.BinaryChannelData;
import org.molgenis.maze.ChannelMapping;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class plotActogram extends PluginModel<Entity>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1983706633143335769L;
	private List<ChannelMapping> channelList;
	private int currChlListIdx = 0;
	private String plot;
	private int chlListSize;
	private int currChlNr;
	private int currChlId;
	private String startDate = "first";
	private String endDate = "first";
	//private Date currDate;
	
	//private List<Mazedata> ChannelMazedata;
	
	
	/*
	 * some getters and setters:
	 */
	
	
	public int getCurrChlId() {
		return currChlId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setCurrChlId(int currChlId) {
		this.currChlId = currChlId;
	}

	public int getCurrChlListIdx() {
		return currChlListIdx;
	}

	public void setCurrChlListIdx(int currChlListIdx) {
		this.currChlListIdx = currChlListIdx;
	}

	public int getChlListSize() {
		return chlListSize;
	}

	public void setChlListSize(int chlListSize) {
		this.chlListSize = chlListSize;
	}
	
	public int getCurrChlNr() {
		return currChlNr;
	}

	public void setCurrChlNr(int currChlNr) {
		this.currChlNr = currChlNr;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public List<ChannelMapping> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<ChannelMapping> channelList) {
		this.channelList = channelList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public plotActogram(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	public String getCustomHtmlHeaders() {
		return 	"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">" +
				"<script src=\"generated-res/scripts/datetimeinput.js\" type=\"text/javascript\" language=\"javascript\"></script>";

	}

	@Override
	public String getViewName()
	{
		return "plugin_animaldb_mazeexperiment_plotActogram";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/animaldb/mazeexperiment/plotActogram.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		SimpleDateFormat sdfMysqlStamp = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale.US);
		
		String action = request.getString("__action");
		
		// set the dates
		this.startDate = request.getString("startdate");
		this.endDate = request.getString("enddate");
		
		logger.debug("sd " + startDate + "   ed " + endDate );
		
		String tmpString = request.getString("channel").replace(".", "");
		tmpString =  tmpString.replace(",", "");
		
		this.currChlListIdx = Integer.parseInt(tmpString); //get the index of the channel object in the channellist (not! the channel nr.) 
		// set the current channel id.
			
		
		// handle the plot previous buton
		if( action.equals("doPlotPrev") )
		{
			//check for if offset is at min value 1
			if (this.currChlListIdx > 0 ) {
				this.currChlListIdx--;
			}else {
				//msg = new ScreenMessage("You have reached the first channel...",null,false);		//TODO fix freemarker error
				
			}
			action = "doPlot";	// fowared the corrected request to the plot routine
		}
		
		// handle the plot next button
		if( action.equals("doPlotNext") )
		{
			//check for if offset is at min value 1
			if (this.currChlListIdx < chlListSize-1) {
				this.currChlListIdx++;
			}else {
				//msg = new ScreenMessage("You have reached the last channel...",null,false);			//TODO fix freemarker error
				
			}
			action = "doPlot"; // fowared the corrected request to the plot routine
		}
		
		// get the channel id
		this.currChlId = this.channelList.get(this.currChlListIdx).getId();
		
		// handle the plot button and the forward requests from plotnext and plotprevious
		if( action.equals("doPlot") )
		{
			try {
				// set some variables
				Date minDate;
				Date maxDate;

				Calendar c = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				Calendar c3 = Calendar.getInstance();
				//TODO: Danny: Use or Loose
				/*Calendar plotYaxisDate = */Calendar.getInstance();
				//Calendar c4 = Calendar.getInstance();

				int startyear = 0;
				int startmonth = 0;
				int startday = 0;
				int rowCtr = 0;
				int dataListSize;
				//int[] currDayData = new int[720];
				//int currBin = 0;
				int currDayBin = 0;
				int currDay = 0;
				
				double[][] plotData;
				double qualBarHeight = 75;
				
				long binSize = 1000*60*2;	//set the binsize for the plot (in milliseconds)
				long currDpMs = 0;
				long currDayOffsetMs =0;
				long dpBin = 0;
				long nrNewDays = 0;
				long dayMs = 24*60*60*1000;			// a long containing the amount of milliseconds in a day
				long offsetMS = 0;
				
				boolean firstIteration = true;
				boolean startNewDay = false;

				//List<int[]> plotData = Collections.emptyList();
				


				// get all the data for the requested channel from the binaryconverted table
				db.beginTx();
				Query<BinaryChannelData> bcdq;
				bcdq = db.query(BinaryChannelData.class);
				bcdq.addRules(new QueryRule("channelid", Operator.EQUALS, this.currChlId));
				bcdq.addRules(new QueryRule("timestamp", Operator.GREATER, sdfMysqlStamp.format(sdf.parse(this.startDate))));
				bcdq.addRules(new QueryRule("timestamp", Operator.LESS, sdfMysqlStamp.format(sdf.parse(this.endDate))));
				bcdq.addRules(new QueryRule(Operator.SORTASC,"timestamp"));
				List<BinaryChannelData> channelSwitchData = bcdq.find();
				db.commitTx();

				dataListSize = channelSwitchData.size();
				if (dataListSize > 0){
				
					// get the date range for the channel:
					//minDate = channelSwitchData.get(0).getTimestamp();
					//maxDate = channelSwitchData.get(dataListSize-1).getTimestamp();
					minDate = sdf.parse(this.startDate);
					maxDate = sdf.parse(this.endDate);
					
					logger.info("dataListSize is: "+ dataListSize);
					
					long nrOfDays = (maxDate.getTime()-minDate.getTime()+ (dayMs-1))/(dayMs);
					logger.info("nr of days to plot: "+ nrOfDays);
	
					plotData = new double[(int)nrOfDays][720];
					
					//String blaat ="";
	
					//Create the data arrays per day
				

					try{

						Iterator<BinaryChannelData> BcdIterator = channelSwitchData.iterator();
						while (BcdIterator.hasNext()) {

							//accumulate the data per day in an array

							rowCtr++;

							// Do things specific for the first iteration								
							if(firstIteration){
								//logger.info("FIRST iteration");
								// fill the currentdata array with 0
								Arrays.fill(plotData[currDay],0);

								// set the offsets for the dataset
								c.setTime(minDate);
								//detect year-month-day
								startyear = c.get(Calendar.YEAR);
								startmonth = c.get(Calendar.MONTH);
								startday = c.get(Calendar.DAY_OF_MONTH);	

								// and set the offset
								c2.set(startyear,startmonth,startday,0,0,0);
								currDayOffsetMs = c2.getTimeInMillis();
								offsetMS = currDayOffsetMs;
								
								// set the initial bin
								currDpMs = c.getTimeInMillis();
								//logger.debug("currDpMs = : "+ currDpMs);
								//logger.debug("curr dpbin = : "+ dpBin);
								//logger.debug("Curr day offset ms: "+ currDayOffsetMs);
								dpBin = (currDpMs - currDayOffsetMs) / binSize;
								//logger.debug("initial bin = : "+ dpBin); 
								currDayBin = (int)dpBin;
								//logger.debug("initial currDaybin" + currDayBin);
								//currBin = currDayBin;
								
								//currBin = (int)(currDayOffsetMs / binSize);
								//currDayBin = currBin;
								//logger.info(currDayBin);
								// end of the firstIteration 

								
								firstIteration = false;
							}//endif firstIteration

							// accumulate the (qualitative) plotdata in binsize(for now 2 min) bins

							BinaryChannelData cr = BcdIterator.next();	//get the next datapoint
							//logger.info("switch event["+ Integer.toString(rowCtr) +"]: " + cr.getTimestamp());

							
							//check in which bin the point should go
							c3.setTime(cr.getTimestamp());
							currDpMs = c3.getTimeInMillis();
							//logger.debug("next currDpMs: " + currDpMs );
							dpBin = (currDpMs - currDayOffsetMs) / binSize;
							//logger.info("dpBin = " + dpBin);
							//logger.debug("next dpbin: " + dpBin );
							
							//Check if a new day should be started:
							if ((int)dpBin >= 720) {
								//logger.info("setting startnewday to true");
								startNewDay = true;	
								// check the amount of new days: (in case of no data on a day the dayNr should be increased correctly accordingly)
								nrNewDays = dpBin / 720;
								logger.debug("NR of new days : " + nrNewDays);
								
							}

							if (startNewDay) {
								
												
								//plotData[currDay] = currDayData;
								// add the correct amount of days to the plot
								for (long d = 0; d < nrNewDays ; d++) {
									// increase the daycounter
									
									
									currDay++;
									logger.debug("Current day:" + currDay);
									
									// prefill all the bins of the new day wit 0 
									Arrays.fill(plotData[currDay],0);
									
									// correct the day offset with the amount of ms in a day
									currDayOffsetMs = currDayOffsetMs + (dayMs);
									

									
								}
																								
								// correct the active bin for the new offset on the active day
								dpBin = (currDpMs - currDayOffsetMs) / binSize;
								
								currDayBin =(int)dpBin; //increase the daybincounter
								
								// for qualitative plot : fill the bin with a standard bar height value
								plotData[currDay][currDayBin] = qualBarHeight;
								
								// set start new day fals to allow for adding new databins								
								startNewDay = false;

							}

							if ((int)dpBin == currDayBin) {
								// do nothing for qualitative plot.
								
								//optionally add accumulation code for quantitative plot
								
							}else {


								currDayBin =(int)dpBin; //increase the daybincounter
								//logger.debug("cd: "+ currDay + " , cdbin: " + currDayBin );
								plotData[currDay][currDayBin-1] = qualBarHeight;

								// optionally add accumulation code for quantitative plot
								
							}						
						}//end while

						String data = "<table>";
						GoogleBarChart chart = new GoogleBarChart();

						int dc = 0;
						for ( double[] day : plotData){  
							
							chart.createBarChart(day);
							String chartString = "<img src=\"" + chart.getUrl() + "\" >";
							String date = "";
							try{
								//date = sdf2.format(plotDates.get(dc));
								c.setTimeInMillis(offsetMS + (dc * dayMs));
								date = sdf2.format(c.getTime());
								
								
							}catch(Exception e){
								date = "-";
								e.printStackTrace();
							}
							
							data = data + "<tr><td>" + date + "</td><td>" + chartString + "</td></tr>";
							dc++;
						}
						data = data + "</table>";
						this.plot = data;
					}
					catch (Exception e) {
						e.printStackTrace();
						this.plot = "Something went wrong... <br><hr> " ;
						
					}

				}else{
					// the dataset is empty
					logger.info("Dataset is empty");
					this.plot = "The plot cannot be generated, because there is no data for this channel";
				}//end if else
			}//end try
			catch (Exception e) {
				this.plot = "something went horribly wrong";
				e.printStackTrace();
			}
			// add status messages to the screen
			//this.getMessages().add(msg);			//TODO fix freemarker error
		}
	}

	@Override
	public void reload(Database db)
	{

			
		// Populate channel list
		try {
			// query the channelmapping table and harvest the available channels
			Query<ChannelMapping> q;
			q = db.query(ChannelMapping.class);
			q.addRules(new QueryRule(Operator.SORTASC,"channelnumber"));
			List<ChannelMapping> ChmpList = q.find();
			
			// put the available channel objects in a list
			this.setChannelList(ChmpList);
			this.chlListSize = this.channelList.size();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// set the current channel id
		try{
			this.currChlId = this.channelList.get(this.currChlListIdx).getId();
			this.currChlNr = this.channelList.get(this.currChlListIdx).getChannelnumber();
		}catch(Exception e){
			// setting from previous did not work so set defaults
			logger.debug("Setting current channel did not work.");
			e.printStackTrace();
					
		}
			// set the start & enddate
			Calendar c = Calendar.getInstance();	// create a new calendar object
			Date now = c.getTime();					// get the current date/time from it
			
			c.add(Calendar.DAY_OF_MONTH, -30);
			Date start = c.getTime();	
			
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
		try{	
			if (this.startDate.equals("first")){			// check if startdate is set
				// if not set put the default on 30 days before now 
				
				this.setStartDate(sdf.format(start));
				
			}else {
				// get the value from the input
				this.setStartDate(this.startDate);
				// TODO add code to recalculate the plot offsets because changing the date offsets invalidates array sizes resulting in a crash :(
			}
			if (this.endDate.equals("first")){			// check if startdate is set
				// if not set put the default on now
				this.setEndDate(sdf.format(now));
			}else {
				// get the value from the input
				this.setEndDate(this.endDate);
				
			}
		}catch(Exception e){
			// setting from previous did not work so set defaults
			logger.debug("Setting start and enddate did not work.");
			e.printStackTrace();
					
		}	
		
		
	}

}
