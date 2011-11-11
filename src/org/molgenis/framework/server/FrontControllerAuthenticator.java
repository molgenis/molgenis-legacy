package org.molgenis.framework.server;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.Login;

public class FrontControllerAuthenticator
{
	public boolean login(MolgenisRequest request, String username, String password)
			throws Exception
	{
		//try to login
		Database db = request.getDatabase();
		boolean loggedIn = db.getSecurity().login(db, username, password);
		
		if(loggedIn)
		{
			//TODO: Missing redirect???
			//Login login = new org.molgenis.auth.DatabaseLogin(request.getDatabase(), "ClusterDemo");
			
			//store login in session
			Login login = db.getSecurity();
			request.getRequest().getSession().setAttribute("login", login);
			return true;
		}
		else
		{
			return false;
		}
	}

	public void logout(MolgenisRequest request, MolgenisResponse response)
			throws Exception
	{
		// remove login fron session
		HttpSession session = request.getRequest().getSession();
		session.setAttribute("login", null);

		// get current db login and invalidate the session
		// if user is not logged in, and login is required
		Login userLogin = null;
		userLogin = request.getDatabase().getSecurity();
		
		if ((!userLogin.isAuthenticated() && userLogin.isLoginRequired()))
		{
			response.getResponse().setHeader("WWW-Authenticate",
					"BASIC realm=\"MOLGENIS\"");
			response.getResponse().sendError(
					HttpServletResponse.SC_UNAUTHORIZED);
			session.invalidate();
			return;
		}

		// logout from db
		userLogin.logout(request.getDatabase());
	}

}
