package org.molgenis.animaldb.plugins.administration;

public class DecProject extends DecEntity {
	private int decAppListId;
	private String decNr;
	private String decTitle;
	private String decApplicantName;
	private String pdfDecApplication;
	private String pdfDecApproval;
	private String fieldBiology;
	private String decBudget;
	
	public void setDecAppListId(int decAppListId)
	{
		this.decAppListId = decAppListId;
	}
	public int getDecAppListId()
	{
		return decAppListId;
	}
	
	public void setDecNr(String decNr) {
		this.decNr = decNr;
	}
	public String getDecNr() {
		return decNr;
	}
	public String getDecTitle() {
		return decTitle;
	}
	public void setDecTitle(String decTitle) {
		this.decTitle = decTitle;
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
	public String getDecBudget()
	{
		return decBudget;
	}
	public void setDecBudget(String decBudget)
	{
		this.decBudget = decBudget;
	}
}
