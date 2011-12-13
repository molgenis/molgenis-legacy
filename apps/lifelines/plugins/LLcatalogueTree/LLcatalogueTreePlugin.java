package plugins.LLcatalogueTree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;



public class LLcatalogueTreePlugin extends PluginModel<Entity> {
	private String Status = "Welcome!";
	
	private static final long serialVersionUID = -6143910771849972946L;

	private JQueryTreeViewMeasurement<JQueryTreeViewElementMeasurement> treeView = null;

	private HashMap<String, Protocol> nameToProtocol;

	private HashMap<String, JQueryTreeViewElementMeasurement> labelToTree;

	private List<Measurement> shoppingCart = new ArrayList<Measurement>();
	
	CheckboxInput checkBoxInput ;
	
	public String MeasurementName = "------------------------------------->";

	public LLcatalogueTreePlugin(String name, ScreenController<?> parent) {
		super(name, parent);

	}
	
	/** My new action */
	public void handleRequest(Database db, Tuple request) {

		System.out.println("CAUGHT IT: " + request);
//		System.out.println(request.getInt("measurementId"));
//		System.out.println(request.getString("measurementName"));
//		System.out.println(request.getAction().startsWith("DeleteMeasurement"));

		try {

			Measurement selected = Measurement.findById(db, request.getInt("measurementId"));
			System.out.println("selected measurement id >>>>>>"+ selected);
			if (selected == null) {
				this.setError("measurementId unknown: "	+ request.getInt("measurementId"));
			} else {
				System.out.println("--->" + selected);
				this.shoppingCart.add(selected);
				this.setSuccess("The item \""+ selected.getName() + "\" has been successfully added to your shopping cart");

				// clean the ordered measurement list form dublicates
				this.shoppingCart = cleanShoppingCart();

				 
				Vector<ValueLabel> ShoppingCartOptions = null;
				List<String> shoppingCartLabels = new Vector<String>() ;
				for (int i=0; i<this.shoppingCart.size(); i++) {
					shoppingCartLabels.add(this.shoppingCart.get(i).getName());
				}
		
				this.checkBoxInput = new CheckboxInput("ShoppingCart", "Shopping Cart", "ShoppingCart", ShoppingCartOptions, shoppingCartLabels);
				checkBoxInput.render();
				
				
			}
			if ("OrderMeasurements".equals(request.getAction())) {
				HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
				HttpServletRequest httpRequest   = rt.getRequest();
				HttpServletResponse httpResponse = rt.getResponse();
				
				//String redirectURL = httpRequest.getRequestURL() + "?__target=main" + "&select=" + this.getApplicationController().getLogin().getRedirect();
				String redirectURL = httpRequest.getRequestURL() + "?__target=main" + "&select=MeasurementsOrderForm" ;
				httpResponse.sendRedirect(redirectURL);
				
				this.setStatus("<h4>You order is being processed.</h4>" ) ;
			} //TODO fix this : else if ("DeleteMeasurement".equals(request.getAction()))	{
			else if (request.getAction().startsWith("DeleteMeasurement")) {
				
				System.out.println("Here's the request on DELETE :"+ request);
				String measurementName  =  request.getString("measurementName"); //TODO :  this is not working
				measurementName = request.getAction().substring("DeleteMeasurement".length()+2+"measurementName".length(), request.getAction().length());
				
				System.out.println("Here's the request.measurement id on DELETE:"+ measurementName);
				this.deleteShoppingItem(measurementName);
			} 
				
			
		} catch (Exception e) {
			//this.setError("LLCataloguetreePlugin handle request " + e.getMessage());
		}

	}

	private void add(CheckboxInput checkboxInput) {
		// TODO Auto-generated method stub
		
	}

	private void deleteShoppingItem(String selected) {
		//search the item
		System.out.println(">>Trying to remove " + selected );
		
		for (int i=0; i<this.shoppingCart.size(); i++) {
			if (this.shoppingCart.get(i).getName().equals(selected)) {
				this.shoppingCart.remove(i);
				this.setStatus("The item \""+ selected + "\" has been successfully removed from your shopping cart");
				this.setSuccess("The item \""+ selected + "\" has been successfully removed from your shopping cart");
			}
		}
		
		System.out.println(">>Shopping cart after the removal : " + this.shoppingCart);
	}

	private List<Measurement> cleanShoppingCart() {
		List<Measurement> newShoppingCart = new ArrayList<Measurement>(); 
		
		for (int i=0; i<this.getShoppingCart().size(); i++) {
			Measurement m = this.getShoppingCart().get(i);
			if (!newShoppingCart.contains(m)) newShoppingCart.add(m);
		}
		
		return newShoppingCart;
	}

	public void recursiveAddingTree(List<String> parentNode,
			JQueryTreeViewElementMeasurement parentTree, Database db) {

		for (String protocolName : parentNode) {

			Protocol protocol = nameToProtocol.get(protocolName);

			if (protocol != null) {

				JQueryTreeViewElementMeasurement childTree;

				if (labelToTree.containsKey(protocolName)) {

					childTree = labelToTree.get(protocolName);

				} else {

					childTree = new JQueryTreeViewElementMeasurement(
							protocolName, parentTree);
					childTree.setCollapsed(true);
					labelToTree.put(protocolName, childTree);
				}

				if (protocol.getSubprotocols_Name() != null
						&& protocol.getSubprotocols_Name().size() > 0) {

					recursiveAddingTree(protocol.getSubprotocols_Name(),
							childTree, db);

				}
				if (protocol.getFeatures_Name() != null
						&& protocol.getFeatures_Name().size() > 0) {

					addingMeasurementTotree(protocol.getFeatures_Name(),
							childTree, db);
				}
			}
		}
	}

	// @Override
	// public String getCustomHtmlBodyOnLoad()
	// {
	//
	//
	// // JQuerySplitterContents c = new JQuerySplitterContents();
	// // JQuerySplitter2<JQuerySplitterContents> jqs2 = new
	// JQuerySplitter2<JQuerySplitterContents>("aaa", c);
	//
	//
	// return treeView.toHtml();;
	//
	// }

	public void addingMeasurementTotree(List<String> childNode,
			JQueryTreeViewElementMeasurement parentTree, Database db) {

		String url = "molgenis.do?__target=catalogueOverview&select=measurement";

		// System.out.println(childNode);

		List<Measurement> measurementList;
		try {
			measurementList = db.find(Measurement.class, new QueryRule(
					Measurement.NAME, Operator.IN, childNode));

			for (Measurement measurement : measurementList) {

				JQueryTreeViewElementMeasurement childTree;

				if (labelToTree.containsKey(measurement.getName())) {

					childTree = labelToTree.get(measurement.getName());

				} else {

					childTree = new JQueryTreeViewElementMeasurement(
							measurement, parentTree, url);

					labelToTree.put(measurement.getName(), childTree);
				}
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getViewName() {
		return "plugins_LLcatalogueTree_LLcatalogueTreePlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/LLcatalogueTree/LLcatalogueTreePlugin.ftl";
	}

	

	@Override
	public void reload(Database db) {

		List<String> topProtocols = new ArrayList<String>();

		List<String> bottomProtocols = new ArrayList<String>();

		List<String> middleProtocols = new ArrayList<String>();

		labelToTree = new HashMap<String, JQueryTreeViewElementMeasurement>();

		nameToProtocol = new HashMap<String, Protocol>();

		try {
			
			for (Protocol p : db.find(Protocol.class, 
					new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, "DataShaper"))) {

				// System.out.println(p.getName());

				List<String> subNames = p.getSubprotocols_Name();

				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

				if (!subNames.isEmpty()) {

					if (!topProtocols.contains(p.getName()))
						topProtocols.add(p.getName());

					for (String subProtocol : subNames) {

						if (!middleProtocols.contains(subProtocol))
							middleProtocols.add(subProtocol);
					}

				} else {

					if (!bottomProtocols.contains(p.getName())) {
						bottomProtocols.add(p.getName());

					}
				}
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		middleProtocols.removeAll(bottomProtocols);

		topProtocols.removeAll(middleProtocols);

		JQueryTreeViewElementMeasurement protocolsTree = new JQueryTreeViewElementMeasurement(
				"Protocols", null);

		if(topProtocols.size() == 0){
			recursiveAddingTree(bottomProtocols, protocolsTree, db);

		}else{
			recursiveAddingTree(topProtocols, protocolsTree, db);
		}

		treeView = new JQueryTreeViewMeasurement<JQueryTreeViewElementMeasurement>(
				"Protocols", protocolsTree);

		// a = db.query();
		treeView.setMeasurementDetails("Measurement details........... ");

	}

	@Override
	public boolean isVisible() {
		if (!this.getLogin().isAuthenticated()) {
			return false;
		}
		return true;
	}

	public String getcheckBoxInput(){
		return this.checkBoxInput.toHtml();
	}
	public String getTreeView() {
		return treeView.toHtml();
	}

	public String getMeasurementDetails() {
		return "getMeasurementDetails";
	}

	public List<Measurement> getShoppingCart() {
		return shoppingCart;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}

}
