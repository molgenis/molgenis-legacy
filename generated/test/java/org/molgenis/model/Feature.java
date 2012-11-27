
/* File:        org.molgenis/model/Feature.java
 * Generator:   org.molgenis.generators.DataTypeGen 4.0.0-testing
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 

package org.molgenis.model;

/**
 * Feature: .
 * @author MOLGENIS generator
 */
@javax.persistence.Entity
//@org.hibernate.search.annotations.Indexed
@javax.persistence.Table(name = "Feature"
)


@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
//@EntityListeners({org.molgenis.model.db.FeatureEntityListener.class})
public class Feature extends org.molgenis.model.Characteristic 
{
	// fieldname constants
	public final static String DATATYPE = "dataType";
	public final static String ID = "id";
	
	//static methods
	/**
	 * Shorthand for db.query(Feature.class).
	 */
	public static org.molgenis.framework.db.Query<? extends Feature> query(org.molgenis.framework.db.Database db)
	{
		return db.query(Feature.class);
	}
	
	/**
	 * Shorthand for db.find(Feature.class, org.molgenis.framework.db.QueryRule ... rules).
	 */
	public static java.util.List<? extends Feature> find(org.molgenis.framework.db.Database db, org.molgenis.framework.db.QueryRule ... rules) throws org.molgenis.framework.db.DatabaseException
	{
		return db.find(Feature.class, rules);
	}	
	
	/**
	 * 
	 */
	public static Feature findById(org.molgenis.framework.db.Database db, Integer id) throws org.molgenis.framework.db.DatabaseException
	{
		org.molgenis.framework.db.Query<Feature> q = db.query(Feature.class);
		q.eq(Feature.ID, id);
		java.util.List<Feature> result = q.find();
		if(result.size()>0) return result.get(0);
		else return null;
	}

	/**
	 * 
	 */
	public static Feature findByIdentifier(org.molgenis.framework.db.Database db, String identifier) throws org.molgenis.framework.db.DatabaseException
	{
		org.molgenis.framework.db.Query<Feature> q = db.query(Feature.class);
		q.eq(Feature.IDENTIFIER, identifier);
		java.util.List<Feature> result = q.find();
		if(result.size()>0) return result.get(0);
		else return null;
	}

	/**
	 * 
	 */
	public static Feature findByNameIdentifier(org.molgenis.framework.db.Database db, String name, String identifier) throws org.molgenis.framework.db.DatabaseException
	{
		org.molgenis.framework.db.Query<Feature> q = db.query(Feature.class);
		q.eq(Feature.NAME, name);q.eq(Feature.IDENTIFIER, identifier);
		java.util.List<Feature> result = q.find();
		if(result.size()>0) return result.get(0);
		else return null;
	}

	
	// member variables (including setters.getters for interface)


	//dataType[type=enum]
	@javax.persistence.Column(name="dataType", nullable=false)
	@javax.xml.bind.annotation.XmlElement(name="dataType")
	
				

	@javax.validation.constraints.NotNull
	private String dataType =  "string";
	@javax.persistence.Transient
	private String dataType_label = null;
	@javax.persistence.Transient
	private java.util.List<org.molgenis.util.ValueLabel> dataType_options = new java.util.ArrayList<org.molgenis.util.ValueLabel>();


	//id[type=int]
	

	//constructors
	public Feature()
	{
		//set the type for a new instance
		set__Type(this.getClass().getSimpleName());
	
		//options for enum DataType
		dataType_options.add(new org.molgenis.util.ValueLabel("xref","xref"));
		dataType_options.add(new org.molgenis.util.ValueLabel("string","string"));
		dataType_options.add(new org.molgenis.util.ValueLabel("nominal","nominal"));
		dataType_options.add(new org.molgenis.util.ValueLabel("ordinal","ordinal"));
		dataType_options.add(new org.molgenis.util.ValueLabel("date","date"));
		dataType_options.add(new org.molgenis.util.ValueLabel("datetime","datetime"));
		dataType_options.add(new org.molgenis.util.ValueLabel("int","int"));
		dataType_options.add(new org.molgenis.util.ValueLabel("code","code"));
		dataType_options.add(new org.molgenis.util.ValueLabel("image","image"));
		dataType_options.add(new org.molgenis.util.ValueLabel("decimal","decimal"));
		dataType_options.add(new org.molgenis.util.ValueLabel("bool","bool"));
		dataType_options.add(new org.molgenis.util.ValueLabel("file","file"));
		dataType_options.add(new org.molgenis.util.ValueLabel("log","log"));
		dataType_options.add(new org.molgenis.util.ValueLabel("data","data"));
		dataType_options.add(new org.molgenis.util.ValueLabel("exe","exe"));
	}
	
	//getters and setters
	/**
	 * Get the dataType.
	 * @return dataType.
	 */
	public String getDataType()
	{
		return this.dataType;
	}
	
	@Deprecated
	public String getDataType(org.molgenis.framework.db.Database db)
	{
		throw new UnsupportedOperationException();
	}	
	
	/**
	 * Set the dataType.
	 * @param dataType
	 */
	public void setDataType( String dataType)
	{
		
		this.dataType = dataType;
	}

	
	/**
	 * Get tha label for enum DataType.
	 */
	public String getDataTypeLabel()
	{
		return this.dataType_label;
	}
	
	/**
	 * DataType is enum. This method returns all available enum options.
	 */
	public java.util.List<org.molgenis.util.ValueLabel> getDataTypeOptions()
	{
		return dataType_options;
	}	
	

	

	


	/**
	 * Generic getter. Get the property by using the name.
	 */
	@Override
	public Object get(String name)
	{
		name = name.toLowerCase();
		if (name.toLowerCase().equals("id"))
			return getId();
		if (name.toLowerCase().equals("identifier"))
			return getIdentifier();
		if (name.toLowerCase().equals("name"))
			return getName();
		if (name.toLowerCase().equals("__type"))
			return get__Type();
		if(name.toLowerCase().equals("__type_label"))
			return get__TypeLabel();
		if (name.toLowerCase().equals("description"))
			return getDescription();
		if (name.toLowerCase().equals("datatype"))
			return getDataType();
		if(name.toLowerCase().equals("datatype_label"))
			return getDataTypeLabel();
		return "";
	}	
	
	@Override
	public void validate() throws org.molgenis.framework.db.DatabaseException
	{
		if(this.getId() == null) throw new org.molgenis.framework.db.DatabaseException("required field id is null");
		if(this.getIdentifier() == null) throw new org.molgenis.framework.db.DatabaseException("required field identifier is null");
		if(this.getName() == null) throw new org.molgenis.framework.db.DatabaseException("required field name is null");
		if(this.get__Type() == null) throw new org.molgenis.framework.db.DatabaseException("required field __Type is null");
		if(this.getDataType() == null) throw new org.molgenis.framework.db.DatabaseException("required field dataType is null");
	}
	
	
	
	//@Implements
	@Override
	public void set( org.molgenis.util.Tuple tuple, boolean strict )  throws Exception
	{
		//optimization :-(
		if(tuple instanceof org.molgenis.util.ResultSetTuple)
		{
				//set Id
			this.setId(tuple.getInt("id"));
			//set Identifier
			this.setIdentifier(tuple.getString("Identifier"));
			//set Name
			this.setName(tuple.getString("Name"));
			//set __Type
			this.set__Type(tuple.getString("__Type"));
			//set Description
			this.setDescription(tuple.getString("description"));
			//set DataType
			this.setDataType(tuple.getString("dataType"));
		}
		else if(tuple != null)
		{
			//set Id
			if( strict || tuple.getInt("id") != null) this.setId(tuple.getInt("id"));
			if( tuple.getInt("Feature_id") != null) this.setId(tuple.getInt("Feature_id"));
			//set Identifier
			if( strict || tuple.getString("Identifier") != null) this.setIdentifier(tuple.getString("Identifier"));
			if( tuple.getString("Feature_Identifier") != null) this.setIdentifier(tuple.getString("Feature_Identifier"));
			//set Name
			if( strict || tuple.getString("Name") != null) this.setName(tuple.getString("Name"));
			if( tuple.getString("Feature_Name") != null) this.setName(tuple.getString("Feature_Name"));
			//set __Type
			if( strict || tuple.getString("__Type") != null) this.set__Type(tuple.getString("__Type"));
			if( tuple.getString("Feature___Type") != null) this.set__Type(tuple.getString("Feature___Type"));
			//set Description
			if( strict || tuple.getString("description") != null) this.setDescription(tuple.getString("description"));
			if( tuple.getString("Feature_description") != null) this.setDescription(tuple.getString("Feature_description"));
			//set DataType
			if( strict || tuple.getString("dataType") != null) this.setDataType(tuple.getString("dataType"));
			if( tuple.getString("Feature_dataType") != null) this.setDataType(tuple.getString("Feature_dataType"));
		}
		//org.apache.log4j.Logger.getLogger("test").debug("set "+this);
	}
	
	
	
	

	@Override
	public String toString()
	{
		return this.toString(false);
	}
	
	@Override
	public String toString(boolean verbose)
	{
		String result = "Feature(";
		result+= "id='" + getId()+"' ";	
		result+= "identifier='" + getIdentifier()+"' ";	
		result+= "name='" + getName()+"' ";	
		result+= "__Type='" + get__Type()+"' ";	
		result+= "description='" + getDescription()+"' ";	
		result+= "dataType='" + getDataType()+"'";	
		result += ");";
		return result;

	}

	/**
	 * Get the names of all public properties of Feature.
	 */
	@Override
	public java.util.Vector<String> getFields(boolean skipAutoIds)
	{
		java.util.Vector<String> fields = new java.util.Vector<String>();
		if(!skipAutoIds)
		{
			fields.add("id");
		}
		{
			fields.add("identifier");
		}
		{
			fields.add("name");
		}
		{
			fields.add("__Type");
		}
		{
			fields.add("description");
		}
		{
			fields.add("dataType");
		}
		return fields;
	}	

	@Override
	public java.util.Vector<String> getFields()
	{
		return getFields(false);
	}

	@Override
	public String getIdField()
	{
		return "id";
	}
	

	
	@Override
	public java.util.List<String> getLabelFields()
	{
		java.util.List<String> result = new java.util.ArrayList<String>();
		result.add("Identifier");
		return result;
	}

	@Override
	@Deprecated
	public String getFields(String sep)
	{
		return (""
		+ "id" +sep
		+ "identifier" +sep
		+ "name" +sep
		+ "__Type" +sep
		+ "description" +sep
		+ "dataType" 
		);
	}

	@Override
	public Object getIdValue()
	{
		return get(getIdField());
	}		
	
	
    @Override
	public String getXrefIdFieldName(String fieldName) {
        
        return null;
    }	

	@Override
	public boolean equals(Object obj) {
   		if (obj == null) { return false; }
   		if (obj == this) { return true; }
   		if (obj.getClass() != getClass()) {
     		return false;
   		}
		Feature rhs = (Feature) obj;
   		return new org.apache.commons.lang.builder.EqualsBuilder()
             	.appendSuper(super.equals(obj))
                .isEquals();
  	}

  	@Override
    public int hashCode() {
    	int firstNumber = this.getClass().getName().hashCode();
    	int secondNumber = this.getClass().getSimpleName().hashCode();
    	if(firstNumber % 2 == 0) {
    	  firstNumber += 1;
    	}
    	if(secondNumber % 2 == 0) {
    		secondNumber += 1;
    	}
    
		return new org.apache.commons.lang.builder.HashCodeBuilder(firstNumber, secondNumber)
             	.appendSuper(super.hashCode())
   			.toHashCode();
    }  	
  	


	@Override
	@Deprecated
	public String getValues(String sep)
	{
		java.io.StringWriter out = new java.io.StringWriter();
		{
			Object valueO = getId();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getIdentifier();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getName();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = get__Type();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getDescription();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS+sep);
		}
		{
			Object valueO = getDataType();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS);
		}
		return out.toString();
	}
	
	@Override
	public Feature create(org.molgenis.util.Tuple tuple) throws Exception
	{
		Feature e = new Feature();
		e.set(tuple);
		return e;
	}
	
//1
	@javax.persistence.OneToMany(fetch=javax.persistence.FetchType.LAZY, mappedBy="feature"/*, cascade={javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REFRESH}*/)
    private java.util.Collection<org.molgenis.model.Category> featureCategoryCollection = new java.util.ArrayList<org.molgenis.model.Category>();

	@javax.xml.bind.annotation.XmlTransient
	public java.util.Collection<org.molgenis.model.Category> getFeatureCategoryCollection()
	{
            return featureCategoryCollection;
	}

    public void setFeatureCategoryCollection(java.util.Collection<org.molgenis.model.Category> collection)
    {
        for (org.molgenis.model.Category category : collection) {
            category.setFeature(this);
        }
        featureCategoryCollection = collection;
    }	

	
}

