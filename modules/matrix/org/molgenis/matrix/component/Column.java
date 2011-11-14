package org.molgenis.matrix.component;

import java.io.Serializable;

import org.apache.commons.lang.WordUtils;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

public class Column implements Serializable {
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

    public Column(Protocol protocol, Measurement measurement) {
        this.protocol = protocol;
        this.measurement = measurement;
    } 

    public String getName() {
        return measurement.getName();
    }

    public ColumnType getType() {
        return getColumnType(measurement.getDataType());
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Measurement getMeasurement() {
        return measurement;
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
