<#--if name??>${name}<#else>null</#if><br-->
<!-- label: <#if value??>${value}<#else>null</#if><br>
rowIndex: ${rowStartIndex}<br>
colIndex: ${colStartIndex}<br>
totalNumberOfRows: ${matrix.totalNumberOfRows}<br>
totalNumberOfCols: ${matrix.totalNumberOfCols}<br>
 -->
 
APPLIED FILTERS:<br>
<#list matrix.filters as filter>
type: ${filter.filterType} / queryrule: ${filter.queryRule} / index: ${filter.index}<br>
</#list>


<#assign colHeader = matrix.colType + " " + (colStartIndex+1) + "-" + (colStartIndex+matrix.visibleCols?size) + " of " + matrix.totalNumberOfCols>
<#assign rowHeader = matrix.rowType + "<br>" + (rowStartIndex+1) + "-" + (rowStartIndex+matrix.visibleRows?size) + " of " + matrix.totalNumberOfRows>


<table>
	<tr>
		<td class="menuitem shadeHeader" onclick="mopen('matrix_plugin_FileSub');">
			Menu
			<img src="res/img/pulldown.gif"/><br>
			<div class="submenu" id="matrix_plugin_FileSub">
				<table>
				<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}__action.value='${req_tag}download_visible';__show.value='download';submit();"><img src="res/img/download.png" align="left" />Download visible as text</td></tr>
				<tr><td class="submenuitem" onclick=""><img src="res/img/download.png" align="left" />Download visible as Excel</td></tr>
				<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}__action.value='{req_tag}download_all';__show.value='download';submit();"><img src="res/img/download.png" align="left" />Download all as text</td></tr>
				<tr><td class="submenuitem" /><img src="res/img/download.png" align="left" />Download all as Excel</td></tr>
				<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}document.forms.${matrix.screenName}.__target.value='${matrix.screenName}';document.forms.${matrix.screenName}.__action.value = 'resetMatrixRenderer';document.forms.${matrix.screenName}.submit();"><img src="res/img/update.gif" align="left" />Reset viewer</td></tr>
				</table>
			</div>											
		</td>
		
		<td align="center" class="shadeHeader" valign="center">
			<input type="image" src="res/img/first.png" onclick="__action.value = '${req_tag}moveFarLeft';" />
			<input type="image" src="res/img/prev.png" onclick="__action.value = '${req_tag}moveLeft';"/>
			<b><font class="fontColor">${colHeader}</font></b>
			<input type="image" src="res/img/next.png" onclick="__action.value = '${req_tag}moveRight';"/>
			<input type="image" src="res/img/last.png"  onclick="__action.value = '${req_tag}moveFarRight';" />
		</td>
	</tr>
	<tr>
		<td rowspan="2" class="shadeHeader" align="right">
			<input type="image" src="res/img/rowStart.png" onclick="__action.value = '${req_tag}moveFarUp';"/><br>
			<input type="image" src="res/img/up.png" onclick="__action.value = '${req_tag}moveUp';"/><br>
			<b><font class="fontColor">${rowHeader}</font></b><br>
			<input type="image" src="res/img/down.png" onclick="__action.value = '${req_tag}moveDown';"/><br>
			<input type="image" src="res/img/rowStop.png" onclick="__action.value = '${req_tag}moveFarDown';"/><br>
			<br>
			<table>
				<tr><td><font class="fontColor">Stepsize</font></td><td><input type="text" name="${req_tag}stepSize" value="${matrix.stepSize}" size="1"></td></tr>
				<tr><td><font class="fontColor">Width</font></td><td><input type="text" name="${req_tag}width" value="${matrix.visibleCols?size}" size="1"></td></tr>
				<tr><td><font class="fontColor">Height</font></td><td><input type="text" name="${req_tag}height" value="${matrix.visibleRows?size}" size="1"></td></tr>
				<tr><td colspan="2"><input type="submit" value="Update" onclick="__action.value = '${req_tag}changeSubmatrixSize'; submit();"></td></tr>
				<tr><td colspan="2">&nbsp;</td></tr>
				<tr><td colspan="2"><input type="submit" value="Apply filters..." onclick="__action.value = '${req_tag}filter'; submit();"></td></tr>
				<tr><td colspan="2">
					<select name="FILTER_SELECTION_TYPE">
						<option value="evr">on everything</option>
						<option value="vis">on visible</option>
					</select>
				</td></tr>
			</table>
		</td>
		<td>
			<!-- empty -->
		</td>
	</tr>
	<tr>
		<td>
			
			<table class="tableBorder">
				<tr>
					<td colspan="2"></td>
					<td colspan="${matrix.visibleCols?size}">Apply filter on column header:
						<select name="FILTER_ATTRIBUTE_COL_HEADER">
							<#list matrix.colHeaderFilterAttributes as att><option value="${att}">${att}</option></#list>
						</select>
						<select name="FILTER_OPERATOR_COL_HEADER">
							<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
						</select>
						<input type="text" size="4" name="FILTER_VALUE_COL_HEADER"></input></nobr>
					</td>
				</tr>
			
				<tr>
					<td colspan="2">Apply filter on row header:</td>
					<#list matrix.visibleCols as col>
						<td class="matrixTableCell colorOfTitle"><b>${matrix.renderCol(col)}</b></td>
					</#list>
				</tr>
				<tr>
					<td colspan="2"><nobr>
						<select name="FILTER_ATTRIBUTE_ROW_HEADER">
							<#list matrix.rowHeaderFilterAttributes as att><option value="${att}">${att}</option></#list>
						</select>
						<select name="FILTER_OPERATOR_ROW_HEADER">
							<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
						</select>
						<input type="text" size="4" name="FILTER_VALUE_ROW_HEADER"></input></nobr>
					</td>
					<#list matrix.visibleCols as col>
						<td><nobr>
							<select name="FILTER_OPERATOR_COL_${col_index}">
								<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
							</select>
							<input type="text" size="4" name="FILTER_VALUE_COL_${col_index}"></input></nobr>
						</td>
					</#list>
				</tr>
				<#list matrix.visibleRows as row> 
					<tr>
						<td class="matrixTableCell colorOfTitle">
							<b>${matrix.renderRow(row)}</b>
						</td>
						<td>
							<nobr>
							<select name="FILTER_OPERATOR_ROW_${row_index}">
								<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
							</select>
							<input type="text" size="4" name="FILTER_VALUE_ROW_${row_index}"></input></nobr>
						</td>
						<#list 0..matrix.visibleCols?size-1 as i>								
			  				<td class="matrixTableCell matrixRowColor<#if row_index%2==0>1<#else>0</#if>">${matrix.renderValue(matrix.visibleValues[row_index][i])}</td>
						</#list> 
					</tr>
				</#list>
			</table>
			
		</td>
	</tr>
</table>