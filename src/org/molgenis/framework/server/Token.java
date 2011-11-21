package org.molgenis.framework.server;

import java.util.Date;

public class Token
{
	private String uuidTokenValue;
	private Date expiresAt;
	private Date createdAt;
	
	public Token(String uuidTokenValue, Date createdAt, Date expiresAt)
	{
		super();
		this.uuidTokenValue = uuidTokenValue;
		this.expiresAt = expiresAt;
		this.createdAt = createdAt;
	}

	public void setUuidTokenValue(String uuidTokenValue)
	{
		this.uuidTokenValue = uuidTokenValue;
	}

	public void setExpiresAt(Date expiresAt)
	{
		this.expiresAt = expiresAt;
	}

	public void setCreatedAt(Date createdAt)
	{
		this.createdAt = createdAt;
	}

	public String getUuidTokenValue()
	{
		return uuidTokenValue;
	}

	public Date getExpiresAt()
	{
		return expiresAt;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}
	
}
