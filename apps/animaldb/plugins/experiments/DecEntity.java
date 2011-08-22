package plugins.experiments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DecEntity {
	private int id;
	private String name;
	private Date startDate;
	private Date endDate;
	private SimpleDateFormat oldDateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US); // for showing in the old date box
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US); // how it comes out of the DB nowadays
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setStartDate(String startDate) throws ParseException
	{
		this.startDate = newDateOnlyFormat.parse(startDate);
	}
	public String getStartDate()
	{
		if (startDate == null) return "";
		return oldDateOnlyFormat.format(startDate);
	}
	
	public void setEndDate(String endDate) throws ParseException
	{
		this.endDate = newDateOnlyFormat.parse(endDate);
	}
	public String getEndDate()
	{
		if (endDate == null) return "";
		return oldDateOnlyFormat.format(endDate);
	}
}
