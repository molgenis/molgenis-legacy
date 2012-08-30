<#macro plugins_xgapwizard_QTLDataSetWizard screen>
<#assign model = screen.myModel>
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
<#--begin your plugin-->

<br>
	<i>Into investigation:</i>
	<select name = "invSelect">
	<#if model.investigations?exists>
		<#list model.investigations as i>
			<option value="${i.id?c}" <#if model.selectedInv?exists && model.selectedInv == i.id>SELECTED</#if>>${i.name}</option>
		</#list>
	</#if>
	</select>
	
	<br>
	<br>
	
<table cellpadding="5">
	<tr>
		<td colspan="2"><hr /></td>
	</tr>
	<tr>
		<td style="white-space: nowrap;">
			Genotypes:
		</td>
		<td>
			<input type="file" name="GenoFile"><input type="submit" id="upload_genotypes" value="Upload" onclick="__action.value='uploadGeno';return true;"/><#-->input type="button" value="Show example" onclick="mopen('genoExample');return true;"/-->

			<br><i>Example</i><br>
			<table><tr><td class="matrixTableCell matrixRowColor1" style="background: white"><FONT FACE= "Courier New">
				inv1	ind2	ind3	ind4	ind5<br>
				mar1	A	A	B	A	B<br>
				mar2	A	A	B	A	B<br>
				mar3	A	A	A	A	B</font>
			</td></tr></table>
			<br><b>Your individuals must be in the first line.</b> If they are not, please have a look at <a target="_blank" href="http://www.molgenis.org/wiki/xQTLBiologistImport">the manual</a>.
			If you have not annotated your data yet, individuals are automatically added with cross type <select name="cross"><#list model.crosses as cross><option value="${cross.id}">${cross.name}</option></#list></select>
		</td>
	</tr>
	<tr>
		<td colspan="2"><hr /></td>
	</tr>
	<tr>
		<td style="white-space: nowrap;">
			Phenotypes:
		</td>
		<td><input type="file" name="PhenoFile"><input type="submit" id="upload_phenotypes" value="Upload" onclick="__action.value='uploadPheno';return true;"/><#--input type="button" value="Show example" onclick="mopen('phenoExample');return true;"/-->

			<br><i>Example</i><br>
			<table><tr><td class="matrixTableCell matrixRowColor1" style="background: white"><FONT FACE= "Courier New">
				inv1	ind2	ind3	ind4	ind5<br>
				trt1	45	567	345	234	12<br>
				trt2	13	673	867	754	234<br>
				trt3	347	34	375	56	345</font>
			</td></tr></table>
			<br><b>Again, your individuals must be in the first line.</b> If they are not, please have a look at <a target="_blank" href="http://www.molgenis.org/wiki/xQTLBiologistImport">the manual</a>.<br>
			If you have not annotated your data yet, traits are automatically as <select name="trait"><#list model.xqtlObservableFeatureTypes as xof><option value="${xof}">${xof}</option></#list></select>
		</td>
	</tr>
	<tr>
		<td colspan="2"><hr /></td>
	</tr>
	<tr>
		<td style="white-space: nowrap;">
			Map:
		</td>
		<td><input type="file" name="MapFile"><input type="submit" id="upload_map" value="Upload" onclick="__action.value='uploadMap';return true;"/><#--input type="button" value="Show example" onclick="mopen('mapExample');return true;"/-->

			<br><i>Example</i><br>
			<table><tr><td class="matrixTableCell matrixRowColor1" style="background: white"><FONT FACE= "Courier New">
				name	chr	cm<br>
				mar1	1	0.0<br>
				mar2	1	6.398<br>
				mar3	1	10.786<br>
				mar4	2	1.913<br>
				mar5	2	15.059</font>
			</td></tr></table>
			<br><b>Please use this exact first line.</b><br>
		</td>
	</tr>
	<tr>
		<td colspan="2"><hr /></td>
	</tr>
</table>
<br>
<i>For additional information and notes, please refer to the <a target="_blank" href="http://www.molgenis.org/wiki/xQTLBiologistImport">the manual</a>.</i>
<br>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
