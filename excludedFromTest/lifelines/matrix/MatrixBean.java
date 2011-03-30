package lifelines.matrix;

import java.io.OutputStream;
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

import lifelines.matrix.Column.ColumnType;
import lifelines.matrix.Exporter.ExportFactory;
import lifelines.matrix.Exporter.MatrixExporter;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;

import app.JpaDatabase;

public class MatrixBean extends PagableDataModel<String> {
	private JpaDatabase db = new JpaDatabase();
	
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

	private final static int pageSize = 5;
	
	private Map<Investigation, List<Column>> investigationColumns = new HashMap<Investigation, List<Column>>();
	
	private DatatableStateHolder datatableStateHolder = new DatatableStateHolder();

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

	public void selectInvestigation(ValueChangeEvent vce) throws DatabaseException {
		String value = vce.getNewValue().toString();
		if(!value.isEmpty()) {			
			//stores columns of dataset
			if(investigation != null) {
				investigationColumns.put(investigation, getMatrix().getColumns());
			}
			
			int id = Integer.parseInt(value);
			investigation = db.getEntityManager().find(Investigation.class, id);
			getMatrix().setInvestigation(investigation);
			
			
			
			
			//restore columns of dataset
			if(investigationColumns.containsKey(investigation)) {
				getMatrix().setColumns(investigationColumns.get(investigation));
			} else {
				getMatrix().setColumns(new ArrayList<Column>());
				

			}
			refresh();			
		} else {
			investigation = null;
		}
	}
	
	public boolean isInvestigationSelected() {
		return investigation != null;
	}
	
	public String getSelectedInvestigation() {
		if(investigation != null) {
			return investigation.getId().toString();
		}
		return null;
	}
	
	public List<SelectItem> getInvestegationItems() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		for(Investigation inv : investigations) {
			result.add(new SelectItem(inv, inv.getName(), inv.getDescription()));
		}
		return result;
	}
	
	public MatrixBean() throws NumberFormatException, DatabaseException,
			SQLException, ParseException {
		super(new DBMatrix<String>(String.class, 5));
	
		// super(new MemoryDBMatrix());
		// super.addColumn("patient.geslacht");
		// super.addColumn("patient.gebdat");
		// super.addColumn("patient.postcode");

		loadObservableFeatureGroups();
		loadFeaturesByName();
		loadInvestigation();
	}
	
	public void setTest(Object obj) {
		System.out.println(obj.toString());
	}
	
	public Object getTest() {
		return null;
	}

	@Override
	public int getPageSize() {
		return pageSize;
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

	public void cmbExcelAll(ActionEvent ae) {
		export(true,  ExportFactory.exportType.EXCEL);
	}

	public void cmbExcelVisible(ActionEvent ae) {
		export(false, ExportFactory.exportType.EXCEL);
	}	
	
	public void cmbSpSSAll(ActionEvent ae) {
		export(true, ExportFactory.exportType.SPSS);
	}
	
	public void cmbSpSSVisible(ActionEvent ae) {
		export(false, ExportFactory.exportType.SPSS);
	}
	
	private void export(boolean allData, ExportFactory.exportType exportType) {
		try {
			   MatrixExporter exporter = ExportFactory.create(exportType);
			
		       //Get the request from the FacesContext and create outputstream
		       FacesContext context = FacesContext.getCurrentInstance();
		       HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		       response.setContentType(exporter.getContentType());
		       response.setHeader("Content-disposition", "attachment; filename=Matrix." + exporter.getFileExtenstion());
		       ServletOutputStream out = response.getOutputStream();			
				
		       if(allData) { //load all data
		    	   int numberOfRows = getMatrix().getNumberOfRows();
		    	   getMatrix().loadData(numberOfRows, 0);		    	   
		       } 		       		       
			   	
		       exporter.export(getMatrix(), out);

		       /* flush it to HTTP */
		       out.flush();
		       out.close();

		       /* end of request */
		       FacesContext faces = FacesContext.getCurrentInstance();
		       faces.responseComplete();    
				
			} catch (Exception e) {
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
		Column.ColumnType columnType = ColumnType.String;
		if(dateType.equals("string")) {
			columnType = ColumnType.String;
		} else if(dateType.equals("int")) {
			columnType = ColumnType.Integer;
		} else if(dateType.equals("code")) {
			columnType = ColumnType.Code;
		} else if(dateType.equals("decimal")) {
			columnType = ColumnType.Decimal;
		} else if(dateType.equals("datetime")) {
			columnType = ColumnType.Timestamp;
		} else {
			throw new UnsupportedOperationException(String.format("Type %s not available",dateType));
		}
		
		Column column = new Column(m.getName(), columnType, "");
		super.addColumn(column);
	}
	
	public void addAllColumns() throws DatabaseException {
		getMatrix().setColumns(new ArrayList<Column>());
		for(Measurement m : ColumnUtils.getMeasurements(investigation)) {
			String name = m.getName();
//			name = name.substring(name.indexOf(".")+1);
			getMatrix().getColumns().add(new Column(name,ColumnType.String,""));	
		}		
	}
	
	public void removeAllColumns() {
		getMatrix().setColumns(new ArrayList<Column>());
	}

	

//	public void addColumnByNameGroup() {
//		//super.addColumn(selectedGroupByName + "." + selectedFeatureByName);
//	}

	private int removeColumnIndex;

	public void setRemoveColumnIndex(int index) {
		removeColumnIndex = index;
	}

	public void removeColumn() {
		super.removeColumn(removeColumnIndex);
	}

	private int sortColumnIndex;

	public void setSortColumnIndex(int sortColumnIndex) {
		this.sortColumnIndex = sortColumnIndex;
	}

	public void sortColumn() {
		super.sortColumn(sortColumnIndex);
	}

	public void filterChanged(ActionEvent ae) {
		super.refresh();
	}

	public Collection<Integer> getTargets() {
		return super.getMatrix().getRows();
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
}