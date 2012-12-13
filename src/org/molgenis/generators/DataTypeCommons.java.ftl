	public void set( org.molgenis.util.tuple.Tuple tuple, boolean strict ) throws Exception
	{
<#list allFields(entity) as f>
	<#assign type_label = f.getType().toString()>
	<#if f.type == "mref">
		//set ${JavaName(f)}
		if( tuple.get("${f.name?lower_case}")!= null || tuple.get("${entity.name?lower_case}_${f.name?lower_case}")!= null) 
		{
			java.util.List<${type(f.xrefField)}> values = new java.util.ArrayList<${type(f.xrefField)}>();
			java.util.List<?> mrefs = tuple.getList("${f.name?lower_case}");
			if(tuple.get("${entity.name?lower_case}_${f.name?lower_case}")!= null) mrefs = tuple.getList("${entity.name?lower_case}_${f.name?lower_case}");
			if(mrefs != null) for(Object ref: mrefs)
			{
			<#if databaseImp = 'JPA'>
				if(ref instanceof String)
					values.add(${type(xrefField(model,f))}.parse${settertype(xrefField(model,f))}((String)ref));
				else if(ref instanceof org.molgenis.util.AbstractEntity) 	
					values.add((${type(xrefField(model,f))})((org.molgenis.util.AbstractEntity)ref).getIdValue() );
				else
					values.add((${type(xrefField(model,f))})ref);		
			<#else>
			  	<#if JavaType(f.xrefField) == "String" >
			  		values.add((${JavaType(f.xrefField)})ref);
			  	<#else>
			  		values.add(${type(f.xrefField)}.parse${settertype(f.xrefField)}((ref.toString())));
			  	</#if>
		  	</#if>
			}											
			this.set${JavaName(f)}_${JavaName(f.xrefField)}( values );
		}
	<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
		//set labels ${label} for mref field ${JavaName(f)}	
		if( tuple.get("${f.name?lower_case}_${label?lower_case}")!= null || tuple.get("${entity.name?lower_case}_${f.name?lower_case}_${label?lower_case}")!= null) 
		{
			java.util.List<${type(f.xrefLabels[label_index])}> values = new java.util.ArrayList<${type(f.xrefLabels[label_index])}>();
			java.util.List<?> mrefs = tuple.getList("${f.name?lower_case}_${label?lower_case}");
			if(tuple.get("${entity.name?lower_case}_${f.name?lower_case}_${label?lower_case}")!= null) mrefs = tuple.getList("${entity.name?lower_case}_${f.name?lower_case}_${label?lower_case}");
			
			if(mrefs != null) 
				for(Object ref: mrefs)
				{
				<#if type(f.xrefLabels[label_index]) == "String">
					<#-- values.add(${type(f.xrefLabels[label_index])}.parse${settertype(f.xrefLabels[label_index])}(ref.toString())); -->
					String[] refs = ref.toString().split("\\|");
					for(String r : refs) {
						values.add(r);	
					}						
				<#else>
			  		<#if JavaType(f.xrefField) == "String" >
			  		values.add((${JavaType(f.xrefField)})ref);
			  		<#else>
			  		values.add(${type(f.xrefField)}.parse${settertype(f.xrefField)}((ref.toString())));
			  		</#if>						
				</#if>
				}							
			this.set${JavaName(f)}_${JavaName(label)}( values );			
		}	
	</#list></#if>					
	<#else>
		//set ${JavaName(f)}
		<#if f.type == "xref">	
		if( strict || tuple.get${settertype(f)}("${f.name?lower_case}_${f.xrefField.name?lower_case}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${f.name?lower_case}_${f.xrefField.name?lower_case}"));
		if( tuple.get${settertype(f)}("${entity.name?lower_case}_${f.name?lower_case}_${f.xrefField.name?lower_case}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${entity.name?lower_case}_${f.name?lower_case}_${f.xrefField.name?lower_case}"));
		//alias of xref
		<#if databaseImp = 'JPA'>
		if( tuple.get("${f.name}") != null) { 
			if(org.molgenis.util.AbstractEntity.isObjectRepresentation(tuple.get("${f.name?lower_case}").toString())) {
				${f.xrefEntity.namespace}.${JavaName(f.xrefEntity)} instance = org.molgenis.util.AbstractEntity.setValuesFromString((String)tuple.get("${f.name?lower_case}"), ${f.xrefEntity.namespace}.${JavaName(f.xrefEntity)}.class);
				this.set${JavaName(f)}(instance);				
			} else {
				this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f.xrefField)?lower_case}("investigation")); // FIXME bug?!
			}
		}
		if( tuple.get("${entity.name?lower_case}_${f.name?lower_case}") != null)
			this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f.xrefField)}("${entity.name?lower_case}_${f.name?lower_case}"));			
		
		if( tuple.get("${entity.name?lower_case}.${f.name?lower_case}") != null) 
			this.set${JavaName(f)}((${f.xrefEntity.namespace}.${JavaName(f.xrefEntity)})tuple.get("${entity.name?lower_case}.${f.name?lower_case}_${f.xrefField.name?lower_case}"));
		<#else>			
		if( tuple.get("${f.name?lower_case}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${f.name?lower_case}"));
		if( tuple.get("${entity.name?lower_case}_${f.name?lower_case}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${entity.name?lower_case}_${f.name?lower_case}"));
		</#if>
		//set label for field ${JavaName(f)}
		<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
		if( strict || tuple.get("${f.name?lower_case}_${label?lower_case}") != null) this.set${JavaName(f)}_${JavaName(label)}(tuple.get${settertype(f.xrefLabels[label_index])}("${f.name?lower_case}_${label?lower_case}"));			
		if( tuple.get("${entity.name?lower_case}_${f.name?lower_case}_${label?lower_case}") != null ) this.set${JavaName(f)}_${JavaName(label)}(tuple.get${settertype(f.xrefLabels[label_index])}("${entity.name?lower_case}_${f.name?lower_case}_${label?lower_case}"));		
		</#list></#if>
		<#elseif f.type == "nsequence">
		if( strict || tuple.getNSequence("${f.name?lower_case}") != null)this.set${JavaName(f)}(tuple.getNSequence("${f.name?lower_case}")); //FIXME
		if(tuple.getNSequence("${entity.name?lower_case}_${f.name?lower_case}") != null) this.set${JavaName(f)}(tuple.getNSequence("${entity.name?lower_case}_${f.name?lower_case}")); //FIXME
		<#elseif f.type == "onoff">
		if( strict || tuple.getOnoff("${f.name?lower_case}") != null) this.set${JavaName(f)}(tuple.getOnoff("${f.name?lower_case}")); //FIXME
		if( tuple.getOnoff("${entity.name?lower_case}_${f.name?lower_case}") != null) this.set${JavaName(f)}(tuple.getOnoff("${entity.name?lower_case}_${f.name?lower_case}")); //FIXME
		<#else>
		if( strict || tuple.get${settertype(f)}("${f.name?lower_case}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${f.name?lower_case}"));
		if( tuple.get${settertype(f)}("${entity.name?lower_case}_${f.name?lower_case}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${entity.name?lower_case}_${f.name?lower_case}"));
		</#if>
		<#if f.type == "file" || f.type=="image">
		this.set${JavaName(f)}AttachedFile(new java.io.File(tuple.getString("filefor_${f.name?lower_case}")));
		if(tuple.getString("filefor_${entity.name?lower_case}_${f.name?lower_case}") != null) this.set${JavaName(f)}AttachedFile(new java.io.File(tuple.getString("filefor_${entity.name?lower_case}_${f.name?lower_case}"))); //FIXME filefor hack
		</#if>						
	</#if>
</#list>
	}
