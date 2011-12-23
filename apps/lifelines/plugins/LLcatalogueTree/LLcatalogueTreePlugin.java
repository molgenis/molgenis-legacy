package plugins.LLcatalogueTree;

import gcc.catalogue.ShoppingCart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;



public class LLcatalogueTreePlugin extends PluginModel<Entity> {
	
	private static final long serialVersionUID = -6143910771849972946L;
	private JQueryTreeViewMeasurement<JQueryTreeViewElementMeasurement> treeView = null;
	private HashMap<String, Protocol> nameToProtocol;
	private HashMap<String, JQueryTreeViewElementMeasurement> labelToTree;
	private List<Measurement> shoppingCart = new ArrayList<Measurement>();
	
	public LLcatalogueTreePlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/shopping_cart.css\">";
    }
	
	public void handleRequest(Database db, Tuple request) {

		try {
			if ("OrderMeasurements".equals(request.getAction())) {
				this.addMeasurementsToTree(db, request);
				
			} else if (request.getAction().startsWith("DeleteMeasurement")) {
				
				String measurementName  =  request.getString("measurementName"); //TODO :  this is not working
				measurementName = request.getAction().substring("DeleteMeasurement".length()+2+"measurementName".length(), request.getAction().length());
				this.deleteShoppingItem(measurementName);
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			this.setError("There was a problem handling yor order: " + e.getMessage());
		}

	}

	private void addMeasurementsToTree(Database db, Tuple request) throws DatabaseException, IOException {
		
		// fill shopping cart using selected selectboxes (measurements)
		// the ID's and names of the selectboxes are the same as the measurement names,
		// so we can easily get them from the request
		List<Measurement> allMeasList  = db.find(Measurement.class);
		for (Measurement m : allMeasList) {
			if (request.getBool(m.getName()) != null) {
				this.shoppingCart.add(m);
			}
		}

		List<Integer> orderedMeasurementIds = new ArrayList<Integer>();
		for (Measurement m : this.shoppingCart) {
			orderedMeasurementIds.add(m.getId());
		}
		
		Query<ShoppingCart> q = db.query(ShoppingCart.class);
		q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
		q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, false));
		List<ShoppingCart> result = q.find();
		
		if (result.isEmpty()) {
			//Add to database 
			ShoppingCart shoppingCart = new ShoppingCart();
			shoppingCart.setMeasurements(orderedMeasurementIds);
			shoppingCart.setUserID(this.getLogin().getUserName());
			shoppingCart.setCheckedOut(false);
			db.add(shoppingCart);
			System.out.println("Shopping cart has been added to the DB");
			
		} else {
			ShoppingCart shoppingCart = result.get(0); // assuming user can have only one shopping cart that's NOT checked out
			shoppingCart.setMeasurements(orderedMeasurementIds);
			db.update(shoppingCart);
			System.out.println("Shopping cart has been updated in the DB");
		}
			
		HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
		HttpServletRequest httpRequest   = rt.getRequest();
		HttpServletResponse httpResponse = rt.getResponse();
		String redirectURL = httpRequest.getRequestURL() + "?__target=" + this.getParent().getName() + "&select=MeasurementsOrderForm";
		
		httpResponse.sendRedirect(redirectURL);
	}
	
	private void deleteShoppingItem(String selected) {
		//search the item
		for (int i=0; i<this.shoppingCart.size(); i++) {
			if (this.shoppingCart.get(i).getName().equals(selected)) {
				this.shoppingCart.remove(i);
				this.getModel().getMessages().add(new ScreenMessage("The item \""+ selected + "\" has been successfully removed from your shopping cart", true));
			}
		}
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

	public void addingMeasurementTotree(List<String> childNode,
			JQueryTreeViewElementMeasurement parentTree, Database db) {

		 try {
			List<Measurement> measurementList = db.find(Measurement.class, new QueryRule(
					Measurement.NAME, Operator.IN, childNode));
			for (Measurement measurement : measurementList) {

				JQueryTreeViewElementMeasurement childTree;
				if (labelToTree.containsKey(measurement.getName())) {
					childTree = labelToTree.get(measurement.getName());
				} else {
					childTree = new JQueryTreeViewElementMeasurement(measurement, parentTree);
					labelToTree.put(measurement.getName(), childTree);
				}
			}
		} catch (DatabaseException e) {
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
			
			for (Protocol p : db.find(Protocol.class/*, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, "DataShaper")*/)) {
				// Hardcoded filter should go!!! Otherwise this only works for one situation

				List<String> subNames = p.getSubprotocols_Name();

				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

				if (!subNames.isEmpty()) {

					if (!topProtocols.contains(p.getName())) {
						topProtocols.add(p.getName());
					}
					for (String subProtocol : subNames) {
						if (!middleProtocols.contains(subProtocol)) {
							middleProtocols.add(subProtocol);
						}
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
	}

	public String getTreeView() {
		return treeView.toHtml();
	}

	public List<Measurement> getShoppingCart() {
		return shoppingCart;
	}

}
