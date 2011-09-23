package org.molgenis.util;

import java.lang.reflect.InvocationTargetException;

public class HandleRequestDelegationException extends Exception{
	public HandleRequestDelegationException(InvocationTargetException e)	{
		super(e);
	}

	public HandleRequestDelegationException()
	{
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

}
