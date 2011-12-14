package plugins.data;

import gcc.catalogue.ShoppingCart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.Tuple;

public class MeasurementsOrderForm extends PluginModel<Entity>{


	private static final long serialVersionUID = -8140222842047905408L;
	
	private String Status = "Welcome to Measurements Order Form";

	//private List<String> shoppingCart = new ArrayList<String>();
	private List<ShoppingCart> shoppingCart = new ArrayList<ShoppingCart>();
	
	public MeasurementsOrderForm(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_data_MeasurementsOrderForm";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/MeasurementsOrderForm.ftl";
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/shopping_cart.css\">";
    }

	
	@Override
	public void handleRequest(Database db, Tuple request) throws HandleRequestDelegationException, Exception {
		//TODO : Retrieve shopping list here . Is there a convenient way to pass it form the redirected plugin (will it still be convenient with larger amount of items?)
		//TODO : maybe consider adding  shopping List in the database. 
		
		this.reload(db);
		
		
		if ("EmptyShoppingCart".equals(request.getAction())) {
			//empty db table
			String  truncateShpCrtSql = String.format("truncate table shoppingCart;");
			ResultSetTuple removedRecords = new ResultSetTuple(db.executeQuery(truncateShpCrtSql));
			this.reload(db);
			this.setSuccess("Your shopping cart is now empty, you can reload items from catalogue tree");
			//db.executeQuery(query, queryRules) remove(ShoppingCart.class);
			
		}else if ("checkoutOrder".equals(request.getAction())) {
			if (shoppingCart.isEmpty()) this.setSuccess("Your shopping cart is empty.You cannot continue with the checkout!Please visit the catalogue tree.");
			else {
	    		System.out.println(">>>shoppingCart>>>>>>>>"+ shoppingCart);

	    		String emailContents = "Dear Salome, " + "\n\n"; 
	    		emailContents += "The user : "+ this.getLogin().getUserName() +"\n";
	    		emailContents += "has send a request for the items/measurements below:" + "\n";
	    		for (int i=0; i<shoppingCart.size(); i++) {
	    			emailContents += shoppingCart.get(i) + "\n";
	    		}
	    		emailContents += "\n\n" ;

	    		System.out.println(emailContents);
	    		this.getEmailService().email("New items/measurements ordered", emailContents, "antonakd@gmail.com", true);
	    		
	    		this.getModel().getMessages().add(new ScreenMessage("Your orders request has been sent!", true));
	    		
			}
		}
	}

	@Override
	public void reload(Database db) {
		System.out.println("request>checkoutOrder>>>>>>>>>");
		
		Query<ShoppingCart> q = db.query(ShoppingCart.class);
		q.addRules(new QueryRule("userID", Operator.EQUALS, this.getLogin().getUserName()));
		try {
			shoppingCart = q.find();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		this.setStatus("Shopping cart loaded");

	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}

	public List<ShoppingCart> getshoppingCart() {
		return shoppingCart;
	}
}
