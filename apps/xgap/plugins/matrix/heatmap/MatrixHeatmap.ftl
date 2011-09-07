<#macro MatrixHeatmap screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	
	
	<input type="hidden" name="__rStartVal" />
	<input type="hidden" name="__gStartVal" />
	<input type="hidden" name="__bStartVal" />
	
	<input type="hidden" name="__rStopVal" />
	<input type="hidden" name="__gStopVal" />
	<input type="hidden" name="__bStopVal" />
	
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

Starting color:

<table class="color-picker" cellspacing="2" cellpadding="0" border="0">
<col style="width: 40px" />
<col style="" />
<col style="width: 10px" />
<col style="width: 50px" />
<tr>
	<td><label for="red-slider-start">Red:</label></td>
	<td>
		<div class="slider" id="red-slider-start" tabIndex="1">
			<input class="slider-input" id="red-slider-input-start" />
		</div>
	</td>
	<td><input id="red-input-start" maxlength="3" tabIndex="2" /></td>
	<td rowspan="3" id="color-result-start"></td>
</tr>
<tr>
	<td><label for="green-slider-start">Green:</label></td>
	<td>
		<div class="slider" id="green-slider-start" tabIndex="3">
			<input class="slider-input" id="green-slider-input-start" />
		</div>
	</td>
	<td><input id="green-input-start" maxlength="3" tabIndex="4" /></td>
</tr>
<tr>
	<td><label for="blue-slider-start">Blue:</label></td>
	<td>
		<div class="slider" id="blue-slider-start" tabIndex="5">
			<input class="slider-input" id="blue-slider-input-start" />
		</div>
	</td>
	<td><input id="blue-input-start" maxlength="3" tabIndex="6" /></td>
</tr>
</table>

<br>
Ending color:

<table class="color-picker" cellspacing="2" cellpadding="0" border="0">
<col style="width: 40px" />
<col style="" />
<col style="width: 10px" />
<col style="width: 50px" />
<tr>
	<td><label for="red-slider-stop">Red:</label></td>
	<td>
		<div class="slider" id="red-slider-stop" tabIndex="1">
			<input class="slider-input" id="red-slider-input-stop" />
		</div>
	</td>
	<td><input id="red-input-stop" maxlength="3" tabIndex="2" /></td>
	<td rowspan="3" id="color-result-stop"></td>
</tr>
<tr>
	<td><label for="green-slider-stop">Green:</label></td>
	<td>
		<div class="slider" id="green-slider-stop" tabIndex="3">
			<input class="slider-input" id="green-slider-input-stop" />
		</div>
	</td>
	<td><input id="green-input-stop" maxlength="3" tabIndex="4" /></td>
</tr>
<tr>
	<td><label for="blue-slider-stop">Blue:</label></td>
	<td>
		<div class="slider" id="blue-slider-stop" tabIndex="5">
			<input class="slider-input" id="blue-slider-input-stop" />
		</div>
	</td>
	<td><input id="blue-input-stop" maxlength="3" tabIndex="6" /></td>
</tr>
</table>



<script type="text/javascript">

// init code
var rStart = new Slider(document.getElementById("red-slider-start"), document.getElementById("red-slider-input-start"));
rStart.setMaximum(255);
var gStart = new Slider(document.getElementById("green-slider-start"), document.getElementById("green-slider-input-start"));
gStart.setMaximum(255);
var bStart = new Slider(document.getElementById("blue-slider-start"), document.getElementById("blue-slider-input-start"));
bStart.setMaximum(255);

var rStop = new Slider(document.getElementById("red-slider-stop"), document.getElementById("red-slider-input-stop"));
rStop.setMaximum(255);
var gStop = new Slider(document.getElementById("green-slider-stop"), document.getElementById("green-slider-input-stop"));
gStop.setMaximum(255);
var bStop = new Slider(document.getElementById("blue-slider-stop"), document.getElementById("blue-slider-input-stop"));
bStop.setMaximum(255);

var riStart = document.getElementById("red-input-start");
riStart.onchange = function () {
	rStart.setValue(parseInt(this.value));
};

var giStart = document.getElementById("green-input-start");
giStart.onchange = function () {
	gStart.setValue(parseInt(this.value));
};

var biStart = document.getElementById("blue-input-start");
biStart.onchange = function () {
	bStart.setValue(parseInt(this.value));
};

var riStop = document.getElementById("red-input-stop");
riStop.onchange = function () {
	rStop.setValue(parseInt(this.value));
};

var giStop = document.getElementById("green-input-stop");
giStop.onchange = function () {
	gStop.setValue(parseInt(this.value));
};

var biStop = document.getElementById("blue-input-stop");
biStop.onchange = function () {
	bStop.setValue(parseInt(this.value));
};

rStart.onchange = gStart.onchange = bStart.onchange = function () {
	var crStart = document.getElementById("color-result-start");
	crStart.style.backgroundColor = "rgb(" + rStart.getValue() + "," + 
								gStart.getValue() + "," + 
								bStart.getValue() + ")";
	riStart.value = rStart.getValue();
	giStart.value = gStart.getValue();
	biStart.value = bStart.getValue();
	
	if (typeof window.onchange == "function")
		window.onchange();
};

rStop.onchange = gStop.onchange = bStop.onchange = function () {
	var crStop = document.getElementById("color-result-stop");
	crStop.style.backgroundColor = "rgb(" + rStop.getValue() + "," + 
								gStop.getValue() + "," + 
								bStop.getValue() + ")";
	riStop.value = rStop.getValue();
	giStop.value = gStop.getValue();
	biStop.value = bStop.getValue();
	
	if (typeof window.onchange == "function")
		window.onchange();
};



<#-- BUG HACKAROUND! can't render 0,0,0 -->
<#if model.start.r == 0 && model.start.g == 0 && model.start.b == 0>
	rStart.setValue(0);
	gStart.setValue(0);
	bStart.setValue(1);
<#else>
	rStart.setValue(${model.start.r});
	gStart.setValue(${model.start.g});
	bStart.setValue(${model.start.b});
</#if>
<#if model.stop.r == 0 && model.stop.g == 0 && model.stop.b == 0>
	rStop.setValue(0);
	gStop.setValue(0);
	bStop.setValue(1);
<#else>
	rStop.setValue(${model.stop.r});
	gStop.setValue(${model.stop.g});
	bStop.setValue(${model.stop.b});
</#if>

// end init

//function setRgb(nRed, nGreen, nBlue) {
//	r.setValue(nRed);
//	g.setValue(nGreen);
//	b.setValue(nBlue);
//}

//function getRgb() {
//	return {
//		r:	r.getValue(),
//		g:	g.getValue(),
//		b:	b.getValue()
//	};
//}

function fixSize() {
	rStart.recalculate();
	gStart.recalculate();
	bStart.recalculate();
	rStop.recalculate();
	gStop.recalculate();
	bStop.recalculate();
}

window.onresize = fixSize;

fixSize();

</script>

<br>
<input type="submit" value="Draw new colors" onclick="document.forms.${screen.name}.__rStartVal.value = rStart.getValue(); document.forms.${screen.name}.__gStartVal.value = gStart.getValue(); document.forms.${screen.name}.__bStartVal.value = bStart.getValue(); document.forms.${screen.name}.__rStopVal.value = rStop.getValue(); document.forms.${screen.name}.__gStopVal.value = gStop.getValue(); document.forms.${screen.name}.__bStopVal.value = bStop.getValue(); document.forms.${screen.name}.__action.value = 'draw'; document.forms.${screen.name}.submit();"/><br>
<br>

<#if model.lowestVal?exists && model.highestVal?exists>
<table border="1"><tr><td>
<#if model.autoScale?exists && model.autoScale == false>Customly scaled between ${model.customStart} and ${model.customStop}<#else>Automatically scaled between ${model.lowestVal} and ${model.highestVal}</#if> <br>
<#if model.autoScale?exists && model.autoScale == false>
Remove custom scale <input type="submit" value="Remove" onclick="document.forms.${screen.name}.__action.value = 'removeCustomScale'; document.forms.${screen.name}.submit();"><br>
</#if>
Use custom <input type="text" name="customScaleStart" size="3"> - <input type="text" name="customScaleStop" size="3"> <input type="submit" value="Set" onclick="document.forms.${screen.name}.__action.value = 'addCustomScale'; document.forms.${screen.name}.submit();"><br>
</td></tr></table>
<#else>
Scaled between <b>NA - NA</b><br>
</#if>

<br>

	<table>
		<tr>
			<td class="menuitem shadeHeader" onclick="mopen('matrix_plugin_FileSub');">
				Menu
				<img src="res/img/pulldown.gif"/><br>
				<div class="submenu" id="matrix_plugin_FileSub">
					<table>
						<#-->tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}document.forms.${screen.name}.__target.value='${screen.name}';document.forms.${screen.name}.__action.value='download_visible';document.forms.${screen.name}.__show.value='download';document.forms.${screen.name}.submit();"><img src="res/img/download.png" align="left" />Download visible as text</td></tr>
						<tr><td class="submenuitem" onclick="location.href='downloadmatrixasexcel?id=${model.selectedData.id?c}&download=some&coff=${browser.colStart}&clim=${browser.colStop-browser.colStart}&roff=${browser.rowStart}&rlim=${browser.rowStop-browser.rowStart}'"><img src="res/img/download.png" align="left" />Download visible as Excel</td></tr>
						<tr><td class="submenuitem" onclick="if( window.name == '' ){ window.name = 'molgenis'+Math.random();}document.forms.${screen.name}.__target.value='${screen.name}';document.forms.${screen.name}.__action.value='download_all';document.forms.${screen.name}.__show.value='download';document.forms.${screen.name}.submit();"><img src="res/img/download.png" align="left" />Download all as text</td></tr>
						<tr><td class="submenuitem" onclick="location.href='downloadmatrixasexcel?id=${model.selectedData.id?c}&download=all'"><img src="res/img/download.png" align="left" />Download all as Excel</td></tr-->
						<#if model.selectedData.source == "BinaryFile" && model.hasBackend == true><tr><td class="submenuitem" onclick="location.href='downloadfile?id=${model.selectedData.id?c}'"><img src="res/img/download.png" align="left" />Download all as binary</td></tr></#if>
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
					<tr><td colspan="2"><input type="submit" value="Change size" onclick="document.forms.${screen.name}.__action.value = 'changeSubmatrixSize'; document.forms.${screen.name}.submit();"></td></tr>
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
						<#list model.heatMatrix.colNames as n>
							<td class="matrixTableCell colorOfTitle">
								${model.renderCol(n)}
							</td>
						</#list>
					</tr>
					<#list model.heatMatrix.rowNames as n> 
						<tr>
							<td class="matrixTableCell colorOfTitle">
								${model.renderRow(n)}
							</td>
							<#assign x = model.heatMatrix.numberOfCols>
							<#list 0..x-1 as i>								
					  			<#if model.heatMatrix.elements[n_index][i]?exists>
						  			<#if model.selectedData.valuetype == "Decimal">
						  				<#assign val = model.heatMatrix.elements[n_index][i]>
						  				${val}
						  			<#else>
						  				<#if model.heatMatrix.elements[n_index][i] != "">
							  			<#assign val = model.heatMatrix.elements[n_index][i]>
						  				${val}
						  				<#else>
						  					<td class="matrixTableCell matrixRowColorEmpty">&nbsp;</td>
						  					<#--if n_index%2==0>
						  						<td class="matrixTableCell matrixRowColor1">&nbsp;</td>
						  					<#else>
						  						<td class="matrixTableCell matrixRowColor0">&nbsp;</td>
						  					</#if-->
						  				</#if>
						  			</#if>	
					  			<#else>
					  				<td class="matrixTableCell matrixRowColorEmpty">&nbsp;</td>
				  					<#--if n_index%2==0>
				  						<td class="matrixTableCell matrixRowColor1">&nbsp;</td>
				  					<#else>
				  						<td class="matrixTableCell matrixRowColor0">&nbsp;</td>
				  					</#if-->
					  			</#if>
							</#list> 
						</tr>
					</#list>
				</table>
			</td>
		</tr>
	</table>
</#if>

</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
