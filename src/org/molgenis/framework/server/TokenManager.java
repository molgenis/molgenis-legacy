package org.molgenis.framework.server;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class TokenManager
{
	private HashMap<String, Token> securityTokens;
	
	public TokenManager()
	{
		this.securityTokens = new HashMap<String, Token>();
	}
	
	public void createToken(String userName, boolean refreshToken)
	{
		// no tokens for anonymous
		if(userName.equals("anonymous"))
		{
			return;
		}
		
		// if there is no token yet, create it
		if(!securityTokens.containsKey(userName))
		{
			securityTokens.put(userName, newToken());
		}
		// if there is a token, refresh if refreshToken == true
		else if(refreshToken)
		{
			securityTokens.get(userName).setExpiresAt(oneDayFromNow());
		}
	}
	
	public void invalidateTokens()
	{
		for(String usr : securityTokens.keySet())
		{
			Date expiresAt = securityTokens.get(usr).getExpiresAt();
			Date now = new Date();
			
			if(expiresAt.before(now))
			{
				securityTokens.remove(usr);
			}
		}
	}
	
	public void printTokens()
	{
		for(String usr : securityTokens.keySet())
		{
			String uuid = securityTokens.get(usr).getUuidTokenValue();
			String created = securityTokens.get(usr).getCreatedAt().toString();
			String expires = securityTokens.get(usr).getExpiresAt().toString();
			System.out.println("TOKEN: " + usr + " has " + uuid + " created at " + created + " valid until " + expires);
		}
	}
	
	private Token newToken()
	{
		String uuid = UUID.randomUUID().toString();
		Date now = new Date();
		return new Token(uuid, now, oneDayFromNow());
		//return new Token(uuid, now, thirtySecondsFromNow());
	}
	
	private Date oneDayFromNow()
	{
		Calendar cal = Calendar.getInstance();
		//Add one day to current date time for +24 hrs
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	//debug purposes
	private Date thirtySecondsFromNow()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 30);
		return cal.getTime();
	}
}
