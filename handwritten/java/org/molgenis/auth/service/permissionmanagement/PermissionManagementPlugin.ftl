<#macro org_molgenis_auth_service_permissionmanagement_PermissionManagementPlugin screen>
<!-- normally you make one big form for the whole plugin-->
	
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
		
<#if screen.varmodel?exists>
	<#assign modelExists = true>
	<#assign model = screen.varmodel>
	<#assign service = screen.service>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

		<div class="screenbody">
			<div class="screenpadding">

<#if model.action == "AddEdit">

<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />	

<p><strong>
<#if model.permId == 0>Add Permission

<table cellpadding="1" cellspacing="1" border="1" class="display" id="addtable">
	<tr>
		<td style="padding:5px">Entity:</td> 
		<td style="padding:5px">
			<select name="entity" id="entity" class="selectbox">
				<#list service.findPermissions(model.getRole().getId(), "own") as perms>
					<#assign entity = service.findEntity(perms.getEntity())>
					<option value="${entity.getId()?string.computer}">${entity.getName()}</option>
				</#list>
			</select>
		</td>
	</tr>
	<tr>
		<td style="padding:5px">Permission:</td> 
		<td style="padding:5px">
			<select name="permission" id="permission" class="selectbox">
				<option value="read">Read</option>
				<option value="write">Write</option>
				<option value="execute">Execute</option>
				<option value="own">Own</option> <#-- Only super users.. should we have this here? -->
			</select>
		</td>
	</tr>
	<tr>
		<td style="padding:5px">Role:</td> 
		<td style="padding:5px">
			<select name="role" id="role" class="selectbox">
				<#list service.findRoles() as users>
					<option value="${users.getId()?string.computer}">${users.getName()}</option>
				</#list>
			</select>
		</td>
	</tr>
</table>

<div id='buttons_part' class='row'>
<input type='submit' class='submitbutton' value='AddPerm' onclick="__action.value='AddPerm'"/>
</div>


<#else>Modify Permission
		 <#assign permission = service.findPermission(model.getPermId())>
		 <#assign entity = service.findEntity(permission.getEntity())>
		 <#assign role = service.findRole(permission.getRole_())>	 

<table cellpadding="1" cellspacing="1" border="1" class="display" id="addtable">
	<tr>
		<td style="padding:5px">Entity:</td> 
		<td style="padding:5px">
			<select name="entity" id="entity" class="selectbox">
				<#list service.findEntities() as entities>
					<option value="${entities.getId()?string.computer}"
					<#if entity == entities>
						selected="selected"
					</#if>
					>${entities.getName()}</option>
				</#list>
			</select>
		</td>
	</tr>
	<tr>
		<td style="padding:5px">Permission:</td> 
		<td style="padding:5px">
			<select name="permission" id="permission" class="selectbox">
				<option value="read"
				<#if permission.getPermission() == 'read'>
					selected="selected"
				</#if>
				>Read</option>
				<option value="write"
				<#if permission.getPermission() == 'write'>
					selected="selected"
				</#if>
				>Write</option>
				<option value="execute"
				<#if permission.getPermission() == 'execute'>
					selected="selected"
				</#if>
				>Execute</option>
				<option value="own"
				<#if permission.getPermission() == 'own'>
					selected="selected"
				</#if>
				>Own</option> <#-- Only super users.. should we have this here? -->
			</select>
		</td>
	</tr>
	<tr>
		<td style="padding:5px">User/group:</td> 
		<td style="padding:5px">
			<select name="role" id="role" class="selectbox">
				<#list service.findRoles() as roles>
					<option value="${roles.getId()?string.computer}"
					<#if role.getName() == roles.getName()>
						selected="selected"
					</#if>
					>${roles.getName()}</option>
				</#list>
			</select>
		</td>
	</tr>
</table>

<div id='buttons_part' class='row'>
<input type='submit' class='submitbutton' value='UpdatePerm' onclick="__action.value='UpdatePerm'"/>
</div>

</form>

</#if> <!-- endif for Add vs. Edit if block -->
	
<#elseif model.action == "Remove">

<p><a href="molgenis.do?__target=${screen.name}&__action=Show">Back to overview</a></p>

<#else>

<div style="float:left; margin-right:50px">

<h2>User ${model.getRole().getName()}'s permissions</h2>

<table cellpadding="1" cellspacing="1" border="1" class="display" id="listtable">

<#--Creates a top row with labels-->
	<tr>
		<th style="color:darkblue;font-weight:bold;padding:5px">Entity</th> 
		<th style="color:darkblue;font-weight:bold;padding:5px">Level</th>
	</tr>

<#--Creates row with the actual permissions-->	

<#list service.findPermissions(model.getRole().getId()) as perm>
	<#assign entity = service.findEntity(perm.getEntity())>
	<tr>
		<td style="padding:5px">${entity.name}</td> 
		<td style="padding:5px">${perm.getPermission()}</td>
	</tr>
</#list>

</table>

</div>

<div style="float:right">

<h2>Permission for entities owned by user<br />${model.getRole().getName()}</h2>

<table cellpadding="1" cellspacing="1" border="1" class="display" id="listtable2">

<#--Creates a top row with labels-->
	<tr>
		<th style="color:darkblue;font-weight:bold;padding:5px">User/group</th> 
		<th style="color:darkblue;font-weight:bold;padding:5px">Entity</th> 
		<th style="color:darkblue;font-weight:bold;padding:5px">Level</th>
		<th colspan="2" style="color:darkblue;font-weight:bold;padding:5px"></th>
	</tr>

<#--Creates row with the actual permissions-->	

<#list service.findUserPermissions(model.getRole().getId(), false) as perm>
	<#assign user = service.findRole(perm.getRole_())>
	<#assign entity = service.findEntity(perm.getEntity())>
	<tr>
		<td style="padding:5px">${user.name}</td> 
		<td style="padding:5px">${entity.name}</td> 
		<td style="padding:5px">${perm.getPermission()}</td>
		<td style="padding:5px">
			<a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=${perm.getId()}">Modify</a>
		</td>
		<td style="padding:5px">
			<a href="molgenis.do?__target=${screen.name}&__action=Remove&id=${perm.getId()}">Remove</a>
		</td>
	</tr>
</#list>

</table>

<p>
	<a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=0">Add</a>
</p>

</div>

<div style="clear:left; clear:right">
</div>

</#if>

			</div>
		</div>
	</div>

</#macro>
