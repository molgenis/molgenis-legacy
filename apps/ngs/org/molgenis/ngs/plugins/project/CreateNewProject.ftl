<#macro plugins_ngs_project_CreateNewProject screen>
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
		
		<#-- Hack to immediatly clear the message so it doesn't "stick". -->
		${screen.clearMessage()}


<#if screen.model?exists>
	<#assign modelExists = true>
	<#assign model = screen.model>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->	

<p> Please fill in the following fields (fields marked with a '*' are required) to create a new project. 
</p>

<div id="projectname" class="row">
<label for="projectname"> * Project Name:</label>
<br>
<input type="text" name="projectname" id="projectname" class="textbox" />
</div>

<h3> Customer (Organization) details: </h3>

<div id="orgname" class="row">
<label for="orgname"> * Organization Name:</label>
<br>
<input type="text" name="orgname" id="orgname" class="textbox" />
</div>

<div id="address" class="row">
<label for="address">* Organization Address:</label>
<br>
<input type="text" name="address" id="address" class="textbox" />
</div>

<div id="firstname" class="row">
<label for="firstname">* First Name Contact:</label>
<br>
<input type="text" name="firstname" id="firstname" class="textbox" />
</div>

<div id="lastname" class="row">
<label for="lastname">* Last Name Contact:</label>
<br>
<input type="text" name="lastname" id="lastname" class="textbox" />
</div>

<div id="email" class="row">
<label for="email">* Email Contact:</label>
<br>
<input type="text" name="email" id="email" class="textbox" />
</div>

<div id="telephone" class="row">
<label for="telephone">* Contact Telephone:</label>
<br>
<input type="text" name="telephone" id="telephone" class="textbox" />
</div>

<h3> Project Details: </h3>

<div id="contractcode" class="row">
<label for="contractcode">* Contract Code:</label>
<br>
<input type="text" name="contractcode" id="contractcode" class="textbox" />
</div>

<div id="startdate" class="row">
<label for="startdate">* Contract Start Date:</label>
<br>
<input type="text" name="startdate" id="startdate" class="textbox" />
</div>

<div id="enddate" class="row">
<label for="enddate">* Contract End Date:</label>
<br>
<input type="text" name="enddate" id="enddate" class="textbox" />
</div>

<div id="labworker" class="row">
<label for="labworker">* Lab tech:</label>
<br>
<select name="labworker" id="labworker" class="selectbox">
<#list model.labworkers as labworkers>
<option value="${labworkers.getFirstName()} ${labworkers.getLastName()}">${labworkers.getFirstName()} ${labworkers.getLastName()}</option>
</#list>
</select>
</div>

<h3> Sample Details: </h3>

<div id="numsamples" class="row">
<label for="numsamples">* Number of Samples:</label>
<br>
<input type="text" name="numsamples" id="numsamples" class="textbox" />
</div>

<p> Please fill in any sample-generic details </p>

<div id="typesample" class="row">
<label for="typesample">* Sample Type:</label>
<br>
<select name="typesample" id="typesample" class="selectbox">
<option value="dna"> DNA </option>
<option value="rna"> RNA </option>
</select>
</div>

<div id="workflow" class="row">
<label for="workflow">* Workflow:</label>
<br>
<select name="workflow" id="workflow" class="selectbox">
<#list model.workflows as workflow>
<option value="${workflow.getName()}">${workflow.getName()} </option>
</#list>
</select>
</div>

<div id="location" class="row">
<label for="location">Current location:</label>
<br>
<input type="text" name="location" id="location" class="textbox" />
</div>

<div id="origin" class="row">
<label for="origin">Origin:</label>
<br>
<input type="text" name="origin" id="origin" class="textbox" />
</div>

<div id="fraglength" class="row">
<label for="fraglength">Fragment Length:</label>
<br>
<input type="text" name="fraglength" id="fraglength" class="textbox" />
</div>

<div id="readlength" class="row">
<label for="readlength">Read length:</label>
<br>
<input type="text" name="readlength" id="readlength" class="textbox" />
</div>

<div id="sampname" class="row">
<label for="sampname">Sample Name:</label>
<br>
<input type="text" name="sampname" id="sampname" class="textbox" />
</div>

<div id="descbox" class="row">
<label for="descbox">Sample Description:</label>
<br>
<textarea name="descbox" cols="40" rows="5">
Enter your sample description here...
</textarea><br>
</div>

<div id='buttons_part' class='row'>
<input type='submit' class='submitbutton' value='Submit' onclick="__action.value='Submit'"/>
</div>

<#--<input name="myinput" value="${screen.getMyValue()}">
<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>

</form>
</#macro>