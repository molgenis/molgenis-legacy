package org.molgenis.auth;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdException;
import org.expressme.openid.OpenIdManager;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

/**
 * OpenIdLogin is a DatabaseLogin that authenticates via OpenId
 * while authorization is done via the database
 * @author robert wagner
 *
 */
public class OpenIdLogin extends DatabaseLogin
{

    private static final long serialVersionUID = 1L;
    static final long ONE_HOUR     = 3600000L;
    static final long TWO_HOUR     = ONE_HOUR * 2L;
    static final String ATTR_MAC   = "openid_mac";
    static final String ATTR_ALIAS = "openid_alias";

    protected final transient Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public OpenIdLogin() {
	logger.debug("OpenIdLogin()");
    }

    public OpenIdLogin(Database db)
    {
	this.login(db, "anonymous", "anonymous");
    }

    /**
     * 
     * @param db
     * @param request
     * @param response
     * @param returnURL
     * @param op
     * @throws IOException
     * @throws DatabaseException
     * @throws ParseException
     */
    public void authenticate(Database db, HttpServletRequest request, HttpServletResponse response, String returnURL, String op) throws IOException, DatabaseException, ParseException
    {
	OpenIdManager manager = new OpenIdManager();

	if ("authenticated".equals(op))
	{
	    // check nonce:
	    checkNonce(request.getParameter("openid.response_nonce"));
	    // get authentication:
	    byte[] mac_key                = (byte[]) request.getSession().getAttribute(ATTR_MAC);
	    String alias                  = (String) request.getSession().getAttribute(ATTR_ALIAS);
	    Authentication authentication = manager.getAuthentication(request, mac_key, alias);

	    this.user                     = this.getMolgenisUser(db, authentication);
	    this.reload(db);
	}
	else if ("Google".equals(op) || "Yahoo".equals(op))
	{
	    //    		manager.setRealm("http://localhost:8080");
	    manager.setReturnTo(returnURL);

	    // redirect to Google||Yahoo sign on page:
	    Endpoint endpoint       = manager.lookupEndpoint(op);
	    Association association = manager.lookupAssociation(endpoint);
	    request.getSession().setAttribute(ATTR_MAC, association.getRawMacKey());
	    request.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
	    String url              = manager.getAuthenticationUrl(endpoint, association);
	    response.sendRedirect(url);
	}
	else
	{
	    throw new IOException("Bad parameter op=" + op);
	}
    }

    /**
     * 
     * @param nonce
     */
    private void checkNonce(String nonce) {
	// check response_nonce to prevent replay-attack:
	if (nonce==null || nonce.length()<20)
	    throw new OpenIdException("Verify failed.");
	long nonceTime = getNonceTime(nonce);
	long diff = System.currentTimeMillis() - nonceTime;
	if (diff < 0)
	    diff = (-diff);
	if (diff > ONE_HOUR)
	    throw new OpenIdException("Bad nonce time.");
	if (isNonceExist(nonce))
	    throw new OpenIdException("Verify nonce failed.");
	storeNonce(nonce, nonceTime + TWO_HOUR);
    }

    private boolean isNonceExist(String nonce) {
	// TODO: check if nonce is exist in database:
	return false;
    }

    private void storeNonce(String nonce, long expires) {
	// TODO: store nonce in database:
    }

    private long getNonceTime(String nonce) {
	try {
	    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
	    .parse(nonce.substring(0, 19) + "+0000")
	    .getTime();
	}
	catch(ParseException e) {
	    throw new OpenIdException("Bad nonce time.");
	}
    }

    //	public void setUser(MolgenisUser molgenisUser) {
    //		this.user = molgenisUser;
    //		
    //	}

    /**
     * 
     */
    private MolgenisUser getMolgenisUser(Database db, Authentication authentication) throws DatabaseException, ParseException, IOException
    {
	// check if already in database
	List<MolgenisUser> users = db.query(MolgenisUser.class).eq(MolgenisUser.NAME, authentication.getEmail()).find();

	if (users.size() == 0)
	{
	    MolgenisUser user = new MolgenisUser();
	    user.setName(authentication.getEmail());
	    user.setFirstname(authentication.getFirstname());
	    user.setLastname(authentication.getLastname());
	    user.setEmailaddress(authentication.getEmail());
	    user.setPassword(UUID.randomUUID().toString());

	    db.beginTx();
	    db.add(user);
	    db.commitTx();

	    return user;
	}
	else
	{
	    return users.get(0); // safe because name is unique
	}
    }
}
