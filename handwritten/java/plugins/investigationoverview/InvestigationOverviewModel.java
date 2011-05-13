package plugins.investigationoverview;

import java.util.HashMap;
import org.molgenis.organization.Investigation;

public class InvestigationOverviewModel{

	private Investigation selectedInv;

	private HashMap<String, String> annotationList;
	
	private HashMap<String,String> expList;
	
	private HashMap<String,String> otherList;
	
	private Boolean showAllAnnotations;
	
	private Boolean showAllExperiments;
	
	private Boolean showAllOther;
	
	public Investigation getSelectedInv()
	{
		return selectedInv;
	}

	public void setSelectedInv(Investigation selectedInv)
	{
		this.selectedInv = selectedInv;
	}

	public HashMap<String, String> getAnnotationList()
	{
		return annotationList;
	}

	public void setAnnotationList(HashMap<String, String> annotationList)
	{
		this.annotationList = annotationList;
	}

	public HashMap<String, String> getOtherList()
	{
		return otherList;
	}

	public void setOtherList(HashMap<String, String> otherList)
	{
		this.otherList = otherList;
	}

	public Boolean getShowAllAnnotations()
	{
		return showAllAnnotations;
	}

	public void setShowAllAnnotations(Boolean showAllAnnotations)
	{
		this.showAllAnnotations = showAllAnnotations;
	}

	public Boolean getShowAllExperiments()
	{
		return showAllExperiments;
	}

	public void setShowAllExperiments(Boolean showAllExperiments)
	{
		this.showAllExperiments = showAllExperiments;
	}

	public Boolean getShowAllOther()
	{
		return showAllOther;
	}

	public void setShowAllOther(Boolean showAllOther)
	{
		this.showAllOther = showAllOther;
	}

	public HashMap<String, String> getExpList()
	{
		return expList;
	}

	public void setExpList(HashMap<String, String> expList)
	{
		this.expList = expList;
	}
	

}
