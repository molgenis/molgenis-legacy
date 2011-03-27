/* Date:        August 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Tuple;


public class YearlyReportModule extends PluginModel
{
	private static final long serialVersionUID = -9028427140087603225L;
	private AnimalDBReport report = null;
	private int year;
	private String form;
	private List<Integer> lastYearsList;
	
	public String getCustomHtmlHeaders()
    {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }
	
	public AnimalDBReport getReport() {
		return report;
	}

	public void setReport(AnimalDBReport report) {
		this.report = report;
	}

	public YearlyReportModule(String name, ScreenModel parent)
	{
		super(name, parent);
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		this.lastYearsList = new ArrayList<Integer>();
		for (int earlier = 0; earlier < 5; earlier++) {
			this.lastYearsList.add(currentYear - earlier);
		}
	}
	
	public List<Integer> getLastYearsList() {
		return lastYearsList;
	}

	@Override
	public String getViewName()
	{
		return "plugins_report_YearlyReportModule";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/report/YearlyReportModule.ftl";
	}
	
	private void makeReport(Database db) {
		
		if (form.equals("4A")) {
			report = new VWAReport4(db);
			report.makeReport(year, "A");
		}
		if (form.equals("4B")) {
			report = new VWAReport4(db);
			report.makeReport(year, "B");
		}
		if (form.equals("4C")) {
			report = new VWAReport4(db);
			report.makeReport(year, "C");
		}
		if (form.equals("5")) {
			report = new VWAReport5(db);
			report.makeReport(year, "");
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
		
			if( action.equals("generateYearlyReport") )
			{
				this.year = request.getInt("year");
				this.form = request.getString("form");
				makeReport(db);
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		//
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		
		if(this.getLogin().isAuthenticated()){
			return true;
		}else
		{
			return false;
		}
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public int getYear()
	{
		return year;
	}

	public void setForm(String form)
	{
		this.form = form;
	}

	public String getForm()
	{
		return form;
	}
}
