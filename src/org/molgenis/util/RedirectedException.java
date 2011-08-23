package org.molgenis.util;

import java.lang.reflect.InvocationTargetException;

public class RedirectedException extends Exception{
	public RedirectedException(InvocationTargetException e)	{
		super(e);
	}

	public RedirectedException()
	{
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

}
