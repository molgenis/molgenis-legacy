package lifelines.matrix;

import javax.faces.event.ActionEvent;

/**
 *
 * @author joris lops
 */
public class SimplePager {
    private int currentPageIndex = 0;
    private int pageSize = 10;
    private boolean dirty = false;

    private PagableMatrix matrix;


    public SimplePager(PagableMatrix dataModel) {
    	this.matrix = dataModel;
    }

    public SimplePager(PagableMatrix dataModel, int pageSize) {
    	this.matrix = dataModel;
    	this.pageSize = pageSize;
    }

    public boolean hasNextPage() {
        if(((currentPageIndex+1)*pageSize) < matrix.getColumns().size()) {
            return true;
        } else {
            return false;
        }
    }

    public void nextPage() {
        if(hasNextPage()) {
            ++currentPageIndex;
            dirty = true;
        }
    }

    public void lastPage() {
        currentPageIndex = (matrix.getColumns().size() / pageSize) - 1;
    }
    
    public boolean hasPrevPage() {
        if(((currentPageIndex-1)*pageSize) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void prevPage() {
        if(hasPrevPage()) {
            --currentPageIndex;
            dirty = true;
        }
    }

    public void firstPage() {
        currentPageIndex = 0;
        dirty = true;
    }

    public int getCurrentPageIndex() {
        if(currentPageIndex > (matrix.getColumns().size() / pageSize) ) {
            currentPageIndex = 0;
        }
        return currentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        if(currentPageIndex < 0 || matrix.getColumns().size() / pageSize > currentPageIndex) {
            throw new IllegalArgumentException("Incorrect currentPageIndex");
        }
        
        this.currentPageIndex = currentPageIndex;
        dirty = true;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        dirty = true;
    }

    public int getRowCount() {
        return matrix.getColumns().size();
    }

//    public void setRowCount(int rowCount) {
//        this.rowCount = rowCount;
//    }
    
    public void nextPage(ActionEvent ae) {
    	nextPage();
    }
    
    public void prevPage(ActionEvent ae) {
    	prevPage();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }    
}