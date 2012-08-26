package plugins.biobankimporter;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class TableField {

	//Prediction prediction = null;
	
	public static boolean COLVALUE = true;
	
	public static boolean COLHEADER = false;
	
	//e.g. 'Protocol'
	private String classType = "";
	//e.g. 'name'
	private String fieldName = "";
	
	private String multipleValues = "";

	private int columnIndex;
	
	private int[] dependentColumnIndex;
	
	private Boolean vertical;

	private Protocol protocol = null;
	
	private ObservedValue observedValue = null;
	
	private Measurement measurement = null;
	
	private Panel panel = null;
	
	private Category category = null;

	private TableField Target = null;

	private TableField Feature = null;
	
	private Tuple defaults = new SimpleTuple();
	
	private int observationTarget = -1;
	
	private List<InvestigationElement> entity = null;
	
	private String relationString = null;
	
	private List<String> cellValues = new ArrayList<String>();

	public List<InvestigationElement> getEntity() {
		return entity;
	}
	
	public void setEntity(List<InvestigationElement> entity) {
		this.entity = entity;
	}
	
	public TableField(String ClassType, String fieldName, String multipleValues, int columnIndex, Boolean Vertical, Tuple defaults, int... dependentColumnIndex) {
		
		this.fieldName = fieldName;
		this.classType = ClassType;
		this.columnIndex = columnIndex;
		this.multipleValues = multipleValues;
		this.vertical = Vertical;
		this.defaults = defaults;
		this.dependentColumnIndex = dependentColumnIndex;
	}

	public Tuple getDefaults() {
		return defaults;
	}
	
	public void setDefaults(Tuple defaults) {
		this.defaults = defaults;
	}
	
	public int[] getDependentColumnIndex() {
		return dependentColumnIndex;
	}
	
	public void setDependentColumnIndex(int... dependentColumnIndex) {
		this.dependentColumnIndex = dependentColumnIndex;
	}
	
	public void initializeHashMap(String ClassType){
	}
	
	public String getClassType(){
		return classType;
	}
	
	public Boolean getVertical(){
		return vertical;
	}
	
	public int getColumnIndex(){
		return columnIndex;
	}
	
	public void setRelation(TableField Target, TableField Feature){
		this.Target = Target;
		this.Feature  = Feature;
	}
	public TableField getTarget(){
		return Target;
	}
	public TableField getFeature(){
		return Feature;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValueSplitter() {
		return "\\|";
	}

	public void setObservationTarget(int observationTarget) {
		this.observationTarget = observationTarget;
	}
	
	public int getObservationTarget() {
		return observationTarget;
	}

	public void setRelation(String fieldName) {
		
		this.relationString = fieldName;
		
	}

	public String getMultipleValues() {
		return multipleValues;
	}
	
	public String getRelationString() {
		return relationString;
	}

	public void setRelationString(String relationString) {
		this.relationString = relationString;
	}
	
	public List<String> getCellValue() {
		return cellValues;
	}

	public void addCellValue(String value) {
		cellValues.add(value);
	}
}
