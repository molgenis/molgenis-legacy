package plugins.experiments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DecProject {
	private int id;
	private int decAppListId;
	private String name;
	private String decNr;
	private String decApplicantName;
	private String pdfDecApplication;
	private String pdfDecApproval;
	private String fieldBiology;
	private Date startDate;
	private Date endDate;
	private SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setDecAppListId(int decAppListId)
	{
		this.decAppListId = decAppListId;
	}
	public int getDecAppListId()
	{
		return decAppListId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setDecNr(String decNr) {
		this.decNr = decNr;
	}
	public String getDecNr() {
		return decNr;
	}
	public void setDecApplicantName(String decApplicantName) {
		this.decApplicantName = decApplicantName;
	}
	public String getDecApplicantName() {
		return decApplicantName;
	}
	public void setPdfDecApplication(String pdfDecApplication) {
		this.pdfDecApplication = pdfDecApplication;
	}
	public String getPdfDecApplication() {
		return pdfDecApplication;
	}
	public void setPdfDecApproval(String pdfDecApproval) {
		this.pdfDecApproval = pdfDecApproval;
	}
	public String getPdfDecApproval() {
		return pdfDecApproval;
	}
	public void setFieldBiology(String fieldBiology) {
		this.fieldBiology = fieldBiology;
	}
	public String getFieldBiology() {
		return fieldBiology;
	}
	public void setStartDate(String startDate) throws ParseException
	{
		this.startDate = sdf.parse(startDate);
	}
	public String getStartDate()
	{
		if (startDate == null) return "";
		return sdf.format(startDate);
	}
	public void setEndDate(String endDate) throws ParseException
	{
		this.endDate = sdf.parse(endDate);
	}
	public String getEndDate()
	{
		if (endDate == null) return "";
		return sdf.format(endDate);
	}
}
