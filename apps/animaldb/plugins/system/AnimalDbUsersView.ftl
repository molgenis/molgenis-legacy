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

<div class="row">
	<label for="firstname">First name:</label>
	<input type="text" name="firstname" id="firstname" class="textbox" />
</div>

<div class="row">
	<label for="lastname">Last name:</label>
	<input type="text" name="lastname" id="lastname" class="textbox" />
</div>

<div class="row">
	<label for="email">Email:</label>
	<input type="text" name="email" id="email" class="textbox" />
</div>

<div class="row">
	<label for="username">User name:</label>
	<input type="text" name="username" id="username" class="textbox" />
</div>

<div class="row">
	<label for="password1">Password:</label>
	<input type="password" name="password1" id="password1" class="textbox" />
</div>

<div class="row">
	<label for="password2">Repeat password:</label>
	<input type="password" name="password2" id="password2" class="textbox" />
</div>

<div class="row">
	<label for="investigation">Investigation:</label>
	<select name="investigation" id="investigation">
		<option value="-1">New (specify name below)...</option>
		<#list screen.investigations as investigation>
			<option value="${investigation.id?string.computer}">${investigation.name}</option>
		</#list>
	</select>
</div>

<div class="row">
	<label for="newinv">New investigation name (if applicable):</label>
	<input type="text" name="newinv" id="newinv" class="textbox" />
</div>

<div class='row'>
	<input id='adduser' type='submit' class='addbutton' value='Add' onclick="__action.value='Add'" />
	<input id='canceluser' type='submit' class='addbutton' value='Cancel' onclick="__action.value='Cancel'" />
</div>

<#else>

<p><a href="molgenis.do?__target=${screen.name}&__action=New">Make new user</a></p>

<p>Welcome, ${screen.userName}. You own the following investigation(s):</p>
<table>
	<tr>
		<th style='padding:5px'>Name</th>
		<th style='padding:5px'>Read-rights</th>
		<th style='padding:5px'>Write-rights</th>
	</tr>
<#assign i = 0>
<#list screen.investigations as inv>
	<#assign invname = inv.name>
	<tr>
		<td style='padding:5px'>${invname}</td>
		<td style='padding:5px'>
		<#if screen.getInvestigationSharers(invname, false)??>
			${screen.getInvestigationSharers(invname, false)}<br />
		<#else>
			Currently none<br />
		</#if>
			<select name="shareread" id="shareread">
				<#list screen.users as moluser>
					<option value="${moluser.id?string.computer}">${moluser.name}</option>
				</#list>
			</select>
			<input type='submit' class='addbutton' value='Share' onclick="__action.value='ShareRead${i}'" />
		</td>
		<td style='padding:5px'>
		<#if screen.getInvestigationSharers(invname, true)??>
			${screen.getInvestigationSharers(invname, true)}<br />
		<#else>
			Currently none<br />
		</#if>
			<select name="sharewrite" id="sharewrite">
				<#list screen.users as moluser>
					<option value="${moluser.id?string.computer}">${moluser.name}</option>
				</#list>
			</select>
			<input type='submit' class='addbutton' value='Share' onclick="__action.value='ShareWrite${i}'" />
		</td>
	</tr>
	<#assign i = i + 1>
</#list>

</#if>
</table>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
