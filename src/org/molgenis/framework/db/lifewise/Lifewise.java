package org.molgenis.framework.db.lifewise;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.JoinQuery;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryImp;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jdbc.JDBCMapper;
import org.molgenis.framework.security.Login;
import org.molgenis.model.elements.Model;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;

public class Lifewise implements Database {

    private static final transient Logger logger = Logger.getLogger(Lifewise.class.getSimpleName());
    @SuppressWarnings("rawtypes")
    private Map<String, JDBCMapper> mappers = new TreeMap<String, JDBCMapper>();

    protected <E extends Entity> void putMapper(Class<E> klazz, JDBCMapper<E> mapper) {
        this.mappers.put(klazz.getName(), mapper);
    }

    private <E extends Entity> JDBCMapper<E> getMapperFor(List<E> entities) throws DatabaseException {
        try {
            Class klazz = entities.get(0).getClass();
            return getMapperFor(klazz);
        } catch (NullPointerException e) {
            // transform to generic exception
            logger.error("trying to store empty list");
            throw new DatabaseException("getMapperFor failed because of empty list");
        }
    }

    private <E extends Entity> JDBCMapper<E> getMapperFor(Class<E> klazz) throws DatabaseException {
        // transform to generic exception
        @SuppressWarnings("unchecked")
        JDBCMapper<E> mapper = mappers.get(klazz.getName());
        if (mapper == null) {
            throw new DatabaseException("getMapperFor failed because no mapper available for "
                    + klazz.getName());
        }
        return mapper;
    }

    @Override
    public Model getMetaData() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beginTx() throws DatabaseException {
    }

    @Override
    public void commitTx() throws DatabaseException {
    }

    @Override
    public void rollbackTx() throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean inTx() {
        return true;
    }
    private XmlRpcClientConfigImpl config;
    private XmlRpcClient client = null;

    //"http://localhost:5454/"
    public Lifewise(String url) {
        try {
            config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(url));
            client = new XmlRpcClient();
            client.setConfig(config);
        } catch (Exception ex) {
            logger.error("Lifewise failed");
        }
    }

    public static String marshal(Object objectToMarshall) throws Exception {
        Class[] classes = null;
        if(objectToMarshall.getClass().equals(SendList.class)) {
            classes = new Class[] {SendList.class, ((SendList)objectToMarshall).getList().get(0).getClass()};
        } else {
            classes = new Class[] {objectToMarshall.getClass()}; 
        }

        JAXBContext jaxbCtx1 = javax.xml.bind.JAXBContext.newInstance(classes);

        Marshaller m = jaxbCtx1.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        m.marshal(objectToMarshall, sw);
        return sw.toString();
    }

    public static Object unMarshal(String xmlObjects, Class[] klass) throws Exception {
        JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(klass);
        Unmarshaller um1 = jaxbCtx.createUnmarshaller();
        StringReader sr = new StringReader(xmlObjects);
        Object objs = um1.unmarshal(sr);
        return objs;
    }

    public static Object unMarshal(String xmlObjects, Class klass) throws Exception {
        return unMarshal(xmlObjects, new Class[]{klass});
    }

    private <E extends Entity> List<E> executeClientListOperation(Class<E> klazz, String operation, List<QueryRule> qr) {
        try {
            String xmlResult = null;
            if(qr != null && qr.size() > 0) {
                String xmlParam = marshal(new SendList<QueryRule>(qr));
                xmlResult = (String) client.execute(operation, new Object[]{xmlParam});
            } else {
                xmlResult = (String) client.execute(operation, new Object[]{});
            }

            @SuppressWarnings("unchecked")
            Class[] classes = new Class[]{SendList.class, klazz};
            Object o = unMarshal(xmlResult, classes);
            o.toString();

            SendList<E> result = (SendList<E>) unMarshal(xmlResult, new Class[]{SendList.class, klazz});
            return result.getList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("int executeClientOperation(String operation, List<E> entities)");
        }
        return null;
    }

    private <E extends Entity> int executeClientOperation(String operation, List<E> entities) {
        return executeClientOperation(operation, entities, true);
    }

    private <E extends Entity> int executeClientOperation(String operation, List<E> entities, boolean updateEntities) {
        try {
            if (entities.size() > 0) {
                SendList<E> sendList = new SendList<E>(entities);
                String xmlMessage = marshal(sendList);

                String xmlResult = (String) client.execute(operation, new Object[]{xmlMessage});
                @SuppressWarnings("unchecked")
                SendList<E> result = (SendList<E>) unMarshal(xmlResult, sendList.getClass());
                if (updateEntities) {
                    int idx = 0;
                    for (E e : entities) {
                        result.getList().set(idx++, e);
                    }
                }
                return result.getList().size();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("int executeClientOperation(String operation, List<E> entities)");
        }
        return -1;
    }

    @Override
    public <E extends Entity> int count(Class<E> entityClass, QueryRule... rules) throws DatabaseException {
        try {
            String xmlRules = null;
            if (rules.length > 0) {
                List<QueryRule> rs = new ArrayList<QueryRule>();
                rs.addAll(Arrays.asList(rules));
                xmlRules = marshal(new SendList<QueryRule>(rs));
            }

            List<Object> params = new ArrayList<Object>();
            if (xmlRules != null) {
                params.add(xmlRules);
            }

            String xmlResult = (String) client.execute("QueryHandler.count", params);
            QueryResult result = ((QueryResult)unMarshal(xmlResult, QueryResult.class));
            return result.getCount();
        } catch (Exception ex) {
            logger.error("count(Class<E> entityClass, QueryRule... rules) throws DatabaseException failed" + ex.toString());
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public <E extends Entity> void find(Class<E> entityClass, CsvWriter writer, QueryRule... rules)
            throws DatabaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Entity> List<E> findByExample(E example) throws DatabaseException {
        try {
            Query<E> q = (Query<E>) this.query(example.getClass());
            for (String field : example.getFields()) {
                if (example.get(field) != null) {
                    if (example.get(field) instanceof List) {
                        if (((List) example.get(field)).size() > 0) {
                            q.in(field, (List) example.get(field));
                        }
                    } else {
                        q.equals(field, example.get(field));
                    }
                }
            }

            return q.find();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Entity> E findById(Class<E> entityClass, Object id) throws DatabaseException {
        try {
            String idField = mappers.get(entityClass.getSimpleName()).create().getIdField();
            QueryRule qr = new QueryRule(idField, Operator.EQUALS, id);

            String xmlQueryRule = marshal(qr);
            String xmlResult = (String) client.execute("QueryHandler.findById", new Object[]{xmlQueryRule});
            return (E) unMarshal(xmlResult, entityClass);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("E findById(Class<E> entityClass, Object id) throws DatabaseException failed");
            return null;
        }
    }

    @Override
    public <E extends Entity> Query<E> query(Class<E> entityClass) {
        return new QueryImp<E>(this, entityClass);
    }

    @Override
    public JoinQuery query(List<String> fields) throws DatabaseException {
        return new JoinQuery(this, fields);
    }

    @Override
    public <E extends Entity> List<E> find(Class<E> klazz, QueryRule... rules) throws DatabaseException {
        List<QueryRule> pRules = new ArrayList<QueryRule>();
        pRules.addAll(Arrays.asList(rules));
        return executeClientListOperation(klazz, "QueryHandler.find", pRules);
    }

    @Override
    public <E extends Entity> int add(E entity) throws DatabaseException, IOException {
        List<E> entityList = new ArrayList<E>();
        entityList.add(entity);
        return this.add(entityList);
    }

    @Override
    public <E extends Entity> int add(List<E> entities) throws DatabaseException, IOException {
        return executeClientOperation("QueryHandler.add", entities);
    }

    @Override
    public <E extends Entity> int add(Class<E> klazz, CsvReader reader, CsvWriter writer) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <E extends Entity> int update(E entity) throws DatabaseException, IOException {
        List<E> entityList = new ArrayList<E>();
        entityList.add(entity);
        return this.update(entityList);
    }

    @Override
    public <E extends Entity> int update(List<E> entities) throws DatabaseException, IOException {
        return executeClientOperation("QueryHandler.add", entities);
    }

    @Override
    public <E extends Entity> int update(Class<E> klazz, CsvReader reader) throws DatabaseException, IOException,
            Exception {
        return getMapperFor(klazz).update(reader);
    }

    @Override
    public <E extends Entity> int remove(E entity) throws DatabaseException, IOException {
        List<E> entityList = new ArrayList<E>();
        entityList.add(entity);
        return this.remove(entityList);
    }

    @Override
    public <E extends Entity> int remove(List<E> entities) throws DatabaseException, IOException {
        return executeClientOperation("QueryHandler.remove", entities, false);
    }

    @Override
    public <E extends Entity> int remove(Class<E> entityClass, CsvReader reader) throws DatabaseException, IOException,
            Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <E extends Entity> int update(List<E> entities, DatabaseAction dbAction, String... keyName)
            throws DatabaseException, ParseException, IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public File getFilesource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() throws DatabaseException {
        // TODO Auto-generated method stub
    }

    @Override
    public List<String> getEntityNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E extends Entity> List<E> toList(Class<E> klazz, CsvReader reader, int noEntities) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Class> getEntityClasses() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Login getSecurity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLogin(Login login) {
        // TODO Auto-generated method stub
    }

    @Override
    public Class<Entity> getClassForName(String simpleName) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public <E extends Entity> void find(Class<E> entityClass, CsvWriter writer, List<String> fieldsToExport,
			QueryRule... rules) throws DatabaseException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public <E extends Entity> Query<E> queryByExample(E entity)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
