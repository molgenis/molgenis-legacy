<#--
Common parts for saving files to an entity.
-->
	public void prepareFileAttachements(List<${JavaName(entity)}> entities, File baseDir) throws IOException
	{
<#if hasFiles(entity)>		
		for(${JavaName(entity)} entity: entities)
		{	
<#list updateFields(entity) as field>
	<#if field.type.toString() == "file" || field.type.toString() == "image">
			//set a dummy for the file if it was attached (to evade not null exceptions)
			if(entity.get${JavaName(field)}AttachedFile() != null)
			{
				entity.set${JavaName(field)}("dummy");
			}
</#if>
</#list>
		}
</#if>
	}

	public boolean saveFileAttachements(List<${JavaName(entity)}> entities, File baseDir) throws IOException
	{
<#if hasFiles(entity)>		
		for(${JavaName(entity)} entity: entities)
		{		
<#list updateFields(entity) as field>
<#if field.type.toString() == "file" || field.type.toString() == "image">
			//store a file attachement
			if(entity.get${JavaName(field)}AttachedFile() != null)
			{
				String filename = entity.get${JavaName(field)}AttachedFile().toString();
				String extension = filename.substring(filename.lastIndexOf('.'));
				filename = "${JavaName(entity)}/${JavaName(field)}"+<#list keyFields(entity) as f>entity.get${JavaName(f)}()<#if f_has_next>+"_"+</#if></#list>+extension;	
				entity.set${JavaName(field)}(filename);
		
				FileUtils.copyFile( entity.get${JavaName(field)}AttachedFile(), new File( baseDir.toString()+"/"+ entity.get${JavaName(field)}() ) );
			}
</#if>
</#list>
		}
		return true;
<#else>
		return false;
</#if>
	}