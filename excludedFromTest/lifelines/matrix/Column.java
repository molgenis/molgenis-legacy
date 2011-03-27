package lifelines.matrix;

import java.io.Serializable;

import org.richfaces.model.Ordering;

public class Column implements Serializable {

    private String name;
    private Object filter;
    private Ordering ordering = Ordering.UNSORTED;

	public Ordering getOrdering() {
		return ordering;
	}

	public void setOrdering(Ordering ordering) {
		this.ordering = ordering;
	}

	public enum ColumnType {
        Date("Date"),
        Timestamp("Timestamp"),
        Integer("Integer"),
        Decimal("Decimal"),
        String("String"),
        Code("Code");
        private String name;

        ColumnType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    };
    private ColumnType type;

//    public enum Sort {
//        NONE("None"),
//        ASC("Asc"),
//        DESC("Desc");
//        private String name;
//
//        Sort(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public String toString() {
//            return name;
//        }
//    };
//    private Sort sort;

    public Column(String name, ColumnType type, String filter) {
        this.name = name;
        //this.filter = filter;
        this.filter = "";
        this.type = type;
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
    
    private Object myFilter;

	public Object getMyFilter() {
		return myFilter;
	}

	public void setMyFilter(Object myFilter) {
		this.myFilter = myFilter;
	}
    
    
    

//    public Sort getSort() {
//        return sort;
//    }
//
//    public void setSort(Sort sort) {
//        this.sort = sort;
//    }
}
