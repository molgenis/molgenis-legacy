<#macro plugin_animaldb_mazeexperiment_plotActogram screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
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
<div>	
<div id="select_date" >
<fieldset><legend>Select date range to plot:</legend>
<div>
<br>
<label for="startdate">Start date:</label><input type="text" id="startdate" name="startdate" value="${screen.startDate}" onclick="showDateInput(this,true) " autocomplete="off"/>
<br>
<label for="enddate">End date</label><input type="text" id="enddate" name="enddate" value="${screen.endDate}" onclick="showDateInput(this,true) " autocomplete="off"/>
<br>
</div>
<div id="channelhelp" style="float:left">
<table>
	<tr>
		<td>maze</td><td>channel offset:</td>
	</tr>
	<tr>
		<td>1</td><td>1</td>
	</tr>
	<tr>
		<td>2</td><td>25</td>
	</tr>
	<tr>
		<td>3</td><td>49</td>
	</tr>
	<tr>
		<td>4</td><td>73</td>
	</tr>
	<tr>
		<td>5</td><td>97</td>
	</tr>
	<tr>
		<td>6</td><td>145</td>
	</tr>
	<tr>
		<td>7</td><td>169</td>
	</tr>
	<tr>
		<td>8</td><td>193</td>
	</tr>
	<tr>
		<td>9</td><td>217</td>
	</tr>
	<tr>
		<td>10</td><td>241</td>
	</tr>
</table>
</div>
		
</div>
</fieldset>
</div>

<div id='select_channel' >
<fieldset><legend>Select channel to plot:</legend>
<br>
	<input type='submit' class='addbutton' value='plotprev' onclick="__action.value='doPlotPrev'" />
	&nbsp;
	<select id="channel" name="channel" autocomplete="on">
		<option value="${screen.currChlListIdx}">${screen.currChlNr}</option>
		<#assign i = 1>
		<#list screen.channelList as chl>
			<option value="${chl_index}">${chl.channelnumber}</option>
			<#assign i = i + 1>
		</#list>
	</select>
	<input type='submit' class='addbutton' value='plot' onclick="__action.value='doPlot'" />
	&nbsp;
	<input type='submit' class='addbutton' value='plotnext' onclick="__action.value='doPlotNext'" />
	<br>
</fieldset>
</div>

<div id="actogram">
	<p><#if screen.plot?exists>${screen.plot}</#if></p>
</div>

<hr>


<#--<input name="myinput" value="${screen.getMyValue()}">
<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
