package org.molgenis.framework.server;

import java.io.IOException;
import java.io.PrintWriter;

import org.molgenis.framework.server.FrontControllerAuthenticator.LoginStatus;
import org.molgenis.framework.server.FrontControllerAuthenticator.LogoutStatus;

/**
 * Simple authentication GUI service for browser-accessed MolgenisServices.
 * If you want to add login/logout and all security related checks in your
 * MolgenisService, be sure to use the handleAuthentication(MolgenisRequest
 * req, MolgenisResponse res) function below. Read its Javadoc for details.
 * Remember that login is shared across MolgenisServices when accessed using
 * the same session (i.e. don't close your browser), so when logged in via
 * one service, you are also logged in for the others.
 *
 */
public class MolgenisServiceAuthenticationHelper
{
	
	public static String LOGIN_USER_NAME = "usr";
	public static String LOGIN_PASSWORD = "pwd";
	static String LOGOUT_REQUEST = "logout";

	/**
	 * Handles authentication in combination with a simple menu structure
	 * which guides and informs the user about his/her authentication status.<br><br>
	 * 
	 * You should reuse this in every MolgenisService that uses authentication
	 * and is accessed through the browser, except the regular web GUI.<br><br>
	 * 
	 * At the top of the handleRequest() you are implementing, put:<br><br>
	 * 
	 * if(MolgenisServiceAuthenticationHelper.handleAuthentication(req, res) == false)<br>
	 *	{<br>
	 *		return;<br>
	 *	}<br><br>
	 *
	 * This will handle login and logout requests, and display a login box if the
	 * user is not authenticated. The important bit is the <i>return</i>: this prevents
	 * your service from executing any further when the user is not authenticated.
	 * HandleAuthentication() expects the string variables LOGIN_USER_NAME and LOGIN_PASSWORD in a login
	 * request, and the LOGOUT_REQUEST variable plus value for a logout request.
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	public static boolean handleAuthentication(MolgenisRequest req, PrintWriter out) throws IOException
	{

		
		//login request
		if(req.getString(LOGIN_USER_NAME) != null && req.getString(LOGIN_PASSWORD) != null )
		{
			String username = req.getString(LOGIN_USER_NAME);
			String password = req.getString(LOGIN_PASSWORD);
			
			System.out.println("going to log in with " + username + " / " + password);
			
			LoginStatus login = FrontControllerAuthenticator.login(req, username, password);	
			
			
			if(login == LoginStatus.ALREADY_LOGGED_IN)
			{
				// reach this by using the 'back' button of the browser and click Login again :)
				out.println("<table><tr><td colspan=\"2\">You are already logged in.</td></tr>");
				out.println("<tr><td>" + displayLogoutForm() + "</td>");
				out.println("<td><form><input type=\"submit\" value=\"Continue\"></form></td></tr></table>");
				return false;
			}
			else if(login == LoginStatus.SUCCESSFULLY_LOGGED_IN)
			{
				out.println("<table><tr><td>Welcome, " + username + "!</td></tr>");
				out.println("<tr><td align=\"right\"><form><input type=\"submit\" value=\"Continue\"></form></td></tr></table>");
				return false;
			}
			else if(login == LoginStatus.AUTHENTICATION_FAILURE)
			{
				out.println("<table><tr><td>User or password unknown.</td></tr>");
				out.println("<tr><td align=\"right\"><form><input type=\"submit\" value=\"Retry\"></form></td></tr></table>");
				return false;
			}
			else if(login == LoginStatus.EXCEPTION_THROWN)
			{
				out.println("An error occurred. Contact your administrator.");
				return false;
			}
			else
			{
				throw new IOException("Unknown login status: " + login);
			}
		}
		
		
		// logout request
		if(req.getString(LOGOUT_REQUEST) != null && req.getString(LOGOUT_REQUEST).equals(LOGOUT_REQUEST) )
		{

			LogoutStatus logout = FrontControllerAuthenticator.logout(req);

			if(logout == LogoutStatus.ALREADY_LOGGED_OUT)
			{
				// reach this by using the 'back' button of the browser and click Logout again :)
				out.println("<table><tr><td>You already logged out.</td></tr>");
				out.println("<tr><td align=\"right\"><form><input type=\"submit\" value=\"Continue\"></form></td></tr></table>");
				return false;
			}
			else if(logout == LogoutStatus.SUCCESSFULLY_LOGGED_OUT)
			{
				out.println("<table><tr><td>You are successfully logged out.</td></tr>");
				out.println("<tr><td align=\"right\"><form><input type=\"submit\" value=\"Continue\"></form></td></tr></table>");
				return false;
				
			}
			else if(logout == LogoutStatus.EXCEPTION_THROWN)
			{
				out.println("An error occurred. Contact your administrator.");
				return false;
			}
			else
			{
				throw new IOException("Unknown logout status: " + logout);
			}
		}
		
		// regular request: check if user is authenticated, and if not, display login box
		if(!req.getDatabase().getSecurity().isAuthenticated())
		{
			out.println("<form name=\"input\" action=\"\" method=\"post\">");
			out.println("<table><tr><td colspan=\"2\">Please log in:</td></tr>");
			out.println("<tr><td>Username:</td><td><input type=\"text\" name=\"" + LOGIN_USER_NAME + "\"></td></tr>");
			out.println("<tr><td>Password:</td><td><input type=\"text\" name=\"" + LOGIN_PASSWORD + "\"></td></tr>");
			out.println("<tr><td colspan=\"2\" align=\"right\"><input type=\"submit\" value=\"Login\"></td></tr></table>");
			out.println("</form>");
			return true;
		}
		
		return true;
	}
	
	/**
	 * Display a simple logout button. Reusable in services.
	 */
	public static String displayLogoutForm()
	{
		String logoutForm = "<form name=\"input\" action=\"\" method=\"post\">";
		logoutForm += "<input type=\"hidden\" name=\"" + LOGOUT_REQUEST + "\" value=\"" + LOGOUT_REQUEST + "\">";
		logoutForm += "<input type=\"submit\" value=\"Logout\">";
		logoutForm += "</form>";
		return logoutForm;
	}
	
}
