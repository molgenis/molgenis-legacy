<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
		${model.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
			
<#--begin your plugin-->	

<h2>Minimal code </h2>

<table cellpadding="4">

<tr><td>Freermarker example</td><td>Example</td></tr>

<tr><td>
<#noparse><@action name="action1" /></#noparse>
</td><td>
<@action name="action1" />
</td></tr>

<tr><td>
<#noparse><@string name="string1" value="hello world"/></#noparse>
</td><td>
<@string name="string1" value="hello world"/>
</td></tr>

<tr><td>
<#noparse><@bool name="bool1" value="true"/></#noparse>
</td><td>
<@bool name="bool1" value="true"/>
</td></tr>

<tr><td>
<#noparse><@checkbox name="checkbox1" options="1,2" option_labels="option1,option2" value="1"/></#noparse>
</td><td>
<@checkbox name="checkbox1" options="1,2" option_labels="option1,option2" value="1"/>
</td></tr>

<tr><td>
<#noparse><@date name="date1" value="2010-03-11"/></#noparse>
</td><td>
<@date name="date1" value="2010-03-11"/>
</td></tr>

<tr><td>
<#noparse><@datetime name="datetime1" value="2010-03-11"/></#noparse>
</td><td>
<@datetime name="datetime1" value="2010-03-11"/>
</td></tr>

<tr><td>
<#noparse><@enum name="enum1" options="enumval1,enumval2" value="enumval1"/></#noparse>
</td><td>
<@enum name="enum1" options="enumval1,enumval2" value="enumval1"/>
</td></td>

<tr><td>
<#noparse><@file name="file1"/></#noparse>
</td><td>
<@file name="file1"/>
</td></td>

<tr><td>
<#noparse><@int name="int1" value="1"/></#noparse>
</td><td>
<@int name="int1" value="1"/>
</td></td>

<tr><td>
<#noparse><@xref name="xref1" xref_entity="org.molgenis.organization.Investigation"/></#noparse>
</td><td>
<@xref name="xref1" xref_entity="org.molgenis.organization.Investigation"/>
</td></td>

<tr><td>
<#noparse><@mref name="mref1" xref_entity="org.molgenis.organization.Investigation"/></#noparse>
</td><td>
<@mref name="mref1" xref_entity="org.molgenis.organization.Investigation"/>
</td></td>

<tr><td>
<#noparse><@decimal name="decimal1" value="1.2"/></#noparse>
</td><td>
<@decimal name="decimal1" value="1.2"/>
</td></td>

<tr><td>
<#noparse><@text name="text1" value="hello world<br/>and more"/></#noparse>
</td><td>
<@text name="text1" value="hello world<br/>and more"/>
</td></td>


<tr><td colspan="2">${model.lastRequest}</td></tr>	
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
