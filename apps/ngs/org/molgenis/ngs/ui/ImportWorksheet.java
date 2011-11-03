package org.molgenis.ngs.ui;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.molgenis.auth.Person;
import org.molgenis.compute.commandline.ComputeBundle;
import org.molgenis.compute.commandline.ComputeBundleFromDirectory;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.ngs.Flowcell;
import org.molgenis.ngs.LibraryBarcode;
import org.molgenis.ngs.LibraryCapturing;
import org.molgenis.ngs.LibraryLane;
import org.molgenis.ngs.Machine;
import org.molgenis.ngs.NgsSample;
import org.molgenis.ngs.Trio;
import org.molgenis.ngs.Worksheet;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.WorkflowElementParameter;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

/**
 * ImportWorksheetController takes care of all user requests and application
 * logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>ImportWorksheetModel holds application state and business logic on
 * top of domain model. Get it via this.getModel()/setModel(..) <li>
 * ImportWorksheetView holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class ImportWorksheet extends EasyPluginController<ImportWorksheetModel>
{
	public ImportWorksheet(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new ImportWorksheetModel(this)); // the default model
		this.setView(new FreemarkerView("ImportWorksheetView.ftl", getModel())); // <plugin
																					// flavor="freemarker"
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception
	{

		// reload the worksheet table after import...

		// empty worksheet table
		List<Worksheet> wsl = db.query(Worksheet.class).find();
		for (Worksheet ws : wsl)
		{
			db.remove(ws);
		}

		// empty worksheet 'lane level' too
		// List<WorksheetLaneLevel> wslll =
		// db.query(WorksheetLaneLevel.class).find();
		// for (WorksheetLaneLevel wsll : wslll) {
		// db.remove(wsll);
		// }

		// fill worksheet table
		List<LibraryLane> libraryLaneList = db.query(LibraryLane.class).find();
		for (LibraryLane libraryList : libraryLaneList)
		{
			print("LibraryLane: " + libraryList);
			// get data
			// db.f
			Flowcell flowcell = db.findById(Flowcell.class, libraryList.getFlowcell_Id());
			NgsSample sample = db.findById(NgsSample.class, libraryList.getSample());
			LibraryCapturing lc = db.findById(LibraryCapturing.class, libraryList.getCapturing());
			LibraryBarcode lb = db.findById(LibraryBarcode.class, libraryList.getBarcode());
			Investigation inv = db.findById(Investigation.class, sample.getInvestigation());
			Person i = null;
			if (inv.getContacts_Id().size() > 0) i = db.findById(Person.class, inv.getContacts_Id().get(0));
			Machine m = db.findById(Machine.class, flowcell.getMachine());

			// create sheet
			Worksheet ws = new Worksheet();
			ws.setExternalSampleID(sample.getName());
			ws.setBarcode(lb.getBarcode());
			ws.setProject(inv.getName());
			if (inv.getContacts_Id().size() > 0) ws.setContact(i.getLastName());
			ws.setLane(libraryList.getLane());
			ws.setSequencingStartDate(flowcell.getRunDate());
			ws.setSequencer(m.getMachine());
			ws.setRun(flowcell.getRun());
			ws.setFlowcell(flowcell.getName());
			if (lc == null)
			{
				ws.setCapturingKit("NA");
			}
			else
			{
				ws.setCapturingKit(lc.getCapturing());
			}
			
			// now set trio; i.e. iterate through trios until find a trio pointing to this sample
			List<Trio> tl = db.find(Trio.class);
			Trio trio = new Trio();
			trio.setName("");
			for (Trio t : tl) if (t.getSamples().contains(sample.getId())) trio = t; // assuming only one trio refering to this sample
			ws.setTrio(trio.getName());
			
			print("LibraryList.getname(): " + libraryList.getName());
			ws.setLibrary(libraryList.getName());
			ws.setComments(libraryList.getDescription());
			// add sheet
			db.add(ws);

		}
		/*
		// put data in lane level worksheet, too:

		// 1. collect all library lanes
		List<LibraryLane> ll_list_all = db.query(LibraryLane.class).find();
		// print(" 111 " + ll_list_all);

		// 2. create {string flowcell, map {string lane, string barcodes}}
		HashMap<Integer, HashMap> flowlane = new HashMap<Integer, HashMap>();
		for (LibraryLane ll : ll_list_all)
		{
			int flowcellID = ll.getFlowcell_Id();
			String bc = ll.getBarcode_Barcode().split(" ")[2];

			// add project before barcode:
			// Flowcell fc = db.findById(Flowcell.class, flowcellID);
			NgsSample sample = db.findById(NgsSample.class, ll.getSample());
			Investigation inv = db.findById(Investigation.class, sample.getInvestigation());
			bc = inv.getName() + "," + bc;

			HashMap lanebarcodes;
			if (flowlane.containsKey(flowcellID))
			{ // flowcell was already found
				lanebarcodes = flowlane.get(flowcellID); // get map{lane,
															// barcodes}

				if (lanebarcodes.containsKey(ll.getLane()))
				{
					// lane was already found
					lanebarcodes.put(ll.getLane(), lanebarcodes.get(ll.getLane()) + ";" + bc); // with
																							// comma
				}
				else
				{
					// we found a new lane
					lanebarcodes.put(ll.getLane(), bc);
				}

			}
			else
			{ // we found a new flowcell
				print("First time we see lane nr " + ll.getLane());
				lanebarcodes = new HashMap<String, String>();
				lanebarcodes.put(ll.getLane(), bc); // first without comma
			}

			// print(" 222 " + flowcellname + lanebarcodes);
			flowlane.put(flowcellID, lanebarcodes);
		}

		for (Integer flowcellID : flowlane.keySet())
		{
			print("Different lanes and barcodes on flowcell (" + flowcellID + "): " + flowlane.get(flowcellID));
		}
		*/
		/*
		 * // now fill WorksheetLaneLevel for every flowcell for (Integer
		 * flowcellID : flowlane.keySet()) { Flowcell flowcell =
		 * db.findById(Flowcell.class, flowcellID); Machine m =
		 * db.findById(Machine.class, flowcell.getMachine());
		 * 
		 * // for every lane: HashMap<String, String> lanebarcodes =
		 * flowlane.get(flowcellID); for (String lane : lanebarcodes.keySet()) {
		 * WorksheetLaneLevel wsll = new WorksheetLaneLevel();
		 * wsll.setSequencingStartDate(flowcell.getRunDate());
		 * wsll.setSequencer(m.getMachine()); wsll.setRun(flowcell.getRun());
		 * wsll.setFlowcell(flowcell.getName()); wsll.setLane(lane);
		 * wsll.setBarcodes(lanebarcodes.get(lane));
		 * 
		 * // add element db.add(wsll); } }
		 */

		// create file for download
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File f = new File(tmpDir + File.separator + "worksheet.csv");
		// getModel().worksheetpath = tmpDir + File.separator + "worksheet.csv";

		print("tmpdir = " + f.toString());

		CsvFileWriter writer = new CsvFileWriter(f);
		writer.setSeparator(",");
		Boolean first = true;
		for (Worksheet ws : db.query(Worksheet.class).find())
		{
			if (first)
			{
				writer.setHeaders(ws.getFields());
				writer.writeHeader();
				print("Header: " + ws.getFields().toString());
				first = false;
			}
			print(ws.toString());
			writer.writeRow(ws);
		}
		writer.close();

	}

	public void uploadaction(final Database db, Tuple request) throws Exception
	{
		String duplicateaction = request.getString("duplicates"); // update,ignore,error

		 File file = request.getFile("upload");
		String workflowdir = "/Users/mdijkstra/Dropbox/NGS/compute/New_Molgenis_Compute_for_GoNL/Example_01";
//		File file = new File("/Users/mdijkstra/Dropbox/NGS/compute/GAF.csv");
//		File file = new File(workflowdir + "/SampleList_A102.csv");
		
		if (file == null)
		{
			throw new Exception("No file selected.");
		}
		else if (!file.getName().endsWith(".csv"))
		{
			throw new Exception("File does not end with '.csv', other formats are not supported.");
		}

		System.out.println(">> Start reading csv " + file.toString());

		ComputeBundle cb = new ComputeBundleFromDirectory(new File(workflowdir), file);
		List<Tuple> lt = cb.getUserParameters();


		for (Tuple tuple : lt)
		{
			// in-house: only import this line if GCC_Analysis is set to "Yes"
			// GoNL: if we have trio's then we should deal with GoNL data and set importthis = true;
			Boolean importthis = tuple.getString("trio") != null;
			if (!importthis) {
				// no GoNL, so this should be in-house data
				importthis = tuple.getString("GCC_Analysis") != null;
				importthis = tuple.getString("GCC_Analysis").equals("Yes");
			}

			if (!tuple.isNull(Worksheet.FLOWCELL) && !tuple.isNull(Worksheet.PROJECT) && importthis)
			{
				// _investigator_
				String investigatorname = tuple.getString(Worksheet.CONTACT);
				Person inv = null;
				if (investigatorname != null)
				{
					inv = (Person) getObject(db, Person.class, Person.NAME, investigatorname);
					if (inv == null)
					{
						inv = new Person();
						inv.setName(investigatorname);
						// inv.setLastName(investigatorname);
						db.add(inv);
					}
				}

				// _project_
				String projectname = tuple.getString(Worksheet.PROJECT);
				Investigation investigation = (Investigation) getObject(db, Investigation.class, Investigation.NAME,
						projectname);
				if (investigation == null)
				{
					investigation = new Investigation();
					investigation.setName(projectname);
					if (inv != null) investigation.getContacts_Id().add(inv.getId());
					db.add(investigation);
				}

				// _library_ Capturing
				LibraryCapturing libcap = null;
				String capturing = tuple.getString(Worksheet.CAPTURINGKIT);
				if (capturing == null)
				{
					capturing = "NA";
				}
				else
				{
					libcap = (LibraryCapturing) getObject(db, LibraryCapturing.class, LibraryCapturing.CAPTURING,
							capturing);
					if (libcap == null)
					{
						libcap = new LibraryCapturing();
						libcap.setCapturing(capturing);
						db.add(libcap);
					}
				}

				// _libary_ Barcode
				String barcode = tuple.getString(Worksheet.BARCODE);
				if (barcode == null) barcode = "NA";
				LibraryBarcode libbar = (LibraryBarcode) getObject(db, LibraryBarcode.class, LibraryBarcode.BARCODE,
						barcode);

				if (libbar == null)
				{
					libbar = new LibraryBarcode();
					libbar.setBarcode(barcode);
					db.add(libbar);
				}

				// _sample_
				// assume that a sample names occurs in only one line
				String samplename = tuple.getString(Worksheet.EXTERNALSAMPLEID);
				NgsSample sample = (NgsSample) getObject(db, NgsSample.class, NgsSample.NAME, samplename);
				if (sample == null)
				{
					sample = new NgsSample();
					sample.setName(samplename);
					sample.setInvestigation(investigation);
					db.add(sample);
				}
				
				// _trio_
				String thistrio = tuple.getString("trio");
				if (thistrio != null) {
					
					// GoNL data with trio's
					
					Trio trio = (Trio) getObject(db, Trio.class, "name", thistrio); // FIXME: replace "name" by some 'constant'
										
					if (trio == null) {
						// trio did not exist yet, so create
						trio = new Trio();
						trio.setName(thistrio);
						db.add(trio);
					}
					
					// get samples that already belong to this trio
					List<Integer> trioSamples = trio.getSamples();
					
					// add sample to this trio
					trioSamples.add(sample.getId());
					trio.setSamples(trioSamples);
					
					// update database
					db.update(trio);
					
				}

				// _machine-
				String machinename = tuple.getString(Worksheet.SEQUENCER);
				Machine machine = (Machine) getObject(db, Machine.class, Machine.MACHINE, machinename);
				if (machine == null)
				{
					machine = new Machine();
					machine.setMachine(machinename);
					db.add(machine);
				}

				// _flowcell_
				String flowcellname = tuple.getString(Worksheet.FLOWCELL);
				if (flowcellname == null) flowcellname = "FLOWCELLNAME";
				Flowcell flowcell = (Flowcell) getObject(db, Flowcell.class, Flowcell.NAME, flowcellname);
				if (flowcell == null)
				{
					print(">> We create a new flowcell");
					flowcell = new Flowcell();
					flowcell.setName(flowcellname);
					String flowcellrun = tuple.getString(Worksheet.RUN);
					if (flowcellrun == null) flowcellrun = "NA";

					// prefix leading 0's so that run length is always 4
					for (int i = flowcellrun.length(); i < 4; i++)
					{
						flowcellrun = "0" + flowcellrun;
					}

					flowcell.setRun(flowcellrun);
					String date = tuple.getString(Worksheet.SEQUENCINGSTARTDATE);
					// Calendar cal = Calendar.getInstance();
					// cal.set(2000 + Integer.parseInt(date.substring(0,
					// 2)),
					// Integer.parseInt(date.substring(2, 4)),
					// Integer.parseInt(date.substring(4, 6)));
					// print(cal.getTime().toString());
					if (date != null && !"".equals(date))
					{
						java.util.Date date_tmp = new java.util.Date(2000 - 1900 + Integer.parseInt(date
								.substring(0, 2)), Integer.parseInt(date.substring(2, 4)) - 1, Integer.parseInt(date
								.substring(4, 6)));
						flowcell.setRunDate(date_tmp);
					}
					flowcell.setMachine(machine);
					db.add(flowcell);
				}

				// _flowcell lane library_
				Query<LibraryLane> q = db.query(LibraryLane.class);
				List<LibraryLane> flslst = q.find();
				LibraryLane fls = null;
				for (LibraryLane thisfls : flslst)
				{
					Boolean equal = (thisfls.getFlowcell_Id().equals(flowcell.getId()))
							&& (thisfls.getLane().substring(1).equals(tuple.getString("lane")))
							&& (thisfls.getSample_Id().equals(sample.getId()));
					if (equal)
					{
						fls = thisfls;
						print(" ALERT !!! >> Flowcell-Lane-s object was already found: " + fls.toString());
						break;
					}
				}

				if (fls == null)
				{
					print("The library is: " + tuple.getString(Worksheet.LIBRARY));
					// if fll is still null, then it doesn't exist in the DB
					// yet, so we will add it
					fls = new LibraryLane();
					// if ("g".equalsIgnoreCase(status))
					// status = "to do";
					// else
					// status = "done";
					fls.setFlowcell(flowcell);
					fls.setLane(tuple.getString(Worksheet.LANE));
					fls.setSample(sample);

					String library = tuple.getString(Worksheet.LIBRARY);
					if (library != null && !library.equals(""))
					{
						fls.setName(library);
					}
					else
					{
						fls.setName("NA");
					}

					fls.setDescription(tuple.getString(Worksheet.COMMENTS));
					fls.setBarcode(libbar);
					if (libcap != null) fls.setCapturing(libcap);

					print("Before adding flowcell lane lib");
					print("fls: " + fls);
					db.add(fls);
					print("Flowcell-Lane-Library object added");
				}

				// getModel().investigations = q.find();

			}
		}

		getModel().setSuccess("UPLOAD " + request.getString("uploadOriginalFileName"));
	}

	private void print(String str)
	{
		System.out.println(">> " + str);
	}

	public Object getObject(Database db, Class c, String uniquefield, String fieldvalue)
	{
		Query q = db.query(c);
		q.equals(uniquefield, fieldvalue);
		List<Object> objlist;

		try
		{
			objlist = q.find();
			if (objlist.size() == 1)
			{
				return objlist.get(0);
			}
		}
		catch (Exception e)
		{
			System.out.println(">> Exception <<");
			e.printStackTrace();
		}

		// if the number of objects in the db != 1, then it should equal 0, and
		// thus it doesn't exist and we return nulll
		return null;
	}

}