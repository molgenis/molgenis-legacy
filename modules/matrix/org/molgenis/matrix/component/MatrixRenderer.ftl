<#--if name??>${name}<#else>null</#if><br-->
<!-- label: <#if value??>${value}<#else>null</#if><br>
rowIndex: ${matrix.rowIndex}<br>
colIndex: ${matrix.colIndex}<br>
totalNumberOfRows: ${matrix.totalNumberOfRows}<br>
totalNumberOfCols: ${matrix.totalNumberOfCols}<br>
filteredNumberOfRows: ${matrix.filteredNumberOfRows}<br>
filteredNumberOfCols: ${matrix.filteredNumberOfCols}<br> -->

<#if matrix.filteredNumberOfCols == matrix.totalNumberOfCols>
	<#assign colHeader = "COLTYPE " + matrix.colIndex + "-" + (matrix.colIndex+matrix.visibleCols?size) + " of " + matrix.totalNumberOfCols>
<#else>
	<#assign colHeader = "COLTYPE " +matrix.colIndex + "-" + (matrix.colIndex+matrix.visibleCols?size) + " of " + matrix.filteredNumberOfCols + " filtered results (total " + matrix.totalNumberOfCols + ")">
</#if>
	
<#if matrix.filteredNumberOfRows == matrix.totalNumberOfRows>
	<#assign rowHeader = "ROWTYPE " + matrix.rowIndex + "-" + (matrix.rowIndex+matrix.visibleRows?size) + " of " + matrix.totalNumberOfRows>
<#else>
	<#assign rowHeader = "ROWTYPE " + matrix.rowIndex + "-" + (matrix.rowIndex+matrix.visibleRows?size) + " of " + matrix.filteredNumberOfRows + " filtered results (total " + matrix.totalNumberOfRows + ")">
</#if>

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
				<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}__action.value = '{req_tag}refresh';submit();"><img src="res/img/update.gif" align="left" />Reset viewer</td></tr>
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
				<tr><td><font class="fontColor">Stepsize</font></td><td><input type="text" name="stepSize" value="5" size="1"></td></tr>
				<tr><td><font class="fontColor">Width</font></td><td><input type="text" name="width" value="5" size="1"></td></tr>
				<tr><td><font class="fontColor">Height</font></td><td><input type="text" name="height" value="10" size="1"></td></tr>
				<tr><td><input type="submit" value="Change" onclick="__action.value = '${req_tag}changeSubmatrixSize'; submit();"></td></tr>
				<tr><td><input type="submit" value="Filter visible" onclick="__action.value = '${req_tag}filterVisible'; submit();"></td></tr>
				<tr><td><input type="submit" value="Filter all" onclick="__action.value = '${req_tag}filterAll'; submit();"></td></tr>
			</table>
		</td>
		<td>
			<!-- leeg -->
		<td/>
		<td valign="top">
			<!-- leeg -->
		</td>
	</tr>
	<tr>
		<td colspan="2">
			
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
			
		</td>
	</tr>
</table>