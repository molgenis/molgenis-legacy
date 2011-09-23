package org.molgenis.sandbox.ui;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.LabelInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.ObservationElementMatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/**
 * MatrixTestsController takes care of all user requests and application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>MatrixTestsModel holds application state and business logic on top
 * of domain model. Get it via this.getModel()/setModel(..) <li>MatrixTestsView
 * holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class MatrixTests extends EasyPluginController<MatrixTestsModel>
{

	ObservationElementMatrixViewer matrixViewer;

	public MatrixTests(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new MatrixTestsModel(this)); // the default model
		this.setView(new FreemarkerView("MatrixTestsView.ftl", getModel())); // <plugin
																				// flavor="freemarker"

		matrixViewer = new ObservationElementMatrixViewer(this, "mymatrix", new SliceablePhenoMatrix(this.getDatabase(),
				Individual.class, Measurement.class));
	}

	public void handleRequest(Database db, Tuple t)
	{
		try
		{
			if (t.getAction().startsWith(matrixViewer.getName()))
			{
				matrixViewer.handleRequest(db, t);
			}
			else
			{
				// other actions to be done by hand

				this.delegate(t.getAction(), db, t);

			}

		}
		catch (HandleRequestDelegationException e)
		{
			// TODO Auto-generated catch block
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

	}

	public String render()
	{

		MolgenisForm f = new MolgenisForm(this);

		// add the matrix
		f.add(matrixViewer);

		f.add(new ActionInput("generateData"));
		f.add(new Newline());

		return f.render();
	}

	public void generateData(Database db, Tuple t) throws DatabaseException
	{
		// individuals
		List<Individual> individuals = new ArrayList<Individual>();
		for (int i = 0; i < 25; i++)
		{
			if (db.query(Individual.class).eq(Individual.NAME, "inv" + i).count() == 0)
			{
				Individual inv = new Individual();
				inv.setName("inv" + i);
				inv.setOwns(2);
				individuals.add(inv);
			}
		}
		// measurements
		List<Measurement> measurements = new ArrayList<Measurement>();
		for (int i = 0; i < 25; i++)
		{
			if (db.query(Measurement.class).eq(Measurement.NAME, "meas" + i).count() == 0)
			{
				Measurement meas = new Measurement();
				meas.setName("meas" + i);
				meas.setOwns(2);
				measurements.add(meas);
			}
		}
		db.add(individuals);
		db.add(measurements);

		// values
		List<ObservedValue> values = new ArrayList<ObservedValue>();
		for (Individual i : individuals)
		{
			for (Measurement m : measurements)
			{
				ObservedValue v = new ObservedValue();
				v.setFeature(m);
				v.setTarget(i);
				v.setValue("val" + i.getId() + "," + m.getId());
				values.add(v);
			}
		}
		db.add(values);

	}
}
