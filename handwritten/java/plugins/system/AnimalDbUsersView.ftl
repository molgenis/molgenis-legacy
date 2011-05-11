<#macro plugins_system_AnimalDbUsers screen>
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

<#if screen.action == "New">

<div id="firstname" class="row">
	<label for="firstname">First name:</label>
	<input type="text" name="firstname" id="firstname" class="textbox" />
</div>

<div id="lastname" class="row">
	<label for="lastname">Last name:</label>
	<input type="text" name="lastname" id="lastname" class="textbox" />
</div>

<div id="email" class="row">
	<label for="email">Email:</label>
	<input type="text" name="email" id="email" class="textbox" />
</div>

<div id="username" class="row">
	<label for="username">User name:</label>
	<input type="text" name="username" id="username" class="textbox" />
</div>

<div id="password1" class="row">
	<label for="password1">Password:</label>
	<input type="password" name="password1" id="password1" class="textbox" />
</div>

<div id="password2" class="row">
	<label for="password2">Repeat password:</label>
	<input type="password" name="password2" id="password2" class="textbox" />
</div>

<div id="investigationdiv" class="row">
	<label for="investigation">Investigation:</label>
	<select name="investigation" id="investigation"> 
		<#list screen.investigations as investigation>
			<option value="${investigation.id?string.computer}">${investigation.name}</option>
		</#list>
	</select>
</div>

<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='Add'" />
</div>

<#else>

<p><a href="molgenis.do?__target=${screen.name}&__action=New">Make new user...</a></p>

<table>
	<tr><th>User name</th><th>Investigation(s)</th></tr>

<#assign i = 0>
<#list screen.getUserNames() as user>
	<tr><td>${user}</td><td>${screen.getInvestigationName(i)}</td></tr>
	<#assign i = i + 1>
</#list>

</table>

</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
