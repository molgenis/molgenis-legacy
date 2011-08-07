package org.molgenis.framework.db.jpa;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.molgenis.fieldtypes.FieldType;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.util.Entity;

/**
 * @author Morris Swertz
 * @author Joris Lops
 */
public interface JpaMapper<E extends Entity> extends Mapper<E>
{
	/** Create a new instance */
	public E create();
	
	public List<E> findAll();
	public List<E> find(String jpaQlWhereClause, Integer limit, Integer offset);
	public int count(String jpaQlWhereClause);
//        public int count(EntityManager em, QueryRule... rules);

	/**
	 * helper method create a new instance of E
	 */
	//public E create(Tuple t);

	/**
	 * maps {@link org.molgenis.framework.Database#add(List)}
	 * @throws DatabaseException 
	 */
	public int add(List<E> entities) throws DatabaseException;

	/**
	 * maps {@link org.molgenis.framework.Database#update(List)}
	 */
	public int update(List<E> entities) throws DatabaseException;

	/**
	 * maps {@link org.molgenis.framework.Database#remove(List)}
	 */
	public int remove(List<E> entities) throws DatabaseException;

	/**
	 * helper method to prepares file for saving.
	 * 
	 * @throws IOException
	 */
	public void prepareFileAttachements(List<E> entities, File dir) throws IOException;

	/**
	 * helper method to do some actions after the transaction. For example:
	 * write files to disk. FIXME make a listener?
	 * 
	 * @return true if files were saved (will cause additional update to the
	 *         database)
	 * @throws IOException
	 */
	public boolean saveFileAttachements(List<E> entities, File dir) throws IOException;
	
	public FieldType getFieldType(String field);
	
	public String getTableFieldName(String field);
	

}
