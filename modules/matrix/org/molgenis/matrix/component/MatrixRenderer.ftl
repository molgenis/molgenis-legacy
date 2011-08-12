<#if name??>${name}<#else>null</#if><br>
<!-- label: <#if value??>${value}<#else>null</#if><br>
rowIndex: ${matrix.rowIndex}<br>
colIndex: ${matrix.colIndex}<br>
totalNumberOfRows: ${matrix.totalNumberOfRows}<br>
totalNumberOfCols: ${matrix.totalNumberOfCols}<br>
filteredNumberOfRows: ${matrix.filteredNumberOfRows}<br>
filteredNumberOfCols: ${matrix.filteredNumberOfCols}<br> -->

<#assign matrix_component_request_tag = "matrix_component_request_tag_">

<table>
	<tr>
		<td class="menuitem shadeHeader" onclick="mopen('matrix_component_menu');">
			Menu
			<img src="res/img/pulldown.gif"/><br>
			<div class="submenu" id="matrix_component_menu">
				<table>
					<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}__action.value='${matrix_component_request_tag}download_visible';__show.value='download';submit();"><img src="res/img/download.png" align="left" />Download visible as text</td></tr>
					<tr><td class="submenuitem" onclick=""><img src="res/img/download.png" align="left" />Download visible as Excel</td></tr>
					<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}__action.value='{matrix_component_request_tag}download_all';__show.value='download';submit();"><img src="res/img/download.png" align="left" />Download all as text</td></tr>
					<tr><td class="" />Download all as Excel</td></tr>
					<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}__action.value = '{matrix_component_request_tag}refresh';submit();"><img src="res/img/update.gif" align="left" />Reset viewer</td></tr>
				</table>
			</div>											
		</td>
		
		<td align="center" class="shadeHeader" valign="center">
			<input type="image" src="res/img/first.png" onclick="__action.value = '${matrix_component_request_tag}moveFarLeft';" />
			<input type="image" src="res/img/prev.png" onclick="__action.value = '${matrix_component_request_tag}moveLeft';"/>
			
			<#if matrix.filteredNumberOfCols == matrix.totalNumberOfCols>
				<#assign colHeader = "COLTYPE?? " + matrix.colIndex + "-" + (matrix.colIndex+matrix.visibleCols?size) + " of " + matrix.totalNumberOfCols>
			<#else>
				<#assign colHeader = "COLTYPE?? " +matrix.colIndex + "-" + (matrix.colIndex+matrix.visibleCols?size) + " of " + matrix.filteredNumberOfCols + " filtered results (total " + matrix.totalNumberOfCols + ")">
			</#if>
			
			<b><font class="fontColor">${colHeader}</font></b>
			<input type="image" src="res/img/next.png" onclick="__action.value = '${matrix_component_request_tag}moveRight';"/>
			<input type="image" src="res/img/last.png"  onclick="__action.value = '${matrix_component_request_tag}moveFarRight';" />
		</td>
		
		
		<#if matrix.filteredNumberOfRows == matrix.totalNumberOfRows>
			<#assign rowHeader = "ROWTYPE?? " + matrix.rowIndex + "-" + (matrix.rowIndex+matrix.visibleRows?size) + " of " + matrix.totalNumberOfRowss>
		<#else>
			<#assign rowHeader = "ROWTYPE?? " + matrix.rowIndex + "-" + (matrix.rowIndex+matrix.visibleRows?size) + " of " + matrix.filteredNumberOfRows + " filtered results (total " + matrix.totalNumberOfRows + ")">
		</#if>
		
		</tr>
</table>

<table class="tableBorder">
	<tr>
		<td></td><td></td>
		<#list matrix.visibleCols as col>
			<td class="matrixTableCell colorOfTitle"><b>${matrix.renderCol(col)}</b></td>
		</#list>
	</tr>
	<tr>
		<td></td><td></td>
		<#list matrix.visibleCols as col>
			<td><nobr><select name="FILTER_OPERATOR_COL_${col_index}"><option value="GREATER">&gt;</option><option value="GREATER_EQUAL">&gt;=</option><option value="LESS">&lt;</option><option value="LESS_EQUAL">&lt;=</option><option value="EQUALS">==</option></select><input type="text" size="4" name="FILTER_VALUE_COL_${col_index}"></input></nobr></td>
		</#list>
	</tr>
	<#list matrix.visibleRows as row> 
		<tr>
			<td class="matrixTableCell colorOfTitle">
				<b>${matrix.renderRow(row)}</b>
			</td>
			<td><nobr><select name="FILTER_OPERATOR_ROW_${row_index}"><option value="GREATER">&gt;</option><option value="GREATER_EQUAL">&gt;=</option><option value="LESS">&lt;</option><option value="LESS_EQUAL">&lt;=</option><option value="EQUALS">==</option></select><input type="text" size="4" name="FILTER_VALUE_ROW_${row_index}"></input></nobr></td>
			<#list 0..matrix.visibleCols?size-1 as i>								
  				<td class="matrixTableCell matrixRowColor<#if row_index%2==0>1<#else>0</#if>">${matrix.renderValue(matrix.visibleValues[row_index][i])}</td>
			</#list> 
		</tr>
	</#list>
</table>