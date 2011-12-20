	@Override
	//Resolve
	public void resolveForeignKeys(List<${JavaName(entity)}> entities)  throws DatabaseException, ParseException
	{
<#assign has_xrefs=false>	
<#list allFields(entity) as f>
  	<#if (f.type == 'xref' || f.type == 'mref') &&   f.xrefLabelNames[0] != f.xrefFieldName>
  		<#assign has_xrefs=true>	
		//create foreign key map for field '${name(f)}' to ${name(f.xrefEntity)}.${name(f.xrefField)} using ${csv(f.xrefLabelNames)})	
		//we will use a hash of the values to ensure that entities are only queried once	
		final Map<String, QueryRule> ${name(f)}Rules = new LinkedHashMap<String, QueryRule>();
	</#if>
</#list>	
<#if has_xrefs>		
		//create all query rules	
		for(${JavaName(entity)} object: entities)
		{
<#list allFields(entity) as f>
	<#if (f.type == 'xref' || f.type == 'mref') && f.xrefLabelNames[0] != f.xrefFieldName>
		<#if f.xrefLabelNames?size &gt; 1>
			//create xref/mref rule filtering ${f.xrefEntityName} on the combination of labels ${csv(f.xrefLabelNames)}
			{
				List<QueryRule> rules = new ArrayList<QueryRule>();
				String key = "";

				<#if f.type == 'xref'>
				Object label = object.get${JavaName(f)}_${JavaName(label)}();
				<#else>
				for(String label: object.get${JavaName(f)}_${JavaName(label)}())
				</#if>
				{
					<#list f.xrefLabelNames as label>
					rules.add(new QueryRule("${label}", Operator.EQUALS, label));	
					key += 	label;
					</#list>			
					QueryRule complexRule = new QueryRule(rules);
					if(!${name(f)}Rules.containsKey(key))
					{
						${name(f)}Rules.put(key, complexRule);
						${name(f)}Rules.put(key+"_OR_", new QueryRule(Operator.OR));
					}
				}
			}
		<#else>
			//create xref/mref rule filtering ${f.xrefEntityName} on the label ${csv(f.xrefLabelNames)}
			{
				<#if f.type == 'xref'>
				Object label = object.get${JavaName(f)}_${JavaName(f.xrefLabelNames[0])}();
				<#else>
				for(String label: object.get${JavaName(f)}_${JavaName(f.xrefLabelNames[0])}())
				</#if>
				{
					QueryRule xrefFilter = new QueryRule("${f.xrefLabelNames[0]}", Operator.EQUALS, label);
					
					if(label != null && !${name(f)}Rules.containsKey(label))
					{
						${name(f)}Rules.put(""+label, xrefFilter);
						${name(f)}Rules.put(""+label+"_OR_", new QueryRule(Operator.OR));
					}
				}
			}		
		</#if>	
	</#if>
</#list>
		}

<#list allFields(entity) as f>
<#if (f.type == 'xref' || f.type == 'mref') && f.xrefLabelNames[0] != f.xrefFieldName>
		//resolve foreign key field '${name(f)}' to ${name(f.xrefEntity)}.${name(f.xrefField)} using ${csv(f.xrefLabelNames)})
<#if databaseImp = 'JPA'>	
		final java.util.Map<String,${JavaName(f.xrefEntity)}> ${name(f)}_Labels_to_IdMap = new java.util.TreeMap<String,${JavaName(f.xrefEntity)}>();
<#else>		
		final java.util.Map<String,${JavaType(f.xrefField)}> ${name(f)}_Labels_to_IdMap = new java.util.TreeMap<String,${JavaType(f.xrefField)}>();
</#if>
		if(${name(f)}Rules.size() > 0)
		{		
		
			List<${JavaName(f.xrefEntity)}> ${name(f)}List = null;
			try
			{
				${name(f)}List = getDatabase().find(${JavaName(f.xrefEntity)}.class, ${name(f)}Rules.values().toArray(new QueryRule[${name(f)}Rules.values().size()]));
			}
			catch(Exception e)
			{
				// something went wrong while querying for this entities' name field
				// we assume it has no such field, which should have been checked earlier ofcourse
				// regardless, just quit the function now
				throw new DatabaseException(e);
			}
		
			for(${JavaName(f.xrefEntity)} xref :  ${name(f)}List)
			{
				String key = "";
				<#list f.xrefLabelNames as label>
				key += 	xref.get${JavaName(label)}();
				</#list>	
<#if databaseImp = 'JPA'>	
				${name(f)}_Labels_to_IdMap.put(key, xref);
<#else>		
				${name(f)}_Labels_to_IdMap.put(key, xref.get${JavaName(f.xrefField)}());
</#if>	
			}
		}
</#if>
</#list>

		//update objects with the keys
		for(int i = 0; i < entities.size(); i++)
		{
			${JavaName(entity)} object = entities.get(i);		
			<#list allFields(entity) as f>
			<#if (f.type == 'xref'  || f.type == 'mref') && f.xrefLabelNames[0] != f.xrefFieldName>
			//update object using label fields ${csv(f.xrefLabelNames)}
			if(object.get${JavaName(f)}() == null <#if f.type == 'mref'>|| object.get${JavaName(f)}().size() == 0</#if>)
			{
				<#if f.type == 'mref'>
				for(int j = 0; j < object.get${JavaName(f)}_${JavaName(f.xrefLabelNames[0])}().size(); j++)
				</#if>
				{
					String key = "";
					<#list f.xrefLabelNames as label>
					key += 	object.get${JavaName(f)}_${JavaName(label)}()<#if f.type=='mref'>.get(j)</#if>;
					</#list>
					<#if f.type == 'xref'>
					object.set${JavaName(f)}(${name(f)}_Labels_to_IdMap.get(key));
					<#else>
					object.get${JavaName(f)}().add(${name(f)}_Labels_to_IdMap.get(key));
					</#if>
				}
			}
			</#if>
			</#list>	
						
		}
</#if>		
	}	
	
	@Override
	public FieldType getFieldType(String fieldName)
	{
		<#list viewFields(entity) as f>
			if("${name(f)}".equalsIgnoreCase(fieldName) || "${name(f.entity)}.${name(f)}".equalsIgnoreCase(fieldName)) 
				return new ${JavaName(f.type.toString())}Field();
		</#list>
		return new UnknownField();
	}		
	