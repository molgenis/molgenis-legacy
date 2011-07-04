package org.molgenis.matrix.browser;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.paging.DatabasePager;
import org.molgenis.matrix.MatrixException;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.protocol.ProtocolApplication;

/**
 * Enabling browsing on top of matrix like data.
 * 
 * Challenges:
 * <ul>
 * <li>There can be multiple ObservedValue per Feature,Target pair so an
 * additional grouping mechanism is needed
 * <li>User can choose new values by [Feature], [ProtocolApplication,Feature],
 * [Target] or [ProtocolApplication,Target].
 * <li>We will try to merge 'Data' concept with 'ProtocolApplication' concept.
 * <li>Users may want to sort or filter on row/column meta data (e.g. on
 * Individual.name).
 * <li>The data sets are huge so we cannot hold all data in memory
 * <li>Users may want to filter/sort on observedvalues (how to do that if we
 * have repeating observed values???)
 * </ul>
 * 
 * Current draft inspired by DatabasePager and 'old' MatrixBrower
 */
public abstract class DatabaseMatrixBrowser<R extends ObservationElement, C extends ObservationElement>
{
	private DatabasePager<R> rowPager;
	private DatabasePager<C> columnPager;
	private DatabasePager<ProtocolApplication> applicationPager;

	public void rowFirst() throws MatrixException
	{
		
	}

	public void rowPrev() throws MatrixException
	{
		
	}

	public void rowNext() throws MatrixException
	{
		
	}

	public void rowLast() throws MatrixException
	{
		
	}

	public int getRowLimit()
	{
		return -1;
	}

	/**
	 * Changes the limit, that is, the number of entities to be retrieved in one
	 * page. If necessary the offset will be updated to make sure that each page
	 * is of 'limit' size (except the last).
	 * 
	 * @throws DatabaseException
	 */
	public void setRowLimit( int limit ) throws MatrixException
	{
		
	}

	/**
	 * Retrieve the current offset, that is, the index of the first entity to be
	 * retrieved in the current page.
	 * 
	 * @return current offset.
	 */
	public abstract int getOffset();

	/**
	 * Update the offset to match index.
	 * <p>
	 * If necessary the offset will be rounded down to ensure that it is a
	 * multiplication of limit, that is, offset % limit == 0.
	 * 
	 * @param index
	 */
	public abstract void setOffset( int index );

	/**
	 * Retrieve the name of the field that the pages are currently ordered by. 
	 * @return current order by field name.
	 */
	public abstract String getOrderByField();

	/**
	 * Set the field to order the page by. If changed, the offset will be re-set to first().
	 * 
	 * @param orderByField name
	 * @throws DatabaseException 
	 */
	public abstract void setOrderByField( String orderByField ) throws DatabaseException;

	/**
	 * Retrieve current order-by operator, either {@link org.molgenis.framework.db.QueryRule.Operator#SORTASC} or {@link org.molgenis.framework.db.QueryRule.Operator#SORTDESC}
	 * 
	 * @return Operator
	 */
	public abstract Operator getOrderByOperator();

	/**
	 * Set the order-by operator, , either {@link org.molgenis.framework.db.QueryRule.Operator#SORTASC} or {@link org.molgenis.framework.db.QueryRule.Operator#SORTDESC}. If changed, the offset will be re-set to
	 * first() as all pages are re-ordered.
	 * 
	 * @param orderByOperator
	 * @throws DatabaseException
	 */
	public abstract void setOrderByOperator( Operator orderByOperator ) throws DatabaseException;

	/**
	 * Add a filter to the filter list. If not yet in, the offset will be re-st
	 * to first();
	 * 
	 * @param rule
	 * @throws DatabaseException
	 */
	public abstract void addFilter( QueryRule rule ) throws DatabaseException;

	/**
	 * Retrieve the current list of filters as array. The index of this filters can be used to remove
	 *         specific filters. @see #removeFilter(int)
	 * @return current filters. 
	 */
	public abstract QueryRule[] getFilters();

	/**
	 * Remove a specific filter by index.
	 * 
	 * @param index of the filter to be removed.
	 * @throws DatabaseException
	 */
	public abstract void removeFilter( int index ) throws DatabaseException;

	/**
	 * Reset the orderByField and orderByOperator to default as passed during
	 * construction of this DatabasePager.
	 * 
	 * @throws DatabaseException
	 */
	public abstract void resetOrderBy() throws DatabaseException;

	/**
	 * Reset the filters to default as passed during
	 * construction of this DatabasePager (effectively removing all user defined
	 * filters).
	 */
	public abstract void resetFilters();

	/**
	 * Retrieve the current number of entities in the database, after filtering.
	 * @return current count of entities in the Database.
	 * @throws DatabaseException 
	 */
	public abstract int getCount(Database db) throws DatabaseException;

	/**
	 * Retrieve the current page as based on offset and limit, that is, entity[offset | offset >= 0 && offset < limit] until entity[offset+limit || count]
	 * @return current page of entities with length getLimit().
	 * @throws DatabaseException 
	 */
	//public abstract List<E> getPage(Database db) throws DatabaseException;

	/**
	 * Force reload.
	 * @param dirty
	 */
	//void setDirty(boolean dirty);
	
	
	
}
