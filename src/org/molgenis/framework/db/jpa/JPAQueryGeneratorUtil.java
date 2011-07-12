package org.molgenis.framework.db.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.logging.LogFactory;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.AbstractEntity;
import org.molgenis.util.Entity;

/**
 * @author joris lops
 */
public class JPAQueryGeneratorUtil {

    public static <IN extends Entity> TypedQuery<IN> createQuery(
	    Class<IN> inputClass, Mapper<IN> mapper, EntityManager em,
	    QueryRule... rules) throws DatabaseException {
	return createQuery(inputClass, inputClass, mapper, em, rules);
    }

    public static <IN extends Entity, OUT> TypedQuery<OUT> createQuery(
	    Class<IN> inputClass, Class<OUT> outputClass, Mapper<IN> mapper,
	    EntityManager em, QueryRule... rules) throws DatabaseException {
	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<OUT> cq = cb.createQuery(outputClass);
	Root<IN> root = cq.from(inputClass);

	if (inputClass.getSimpleName().equals(outputClass.getSimpleName())) {
	    cq.select((Selection<? extends OUT>) root);
	} else {
	    cq.select((Selection<? extends OUT>) cb.count(root));
	}

	int[] limitOffset = createWhere(mapper, em, root, cq, cb, rules);

	TypedQuery<OUT> query = em.createQuery(cq);
	if (limitOffset[0] != -1) {
	    query.setMaxResults(limitOffset[0]);
	}
	if (limitOffset[1] != -1) {
	    query.setFirstResult(limitOffset[1]);
	}
	return query;
    }    
    
    public static <IN extends Entity> TypedQuery<Long> createCount(
	    Class<IN> inputClass, Mapper<IN> mapper, EntityManager em,
	    QueryRule... rules) throws DatabaseException {
	return createQuery(inputClass, Long.class, mapper, em, rules);
    }

    private static <E extends Entity> int[] createWhere(Mapper<E> mapper, EntityManager em,
	    Root<E> root, CriteriaQuery cq, CriteriaBuilder cb,
	    QueryRule... rules) throws DatabaseException {

	int[] limitOffset = new int[]{-1, -1};

	Predicate whereClause = null;
	List<Order> orders = new ArrayList<Order>();

	QueryRule prevRule = null;
	for (QueryRule r : rules) {
	    QueryRule rule = new QueryRule(r);
	    if (mapper != null) {
		String attributeName = rule.getField();

		Operator operator = rule.getOperator();
		Predicate predicate = null;
                          
		switch (operator) {
		    case LAST:
			throw new UnsupportedOperationException(
				"Not supported yet.");
		    case SORTASC:
		    	rule.setValue(mapper.getTableFieldName(rule.getValue().toString()));
			orders.add(cb.asc(root.get((String) rule.getValue())));
			break;
		    case SORTDESC:
		    	rule.setValue(mapper.getTableFieldName(rule.getValue().toString()));
			orders.add(cb.desc(root.get((String) rule.getValue())));
			break;
		    case LIMIT:
			limitOffset[0] = (Integer) rule.getValue();
			break;
		    case OFFSET:
			limitOffset[1] = (Integer) rule.getValue();
			break;
		    default:
			switch (operator) {
			    case EQUALS:

				if (rule.getValue() instanceof Entity) {
				    try {
					predicate = cb.equal(root.get(attributeName),
						rule.getValue());
				    } catch (Exception ex) {
					LogFactory.getLog(
						JPAQueryGeneratorUtil.class.getName()).error(ex);
				    }
				} else {
				    try {
					// it's a xref attribute which is joined to root
					if (root.get(attributeName).getJavaType().newInstance() instanceof Entity) {
					    Entity entity = root.getJavaType().newInstance();
					    String xrefAttribtename = entity.getXrefIdFieldName(attributeName);
					    
					    Join join = root.join(attributeName);
					    Expression attribute = join.get(xrefAttribtename);
					    Object value = rule.getValue();
					    predicate = cb.equal(attribute, value);
					} else { //normal attribute
					    predicate = cb.equal(
						    root.get(attributeName),
						    rule.getValue());
					}
				    } catch (InstantiationException ex) {
					// this is a hack, newInstance can not be called on inmutable object
					// like Integer
					predicate = cb.equal(root.get(attributeName),
						rule.getValue());
				    } catch (IllegalAccessException ex) {
					LogFactory.getLog(
						JPAQueryGeneratorUtil.class.getName()).error(ex);
					throw new DatabaseException(ex);
				    }
				}
				break;
			    case NOT:
				predicate = cb.notEqual(root.get(attributeName),
					rule.getValue());
				break;
			    case LIKE:
				predicate = cb.like(
					root.get(attributeName).as(String.class),
					(String) rule.getValue());
				break;
			    case LESS:
				predicate = cb.lessThan(
					root.get(attributeName).as(String.class),
					(Comparable) rule.getValue());
				break;
			    case GREATER:
				predicate = cb.greaterThan(
					root.get(attributeName).as(String.class),
					(Comparable) rule.getValue());
				break;
			    case LESS_EQUAL:
				predicate = cb.lessThanOrEqualTo(root.get(attributeName).as(String.class),
					(Comparable) rule.getValue());
				break;
			    case GREATER_EQUAL:
				predicate = cb.greaterThanOrEqualTo(
					root.get(attributeName).as(String.class),
					(Comparable) rule.getValue());
				break;
			    case NESTED:
				QueryRule[] nestedrules = rule.getNestedRules();
				createWhere(mapper, em, root, cq, cb, nestedrules);
				break;
			    case IN: // not a query but a list for example SELECT * FROM
				// x WHERE x.a1 IN (v1, v2, v3)
				Object[] values = new Object[0];
				if (rule.getValue() instanceof List) {
				    values = ((List<Object>) rule.getValue()).toArray();
				} else {
				    values = (Object[]) rule.getValue();
				}

				Class attrClass = root.get(attributeName).getJavaType();
				//pseudo code: if(Attribute instanceof AbstractEntity)
				if (AbstractEntity.class.isAssignableFrom(root.get(attributeName).getJavaType())) {
				    Field idField = getIdField(attrClass);
				    predicate = root.get(attributeName).get(idField.getName()).in(values);
				} else {
				    predicate = root.get(attributeName).in(values);
				}

				break;
			}
			// make a where clause from the predicate
			if (whereClause != null) {
			    if (predicate != null) {
				if (prevRule != null && prevRule.getOperator().equals(Operator.OR)) {
				    whereClause = cb.or(whereClause, predicate);
				} else {
				    whereClause = cb.and(whereClause, predicate);
				}
			    }
			} else {
			    whereClause = predicate;
			}
			break;
		}
	    }
	    prevRule = rule;
	}
	if (orders.size() > 0) {
	    cq.orderBy(orders);
	}
	if (whereClause != null) {
	    cq.where(whereClause);
	}
	return limitOffset;
    }

    private static Field getIdField(Class<Entity> entity) {
	for (Field f : entity.getDeclaredFields()) {
	    Annotation annotation = f.getAnnotation(javax.persistence.Id.class);
	    if (annotation != null) {
		return f;
	    }
	}
	return null;
    }
}