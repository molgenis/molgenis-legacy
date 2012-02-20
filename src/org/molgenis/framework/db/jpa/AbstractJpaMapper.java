package org.molgenis.framework.db.jpa;


import java.io.File;
import java.text.ParseException;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.TupleWriter;

/**
 *
 * @author joris lops
 */
public abstract class AbstractJpaMapper<E extends Entity> implements Mapper<E> {
    @Override
    public abstract E create();    
    
    public abstract void create(E entity) throws DatabaseException;
    public abstract void edit(E entity) throws DatabaseException;
    public abstract void destroy(E entity) throws DatabaseException;


    @Override
    public abstract List<E> toList(CsvReader reader, int limit) throws DatabaseException;
    @Override
    public abstract String getTableFieldName(String field);
    @Override
    public abstract FieldType getFieldType(String field);
    @Override
    public abstract void resolveForeignKeys(List<E> enteties) throws ParseException, DatabaseException;
    @Override
    public abstract String createFindSqlInclRules(QueryRule[] rules) throws DatabaseException;
    //@Override
    public void prepareFileAttachements(List<E> entities, File filesource) 
    {
    
    }
    //@Override
    public  boolean saveFileAttachements(List<E> entities, File filesource) {
        return true;
    }   

    
    private static int BATCH_SIZE = 500;
    private final static Logger logger = Logger.getLogger(AbstractJpaMapper.class);    

    protected JpaDatabase database;
    protected EntityManager em;

//    protected AbstractJpaMapper(JpaDatabase database) {
//        this.database = database;
//        this.em = database.getEntityManager();
//    }    
    
    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public int add(List<E> entities) throws DatabaseException {
        int updatedRows = 0;
        final String TX_TICKET = "ADD_" + this.getClass().getSimpleName();
        try
        {
                // begin transaction for all batches
                database.beginPrivateTx(TX_TICKET);
                Session session = (Session) em.getDelegate();
                session.setFlushMode(FlushMode.MANUAL);
                

                // prepare all file attachments
                this.prepareFileAttachements(entities, database.getFilesource());
                this.saveFileAttachements(entities, database.getFilesource());

                // insert this class in batches
                for (int i = 0; i < entities.size(); i += BATCH_SIZE)
                {
                        int endindex = Math.min(i + BATCH_SIZE, entities.size());
                        List<E> sublist = entities.subList(i, endindex);
                        
                        this.resolveForeignKeys(entities);
                        for (E e : sublist) {
                            create(e);
                            ++updatedRows;
                        }
                        em.flush();
                        em.clear();
                }

                // commit all batches
                database.commitPrivateTx(TX_TICKET);

                logger.info(updatedRows + " " + this.create().getClass().getSimpleName() + " objects added");
                return updatedRows;
        }
        catch (Exception sqle)
        {
        	System.out.println(ExceptionUtils.getRootCause(sqle));
        	
                database.rollbackPrivateTx(TX_TICKET);
                logger.error("ADD failed on " + this.create().getClass().getSimpleName() + ": " + sqle.getMessage());
                throw new DatabaseException(sqle);
        }        
    }
    
    //Move-up
    @Override
    public int add(CsvReader reader, TupleWriter writer) throws DatabaseException {
        int rowsAffected = 0;
        final String TX_TICKET = "ADD+" + this.create().getClass().getCanonicalName() + "_CSV";
        try
        {
                database.beginPrivateTx(TX_TICKET);

                List<E> entities = toList(reader, BATCH_SIZE);

                if (writer != null)
                {
                        writer.setHeaders(entities.get(0).getFields());
                        writer.writeHeader();
                }

                while (entities.size() > 0)
                {
                        // resolve foreign keys
                        this.resolveForeignKeys(entities);

                        // add to the database
                        rowsAffected += database.add(entities);
                        if (writer != null)
                        {
                                for (E entity : entities)
                                {
                                        writer.writeRow(entity);
                                }
                        }
                        entities = toList(reader, BATCH_SIZE);
                }

                database.commitPrivateTx(TX_TICKET);
        }
        catch (Exception e)
        {
                database.rollbackPrivateTx(TX_TICKET);
                throw new DatabaseException(e);
        }
        return rowsAffected;
    }

    
    
    @Override
    public int update(List<E> entities) throws DatabaseException {
        int updatedRows = 0;
        final String TX_TICKET = "UPDATE" + this.getClass().getSimpleName();
        try
        {
                // start anonymous transaction for the batched update
                database.beginPrivateTx(TX_TICKET);

                // prepare file attachments
                this.prepareFileAttachements(entities, database.fileSource);


                // update in batches
                for (int i = 0; i < entities.size(); i += BATCH_SIZE)
                {
                        int endindex = Math.min(i + BATCH_SIZE, entities.size());
                        List<E> sublist = entities.subList(i, endindex);

                        // put the files in their place
                        this.saveFileAttachements(sublist, database.fileSource);

                        this.resolveForeignKeys(entities);
                        for (E e : sublist) {
                            edit(e);
                            ++updatedRows;
                        }
                        em.flush();
                }

                database.commitPrivateTx(TX_TICKET);

                logger.info(updatedRows + " " + this.create().getClass().getSimpleName() + " objects updated");
                return updatedRows;
        }
        catch (Exception sqle)
        {
            database.rollbackPrivateTx(TX_TICKET);
            logger.error("update failed on " + this.create().getClass().getSimpleName() + ": " + sqle.getMessage());
            throw new DatabaseException(sqle);
        }
    }

    //Move-Up
    @Override
    public int update(CsvReader reader) throws DatabaseException {
        int rowsAffected = 0;
        final String TX_TICKET = "ADD+" + this.create().getClass().getCanonicalName() + "_CSV";
        try
        {
                database.beginPrivateTx(TX_TICKET);
                List<E> entities = toList(reader, BATCH_SIZE);
                while (entities.size() > 0)
                {
                        // resolve foreign keys
                        this.resolveForeignKeys(entities);

                        // update to the database
                        rowsAffected += database.update(entities);
                        entities = toList(reader, BATCH_SIZE);
                }

                database.commitPrivateTx(TX_TICKET);
        }
        catch (Exception e)
        {
                database.rollbackPrivateTx(TX_TICKET);
                throw new DatabaseException(e);
        }
        return rowsAffected;
    }

    @Override
    public int remove(List<E> entities) throws DatabaseException {
        int updatedRows = 0;
        final String TX_TICKET = "REMOVE_" + this.getClass().getSimpleName();
        try
        {
                // start anonymous transaction for the batched remove
                database.beginPrivateTx(TX_TICKET);

                // remove in batches
                for (int i = 0; i < entities.size(); i += BATCH_SIZE)
                {
                        int endindex = Math.min(i + BATCH_SIZE, entities.size());
                        List<E> sublist = entities.subList(i, endindex);

                        // remove mrefs before the entity itself
                        this.resolveForeignKeys(entities);
                        for (E e : sublist) {
                            destroy(e);
                            ++updatedRows;
                        }
                        em.flush();
                }

                database.commitPrivateTx(TX_TICKET);

                logger.info(updatedRows + " " + this.create().getClass().getSimpleName() + " objects removed");
                return updatedRows;
        }
        catch (Exception sqle)
        {
            database.rollbackPrivateTx(TX_TICKET);
            logger.error("remove failed on " + this.create().getClass().getSimpleName() + ": " + sqle.getMessage());
            throw new DatabaseException(sqle);
        }
    }
}
