<#macro GenomeBrowser screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	<input type="hidden" name="__shopMeName">
	<input type="hidden" name="__shopMeId">
	
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
		
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

list of GFF files<br>
check if MolgenisFile is readable by anonymous<br>
dropdown with USCS databases: http://genome.ucsc.edu/FAQ/FAQreleases.html#release1<br>
<br>
uitleggen dat files van buitenaf zichtbaar moeten zijn!<br>

<#if model.release??>
	<#assign release = model.release>
<#else>
	<#assign release = "ce10">
</#if>

<input type="text" name="release" class="searchBox" value="${release}" >

<br>
[create linkout]<br>
<br>
<#if model.appUrl?? && model.appUrl?contains('localhost')>
ERROR: you are not connected to the interwebz, or your outgoing port is blocked! cannot serve out GFF to USCS
<#elseif model.appUrl??>
resultaat:
<#if model.filesAreVisible>

	<#list model.gffFiles as f>
		<a href="http://genome.ucsc.edu/cgi-bin/hgTracks?db=ce10&hgt.customText=${model.appUrl}/viewfile/${f.name}">click me</a><br>
	</#list>

<#else>
FILES ARE NOT VISIBLE
</#if>



<#else>
NO REQUEST YET
</#if>

<@action name="addTrack" label="Add"/>

	</div>
</form>
</#macro>
