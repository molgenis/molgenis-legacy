name: ${name}<br>
label: <#if value??>${value}<#else>null</#if><br>
rows: ${matrix.totalNumberOfRows}
cols: ${matrix.totalNumberOfCols}

<table>
	<tr>
		<td></td>
		<#list matrix.visibleCols as col>
			<td>${matrix.renderCol(col)}</td>
		</#list>
	</tr>
	<#list matrix.visibleRows as row>
		<tr>
		<td>${matrix.renderRow(row)}</td>
			<#list matrix.visibleCols as col>
				<td>${matrix.renderValue(matrix.visibleValues[row_index][col_index])}
			</#list>
		</tr>
	</#list>
</table>