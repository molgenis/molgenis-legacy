package plugins.investigationoverview;

import java.util.HashMap;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;
import org.molgenis.organization.Investigation;

public class InvestigationOverviewModel extends SimpleScreenModel{
	
	public InvestigationOverviewModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private Investigation selectedInv;

	private HashMap<String, String> AnnotationList;
	
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
		return AnnotationList;
	}

	public void setAnnotationList(HashMap<String, String> annotationList)
	{
		AnnotationList = annotationList;
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

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}

	

}
