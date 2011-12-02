package plugins.predictionModel;

import java.util.HashMap;

import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;

public class TableField {

	Prediction prediction = null;
	
	public static boolean VERTICAL = true;
	
	public static boolean HORIZONTAL = false;
	
	String ClassType = "";
	
	int columnIndex;
	
	Boolean Vertical;
	
	TableField Object = null;
	
	private String Relation = "";
	
	private HashMap<String, Object> nameToClass;

	Protocol protocol = null;
	
	ObservedValue observedValue = null;
	
	Measurement measurement = null;
	
	Panel panel = null;

	private TableField Target = null;

	private TableField Feature = null;

	
	public TableField(String ClassType,int columnIndex, Boolean Vertical) {
		this.ClassType = ClassType;
		this.columnIndex = columnIndex;
		this.Vertical = Vertical;
	}
	
	public TableField(Panel panel, String ClassType,int columnIndex, Boolean Vertical) {
		this.panel = panel;
		this.ClassType = ClassType;
		this.columnIndex = columnIndex;
		this.Vertical = Vertical;
	}
	
	public TableField(Measurement measurement, String ClassType,int columnIndex, Boolean Vertical) {
		this.measurement = measurement;
		this.ClassType = ClassType;
		this.columnIndex = columnIndex;
		this.Vertical = Vertical;
	}
	public TableField(ObservedValue observedValue, String ClassType,int columnIndex, Boolean Vertical) {
	
		this.observedValue = observedValue;
		this.ClassType = ClassType;
		this.columnIndex = columnIndex;
		this.Vertical = Vertical;
	}
	
	public TableField(Protocol protocol, String ClassType,int columnIndex, Boolean Vertical) {
		this.protocol  = protocol;
		this.ClassType = ClassType;
		this.columnIndex = columnIndex;
		this.Vertical = Vertical;
	}

	public void initializeHashMap(String ClassType){
	}
	
	public String getClassType(){
		return ClassType;
	}
	
	public Boolean getVertical(){
		return Vertical;
	}
	
	public int getColumnIndex(){
		return columnIndex;
	}
	
	public Prediction getPrediction(){
		return prediction;
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
}
