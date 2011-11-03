package org.molgenis.matrix.component;

import lifelines.matrix.*;
import java.io.Serializable;

import org.apache.commons.lang.WordUtils;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

public class Column implements Serializable {
    private String name;
    private Object filter;
    private boolean invalidFilter = false;
    private ColumnType type;
    private boolean isDirty = true;
    private Protocol protocol;
    private Measurement measurement;

    public enum ColumnType {
        Date("date"),
        Timestamp("timestamp"),
        Datetime("datetime"),
        Integer("int", false),
        Decimal("decimal", false),
        String("string"),
        Code("code", false);
        
        private String name;
        private boolean quote = true;

        ColumnType(String name) {
            this.name = name;
        }
        
        ColumnType(String name, boolean  quote) {
            this.name = name;
            this.quote = quote;
        }

        public boolean isQuote() {
            return quote;
        }

        @Override
        public String toString() {
            return name;
        }
    };

    public Column(String name, ColumnType type, Protocol protocol, Measurement measurement, String filter) {
        this.name = name;
        this.protocol = protocol;
        this.measurement = measurement;
        this.filter = filter;
        this.type = type;
    }    
    
    public Column(String name, ColumnType type, String filter) {
        this.name = name;
        this.filter = filter;
        this.type = type;
    }

    public boolean isInvalidFilter() {
        return invalidFilter;
    }

    public void setInvalidFilter(boolean invalidFilter) {
        this.invalidFilter = invalidFilter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getFilter() {
        return filter;
    }

    public void setFilter(Object filter) {
        this.filter = filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public ColumnType getType() {
        return type;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public static ColumnType getColumnType(String columnType) {
        columnType = WordUtils.capitalize(columnType);
        try {
            return ColumnType.valueOf(columnType);
        } catch (Exception ex) {
            if (columnType.equals("int")) {
                return ColumnType.Integer;
            }
        }
        return ColumnType.String;
    }
}
