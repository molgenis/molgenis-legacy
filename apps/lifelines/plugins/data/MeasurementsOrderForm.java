package plugins.data;

import gcc.catalogue.ShoppingCart;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.molgenis.util.SimpleEmailService.EmailException;
import org.molgenis.util.Tuple;

public class MeasurementsOrderForm extends PluginModel<Entity>{


	private static final long serialVersionUID = -8140222842047905408L;
	private ShoppingCart shoppingCart = null;
	private MolgenisUser user = null;
	
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
		
		this.reload(db);
		
		
		if ("EmptyShoppingCart".equals(request.getAction())) {
	
			this.emptyShoppingCart(db);
			this.reload(db);
			
		}else if ("checkoutOrder".equals(request.getAction())) {
			if (shoppingCart == null){
				this.getModel().getMessages().add(new ScreenMessage("Your shopping cart is empty. You cannot continue with the checkout! Please visit the catalogue tree.", true));
				this.reload(db);
			}
			else if (!this.checkIfUserDetailsEmpty(db)) {
				this.getModel().getMessages().add(new ScreenMessage("Please complete your profile first!", true));
			}
			else {
				System.out.println("checkIfUserDetailsEmpty:>>>>>>>>>>>>>>>>>>>>"+this.checkIfUserDetailsEmpty(db));
				
				this.updateShoppingCartAsCheckedOut(db);
	    		this.sendOrderEmail(db); 
				this.getModel().getMessages().add(new ScreenMessage("Your orders request has been sent!", true));
				this.reload(db);
			}
		}
	}


	public void updateShoppingCartAsCheckedOut(Database db) {
		shoppingCart.setCheckedOut(true);
		try {
			db.update(shoppingCart);

		} catch (DatabaseException e) {
			this.getModel().getMessages().add(new ScreenMessage("A problem with update shopping cart has occured", true));
			e.printStackTrace();
		}
	}
	
	public boolean checkIfUserDetailsEmpty(Database db) {
		boolean allFieldsAvailable = false;
		
		try {
			user = MolgenisUser.findById(db, this.getLogin().getUserId());
			if (!(user.getAddress() == null ||
				  user.getCity() == null || 
				  user.getDepartment() == null ||
				  user.getAffiliation() == null))
				  allFieldsAvailable = true; 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return allFieldsAvailable;
	}
	
	public void sendOrderEmail(Database db) throws DatabaseException  {
		MolgenisUser admin = db.query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().get(0);
		if (StringUtils.isEmpty(admin.getEmail()))
			throw new DatabaseException("Registration failed: the administrator has no email address set used to confirm your registration. Please contact your administrator about this.");
		

		String emailContents = "Dear admin, " + "\n\n"; 
		emailContents += "The user : "+ this.getLogin().getUserName() +"\n";
		emailContents += "has sent a request for the items/measurements below:" + "\n";
		for (String name : shoppingCart.getMeasurements_Name()) {
			emailContents += name + "\n";
		}
		emailContents += "\n User details: \n"; 
		emailContents += "Title: "+ user.getTitle() +"\n";
		emailContents += "First Name: "+ user.getFirstName() +"\n";
		emailContents += "Last Name: "+ user.getLastName() +"\n";
		emailContents += "Department: "+ user.getDepartment() +"\n";
		
		emailContents += "Email: "+ user.getEmail() +"\n";
		emailContents += "Phone: "+ user.getPhone() +"\n";
		emailContents += "Fax: "+ user.getFax() +"\n";
		emailContents += "TollFreePhone: "+ user.getTollFreePhone() +"\n";
		emailContents += "Address: "+ user.getAddress() +"\n"; 
		emailContents += "Phone: "+ user.getPhone() +"\n";

		emailContents += "Department: "+ user.getDepartment() +"\n";
		emailContents += "Affiliation: "+ user.getAffiliation() +"\n";
		emailContents += "City: "+ user.getCity() +"\n";
		emailContents += "Country: "+ user.getCountry() +"\n";
		
		//TODO :Institute,	Position
		
		System.out.println(emailContents);
		try {
			this.getEmailService().email("New items/measurements ordered", emailContents, admin.getEmail(), true);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void emptyShoppingCart(Database db) {
		//empty db table: actually delete the ones that have checkedOut='false' 
		List<ShoppingCart> resshoppingCart  = new ArrayList<ShoppingCart>();
		Query<ShoppingCart> q = db.query(ShoppingCart.class);
		q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, false));
		try {
			db.beginTx();
			resshoppingCart = q.find();
			db.remove(resshoppingCart);
			db.commitTx();

		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		this.reload(db);
		this.getModel().getMessages().add(new ScreenMessage("Your shopping cart is now empty, you can reload items from catalogue tree", true));
	}
	
	@Override
	public void reload(Database db) {
		System.out.println("At RELOAD>>>>>>>>>>>>>>>");


		try {
			db.beginTx();
			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, false));

			if (!q.find().isEmpty()) shoppingCart = q.find().get(0);
			db.commitTx();

			System.out.println(">>>>@@@@@@>>>>"+shoppingCart);
		} catch (Exception e) {
			this.getModel().getMessages().add(new ScreenMessage("No shopping cart available", false));
			e.printStackTrace();
		}

	}

	public ShoppingCart getshoppingCart() {
		return shoppingCart;
	}
}
