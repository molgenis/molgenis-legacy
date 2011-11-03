<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
	
		<div class="form_header" id="${model.getName()}">
		${model.label}
		</div>
		<div id="protocols">
			<span><a></a>&nbsp;</span>
			<span class="${model.getIndividualNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setIndividual">Individuals_info</a></span>
			<span class="${model.getPersonalNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setPersonal">Personal_info</a></span>
			<span class="${model.getMedicalNavClass()}"><a href="molgenis.do?__target=${screen.name}&__action=setMedical">Medical_info</a></span>
		</div>
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
			
<#--begin your plugin-->	
<#if !model.error>
	
	${model.getMatrixViewerIndv()}
	<input type="submit" name="setSelection" value='Get selected from matrix' onclick='__action.value="setSelection"' />
	
	
	<#if model.getSelection()??>
		<div id="bla" style="margin-top:35px">
			<input type="submit" name="setSample" value='Sample_info' onclick='__action.value="setSample"' />
			<input type="submit" name="setDNA" value='DNA' onclick='__action.value="setDNA"' />
			<input type="submit" name="setRNA" value='RNA' onclick='__action.value="setRNA"' />
			<input type="submit" name="setSerum" value='Serum' onclick='__action.value="setSerum"' />
			<input type="submit" name="setPlasma" value='Plasma' onclick='__action.value="setPlasma"' />
			<input type="submit" name="setBiopsy" value='Biopsies' onclick='__action.value="setBiopsy"' />
			<input type="submit" name="setHLA" value='HLA' onclick='__action.value="setHLA"' />
		</div>
	
		${model.getMatrixViewerSample()}
		<p>You selected from the matrix: ${model.getSelection()}</p>	
	</#if>
<#else>
	<p>There is a problem. probably there is no protocol given.</p>
	<a href="http://localhost:8080/molgenis_apps/molgenis.do?__target=mainmenu&select=protocol">Go to protocols </a>
</#if>
	<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
