<#--
Common parts for saving multiplicative references (mrefs) to an entity.
-->	
	/** 
	 * This method queries the link tables to load mref fields. For performance reasons this is done for the whole batch.
	 * As a consequence the number of queries equals the number of mref fields. This may be memory hungry.
	 */
	public void mapMrefs( List<${JavaName(entity)}> entities ) throws DatabaseException			
	{
		try
		{
			//list the ${name(entity)} ids to query
			List<${pkeyJavaType(entity)}> ${name(entity)}Ids = new ArrayList<${pkeyJavaType(entity)}>();
			for(${JavaName(entity)} entity: entities)
			{
				${name(entity)}Ids.add(entity.get${JavaName(pkey(entity))}());
			}
			
<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>			
			//map the ${f.name} mrefs
			List<${JavaName(mref_entity)}> ${name(f)}_mrefs = this.getDatabase().query(${JavaName(mref_entity)}.class).in("${f.mrefLocalid}", ${name(entity)}Ids).sortASC("${pkey(model.getEntity(mref_entity)).name}").find();
			Map<${pkeyJavaType(entity)},List<${pkeyJavaType(f.xrefEntity)}>> ${name(f)}_${name(mref_remote_field)}_map = new LinkedHashMap<${pkeyJavaType(entity)},List<${pkeyJavaType(f.xrefEntity)}>>();
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
			Map<${pkeyJavaType(entity)},List<${JavaType(f.xrefLabels[label_index])}>> ${name(f)}_${label}_map = new LinkedHashMap<${pkeyJavaType(entity)},List<${JavaType(f.xrefLabels[label_index])}>>();
			</#list></#if>
			
			for(${JavaName(mref_entity)} ref: ${name(f)}_mrefs)
			{
				if(${name(f)}_${name(mref_remote_field)}_map.get(ref.get${JavaName(mref_local_field)}()) == null) ${name(f)}_${name(mref_remote_field)}_map.put(ref.get${JavaName(mref_local_field)}(),new ArrayList<${pkeyJavaType(f.xrefEntity)}>()); 
				${name(f)}_${name(mref_remote_field)}_map.get(ref.get${JavaName(mref_local_field)}()).add(ref.get${JavaName(mref_remote_field)}());
				<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
				if(${name(f)}_${label}_map.get(ref.get${JavaName(mref_local_field)}()) == null)	${name(f)}_${label}_map.put(ref.get${JavaName(mref_local_field)}(),new ArrayList<${JavaType(f.xrefLabels[label_index])}>());
				${name(f)}_${label}_map.get(ref.get${JavaName(mref_local_field)}()).add(ref.get${JavaName(mref_remote_field)}_${JavaName(label)}());
				</#list></#if>
			}
			
</#if></#list>
			
			//load the mapped data into the entities
			for(${JavaName(entity)} entity: entities)
			{
				${pkeyJavaType(entity)} id = entity.get${JavaName(pkey(entity))}();
<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>	
				if(${name(f)}_${name(mref_remote_field)}_map.get(id) != null)
				{
					entity.set${JavaName(f)}(${name(f)}_${name(mref_remote_field)}_map.get(id));
				}
				<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
				if(${name(f)}_${label}_map.get(id) != null)
				{
					entity.set${JavaName(f)}_${JavaName(label)}(${name(f)}_${label}_map.get(id));
				}
				</#list></#if>
</#if></#list>			
			}
		} 
		catch(Exception e)
		{	
			throw new DatabaseException(e);
		}
<#--	
		//FIXME: make efficient in batches
<#if entity.hasAncestor()>
		//this.getSuperTypeMapper().mapMrefs((List<${JavaName(entity.getAncestor())}>entities);
</#if>	

<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>	
		//${f}	
		{	
			for (int i = 0; i < entities.size(); i++)
			{
				${JavaName(entity)} entity = entities.get(i);
			
				//retrieve currently known mrefs
				QueryRule rule = new QueryRule( "${name(mref_local_field)}", QueryRule.Operator.EQUALS,  entity.get${JavaName(pkey(entity))}() );
				List<${JavaName(mref_entity)}> existing_mrefs = getDatabase().find( ${JavaName(mref_entity)}.class, rule );		
				//assign ids
				List<Integer> ids = new ArrayList<Integer>();
				<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
				List<String> ${label}List = new ArrayList<String>();
				</#list></#if>
				for(${JavaName(mref_entity)} ref: existing_mrefs)
				{
					ids.add(ref.get${JavaName(mref_remote_field)}());
					<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
					${label}List.add(ref.get${JavaName(mref_remote_field)}_${label}());
					</#list></#if>
				}	
				entity.set${JavaName(f)}(ids);
				<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
				entity.set${JavaName(f)}_${label}(${label}List);
				</#list></#if>			
			}
		}
</#if></#list>
-->
	}		
	
	/**
	 * This method updates the mref entity tables. It deletes existing and adds the new (this to ensure ordering).
	 */		
	public void storeMrefs( List<${JavaName(entity)}> entities ) throws DatabaseException, IOException, ParseException	
	{
		//create an List of ${JavaName(entity)} ids to query for
		List<${pkeyJavaType(entity)}> entityIds = new ArrayList<${pkeyJavaType(entity)}>(); 
		for (${JavaName(entity)} entity : entities) 
		{
			entityIds.add(entity.get${JavaName(pkey(entity))}());		
		}
		
<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >	
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>	
		//delete existing mrefs
		getDatabase().remove(getDatabase().query( ${JavaName(mref_entity)}.class).in("${mref_local_field}", entityIds).find());
		List<${JavaName(mref_entity)}> ${name(mref_entity)}ToAdd = new ArrayList<${JavaName(mref_entity)}>();

</#if></#list>	

		//check for each mref what needs to be added
		for(${JavaName(entity)} entity: entities)
		{
<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >	
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>		
			//remove duplicates using Set
			entity.set${JavaName(f)}(new ArrayList(new LinkedHashSet(entity.get${JavaName(f)}())));
			for(${pkeyJavaType(f.xrefEntity)} id: entity.get${JavaName(f)}())
			{
				${JavaName(mref_entity)} new_mref = new ${JavaName(mref_entity)}();
				new_mref.set${JavaName(mref_local_field )}( entity.get${JavaName(pkey(entity))}() );
				new_mref.set${JavaName(mref_remote_field)}( id );
				${name(mref_entity)}ToAdd.add(new_mref);
			}

<#--	
			//add new mrefs that are not in ${name(mref_entity)}Existing
			for(${pkeyJavaType(f.xrefEntity)} id: entity.get${JavaName(mref_remote_field)}())
			{
				if( ${name(mref_entity)}Existing.get(entity.getId()) == null || ! ${name(mref_entity)}Existing.get(entity.getId()).contains(id) )
				{
					${JavaName(mref_entity)} new_mref = new ${JavaName(mref_entity)}();
					new_mref.set${JavaName(mref_local_field )}( entity.get${JavaName(pkey(entity))}() );
					new_mref.set${JavaName(mref_remote_field)}( id );
					if(!${name(mref_entity)}ToAdd.contains(new_mref)) ${name(mref_entity)}ToAdd.add(new_mref);
				}
			}
			//remove existing mrefs that are not in entity.${name(f)}
			if(${name(mref_entity)}Existing.get(entity.getId()) != null) 
			{
				for(${pkeyJavaType(f.xrefEntity)} id: ${name(mref_entity)}Existing.get(entity.getId()))
				{
					if( entity.get${JavaName(f)}() == null  || !entity.get${JavaName(f)}().contains(id))
					{
						${JavaName(mref_entity)} deprecated_mref = new ${JavaName(mref_entity)}();
						deprecated_mref.set${JavaName(mref_local_field )}( entity.get${JavaName(pkey(entity))}() );
						deprecated_mref.set${JavaName(mref_remote_field)}( id );
						if(!${name(mref_entity)}ToDelete.contains(deprecated_mref)) ${name(mref_entity)}ToDelete.add(deprecated_mref);
					}
				}
			}
-->
</#if></#list>
		}
		
<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >	
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>			
		//process changes to ${mref_entity}
		getDatabase().add( ${name(mref_entity)}ToAdd );
<#--		getDatabase().remove( ${name(mref_entity)}ToDelete ); -->
</#if></#list>
		
<#-- 	//FIXME: make efficient in batches
<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >	
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>	
		{
			// what mrefs to add/delete
			List<${JavaName(mref_entity)}> toDelete = new ArrayList<${JavaName(mref_entity)}>();
			List<${JavaName(mref_entity)}> toAdd = new ArrayList<${JavaName(mref_entity)}>();

			for (${JavaName(entity)} entity : entities)
			{
				//retrieve currently known mrefs
				QueryRule rule = new QueryRule( "${mref_local_field}", QueryRule.Operator.EQUALS, entity.get${JavaName(pkey(entity))}() );
				List<${JavaName(mref_entity)}> existing_mrefs = getDatabase().find( ${JavaName(mref_entity)}.class, rule );

				// check for removals
				List existing_ids = new ArrayList();
				for (${JavaName(mref_entity)} ref : existing_mrefs)
				{
					existing_ids.add(ref.get${JavaName(mref_remote_field)}());
					if (!entity.get${JavaName(f)}().contains( ref.get${JavaName(mref_remote_field)}() ))
					{
						toDelete.add( ref );
					}
				}

				// check for additions
				for (Integer ref : entity.get${JavaName(f)}())
				{
					if(!existing_ids.contains(ref))
					{
						${JavaName(mref_entity)} new_mref = new ${JavaName(mref_entity)}();
						new_mref.set${JavaName(mref_local_field)}( entity.get${JavaName(pkey(entity))}() );
						new_mref.set${JavaName(mref_remote_field)}( ref );
						toAdd.add( new_mref );
					}
				}
			}

			// execute
			getDatabase().add( toAdd );
			getDatabase().remove( toDelete );
		}
</#if></#list>
-->
	}	
	
	public void removeMrefs( List<${JavaName(entity)}> entities ) throws DatabaseException, IOException, ParseException
	{
		//create an list of ${JavaName(entity)} ids to query for
		List<${pkeyJavaType(entity)}> entityIds = new ArrayList<${pkeyJavaType(entity)}>(); 
		for (${JavaName(entity)} entity : entities) 
		{
			entityIds.add(entity.get${JavaName(pkey(entity))}());		
		}	
	
<#list entity.getAllFields() as f><#if f.type.toString() == "mref" >	
<#assign mref_entity = f.mrefName>
<#assign mref_remote_field = f.mrefRemoteid/>
<#assign mref_local_field = f.mrefLocalid/>		
		//remove all ${mref_entity} elements for field entity.${f.name}
		getDatabase().remove( getDatabase().query( ${JavaName(mref_entity)}.class).in("${mref_local_field}", entityIds).find() );
</#if></#list>
	}	