	
	public ${JavaName(entity)}Mapper(JDBCDatabase database)
	{
		super(database);
	}
	
	
	@Override
	public JDBCMapper<#if entity.hasAncestor()><${JavaName(entity.getAncestor())}><#else><${JavaName(entity)}></#if> getSuperTypeMapper()
	{
<#if entity.hasAncestor()>
		//${JavaName(entity)} is a subclass of ${JavaName(entity.getAncestor())}
		return (JDBCMapper) new ${JavaName(entity.getAncestor())}Mapper(this.getDatabase());
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
			+", xref_${path.parent.name}.${SqlName(path.value.name)} AS ${SqlName(path.name)}"</#if></#list></#list>
			+" FROM ${SqlName(entity)} "<#list superclasses(entity)?reverse as superclass><#if name(superclass) != name(entity)>
			+" INNER JOIN ${SqlName(superclass)} ON (${SqlName(entity)}.${SqlName(pkey(entity))} = ${SqlName(superclass)}.${SqlName(pkey(entity))})"</#if></#list>
<#--this piece of dark magic that attaches all xref_label possibilities -->

<#list viewFields(entity,"xref") as f>
			
			//label for ${f.name}=${csv(f.xrefLabelNames)}
<#assign pathlist = []/>			
<#list f.xrefLabelTree.getAllChildren(true) as path>
//${path.name}
<#if path.value.type != "xref" && !pathlist?seq_contains(path.parent.name)>
<#assign pathlist = pathlist + [path.parent.name]/>
<#if !path.parent.parent?exists>
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.parent.name} " 
			+" ON xref_${SqlName(path.parent.name)}.${SqlName(pkey(path.value.entity))} = ${SqlName(f.entity)}.${SqlName(f.name)}"
<#elseif path.value.entity == path.parent.value.xrefEntity>
			//linked via ${path.parent.value.entity.name}.${path.parent.value.name}
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.parent.name} " 
			+" ON xref_${SqlName(path.parent.name)}.${SqlName(pkey(path.value.entity))} = xref_${SqlName(path.parent.parent.name)}.${SqlName(path.parent.value.name)}"
<#else>
			//linked ${path.value.entity.name}.${path.value.name} via superclass	
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.name}"
			+" ON xref_${SqlName(path.name)}.${SqlName(path.value.name)} = xref_${path.name}.${SqlName(pkey(path.value.entity))}"
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.parent.name} " 					
			+" ON xref_${SqlName(path.parent.name)}.${SqlName(path.parent.value)} = xref_${SqlName(path.parent.parent.name)}.${SqlName(pkey(path.value.entity))}"			
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
<#if path.value.type != "xref" && !pathlist?seq_contains(path.parent.name)>
<#assign pathlist = pathlist + [path.parent.name]/>
<#if !path.parent.parent?exists>
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.parent.name} " 
			+" ON xref_${SqlName(path.parent.name)}.${SqlName(pkey(path.value.entity))} = ${SqlName(f.entity)}.${SqlName(f.name)}"
<#elseif path.value.entity == path.parent.value.xrefEntity>
			//linked via ${path.parent.value.entity.name}.${path.parent.value.name}
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.parent.name} " 
			+" ON xref_${SqlName(path.parent.name)}.${SqlName(pkey(path.value.entity))} = xref_${SqlName(path.parent.parent.name)}.${SqlName(path.parent.value.name)}"
<#else>
			//linked ${path.value.entity.name}.${path.value.name} via superclass	
			+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.name}"
			+" ON xref_${SqlName(path.name)}.${SqlName(path.value.name)} = xref_${path.name}.${SqlName(pkey(path.value.entity))}"
		   	+" LEFT JOIN ${SqlName(path.value.entity)} AS xref_${path.parent.name} " 					
			+" ON xref_${SqlName(path.parent.name)}.${SqlName(path.parent.value)} = xref_${SqlName(path.parent.parent.name)}.${SqlName(pkey(path.value.entity))}"			
</#if></#if></#list>
</#list>;		  	  
			  
	}
	
	@Override
	public String getTableFieldName(String fieldName)
	{
		<#list viewFields(entity) as f>
		<#assign type= f.type>
		if("${f.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f.entity)}.${SqlName(f)}";
		</#list>	
		<#list viewFields(entity,"xref") as f>	
		if("${f.name}_${f.xrefField.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f.entity)}.${SqlName(f)}";	
		<#list f.xrefLabelTree.getTreeElements()?values as path><#if path.value.type != "xref">
		if("${path.name}".equalsIgnoreCase(fieldName)) return "xref_${path.parent.name}.${SqlName(path.value.name)}";	
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
	public Type getFieldType(String fieldName)
	{
		<#list viewFields(entity) as f>
		<#assign type= f.type>
		<#if type == "user" || type == "xref" || type == "mref">		
		<#assign type = f.xrefField.type/>
		if("${name(f)}".equalsIgnoreCase(fieldName) || "${name(f.entity)}.${name(f)}".equalsIgnoreCase(fieldName)) return Type.${type?upper_case};
		if("${name(f)}_${name(xref_label)}".equalsIgnoreCase(fieldName) || "${name(f.entity)}.${name(f)}_${name(xref_label)}".equalsIgnoreCase(fieldName)) return Type.STRING;
		<#else>		
		if("${name(f)}".equalsIgnoreCase(fieldName) || "${name(f.entity)}.${name(f)}".equalsIgnoreCase(fieldName)) return Type.${type?upper_case};
		</#if>
		</#list>
		return Type.STRING;
	}		
	
	@Override
	public void resolveForeignKeys(List<${JavaName(entity)}> entities)  throws DatabaseException, ParseException
	{
<#assign has_xrefs=false>		
<#list allFields(entity) as f><#if f.type == 'xref' &&   f.xrefLabelNames[0] != f.xrefFieldName><#assign has_xrefs=true>	
		//create foreign key map for field '${name(f)}' to ${name(f.xrefEntity)}.${name(f.xrefField)} using ${csv(f.xrefLabelNames)})	
		//we will use a hash of the values to ensure that entities are only queried once	
		final java.util.Map<String,QueryRule> ${name(f)}Rules = new java.util.TreeMap<String,QueryRule>();
</#if></#list>	
<#if has_xrefs>		
		//create all query rules	
		for(${JavaName(entity)} object: entities)
		{
<#list allFields(entity) as f><#if f.type == 'xref' && f.xrefLabelNames[0] != f.xrefFieldName>
		<#if f.xrefLabelNames?size &gt; 1>
			//create xref rule filtering on the combination of labels ${csv(f.xrefLabelNames)}
			{
				List<QueryRule> rules = new ArrayList<QueryRule>();
				String key = "";
				<#list f.xrefLabelNames as label>
				rules.add(new QueryRule("${f.name}_${label}", Operator.EQUALS, object.get${JavaName(f)}_${label}()));	
				key += 	object.get${JavaName(f)}_${label}();
				</#list>			
				QueryRule complexRule = new QueryRule(rules);
				if(!${name(f)}Rules.containsKey(key))
				{
					${name(f)}Rules.put(key, complexRule);
					${name(f)}Rules.put(key+"_OR_", new QueryRule(Operator.OR));
				}
			}
		<#else>
			//create xref rule filtering on the label ${csv(f.xrefLabelNames)}
			{
				QueryRule xrefFilter = new QueryRule("${f.xrefLabelNames[0]}", Operator.EQUALS, object.get${JavaName(f)}_${f.xrefLabelNames[0]}());
				if(object.get${JavaName(f)}() == null && object.get${JavaName(f)}_${f.xrefLabelNames[0]}()!= null && !${name(f)}Rules.containsKey(object.get${JavaName(f)}_${f.xrefLabelNames[0]}()))
				{
					${name(f)}Rules.put(""+object.get${JavaName(f)}_${f.xrefLabelNames[0]}(), xrefFilter);
					${name(f)}Rules.put(""+object.get${JavaName(f)}_${f.xrefLabelNames[0]}()+"_OR_", new QueryRule(Operator.OR));
				}
			}
		</#if>	
</#if></#list>
		}

<#list allFields(entity) as f><#if f.type == 'xref' && f.xrefLabelNames[0] != f.xrefFieldName>
		//resolve foreign key field '${name(f)}' to ${name(f.xrefEntity)}.${name(f.xrefField)} using ${csv(f.xrefLabelNames)})
		final java.util.Map<String,${JavaType(f.xrefField)}> ${name(f)}_Labels_to_IdMap = new java.util.TreeMap<String,${JavaType(f.xrefField)}>();
		
		List<${JavaName(f.xrefEntity)}> ${name(f)}List = getDatabase().find(${JavaName(f.xrefEntity)}.class, ${name(f)}Rules.values().toArray(new QueryRule[${name(f)}Rules.values().size()]));
		for(${JavaName(f.xrefEntity)} xref :  ${name(f)}List)
		{
			String key = "";
			<#list f.xrefLabelNames as label>
			key += 	xref.get${JavaName(label)}();
			</#list>		
			${name(f)}_Labels_to_IdMap.put(key, xref.get${JavaName(f.xrefField)}());
		}
	
</#if></#list>

		//update objects with the keys
		for(int i = 0; i < entities.size(); i++)
		{
			${JavaName(entity)} object = entities.get(i);		
			<#list allFields(entity) as f><#if f.type == 'xref'  && f.xrefLabelNames[0] != f.xrefFieldName>
			//update object using label fields ${csv(f.xrefLabelNames)}
			if(object.get${JavaName(f)}() == null)
			{
				String key = "";
				<#list f.xrefLabelNames as label>
				key += 	object.get${JavaName(f)}_${label}();
				object.set${JavaName(f)}(${name(f)}_Labels_to_IdMap.get(key));
				</#list>
			}
			</#if></#list>	
						
		}
</#if>		
	}	
	
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
				for (${JavaName(field.getMrefName())} mref : mref_mapping_entities) mref_ids.add(mref.get${JavaName(field.mrefLocalid)}());
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
				for (${JavaName(field.getMrefName())} mref : mref_mapping_entities) mref_ids.add(mref.get${JavaName(field.mrefLocalid)}());
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
	