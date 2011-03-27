package lifelines.matrix;

import java.util.HashMap;
import java.util.Map;
import org.richfaces.model.SortOrder;

public class DatatableStateHolder {
	private Map<String, SortOrder> sortOrders = new HashMap<String, SortOrder>();
	private Map<String, Object> columnFilterValues = new HashMap<String, Object>();

	public Map<String, Object> getColumnFilterValues() {
		return columnFilterValues;
	}

	public void setColumnFilterValues(Map<String, Object> columnFilterValues) {
		this.columnFilterValues = columnFilterValues;
	}

	public Map<String, SortOrder> getSortOrders() {
		return sortOrders;
	}

	public void setSortOrders(Map<String, SortOrder> sortOrders) {
		this.sortOrders = sortOrders;
	}
}