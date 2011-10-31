//package org.molgenis.compute.ui;
//
//import java.io.OutputStream;
//
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.ui.EasyPluginController;
//import org.molgenis.framework.ui.FreemarkerView;
//import org.molgenis.framework.ui.ScreenController;
//import org.molgenis.framework.ui.ScreenModel.Show;
//import org.molgenis.framework.ui.html.MolgenisForm;
//import org.molgenis.framework.ui.html.Paragraph;
//import org.molgenis.ngs.NgsSample;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.util.HandleRequestDelegationException;
//import org.molgenis.util.Tuple;
//
///**
// * MatrixTestsController takes care of all user requests and application logic.
// * 
// * <li>Each user request is handled by its own method based action=methodName.
// * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
// * user <li>MatrixTestsModel holds application state and business logic on top
// * of domain model. Get it via this.getModel()/setModel(..) <li>MatrixTestsView
// * holds the template to show the layout. Get/set it via
// * this.getView()/setView(..).
// */
//public class SampleMatrix extends EasyPluginController<SampleMatrixModel>
//{
//	private static final long serialVersionUID = 2924809526072222758L;
//	MatrixViewer matrixViewer = null;
//	MolgenisForm form = null;
//	Paragraph selection = null;
//
//	public SampleMatrix(String name, ScreenController<?> parent)
//	{
//		super(name, null, parent);
//		this.setModel(new SampleMatrixModel(this));
//		this.setView(new FreemarkerView("MatrixTestsView.ftl", getModel()));
//	}
//
//	@Override
//	public Show handleRequest(Database db, Tuple request, OutputStream out)
//			throws HandleRequestDelegationException
//	{
//		if (request.getAction().startsWith(matrixViewer.getName()))
//		{
//			matrixViewer.handleRequest(db, request);
//		}
//		else
//		{
//			this.delegate(request.getAction(), db, request);
//		}
//		// default show
//		return Show.SHOW_MAIN;
//	}
//
//	/**
//	 * At each page view: reload data from database into model and/or change.
//	 * 
//	 * Exceptions will be caught, logged and shown to the user automatically via
//	 * setMessages(). All db actions are within one transaction.
//	 */
//	@Override
//	public void reload(Database db) throws Exception
//	{
//
//		SliceablePhenoMatrix<NgsSample, Measurement> m = new SliceablePhenoMatrix(
//				db, NgsSample.class, Measurement.class);
//		matrixViewer = new MatrixViewer(this, "mymatrix", m);
//
//		form = new MolgenisForm(this);
//		// add the matrix
//		form.add(matrixViewer);
//	}
//
//	public String render()
//	{
//		return form.render();
//	}
//
//	
//}
