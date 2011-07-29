	@Override
	//Resolve
	public void resolveForeignKeys(List<${JavaName(entity)}> entities)  throws DatabaseException, ParseException
	{
<#assign has_xrefs=false>		
<#list allFields(entity) as f>
  	<#if f.type == 'xref' &&   f.xrefLabelNames[0] != f.xrefFieldName>
  		<#assign has_xrefs=true>	
		//create foreign key map for field '${name(f)}' to ${name(f.xrefEntity)}.${name(f.xrefField)} using ${csv(f.xrefLabelNames)})	
		//we will use a hash of the values to ensure that entities are only queried once	
		final List<QueryRule> ${name(f)}Rules = new ArrayList<QueryRule>();
	</#if>
</#list>	
<#if has_xrefs>		
		//create all query rules	
		for(${JavaName(entity)} object: entities)
		{
<#list allFields(entity) as f>
	<#if f.type == 'xref' && f.xrefLabelNames[0] != f.xrefFieldName>
		<#if f.xrefLabelNames?size &gt; 1>
			//create xref rule filtering on the combination of labels ${csv(f.xrefLabelNames)}
			{
				List<QueryRule> rules = new ArrayList<QueryRule>();
				String key = "";
				<#list f.xrefLabelNames as label>
				rules.add(new QueryRule("${label}", Operator.EQUALS, object.get${JavaName(f)}_${JavaName(label)}()));	
				key += 	object.get${JavaName(f)}_${JavaName(label)}();
				</#list>			
				//${name(f)}Rules.add(complexRule);
				${name(f)}Rules.add(new QueryRule(Operator.OR));
			}
		<#else>
			//create xref rule filtering on the label ${csv(f.xrefLabelNames)}
			{				
				if(object.get${JavaName(f)}_${JavaName(f.xrefLabelNames[0])}()!= null)
				{
					QueryRule xrefFilter = new QueryRule("${f.xrefLabelNames[0]}", Operator.EQUALS, object.get${JavaName(f)}_${JavaName(f.xrefLabelNames[0])}());
					${name(f)}Rules.add(xrefFilter);
					${name(f)}Rules.add(new QueryRule(Operator.OR));
				}
			}
		</#if>	
	</#if>
</#list>
		}

<#list allFields(entity) as f>
<#if f.type == 'xref' && f.xrefLabelNames[0] != f.xrefFieldName>
		//resolve foreign key field '${name(f)}' to ${name(f.xrefEntity)}.${name(f.xrefField)} using ${csv(f.xrefLabelNames)})
<#if databaseImp = 'JPA'>	
		final java.util.Map<String,${JavaName(f.xrefEntity)}> ${name(f)}_Labels_to_IdMap = new java.util.TreeMap<String,${JavaName(f.xrefEntity)}>();
<#else>		
		final java.util.Map<String,${JavaType(f.xrefField)}> ${name(f)}_Labels_to_IdMap = new java.util.TreeMap<String,${JavaType(f.xrefField)}>();
</#if>		

		
		List<${JavaName(f.xrefEntity)}> ${name(f)}List = null;
		try
		{
		
			${name(f)}List = getDatabase().find(${JavaName(f.xrefEntity)}.class, ${name(f)}Rules.toArray(new QueryRule[${name(f)}Rules.size()]));
		}
		catch(Exception e)
		{
			// something went wrong while querying for this entities' name field
			// we assume it has no such field, which should have been checked earlier ofcourse
			// regardless, just quit the function now
			return;
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
	
</#if>
</#list>

		//update objects with the keys
		for(int i = 0; i < entities.size(); i++)
		{
			${JavaName(entity)} object = entities.get(i);		
			<#list allFields(entity) as f>
				<#if f.type == 'xref'  && f.xrefLabelNames[0] != f.xrefFieldName>
			//update object using label fields ${csv(f.xrefLabelNames)}
			if(object.get${JavaName(f)}() == null)
			{
				String key = "";
					<#list f.xrefLabelNames as label>
				key += 	object.get${JavaName(f)}_${JavaName(label)}();
				object.set${JavaName(f)}(${name(f)}_Labels_to_IdMap.get(key));
					</#list>
			}
				</#if>
			</#list>	
						
		}
</#if>		
	}	