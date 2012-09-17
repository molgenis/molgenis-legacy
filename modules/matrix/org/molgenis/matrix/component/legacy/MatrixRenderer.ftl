

<#assign colHeader = matrix.colType + " " + (matrix.colIndices[0]) + " ~ " + (matrix.colIndices[matrix.colIndices?size-1]) + " of " + (matrix.totalNumberOfCols-1)>
<#assign rowHeader = matrix.rowType + "<br>" + (matrix.rowIndices[0]) + " ~ " + (matrix.rowIndices[matrix.rowIndices?size-1]) + "<br>of " + (matrix.totalNumberOfRows-1)>


<table cellpadding="3">
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
				<tr><td colspan="2"><input type="submit" value="Update" onclick="__action.value = '${req_tag}updatePagingSettings'; submit();"></td></tr>
				<tr><td colspan="2">&nbsp;</td></tr>
				<tr><td colspan="2"><!--input type="submit" value="Apply filters..." onclick="__action.value = '${req_tag}filter'; submit();"--></td></tr>
				<tr><td colspan="2">
					<!--select name="FILTER_SELECTION_TYPE">
						<option value="evr">on everything</option>
						<option value="vis">on visible</option>
					</select-->
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
					<td colspan="${matrix.visibleCols?size}"></td>
					<#--td colspan="${matrix.visibleCols?size}">Apply filter on column header:
						<select name="FILTER_ATTRIBUTE_COL_HEADER">
							<#list matrix.colHeaderFilterAttributes as att><option value="${att}">${att}</option></#list>
						</select>
						<select name="FILTER_OPERATOR_COL_HEADER">
							<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
						</select>
						<input type="text" size="4" name="FILTER_VALUE_COL_HEADER"></input></nobr>
					</td-->
				</tr>
			
				<tr>
					<td colspan="2"><#--Apply filter on row header:--></td>
					<#list matrix.visibleCols as col>
						<td class="matrixTableCell colorOfTitle"><b>${matrix.renderDescriptor.renderCol(col)}</b></td>
					</#list>
				</tr>
				<tr>
				<td colspan="2"></td>
					<#-->td colspan="2"><nobr>
						<select name="FILTER_ATTRIBUTE_ROW_HEADER">
							<#list matrix.rowHeaderFilterAttributes as att><option value="${att}">${att}</option></#list>
						</select>
						<select name="FILTER_OPERATOR_ROW_HEADER">
							<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
						</select>
						<input type="text" size="4" name="FILTER_VALUE_ROW_HEADER"></input></nobr>
					</td-->
					<#list matrix.visibleCols as col>
						<td>
							<div style="font-family: Courier, 'Courier New', monospace">${matrix.colIndices[col_index]}</div>
						
							<#--nobr>
							<select name="FILTER_OPERATOR_COL_${col_index}">
								<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
							</select>
							<input type="text" size="4" name="FILTER_VALUE_COL_${col_index}"></input></nobr-->
						</td>
					</#list>
				</tr>
				<#list matrix.visibleRows as row> 
					<tr>
						<td class="matrixTableCell colorOfTitle"><b>${matrix.renderDescriptor.renderRow(row)}</b></td>
						<td>
							<div style="font-family: Courier, 'Courier New', monospace">${matrix.rowIndices[row_index]}</div>
							<#--nobr>
							<select name="FILTER_OPERATOR_ROW_${row_index}">
								<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
							</select>
							<input type="text" size="4" name="FILTER_VALUE_ROW_${row_index}"></input></nobr-->
						</td>
						<#list 0..matrix.visibleCols?size-1 as i>								
			  				<td class="matrixTableCell matrixRowColor<#if row_index%2==0>1<#else>0</#if>">${matrix.renderDescriptor.renderValue(matrix.visibleValues[row_index][i])}</td>
						</#list> 
					</tr>
				</#list>
			</table>
		</td>
	</tr>
</table>

<br>
<i>Applied filters, in order:</i>
<table cellpadding="5">
	<#assign filterIndex = 0>
	<!-- First show non-paging filters. TODO: remove duplication! -->
	<#list matrix.otherFilters as filter>
	<tr>
		<td>${filter}</td>
		<!-- Remove button -->
		<td><input type="image" src="generated-res/img/exit.bmp" onclick="__action.value = '${req_tag}remove_filter${filterIndex}'; submit();"/></td>
		<!-- Push down button -->
		<td>
		<#if filterIndex != 0>
			<input type="image" src="generated-res/img/sort_desc.gif" onclick="__action.value = '${req_tag}push_filter_down${filterIndex}'; submit();"/>
		</#if>
		</td>
		<!-- Push up button -->
		<td>
		<#if filterIndex < matrix.otherFilters?size-1>
			<input type="image" src="generated-res/img/sort_asc.gif" onclick="__action.value = '${req_tag}push_filter_up${filterIndex}'; submit();"/>
		</#if>
		</td>
	</tr>
		<#assign filterIndex = filterIndex + 1>
	</#list>
	<!-- Then show paging filters. TODO: remove duplication! -->
	<#assign pagingFilterIndex = 0>
	<#list matrix.pagingFilters as filter>
	<tr>
		<td>${filter}</td>
		<!-- Remove button -->
		<td><input type="image" src="generated-res/img/exit.bmp" onclick="__action.value = '${req_tag}remove_filter${filterIndex}'; submit();"/></td>
		<!-- Push down button -->
		<td>
		<#if pagingFilterIndex != 0>
			<input type="image" src="generated-res/img/sort_desc.gif" onclick="__action.value = '${req_tag}push_filter_down${filterIndex}'; submit();"/>
		</#if>
		</td>
		<!-- Push up button -->
		<td>
		<#if pagingFilterIndex < matrix.pagingFilters?size-1>
			<input type="image" src="generated-res/img/sort_asc.gif" onclick="__action.value = '${req_tag}push_filter_up${filterIndex}'; submit();"/>
		</#if>
		</td>
	</tr>
		<#assign filterIndex = filterIndex + 1>
		<#assign pagingFilterIndex = pagingFilterIndex + 1>
	</#list>
</table>

<br><i>Add a new filter:</i><br>


<table cellpadding="10"><tr>
<td><input name="filterSelect" type="radio" onclick="display('show', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');" checked>By index</td>
<td><input name="filterSelect" type="radio" onclick="display('show', 'filter2');display('hide', 'filter1');display('hide', 'filter3');display('hide', 'filter4');display('hide', 'filter5');">By column values</td>
<td><input name="filterSelect" type="radio" onclick="display('show', 'filter3');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter4');display('hide', 'filter5');">By row values</td>
<td><input name="filterSelect" type="radio" onclick="display('show', 'filter4');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter5');">By column attributes</td>
<td><input name="filterSelect" type="radio" onclick="display('show', 'filter5');display('hide', 'filter1');display('hide', 'filter2');display('hide', 'filter3');display('hide', 'filter4');">By row attributes</td>
</tr></table>

<br>

<div id="filter1">
	<table>
		<tr>
			<td>
				Filter by index:
			</td>
			<td>
				<select name="${req_tag}add_filter_by_indexFILTER_FIELD">
					<option value="row">${matrix.rowType} index</option>
					<option value="col">${matrix.colType} index</option>
				</select>
			</td>
			<td>
				<select name="${req_tag}add_filter_by_indexFILTER_OPERATOR">
					<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
				</select>
			</td>
			<td>
				<input type="text" size="8" name="${req_tag}add_filter_by_indexFILTER_VALUE" />
			</td>
			<td>
				<input type="submit" value="Apply" onclick="__action.value = '${req_tag}add_filter_by_index'; submit();">
			</td>
		</tr>
		<tr>
	</table>
</div>
<div id="filter2" style="display:none">
	<table>
		<tr>
			<td>
				Filter by column values:
			</td>
			<td>
				<select name="${req_tag}add_filter_by_col_valueFILTER_FIELD">
					<#list matrix.visibleCols as col><option value="col_${matrix.colIndices[col_index]}">${matrix.renderDescriptor.renderColSimple(col)}</option></#list>
				</select>
			</td>
			<td>
				<select name="${req_tag}add_filter_by_col_valueFILTER_OPERATOR">
					<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
				</select>
			</td>
			<td>
				<input type="text" size="8" name="${req_tag}add_filter_by_col_valueFILTER_VALUE" />
			</td>
			<td>
				<input type="submit" value="Apply" onclick="__action.value = '${req_tag}add_filter_by_col_value'; submit();">
			</td>
		</tr>
	</table>
</div>
<div id="filter3" style="display:none">
	<table>
		<tr>
			<td>
				Filter by row values:
			</td>
			<td>
				<select name="${req_tag}add_filter_by_row_valueFILTER_FIELD">
					<#list matrix.visibleRows as row><option value="row_${matrix.rowIndices[row_index]}">${matrix.renderDescriptor.renderRowSimple(row)}</option></#list>
				</select>
			</td>
			<td>
				<select name="${req_tag}add_filter_by_row_valueFILTER_OPERATOR">
					<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
				</select>
			</td>
			<td>
				<input type="text" size="8" name="${req_tag}add_filter_by_row_valueFILTER_VALUE" />
			</td>
			<td>
				<input type="submit" value="Apply" onclick="__action.value = '${req_tag}add_filter_by_row_value'; submit();">
			</td>
		</tr>
	</table>
</div>
<div id="filter4" style="display:none">
	<table>
		<tr>
			<td>
				Filter by column attributes:
			</td>
			<td>
				<select name="${req_tag}add_filter_by_col_attrbFILTER_FIELD">
					<#list matrix.colHeaderFilterAttributes as cha>
						<option value="col_att_${cha}">${cha}</option>
					</#list>
				</select>
			</td>
			<td>
				<select name="${req_tag}add_filter_by_col_attrbFILTER_OPERATOR">
					<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
				</select>
			</td>
			<td>
				<input type="text" size="8" name="${req_tag}add_filter_by_col_attrbFILTER_VALUE" />
			</td>
			<td>
				<input type="submit" value="Apply" onclick="__action.value = '${req_tag}add_filter_by_col_attrb'; submit();">
			</td>
		</tr>
	</table>
</div>
<div id="filter5" style="display:none">
	<table>
		<tr>
			<td>
				Filter by row attributes:
			</td>
			<td>
				<select name="${req_tag}add_filter_by_row_attrbFILTER_FIELD">
					<#list matrix.rowHeaderFilterAttributes as rha>
						<option value="row_att_${rha}">${rha}</option>
					</#list>
				</select>
			</td>
			<td>
				<select name="${req_tag}add_filter_by_row_attrbFILTER_OPERATOR">
					<#list operators?keys as op><option value="${op}">${operators[op]}</option></#list>
				</select>
			</td>
			<td>
				<input type="text" size="8" name="${req_tag}add_filter_by_row_attrbFILTER_VALUE" />
			</td>
			<td>
				<input type="submit" value="Apply" onclick="__action.value = '${req_tag}add_filter_by_row_attrb'; submit();">
			</td>
		</tr>
	</table>
</div>
