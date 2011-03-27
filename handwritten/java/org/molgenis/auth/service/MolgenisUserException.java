package org.molgenis.auth.service;

public class MolgenisUserException extends Exception
{
	String message = "";

	public MolgenisUserException(String message)
	{
		super(message);
		this.message = message;
	}

	public MolgenisUserException(Exception exception)
	{
		super(exception);
		this.message = exception.getMessage();
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}
}
