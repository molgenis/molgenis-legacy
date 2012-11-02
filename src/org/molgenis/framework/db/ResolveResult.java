package org.molgenis.framework.db;

import java.util.List;

/**
 * Helper bean for resolveForeignKeys in generated CsvReader classes.
 * 
 * @author joeri
 * 
 * @param <E>
 */
public class ResolveResult<E>
{

	List<E> resolved;
	List<E> unresolved;

	public List<E> getResolved()
	{
		return resolved;
	}

	public void setResolved(List<E> resolved)
	{
		this.resolved = resolved;
	}

	public List<E> getUnresolved()
	{
		return unresolved;
	}

	public void setUnresolved(List<E> unresolved)
	{
		this.unresolved = unresolved;
	}

}
