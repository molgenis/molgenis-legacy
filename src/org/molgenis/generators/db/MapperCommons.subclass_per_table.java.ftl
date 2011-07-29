	
	public ${JavaName(entity)}Mapper(JDBCDatabase database)
	{
		super(database);
	}
	
	
	@Override
	public JDBCMapper<#if entity.hasAncestor()><${JavaName(entity.getAncestor())}><#else><${JavaName(entity)}></#if> getSuperTypeMapper()
	{
<#if entity.hasAncestor()>
		//${JavaName(entity)} is a subclass of ${JavaName(entity.getAncestor())}
		return (JDBCMapper<#if entity.hasAncestor()><${JavaName(entity.getAncestor())}><#else><${JavaName(entity)}></#if>) new ${JavaName(entity.getAncestor())}Mapper(this.getDatabase());
<#else>
		//${JavaName(entity)} has no superclass
		return null;
</#if>	
	}	
	
	public List<${JavaName(entity)}> createList(int size)
	{
<#if !entity.abstract>
		return new ArrayList<${JavaName(entity)}>(size); 
<#else>
		return null;
</#if>
	}			

	public ${JavaName(entity)} create()
	{
<#if !entity.abstract>	
		return new ${JavaName(entity)}();
<#else>
		return null; //abstract type, cannot be instantiated
</#if>
	}
	
	public String createFindSql(QueryRule ... rules) throws DatabaseException
	{	
	
			
		return "SELECT <#list viewFields(entity) as f>${SqlName(f.entity)}.${SqlName(f)}<#if f_has_next>"
			+", </#if></#list>"<#list viewFields(entity,"xref") as f><#list f.xrefLabelTree.getAllChildren(true) as path><#if path.value.type != "xref">
			//parent is ${path.getParent()}
			+", xref_${path.getParent().name}.${SqlName(path.value.name)} AS ${SqlName(path.name)}"</#if></#list></#list>
			+" FROM ${SqlName(entity)} "<#list superclasses(entity)?reverse as superclass><#if name(superclass) != name(entity)>
			+" INNER JOIN ${SqlName(superclass)} ON (${SqlName(entity)}.${SqlName(pkey(entity))} = ${SqlName(superclass)}.${SqlName(pkey(entity))})"</#if></#list>
<#--this piece of dark magic that attaches all xref_label possibilities -->

<#list viewFields(entity,"xref") as f>
			
			//label for ${f.name}=${csv(f.xrefLabelNames)}
<#assign pathlist = []/>			
<#list f.xrefLabelTree.getAllChildren(true) as path>
//path==${path.name}. type==${path.value.type}.
<#if path.value.type != "xref" && !pathlist?seq_contains(path.getParent().name)>
//in if path.value.type != "xref" && !pathlist?seq_contains(path.getParent().name)
<#assign pathlist = pathlist + [path.getParent().name]/>
<#if !path.getParent().parent?exists>
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.getParent().name} " 
			+" ON xref_${SqlName(path.getParent().name)}.${SqlName(pkey(path.value.entity))} = ${SqlName(f.entity)}.${SqlName(f.name)}"
<#elseif path.value.entity == path.getParent().value.xrefEntity>
			//linked via ${path.getParent().value.entity.name}.${path.getParent().value.name}
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.getParent().name} " 
			+" ON xref_${SqlName(path.getParent().name)}.${SqlName(pkey(path.value.entity))} = xref_${SqlName(path.getParent().parent.name)}.${SqlName(path.getParent().value.name)}"
<#else>
			//linked ${path.value.entity.name}.${path.value.name} via superclass	
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.name}"
			+" ON xref_${SqlName(path.name)}.${SqlName(path.value.name)} = xref_${path.name}.${SqlName(pkey(path.value.entity))}"
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.getParent().name} " 					
			+" ON xref_${SqlName(path.getParent().name)}.${SqlName(path.getParent().value)} = xref_${SqlName(path.getParent().parent.name)}.${SqlName(pkey(path.value.entity))}"			
</#if></#if></#list>
</#list>;

	}	

	public String createCountSql(QueryRule ... rules) throws DatabaseException
	{	
		return "select count(*) as num_rows " 
			  +" FROM ${SqlName(entity)} "<#list superclasses(entity)?reverse as superclass><#if name(superclass) != name(entity)>
			  +" INNER JOIN ${SqlName(superclass)} ON (${SqlName(entity)}.${SqlName(pkey(entity))} = ${SqlName(superclass)}.${SqlName(pkey(entity))})"</#if></#list>
<#--this piece of dark magic that attaches all xref_label possibilities -->
<#list viewFields(entity,"xref") as f>
			
			//label for ${f.name}=${csv(f.xrefLabelNames)}
<#assign pathlist = []/>			
<#list f.xrefLabelTree.getAllChildren(true) as path>
//${path.name}
<#if path.value.type != "xref" && !pathlist?seq_contains(path.getParent().name)>
<#assign pathlist = pathlist + [path.getParent().name]/>
<#if !path.getParent().parent?exists>
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.getParent().name} " 
			+" ON xref_${SqlName(path.getParent().name)}.${SqlName(pkey(path.value.entity))} = ${SqlName(f.entity)}.${SqlName(f.name)}"
<#elseif path.value.entity == path.getParent().value.xrefEntity>
			//linked via ${path.getParent().value.entity.name}.${path.getParent().value.name}
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.getParent().name} " 
			+" ON xref_${SqlName(path.getParent().name)}.${SqlName(pkey(path.value.entity))} = xref_${SqlName(path.getParent().parent.name)}.${SqlName(path.getParent().value.name)}"
<#else>
			//linked ${path.value.entity.name}.${path.value.name} via superclass	
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.name}"
			+" ON xref_${SqlName(path.name)}.${SqlName(path.value.name)} = xref_${path.name}.${SqlName(pkey(path.value.entity))}"
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.getParent().name} " 					
			+" ON xref_${SqlName(path.getParent().name)}.${SqlName(path.getParent().value)} = xref_${SqlName(path.getParent().parent.name)}.${SqlName(pkey(path.value.entity))}"			
</#if></#if></#list>
</#list>;		  	  
			  
	}
	
	@Override
	public String getTableFieldName(String fieldName)
	{
		<#list viewFields(entity) as f>
		<#assign type= f.type>
		if("${f.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f.entity)}.${SqlName(f)}";
		if("${entity.name}_${f.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f.entity)}.${SqlName(f)}";
		</#list>	
		<#list viewFields(entity,"xref") as f>	
		if("${f.name}_${f.xrefField.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f.entity)}.${SqlName(f)}";
		if("${entity.name}_${f.name}_${f.xrefField.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f.entity)}.${SqlName(f)}";
		<#list f.xrefLabelTree.getTreeElements()?values as path><#if path.value.type != "xref">
		if("${path.name}".equalsIgnoreCase(fieldName)) return "xref_${path.getParent().name}.${SqlName(path.value.name)}";	
		if("${entity.name}_${path.name}".equalsIgnoreCase(fieldName)) return "xref_${path.getParent().name}.${SqlName(path.value.name)}";
		</#if></#list></#list>
		<#--
		<#assign xref_entity = f.xrefEntity/> 
		<#assign xref_field = f.xrefField/>
		//alias for query on id field of xref entity
		if("${name(f)}_${name(xref_field)}".equalsIgnoreCase(fieldName)) return "${SqlName(f.entity)}.${SqlName(f)}";
		//alias(es) for query on label of the xref entity
			<#list f.xrefLabelNames as label>
		if("${name(f)}_${name(label)}".equalsIgnoreCase(fieldName)) return "xref_${label}.${SqlName(label)}";
			</#list>
		</#list>
		-->		  		
		return fieldName;
	}
	
	@Override
	public org.molgenis.framework.db.jdbc.ColumnInfo.Type getFieldType(String fieldName)
	{
		<#list viewFields(entity) as f>
		<#assign type= f.type>
		<#if type == "user" || type == "xref" || type == "mref">		
		<#assign type = f.xrefField.type/>
		if("${name(f)}".equalsIgnoreCase(fieldName) || "${name(f.entity)}.${name(f)}".equalsIgnoreCase(fieldName)) return org.molgenis.framework.db.jdbc.ColumnInfo.Type.${type?upper_case};
		<#list f.xrefLabelNames as xref_label>
		if("${name(f)}_${name(xref_label)}".equalsIgnoreCase(fieldName) 
		    || "${name(f.entity)}.${name(f)}_${name(xref_label)}".equalsIgnoreCase(fieldName))
		{
			return org.molgenis.framework.db.jdbc.ColumnInfo.Type.STRING;
		}
		</#list>
		<#else>		
		if("${name(f)}".equalsIgnoreCase(fieldName) || "${name(f.entity)}.${name(f)}".equalsIgnoreCase(fieldName)) return org.molgenis.framework.db.jdbc.ColumnInfo.Type.${type?upper_case};
		</#if>
		</#list>
		return org.molgenis.framework.db.jdbc.ColumnInfo.Type.STRING;
	}		
	
<#include "MapperCommons.java.ftl">
	
	public void setAutogeneratedKey(int i, ${JavaName(entity)} entity)
	{
<#list keyFields(entity) as field><#if field.auto && field.type.toString() == "int">
		entity.set${JavaName(field)}(i);
</#if></#list>
	}
	
	@Override
	public QueryRule rewriteMrefRule(Database db, QueryRule rule) throws DatabaseException
	{
<#assign else = false>
<#list entity.getAllFields() as field>
	<#if field.type = "mref">	
		<#if else>else </#if>if("${field.name}".equalsIgnoreCase(rule.getField()))
		{
			// replace with id filter based on the many-to-many links in
			// ${field.getMrefName()}
			List<${JavaName(field.getMrefName())}> mref_mapping_entities = db.find(${JavaName(field.getMrefName())}.class, new QueryRule(
					"${SqlName(field.mrefRemoteid)}", rule.getOperator(), rule.getValue()));
			if (mref_mapping_entities.size() > 0)
			{
				List<${JavaType(pkey(entity))}> mref_ids = new ArrayList<${JavaType(pkey(entity))}>();
				for (${JavaName(field.getMrefName())} mref : mref_mapping_entities) mref_ids.add(mref.get${JavaName(field.mrefLocalid)}_${JavaName(pkey(field.xrefEntity))}());
				return new QueryRule("${SqlName(pkey(entity))}", Operator.IN, mref_ids);
			}		
			else
			{
				// no records to be shown
				return new QueryRule("${SqlName(pkey(entity))}", Operator.EQUALS, Integer.MIN_VALUE);
			}			
		}
		<#list field.xrefLabelNames as label>
		else if("${field.name}_${label}".equalsIgnoreCase(rule.getField()))
		{
			// replace with id filter based on the many-to-many links in
			// ${field.getMrefName()}
			List<${JavaName(field.getMrefName())}> mref_mapping_entities = db.find(${JavaName(field.getMrefName())}.class, new QueryRule(
					"${SqlName(field.name+"_"+label)}", rule.getOperator(), rule.getValue()));
			if (mref_mapping_entities.size() > 0)
			{
				List<${JavaType(pkey(entity))}> mref_ids = new ArrayList<${JavaType(pkey(entity))}>();
				for (${JavaName(field.getMrefName())} mref : mref_mapping_entities) mref_ids.add(mref.get${JavaName(field.mrefLocalid)}_${JavaName(pkey(entity))}());
				return new QueryRule("${SqlName(pkey(entity))}", Operator.IN, mref_ids);
			}		
			else
			{
				// no records to be shown
				return new QueryRule("${SqlName(pkey(entity))}", Operator.EQUALS, Integer.MIN_VALUE);
			}
		}
		</#list>
		<#assign else = true>				
		</#if>
		</#list>
		<#if else>else</#if>
		{
			return rule;
		}
	}
	