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


public class PagableDataModel<Row, T> extends ExtendedDataModel {
    private Integer currentPk;
    private List<Row> wrappedKeys = null;
    private HashMap<Row, T[]> wrappedData = null;
    private SequenceRange cachedRange = null;
    private boolean refresh = true; //refresh database if column or filter changes
    private PagableMatrix<Column, Row> matrix;

    public PagableDataModel(PagableMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public Object getRowKey() {
        return currentPk;
    }

    @Override
    public void setRowKey(Object key) {
        this.currentPk = (Integer) key;
    }

    @Override
    public void walk(final FacesContext context, final DataVisitor visitor, final Range range, final Object argument)
            throws IOException {
        SequenceRange sequenceRange = (SequenceRange) range;
        int firstRow = sequenceRange.getFirstRow();
        int numberOfRows = sequenceRange.getRows();
        if (numberOfRows < 0) { //for extended data table
            numberOfRows = matrix.getNumberOfRows();
        }

        if (this.wrappedData == null
                || !areEqualRanges(this.cachedRange, sequenceRange) || refresh
                || dirtyColumns(matrix.getColumns()) || matrix.isDirty()) {

            resetDirty(matrix.getColumns());
            matrix.setDirty(false);

            wrappedKeys = new ArrayList<Row>();
            wrappedData = new HashMap<Row, T[]>();

            try {
                matrix.loadData(numberOfRows, firstRow);
                rowCount = matrix.getNumberOfRows();

                Collection<Column> columns = matrix.getColumns();
                List<String> colNames = new ArrayList<String>();
                for (Column col : columns) {
                    colNames.add(col.getName());
                }

                T[][] data = (T[][]) matrix.getData();
                List<Row> targets = matrix.getRows();

                int size = matrix.getRows().size() < numberOfRows ? matrix.getRows().size()
                        : numberOfRows;
                for (int idx = 0; idx < size; ++idx) {
                    wrappedKeys.add(targets.get(idx));
                    wrappedData.put(targets.get(idx), data[idx]);
                }

                this.cachedRange = sequenceRange;
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
            refresh = false;
        }

        int size = matrix.getRows().size() < numberOfRows ? matrix.getRows().size()
                : numberOfRows;
        for (int idx = 0; idx < size; ++idx) {
            visitor.process(context, matrix.getRows().get(idx), argument);
        }
    }

    private void resetDirty(List<Column> columns) {
        for (Column column : columns) {
            column.setDirty(false);
        }
    }

    private boolean dirtyColumns(List<Column> columns) {
        for (Column column : columns) {
            if (column.isDirty()) {
                return true;
            }
        }
        return false;
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
        refresh();
    }

    int rowCount = 0;
    @Override
    public int getRowCount() {
//        if(refresh) {
//
//        }
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

    public PagableMatrix<Column, Row> getMatrix() {
        return matrix;
    }

    public int getPageSize() {
        return matrix.getPageSize();
    }
}