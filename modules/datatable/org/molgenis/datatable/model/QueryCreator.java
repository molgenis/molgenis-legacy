package org.molgenis.datatable.model;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.model.elements.Field;

import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.expr.SimpleExpression;

public interface QueryCreator
{

	SQLQueryImpl createQuery(Connection connection, SQLTemplates dialect);

	List<String> getHiddenFieldNames();

	List<Field> getFields();

	LinkedHashMap<String, SimpleExpression<? extends Object>> getAttributeExpressions();

}
