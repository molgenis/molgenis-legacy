package plugins.requestData;

import gcc.catalogue.ShoppingCart;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.BoolInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.EmailInput;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.RichtextInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.util.EmailService;
import org.molgenis.util.SimpleEmailService.EmailException;
import org.molgenis.util.Tuple;

/**
 * LifeLinesRequestController takes care of all user requests and application
 * logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>LifeLinesRequestModel holds application state and business logic on
 * top of domain model. Get it via this.getModel()/setModel(..) <li>
 * LifeLinesRequestView holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class LifeLinesRequest extends EasyPluginController<LifeLinesRequestModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9143685202494784007L;
	boolean submitted = false;
	Tuple tuple = null;

	final static String FIRSTNAME = "FirstName";
	final static String LASTNAME = "LastName";

	public LifeLinesRequest(String name, ScreenController<?> parent) {
		super(name, null, parent);
		this.setModel(new LifeLinesRequestModel(this)); // the default model
		// this.setView(new FreemarkerView("LifeLinesRequestView.ftl",
		// getModel())); //<plugin flavor="freemarker"
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception {
		// //example: update model with data from the database
		// Query q = db.query(Investigation.class);
		// q.like("name", "molgenis");
		// getModel().investigations = q.find();
	}

	/**
	 * When action="updateDate": update model and/or view accordingly.
	 * 
	 * Exceptions will be logged and shown to the user automatically. All db
	 * actions are within one transaction.
	 */
	public void updateDate(Database db, Tuple request) throws Exception {
		getModel().date = request.getDate("date");

		// //Easily create object from request and add to database
		// Investigation i = new Investigation(request);
		// db.add(i);
		// this.setMessage("Added new investigation");

		getModel().setSuccess("Update successful");
	}

	public void submit(Database db, Tuple request) throws DatabaseException, EmailException {
		sentEmail(db, request);
		this.setSucces("Request submitted succesfully");
		submitted = true;
		tuple = request;
	}
	
	public void sentEmail(Database db, Tuple request) throws DatabaseException, EmailException {
		String subject   = "Data request";
		
		ShoppingCart shc = new ShoppingCart();
		shc = db.query(ShoppingCart.class).eq(ShoppingCart.ID, request.getString("MyMeasurementSelection") ).find().get(0);
		shc.getName();
		
		String email = "Hello admin, \n\n" +
				" User " + request.getString(FIRSTNAME)	+ " "+
				request.getString(LASTNAME) + 
				"(" + request.getString("emailAddress") + ")" +	
				" has requested data with id: " +
				request.getString("MyMeasurementSelection") +
				" and name: " + shc.getName();
		
		if (request.getString("GWAS")=="true") email +=  " GWAS ";
		
		email+= " with NoIndividuals: "+ request.getString("NoIndividuals") +
			    " Summary:"+ request.getString("Summary");
				
		// get admin email
		MolgenisUser admin = db.query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().get(0);
		if (StringUtils.isEmpty(admin.getEmail()))
			throw new DatabaseException("Sending data request failed: the administrator has no email address set. Please contact your administrator about this.");
		
		
		EmailService ses = this.getEmailService();
		ses.email(subject, email, admin.getEmail(), true);
		
		//this.getMessages().add(new ScreenMessage(feedback, true));
		
		System.out.println("Email : " + admin.getEmail()+ "data request >>>"+ email);
	}

	public String render() {
		MolgenisForm form = new MolgenisForm(this.getModel());

		if (submitted) {
			submitted = false;

			form.add(new Paragraph(
					"Thank you <b>"
							+ tuple.getString(FIRSTNAME)
							+ " "
							+ tuple.getString(LASTNAME)
							+ "</b> for the request. You should have received a copy of your request in your mailbox. The LifeLines data officer will contact your shortly."));
			return form.render();
		}

		DivPanel l = new DivPanel();
		l.setLabel("<h3>LifeLines Data Request:</h3>");

		l.add(new Paragraph("Please specify here your LifeLines data request. You request will be sent to ... for evaluation."));

		StringInput first = new StringInput(FIRSTNAME);
		first.setNillable(false);
		l.add(first);

		StringInput last = new StringInput(LASTNAME);
		last.setNillable(false);
		l.add(last);

		EmailInput email = new EmailInput("emailAddress");
		email.setNillable(false);
		l.add(email);

		XrefInput x = new XrefInput("MyMeasurementSelection", ShoppingCart.class);
		x.setNillable(false);
		l.add(x);

		BoolInput b = new BoolInput("GWAS");
		b.setNillable(false);
		b.setValue(false);
		l.add(b);

		IntInput i = new IntInput("NoIndividuals");
		i.setNillable(false);
		l.add(i);

		RichtextInput t = new RichtextInput("Summary");
		t.setNillable(false);
		l.add(t);

		l.add(new ActionInput("submit"));

		form.add(l);

		return form.render();
	}
	
//	@Override
//	public boolean isVisible()
//	{
//		//you can use this to hide this plugin, e.g. based on user rights.
//		//e.g.
//		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
//		if (!this.getLogin().isAuthenticated()) {
//			return false;
//		}
//		return true;
//	}
}