package plugins.LLcatalogueTree;

import java.util.Vector;

import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.util.SimpleTree;

public class JQueryTreeViewMeasurement<E> extends JQueryTreeView<E>{

	private SimpleTree<JQueryTreeViewElementMeasurement> treeData;
	
	public JQueryTreeViewMeasurement(String name, SimpleTree treeData) {
		super(name, treeData);
		this.treeData = treeData;
	}
	
	public SimpleTree<JQueryTreeViewElementMeasurement> getTree (){
		return treeData;
	}
}
