package org.molgenis.ngs.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.generators.csv.CsvExportGen;
import org.molgenis.ngs.Flowcell;
import org.molgenis.ngs.FlowcellLaneLibrary;
import org.molgenis.ngs.FlowcellLaneSample;
import org.molgenis.ngs.Investigator;
import org.molgenis.ngs.Lane;
import org.molgenis.ngs.Library;
import org.molgenis.ngs.LibraryBarcode;
import org.molgenis.ngs.LibraryCapturing;
import org.molgenis.ngs.NgsSample;
import org.molgenis.ngs.Project;
import org.molgenis.ngs.Worksheet;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Sample;

import app.JDBCDatabase;

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
public class ImportWorksheet extends EasyPluginController<ImportWorksheetModel> {
	public ImportWorksheet(String name, ScreenController<?> parent) {
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
	public void reload(Database db) throws Exception {
		// //example: update model with data from the database
		// Query q = db.query(Investigation.class);
		// q.like("name", "molgenis");
		// getModel().investigations = q.find();

//		String sqlalldata = "SELECT fls.status, s.name, lb.barcode, p.name, fls.lanenumber, f.daterun, f.machine, ot.name AS otname FROM FlowcellLaneSample fls" +
//				" JOIN Flowcell f ON (fls.flowcell = f.id)" +
//				" JOIN ObservationTarget ot ON (ot.id = f.id)" +
//				" JOIN NgsSample s ON (fls.sample = s.id)" +
//				" JOIN Library l ON (s.library = l.id)" +
//				" JOIN LibraryBarcode lb ON (l.barcode = lb.id) " +
//				" JOIN LibraryCapturing lc ON (l.capturing = lc.id)" +
//				" JOIN Project p ON (s.project = p.id);";
//		getModel().worksheetsql = ((JDBCDatabase)db).sql(sqlalldata);

		// empty worksheet table
		List<Worksheet> wsl = db.query(Worksheet.class).find();
		for (Worksheet ws : wsl) {
			db.remove(ws);
		}
		
		// fill worksheet table		
		List<FlowcellLaneSample> flslst = db.query(FlowcellLaneSample.class).find();
		for (FlowcellLaneSample fls : flslst) {
			// get data
			Flowcell f = db.findById(Flowcell.class, fls.getFlowcell_Id());
			NgsSample s = db.findById(NgsSample.class, fls.getSample());
			Library l = db.findById(Library.class, s.getLibrary());
			LibraryCapturing lc = db.findById(LibraryCapturing.class, l.getCapturing());
			LibraryBarcode lb = db.findById(LibraryBarcode.class, l.getBarcode());
			Project p = db.findById(Project.class, s.getProject());
			Investigator i = db.findById(Investigator.class, p.getInvestigator_Id());
			
			// create sheet
			Worksheet ws = new Worksheet();
			ws.setStatus(fls.getStatus());
			ws.setSample(s.getName());
			ws.setBarcode(lb.getBarcode());
			ws.setProject(p.getName());
			ws.setInvestigator(i.getLastName());
			ws.setLane(fls.getLanenumber());
			ws.setDate(f.getDaterun());
			ws.setMachine(f.getMachine());
			ws.setFlowcell(f.getName());
			ws.setCapturing(lc.getCapturing());
			ws.setRemark(fls.getRemark());
			// add sheet
			db.add(ws);
		}
		
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File f = new File(tmpDir + File.separator + "worksheet.csv");
		getModel().worksheetpath = tmpDir + File.separator + "worksheet.csv";
		CsvWriter writer = new CsvFileWriter(f);
		writer.setSeparator(",");
		Boolean first = true;
		for (Worksheet ws : db.query(Worksheet.class).find()) {
			if (first) {
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

	public void uploadaction(final Database db, Tuple request) throws Exception {
		File file = request.getFile("upload");
		// File file = new File(request.getString("upload"));
		/*
		 * if (file == null) { throw new Exception("No file selected."); } else
		 * if (!file.getName().endsWith(".csv")) { throw new Exception(
		 * "File does not end with '.csv', other formats are not supported."); }
		 */

		System.out.println(">> Start reading csv");

		CsvReader reader = new CsvFileReader(new File("/Users/mdijkstra/Desktop/lane-barcodes.csv"));
		// System.out.println(">>" + reader.)
		reader.setSeparator(',');
		System.out.println(">> " + reader.colnames());

		reader.parse(new CsvReaderListener() {
			@Override
			public void handleLine(int line_number, Tuple tuple) throws Exception {
				System.out.println(">> Parsing line " + line_number);

				// _investigator_
				String investigatorname = tuple.getString("investigator");
				Investigator inv = (Investigator) getObject(db, Investigator.class, "LastName", investigatorname);
				if (inv == null) {
					inv = new Investigator();
					inv.setLastName(investigatorname);
					db.add(inv);
				}

				// _project_
				String projectname = tuple.getString("project");
				Project project = (Project) getObject(db, Project.class, "name", projectname);
				if (project == null) {
					project = new Project();
					project.setName(projectname);
					project.setInvestigator_Id(inv.getId());
					db.add(project);
				}

				// _library_
				// _library_ (1a) Capturing
				String capturing = tuple.getString("capturing");
				LibraryCapturing libcap = (LibraryCapturing) getObject(db, LibraryCapturing.class, "capturing", capturing);
				if (libcap == null) {
					libcap = new LibraryCapturing();
					libcap.setCapturing(capturing);
					db.add(libcap);
				}

				// _libary_ (1b) Barcode
				String barcode = tuple.getString("barcode");
				if (barcode == null)
					barcode = "NA";
				LibraryBarcode libbar = (LibraryBarcode) getObject(db, LibraryBarcode.class, "barcode", barcode);

				if (libbar == null) {
					libbar = new LibraryBarcode();
					libbar.setBarcode(barcode);
					db.add(libbar);
				}

				// _library_ (1)
				Query q = db.query(Library.class);
				// q.addRules(new QueryRule("capturing", Operator.EQUALS,
				// capturing));
				// q.addRules(new QueryRule("barcode", Operator.EQUALS,
				// barcode));
				List<Library> liblist = q.find();

				// first select the target library, if available (... the
				// q.addRules statements don't work...)
				Library lib = null;
				for (Library thislib : liblist) {
					if (thislib.getCapturing_Id() == libcap.getId() && thislib.getBarcode_Id() == libbar.getId()) {
						lib = thislib;
					}
				}

				if (lib == null) {
					lib = new Library();

					lib.setCapturing(libcap);
					lib.setBarcode(libbar);

					// Why is library an ObservatgionTarget?! OT has field name
					// which lib shouldnt have... :-s
					lib.setName("lib_" + capturing + barcode);

					db.add(lib);
				}

//				// add the sample to the library (if not there yet)
//				List<Integer> libsamples = lib.getSamples();
//				if (!libsamples.contains(sample.getId())) {
//					libsamples.add(sample.getId());
//					lib.setSamples(libsamples);
//					db.update(lib);
//				}

				// _sample_
				// assume that a sample names occurs in only one line
				String samplename = tuple.getString("sample");
				NgsSample sample = (NgsSample) getObject(db, NgsSample.class, "name", samplename);
				if (sample == null) {
					sample = new NgsSample();
					sample.setName(samplename);
					sample.setProject(project);
					sample.setLibrary(lib);
					db.add(sample);
				}
				
				// _flowcell_
				String flowcellname = tuple.getString("flowcell");
				Flowcell flowcell = (Flowcell) getObject(db, Flowcell.class, "name", flowcellname);
				if (flowcell == null) {
					print(">> We create a new flowcell");
					flowcell = new Flowcell();
					flowcell.setName(flowcellname);
					String date = tuple.getString("date");					
					//Calendar cal = Calendar.getInstance();
					//cal.set(2000 + Integer.parseInt(date.substring(0, 2)), Integer.parseInt(date.substring(2, 4)), Integer.parseInt(date.substring(4, 6)));
					//print(cal.getTime().toString());
					java.util.Date date_tmp = new java.util.Date(2000 - 1900 + Integer.parseInt(date.substring(0, 2)), Integer.parseInt(date.substring(2, 4)) - 1, Integer.parseInt(date.substring(4, 6)));
					print(date_tmp.toString());
					flowcell.setDaterun(date_tmp);
					flowcell.setMachine(tuple.getString("machine"));
					db.add(flowcell);
				}

				// _flowcell lane library_
				q = db.query(FlowcellLaneSample.class);
				List<FlowcellLaneSample> flslst = q.find();
				FlowcellLaneSample fls = null;
				for (FlowcellLaneSample thisfls : flslst) {
					Boolean equal = (thisfls.getFlowcell_Id().equals(flowcell.getId())) && (thisfls.getLanenumber().substring(1).equals(tuple.getString("lane")))
							&& (thisfls.getSample_Id().equals(sample.getId()));
					if (equal) {
						fls = thisfls;
						print(" ALERT !!! >> Flowcell-Lane-Library object was already found: " + fls.toString());
						break;
					}
				}

				if (fls == null) {
					// if fll is still null, then it doesn't exist in the DB
					// yet, so we will add it
					fls = new FlowcellLaneSample();
					String status = tuple.getString("status");
					if ("g".equalsIgnoreCase(status))
						status = "to do";
					else
						status = "done";
					fls.setStatus(status);
					fls.setFlowcell(flowcell);
					fls.setLanenumber(tuple.getString("lane"));
					fls.setSample(sample);
					fls.setName("flowcell_lane_sample_name_" + samplename);
					fls.setRemark(tuple.getString("remark"));
					
					print("Before adding flowcell lane lib");
					db.add(fls);
					print("Flowcell-Lane-Library object added");
				}

				// getModel().investigations = q.find();

			}
		});

		System.out.println(">> Stop");
		getModel().setSuccess("UPLOAD " + request.getString("uploadOriginalFileName"));
	}

	private void print(String str) {
		System.out.println(">> " + str);
	}

	public Object getObject(Database db, Class c, String uniquefield, String fieldvalue) {
		Query q = db.query(c);
		q.equals(uniquefield, fieldvalue);
		List<Object> objlist;

		try {
			objlist = q.find();
			if (objlist.size() == 1) {
				return objlist.get(0);
			}
		} catch (Exception e) {
			System.out.println(">> Exception <<");
			e.printStackTrace();
		}

		// if the number of objects in the db != 1, then it should equal 0, and
		// thus it doesn't exist and we return nulll
		return null;
	}

	/**
	 * When action="updateDate": update model and/or view accordingly.
	 * 
	 * Exceptions will be logged and shown to the user automatically. All db
	 * actions are within one transaction.
	 */
	public void updateDate(Database db, Tuple request) throws Exception {
		getModel().date = request.getDate("date");

		// //Easily create object from request and add to database
		// Investigation i = new Investigation(request);
		// db.add(i);
		// this.setMessage("Added new investigation");

		getModel().setSuccess("update succesfull");
	}
}