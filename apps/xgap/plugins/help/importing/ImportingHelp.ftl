<!--Date:        May 15, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_help_importing_ImportingHelp screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
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
		
	<#assign bgColor = "#ffffff">
	<#assign borderStyle = "solid">
	<#assign borderWidthSpacing = "1px">
	<#assign picSize = "15">
	<#assign padding = "5px">
	<#assign styleBlack = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #000000; background-color: ${bgColor};\"">
		
	
	<#assign courier = "<font face=\"Courier New, Courier, mono, serif\">">	
	<#assign endFont = "</font>">
	
<h2>Help with importing data</h2>
	The content of your Excel file should match the database entities. For example:<br>
	<br><br>
	Your model looks like this:<br>
	${courier}
	
		&lt;entity name="Contact"&gt;<br>
		&nbsp;&nbsp;&nbsp;&lt;field name="id" type="autoid"/&gt;<br>
		&nbsp;&nbsp;&nbsp;&lt;field name="firstname" /&gt;<br>
		&nbsp;&nbsp;&nbsp;&lt;field name="midinitials" /&gt;<br>
		&nbsp;&nbsp;&nbsp;&lt;field name="lastname" /&gt;<br>
		&lt;/entity&gt;<br>
		 <br>
		&lt;entity name="Address"&gt;<br>
		&nbsp;&nbsp;&nbsp;&lt;field name="phone"/&gt;<br>
		&nbsp;&nbsp;&nbsp;&lt;field name="address_type" type="enum" enum_options="[home,work,mobile]"/&gt;<br>
		&nbsp;&nbsp;&nbsp;&lt;field name="contact" type="xref" xref_field="Contact.id" xref_label="lastname"/&gt;<br>
		&lt;/entity&gt;<br>
	
	${endFont}
	<br>
	
	Then your Excel file could look like this:<br>
	<br>
	Sheet named <b>Contact</b>, containing these headers:<br>
	<table "border-collapse: separate;">
		<tr>
			<td ${styleBlack}>id</td>
			<td ${styleBlack}>firstname</td>
			<td ${styleBlack}>midinitials</td>
			<td ${styleBlack}>lastname</td>
		</td>
	</table>
	<br>
	TODO
	<br>
	Sheet names do not need exact case matching and may be in any order.<br>
	TODO
	<br>
	
</tr>
		
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
