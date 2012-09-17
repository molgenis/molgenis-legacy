package org.molgenis.mutation.service;

public class StatisticsServiceException extends RuntimeException
{
	// The serial version UID of this class. Needed for serialization.
	private static final long serialVersionUID = -6216398955010588895L;

	/**
	 * The default constructor for <code>SearchException</code>.
	 */
	public StatisticsServiceException()
	{
		// Documented empty block
	}

	/**
	 * Constructs a new instance of <code>SearchException</code>.
	 *
	 * @param message the throwable message.
	 */
	public StatisticsServiceException(String message)
	{
		super(message);
	}
}
