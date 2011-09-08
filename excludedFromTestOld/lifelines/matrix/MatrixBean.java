package lifelines.matrix;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import lifelines.matrix.Exporter.ExportFactory;
import lifelines.matrix.Exporter.MatrixExporter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;

import app.JpaDatabase;

public class MatrixBean {
    private Database db = new JpaDatabase();
    private PagableDataModel<Column, Integer> dataModel;
    private Collection<String> featureGroups;
    private String selectedFeatureGroup;
    private Collection<String> features;
    private String selectedFeature = null;
    private String selectedFeatureByName;
    private List<String> featuresByName;
    private String selectedGroupByName;
    private List<String> groupsByName;
    private List<Investigation> investigations;
    private Investigation investigation;
    private Map<Investigation, List<Column>> investigationColumns = new HashMap<Investigation, List<Column>>();
    private DatatableStateHolder datatableStateHolder = new DatatableStateHolder();
    private Column sortColumn;
    private String exportType = ""; //EXCEL, SPSS
    private String exportOption = "All";
    private boolean exportAllColumns = false;

    public MatrixBean() throws NumberFormatException, DatabaseException, SQLException, ParseException, Exception {
    	dataModel = new PagableDataModel<Column, Integer>(new RelationalMatrix(10));
        loadObservableFeatureGroups();
        loadFeaturesByName();
        loadInvestigation();
    }

    public boolean isExportAllColumns() {
        return exportAllColumns;
    }

    public void setExportAllColumns(boolean exportAllColumns) {
        this.exportAllColumns = exportAllColumns;
    }



    public DatatableStateHolder getDatatableStateHolder() {
        return datatableStateHolder;
    }

    public void setDatatableStateHolder(DatatableStateHolder datatableStateHolder) {
        this.datatableStateHolder = datatableStateHolder;
    }
    private Object tableState;

    public Object getTableState() {
        return tableState;
    }

    public void setTableState(Object tableState) {
        this.tableState = tableState;
    }

    private PagableMatrix<Column, Column> getMatrix() {
        return dataModel.getMatrix();
    }

    public void selectInvestigation(ValueChangeEvent vce) throws DatabaseException {
        String value = vce.getNewValue().toString();
        if (!value.isEmpty()) {
            //stores columns of dataset
//            if (investigation != null) {
////                addAllColumns();
//                investigationColumns.put(investigation, getMatrix().getColumns());
//            } else {
//                addAllColumns();
//            }

            int id = Integer.parseInt(value);
            investigation = db.findById(Investigation.class, id);
            //investigation = db.getEntityManager().find(Investigation.class, id);
            getMatrix().setInvestigation(investigation);

            //restore columns of dataset
//            if (investigationColumns.containsKey(investigation)) {
//                getMatrix().setColumns(investigationColumns.get(investigation));
//            } else {
//                getMatrix().setColumns(new ArrayList<Column>());
//            }
            dataModel.refresh();
        } else {
            investigation = null;
        }
    }

    public boolean isInvestigationSelected() {
        return investigation != null;
    }

    public String getSelectedInvestigation() {
        if (investigation != null) {
            return investigation.getId().toString();
        }
        return null;
    }

    public List<SelectItem> getInvestegationItems() {
        List<SelectItem> result = new ArrayList<SelectItem>();
        for (Investigation inv : investigations) {
            result.add(new SelectItem(inv.getId(), inv.getName(), inv.getDescription()));
        }
        return result;
    }

    public List<Investigation> getInvestigations() {
        return investigations;
    }

    public void setInvestigations(List<Investigation> investigation) {
        this.investigations = investigation;
    }

    public void cmbFeatureGroupSelected(ValueChangeEvent vce)
            throws DatabaseException {
        String selectedValue = (String) vce.getNewValue();
        loadObservableFeatureNamesByGroup(selectedValue);
    }

    public void cmbFeatureNameSelected(ValueChangeEvent vce)
            throws DatabaseException {
        loadGroupsByName((String) vce.getNewValue());
    }

    public void cmbInvestigationSelected(ValueChangeEvent vce) {
        System.out.println(vce.toString());
    }

    public void cmbExport(ActionEvent ae) {
        if(exportType.equalsIgnoreCase("Excel")) {
            if(exportOption.equalsIgnoreCase("ALL"))
                export(exportOptions.ALL, ExportFactory.exportType.EXCEL);    
            else 
                export(exportOptions.VISABLE, ExportFactory.exportType.EXCEL);    
        } else if(exportType.equalsIgnoreCase("Spss")) {
            if(exportOption.equalsIgnoreCase("ALL"))
                export(exportOptions.ALL, ExportFactory.exportType.SPSS);
            else
                export(exportOptions.VISABLE, ExportFactory.exportType.SPSS);
        }
    }

    public void changeOrdering(ActionEvent ae) {
        for (Column column : getMatrix().getColumns()) {
            if (column.equals(this.sortColumn)) {
                column.changeOrdering();
            } else {
                column.resetOrdering();
            }
        }
    }

    private enum exportOptions {
        ALL,
        VISABLE
    }

    private void export(exportOptions exportOption, ExportFactory.exportType exportType) {
        try {
            MatrixExporter exporter = ExportFactory.create(exportType);

            //Get the request from the FacesContext and create outputstream
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.setContentType(exporter.getContentType());
            response.setHeader("Content-disposition", "attachment; filename=Matrix." + exporter.getFileExtenstion());
            ServletOutputStream out = response.getOutputStream();

            if (exportOption == exportOption.ALL) { //load all data
                int numberOfRows = getMatrix().getNumberOfRows();
                getMatrix().loadData(numberOfRows, -1);
            } else {
                //getMatrix().getColumns()
            }

            exporter.export(getMatrix(), out);

            /* flush it to HTTP */
            out.flush();
            out.close();

            /* end of request */
            FacesContext faces = FacesContext.getCurrentInstance();
            faces.responseComplete();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadObservableFeatureGroups() throws DatabaseException {
        featureGroups = ColumnUtils.getColumnGroups();
    }

    private void loadObservableFeatureNamesByGroup(String featureGroupName)
            throws DatabaseException {
        features = ColumnUtils.getColumnsGroup(featureGroupName);
    }

    private void loadFeaturesByName() throws DatabaseException {
        featuresByName = ColumnUtils.getColumnNames();
    }

    private void loadGroupsByName(String columnName) throws DatabaseException {
        groupsByName = ColumnUtils.getGroupsByName(columnName);
    }

    private void loadInvestigation() throws DatabaseException {
        investigations = ColumnUtils.getInvestigation();
    }

    public void addColumn() throws DatabaseException {
        Measurement m = ColumnUtils.getMeasurementByName(investigation, selectedFeatureGroup + "." + selectedFeature);
        String dateType = m.getDataType();
        Column column = new Column(m.getName(), Column.getColumnType(dateType), "");
        dataModel.addColumn(column);
    }

    public void addAllColumns() throws DatabaseException {
        getMatrix().setColumns(new ArrayList<Column>());
        for (Measurement m : ColumnUtils.getMeasurements(investigation)) {
            String name = m.getName();
            dataModel.addColumn(new Column(name, Column.getColumnType(m.getDataType()), ""));
        }
    }

    public void removeAllColumns() {
        getMatrix().setColumns(new ArrayList<Column>());
        dataModel.refresh();
    }
    private int removeColumnIndex;

    public void setRemoveColumnIndex(int index) {
        removeColumnIndex = index;
    }

    public void removeColumn() {
        dataModel.removeColumn(removeColumnIndex);
    }

    public void filterChanged(ActionEvent ae) {
        dataModel.refresh();
    }

    public Collection<Column> getTargets() {
        return getMatrix().getRows();
    }

    public Collection<String> getFeatureGroups() {
        return featureGroups;
    }

    public String getSelectedFeatureGroup() {
        return selectedFeatureGroup;
    }

    public void setSelectedFeatureGroup(String selectedFeatureGroup) {
        this.selectedFeatureGroup = selectedFeatureGroup;
    }

    public Collection<String> getFeatures() {
        return features;
    }

    public void setSelectedFeature(String selectedFeature) {
        this.selectedFeature = selectedFeature;
    }

    public String getSelectedFeature() {
        return selectedFeature;
    }

    public String getSelectedFeatureByName() {
        return selectedFeatureByName;
    }

    public void setSelectedFeatureByName(String selectedFeatureByName) {
        this.selectedFeatureByName = selectedFeatureByName;
    }

    public List<String> getFeaturesByName() {
        return featuresByName;
    }

    public void setFeaturesByName(List<String> featuresByName) {
        this.featuresByName = featuresByName;
    }

    public String getSelectedGroupByName() {
        return selectedGroupByName;
    }

    public void setSelectedGroupByName(String selectedGroupByName) {
        this.selectedGroupByName = selectedGroupByName;
    }

    public List<String> getGroupsByName() {
        return groupsByName;
    }

    public void setGroupsByName(List<String> groupsByName) {
        this.groupsByName = groupsByName;
    }

    public PagableDataModel<Column, Integer> getDataModel() {
        return dataModel;
    }

    public void setDataModel(PagableDataModel<Column, Integer> dataModel) {
        this.dataModel = dataModel;
    }

    public int getPageSize() {
        return this.dataModel.getPageSize();
    }

    public String getSortColumn() {
        if (sortColumn != null) {
            return sortColumn.getName();
        }
        return "";
    }

    public void setSortColumn(String sortColumn) {
        System.out.println("setSortColumn: " + sortColumn.toString());

        for (Column c : getMatrix().getColumns()) {
            if (c.getName().equals(sortColumn)) {
                this.sortColumn = c;
                break;
            }
        }
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getExportOption() {
        return exportOption;
    }

    public void setExportOption(String exportOption) {
        this.exportOption = exportOption;
    }
    
    public void next(ActionEvent ae) {
    	this.dataModel.getMatrix().getColumnPager().nextPage();
    }
 
    public void prev(ActionEvent ae) {
    	this.dataModel.getMatrix().getColumnPager().prevPage();
    }

    public void last(ActionEvent ae) {
        this.dataModel.getMatrix().getColumnPager().lastPage();
    }

    public void first(ActionEvent ae) {
        this.dataModel.getMatrix().getColumnPager().firstPage();
    }

    public boolean getHasNext() {
        return this.dataModel.getMatrix().getColumnPager().hasNextPage();
    }

    public boolean getHasPrev() {
        return this.dataModel.getMatrix().getColumnPager().hasPrevPage();
    }

    public int getCurrentPageIndex() {
        return this.dataModel.getMatrix().getColumnPager().getCurrentPageIndex();
    }

    public int getNumberOfRows() {
        return this.dataModel.getMatrix().getNumberOfRows();
    }

    public int getNumberOfColumns() {
        return this.dataModel.getMatrix().getColumnPager().getRowCount();
    }

    public int getColumnPageSize() {
        return this.dataModel.getMatrix().getColumnPager().getPageSize();
    }
}