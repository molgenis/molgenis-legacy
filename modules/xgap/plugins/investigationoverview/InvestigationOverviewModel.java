package plugins.investigationoverview;

import java.util.HashMap;

import org.molgenis.data.Data;
import org.molgenis.organization.Investigation;

public class InvestigationOverviewModel{

	private Investigation selectedInv;

	private HashMap<String, String> annotationList;
	
	private HashMap<String, Data> expList;
	
	private HashMap<String, String> expDimensions;
	
	private HashMap<String, String> otherList;
	
	private Boolean showAllAnnotations;
	
	private Boolean showAllExperiments;
	
	private Boolean showAllOther;
	
	private Boolean viewDataByTags;
	
	
	private Boolean fileLinkoutIsVisible;
	
	
	

	public Boolean getFileLinkoutIsVisible()
	{
		return fileLinkoutIsVisible;
	}

	public void setFileLinkoutIsVisible(Boolean fileLinkoutIsVisible)
	{
		this.fileLinkoutIsVisible = fileLinkoutIsVisible;
	}

	public HashMap<String, String> getExpDimensions()
	{
		return expDimensions;
	}

	public void setExpDimensions(HashMap<String, String> expDimensions)
	{
		this.expDimensions = expDimensions;
	}

	public Boolean getViewDataByTags()
	{
		return viewDataByTags;
	}

	public void setViewDataByTags(Boolean viewDataByTags)
	{
		this.viewDataByTags = viewDataByTags;
	}

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

	public HashMap<String, Data> getExpList()
	{
		return expList;
	}

	public void setExpList(HashMap<String, Data> expList)
	{
		this.expList = expList;
	}

	

}
