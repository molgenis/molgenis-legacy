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
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
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
	SliceablePhenoMatrix<Individual, Measurement> matrix;
	MatrixViewer matrixViewer;

	public MatrixTests(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new MatrixTestsModel(this)); // the default model
		this.setView(new FreemarkerView("MatrixTestsView.ftl", getModel())); // <plugin
																				// flavor="freemarker"
		matrix = new SliceablePhenoMatrix(this.getDatabase(), Individual.class,
				Measurement.class);
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
		matrixViewer = new MatrixViewer("mymatrix", matrix);

		MolgenisForm f = new MolgenisForm(this);

		f.add(new ActionInput("generateData"));
		f.add(new Newline());
		// rowlimit
		f.add(new IntInput("rowLimit", matrix.getRowLimit()));
		f.add(new ActionInput("changeRowLimit"));
		f.add(new Newline());
		// colLimit
		f.add(new IntInput("colLimit", matrix.getColLimit()));
		f.add(new ActionInput("changeColLimit"));
		f.add(new Newline());

		// move horizontal
		f.add(new ActionInput("moveLeftEnd"));
		f.add(new ActionInput("moveLeft"));
		f.add(new ActionInput("moveRight"));
		f.add(new ActionInput("moveRightEnd"));
		f.add(new Newline());

		// move vertical
		f.add(new ActionInput("moveUpEnd"));
		f.add(new ActionInput("moveUp"));
		f.add(new ActionInput("moveDown"));
		f.add(new ActionInput("moveDownEnd"));
		f.add(new Newline());
		
		try
		{
			// test column filters, currently only 'equals' and 'sort'. Of
			// course this should only show fields in the list
			f.add(new LabelInput("Add column filter:"));
			f.add(new Newline());
			SelectInput colIndex = new SelectInput("colIndex");
			colIndex.setEntityOptions(matrix.getColHeaders());
			colIndex.setNillable(true);
			f.add(colIndex);
			f.add(new StringInput("colValue"));
			f.add(new ActionInput("colEquals"));
			f.add(new Newline());

			// test collumn filters, currently only 'equals' and 'sort'
			f.add(new LabelInput("Add row filter:"));
			f.add(new Newline());
			SelectInput rowIndex = new SelectInput("rowIndex");
			rowIndex.setEntityOptions(matrix.getRowHeaders());
			colIndex.setNillable(true);
			f.add(rowIndex);
			f.add(new StringInput("rowValue"));
			f.add(new ActionInput("rowEquals"));
			f.add(new Newline());

			f.add(new ActionInput("clearFilters", "", "Reset"));

		}
		catch (Exception e)
		{
			this.setError(e.getMessage());
			e.printStackTrace();
		}

		// add the matrix
		f.add(matrixViewer);

		return f.render();
	}

	public void clearFilters(Database db, Tuple t) throws MatrixException
	{
		matrix.reset();
	}

	public void colEquals(Database db, Tuple t) throws MatrixException
	{
		matrix.sliceByColValueProperty(t.getInt("colIndex"),
				ObservedValue.VALUE, QueryRule.Operator.LIKE,
				t.getObject("colValue"));
	}
	
	public void rowEquals(Database db, Tuple t) throws MatrixException
	{
		matrix.sliceByRowValueProperty(t.getInt("rowIndex"),
				ObservedValue.VALUE, QueryRule.Operator.LIKE,
				t.getObject("rowValue"));
	}

	public void changeRowLimit(Database db, Tuple t)
	{
		this.matrix.setRowLimit(t.getInt("rowLimit"));
	}

	public void changeColLimit(Database db, Tuple t)
	{
		this.matrix.setColLimit(t.getInt("colLimit"));
	}

	public void moveLeftEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix.setColOffset(0);
	}

	public void moveLeft(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setColOffset(matrix.getColOffset() - matrix.getColLimit() > 0 ? matrix
						.getColOffset() - matrix.getColLimit()
						: 0);
	}

	public void moveRight(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setColOffset(matrix.getColOffset() + matrix.getColLimit() < matrix
						.getColCount() ? matrix.getColOffset()
						+ matrix.getColLimit() : matrix.getColOffset());
	}

	public void moveRightEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setColOffset((matrix.getColCount() % matrix.getColLimit() == 0 ? new Double(
						matrix.getColCount() / matrix.getColLimit()).intValue() - 1
						: new Double(matrix.getColCount()
								/ matrix.getColLimit()).intValue())
						* matrix.getColLimit());
	}

	public void moveUpEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix.setRowOffset(0);
	}

	public void moveUp(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setRowOffset(matrix.getRowOffset() - matrix.getRowLimit() > 0 ? matrix
						.getRowOffset() - matrix.getRowLimit()
						: 0);
	}

	public void moveDown(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setRowOffset(matrix.getRowOffset() + matrix.getRowLimit() < matrix
						.getRowCount() ? matrix.getRowOffset()
						+ matrix.getRowLimit() : matrix.getRowOffset());
	}

	public void moveDownEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setRowOffset((matrix.getRowCount() % matrix.getRowLimit() == 0 ? new Double(
						matrix.getRowCount() / matrix.getRowLimit()).intValue() - 1
						: new Double(matrix.getRowCount()
								/ matrix.getRowLimit()).intValue())
						* matrix.getRowLimit());
	}

	public void generateData(Database db, Tuple t) throws DatabaseException
	{
		// individuals
		List<Individual> individuals = new ArrayList<Individual>();
		for (int i = 0; i < 25; i++)
		{
			if (db.query(Individual.class).eq(Individual.NAME, "inv" + i)
					.count() == 0)
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
			if (db.query(Measurement.class).eq(Measurement.NAME, "meas" + i)
					.count() == 0)
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
