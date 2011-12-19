
package csvtobin.sources;

public class Data
{
	private String name;
	private String investigation_Name;
	private String targetType;
	private String featureType;
	private String valueType;
	private String storage;
	
	
	
	public String getStorage()
	{
		return storage;
	}
	public void setStorage(String storage)
	{
		this.storage = storage;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getInvestigation_Name()
	{
		return investigation_Name;
	}
	public void setInvestigation_Name(String investigation_Name)
	{
		this.investigation_Name = investigation_Name;
	}
	public String getTargetType()
	{
		return targetType;
	}
	public void setTargetType(String targetType)
	{
		this.targetType = targetType;
	}
	public String getFeatureType()
	{
		return featureType;
	}
	public void setFeatureType(String featureType)
	{
		this.featureType = featureType;
	}
	public String getValueType()
	{
		return valueType;
	}
	public void setValueType(String valueType)
	{
		this.valueType = valueType;
	}
	
	
	public void set( Tuple tuple, boolean strict )  throws Exception
	{
		//set Name
		if( strict || tuple.getString("name") != null) this.setName(tuple.getString("name"));
		if( tuple.getString("Data_name") != null) this.setName(tuple.getString("Data_name"));
		//set Investigation
		if( strict || tuple.getObject("Investigation_name") != null) this.setInvestigation_Name(tuple.getString("Investigation_name"));			
		if( tuple.getObject("Data_Investigation_name") != null ) this.setInvestigation_Name(tuple.getString("Data_Investigation_name"));		
		//set FeatureType
		if( strict || tuple.getString("FeatureType") != null) this.setFeatureType(tuple.getString("FeatureType"));
		if( tuple.getString("Data_FeatureType") != null) this.setFeatureType(tuple.getString("Data_FeatureType"));
		//set TargetType
		if( strict || tuple.getString("TargetType") != null) this.setTargetType(tuple.getString("TargetType"));
		if( tuple.getString("Data_TargetType") != null) this.setTargetType(tuple.getString("Data_TargetType"));
		//set ValueType
		if( strict || tuple.getString("ValueType") != null) this.setValueType(tuple.getString("ValueType"));
		if( tuple.getString("Data_ValueType") != null) this.setValueType(tuple.getString("Data_ValueType"));
		//set Storage
		if( strict || tuple.getString("Storage") != null) this.setStorage(tuple.getString("Storage"));
		if( tuple.getString("Data_Storage") != null) this.setStorage(tuple.getString("Data_Storage"));
	}
	
}

