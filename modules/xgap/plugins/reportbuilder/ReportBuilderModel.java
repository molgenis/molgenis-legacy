package plugins.reportbuilder;

import java.util.List;
import java.util.Map;

import org.molgenis.util.Entity;


public class ReportBuilderModel{

	private Map<String, Integer> annotationTypeAndNr;
	private List<? extends Entity> disambiguate;
	private Report report;
	
	private String selectedAnnotationTypeAndNr;
	private String selectedName;

	public Map<String, Integer> getAnnotationTypeAndNr()
	{
		return annotationTypeAndNr;
	}

	public void setAnnotationTypeAndNr(Map<String, Integer> annotationTypeAndNr)
	{
		this.annotationTypeAndNr = annotationTypeAndNr;
	}

	public List<? extends Entity> getDisambiguate()
	{
		return disambiguate;
	}

	public void setDisambiguate(List<? extends Entity> disambiguate)
	{
		this.disambiguate = disambiguate;
	}

	public String getSelectedAnnotationTypeAndNr()
	{
		return selectedAnnotationTypeAndNr;
	}

	public void setSelectedAnnotationTypeAndNr(String selectedAnnotationTypeAndNr)
	{
		this.selectedAnnotationTypeAndNr = selectedAnnotationTypeAndNr;
	}

	public String getSelectedName()
	{
		return selectedName;
	}

	public void setSelectedName(String selectedName)
	{
		this.selectedName = selectedName;
	}

	public Report getReport()
	{
		return report;
	}

	public void setReport(Report report)
	{
		this.report = report;
	}
	
	
	

}
