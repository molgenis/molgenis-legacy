package org.molgenis.mutation.service;

public class SearchServiceException extends RuntimeException
{
	// The serial version UID of this class. Needed for serialization.
	private static final long serialVersionUID = -6216398955010588895L;

	/**
	 * The default constructor for <code>SearchException</code>.
	 */
	public SearchServiceException()
	{
		// Documented empty block
	}

	/**
	 * Constructs a new instance of <code>SearchException</code>.
	 *
	 * @param message the throwable message.
	 */
	public SearchServiceException(String message)
	{
		super(message);
	}
}
