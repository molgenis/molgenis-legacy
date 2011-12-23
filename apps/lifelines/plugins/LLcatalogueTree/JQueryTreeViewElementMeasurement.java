package plugins.LLcatalogueTree;

import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.pheno.Measurement;

public class JQueryTreeViewElementMeasurement extends JQueryTreeViewElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Label of the tree (that can be made also linkable)  **/ 
	String label;
	Measurement measurement;

	private JQueryTreeViewMeasurement treeView;
	
	public JQueryTreeViewElementMeasurement(Measurement measurement, JQueryTreeViewElementMeasurement parent){
		super(measurement.getName(), parent);
		
		//this.setLabel("<a href=\"#\" onclick=\"$('#leftSide').attr('value','selectNode'); jQuery('#measureId').attr('value','"+measurement.getId()+"'); $('#plugins_LLcatalogueTree_LLcatalogueTreePlugin').submit();\" >"+ name +"</a>");
		this.setLabel(name);
		setMeasurementObject(measurement);
	}

	public JQueryTreeViewElementMeasurement(String name, JQueryTreeViewElementMeasurement parent) {
		super(name, parent);
		measurement = null;
	}
	
	public JQueryTreeViewElementMeasurement(String name, JQueryTreeViewElementMeasurement parent, String url)
	{
		super(name, parent, url);
		measurement = null;
	}

	public Measurement getMeasurementObject(){
		return measurement;
	}
	
	public void setMeasurementObject(Measurement measurement){
		this.measurement = measurement;
	}


	public void setTreeView (JQueryTreeViewMeasurement treeView) {
			
		this.treeView = treeView;
		
	}
	
}