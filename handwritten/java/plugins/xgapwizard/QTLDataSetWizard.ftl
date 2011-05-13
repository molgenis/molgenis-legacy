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
		
		<#-- Hack to immediatly clear the message so it doesn't "stick". -->
		${screen.clearMessage()}
		
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
	
<table>
	<tr>
		<td style="white-space: nowrap;">
			Genotypes:</td><td><input type="file" name="GenoFile"><input type="submit" value="Upload" onclick="__action.value='uploadGeno';return true;"/><#-->input type="button" value="Show example" onclick="mopen('genoExample');return true;"/-->
			<div class="wizardfoldout" id="genoExample">
			<i>Example</i><br>
			<table><tr><td class="matrixTableCell matrixRowColor1" style="background: white"><FONT FACE= "Courier New">
				inv1	ind2	ind3	ind4	ind5<br>
				mar1	A	A	B	A	B<br>
				mar2	A	A	B	A	B<br>
				mar3	A	A	A	A	B</font>
			</td></tr></table>
			</div><br>
		</td>
	</tr>
	<tr>
		<td style="white-space: nowrap;">
			Phenotypes:</td><td><input type="file" name="PhenoFile"><input type="submit" value="Upload" onclick="__action.value='uploadPheno';return true;"/><#--input type="button" value="Show example" onclick="mopen('phenoExample');return true;"/-->
			<div class="wizardfoldout" id="phenoExample">
			<i>Example</i><br>
			<table><tr><td class="matrixTableCell matrixRowColor1" style="background: white"><FONT FACE= "Courier New">
				inv1	ind2	ind3	ind4	ind5<br>
				trt1	45	567	345	234	12<br>
				trt2	13	673	867	754	234<br>
				trt3	347	34	375	56	345</font>
			</td></tr></table>
			</div><br>
		</td>
	</tr>
	<tr>
		<td style="white-space: nowrap;">
			Map:</td><td><input type="file" name="MapFile"><input type="submit" value="Upload" onclick="__action.value='uploadMap';return true;"/><#--input type="button" value="Show example" onclick="mopen('mapExample');return true;"/-->
			<div class="wizardfoldout" id="mapExample">
			<i>Example</i><br>
			<table><tr><td class="matrixTableCell matrixRowColor1" style="background: white"><FONT FACE= "Courier New">
				name	chr	cm<br>
				mar1	1	0.0<br>
				mar2	1	6.398<br>
				mar3	1	10.786<br>
				mar4	2	1.913<br>
				mar5	2	15.059</font>
			</td></tr></table>
			</div><br>
		</td>
	</tr>
</table>

<br>
<b>Important notes:</b><br>
<ul>
<li>For a correct new set, the <b>same</b> individual names must be used in the genotype and phenotype file.</li>
<li>The genotype and phenotype matrix 'rotation' is arbitrary. It does not matter if the individuals are on the rows or columns.</li>
<li>Multiple seperators can be used in all file uploads. Usual ones include tab, whitespace or comma.</li>
<li>The markers in the genotype matrix <b>must</b> be annotated in the map file, or QTL analysis will fail.</li>
<li>It is good practice to also annotate your individuals and traits. In fact, for certain analysis types this is required. (Genetical Genomics, trait heritability estimation, etc)</li>
</ul>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
