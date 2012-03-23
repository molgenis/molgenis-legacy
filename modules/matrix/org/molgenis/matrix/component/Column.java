package org.molgenis.matrix.component;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

public class Column  {
    private Protocol protocol;
    private Measurement measurement;

    //TODO: remove enum and replace by datatype that is a MolgenisType
    @Deprecated
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
        
        if(columnType.startsWith("NUMMER")) {
        	final int decimalPrecision = Integer.parseInt(StringUtils.substringBetween(columnType, ",", ")"));
        	//final int precision = Integer.parseInt(StringUtils.substringBetween(columnType, "(", ","));
        	
        	if(decimalPrecision == 0) {
        		return ColumnType.Integer;
        	} else {
        		return ColumnType.Decimal;	
        	}
        } else if(columnType.startsWith("DATUM") || columnType.startsWith("DATE")) {
        	return ColumnType.Date;
        } else if(columnType.startsWith("TEKST")) {
        	return ColumnType.String;
        }
        
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
