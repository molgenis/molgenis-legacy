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
import org.molgenis.auth.vo.UserLoginVO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.security.SimpleLogin;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

/**
 * This screen shows a login box, or if someone is already logged in, the user
 * information and a logout button.
 */
public class UserLogin extends PluginModel<Entity>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3084964114182861171L;
	private String action           = "init";
	private MolgenisUserService userService;
	private String mailCurator;
	private UserLoginVO userLoginVO = new UserLoginVO();

	public UserLogin(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_auth_ui_UserLogin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/auth/ui/UserLogin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			// reset messages
			this.setMessages();

			this.action = request.getAction();

			if ("Login".equals(this.action))
			{
				this.handleLoginRequest(db, request);
			}
			else if ("Logout".equals(this.action))
			{
				this.getLogin().logout();
				this.getLogin().reload(db);
			}
			else if ("Register".equals(this.action))
			{
				//nop
			}
			else if ("AddUser".equals(this.action))
			{
				this.handleAddRequest(db, request);
			}
			else if ("ChgUser".equals(this.action))
			{
				this.handleChangeUserRequest(db, request);
			}
			else if ("Forgot".equals(this.action))
			{
				//nop
			}
			else if ("Activate".equals(this.action))
			{
				this.handleActivateRequest(db, request);
			}
			else if ("sendPassword".equals(this.action))
			{
				this.handleForgotPasswordRequest(db, request);
			}
		}
		catch (Exception e)
		{
			this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			// If adding user failed, go back to Register screen:
			e.printStackTrace();
			if ("AddUser".equals(this.action)) {
				this.action = "Register";
			}
		}
	}

	public UserLoginVO getUserLoginVO()
	{
		return this.userLoginVO;
	}

	private void handleLoginRequest(Database db, Tuple request) throws DatabaseException
	{
		if ("Google".equals(request.getString("op")) || "Yahoo".equalsIgnoreCase(request.getString("op")) || "authenticated".equals(request.getString("op")))
		{
			try
			{
				// get the http request that is encapsulated inside the tuple
				HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
				HttpServletRequest httpRequest   = rt.getRequest();

				// get the http response that is used in this handleRequest
				HttpServletResponse httpResponse = rt.getResponse();

				String returnURL = httpRequest.getRequestURL() + "?__target=" + this.getScreen().getName() + "&__action=" + request.getAction() + "&op=authenticated";

				if (!(getLogin() instanceof OpenIdLogin))
					throw new Exception("Wrong parameter.");

				((OpenIdLogin) getLogin()).authenticate(db, httpRequest, httpResponse, returnURL, request.getString("op"));

//				this.getDatabaseUser(db);

				// login.authRequest(request.getString("name"));
				// login.verifyResponse();
				
				getLogin().reload(db);
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

			boolean loggedIn = getLogin().login(db, username, password);

			if (loggedIn)
				this.getRootScreen().setLogin(getLogin());
			else
				throw new DatabaseException("Login failed: username or password unknown");
		} else {
			throw new DatabaseException("Login failed: username or password empty");
		}
	}

	private void handleAddRequest(Database db, Tuple request) throws DatabaseException
	{
		try
		{
			if (this.getLogin().isAuthenticated())
			{
				 // if logged in, log out first
				this.getLogin().logout();
			}
			
			// login as admin
			// (a bit evil but less so than giving anonymous write-rights on the
			// MolgenisUser table)
			this.getLogin().login(db, "admin", "admin");
			this.getLogin().reload(db);
			
			MolgenisUserService userService = MolgenisUserService.getInstance(db);
			MolgenisUser user               = this.toMolgenisUser(request);
			userService.insert(user);

			// get the http request that is encapsulated inside the tuple
			HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
			HttpServletRequest httpRequest   = rt.getRequest();

			// Email the user
			String activationURL =
				httpRequest.getRequestURL().toString() +
				"?__target=" + this.getScreen().getName() +
				"&select=" + this.getScreen().getName() +
				"&__action=Activate&actCode=" + user.getActivationCode();
			String emailContents = "Somebody, probably you, requested a user account for " + this.getRootScreen().getLabel() + ".\n";
			emailContents += "Please visit the following URL in order to activate your account:\n";
			emailContents += activationURL + "\n\n";
			emailContents += "If you have not requested an account please ignore this mail.";
			
			//assuming: 'encoded' p.w. (setting deObf = true)
			this.getEmailService().email("Your registration request", emailContents, user.getEmailaddress(), true);
			
			this.getMessages().add(new ScreenMessage("Adding user successful - check your e-mail for activation instructions", true));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DatabaseException("Adding user failed: " + e.getMessage());
		} finally {
		    this.getLogin().logout();
		}
	}

	private void handleActivateRequest(Database db, Tuple request)
	{
	    try
		{
		
    		if (this.getLogin().isAuthenticated())
    		{
    			 // if logged in, log out first
    			this.getLogin().logout();
    		}
    		
    		// login as admin
    		// (a bit evil but less so than giving anonymous write-rights on the
    		// MolgenisUser table)
    		this.getLogin().login(db, "admin", "admin");
    		this.getLogin().reload(db);

			MolgenisUserSearchCriteriaVO criteria = new MolgenisUserSearchCriteriaVO();
			criteria.setActivationCode(request.getString("actCode"));

			MolgenisUserService userService       = MolgenisUserService.getInstance(db);
			List<MolgenisUser> users              = userService.find(criteria);

			if (users.size() != 1)
				throw new MolgenisUserException("No user found for activation code.");
			
			MolgenisUser user                     = users.get(0);
			user.setActive(true);
			userService.update(user);

			this.getMessages().add(new ScreenMessage("Activation successful", true));

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
			this.getMessages().add(new ScreenMessage("Activation failed", false));
		} finally {
		    this.getLogin().logout();
		}
	}
	
	private void handleForgotPasswordRequest(Database db, Tuple request)
	{
	    try
		{
    		if (this.getLogin().isAuthenticated())
    		{
    			 // if logged in, log out first
    			this.getLogin().logout();
    		}
    		
    		// login as admin
    		// (a bit evil but less so than giving anonymous write-rights on the
    		// MolgenisUser table)
    		this.getLogin().login(db, "admin", "admin");
    		this.getLogin().reload(db);

    		MolgenisUserSearchCriteriaVO criteria = new MolgenisUserSearchCriteriaVO();
    		criteria.setName(request.getString("username"));

    		MolgenisUserService userService = MolgenisUserService.getInstance(db);
    		List<MolgenisUser> users = userService.find(criteria);
		
    		if (users.size() != 1) {
    			throw new MolgenisUserException("No user found with this username.");
    		}
    		
    		MolgenisUser user = users.get(0);
    		//TODO: Danny: Use or loose
    		//TODO: Danny Is this a bug ??, we ask the user.email but don't use it to send the email ?
    		/*String email = */user.getEmailaddress();
  
    		String newPassword = UUID.randomUUID().toString().substring(0, 8);
    		user.setPassword(newPassword);
    		this.userService.update(user);

    		String emailContents = "Somebody, probably you, requested a new password for " + 
    		this.getRootScreen().getLabel() + ".\n";
    		emailContents += "The new password is: " + newPassword + "\n";
    		emailContents += "Note: we strongly recommend you reset your password after log-in!";
    		// TODO: make this mandatory (password that was sent is valid only once)

    		//assuming: 'encoded' p.w. (setting deObf = true)
    		this.getEmailService().email("Your new password request", emailContents, user.getEmailaddress(), true);
    		
    		this.getMessages().add(new ScreenMessage("Sending new password successful", true));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage("Sending new password failed", false));
		} finally {
		    this.getLogin().logout();
		}
	}

	private void handleChangeUserRequest(Database db, Tuple request) throws NoSuchAlgorithmException, MolgenisUserException, DatabaseException, ParseException, IOException
	{
		MolgenisUserService userService = MolgenisUserService.getInstance(db);

		if (StringUtils.isNotEmpty(request.getString("oldpwd")) || StringUtils.isNotEmpty(request.getString("newpwd")) || StringUtils.isNotEmpty(request.getString("newpwd2")))
		{
			String oldPwd  = request.getString("oldpwd");
			String newPwd1 = request.getString("newpwd");
			String newPwd2 = request.getString("newpwd2");

			userService.checkPassword(this.getLogin().getUserName(), oldPwd, newPwd1, newPwd2);
		}

		MolgenisUser user = this.userService.findById(this.getLogin().getUserId());
		this.toMolgenisUser(request, user);
		this.userService.update(user);

		this.getMessages().add(new ScreenMessage("Changes successfully applied", true));
	}

	private MolgenisUser toMolgenisUser(Tuple request) throws MolgenisUserException, NoSuchAlgorithmException
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
		this.userService = MolgenisUserService.getInstance(db);
		this.populateAuthenticationForm();
		this.populateUserAreaForm();
		this.populateRegistrationForm();
		this.populateForgotForm();
	}

	private void populateAuthenticationForm()
	{
		if (getLogin() instanceof OpenIdLogin)
		{
			Container form = new OpenIdAuthenticationForm();
			((ActionInput) form.get("google")).setJavaScriptAction("document.forms." + this.getScreen().getName() + ".op.value='Google';document.forms." + this.getScreen().getName() + ".submit();");
			((ActionInput) form.get("yahoo")).setJavaScriptAction("document.forms." + this.getScreen().getName() + ".op.value='Yahoo';document.forms." + this.getScreen().getName() + ".submit();");
			this.userLoginVO.setAuthenticationForm(form);
		}
		else
		{
			this.userLoginVO.setAuthenticationForm(new DatabaseAuthenticationForm());
		}
	}

	private void populateUserAreaForm()
	{
		try
		{
			MolgenisUser user         = this.userService.findById(this.getLogin().getUserId());
			
			UserAreaForm userAreaForm = new UserAreaForm();
			((TablePanel) userAreaForm.get("personal")).get("emailaddress").setValue(user.getEmailaddress());
			((TablePanel) userAreaForm.get("personal")).get("title").setValue(user.getTitle());
			((TablePanel) userAreaForm.get("personal")).get("firstname").setValue(user.getFirstname());
			((TablePanel) userAreaForm.get("personal")).get("lastname").setValue(user.getLastname());
			((TablePanel) userAreaForm.get("personal")).get("institute").setValue(user.getInstitute());
			((TablePanel) userAreaForm.get("personal")).get("department").setValue(user.getDepartment());
			((TablePanel) userAreaForm.get("personal")).get("position").setValue(user.getPosition());
			((TablePanel) userAreaForm.get("personal")).get("city").setValue(user.getCity());
			((TablePanel) userAreaForm.get("personal")).get("country").setValue(user.getCountry());
			
			this.userLoginVO.setUserAreaForm(userAreaForm);
		}
		catch (Exception e)
		{
			//TODO: What to do here?
		}
	}

	private void populateRegistrationForm()
	{
		this.userLoginVO.setRegistrationForm(new RegistrationForm());
	}
	
	private void populateForgotForm()
	{
		this.userLoginVO.setForgotForm(new ForgotForm());
	}

	public String getAction()
	{
		return this.action;
	}

	@Override
	public boolean isVisible()
	{
		if (getLogin() instanceof SimpleLogin)
			return false;

		return true;
	}

	@Override
	public String getLabel()
	{
		if (!getLogin().isAuthenticated())
		{
			return "Login";
		}
		return super.getLabel();
	}

	public void setMailCurator(String mailCurator) {
		this.mailCurator = mailCurator;
	}

	public String getMailCurator() {
		return mailCurator;
	}
}