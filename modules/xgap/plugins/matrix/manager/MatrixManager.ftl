<#macro MatrixManager screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
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

<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<#if !model.uploadMode>
	<#if modelExists && model.browser?exists>
		<#assign browserExists = true>
		<#assign browser = model.browser.model>
	<#else>
		No browser. An error has occurred.
		<#assign browserExists = false>
	</#if>
</#if>

<#if model.uploadMode || browserExists>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<#if model.uploadMode>
	<b>No data storage for selected source type found. Please upload data.</b>
	<br><br><i>Select the upload options you want:</i><br>
	<input type="checkbox" name="prependToRows" value="true" /> Prepend underscore to row names (e.g. to preserve heading numerals)<br />
	<input type="checkbox" name="prependToCols" value="true" /> Prepend underscore to column names (e.g. to preserve heading numerals)<br />
	<input type="checkbox" name="escapeRows" value="true" /> Escape row names to safe format where needed<br />
	<input type="checkbox" name="escapeCols" value="true" /> Escape column names to safe format where needed<br />
	<input type="checkbox" name="trimTextElements" value="true" /> Trim text elements to max length (127 chars)<br />
	<br /><i>Select your data matrix file and press 'upload'.</i><br /> 
	<input type="file" name="upload"/>
	<input type="submit" value="Upload" onclick="__action.value='upload';return true;"/><br>
	<br>
	<i>Alternatively, use this textarea to input your data.</i><br>
	<textarea id="matrixInputTextArea" name="inputTextArea" rows="10" cols="50">	feat1	feat2	feat3
targ1	val1	val2	val3
targ2	val4	val5	val6</textarea>
	<input id="matrixUploadTextArea" type="submit" value="Upload" onclick="__action.value='uploadTextArea';return true;"/><br>
<br>
	<i>Advanced: import an existing xQTL binary file</i><br>
	<input type="file" name="uploadBinaryFile"/>
	<input id="matrixUploadBinary" type="submit" value="Upload" onclick="__action.value='uploadBinary';return true;"/><br>
<#else>



<div class="matrixviewer" style="overflow: auto; max-height: 500px;">				
	<table>
		<tr>
			<td class="shadeHeader">
				&nbsp;
			</td>
			<td align="left" class="shadeHeader" valign="center">
				<input type="image" src="res/img/first.png" onclick="document.forms.${screen.name}.__action.value = 'moveFarLeft';" />
				<input type="image" src="res/img/prev.png" onclick="document.forms.${screen.name}.__action.value = 'moveLeft';"/>
				<b><font class="fontColor"><#if model.getColHeader()?exists>${model.getColHeader()}<#else>0-0 of 0</#if></font></b>
				<input type="image" src="res/img/next.png" onclick="document.forms.${screen.name}.__action.value = 'moveRight';"/>
				<input type="image" src="res/img/last.png"  onclick="document.forms.${screen.name}.__action.value = 'moveFarRight';" />
			</td>
		</tr>
		<tr>
			<td rowspan="2" class="shadeHeader" align="right">
				<input type="image" src="res/img/rowStart.png" onclick="document.forms.${screen.name}.__action.value = 'moveFarUp';"/><br>
				<input type="image" src="res/img/up.png" onclick="document.forms.${screen.name}.__action.value = 'moveUp';"/><br>
				<b><font class="fontColor"><#if model.getRowHeader()?exists>${model.getRowHeader()}<#else>0-0 of 0</#if></font></b><br>
				<input type="image" src="res/img/down.png" onclick="document.forms.${screen.name}.__action.value = 'moveDown';"/><br>
				<input type="image" src="res/img/rowStop.png" onclick="document.forms.${screen.name}.__action.value = 'moveFarDown';"/><br>
				<br>
				<table>
					<tr><td><font class="fontColor">Stepsize</font></td><td><input type="text" name="stepSize" value="${browser.stepSize?c}" size="1"></td></tr>
					<tr><td><font class="fontColor">Width</font></td><td><input type="text" name="width" value="${browser.width?c}" size="1"></td></tr>
					<tr><td><font class="fontColor">Height</font></td><td><input type="text" name="height" value="${browser.height?c}" size="1"></td></tr>
					<tr><td colspan="2"><input type="submit" value="Change size" onclick="document.forms.${screen.name}.__action.value = 'changeSubmatrixSize'; document.forms.${screen.name}.submit();"></td></tr>
					<#-->tr><td colspan="2"><br><input type="submit" value="Apply filter to visible" onclick="document.forms.${screen.name}.__action.value = 'filterVisible'; document.forms.${screen.name}.submit();"></td></tr>
					<tr><td colspan="2"><input type="submit" value="Apply filter to all" onclick="document.forms.${screen.name}.__action.value = 'filterAll'; document.forms.${screen.name}.submit();"></td></tr-->
				</table>
			</td>
			<td valign="top">
				<#if model.message?exists>
					<#if model.message.success>
						<p class="successmessage">${model.message.text}</p>
					<#else>
						<p class="errormessage">${model.message.text}</p>
					</#if>
				</#if>
			</td>
		</tr>
		<tr>
			<td>
				<table class="tableBorder">
					<tr>
						<td></td>
						<#list browser.subMatrix.colNames as n>
							<td class="matrixTableCell colorOfTitle">
								${model.renderCol(n, screen.name)}
							</td>
						</#list>
					</tr>
			
					<#list browser.subMatrix.rowNames as n> 
						<tr>
							<td class="matrixTableCell colorOfTitle">
								${model.renderRow(n, screen.name)}
							</td>
							
							<#assign x = browser.subMatrix.numberOfCols>
							<#list 0..x-1 as i>								
					  			<#if browser.subMatrix.elements[n_index][i]?exists>
						  			<#if model.selectedData.valuetype == "Decimal">
						  				<#assign val = browser.subMatrix.elements[n_index][i]>
						  				<#if n_index%2==0>
						  					<td class="matrixTableCell matrixRowColor1">${val?c}</td>
						  				<#else>
						  					<td class="matrixTableCell matrixRowColor0">${val?c}</td>
						  				</#if>
						  			<#else>
						  				<#if browser.subMatrix.elements[n_index][i] != "">
							  				<#assign val = browser.subMatrix.elements[n_index][i]>
						  					<#if n_index%2==0>
						  						<td class="matrixTableCell matrixRowColor1">${val}</td>
						  					<#else>
						  						<td class="matrixTableCell matrixRowColor0">${val}</td>
						  					</#if>
						  				<#else>
						  					<!--td class="matrixTableCell matrixRowColorEmpty">&nbsp;</td-->
						  					<#if n_index%2==0>
						  						<td class="matrixTableCell matrixRowColor1">&nbsp;</td>
						  					<#else>
						  						<td class="matrixTableCell matrixRowColor0">&nbsp;</td>
						  					</#if>
						  				</#if>
						  			</#if>	
					  			<#else>
					  				<!--td class="matrixTableCell matrixRowColorEmpty">&nbsp;</td-->
				  					<#if n_index%2==0>
				  						<td class="matrixTableCell matrixRowColor1">&nbsp;</td>
				  					<#else>
				  						<td class="matrixTableCell matrixRowColor0">&nbsp;</td>
				  					</#if>
					  			</#if>
							</#list> 
						</tr>
					</#list>
				</table>
			</td>
		</tr>
	</table>
</div><br>



<table cellpadding="5" cellmargin="5">
	<tr>
		<td onMouseOver="this.style.background='#CCCCCC'" onMouseOut="this.style.background='#EAEAEA'" onclick="mopen('matrix_plugin_Actions');" style="text-align:center;width:75px">
			<img width="32" height="32" src="clusterdemo/icons/action.gif"/><br/>Action
		</td>
		<td onMouseOver="this.style.background='#CCCCCC'" onMouseOut="this.style.background='#EAEAEA'" onclick="mopen('matrix_plugin_FileSub');" style="text-align:center;width:70px">
			<img width="32" height="32" src="clusterdemo/icons/download64.png" /><br/>Download
		</td>
		<td onMouseOver="this.style.background='#CCCCCC'" onMouseOut="this.style.background='#EAEAEA'" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}document.forms.${screen.name}.__target.value='${screen.name}';document.forms.${screen.name}.__action.value = 'refresh';document.forms.${screen.name}.submit();" style="text-align:center;width:75px">
			<img width="32" height="32" src="clusterdemo/icons/reset64.png"/><br/>Reset
		</td>
	</tr>
</table>



<#if model.filter?exists>
<table cellpadding="5">
	<tr>
		<td>
			<i>Last action:</i> <b>${model.filter}.</b>
		</td>
	</tr>
</table>
</#if>


<div style="width:400px; display:none" id="matrix_plugin_FileSub">
	<table cellpadding="3">
	<#assign icon_size = 30>
		<tr>
			<td class="submenuitem">
				<b><i><font size="3">Visible values</font></i></b>
			</td>
			<td class="submenuitem">
				<b><i><font size="3">All values</font></i></b>
			</td>
		</tr>
		<tr>
			<td class="submenuitem" onclick="location.href='downloadmatrixascsv?id=inmemory'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/txt_icon.png" align="left" />&nbsp;&nbsp;CSV format
			</td>
			<td class="submenuitem" onclick="location.href='downloadmatrixascsv?id=${model.selectedData.getId()?c}&download=all&stream=false'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/txt_icon.png" align="left" />&nbsp;&nbsp;CSV format
			</td>		
		</tr>	
		<tr>
			<td class="submenuitem" onclick="location.href='downloadmatrixasexcel?id=inmemory'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/excel_icon.png" align="left" />&nbsp;&nbsp;Excel file
			</td>
			<td class="submenuitem" onclick="location.href='downloadmatrixasexcel?id=${model.selectedData.getId()?c}&download=all'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/excel_icon.png" align="left" />&nbsp;&nbsp;Excel file
			</td>
		</tr>
		<tr>
			<td class="submenuitem" onclick="location.href='downloadmatrixasspss?id=inmemory'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/spss_icon.png" align="left" />&nbsp;&nbsp;SPSS file
			</td>
			<td class="submenuitem" onclick="location.href='downloadmatrixasspss?id=${model.selectedData.getId()?c}&download=all'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/spss_icon.png" align="left" />&nbsp;&nbsp;SPSS file
			</td>
		</tr>
		<tr>
			<td class="submenuitem" onclick="location.href='downloadmatrixasrobject?id=inmemory'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/r_icon.gif" align="left" />&nbsp;&nbsp;R matrix object
			</td>
			<td class="submenuitem" onclick="location.href='downloadmatrixasrobject?id=${model.selectedData.getId()?c}&download=all'">
				<img width="${icon_size}" height="${icon_size}" src="clusterdemo/icons/r_icon.gif" align="left" />&nbsp;&nbsp;R matrix object
			</td>
		</tr>
		
		<#if model.selectedData.storage == "Binary" && model.hasBackend == true>
		<tr>
			<td class="submenuitem">
				&nbsp;
			</td>
			<td class="submenuitem" onclick="location.href='downloadfile?name=${model.selectedData.name}'">
				<img width="${icon_size}" height="${icon_size}" src="res/img/download.png" align="left" />&nbsp;&nbsp;Binary file
			</td>
		</tr>
		</#if>
	</table>
</div>



<div id="matrix_plugin_Actions" style="display:none">
	<table cellpadding="10">
		<tr>
			<td>
				Select:<br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter8');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');display('hide', 'filter6');display('hide', 'filter7');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter8'>checked</#if>><b>C</b> ${model.selectedData.featureType?lower_case}s</nobr><br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter9');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');display('hide', 'filter6');display('hide', 'filter7');display('hide', 'filter8');" <#if model.selectedFilterDiv == 'filter9'>checked</#if>><b>R</b> ${model.selectedData.targetType?lower_case}s</nobr>
			</td>
			<td>
				Filter on values:<br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter2');display('hide', 'filter1');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');display('hide', 'filter6');display('hide', 'filter7');display('hide', 'filter8');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter2'>checked</#if>><b>C</b> ${model.selectedData.featureType?lower_case}s</nobr><br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter3');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter4');display('hide', 'filter5');display('hide', 'filter6');display('hide', 'filter7');display('hide', 'filter8');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter3'>checked</#if>><b>R</b> ${model.selectedData.targetType?lower_case}s</nobr>
			</td>
			<td>
				Filter on attributes:<br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter4');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter5');display('hide', 'filter6');display('hide', 'filter7');display('hide', 'filter8');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter4'>checked</#if>><b>C</b> ${model.selectedData.featureType?lower_case}s</nobr><br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter5');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter6');display('hide', 'filter7');display('hide', 'filter8');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter5'>checked</#if>><b>R</b> ${model.selectedData.targetType?lower_case}s</nobr>
			</td>
			<td>
				Special:<br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');display('hide', 'filter6');display('hide', 'filter7');display('hide', 'filter8');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter1'>checked</#if>><b>RC</b> Filter on index</nobr><br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter7');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');display('hide', 'filter6');display('hide', 'filter8');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter7'>checked</#if>><b>RC</b> Filter on two dimensions</nobr><br>
				<nobr><input name="filterSelect" type="radio" onclick="display('show', 'filter6');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');display('hide', 'filter7');display('hide', 'filter8');display('hide', 'filter9');" <#if model.selectedFilterDiv == 'filter6'>checked</#if>>Graph / heatmap with R</nobr>
			</td>
		</tr>
	</table>
	
	<hr>
	
	<div id="filter1" <#if model.selectedFilterDiv != 'filter1'>style="display:none"</#if>>
		<table>
			<tr>
				<td>
					Filter by index:
				</td>
				<td>
					<select name="add_filter_by_indexFILTER_FIELD">
						<option value="row">${model.selectedData.targetType} index</option>
						<option value="col">${model.selectedData.featureType} index</option>
					</select>
				</td>
				<td>
					<select name="add_filter_by_indexFILTER_OPERATOR">
						<#list model.allOperators?keys as op><option value="${op}">${model.allOperators[op]}</option></#list>
					</select>
				</td>
				<td>
					<input type="text" size="8" name="add_filter_by_indexFILTER_VALUE" />
				</td>
				<td>
					<input type="submit" value="Apply to visible" onclick="document.forms.${screen.name}.__action.value = 'filter_visible_by_index'; document.forms.${screen.name}.submit();">
					<input type="submit" value="Apply to all" onclick="document.forms.${screen.name}.__action.value = 'filter_all_by_index'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
			<tr>
		</table>
	</div>
	<div id="filter2" <#if model.selectedFilterDiv != 'filter2'>style="display:none"</#if>>
		<table>
			<tr>
				<td>
					Filter by ${model.selectedData.featureType?lower_case} values:
				</td>
				<td>
					<select name="add_filter_by_col_valueFILTER_FIELD">
						<#list browser.subMatrix.colNames as col><option value="${col}">${col}</option></#list>
					</select>
				</td>
				<td>
					<select name="add_filter_by_col_valueFILTER_OPERATOR">
						<#list model.valueOperators?keys as op><option value="${op}">${model.valueOperators[op]}</option></#list>
					</select>
				</td>
				<td>
					<input type="text" size="8" name="add_filter_by_col_valueFILTER_VALUE" />
				</td>
				<td>
					<input type="submit" value="Apply to visible" onclick="document.forms.${screen.name}.__action.value = 'filter_visible_by_col_value'; document.forms.${screen.name}.submit();">
					<input type="submit" value="Apply to all" onclick="document.forms.${screen.name}.__action.value = 'filter_all_by_col_value'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
		</table>
	</div>
	<div id="filter3" <#if model.selectedFilterDiv != 'filter3'>style="display:none"</#if>>
		<table>
			<tr>
				<td>
					Filter by ${model.selectedData.targetType?lower_case} values:
				</td>
				<td>
					<select name="add_filter_by_row_valueFILTER_FIELD">
						<#list browser.subMatrix.rowNames as row><option value="${row}">${row}</option></#list>
					</select>
				</td>
				<td>
					<select name="add_filter_by_row_valueFILTER_OPERATOR">
						<#list model.valueOperators?keys as op><option value="${op}">${model.valueOperators[op]}</option></#list>
					</select>
				</td>
				<td>
					<input type="text" size="8" name="add_filter_by_row_valueFILTER_VALUE" />
				</td>
				<td>
					<input type="submit" value="Apply to visible" onclick="document.forms.${screen.name}.__action.value = 'filter_visible_by_row_value'; document.forms.${screen.name}.submit();">
					<input type="submit" value="Apply to all" onclick="document.forms.${screen.name}.__action.value = 'filter_all_by_row_value'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
		</table>
	</div>
	<div id="filter4" <#if model.selectedFilterDiv != 'filter4'>style="display:none"</#if>>
		<table>
			<tr>
				<td>
					Filter by ${model.selectedData.featureType?lower_case} attributes:
				</td>
				<td>
					<select name="add_filter_by_col_attrbFILTER_FIELD">
						<#list model.colHeaderAttr as cha>
							<option value="${cha}">${cha}</option>
						</#list>
					</select>
				</td>
				<td>
					<select name="add_filter_by_col_attrbFILTER_OPERATOR">
						<#list model.allOperators?keys as op><option value="${op}">${model.allOperators[op]}</option></#list>
					</select>
				</td>
				<td>
					<input type="text" size="8" name="add_filter_by_col_attrbFILTER_VALUE" />
				</td>
				<td>
					<input type="submit" value="Apply to visible" onclick="document.forms.${screen.name}.__action.value = 'filter_visible_by_col_attrb'; document.forms.${screen.name}.submit();">
					<input type="submit" value="Apply to all" onclick="document.forms.${screen.name}.__action.value = 'filter_all_by_col_attrb'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
		</table>
	</div>
	<div id="filter5" <#if model.selectedFilterDiv != 'filter5'>style="display:none"</#if>>
		<table>
			<tr>
				<td>
					Filter by ${model.selectedData.targetType?lower_case} attributes:
				</td>
				<td>
					<select name="add_filter_by_row_attrbFILTER_FIELD">
						<#list model.rowHeaderAttr as rha>
							<option value="${rha}">${rha}</option>
						</#list>
					</select>
				</td>
				<td>
					<select name="add_filter_by_row_attrbFILTER_OPERATOR">
						<#list model.allOperators?keys as op><option value="${op}">${model.allOperators[op]}</option></#list>
					</select>
				</td>
				<td>
					<input type="text" size="8" name="add_filter_by_row_attrbFILTER_VALUE" />
				</td>
				<td>
					<input type="submit" value="Apply to visible" onclick="document.forms.${screen.name}.__action.value = 'filter_visible_by_row_attrb'; document.forms.${screen.name}.submit();">
					<input type="submit" value="Apply to all" onclick="document.forms.${screen.name}.__action.value = 'filter_all_by_row_attrb'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
		</table>
	</div>
	<div id="filter6" <#if model.selectedFilterDiv != 'filter6'>style="display:none"</#if>>
		<table>
			<tr>
				<td>
					Make R plot of size (pixels):
				</td>
				<td colspan="2">
					<select name="r_plot_resolution">
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
				<td colspan="3">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					<i>Regular plot of type:</i>
				</td>
				<td colspan="2">
					<select name="r_plot_type">
						<#--if model.selectedData.valueType == "Decimal"-->
							<option <#if model.selectedPlotType?exists && model.selectedPlotType == "p">SELECTED</#if> value="p">Points</option>
							<option <#if model.selectedPlotType?exists && model.selectedPlotType == "l">SELECTED</#if> value="l">Lines</option>
							<option <#if model.selectedPlotType?exists && model.selectedPlotType == "o">SELECTED</#if> value="o">Overplotted</option>
							<option <#if model.selectedPlotType?exists && model.selectedPlotType == "s">SELECTED</#if> value="s">Stairs</option>
							<option <#if model.selectedPlotType?exists && model.selectedPlotType == "boxplot">SELECTED</#if> value="boxplot">Boxplot</option>
						<#--if-->
						<option <#if model.selectedPlotType?exists && model.selectedPlotType == "h">SELECTED</#if> value="h">Histogram</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<select name="r_plot_row_select">
						<#list browser.subMatrix.rowNames as row><option value="${row}">${row}</option></#list>
					</select>
				</td>
				<td>
					<input type="submit" value="Plot full row" onclick="document.forms.${screen.name}.__action.value = 'r_plot_full_row'; document.forms.${screen.name}.submit();">
				</td>
				<td>
					<input type="submit" value="Plot visible row" onclick="document.forms.${screen.name}.__action.value = 'r_plot_visible_row'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
			<tr>
				<td>
					<select name="r_plot_col_select">
						<#list browser.subMatrix.colNames as col><option value="${col}">${col}</option></#list>
					</select>
				</td>
				<td>
					<input type="submit" value="Plot full column" onclick="document.forms.${screen.name}.__action.value = 'r_plot_full_col'; document.forms.${screen.name}.submit();">
				</td>
				<td>
					<input type="submit" value="Plot visible column" onclick="document.forms.${screen.name}.__action.value = 'r_plot_visible_col'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
			<tr>
				<td colspan="3">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					<i>Heatmap plot with clustering on:</i>
				</td>
				<td colspan="2">
					<select name="r_heatmap_type">
						<option value="rowscols">Rows and cols</option>
						<option value="rows">Just rows</option>
						<option value="cols">Just cols</option>
						<option value="none">None</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<i>(NA values are replaced with 0)</i>
				</td>
				<td>
					<input type="submit" value="Plot full values" onclick="document.forms.${screen.name}.__action.value = 'r_plot_full_heatmap'; document.forms.${screen.name}.submit();">
				</td>
				<td>
					<input type="submit" value="Plot visible values" onclick="document.forms.${screen.name}.__action.value = 'r_plot_visible_heatmap'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
		</table>
	</div>
	<div id="filter7" <#if model.selectedFilterDiv != 'filter7'>style="display:none"</#if>>
		<table>
			<tr>
				<td>
					Two-dimensional filtering:
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					Select all
					${model.selectedData.targetType?lower_case}s
					with at least
					<select name="2d_filter_by_row_AMOUNT">
						<#list 1..25 as a>
						<option value="${a}">${a}</option>
						</#list>
					</select>
					${model.selectedData.featureType?lower_case}(s)
					having a value
					<select name="2d_filter_by_row_FILTER_OPERATOR">
						<#list model.allOperators?keys as op><option value="${op}">${model.allOperators[op]}</option></#list>
					</select>
					<input type="text" size="8" name="2d_filter_by_row_FILTER_VALUE" />
				</td>
			</tr>
			<tr>
				<td align="right">
					<input type="submit" value="Apply to visible" onclick="document.forms.${screen.name}.__action.value = '2d_filter_visible_row'; document.forms.${screen.name}.submit();">
					<input type="submit" value="Apply to all" onclick="document.forms.${screen.name}.__action.value = '2d_filter_all_row'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td>
					Select all
					${model.selectedData.featureType?lower_case}s
					with at least
					<select name="2d_filter_by_col_AMOUNT">
						<#list 1..25 as a>
						<option value="${a}">${a}</option>
						</#list>
					</select>
					${model.selectedData.targetType?lower_case}(s)
					having a value
					<select name="2d_filter_by_col_FILTER_OPERATOR">
						<#list model.allOperators?keys as op><option value="${op}">${model.allOperators[op]}</option></#list>
					</select>
					<input type="text" size="8" name="2d_filter_by_col_FILTER_VALUE" />
					
				</td>
			</tr>
			<tr>
				<td align="right">
					<input type="submit" value="Apply to visible" onclick="document.forms.${screen.name}.__action.value = '2d_filter_visible_col'; document.forms.${screen.name}.submit();">
					<input type="submit" value="Apply to all" onclick="document.forms.${screen.name}.__action.value = '2d_filter_all_col'; document.forms.${screen.name}.submit();">
				</td>
			</tr>
		</table>
	</div>
	<div id="filter8" <#if model.selectedFilterDiv != 'filter8'>style="display:none"</#if>>
		<table>
			<tr>
				<td colspan="2">
					Select ${model.selectedData.featureType?lower_case}s to be displayed and click 'Apply selection':
				</td>
			</tr>
			<tr>
				<td>
					<br>
					<input type="submit" value="Apply selection, preserve current rows" onclick="document.forms.${screen.name}.__action.value = 'select_preserverows_cols'; document.forms.${screen.name}.submit();">
					<br><br>
					<input type="submit" value="Apply selection, get all rows" onclick="document.forms.${screen.name}.__action.value = 'select_allrows_cols'; document.forms.${screen.name}.submit();">
					<br><br>
				</td>
			</tr>
			<tr>
				<#if model.browser.model.instance.colNames?size gt 100>
				<td>
					<i>More than 100 columns, showing visible:</i><br>
					<div style="overflow: scroll; height: 300px;">
						<#list browser.subMatrix.colNames as colName>
							<input type="checkbox" name="colselect_${colName}" value="true" /> ${colName}<br>
						</#list>
					</div>
				</td>
				<td>
					<i>And the first 100:</i><br>
					<div style="overflow: scroll; height: 300px;">
						<#list model.browser.model.instance.colNames[0..99] as colName>
							<input type="checkbox" name="colselect_${colName}" value="true" /> ${colName}<br>
						</#list>
					</div>
				</td>
				<#else>
				<td colspan="2">
					<i>Less than 100 columns, showing all</i><br>
					<div style="overflow: scroll; height: 300px;">
						<#list model.browser.model.instance.colNames as colName>
							<input type="checkbox" name="colselect_${colName}" value="true" /> ${colName}<br>
						</#list>
					</div>
				</td>
				</#if>
			</tr>
		</table>
	</div>
	<div id="filter9" <#if model.selectedFilterDiv != 'filter9'>style="display:none"</#if>>
		<table>
			<tr>
				<td colspan="2">
					Select ${model.selectedData.targetType?lower_case}s to be displayed and click 'Apply selection':
				</td>
			</tr>
			<tr>
				<td>
					<br>
					<input type="submit" value="Apply selection, preserve current columns" onclick="document.forms.${screen.name}.__action.value = 'select_preservecols_rows'; document.forms.${screen.name}.submit();">
					<br><br>
					<input type="submit" value="Apply selection, get all columns" onclick="document.forms.${screen.name}.__action.value = 'select_allcols_rows'; document.forms.${screen.name}.submit();">
					<br><br>
				</td>
			</tr>
			<tr>
				<#if model.browser.model.instance.rowNames?size gt 100>
				<td>
					<i>More than 100 rows, showing visible:</i><br>
					<div style="overflow: scroll; height: 300px;">
						<#list browser.subMatrix.rowNames as rowName>
							<input type="checkbox" name="rowselect_${rowName}" value="true" /> ${rowName}<br>
						</#list>
					</div>
				</td>
				<td>
					<i>And the first 100:</i><br>
					<div style="overflow: scroll; height: 300px;">
						<#list model.browser.model.instance.rowNames[0..99] as rowName>
							<input type="checkbox" name="rowselect_${rowName}" value="true" /> ${rowName}<br>
						</#list>
					</div>
				</td>
				<#else>
				<td colspan="2">
					<i>Less than 100 rows, showing all</i><br>
					<div style="overflow: scroll; height: 300px;">
						<#list model.browser.model.instance.rowNames as rowName>
							<input type="checkbox" name="rowselect_${rowName}" value="true" /> ${rowName}<br>
						</#list>
					</div>
				</td>
				</#if>
			</tr>
		</table>
	</div>
</div>

<#if model.tmpImgName?exists>
	<br><table><tr><td><i>Click to enlarge</i></td></tr></table>
	<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + model.tmpImgName + "></body></html>">
	<a href="#" onclick="var generate = window.open('', '', 'width=${model.selectedWidth+50},height=${model.selectedHeight+50},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
		<img src="tmpfile/${model.tmpImgName}" width="${model.selectedWidth/5}" height="${model.selectedHeight/5}">
	</a>
</#if>


</#if>

</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
