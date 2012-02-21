package org.molgenis.sandbox.ui;

import java.io.OutputStream;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.matrix.MatrixException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;
import java.util.LinkedHashMap;
import javax.persistence.EntityManager;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.Protocol;

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
public class MatrixTestsMV extends EasyPluginController<MatrixTestsModelMV>
{
	private static final long serialVersionUID = 2924809526072222758L;
	MatrixViewer matrixViewer = null;
	MolgenisForm form = null;
	Paragraph selection = null;

	@SuppressWarnings({ "unchecked", "deprecation" })
	public MatrixTestsMV(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new MatrixTestsModelMV(this));
		this.setView(new FreemarkerView("MatrixTestsView.ftl", getModel()));

		try {
                    Database db = DatabaseFactory.create();
                    EntityManager em = db.getEntityManager();
                    int investigationId = 50;
                    Investigation investigation = em.find(Investigation.class, investigationId);
                    Protocol protocolBezoek1 = em.find(Protocol.class, 50);
                    Protocol protocolBlood = em.find(Protocol.class, 51);
                    LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol = new LinkedHashMap<Protocol, List<Measurement>>();
                    measurementByProtocol.put(protocolBezoek1, (List<Measurement>)(List)protocolBezoek1.getFeatures());
                    measurementByProtocol.put(protocolBlood, (List<Measurement>)(List)protocolBlood.getFeatures());
//                    List<Column> columns = getColumnsFromMeasurementByProtocol(measurementByProtocol);
                    
			matrixViewer = new MatrixViewer(this, "mymatrix", 
					new SliceablePhenoMatrixMV<ObservationTarget, Measurement, ObservedValue>(DatabaseFactory.create(), 
							ObservationTarget.class, Measurement.class, investigation, measurementByProtocol), 
							true, 0, false, false, null);
		} catch (Exception e) {
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
		if (request.getAction().startsWith(matrixViewer.getName())) {
			matrixViewer.handleRequest(db, request);
		} else {
			this.delegate(request.getAction(), db, request);
		}
		//default show
		return Show.SHOW_MAIN;
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
		form = new MolgenisForm(this);
		// add the matrix
		form.add(matrixViewer);
		// add a button to save the current selection
		form.add(new ActionInput("saveSelection", "", "Save current selection to plugin"));
		// add selection (if any)
		if (selection != null) {
			form.add(selection);
		}
	}

	public String render()
	{
		return form.render();
	}
	
	public void saveSelection(Database db, Tuple t) throws DatabaseException, MatrixException {
		List<? extends ObservationElement> rows = (List<? extends ObservationElement>) matrixViewer.getSelection(db);
		String selectionItems = "<ul>";
		for (ObservationElement row : rows) {
			selectionItems += "<li>" + row.getName() + "</li>";
		}
		selectionItems += "</ul>";
		selection = new Paragraph("selection", "You selected from the Matrix component:" + selectionItems);
	}

//	public void generateData(Database db, Tuple t) throws DatabaseException
//	{
//		// individuals
//		List<Individual> individuals = new ArrayList<Individual>();
//		for (int i = 0; i < 25; i++)
//		{
//			if (db.query(Individual.class).eq(Individual.NAME, "inv" + i).count() == 0)
//			{
//				Individual inv = new Individual();
//				inv.setName("inv" + i);
//				inv.setOwns(2);
//				individuals.add(inv);
//			}
//		}
//		// measurements
//		List<Measurement> measurements = new ArrayList<Measurement>();
//		for (int i = 0; i < 25; i++)
//		{
//			if (db.query(Measurement.class).eq(Measurement.NAME, "meas" + i).count() == 0)
//			{
//				Measurement meas = new Measurement();
//				meas.setName("meas" + i);
//				meas.setOwns(2);
//				measurements.add(meas);
//			}
//		}
//		db.add(individuals);
//		db.add(measurements);
//
//		// values
//		List<ObservedValue> values = new ArrayList<ObservedValue>();
//		for (Individual i : individuals)
//		{
//			for (Measurement m : measurements)
//			{
//				ObservedValue v = new ObservedValue();
//				v.setFeature(m);
//				v.setTarget(i);
//				v.setValue("val" + i.getId() + "," + m.getId());
//				values.add(v);
//			}
//		}
//		db.add(values);
//
//	}
}
