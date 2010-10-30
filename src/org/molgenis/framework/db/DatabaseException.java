package org.molgenis.framework.db;

/** A small class defining a database exception. 
 * 
 * @author ?
 *
 */
public class DatabaseException extends Exception {
	String message = "";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseException(String message) {
		super(message);
		this.message = message;
	}
	
	public DatabaseException(Exception exception) {
		super(exception);
		message = exception.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
