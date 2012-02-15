package plugins.LLcatalogueTree;


import org.molgenis.auth.Institute;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;

public class JQueryTreeViewElementObject extends JQueryTreeViewElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Label of the tree (that can be made also linkable)  **/ 
	String label;
	Measurement measurement;

	private JQueryTreeViewMeasurement<JQueryTreeViewElementObject> treeView;

	public JQueryTreeViewElementObject(Measurement measurement,Category category,Institute institute, JQueryTreeViewElementObject parent){

		super(measurement.getName(), parent);

		//this.setLabel("<a href=\"#\" onclick=\"$('#leftSide').attr('value','selectNode'); jQuery('#measureId').attr('value','"+measurement.getId()+"'); $('#plugins_LLcatalogueTree_LLcatalogueTreePlugin').submit();\" >"+ name +"</a>");
		this.setLabel(name);
		setMeasurementObject(measurement);
	}

	public JQueryTreeViewElementObject(Measurement measurement, JQueryTreeViewElementObject parent, String htmlValue){

		super(measurement.getName(), parent, htmlValue);

		//this.setLabel("<a href=\"#\" onclick=\"$('#leftSide').attr('value','selectNode'); jQuery('#measureId').attr('value','"+measurement.getId()+"'); $('#plugins_LLcatalogueTree_LLcatalogueTreePlugin').submit();\" >"+ name +"</a>");
		this.setLabel(name);
		setMeasurementObject(measurement);
	}

	public JQueryTreeViewElementObject(String displayName, JQueryTreeViewElementObject parent, String htmlValue){

		super(displayName, parent, htmlValue);

	}
	
	public JQueryTreeViewElementObject(String name, JQueryTreeViewElementObject parent) {
		super(name, parent);
		measurement = null;
	}

	public JQueryTreeViewElementObject(String name, JQueryTreeViewElementObject parent, String url, String category)
	{
		super(name, parent, url, category);
		measurement = null;
	}

	public Measurement getMeasurementObject(){
		return measurement;
	}

	public void setMeasurementObject(Measurement measurement){
		this.measurement = measurement;
	}


	public void setTreeView (JQueryTreeViewMeasurement<JQueryTreeViewElementObject> treeView) {
		this.treeView = treeView;
	}

	private JQueryTreeViewMeasurement<JQueryTreeViewElementObject> getTreeView() {
		return treeView;
	}



}