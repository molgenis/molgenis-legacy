/* Date:        December 3, 2008
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generate.screen.PluginScreenJavaTemplateGen 3.0.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.auth.ui;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.OpenIdLogin;
import org.molgenis.auth.service.MolgenisUserException;
import org.molgenis.auth.service.MolgenisUserService;
import org.molgenis.auth.ui.form.DatabaseAuthenticationForm;
import org.molgenis.auth.ui.form.ForgotForm;
import org.molgenis.auth.ui.form.OpenIdAuthenticationForm;
import org.molgenis.auth.ui.form.RegistrationForm;
import org.molgenis.auth.ui.form.UserAreaForm;
import org.molgenis.auth.vo.MolgenisUserSearchCriteriaVO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

/**
 * This screen shows a login box, or if someone is already logged in, the user
 * information and a logout button.
 */
public class UserLogin extends EasyPluginController<UserLoginModel>
{
	private static final long serialVersionUID = -3084964114182861171L;

	public UserLogin(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new UserLoginModel(this));
		this.setView(new FreemarkerView("UserLogin.ftl", getModel()));
	}

	public void Login(Database db, Tuple request) throws DatabaseException, IOException
	{
		this.getModel().setAction("Login");

		if ("Google".equals(request.getString("op")) || "Yahoo".equalsIgnoreCase(request.getString("op")) || "authenticated".equals(request.getString("op")))
		{
			try
			{
				// get the http request that is encapsulated inside the tuple
				HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
				HttpServletRequest httpRequest   = rt.getRequest();

				// get the http response that is used in this handleRequest
				HttpServletResponse httpResponse = rt.getResponse();

				String returnURL = httpRequest.getRequestURL() + "?__target=" + this.getName() + "&__action=" + request.getAction() + "&op=authenticated";

				if (!(this.getApplicationController().getLogin() instanceof OpenIdLogin))
					throw new Exception("Wrong parameter.");

				((OpenIdLogin) this.getApplicationController().getLogin()).authenticate(db, httpRequest, httpResponse, returnURL, request.getString("op"));

//				this.getDatabaseUser(db);

				// login.authRequest(request.getString("name"));
				// login.verifyResponse();
				
				this.getApplicationController().getLogin().reload(db);
			}
			catch (Exception e)
			{
				throw new DatabaseException("OpenID login failed: " + e.getMessage());
			}
		}
		else if (StringUtils.isNotEmpty(request.getString("username")) && StringUtils.isNotEmpty(request.getString("password")))
		{
			String username = request.getString("username");
			String password = request.getString("password");

			boolean loggedIn = this.getApplicationController().getLogin().login(db, username, password);

			if (!loggedIn)
				throw new DatabaseException("Login failed: username or password unknown");
			
			HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
			HttpServletRequest httpRequest   = rt.getRequest();
			HttpServletResponse httpResponse = rt.getResponse();

			if (StringUtils.isNotEmpty(this.getApplicationController().getLogin().getRedirect()))
			{
				String redirectURL = httpRequest.getRequestURL() + "?__target=main" + "&select=" + this.getApplicationController().getLogin().getRedirect();
				httpResponse.sendRedirect(redirectURL);
			}
		} else {
			throw new DatabaseException("Login failed: username or password empty");
		}
	}

	public void Logout(Database db, Tuple request) throws DatabaseException, ParseException
	{
		this.getModel().setAction("Logout");
		this.getApplicationController().getLogin().logout();
		this.getApplicationController().getLogin().reload(db);
	}

	public void Register(Database db, Tuple request)
	{
		this.getModel().setAction("Register");
	}
	
	public void Cancel(Database db, Tuple request)
	{
		this.getModel().setAction("Cancel");
	}

	public void AddUser(Database db, Tuple request) throws DatabaseException
	{
		this.getModel().setAction("AddUser");

		try
		{
			if (this.getApplicationController().getLogin().isAuthenticated())
			{
				 // if logged in, log out first
				this.getApplicationController().getLogin().logout();
			}
			
			// login as admin
			// (a bit evil but less so than giving anonymous write-rights on the
			// MolgenisUser table)
			this.getApplicationController().getLogin().login(db, "admin", "admin");
			this.getApplicationController().getLogin().reload(db);
			
			MolgenisUserService userService = MolgenisUserService.getInstance(db);
			MolgenisUser user               = this.toMolgenisUser(request);
			userService.insert(user);

			// get the http request that is encapsulated inside the tuple
			HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
			HttpServletRequest httpRequest   = rt.getRequest();

			// Email the user
			String activationURL =
				httpRequest.getRequestURL().toString() +
				"?__target=" + this.getName() +
				"&select=" + this.getName() +
				"&__action=Activate&actCode=" + user.getActivationCode();
			String emailContents = "Somebody, probably you, requested a user account for " + this.getRoot().getLabel() + ".\n";
			emailContents += "Please visit the following URL in order to activate your account:\n";
			emailContents += activationURL + "\n\n";
			emailContents += "If you have not requested an account please ignore this mail.";
			
			//assuming: 'encoded' p.w. (setting deObf = true)
			this.getEmailService().email("Your registration request", emailContents, user.getEmailaddress(), true);
			
			this.getModel().getMessages().add(new ScreenMessage("Adding user successful - check your e-mail for activation instructions", true));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getModel().setAction("Register");
			throw new DatabaseException("Adding user failed: " + e.getMessage());
		}
		finally
		{
			this.getApplicationController().getLogin().logout();
		}
	}

	public void Activate(Database db, Tuple request)
	{
		this.getModel().setAction("Activate");

	    try
		{
		
    		if (this.getApplicationController().getLogin().isAuthenticated())
    		{
    			 // if logged in, log out first
    			this.getApplicationController().getLogin().logout();
    		}
    		
    		// login as admin
    		// (a bit evil but less so than giving anonymous write-rights on the
    		// MolgenisUser table)
    		this.getApplicationController().getLogin().login(db, "admin", "admin");
    		this.getApplicationController().getLogin().reload(db);

			MolgenisUserSearchCriteriaVO criteria = new MolgenisUserSearchCriteriaVO();
			criteria.setActivationCode(request.getString("actCode"));

			MolgenisUserService userService       = MolgenisUserService.getInstance(db);
			List<MolgenisUser> users              = userService.find(criteria);

			if (users.size() != 1)
				throw new MolgenisUserException("No user found for activation code.");
			
			MolgenisUser user                     = users.get(0);
			user.setActive(true);
			userService.update(user);

			this.getModel().getMessages().add(new ScreenMessage("Activation successful", true));

			// Email the curator
			// TODO: Where to get admin/curator address from?
//			String emailContents = "Dear curator, a new user has activated their account:\n" +
//			user.getTitle() + " " + user.getFirstname() + " " + user.getLastname() + 
//			", " + user.getPosition() + " at the department " + user.getDepartment() +
//			" of " + user.getInstitute() + ".";
//			this.getEmailService().email("New user", emailContents, "p.c.van.den.akker@medgen.umcg.nl", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getModel().getMessages().add(new ScreenMessage("Activation failed", false));
		}
		finally
		{
			this.getApplicationController().getLogin().logout();
		}
	}
	
	public void sendPassword(Database db, Tuple request)
	{
	    try
		{
    		if (this.getApplicationController().getLogin().isAuthenticated())
    		{
    			 // if logged in, log out first
    			this.getApplicationController().getLogin().logout();
    		}
    		
    		// login as admin
    		// (a bit evil but less so than giving anonymous write-rights on the
    		// MolgenisUser table)
    		this.getApplicationController().getLogin().login(db, "admin", "admin");
    		this.getApplicationController().getLogin().reload(db);

    		MolgenisUserSearchCriteriaVO criteria = new MolgenisUserSearchCriteriaVO();
    		criteria.setName(request.getString("username"));

    		MolgenisUserService userService = MolgenisUserService.getInstance(db);
    		List<MolgenisUser> users        = userService.find(criteria);
		
    		if (users.size() != 1) {
    			throw new MolgenisUserException("No user found with this username.");
    		}
    		
    		MolgenisUser user  = users.get(0);
  
    		String newPassword = UUID.randomUUID().toString().substring(0, 8);
    		user.setPassword(newPassword);
    		userService.update(user);

    		String emailContents = "Somebody, probably you, requested a new password for " + 
    		this.getRoot().getLabel() + ".\n";
    		emailContents += "The new password is: " + newPassword + "\n";
    		emailContents += "Note: we strongly recommend you reset your password after log-in!";
    		// TODO: make this mandatory (password that was sent is valid only once)

    		//assuming: 'encoded' p.w. (setting deObf = true)
    		this.getEmailService().email("Your new password request", emailContents, user.getEmailaddress(), true);
    		
    		this.getModel().getMessages().add(new ScreenMessage("Sending new password successful", true));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getModel().getMessages().add(new ScreenMessage("Sending new password failed", false));
		}
		finally
		{
			this.getApplicationController().getLogin().logout();
		}
	}

	public void ChgUser(Database db, Tuple request) throws NoSuchAlgorithmException, MolgenisUserException, DatabaseException, ParseException, IOException
	{
		this.getModel().setAction("ChgUser");

		MolgenisUserService userService = MolgenisUserService.getInstance(db);

		if (StringUtils.isNotEmpty(request.getString("oldpwd")) || StringUtils.isNotEmpty(request.getString("newpwd")) || StringUtils.isNotEmpty(request.getString("newpwd2")))
		{
			String oldPwd  = request.getString("oldpwd");
			String newPwd1 = request.getString("newpwd");
			String newPwd2 = request.getString("newpwd2");

			userService.checkPassword(this.getApplicationController().getLogin().getUserName(), oldPwd, newPwd1, newPwd2);
		}

		MolgenisUser user = userService.findById(this.getApplicationController().getLogin().getUserId());
		this.toMolgenisUser(request, user);
		userService.update(user);

		this.getModel().getMessages().add(new ScreenMessage("Changes successfully applied", true));
	}

	public void Forgot(Database db, Tuple request)
	{
		this.getModel().setAction("Forgot");
	}
	
	private MolgenisUser toMolgenisUser(Tuple request) throws MolgenisUserException
	{
		MolgenisUser user  = new MolgenisUser();

		if (!StringUtils.equals(request.getString("password"), request.getString("password2")))
			throw new MolgenisUserException("Passwords do not match.");

		user.setName(request.getString("username"));
		user.setPassword(request.getString("password"));
		user.setEmailaddress(request.getString("email"));
		user.setTitle(request.getString("title"));
		user.setLastname(request.getString("lastname"));
		user.setFirstname(request.getString("firstname"));
		user.setInstitute(request.getString("institute"));
		user.setDepartment(request.getString("department"));
		user.setPosition(request.getString("position"));
		user.setCity(request.getString("city"));
		user.setCountry(request.getString("country"));
		Calendar cal   = Calendar.getInstance();
		Date now       = cal.getTime();
		String actCode = Integer.toString(Math.abs(now.hashCode()));
		user.setActivationCode(actCode);
		user.setActive(false);
		
		return user;
	}

	private void toMolgenisUser(Tuple request, MolgenisUser user)
	{
		if (StringUtils.isNotEmpty(request.getString("newpwd")))
			user.setPassword(request.getString("newpwd"));
		if (StringUtils.isNotEmpty(request.getString("emailaddress")))
			user.setEmailaddress(request.getString("emailaddress"));
		if (StringUtils.isNotEmpty(request.getString("title")))
			user.setTitle(request.getString("title"));
		if (StringUtils.isNotEmpty(request.getString("lastname")))
			user.setLastname(request.getString("lastname"));
		if (StringUtils.isNotEmpty(request.getString("firstname")))
			user.setFirstname(request.getString("firstname"));
		if (StringUtils.isNotEmpty(request.getString("institute")))
			user.setInstitute(request.getString("institute"));
		if (StringUtils.isNotEmpty(request.getString("department")))
			user.setDepartment(request.getString("department"));
		if (StringUtils.isNotEmpty(request.getString("position")))
			user.setPosition(request.getString("position"));
		if (StringUtils.isNotEmpty(request.getString("city")))
			user.setCity(request.getString("city"));
		if (StringUtils.isNotEmpty(request.getString("country")))
			user.setCountry(request.getString("country"));
	}

	@Override
	public void reload(Database db)
	{
		this.populateAuthenticationForm();
		this.populateUserAreaForm(db);
		this.populateRegistrationForm();
		this.populateForgotForm();
	}

	private void populateAuthenticationForm()
	{
		if (this.getApplicationController().getLogin() instanceof OpenIdLogin)
		{
			Container form = new OpenIdAuthenticationForm();
			((ActionInput) form.get("google")).setJavaScriptAction("document.forms." + this.getName() + ".op.value='Google';document.forms." + this.getName() + ".submit();");
			((ActionInput) form.get("yahoo")).setJavaScriptAction("document.forms." + this.getName() + ".op.value='Yahoo';document.forms." + this.getName() + ".submit();");
			this.getModel().setAuthenticationForm(form);
		}
		else
		{
			this.getModel().setAuthenticationForm(new DatabaseAuthenticationForm());
		}
	}

	private void populateUserAreaForm(Database db)
	{
		try
		{
			MolgenisUserService userService = MolgenisUserService.getInstance(db);
			MolgenisUser user               = userService.findById(this.getApplicationController().getLogin().getUserId());
			
			UserAreaForm userAreaForm       = new UserAreaForm();
			((TablePanel) userAreaForm.get("personal")).get("emailaddress").setValue(user.getEmailaddress());
			((TablePanel) userAreaForm.get("personal")).get("title").setValue(user.getTitle());
			((TablePanel) userAreaForm.get("personal")).get("firstname").setValue(user.getFirstname());
			((TablePanel) userAreaForm.get("personal")).get("lastname").setValue(user.getLastname());
			((TablePanel) userAreaForm.get("personal")).get("institute").setValue(user.getInstitute());
			((TablePanel) userAreaForm.get("personal")).get("department").setValue(user.getDepartment());
			((TablePanel) userAreaForm.get("personal")).get("position").setValue(user.getPosition());
			((TablePanel) userAreaForm.get("personal")).get("city").setValue(user.getCity());
			((TablePanel) userAreaForm.get("personal")).get("country").setValue(user.getCountry());
			
			this.getModel().setUserAreaForm(userAreaForm);
		}
		catch (Exception e)
		{
			this.getModel().setUserAreaForm(new UserAreaForm());
		}
	}

	private void populateRegistrationForm()
	{
		this.getModel().setRegistrationForm(new RegistrationForm());
	}
	
	private void populateForgotForm()
	{
		this.getModel().setForgotForm(new ForgotForm());
	}
}