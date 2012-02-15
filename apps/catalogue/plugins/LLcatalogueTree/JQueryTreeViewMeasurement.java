package plugins.LLcatalogueTree;


import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.util.SimpleTree;

public class JQueryTreeViewMeasurement<E> extends JQueryTreeView<E>{

	private SimpleTree<JQueryTreeViewElementObject> treeData;
	
	public JQueryTreeViewMeasurement(String name, SimpleTree treeData) {
		super(name, treeData);
		this.treeData = treeData;
	}
	
	public SimpleTree<JQueryTreeViewElementObject> getTree (){
		return treeData;
	}
}
