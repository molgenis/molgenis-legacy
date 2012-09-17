/* Date:        July 3, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.matrix.example;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.matrix.ui.MatrixViewer;

/**
 * MatrixViewExample1Model takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class MatrixViewExample1Model extends EasyPluginModel
{
	private MatrixViewer matrixView;
	
	public MatrixViewExample1Model(MatrixViewExample1 controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}

	public MatrixViewer getMatrixView()
	{
		return matrixView;
	}

	public void setMatrixView(MatrixViewer view)
	{
		this.matrixView = view;
	}
	
	
	
}
