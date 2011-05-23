package lifelines.matrix;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.WordUtils;
import org.richfaces.model.Ordering;

public class Column implements Serializable {
    private String name;
    private Object filter;
    private boolean invalidFilter = false;
    private Ordering ordering = Ordering.UNSORTED;
    private ColumnType type;
    private boolean isDirty = true;

    public enum ColumnType {

        Date("date"),
        Timestamp("timestamp"),
        Datetime("datetime"),
        Integer("int"),
        Decimal("decimal"),
        String("string"),
        Code("code");
        private String name;

        ColumnType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    };

    public Column(String name, ColumnType type, String filter) {
        this.name = name;
        this.filter = filter;
        this.type = type;
    }

    public Ordering getOrdering() {
        return ordering;
    }

    public void setOrdering(Ordering ordering) {
        this.ordering = ordering;
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

    public void changeFilter(ActionEvent event) {
        this.isDirty = true;
        if (filter.equals("")) {
            invalidFilter = false;
            return;
        }

        String parsedFilter = FilterParser.parseExpr((String) filter);
        if (parsedFilter == null) {
            invalidFilter = true;
        } else {
            invalidFilter = false;
        }
    }

    public void changeOrdering() {
        this.isDirty = true;

        if (this.ordering == Ordering.UNSORTED) {
            this.ordering = Ordering.ASCENDING;
        } else if (this.ordering == Ordering.ASCENDING) {
            this.ordering = Ordering.DESCENDING;
        } else {
            this.ordering = Ordering.ASCENDING;
        }
    }

    public void resetOrdering() {
        this.ordering = Ordering.UNSORTED;
    }
}
