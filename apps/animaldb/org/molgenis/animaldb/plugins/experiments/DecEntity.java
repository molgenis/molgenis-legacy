package org.molgenis.animaldb.plugins.experiments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DecEntity {
	private int id;
	private String name;
	private Date startDate;
	private Date endDate;
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // for showing in the new date box
	private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // how it comes out of the DB nowadays
	
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
		if (startDate != null && !startDate.equals("")) {
			this.startDate = dbFormat.parse(startDate);
		} else {
			this.startDate = null;
		}
	}
	public String getStartDate()
	{
		if (startDate == null) {
			return "";
		}
		return newDateOnlyFormat.format(startDate);
	}
	
	public void setEndDate(String endDate) throws ParseException
	{
		if (endDate != null && !endDate.equals("")) {
			this.endDate = dbFormat.parse(endDate);
		} else {
			this.endDate = null;
		}
		
	}
	public String getEndDate()
	{
		if (endDate == null) return "";
		return newDateOnlyFormat.format(endDate);
	}
}
