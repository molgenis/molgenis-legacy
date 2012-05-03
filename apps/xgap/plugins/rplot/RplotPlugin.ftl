<!--Date:        October 3, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.1-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro RplotPlugin screen>
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

<table class="tableBorder">
	<tr>
		<td class="matrixTableCell colorOfTitle">
		Select row:
		<select name="rowSelect">
			<#list model.matrixRows as row>
				<#if model.selectedRow?exists && model.selectedRow == row>
					<option SELECTED value="${row}">${row}</option>
				<#else>
					<option value="${row}">${row}</option>
				</#if>
			</#list>
		</select>
		</td>
		<td class="matrixTableCell colorOfTitle">
		Select column:
			<select name="colSelect">
				<#list model.matrixCols as col>
					<#if model.selectedCol?exists && model.selectedCol == col>
						<option SELECTED value="${col}">${col}</option>
					<#else>
						<option value="${col}">${col}</option>
					</#if>
				</#list>
			</select>
		</td>
		<td>
			Type of plot:
			<select name="typeSelect">
				<#if model.selectedData.valueType == "Decimal">
					<option <#if model.selectedPlotType?exists && model.selectedPlotType == "p">SELECTED</#if> value="p">Points</option>
					<option <#if model.selectedPlotType?exists && model.selectedPlotType == "l">SELECTED</#if> value="l">Lines</option>
					<option <#if model.selectedPlotType?exists && model.selectedPlotType == "o">SELECTED</#if> value="o">Overplotted</option>
					<option <#if model.selectedPlotType?exists && model.selectedPlotType == "s">SELECTED</#if> value="s">Stairs</option>
					<option <#if model.selectedPlotType?exists && model.selectedPlotType == "boxplot">SELECTED</#if> value="boxplot">Boxplot</option>
				</#if>
				<option <#if model.selectedPlotType?exists && model.selectedPlotType == "h">SELECTED</#if> value="h">Histogram</option>
			</select>
			<br>
			Size (pixels):
			<select name="resolution">
				<option <#if model.selectedWidth?exists && model.selectedWidth == 480>SELECTED</#if> value="480x640">480 x 640</option>
				<option <#if model.selectedWidth?exists && model.selectedWidth == 600>SELECTED</#if> value="600x800">600 x 800</option>
				<option <#if model.selectedWidth?exists && model.selectedWidth == 640>SELECTED</#if> value="640x480">640 x 480</option>
				<option <#if model.selectedWidth?exists && model.selectedWidth == 768>SELECTED</#if> value="768x1024">768 x 1024</option>
				<option <#if model.selectedWidth?exists && model.selectedWidth == 800>SELECTED</#if> value="800x600">800 x 600</option>
				<option <#if model.selectedWidth?exists && model.selectedWidth == 1024>SELECTED</#if> value="1024x768">1024 x 768</option>
				<option <#if model.selectedWidth?exists && model.selectedWidth == 1680>SELECTED</#if> value="1680x1050">1680 x 1050</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="matrixTableCell colorOfTitle">
			<input type="submit" value="Plot row" onclick="__action.value='plotRow';return true;"/>
		</td>
		<td class="matrixTableCell colorOfTitle">
			<input type="submit" value="Plot column" onclick="__action.value='plotCol';return true;"/>
		</td>
		<td>
			&nbsp;
		</td>
	</tr>
</table>

<#if model.tmpImgName?exists>
	<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + model.tmpImgName + "></body></html>">
	<a href="#" onclick="var generate = window.open('', '', 'width=${model.selectedWidth+50},height=${model.selectedHeight+50},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
		<img src="tmpfile/${model.tmpImgName}" width="${model.selectedWidth/5}" height="${model.selectedHeight/5}">
	</a>
</#if>
<#--<input name="myinput" value="${model.getMyValue()}">
<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
