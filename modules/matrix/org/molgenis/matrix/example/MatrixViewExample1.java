
package org.molgenis.matrix.example;

import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MemoryMatrix;
import org.molgenis.matrix.ui.MatrixViewer;

/**
 * MatrixViewExample1Controller takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>MatrixViewExample1Model holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>MatrixViewExample1View holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class MatrixViewExample1 extends EasyPluginController<MatrixViewExample1Model>
{
	public MatrixViewExample1(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new MatrixViewExample1Model(this)); //the default model
		this.setView(new FreemarkerView("MatrixViewExample1View.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
		List<String> rows = Arrays.asList(new String[]{"row1","row2"});
		List<String> cols = Arrays.asList(new String[]{"col1","col2"});
		MemoryMatrix<String> matrix = new MemoryMatrix<String>(rows,cols);
		matrix.setValue("row1","col1", "cel11");
		matrix.setValue("row1","col2", "cel12");
		matrix.setValue("row2","col1", "cel21");
		matrix.setValue("row2","col2", "cel12");
		
		getModel().setMatrixView(new MatrixViewer(this,"testmatrix", matrix));
	}

}