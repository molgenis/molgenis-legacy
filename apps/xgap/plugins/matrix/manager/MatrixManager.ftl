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
		
		<#-- Hack to immediatly clear the message so it doesn't "stick". -->
		${screen.clearMessage()}


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
	No data storage backend for selected source type found. Please select your data matrix file and proceed with upload into this source.<br>
	<input type="file" name="upload"/>
	<input type="submit" value="Upload" onclick="__action.value='upload';return true;"/><br>
	<br>
	Alternatively, use this textarea to input your data.<br>
	<textarea name="inputTextArea" rows="7" cols="30"><#if model.uploadTextAreaContent?exists>${model.uploadTextAreaContent}</#if></textarea>
	<input type="submit" value="Upload" onclick="__action.value='uploadTextArea';return true;"/><br>
				
<#else>
<div style="overflow: auto; width: inherit;">
	<table>
		<tr>
			<td class="menuitem shadeHeader" onclick="mopen('matrix_plugin_FileSub');">
				Menu
				<img src="res/img/pulldown.gif"/><br>
				<div class="submenu" id="matrix_plugin_FileSub">
					<table>
						<tr><td class="submenuitem" onclick="location.href='downloadmatrixascsv?id=${model.selectedData.getId()?c}&download=some&coff=${browser.colStart}&clim=${browser.colStop-browser.colStart}&roff=${browser.rowStart}&rlim=${browser.rowStop-browser.rowStart}&stream=false'"><img src="res/img/download.png" align="left" />Download visible as text</td></tr>
						<tr><td class="submenuitem" onclick="location.href='downloadmatrixasexcel?id=${model.selectedData.getId()?c}&download=some&coff=${browser.colStart}&clim=${browser.colStop-browser.colStart}&roff=${browser.rowStart}&rlim=${browser.rowStop-browser.rowStart}'"><img src="res/img/download.png" align="left" />Download visible as Excel</td></tr>
						<tr><td class="submenuitem" onclick="location.href='downloadmatrixascsv?id=${model.selectedData.getId()?c}&download=all&stream=false'"><img src="res/img/download.png" align="left" />Download all as text</td></tr>
						<tr><td class="submenuitem" onclick="location.href='downloadmatrixasexcel?id=${model.selectedData.getId()?c}&download=all'"><img src="res/img/download.png" align="left" />Download all as Excel</td></tr>
						<#if model.selectedData.source == "Binary" && model.hasBackend == true><tr><td class="submenuitem" onclick="location.href='downloadfile?name=${model.selectedData.name}'"><img src="res/img/download.png" align="left" />Download all as binary</td></tr></#if>
						<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}document.forms.${screen.name}.__target.value='${screen.name}';document.forms.${screen.name}.__action.value = 'refresh';document.forms.${screen.name}.submit();"><img src="res/img/update.gif" align="left" />Reset viewer</td></tr>
					</table>
				</div>											
			</td>
			
			<td align="center" class="shadeHeader" valign="center">
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
					<tr><td><input type="submit" value="Change" onclick="document.forms.${screen.name}.__action.value = 'changeSubmatrixSize'; document.forms.${screen.name}.submit();"></td></tr>
					<tr><td><input type="submit" value="Filter visible" onclick="document.forms.${screen.name}.__action.value = 'filterVisible'; document.forms.${screen.name}.submit();"></td></tr>
					<tr><td><input type="submit" value="Filter all" onclick="document.forms.${screen.name}.__action.value = 'filterAll'; document.forms.${screen.name}.submit();"></td></tr>
				</table>
			</td>
			<td>
				<!-- leeg -->
			<td/>
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
			<td colspan="2">
				<table class="tableBorder">
					<tr>
						<td></td><td></td>
						<#list browser.subMatrix.colNames as n>
							<td class="matrixTableCell colorOfTitle">
								${model.renderCol(n)}
							</td>
						</#list>
					</tr>
					<tr>
						<td></td><td></td>
						<#list browser.subMatrix.colNames as n>
							<td><nobr><select name="FILTER_OPERATOR_COL_${n}"><#if model.selectedData.valuetype == "Decimal"><option value="GREATER">&gt;</option><option value="GREATER_EQUAL">&gt;=</option><option value="LESS">&lt;</option><option value="LESS_EQUAL">&lt;=</option></#if><option value="EQUALS">==</option></select><input type="text" size="4" name="FILTER_VALUE_COL_${n}"></input></nobr></td>
						</#list>
					</tr>
					<#list browser.subMatrix.rowNames as n> 
						<tr>
							<td class="matrixTableCell colorOfTitle">
								${model.renderRow(n)}
							</td>

							<td><nobr><select name="FILTER_OPERATOR_ROW_${n}"><#if model.selectedData.valuetype == "Decimal"><option value="GREATER">&gt;</option><option value="GREATER_EQUAL">&gt;=</option><option value="LESS">&lt;</option><option value="LESS_EQUAL">&lt;=</option></#if><option value="EQUALS">==</option></select><input type="text" size="4" name="FILTER_VALUE_ROW_${n}"></input></nobr></td>
							
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
</div>
</#if>

</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
