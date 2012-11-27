
/* File:        org.molgenis/model/Characteristic.java
 * Generator:   org.molgenis.generators.DataTypeGen 4.0.0-testing
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 

package org.molgenis.model;

/**
 * Characteristic: .
 * @author MOLGENIS generator
 */
@javax.persistence.Entity
//@org.hibernate.search.annotations.Indexed
@javax.persistence.Table(name = "Characteristic", uniqueConstraints={ @javax.persistence.UniqueConstraint( columnNames={ "Identifier" }), @javax.persistence.UniqueConstraint( columnNames={ "Name", "Identifier" } ) }
)


@javax.persistence.Inheritance(strategy=javax.persistence.InheritanceType.JOINED)
@javax.persistence.DiscriminatorColumn(name="DType", discriminatorType=javax.persistence.DiscriminatorType.STRING)
@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
//@EntityListeners({org.molgenis.model.db.CharacteristicEntityListener.class})
public class Characteristic extends org.molgenis.util.AbstractEntity implements org.molgenis.model.Identifiable
{
	// fieldname constants
	public final static String ID = "id";
	public final static String IDENTIFIER = "Identifier";
	public final static String NAME = "Name";
	public final static String __TYPE = "__Type";
	public final static String DESCRIPTION = "description";
	
	//static methods
	/**
	 * Shorthand for db.query(Characteristic.class).
	 */
	public static org.molgenis.framework.db.Query<? extends Characteristic> query(org.molgenis.framework.db.Database db)
	{
		return db.query(Characteristic.class);
	}
	
	/**
	 * Shorthand for db.find(Characteristic.class, org.molgenis.framework.db.QueryRule ... rules).
	 */
	public static java.util.List<? extends Characteristic> find(org.molgenis.framework.db.Database db, org.molgenis.framework.db.QueryRule ... rules) throws org.molgenis.framework.db.DatabaseException
	{
		return db.find(Characteristic.class, rules);
	}	
	
	/**
	 * 
	 */
	public static Characteristic findById(org.molgenis.framework.db.Database db, Integer id) throws org.molgenis.framework.db.DatabaseException
	{
		org.molgenis.framework.db.Query<Characteristic> q = db.query(Characteristic.class);
		q.eq(Characteristic.ID, id);
		java.util.List<Characteristic> result = q.find();
		if(result.size()>0) return result.get(0);
		else return null;
	}

	/**
	 * 
	 */
	public static Characteristic findByIdentifier(org.molgenis.framework.db.Database db, String identifier) throws org.molgenis.framework.db.DatabaseException
	{
		org.molgenis.framework.db.Query<Characteristic> q = db.query(Characteristic.class);
		q.eq(Characteristic.IDENTIFIER, identifier);
		java.util.List<Characteristic> result = q.find();
		if(result.size()>0) return result.get(0);
		else return null;
	}

	/**
	 * 
	 */
	public static Characteristic findByNameIdentifier(org.molgenis.framework.db.Database db, String name, String identifier) throws org.molgenis.framework.db.DatabaseException
	{
		org.molgenis.framework.db.Query<Characteristic> q = db.query(Characteristic.class);
		q.eq(Characteristic.NAME, name);q.eq(Characteristic.IDENTIFIER, identifier);
		java.util.List<Characteristic> result = q.find();
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


	//Identifier[type=string]
//	@org.hibernate.search.annotations.Field(index=org.hibernate.search.annotations.Index.TOKENIZED, store=org.hibernate.search.annotations.Store.NO)
	@javax.persistence.Column(name="Identifier", nullable=false)
	@javax.xml.bind.annotation.XmlElement(name="identifier")
	
				

	@javax.validation.constraints.NotNull
	private String identifier =  null;


	//assign name[type=string]
//	@org.hibernate.search.annotations.Field(index=org.hibernate.search.annotations.Index.TOKENIZED, store=org.hibernate.search.annotations.Store.NO)
	@javax.persistence.Column(name="Name", nullable=false)
	@javax.xml.bind.annotation.XmlElement(name="name")
	
				

	@javax.validation.constraints.NotNull
	private String name =  null;


	//Subtypes have to be set to allow searching[type=enum]
	@javax.persistence.Column(name="DType", nullable=false)            
	@javax.xml.bind.annotation.XmlElement(name="__Type")
	
				

	@javax.validation.constraints.NotNull
	private String __Type =  null;
	@javax.persistence.Transient
	private String __Type_label = null;
	@javax.persistence.Transient
	private java.util.List<org.molgenis.util.ValueLabel> __Type_options = new java.util.ArrayList<org.molgenis.util.ValueLabel>();


	//description[type=text]
//	@javax.persistence.Lob()
	@javax.persistence.Column(name="description", length=16777216)
	
				

	private String description =  null;

	//constructors
	public Characteristic()
	{
		//set the type for a new instance
		set__Type(this.getClass().getSimpleName());
	
		//options for enum __Type
		__Type_options.add(new org.molgenis.util.ValueLabel("Characteristic","Characteristic"));
		__Type_options.add(new org.molgenis.util.ValueLabel("Feature","Feature"));
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
	 * Get the Identifier.
	 * @return identifier.
	 */
	@Override
	public String getIdentifier()
	{
		return this.identifier;
	}
	
	@Deprecated
	public String getIdentifier(org.molgenis.framework.db.Database db)
	{
		throw new UnsupportedOperationException();
	}	
	
	/**
	 * Set the Identifier.
	 * @param identifier
	 */
	@Override
	public void setIdentifier( String identifier)
	{
		
		this.identifier = identifier;
	}

	

	/**
	 * Get the assign name.
	 * @return name.
	 */
	@Override
	public String getName()
	{
		return this.name;
	}
	
	@Deprecated
	public String getName(org.molgenis.framework.db.Database db)
	{
		throw new UnsupportedOperationException();
	}	
	
	/**
	 * Set the assign name.
	 * @param name
	 */
	@Override
	public void setName( String name)
	{
		
		this.name = name;
	}

	

	/**
	 * Get the Subtypes have to be set to allow searching.
	 * @return __Type.
	 */
	@Override
	public String get__Type()
	{
		return this.__Type;
	}
	
	@Deprecated
	public String get__Type(org.molgenis.framework.db.Database db)
	{
		throw new UnsupportedOperationException();
	}	
	
	/**
	 * Set the Subtypes have to be set to allow searching.
	 * @param __Type
	 */
	@Override
	public void set__Type( String __Type)
	{
		
		this.__Type = __Type;
	}

	
	/**
	 * Get tha label for enum __Type.
	 */
	@Override
	public String get__TypeLabel()
	{
		return this.__Type_label;
	}
	
	/**
	 * __Type is enum. This method returns all available enum options.
	 */
	@Override
	public java.util.List<org.molgenis.util.ValueLabel> get__TypeOptions()
	{
		return __Type_options;
	}	
	

	/**
	 * Get the description.
	 * @return description.
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	@Deprecated
	public String getDescription(org.molgenis.framework.db.Database db)
	{
		throw new UnsupportedOperationException();
	}	
	
	/**
	 * Set the description.
	 * @param description
	 */
	public void setDescription( String description)
	{
		
		this.description = description;
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
		return "";
	}	
	
	@Override
	public void validate() throws org.molgenis.framework.db.DatabaseException
	{
		if(this.getId() == null) throw new org.molgenis.framework.db.DatabaseException("required field id is null");
		if(this.getIdentifier() == null) throw new org.molgenis.framework.db.DatabaseException("required field identifier is null");
		if(this.getName() == null) throw new org.molgenis.framework.db.DatabaseException("required field name is null");
		if(this.get__Type() == null) throw new org.molgenis.framework.db.DatabaseException("required field __Type is null");
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
		}
		else if(tuple != null)
		{
			//set Id
			if( strict || tuple.getInt("id") != null) this.setId(tuple.getInt("id"));
			if( tuple.getInt("Characteristic_id") != null) this.setId(tuple.getInt("Characteristic_id"));
			//set Identifier
			if( strict || tuple.getString("Identifier") != null) this.setIdentifier(tuple.getString("Identifier"));
			if( tuple.getString("Characteristic_Identifier") != null) this.setIdentifier(tuple.getString("Characteristic_Identifier"));
			//set Name
			if( strict || tuple.getString("Name") != null) this.setName(tuple.getString("Name"));
			if( tuple.getString("Characteristic_Name") != null) this.setName(tuple.getString("Characteristic_Name"));
			//set __Type
			if( strict || tuple.getString("__Type") != null) this.set__Type(tuple.getString("__Type"));
			if( tuple.getString("Characteristic___Type") != null) this.set__Type(tuple.getString("Characteristic___Type"));
			//set Description
			if( strict || tuple.getString("description") != null) this.setDescription(tuple.getString("description"));
			if( tuple.getString("Characteristic_description") != null) this.setDescription(tuple.getString("Characteristic_description"));
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
		String result = "Characteristic(";
		result+= "id='" + getId()+"' ";	
		result+= "identifier='" + getIdentifier()+"' ";	
		result+= "name='" + getName()+"' ";	
		result+= "__Type='" + get__Type()+"' ";	
		result+= "description='" + getDescription()+"'";	
		result += ");";
		return result;

	}

	/**
	 * Get the names of all public properties of Characteristic.
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
		+ "description" 
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
		Characteristic rhs = (Characteristic) obj;
   		return new org.apache.commons.lang.builder.EqualsBuilder()
		//identifier
				.append(identifier, rhs.getIdentifier())
		//name
				.append(name, rhs.getName())
		//identifier
				.append(identifier, rhs.getIdentifier())
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
				.append(identifier)
				.append(name)
				.append(identifier)
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
			out.write(valueS);
		}
		return out.toString();
	}
	
	@Override
	public Characteristic create(org.molgenis.util.Tuple tuple) throws Exception
	{
		Characteristic e = new Characteristic();
		e.set(tuple);
		return e;
	}
	

	
}

