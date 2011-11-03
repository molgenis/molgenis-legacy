package org.molgenis.matrix.component.general;

import org.molgenis.framework.db.QueryRule;

/**
 * There are convenience subclasses to make this easy.
 */
public class MatrixQueryRule extends QueryRule {

    public enum Type {

        /** a filter on row or col index. Synonymous to rowHeader(property=id) */
        rowIndex, colIndex,
        /** a filter on a field of the row/colheader */
        rowHeader, colHeader,
        /** a filter on row or column values. Synonymous to rowValueProperty(property=value) */
        rowValues, colValues,
        /** a filter on a property of a row or column value. E.g. protocolApplication */
        rowValueProperty, colValueProperty,
        /** a filter on values, globally */
        value, valueProperty
    }
    //the aspect of the matrix to be filtered.
    private Type filterType;
    //for xValue filters, the index of the dimension. E.g. colId
    private Integer dimIndex;
    
    private int protocolId;
    private int measurementId;

    /**
     * Special constructor for QueryRules in the context of Matrix. Allows more
     * combinations needed for twodimensional data filtering.
     */
    public MatrixQueryRule(Type type, String field, Operator operator, Object value) {
        this.filterType = type;

        this.setField(field);
        this.setOperator(operator);
        this.setValue(value);

        if (operator == Operator.LAST
                || operator == Operator.AND || operator == Operator.OR) {
            throw new IllegalArgumentException(this + ": Operator." + operator
                    + " cannot be used with two arguments");
        }

        if ((operator == Operator.SORTASC || operator == Operator.SORTDESC) && value == null) {
            this.setValue(field);
        }
    }

    /**
     * Field can be left out. In that case the rule will use the default field (depends on implementation).
     * For example: in case of 'index' there is no field. In case of 'ObservedValue' the default may be 'value'.
     * 
     * @param type
     * @param operator
     * @param value
     */
    public MatrixQueryRule(Type type, Operator operator, Object value) {
        this.setFilterType(type);
        this.setOperator(operator);
        this.setValue(value);
    }
    
    
    public MatrixQueryRule(Type type, int protocolId, int measurementId, Operator operator, Object value) {
        this.filterType = type;
        this.protocolId = protocolId;
        this.measurementId = measurementId;
        this.operator = operator;
        this.value = value;
    }

    public void setFilterType(Type filterType) {
        this.filterType = filterType;
    }

    public MatrixQueryRule(Type type, Integer colIndex,
            String colProperty, Operator operator, Object object) {
        this(type, colProperty, operator, object);
        this.dimIndex = colIndex;
    }

    public MatrixQueryRule(Type type, int protoclId, Integer colIndex,
            String colProperty, Operator operator, Object object) {
        this(type, colProperty, operator, object);
        this.dimIndex = colIndex;
        this.protocolId = protoclId;
    }

    public Type getFilterType() {
        return filterType;
    }

    public Integer getDimIndex() {
        return dimIndex;
    }

    public void setDimIndex(Integer dimIndex) {
        this.dimIndex = dimIndex;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public int getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(int measurementId) {
        this.measurementId = measurementId;
    }

    
    
    public String toString() {
        return "Filter type: " + this.getFilterType()
                + (this.getDimIndex() != null ? ", dimension index: " + this.getDimIndex() : "")
                + ", queryrule: " + super.toString();
    }
}
