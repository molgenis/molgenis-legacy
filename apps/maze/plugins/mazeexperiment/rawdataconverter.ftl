<#macro plugins_mazeexperiment_rawdataconverter screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
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


<div id='info'>
	<h3>Clicking this butten will binary convert the raw data and add it to the binaryconverted table.</h3>
	<p>
	<ul>
	 <li> data is converted and storedadded per channel</li>
	 <li> switchevents are aggregated and stored in 1 sec bins</li>
	 <li> When an event is succesfully converted an entry in the Isconverted table is added, to prevent reconversion/import of the event.</li>
	 <li><b>Be warned, data conversion might take some time if there are a lot of unconverted switch events!!</b></li>
	</ul></p>
</div>
<div id='convert' >
	<input type='submit' class="addbutton"  value='dbconvert' onclick="__action.value='dbconv'" />
</div>
<hr>
<div id="statusmessage" >
	<p><#if screen.dbval?exists>${screen.statusmessage}</#if></p>
</div>
<div id='binaryconversion test'>
	<h3>convert an integer to a binary string:</h3>
	<div id='binconvert' >
		<input type="text" name="intval" value="intval" />
		<input type="submit" class="addbutton"  value="binconvert" onclick="__action.value='binconv'" />
	</div>
	<div id='resultval'>
		<p><#if screen.binval?exists>${screen.binval}</#if></p>
	</div>
</div>



<#--<input name="myinput" value="${screen.getMyValue()}">
<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
