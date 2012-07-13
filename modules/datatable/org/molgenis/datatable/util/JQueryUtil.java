package org.molgenis.datatable.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.molgenis.model.elements.Field;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

public class JQueryUtil {

    public static String getDynaTreeNodes(List<Field> columns) {
        // K = tableName, V = Field
        final ImmutableListMultimap<String, Field> fieldsByTable = Multimaps.index(columns,
                new Function<Field, String>() {

                    @Override
                    public String apply(Field field) {
                        if (StringUtils.isEmpty(field.getTableName())) {
                            if (StringUtils.contains(field.getName(), ".")) {
                                return StringUtils.substringBefore(field.getName(), ".");
                            } else {
                                return "Other";
                            }
                        }
                        return field.getTableName();
                    }
                });

        final Map<String, String> rs = new LinkedHashMap<String, String>();
        for (final String tableName : fieldsByTable.keys()) {
            final StringBuilder tableNode = new StringBuilder();
            final ImmutableList<Field> fieldByTable = fieldsByTable.get(tableName);
            tableNode.append("{");
            tableNode.append(String.format("\"title\" : \"%s\", ", tableName));
            if (CollectionUtils.isNotEmpty(fieldByTable)) {
                tableNode.append("\"isFolder\": \"true\",");
                tableNode.append(String.format("\"children\" : [%s]",
                        StringUtils.join(CollectionUtils.collect(fieldByTable, new Transformer() {

                    @Override
                    public Object transform(Object arg0) {
                        final Field f = (Field) arg0;
                        return String.format("{\"title\" : \"%s\", \"path\" : \"%s\"}", f.getName(), f.getSqlName());
                    }
                }), ",")));
            }
            tableNode.append("}");
            rs.put(tableName, tableNode.toString());
        }
        return String.format("[%s]", StringUtils.join(rs.values(), ","));
    }
}
