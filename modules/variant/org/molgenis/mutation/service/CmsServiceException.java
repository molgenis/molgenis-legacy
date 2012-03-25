package org.molgenis.mutation.service;

public class CmsServiceException extends RuntimeException
{
	// The serial version UID of this class. Needed for serialization.
	private static final long serialVersionUID = 6115195811983678029L;

	/**
	 * The default constructor for <code>SearchException</code>.
	 */
	public CmsServiceException()
	{
		// Documented empty block
	}

	/**
	 * Constructs a new instance of <code>SearchException</code>.
	 *
	 * @param message the throwable message.
	 */
	public CmsServiceException(String message)
	{
		super(message);
	}
}
