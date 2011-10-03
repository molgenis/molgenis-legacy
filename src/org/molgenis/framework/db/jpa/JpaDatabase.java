package org.molgenis.framework.db.jpa;

import java.sql.Connection;
import org.molgenis.framework.db.AbstractDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;

import org.molgenis.framework.db.CsvToDatabase.IntegerWrapper;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.JoinQuery;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryImp;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Model;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Entity;
import org.molgenis.util.TupleWriter;
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
public class JpaDatabase extends AbstractDatabase implements Database {

    protected static class EMFactory {

        private static Map<String, EntityManagerFactory> emfs = new HashMap<String, EntityManagerFactory>();
        private static EMFactory instance = null;

        private EMFactory(String persistenceUnit) {
            addEntityManagerFactory(persistenceUnit);
        }

        private static void addEntityManagerFactory(String persistenceUnit) {
            if (!emfs.containsKey(persistenceUnit)) {
                emfs.put(persistenceUnit, Persistence.createEntityManagerFactory(persistenceUnit));
            }
        }

        public static EntityManager createEntityManager(String persistenceUnit) {
            if (instance == null) {
                instance = new EMFactory(persistenceUnit);
            }
            if (!emfs.containsKey(persistenceUnit)) {
                addEntityManagerFactory(persistenceUnit);
            }
            EntityManager result = emfs.get(persistenceUnit).createEntityManager();
            return result;
        }

        public static EntityManager createEntityManager() {
            if (instance == null) {
                instance = new EMFactory("molgenis");
            }
            EntityManager result = emfs.get("molgenis").createEntityManager();
            return result;
        }

        public static EntityManagerFactory getEntityManagerFactoryByName(String name) {
            return emfs.get(name);
        }
    }
    private EntityManager em = null;
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

    public JpaDatabase(EntityManager em, Model model) {
        this.em = em;
        this.model = model;
    }

    public JpaDatabase(Model model) {
        this.model = model;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    private int transactionCount = 0;

    @Override
    public void beginTx() throws DatabaseException {
        if (transactionCount == 0) {
            beginTransaction();
        }
        ++transactionCount;
    }

    /*
     * Jpa doesn't support Nested Transactions
     */
    @Override
    public void beginPrivateTx(String ticket) throws DatabaseException {
        beginTx();
    }

    @Override
    public void commitTx() throws DatabaseException {
        --transactionCount;
        if (transactionCount == 0) {
            commitTransaction();
        }
    }

    /*
     * Jpa doesn't support Nested Transactions
     */
    @Override
    public void commitPrivateTx(String ticket) throws DatabaseException {
        commitTx();
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
    public void rollbackPrivateTx(String ticket) throws DatabaseException {
        try {
            em.getTransaction().rollback();
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
        TypedQuery<Long> query = JPAQueryGeneratorUtil.createCount(entityClass, (Mapper<E>) getMapperFor(entityClass), em, rules);
        Long result = query.getSingleResult();
        return result.intValue();
    }

    @Override
    public <E extends Entity> E findById(Class<E> klazz, Object id)
            throws DatabaseException {
        return em.find(klazz, id);
    }

    @Override
    public <E extends Entity> void find(Class<E> entityClass, TupleWriter writer,
            List<String> fieldsToExport, QueryRule... rules)
            throws DatabaseException {
        boolean first = true;
        int count = 0;
        for (Entity e : find(entityClass, rules)) {
            if (first) {
                writer.setHeaders(fieldsToExport);
                try {
                    writer.writeHeader();
                } catch (Exception e1) {
                    throw new DatabaseException(e1);
                }
                first = false;
            }
            try {
                writer.writeRow(e);
            } catch (Exception e1) {
                throw new DatabaseException(e1);
            }
            count++;
        }
        logger.debug(String.format("find(%s, writer) wrote %s lines", entityClass.getSimpleName(), count));
    }

    @Override
    public <E extends Entity> List<E> find(Class<E> entityClass,
            QueryRule... rules) throws DatabaseException {
        TypedQuery<E> query = JPAQueryGeneratorUtil.createQuery(entityClass, (Mapper<E>) getMapperFor(entityClass), em, rules);
        return query.getResultList();
    }

    @Override
    public <E extends Entity> List<E> findByExample(E example) {
        return JpaFrameworkFactory.createFramework().findByExample(em, example);
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
    public boolean inTx() {
        if (transactionCount == 0) {
            return false;
        }
        return true;
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
    @Deprecated
    public Connection getConnection() throws DatabaseException {
        return JpaFrameworkFactory.createFramework().getConnection(em);
    }
}
