package plugins.GenericImporter;

import java.util.HashMap;

import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class TableField {

	//Prediction prediction = null;
	
	public static boolean COLVALUE = true;
	
	public static boolean COLHEADER = false;
	
	//e.g. 'Protocol'
	String classType = "";
	
	//e.g. 'name'
	String fieldName = "";
	
	int columnIndex;
	
	int[] dependentColumnIndex;
	
	Boolean vertical;
	
	//TableField Object = null;
	
	private String Relation = "";
	
	private HashMap<String, Object> nameToClass;

	Protocol protocol = null;
	
	ObservedValue observedValue = null;
	
	Measurement measurement = null;
	
	Panel panel = null;
	
	Category category = null;

	private TableField Target = null;

	private TableField Feature = null;
	
	private Tuple defaults = new SimpleTuple();

	private ProtocolApplication protocolApplication = null;

	private int observationTarget = -1;
	
	private InvestigationElement entity = null;



	private String relationString = null;

	
	public InvestigationElement getEntity() {
		return entity;
	}


	public void setEntity(InvestigationElement entity) {
		this.entity = entity;
	}


	public TableField(String ClassType, String fieldName, int columnIndex, Boolean Vertical, Tuple defaults, int... dependentColumnIndex) {
		this.fieldName = fieldName;
		this.classType = ClassType;
		this.columnIndex = columnIndex;
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


	public String getRelationString() {
		return relationString;
	}


	public void setRelationString(String relationString) {
		this.relationString = relationString;
	}
}
