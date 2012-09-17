/* Date:        June 7, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.export;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	private Date submissionDate;

	public ExportModel(Export controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		try {
			submissionDate = dfm.parse("2011-07-01");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCsv()
	{
		return csv;
	}

	public void setCsv(String csv)
	{
		this.csv = csv;
	}

	public Date getSubmissionDate()
	{
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate)
	{
		this.submissionDate = submissionDate;
	}
}
