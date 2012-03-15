package org.molgenis.animaldb.plugins.breeding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Litter {
	private int id;
	private String name;
	private Date birthDate;
	private Date weanDate;
	private int size;
	private String isSizeApproximate;
	private int weanSize;
	private String parentgroup = "";
	private String remarks;
	private String status;
	private String line = "";
	//private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
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
	public void setBirthDate(String birthDate) throws ParseException {
		if (birthDate != null) {
			try {
				this.birthDate = dateOnlyFormat.parse(birthDate);
			} catch (ParseException e) {
				//this.birthDate = dateTimeFormat.parse(birthDate);
			}
		} else {
			this.birthDate = null;
		}
	}
	public String getBirthDate() {
		if (birthDate == null) return "";
		return dateOnlyFormat.format(birthDate);
	}
	public void setWeanDate(String weanDate) throws ParseException {
		if (weanDate != null) {
			try {
				this.weanDate = dateOnlyFormat.parse(weanDate);
			} catch (ParseException e) {
				//this.weanDate = dateTimeFormat.parse(weanDate);
			}
		} else {
			this.weanDate = null;
		}
	}
	public String getWeanDate() {
		if (weanDate == null) return "";
		return dateOnlyFormat.format(weanDate);
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getSize() {
		if (size == -1) return "";
		return Integer.toString(size);
	}
	public void setSizeApproximate(String isSizeApproximate) {
		this.isSizeApproximate = isSizeApproximate;
	}
	public String getIsSizeApproximate() {
		return isSizeApproximate;
	}
	public void setWeanSize(int weanSize) {
		this.weanSize = weanSize;
	}
	public String getWeanSize() {
		if (weanSize == -1) return "";
		return Integer.toString(weanSize);
	}
	public void setParentgroup(String parentgroup) {
		this.parentgroup = parentgroup;
	}
	
	public String getParentgroup() {
		if (parentgroup == null) return "";
		return parentgroup;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getLine() {
		return line;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
}
