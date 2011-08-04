package org.molgenis.framework.db.jpa;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;

import org.molgenis.framework.db.CsvToDatabase.IntegerWrapper;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.FileSourceHelper;
import org.molgenis.framework.db.JoinQuery;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryImp;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.security.Login;
import org.molgenis.model.elements.Model;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.SpreadsheetWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Java Persistence API (JPA) implementation of Database to query relational
 * databases.
 * <p>
 * In order to function, {@link org.molgenis.assets.data.jdbc_old.JpaMapper}
 * must be added for each {@link org.molgenis.assets.data.Entity} E that can be
 * queried. These mappers take care of the interaction with a database.
 * 
 * @author Morris Swertz
 * @author Joris Lops
 */
public abstract class JpaDatabase extends AbstractDatabase implements Database {

	protected static class EMFactory {
		private static Map<String, EntityManagerFactory> emfs = new HashMap<String, EntityManagerFactory>(); 
		private static EMFactory instance = null;
		
		private EMFactory(String persistenceUnit) {
			addEntityManagerFactory(persistenceUnit);
		}
		
		private static void addEntityManagerFactory(String persistenceUnit) {
			if(!emfs.containsKey(persistenceUnit)) {
				emfs.put(persistenceUnit, Persistence.createEntityManagerFactory(persistenceUnit));
			}
		}
		
		public static EntityManager createEntityManager(String persistenceUnit) {
			if(instance == null) {
				instance = new EMFactory(persistenceUnit);
			}		
			if(!emfs.containsKey(persistenceUnit)) {
				addEntityManagerFactory(persistenceUnit);
			}			
			return emfs.get(persistenceUnit).createEntityManager();		
		}
		
		public static EntityManager createEntityManager() {
			if(instance == null) {
				instance = new EMFactory("molgenis");
			}		
			return emfs.get("molgenis").createEntityManager();		
		}		

        public static EntityManagerFactory getEntityManagerFactoryByName(String name) {
            return emfs.get(name);
        }
	}	
    /** BATCH SIZE */
    private int BATCH_SIZE = 10000;
    private static Map<String, Mapper<? extends Entity>> mappers = new TreeMap<String, Mapper<? extends Entity>>();
    private EntityManager em = null;
    /** in transaction */
    // private boolean inTransaction;
    /** login */
    private Login login;
    private Model model;
    private String persistenceUnitName;

    protected JpaDatabase(String persistenceUnitName, EntityManager em, Model jdbcMetaDatabase) {
        this.persistenceUnitName = persistenceUnitName;
        this.em = em;
        this.model = jdbcMetaDatabase;
    }

    protected JpaDatabase(String persistenceUnitName, Model jdbcMetaDatabase) {
        this.persistenceUnitName = persistenceUnitName;
        this.model = jdbcMetaDatabase;
    }
    
    protected JpaDatabase(String persistenceUnitName) {
    	this.persistenceUnitName = persistenceUnitName;
    	this.em = EMFactory.createEntityManager();
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public <E extends Entity> int add(E entity) throws DatabaseException {
        int count = -1;
        try {
            beginTransaction();
            List<Entity> entities = new ArrayList<Entity>();
            entities.add(entity);
            count = add(entities);
            commitTransaction();
        } catch (javax.persistence.PersistenceException e) {
            rollbackTx();
            throw new DatabaseException(e.getCause().getMessage());
        }
        return count;
    }
    static int i = 0;

    @Override
    public <E extends Entity> int add(List<E> entities)
            throws DatabaseException {
        int count = -1;
        try {
            beginTransaction();
            if (entities != null && entities.size() > 0) {
                ++i;
                count = getMapper(entities.get(0).getClass().getName()).add(
                        (List<Entity>) entities);
            }
            commitTransaction();
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }

        return count;
    }

    @Override
    public <E extends Entity> int add(final Class<E> klazz,
            final CsvReader reader, final SpreadsheetWriter writer) throws DatabaseException {
        // batch of entities
        final List<E> entityBatch = new ArrayList<E>();
        // counter
        final IntegerWrapper count = new IntegerWrapper(0);

        try {
            beginTransaction();
            reader.parse(new CsvReaderListener() {

                @Override
                public void handleLine(int lineNumber, Tuple tuple)
                        throws Exception {
                    E e = klazz.newInstance();
                    e.set(tuple);
                    entityBatch.add(e);

                    if (entityBatch.size() > BATCH_SIZE) {
                        for (E entity : entityBatch) {
                            em.persist(entity);
                        }

                        count.set(count.get() + 1);
                        em.flush();

                        for (E entity : entityBatch) {
                            writer.writeRow(entity);
                        }

                    }
                }
            });
            commitTransaction();
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }

        return count.get();
    }
    private int transactionCount = 0;

    @Override
    public void beginTx() throws DatabaseException {
        if (transactionCount == 0) {
            beginTransaction();
        }
        ++transactionCount;
//		++transactionCount;
//		if (!inTx())
//		{
//			beginTransaction();
//		}
    }

    @Override
    public void commitTx() throws DatabaseException {
        --transactionCount;
        if (transactionCount == 0) {
            commitTransaction();
        }

//		if(!localTransaction) {
//			commitTx();
//		} else if (inTx()) {
//			commitTx();
//		}
    }

    private void beginTransaction() throws DatabaseException {
        try {
            if (em.getTransaction() != null && !em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void close() throws DatabaseException {
        try {
            em.close();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    private void commitTransaction() throws DatabaseException {
        try {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public <E extends Entity> int count(Class<E> entityClass,
            QueryRule... rules) throws DatabaseException {
        TypedQuery<Long> query = JPAQueryGeneratorUtil.createCount(entityClass, (Mapper<E>) getMapper(entityClass.getName()), em, rules);
        Long result = query.getSingleResult();
        return result.intValue();
    }

    @Override
    public <E extends Entity> void find(Class<E> entityClass, SpreadsheetWriter writer,
            QueryRule... rules) throws DatabaseException {
        try {
            throw new NoSuchMethodException();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // List<E> result = JPAQueryGeneratorUtil.createWhere(entityClass,
        // getMapper(entityClass.getName()), em, rules).getResultList();
    }

    @Override
    public <E extends Entity> void find(Class<E> entityClass, SpreadsheetWriter writer,
            List<String> fieldsToExport, QueryRule... rules)
            throws DatabaseException {
        boolean first = true;
        int count = 0;
        for (Entity e : find(entityClass, rules)) {
            if (first) {
                writer.setHeaders(fieldsToExport);
                writer.writeHeader();
                first = false;
            }
            writer.writeRow(e);
            count++;
        }
        logger.debug(String.format("find(%s, writer) wrote %s lines",
                entityClass.getSimpleName(), count));
    }

    @Override
    public <E extends Entity> List<E> find(Class<E> entityClass,
            QueryRule... rules) throws DatabaseException {
        TypedQuery<E> query = JPAQueryGeneratorUtil.createQuery(entityClass, (Mapper<E>) getMapper(entityClass.getName()), em, rules);
        return query.getResultList();
    }

    @Override
    public <E extends Entity> E findById(Class<E> entityClass, Object id)
            throws DatabaseException {
        try {
            List<E> result = this.query(entityClass).eq(
                    entityClass.newInstance().getIdField(), id).find();
            if (result.size() == 1) {
                return result.get(0);
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return null;
    }

    @Override
    public <E extends Entity> List<E> findByExample(E example) {
        return JpaFrameworkFactory.createFramework().findByExample(em, example);
//		ReadObjectQuery query = new ReadObjectQuery();
//		query.setExampleObject(example);
//		return (List<E>)JpaHelper.getServerSession(em.getEntityManagerFactory()).executeQuery(query);
    }

    @Override
    public Class<? extends Entity> getClassForName(String simpleName) {
        for (Class<? extends Entity> c : getEntityClasses()) {
            if (c.getSimpleName().equalsIgnoreCase(simpleName)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Class<? extends Entity>> getEntityClasses() {
        List<Class<? extends Entity>> result = new ArrayList<Class<? extends Entity>>();
        for (EntityType t : em.getMetamodel().getEntities()) {
            result.add(t.getJavaType());
        }
        return result;
    }

    @Override
    public List<String> getEntityNames() {
        List<String> result = new ArrayList<String>();
        for (Class c : this.getEntityClasses()) {
            result.add(c.getName());
        }
        return result;
    }

    @Override
    public File getFilesource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Model getMetaData() throws DatabaseException {
        return model;
    }

    @Override
    public Login getSecurity() {
        return login;
    }

    @Override
    public boolean inTx() {
        if (transactionCount == 0) {
            return false;
        }
        return true;

//		if (em.getTransaction() == null) 
//                    return false;
//		
//        return em.getTransaction().isActive();
    }

    @Override
    public <E extends Entity> Query<E> query(Class<E> entityClass) {
        return new QueryImp<E>(this, entityClass);
    }

    @Override
    public JoinQuery join(Class<? extends Entity>[] classes) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Entity> int remove(E entity) throws DatabaseException {
        int count = -1;
        try {
            beginTransaction();
            List<Entity> entities = new ArrayList<Entity>();
            entities.add(entity);
            count = remove(entities);
            commitTransaction();
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }
        return count;
    }

    @Override
    public <E extends Entity> int remove(List<E> entities)
            throws DatabaseException {
        int count = -1;
        try {
            beginTransaction();
            count = getMapper(entities.get(0).getClass().getName()).remove(
                    (List<Entity>) entities);
            commitTransaction();
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }
        return count;
    }

    @Override
    public <E extends Entity> int remove(final Class<E> entityClass,
            final CsvReader reader) throws DatabaseException {
        // batch of entities
        final List<E> entityBatch = new ArrayList<E>();
        // counter
        final IntegerWrapper count = new IntegerWrapper(0);

        try {
            beginTransaction();
            reader.parse(new CsvReaderListener() {

                @Override
                public void handleLine(int lineNumber, Tuple tuple)
                        throws Exception {
                    E e = entityClass.newInstance();
                    e.set(tuple);
                    entityBatch.add(e);

                    if (entityBatch.size() > BATCH_SIZE) {
                        for (E entity : entityBatch) {
                            em.remove(e);
                        }

                        count.set(count.get() + 1);
                        em.flush();
                    }
                }
            });
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }
        return count.get();
    }

    @Override
    public void rollbackTx() throws DatabaseException {
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public void setLogin(Login login) {
        this.login = login;
    }

    @Override
    public <E extends Entity> List<E> toList(Class<E> klazz, CsvReader reader,
            int noEntities) throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Entity> int update(E entity) throws DatabaseException {
        int count = -1;
        try {
            beginTransaction();

            List<Entity> entities = new ArrayList<Entity>();
            entities.add(entity);
            count = update(entities);

            commitTransaction();
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }
        return count;
    }

    @Override
    public <E extends Entity> int update(List<E> entities)
            throws DatabaseException {
        try {
            beginTransaction();
            getMapper(entities.get(0).getClass().getName()).update(
                    (List<Entity>) entities);
            commitTransaction();
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }

        return 1;
    }

    @Override
    public <E extends Entity> int update(final Class<E> entityClass,
            final CsvReader reader) throws DatabaseException {
        // batch of entities
        final List<E> entityBatch = new ArrayList<E>();
        // counter
        final IntegerWrapper count = new IntegerWrapper(0);

        try {
            beginTransaction();
            reader.parse(new CsvReaderListener() {

                @Override
                public void handleLine(int lineNumber, Tuple tuple)
                        throws Exception {
                    E e = entityClass.newInstance();
                    e.set(tuple);
                    entityBatch.add(e);

                    if (entityBatch.size() > BATCH_SIZE) {
                        for (E entity : entityBatch) {
                            em.merge(entity);
                        }

                        count.set(count.get() + 1);
                        em.flush();
                    }
                }
            });
            commitTransaction();
        } catch (Exception e) {
            rollbackTx();
            throw new DatabaseException(e);
        }
        return count.get();

    }

    protected static <E extends Entity> void putMapper(Class<E> klazz,
            Mapper<E> mapper) {
        mappers.put(klazz.getName(), mapper);
        mappers.put(klazz.getSimpleName(), mapper);
        // logger.debug("added mapper for klazz " + klazz.getName());
    }

    public <E extends Entity> Mapper<E> getMapper(String name) {
        return (Mapper<E>) mappers.get(name);
    }

    public void flush() {
        em.flush();
    }

    public List executeSQLQuery(String sqlQuery) {
        return em.createNativeQuery(sqlQuery).getResultList();
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public FileSourceHelper getFileSourceHelper() throws Exception {
        throw new UnsupportedOperationException();
    }
   

	@Override
	public List<Tuple> sql(String query, QueryRule ...queryRules) throws DatabaseException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}



	@Override
	public ResultSet executeQuery(String query, QueryRule... queryRules)
			throws DatabaseException {
		throw new UnsupportedOperationException();
	}  
}
