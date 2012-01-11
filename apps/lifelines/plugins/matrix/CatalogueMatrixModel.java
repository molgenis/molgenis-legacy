/* Date:        October 21, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.matrix.component.MatrixViewer;


/**
 * GidsMatrixModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class CatalogueMatrixModel extends EasyPluginModel
{
	//a system variable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	
	MatrixViewer matrixViewerCat = null;
	static String CATMATRIX = "catmatrix";
	boolean error = false;
	String selection = null;
	private String investigation;
	

	public CatalogueMatrixModel(CatalogueMatrix controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}
	

	
	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	/**
	 * Render the matrix viewer as html.
	 * 
	 * @return
	 */
	public String getMatrixViewerIndv() {
		if (matrixViewerCat != null) {
			//matrixViewerIndv.setToHtmlDb(toHtmlDb);
			return matrixViewerCat.render();
		} else {
			return "No viewer available, matrix cannot be rendered.";
		}
	}
	
	public boolean isError() {
		return error;
	}
	
	
	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	public void setError(boolean error) {
		this.error = error;
	}
}
