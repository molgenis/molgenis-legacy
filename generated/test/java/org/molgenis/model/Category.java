
/* File:        org.molgenis/model/Category.java
 * Generator:   org.molgenis.generators.DataTypeGen 4.0.0-testing
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 

package org.molgenis.model;

/**
 * Category: .
 * @author MOLGENIS generator
 */
@javax.persistence.Entity
//@org.hibernate.search.annotations.Indexed
@javax.persistence.Table(name = "Category"
)


@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
//@EntityListeners({org.molgenis.model.db.CategoryEntityListener.class})
public class Category extends org.molgenis.util.AbstractEntity implements org.molgenis.model.Autoid
{
	// fieldname constants
	public final static String ID = "id";
	public final static String FEATURE = "feature";
	public final static String FEATURE_IDENTIFIER = "feature_Identifier";
	
	//static methods
	/**
	 * Shorthand for db.query(Category.class).
	 */
	public static org.molgenis.framework.db.Query<? extends Category> query(org.molgenis.framework.db.Database db)
	{
		return db.query(Category.class);
	}
	
	/**
	 * Shorthand for db.find(Category.class, org.molgenis.framework.db.QueryRule ... rules).
	 */
	public static java.util.List<? extends Category> find(org.molgenis.framework.db.Database db, org.molgenis.framework.db.QueryRule ... rules) throws org.molgenis.framework.db.DatabaseException
	{
		return db.find(Category.class, rules);
	}	
	
	/**
	 * 
	 */
	public static Category findById(org.molgenis.framework.db.Database db, Integer id) throws org.molgenis.framework.db.DatabaseException
	{
		org.molgenis.framework.db.Query<Category> q = db.query(Category.class);
		q.eq(Category.ID, id);
		java.util.List<Category> result = q.find();
		if(result.size()>0) return result.get(0);
		else return null;
	}

	
	// member variables (including setters.getters for interface)


	//id[type=int]
    @javax.persistence.Id @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @javax.persistence.Column(name="id", nullable=false)
	@javax.xml.bind.annotation.XmlElement(name="id")
	
	//@javax.validation.constraints.NotNull
	private Integer id =  null;


	//feature[type=xref]
    @javax.persistence.ManyToOne(fetch=javax.persistence.FetchType.LAZY /*cascade={javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REFRESH}*/)
    @javax.persistence.JoinColumn(name="feature", nullable=false)   	
	
				

	@javax.validation.constraints.NotNull
	private org.molgenis.model.Feature feature = null;
	@javax.persistence.Transient
	private Integer feature_id = null;	
	@javax.persistence.Transient
	private String feature_Identifier = null;						

	//constructors
	public Category()
	{
	
	}
	
	//getters and setters
	/**
	 * Get the id.
	 * @return id.
	 */
	@Override
	public Integer getId()
	{
		return this.id;
	}
	
	
	/**
	 * Set the id.
	 * @param id
	 */
	@Override
	public void setId( Integer id)
	{
		this.id = id;
	}

	

	/**
	 * Get the feature.
	 * @return feature.
	 */
	public org.molgenis.model.Feature getFeature()
	{
		return this.feature;
	}
	
	@Deprecated
	public org.molgenis.model.Feature getFeature(org.molgenis.framework.db.Database db)
	{
		throw new UnsupportedOperationException();
	}	
	
	/**
	 * Set the feature.
	 * @param feature
	 */
	public void setFeature( org.molgenis.model.Feature feature)
	{
		
		this.feature = feature;
	}

	
	
	/**
	 * Set foreign key for field feature.
	 * This will erase any foreign key objects currently set.
	 * FIXME: can we autoload the new object?
	 */
	public void setFeature_Id(Integer feature_id)
	{
		this.feature_id = feature_id;
	}	

	public void setFeature(Integer feature_id)
	{
		this.feature_id = feature_id;
	}
	
	public Integer getFeature_Id()
	{
		
		if(feature != null) 
		{
			return feature.getId();
		}
		else
		{
			return feature_id;
		}
	}	
	 
	/**
	 * Get a pretty label Identifier for cross reference Feature to Feature.Id.
	 */
	public String getFeature_Identifier()
	{		
		//FIXME should we auto-load based on getFeature()?	
		if(feature != null) {
			return feature.getIdentifier();
		} else {
			return feature_Identifier;
		}
	}		
	
	/**
	 * Set a pretty label for cross reference Feature to <a href="Feature.html#Id">Feature.Id</a>.
	 * Implies setFeature(null) until save
	 */
	public void setFeature_Identifier(String feature_Identifier)
	{
		this.feature_Identifier = feature_Identifier;
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
		if (name.toLowerCase().equals("feature"))
			return getFeature();
		if(name.toLowerCase().equals("feature_id"))
			return getFeature_Id();
		if(name.toLowerCase().equals("feature_identifier"))
			return getFeature_Identifier();
		return "";
	}	
	
	@Override
	public void validate() throws org.molgenis.framework.db.DatabaseException
	{
		if(this.getId() == null) throw new org.molgenis.framework.db.DatabaseException("required field id is null");
		if(this.getFeature() == null) throw new org.molgenis.framework.db.DatabaseException("required field feature is null");
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
			//set Feature
			this.setFeature(tuple.getInt("feature"));
			//set label Identifier for xref field Feature
			this.setFeature_Identifier(tuple.getString("feature_Identifier"));	
		}
		else if(tuple != null)
		{
			//set Id
			if( strict || tuple.getInt("id") != null) this.setId(tuple.getInt("id"));
			if( tuple.getInt("Category_id") != null) this.setId(tuple.getInt("Category_id"));
			//set Feature
			if( strict || tuple.getInt("feature_id") != null) this.setFeature(tuple.getInt("feature_id"));
			if( tuple.getInt("Category_feature_id") != null) this.setFeature(tuple.getInt("Category_feature_id"));
			//alias of xref
			if( tuple.getObject("feature") != null) this.setFeature(tuple.getInt("feature"));
			if( tuple.getObject("Category_feature") != null) this.setFeature(tuple.getInt("Category_feature"));
			//set label for field Feature
			if( strict || tuple.getObject("feature_Identifier") != null) this.setFeature_Identifier(tuple.getString("feature_Identifier"));			
			if( tuple.getObject("Category_feature_Identifier") != null ) this.setFeature_Identifier(tuple.getString("Category_feature_Identifier"));		
		}
		//org.apache.log4j.Logger.getLogger("test").debug("set "+this);
	}
	
	
	
	

	@Override
	public String toString()
	{
		return this.toString(false);
	}
	
	public String toString(boolean verbose)
	{
		String result = "Category(";
		result+= "id='" + getId()+"' ";	
		result+= " feature_id='" + getFeature_Id()+"' ";	
		result+= " feature_identifier='" + getFeature_Identifier()+"' ";
		result += ");";
		return result;

	}

	/**
	 * Get the names of all public properties of Category.
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
			fields.add("feature_id");
		}
		fields.add("feature_identifier");
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
		result.add("id");
		return result;
	}

	@Override
	@Deprecated
	public String getFields(String sep)
	{
		return (""
		+ "id" +sep
		+ "feature" 
		);
	}

	@Override
	public Object getIdValue()
	{
		return get(getIdField());
	}		
	
	
    @Override
	public String getXrefIdFieldName(String fieldName) {
        if (fieldName.equalsIgnoreCase("feature")) {
            return "id";
        }
        
        return null;
    }	

	@Override
	public boolean equals(Object obj) {
   		if (obj == null) { return false; }
   		if (obj == this) { return true; }
   		if (obj.getClass() != getClass()) {
     		return false;
   		}
		Category rhs = (Category) obj;
   		return new org.apache.commons.lang.builder.EqualsBuilder()
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
			Object valueO = getFeature();
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
	public Category create(org.molgenis.util.Tuple tuple) throws Exception
	{
		Category e = new Category();
		e.set(tuple);
		return e;
	}
	

	
}

