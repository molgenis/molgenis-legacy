package org.molgenis.ngs.ui;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.molgenis.contribution.Person;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.EntityTable;
import org.molgenis.framework.ui.html.FileInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.ngs.Flowcell;
import org.molgenis.ngs.LibraryLane;
import org.molgenis.ngs.Machine;
import org.molgenis.ngs.NgsBarcode;
import org.molgenis.ngs.NgsCapturingKit;
import org.molgenis.ngs.NgsPrepKit;
import org.molgenis.ngs.NgsSample;
import org.molgenis.ngs.NgsStudy;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
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
public class ImportWorksheet extends EasyPluginController<ImportWorksheet>
{
	Map<String, Person> persons = new LinkedHashMap<String, Person>();
	Map<String, NgsStudy> studies = new LinkedHashMap<String, NgsStudy>();
	Map<String, NgsSample> samples = new LinkedHashMap<String, NgsSample>();
	Map<String, Flowcell> flowcells = new LinkedHashMap<String, Flowcell>();
	Map<String, LibraryLane> liblanes = new LinkedHashMap<String, LibraryLane>();
	Map<String, NgsBarcode> barcodes = new LinkedHashMap<String, NgsBarcode>();
	Map<String, NgsCapturingKit> capturingKits = new LinkedHashMap<String, NgsCapturingKit>();
	Map<String, NgsPrepKit> prepKits = new LinkedHashMap<String, NgsPrepKit>();
	Map<String, Machine> machines = new LinkedHashMap<String, Machine>();

	public enum State
	{
		UPLOAD, REVIEW
	};

	private State state = State.UPLOAD;

	private static final long serialVersionUID = 6075389492409205677L;

	public ImportWorksheet(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(this); // the default model
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// nothing todo
	}

	// handle the upload
	public void uploadWorksheet(Database db, Tuple request) throws IOException, DataFormatException, ParseException
	{
		CsvReader reader = null;
		if(!request.isNull("filefor_worksheetFile"))
		{
			reader = new CsvFileReader(new File(request.getString("filefor_worksheetFile")));
		}
		else
		{
			reader = new CsvStringReader(request.getString("worksheetCsv"));
		}
		
		persons = new LinkedHashMap<String, Person>();
		studies = new LinkedHashMap<String, NgsStudy>();
		samples = new LinkedHashMap<String, NgsSample>();
		flowcells = new LinkedHashMap<String, Flowcell>();
		liblanes = new LinkedHashMap<String, LibraryLane>();
		barcodes = new LinkedHashMap<String, NgsBarcode>();
		capturingKits = new LinkedHashMap<String, NgsCapturingKit>();
		prepKits = new LinkedHashMap<String, NgsPrepKit>();
		machines = new LinkedHashMap<String, Machine>();

		// iterate
		for (Tuple row : reader)
		{
			// parsing validation
			String study_identifier = row.getString("project");
			String study_contact = row.getString("contact");
			String seqType = row.getString("seqType");
			String flowcell_identifier = row.getString("flowcell");
			String flowcell_run = row.getString("run");
			String flowcell_machine = row.getString("sequencer");
			String sample_identifier = row.getString("internalSampleId");
			String sample_externalIdentifier = row.getString("externalSampleID");
			String lib_lane = row.getString("lane");
			String lib_barcode = row.getString("barcode");
			String lib_capturingKit = row.getString("capturingKit");
			String lib_prepKit = row.getString("prepKit");
			String lib_identifier = flowcell_identifier + "_L" + lib_lane + "_" + lib_barcode;
			String barcodeType = row.getString("barcodeType");

			// parsing dates sucks
			Date flowcell_startDate = null;
			DateFormat formatter = new SimpleDateFormat("yyMMdd", Locale.US);
			if (!row.isNull("sequencingStartDate")) flowcell_startDate = new java.sql.Date(formatter.parse(
					row.getString("sequencingStartDate")).getTime());

			// validation
			if (study_identifier == null) throw new DataFormatException("project is required for row: " + row);
			if (study_contact == null) throw new DataFormatException("contact is required for row: " + row);
			if (sample_identifier == null) throw new DataFormatException("internalSampleId is required for row: " + row);

			// object creation, based on identifiers

			if (persons.get(study_contact) == null)
			{
				Person person = new Person();
				person.setName(study_contact);

				persons.put(study_contact, person);
			}

			// required: study
			if (studies.get(study_identifier) == null)
			{
				NgsStudy study = new NgsStudy();
				study.setIdentifier(study_identifier);
				study.setSeqType(seqType);
				study.setContact_Name(study_contact);

				studies.put(study_identifier, study);
			}

			// required: sample
			if (studies.get(sample_identifier) == null)
			{
				NgsSample sample = new NgsSample();
				sample.setIdentifier(sample_identifier);
				sample.setExternalIdentifier(sample_externalIdentifier);
				sample.setStudy_Identifier(study_identifier);

				samples.put(sample_identifier, sample);
			}

			// optional: flowcell
			if (flowcell_identifier != null && flowcells.get(flowcell_identifier) == null)
			{
				Flowcell flowcell = new Flowcell();
				flowcell.setIdentifier(flowcell_identifier);
				flowcell.setRun(flowcell_run);
				flowcell.setMachine_Name(flowcell_machine);
				flowcell.setStartDate(flowcell_startDate);

				flowcells.put(flowcell_identifier, flowcell);
			}

			// optional: libraryLane
			if (lib_identifier != null && liblanes.get(lib_identifier) == null)
			{
				LibraryLane lib = new LibraryLane();
				lib.setIdentifier(lib_identifier);
				lib.setFlowcell_Identifier(flowcell_identifier);
				lib.setLane(lib_lane);
				lib.setBarcode_Name(lib_barcode);
				lib.setCapturingKit_Name(lib_capturingKit);
				lib.setPrepKit_Name(lib_prepKit);
				lib.setSample_Identifier(sample_identifier);

				liblanes.put(lib_identifier, lib);
			}
			
			if(lib_barcode != null && barcodes.get(lib_barcode) == null)
			{
				NgsBarcode bc = new NgsBarcode();
				bc.setName(lib_barcode);
				if(barcodeType != null) bc.setBarcodeType(barcodeType);
				
				barcodes.put(lib_barcode, bc);
			}
			
			if(lib_capturingKit != null && capturingKits.get(lib_capturingKit) == null)
			{
				NgsCapturingKit c = new NgsCapturingKit();
				c.setName(lib_capturingKit);
				
				capturingKits.put(lib_capturingKit, c);
			}
			
			if(lib_prepKit != null && prepKits.get(lib_prepKit) == null)
			{
				NgsPrepKit c = new NgsPrepKit();
				c.setName(lib_prepKit);
				
				prepKits.put(lib_prepKit, c);
			}
			
			if(flowcell_machine != null && machines.get(flowcell_machine) == null)
			{
				Machine c = new Machine();
				c.setName(flowcell_machine);
				
				machines.put(flowcell_machine, c);
			}
		}

		state = State.REVIEW;

	}

	public void saveUpload(Database db, Tuple request) throws DatabaseException
	{
		int count = 0;
		count += db.update(new ArrayList<Person>(persons.values()), DatabaseAction.ADD_UPDATE_EXISTING, "name");
		count += db.update(new ArrayList<NgsStudy>(studies.values()), DatabaseAction.ADD_UPDATE_EXISTING, "identifier");
		count += db.update(new ArrayList<NgsBarcode>(barcodes.values()), DatabaseAction.ADD_UPDATE_EXISTING, "name");
		count += db.update(new ArrayList<NgsPrepKit>(prepKits.values()), DatabaseAction.ADD_UPDATE_EXISTING, "name");
		count += db.update(new ArrayList<Machine>(machines.values()), DatabaseAction.ADD_UPDATE_EXISTING, "name");
		count += db.update(new ArrayList<NgsCapturingKit>(capturingKits.values()), DatabaseAction.ADD_UPDATE_EXISTING, "name");
		count += db.update(new ArrayList<NgsSample>(samples.values()), DatabaseAction.ADD_UPDATE_EXISTING, "identifier");
		count += db.update(new ArrayList<Flowcell>(flowcells.values()), DatabaseAction.ADD_UPDATE_EXISTING, "identifier");
		count += db.update(new ArrayList<LibraryLane>(liblanes.values()), DatabaseAction.ADD_UPDATE_EXISTING, "identifier");

		state = State.UPLOAD;
		
		this.setSuccess("Added or updated "+count+" records succesfully");
	}

	public void resetUpload(Database db, Tuple request)
	{
		state = State.UPLOAD;
	}

	@Override
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);

		if (state == State.UPLOAD)
		{
			view.add(new TextInput("worksheetCsv"));
			view.add(new Newline());
			view.add(new FileInput("worksheetFile"));
			view.add(new Newline());
			view.add(new ActionInput("uploadWorksheet"));
		}
		if (state == State.REVIEW)
		{
			view.add(new Paragraph("<h2>Please review uploaded data:</h2>"));
			view.add(new ActionInput("resetUpload", "reset"));
			view.add(new ActionInput("saveUpload", "save"));

			EntityTable s = new EntityTable("studies", studies.values(), false, "identifier", "contact_name");
			s.setLabel("<h3>Studies:</h3>");
			view.add(s);

			EntityTable p = new EntityTable("persons", persons.values(), false, "name");
			p.setLabel("<h3>Persons:</h3>");
			view.add(p);

			EntityTable sa = new EntityTable("samples", samples.values(), false, "study_identifier", "identifier",
					"externalIdentifier", "sampletype");
			sa.setLabel("<h3>Samples:</h3>");
			view.add(sa);

			EntityTable fc = new EntityTable("flowcells", flowcells.values(), false, "identifier", "machine_name",
					"run", "startDate");
			fc.setLabel("<h3>Flowcells:</h3>");
			view.add(fc);

			EntityTable l = new EntityTable("libraryLanes", liblanes.values(), false, "flowcell_identifier", "lane",
					"barcode_name", "sample_identifier", "prepKit_name", "capturingKit_name");
			l.setLabel("<h3>Libraries loaded on lanes</h3>");
			view.add(l);
			
			EntityTable m = new EntityTable("machines", machines.values(), false, "name");
			m.setLabel("<h3>Machines used:</h3>");
			view.add(m);

			EntityTable b = new EntityTable("barcodes", barcodes.values(), false, "name","barcodeType");
			b.setLabel("<h3>Barcodes used:</h3>");
			view.add(b);
			
			EntityTable c = new EntityTable("capturingKits", capturingKits.values(), false, "name");
			c.setLabel("<h3>Capturing kits used:</h3>");
			view.add(c);
			
			EntityTable k = new EntityTable("prepKits", prepKits.values(), false, "name");
			k.setLabel("<h3>Prep kits used:</h3>");
			view.add(k);
		}

		return view;
	}

}