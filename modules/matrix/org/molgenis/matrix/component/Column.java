package org.molgenis.matrix.component;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

public class Column  {
    private Protocol protocol;
    private Measurement measurement;

    //TODO: remove enum and replace by datatype that is a MolgenisType
    @Deprecated
    public enum ColumnType {
        DATE("date"),
        TIMESTAMP("timestamp"),
        DATETIME("datetime"),
        INTEGER("int", false),
        DECIMAL("decimal", false),
        STRING("string"),
        CODE("code", false);
        
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
//    	measurement.g
        return getColumnType(measurement.getDataType());
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Measurement getMeasurement() {
        return measurement;
    }
    
    @Deprecated
    public static ColumnType getColumnType(String column) {
        String columnType = WordUtils.capitalize(column);
        
        if(columnType.startsWith("NUMMER") || columnType.startsWith("NUMBER")) {
        	final int decimalPrecision = Integer.parseInt(StringUtils.substringBetween(columnType, ",", ")"));
        	//final int precision = Integer.parseInt(StringUtils.substringBetween(columnType, "(", ","));
        	
        	if(decimalPrecision == 0) {
        		return ColumnType.INTEGER;
        	} else {
        		return ColumnType.DECIMAL;	
        	}
        } else if(columnType.startsWith("DATUM") || columnType.startsWith("DATE")) {
        	return ColumnType.DATE;
        } else if(columnType.startsWith("TEKST") || columnType.startsWith("TEXT")) {
        	return ColumnType.STRING;
        } else if(columnType.startsWith("CODE")) {
        	return ColumnType.CODE;
        }
        
        try {
            return ColumnType.valueOf(columnType);
        } catch (Exception ex) {
            if (columnType.equals("int")) {
                return ColumnType.INTEGER;
            }
        }
        return ColumnType.STRING;
    }
}
