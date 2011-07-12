package org.molgenis.matrix;

/**
 * Exception thrown by Matrix
 */
public class MatrixException extends Exception
{
	private static final long serialVersionUID = 1L;

	public MatrixException(String string)
	{
		super(string);
	}

	public MatrixException(Exception e)
	{
		super(e);
	}

}
