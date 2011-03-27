package plugins.report;

import java.util.List;

import org.molgenis.framework.db.Database;

import commonservice.CommonService;

public abstract class AnimalDBReport {
	
	protected CommonService ct;
	protected Database db;
	protected int year;
	protected int nrCol;
	protected List<String> warningsList;
	
	abstract public void makeReport(int year, String type);
}
