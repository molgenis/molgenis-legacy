package plugins.LLcatalogueTree;

import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.pheno.Measurement;

public class JQueryTreeViewElementMeasurement extends JQueryTreeViewElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Label of the tree that can be made also linkable  **/ 
	String label;
	Measurement measurement;

	private JQueryTreeViewMeasurement treeView;
	
	public JQueryTreeViewElementMeasurement(Measurement measurement, JQueryTreeViewElementMeasurement parent, String url){
		super(measurement.getName(), parent);
		//must set measurementId, __action, __target
		this.setLabel("<a href=\"#\" onclick=\"jQuery('#test').attr('value','selectNode'); jQuery('#measureId').attr('value','"+measurement.getId()+"'); jQuery('form').submit();\" >"+ name +"</a>");
		
		setMeasurementObject(measurement);
	}

	public JQueryTreeViewElementMeasurement(String name, JQueryTreeViewElementMeasurement parent) {
		// TODO Auto-generated constructor stub
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