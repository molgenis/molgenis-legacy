	
	
	//@Implements
	public void set( Tuple tuple, boolean strict )  throws Exception
	{
		//optimization :-(
		if(tuple instanceof ResultSetTuple)
		{
	<#list allFields(entity) as f>
		<#assign type_label = f.getType().toString()>
		<#if f.type == "mref">
			//mrefs can not be directly retrieved
			//set ${JavaName(f)}			
		<#else>
			//set ${JavaName(f)}
			<#if f.type == "nsequence">
			this.set${JavaName(f)}(tuple.getNSequence("${f.name}"));
			<#elseif f.type == "onoff">
			this.set${JavaName(f)}(tuple.getOnoff("${f.name}"));
			<#else>
			this.set${JavaName(f)}(tuple.get${settertype(f)}("${f.name}"));
		</#if>
		<#if f.type == "file"  || type_label=="image" >
		</#if>			
		<#if f.type == "xref">			
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>		
			//set label ${label} for xref field ${JavaName(f)}
			this.set${JavaName(f)}_${JavaName(label)}(tuple.get${settertype(f.xrefLabels[label_index])}("${f.name}_${label}"));	
			</#list></#if>			
		</#if>				
		</#if>
	</#list>		
		}
		else if(tuple != null)
		{
	<#list allFields(entity) as f>
		<#assign type_label = f.getType().toString()>
		<#if f.type == "mref">
			//set ${JavaName(f)}
			if( tuple.getObject("${f.name}")!= null || tuple.getObject("${entity.name}_${f.name}")!= null) 
			{
				java.util.List<${type(f.xrefField)}> values = new java.util.ArrayList<${type(f.xrefField)}>();
				java.util.List<?> mrefs = tuple.getList("${f.name}");
				if(tuple.getObject("${entity.name}_${f.name}")!= null) mrefs = tuple.getList("${entity.name}_${f.name}");
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
<#if databaseImp = 'JPA'>				
				this.set${JavaName(f)}_${JavaName(f.xrefField)}( values );
<#else>				
				this.set${JavaName(f)}( values );
</#if>			
			}
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
			//set labels ${label} for mref field ${JavaName(f)}	
			if( tuple.getObject("${f.name}_${label}")!= null || tuple.getObject("${entity.name}_${f.name}_${label}")!= null) 
			{
				java.util.List<${type(f.xrefLabels[label_index])}> values = new java.util.ArrayList<${type(f.xrefLabels[label_index])}>();
				java.util.List<?> mrefs = tuple.getList("${f.name}_${label}");
				if(tuple.getObject("${entity.name}_${f.name}_${label}")!= null) mrefs = tuple.getList("${entity.name}_${f.name}_${label}");
				
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
			if( strict || tuple.get${settertype(f)}("${f.name}_${f.xrefField.name}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${f.name}_${f.xrefField.name}"));
			if( tuple.get${settertype(f)}("${entity.name}_${f.name}_${f.xrefField.name}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${entity.name}_${f.name}_${f.xrefField.name}"));
			//alias of xref
			<#if databaseImp = 'JPA'>
			if( tuple.getObject("${f.name}") != null) { 
				if(AbstractEntity.isObjectRepresentation(tuple.getObject("${f.name}").toString())) {
					${JavaName(f.xrefEntity)} instance = AbstractEntity.setValuesFromString((String)tuple.getObject("${f.name}"), ${JavaName(f.xrefEntity)}.class);
					this.set${JavaName(f)}(instance);				
				} else {
					this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f.xrefField)}("investigation"));
				}
			}
			if( tuple.getObject("${entity.name}_${f.name}") != null)
				this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f.xrefField)}("${entity.name}_${f.name}"));			
			
			if( tuple.getObject("${entity.name}.${f.name}") != null) 
				this.set${JavaName(f)}((${JavaName(f.xrefEntity)})tuple.getObject("${entity.name}.${f.name}_${f.xrefField.name}"));
			<#else>			
			if( tuple.getObject("${f.name}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${f.name}"));
			if( tuple.getObject("${entity.name}_${f.name}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${entity.name}_${f.name}"));
			</#if>
			//set label for field ${JavaName(f)}
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
			if( strict || tuple.getObject("${f.name}_${label}") != null) this.set${JavaName(f)}_${JavaName(label)}(tuple.get${settertype(f.xrefLabels[label_index])}("${f.name}_${label}"));			
			if( tuple.getObject("${entity.name}_${f.name}_${label}") != null ) this.set${JavaName(f)}_${JavaName(label)}(tuple.get${settertype(f.xrefLabels[label_index])}("${entity.name}_${f.name}_${label}"));		
			</#list></#if>
			<#elseif f.type == "nsequence">
			if( strict || tuple.getNSequence("${f.name}") != null)this.set${JavaName(f)}(tuple.getNSequence("${f.name}"));
			if(tuple.getNSequence("${entity.name}_${f.name}") != null) this.set${JavaName(f)}(tuple.getNSequence("${entity.name}_${f.name}"));
			<#elseif f.type == "onoff">
			if( strict || tuple.getOnoff("${f.name}") != null) this.set${JavaName(f)}(tuple.getOnoff("${f.name}"));
			if( tuple.getOnoff("${entity.name}_${f.name}") != null) this.set${JavaName(f)}(tuple.getOnoff("${entity.name}_${f.name}"));
			<#else>
			if( strict || tuple.get${settertype(f)}("${f.name}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${f.name}"));
			if( tuple.get${settertype(f)}("${entity.name}_${f.name}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${entity.name}_${f.name}"));
			</#if>
			<#if f.type == "file" || f.type=="image">
			this.set${JavaName(f)}AttachedFile(tuple.getFile("filefor_${f.name}"));
			if(tuple.getFile("filefor_${entity.name}_${f.name}") != null) this.set${JavaName(f)}AttachedFile(tuple.getFile("filefor_${entity.name}_${f.name}"));
			</#if>						
		</#if>
	</#list>
	<#--if the label itself is not (completely) set it can use the value of another field as default-->
	<#list allFields(entity) as f>
		<#if (f.type == "xref" || f.type == "mref") && f.xrefLabelNames[0] != f.xrefFieldName && f.xrefLabelNames?size &gt; 1>
			<#assign all_labels = f.allPossibleXrefLabels()/>
			//MAGIC guessing of xref_labels:
			//if a some parts of the secondary key for '${f.name}' are set and some not it will search if it can use another label to complete it
			//e.g if protocol_name is set, but protocol_investigation_name is not set it will look for investigation_name in the other labels to copy
			//caveat: it may be left empty on purpose, hence tuple headers should be checked and not null constraints
			if( (<#list f.xrefLabelNames as label><#if label_index &gt; 0>||</#if> this.get${JavaName(f)}_${JavaName(label)}() == null</#list>) && (<#list f.xrefLabelNames as label><#if label_index &gt; 0>||</#if> this.get${JavaName(f)}_${JavaName(label)}() != null</#list>) )
			{
				<#--
				<#list f.xrefLabelNames as label>
				//guess the value for ${label} from other labels, if not set to null on purpose in the tuple
				if( this.get${JavaName(f)}_${JavaName(label)}() == null && !tuple.getFields().contains("${f.name}_${label}") )
				{
					<#list f.labelsToSameEndpoint(label) as otherLabel>
						//${otherLabel}
					</#list>
				}
				</#list>
				-->
			}	
		</#if>		
	</#list>
		}
		//org.apache.log4j.Logger.getLogger("test").debug("set "+this);
	}
	
	
	
	
	