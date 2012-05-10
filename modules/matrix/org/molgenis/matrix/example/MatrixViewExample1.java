
package org.molgenis.matrix.example;

import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.matrix.StringMemoryMatrix;
import org.molgenis.matrix.ui.MatrixViewer;

/**
 * Can be removed?
 */
public class MatrixViewExample1 extends EasyPluginController<MatrixViewExample1Model>
{
	public MatrixViewExample1(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new MatrixViewExample1Model(this)); //the default model
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("MatrixViewExample1View.ftl", getModel());
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
		StringMemoryMatrix matrix = new StringMemoryMatrix(rows,cols);
		matrix.setValue("row1","col1", "cel11");
		matrix.setValue("row1","col2", "cel12");
		matrix.setValue("row2","col1", "cel21");
		matrix.setValue("row2","col2", "cel12");
		
		getModel().setMatrixView(new MatrixViewer(this,"testmatrix", matrix));
	}

}