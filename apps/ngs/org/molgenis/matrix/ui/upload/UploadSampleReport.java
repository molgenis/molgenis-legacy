package org.molgenis.matrix.ui.upload;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.PhenoMemoryMatrix;
import org.molgenis.matrix.StringCsvMemoryMatrix;
import org.molgenis.matrix.ui.PhenoMatrixView;
import org.molgenis.matrix.ui.StringMatrixView;
import org.molgenis.ngs.LibraryLane;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.Tuple;

/**
 * UploadSampleReportController takes care of all user requests and application
 * logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>UploadSampleReportModel holds application state and business logic
 * on top of domain model. Get it via this.getModel()/setModel(..) <li>
 * UploadSampleReportView holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class UploadSampleReport extends
		EasyPluginController<UploadSampleReportModel>
{
	private static final long serialVersionUID = 6754376605793548861L;

	public enum State
	{
		UPLOAD, CHECK, SAVED
	};

	// current state of the plugin
	private State state = State.UPLOAD;
	// place where the uploaded csv is stored
	Matrix<String, String, String> csvMatrix;
	// place where the uploaded data can be viewed
	PhenoMemoryMatrix<LibraryLane, ObservableFeature> phenoMatrix;

	public UploadSampleReport(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new UploadSampleReportModel(this)); // the default model
		// this.setView(new FreemarkerView("UploadSampleReportView.ftl",
		// getModel())); //<plugin flavor="freemarker"
	}

	@Override
	public String render()
	{
		try
		{
			switch (state)
			{
				case UPLOAD:
					return renderUpload();
				case CHECK:
					return renderCheck();
				case SAVED:
					return renderSaved();
				default:
					return "ERROR";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
			return "ERROR" + e.getMessage();
		}
	}

	private String renderSaved()
	{
		MolgenisForm f = new MolgenisForm(this.getModel());

		f.add(new TextParagraph("t",
				"Done. TODO: show result."));
		
		f.add(new PhenoMatrixView<LibraryLane, ObservableFeature> ("test",phenoMatrix));

		f.add(new ActionInput("doReset", "back"));

		return f.render();
	}

	// show the uploaded CSV in the matrix and a button to go 'back' and one to
	// 'save'
	private String renderCheck()
	{

		MolgenisForm f = new MolgenisForm(this.getModel());

		f.add(new TextParagraph("t",
				"Check here your parsed matrix. Click save if you are happy."));
		
		f.add(new StringMatrixView("test", csvMatrix));

		f.add(new ActionInput("doReset", "back"));

		f.add(new ActionInput("doSave", "save data"));

		return f.render();

	}

	// render the first screen showing a textarea with upload
	private String renderUpload()
	{

		MolgenisForm f = new MolgenisForm(this.getModel());

		f
				.add(new TextParagraph(
						"t",
						"paste here your data matrix in CSV or TAB format. First row is column headers and has 1 value less. First column matches the names of the Targets."));

		f.add(new TextInput("csv", "paste your data here"));

		f.add(new ActionInput("doUpload", "upload data"));

		return f.render();

	}

	public void doUpload(Database db, Tuple request)
	{
		// try to upload the csv into a csv matrix
		try
		{
			CsvReader reader = new CsvStringReader(request.getString("csv"));
			this.csvMatrix = new StringCsvMemoryMatrix(reader);
			this.setSucces("data uploaded and parsed succesfully");
			this.state = State.CHECK;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	public void doReset(Database db, Tuple request)
	{
		this.state = State.UPLOAD;
	}

	public void doSave(Database db, Tuple request)
	{
		// copy the matrix into a pheno matrix and store
		try
		{
			PhenoMemoryMatrix<LibraryLane, ObservableFeature> phenoMatrix = new PhenoMemoryMatrix<LibraryLane, ObservableFeature>(
					LibraryLane.class, ObservableFeature.class, csvMatrix);
			phenoMatrix.store(db);
			
			this.phenoMatrix = new PhenoMemoryMatrix<LibraryLane, ObservableFeature>(
					LibraryLane.class, ObservableFeature.class, db);

			this.state = State.SAVED;
			this.setSucces("Pheno matrix saved succesfully");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
		}
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
		//na
	}
	
}