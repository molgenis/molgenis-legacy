package org.molgenis.framework.db.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.apache.commons.lang.StringUtils;

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

	int[] limitOffset = new int[2];
	Arrays.fill(limitOffset, -1);
	Predicate wherePredicate = createWhere(mapper, em, root, cq, cb, limitOffset, rules);
	if(wherePredicate != null) {
		cq.where(wherePredicate);
	}
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

    private static <E extends Entity> Predicate createWhere(Mapper<E> mapper, EntityManager em,
	    Root<E> root, CriteriaQuery cq, CriteriaBuilder cb, int[] limitOffset,
	    QueryRule... rul) throws DatabaseException {

    	List<QueryRule> rules = Arrays.asList(rul);
    	
    	Predicate whereClause = null;
		List<Order> orders = new ArrayList<Order>();
	
		QueryRule prevRule = null;
		forLoop:		
		for (int i = 0; i < rules.size(); ++i) {			
		    QueryRule rule = rules.get(i);
		    if (mapper != null) {
		    
	    	Operator operator = rule.getOperator();
		    if(operator == Operator.SORTASC || operator == Operator.SORTDESC) {
		    	rule.setField(rule.getValue().toString());
		    }
		    	
			String attributeName = rule.getField();
            if(!StringUtils.isEmpty(attributeName)) {
                attributeName = attributeName.substring(0,1).toLowerCase() + attributeName.substring(1);
            }
			
			Predicate predicate = null;
	                          
			switch (operator) {
			    case LAST:
				throw new UnsupportedOperationException(
					"Not supported yet.");
			    case SORTASC:
			    	orders.add(cb.asc(root.get(attributeName)));
				break;
			    case SORTDESC:
			    	orders.add(cb.desc(root.get(attributeName)));
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
						if (root.get(attributeName).getJavaType().getName().equals("java.util.List") || root.get(attributeName).getJavaType().newInstance() instanceof Entity) {
						    Entity entity = root.getJavaType().newInstance();
						    String xrefAttribtename = entity.getXrefIdFieldName(attributeName);
						    
						    Join join = root.join(attributeName, JoinType.LEFT);
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
					createWhere(mapper, em, root, cq, cb, new int[2], nestedrules);
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
							List<QueryRule> restOfQueryRules = rules.subList(i, rules.size());
							Predicate rightsPred = createWhere(mapper, em, root, cq, cb, limitOffset, restOfQueryRules.toArray(new QueryRule[1]));
							if(rightsPred != null) {
								whereClause = cb.or(whereClause, rightsPred);
							}
						    break forLoop;
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
//		if (whereClause != null) {
//		    cq.where(whereClause);
//		}
		return whereClause;
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