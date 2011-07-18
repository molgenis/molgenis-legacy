<#macro plugin_LuceneIndex_AdminIndexes screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	

		<h2> Administration of Database Index</h2>
			<input type="submit" value="Build Index" onclick="__action.value='CreateLuceneIndex';return true;"/><br /><br />
		    <input type="submit" value ="Delete DB Index" onclick="if (confirm('You are about to delete the index. Are you sure you want to proceed?')) { __action.value='DeleteLuceneIndex';return true; } else { return false; }" />
		<h2> Administration OntoCAT Index</h2>
		<!--because of memory cost , this has to be done from server. 	-->	
		
		<input type="text" name="InputToken" value="<#if screen.getInputToken()?exists>${screen.getInputToken()} </#if>"" onkeydown="if (event.keyCode==13)__action.value='SearchLuceneIndex';return true;"/><br /><br />
				<#if screen.useOntologies?exists>
					<#if screen.useOntologies == "true">
  
						<p><h4>Ontologies to use in query expansion:</h4></p>
						<input type="checkbox" name="HPO" value="checked" checked="checked"
						onclick="ALLSEL.checked=((HPO.checked)&&(HD.checked)&&(NCI.checked)&&(MeSH.checked));">
						Human Phenotype Ontology<br>
						<input type="checkbox" name="HD" value="checked" checked="checked"
						onclick="ALLSEL.checked=((HPO.checked)&&(HD.checked)&&(NCI.checked)&&(MeSH.checked));">
						Human Disease Ontology<br>
						<input type="checkbox" name="NCI" value="checked" checked="checked"
						onclick="ALLSEL.checked=((HPO.checked)&&(HD.checked)&&(NCI.checked)&&(MeSH.checked));">
						NCI Thesaurus<br>
						<input type="checkbox" name="MeSH" value="checked" checked="checked"
						onclick="ALLSEL.checked=((HPO.checked)&&(HD.checked)&&(NCI.checked)&&(MeSH.checked));">
						Medical Subject Headings<br>
						<br>
						<input type="checkbox" name="ALLSEL" value="checked" checked="checked"
						  onclick="if (ALLSEL.checked){HPO.checked=true;HD.checked=true;NCI.checked=true;MeSH.checked=true;}
						  else{HPO.checked=false;HD.checked=false;NCI.checked=false;MeSH.checked=false;}">
						Select All [Deselect All]<br>
						<br>
						<input type="submit" value="Search with query expansion" onclick="__action.value='ExpandQuery';return true;"/>
						
					<#elseif screen.useOntologies == "false">
						<p>Query expansion (using Ontologies) not available .</p>
  					</#if>
				</#if>				
				<br/><br/>
			<input type="submit" value="Build Ontocat Index" onclick="__action.value='CreateOntocatLuceneIndex';return true;"/><br /><br />
			<input type="submit" value ="Delete ONTOcat Index" onclick="if (confirm('You are about to delete the index. Are you sure you want to proceed?')) { __action.value='DeleteOntocatIndex';return true; } else { return false; }" />
			
		
		
			<p> <ul><li> The creation of OntoCAT index requires extra memory. This has to be done from server  - run as separate application (with VM argument -Xms512M -Xmx512M)</li></ul></p> 
			
</br></br>	
				
 				<label> 	<#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>	
	
	
			</div>
		</div>
	</div>
</form>
</#macro>
