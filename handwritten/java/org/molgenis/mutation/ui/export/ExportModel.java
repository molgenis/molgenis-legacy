/* Date:        June 7, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.export;

import org.molgenis.framework.ui.EasyPluginModel;

/**
 * ExportModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class ExportModel extends EasyPluginModel
{
	//a system veriable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	private String csv;

	public ExportModel(Export controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}

	public String getCsv()
	{
		return csv;
	}

	public void setCsv(String csv)
	{
		this.csv = csv;
	}
}
