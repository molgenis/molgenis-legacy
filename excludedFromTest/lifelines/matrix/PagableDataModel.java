package lifelines.matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

import lifelines.matrix.Column.ColumnType;

public abstract class PagableDataModel<T> extends ExtendedDataModel {
	//to support datascroller
    private Integer currentPk;
    private List<Integer> wrappedKeys = null;
    private HashMap<Integer, T[]> wrappedData = null;
    private SequenceRange cachedRange = null;
    private boolean refresh = false; //refresh database if column or filter changes

	private PagableMatrix<T, Column, Integer> matrix;
	
	public PagableDataModel(PagableMatrix<T, Column, Integer> matrix) {
		this.matrix = matrix;
	}
	
	@Override
	public Object getRowKey() {
		return currentPk;
	}

	@Override
	public void setRowKey(Object key) {
		this.currentPk = (Integer)key;
	}

    @Override
    public void walk(final FacesContext context, final DataVisitor visitor, final Range range, final Object argument)
            throws IOException {
        SequenceRange sequenceRange = (SequenceRange) range;
        int firstRow = sequenceRange.getFirstRow();
        int numberOfRows = sequenceRange.getRows();
        if(numberOfRows < 0) { //for extended data table
        	numberOfRows = matrix.getNumberOfRows();
        }
        
        
        //refresh();
        if(true) {
        //if (this.wrappedData == null || !areEqualRanges(this.cachedRange, sequenceRange) || refresh) {
            wrappedKeys = new ArrayList<Integer>();
            wrappedData = new HashMap<Integer, T[]>();

            try {
            	matrix.loadData(numberOfRows, firstRow);
            	
            	Collection<Column> columns = matrix.getColumns();
            	List<String> colNames = new ArrayList<String>();
            	for(Column col : columns) {
            		colNames.add(col.getName());
            	}            	
            	
            	T[][] data = (T[][]) matrix.getData();
            	List<Integer> targets = matrix.getRows();

                int size = matrix.getRows().size() < numberOfRows ? matrix.getRows().size()
            			: numberOfRows;            	
            	for(int idx = 0; idx < size; ++idx) {
                    wrappedKeys.add(targets.get(idx));
                    wrappedData.put(targets.get(idx), data[idx]);	
            	}

                this.cachedRange = sequenceRange;
            } catch (Exception ex) {
                ex.printStackTrace();
            } 
            refresh = false;
        }
        
        int size = matrix.getRows().size() < numberOfRows ? matrix.getRows().size()
        			: numberOfRows;        	
        for(int idx = 0; idx < size; ++idx) {
        	visitor.process(context, matrix.getRows().get(idx), argument);
        }
    }
    
    private static boolean areEqualRanges(SequenceRange range1, SequenceRange range2) {
        if (range1 == null || range2 == null) {
            return range1 == null && range2 == null;
        } else {
            return range1.getFirstRow() == range2.getFirstRow() && range1.getRows() == range2.getRows();
        }
    }    

	public void addColumn(Column column) {
		this.matrix.getColumns().add(column);
		refresh();
	}    
	
	public void removeColumn(int index) {
		this.matrix.getColumns().remove(index);
		refresh();
	}
	
	public void sortColumn(int index) {
//		for(Column c : matrix.getColumns()) {
//                    if(c.equals(matrix.getColumns().get(index))) {
//                        if(!c.getSort().equals(Column.Sort.NONE)) {
//                            Column.Sort sort = c.getSort();
//                            if(sort.equals(Column.Sort.ASC)) {
//                                c.setSort(Column.Sort.DESC);
//                            } else {
//                                c.setSort(Column.Sort.ASC);
//                            }
//                        } else {
//                            c.setSort(Column.Sort.ASC);
//                        }
//                    } else {
//                        c.setSort(Column.Sort.NONE);
//                    }
//		}
		refresh();
	}
    
	@Override
	public int getRowCount() {
		int rowCount = matrix.getNumberOfRows();
		System.out.println("rowCount:" +rowCount);
		return rowCount;
	}

	@Override
	public Object getRowData() {
        if (currentPk == null) {
            return null;
        }

        T[] object = wrappedData.get(currentPk);
        return object;
    }

	@Override
	public int getRowIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getWrappedData() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRowAvailable() {
        if (currentPk == null) {
            return false;
        }
        if (wrappedKeys.contains(currentPk)) {
            return true;
        }
        if (wrappedData.entrySet().contains(currentPk)) {
            return true;
        }
        return false;
	}

	@Override
	public void setRowIndex(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWrappedData(Object arg0) {
		throw new UnsupportedOperationException();
	}
	
	public void refresh() {
		this.refresh = true;
	}
	
	public Column getColumn(int index) {
		return matrix.getColumns().get(index);
	}
	
	public List<Column> getColumns() {
		return matrix.getColumns();
	}
	
	public PagableMatrix<T, Column, Integer> getMatrix() {
		return matrix;
	}
	
	public abstract int getPageSize();
	
}
