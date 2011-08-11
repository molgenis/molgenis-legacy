name: <#if name??>${name}<#else>null</#if><br>
label: <#if value??>${value}<#else>null</#if><br>
rowIndex: ${matrix.rowIndex}<br>
colIndex: ${matrix.colIndex}<br>
totalNumberOfRows: ${matrix.totalNumberOfRows}<br>
totalNumberOfCols: ${matrix.totalNumberOfCols}<br>
filteredNumberOfRows: ${matrix.filteredNumberOfRows}<br>
filteredNumberOfCols: ${matrix.filteredNumberOfCols}<br>


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