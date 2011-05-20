<#macro plugins_welcome_BbmriWelcomeScreenPlugin screen>
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
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->	

<h1> Welcome to the BBMRI-NL biobank registry </h1>

<p>This registry lists Dutch Biobanks, Biobankers and their host institutes.</p>

<p>To use the registry:
<ul><li/>Please <a href="molgenis.do?__target=main&select=UserLogin">login</a> using the username and password provided.
<li/>Then you can browse biobanks, biobankers and host institutes from the menu on the left
<li/>Click <img src="http://www.bbmriwiki.nl/biobanks/generated-res/img/editview.gif"/> to get details or edit the entry
<li/>Click <img src="http://www.bbmriwiki.nl/biobanks/generated-res/img/listview.png"/> to go back to listview
</ul></p>

<#if screen.userId != 0>
<p><a href="molgenis.do?__target=Cohorts&__action=filter_set&__filter_attribute=Approved&__filter_operator=EQUALS&__filter_value=1">See all approved cohorts</a></p>
</#if>

<p> This is a beta system and we want to improve the user interface still a lot including an advanced search option. Also now everybody can edit everything which we are now making more finegrained.
<br/>Please report any ideas, suggestions, bugs to <a href="mailto:m.a.swertz@rug.nl">m.a.swertz@rug.nl</a>  </p>

<p>This system was developed by the NBIC Biobanking platform, BBMRI-NL bioinformatics rainbow hosted at the Coordination Center, UMCG, and in collaboration with GEN2PHEN,
LifeLines and the European Bionformatics Institute regarding data model and tools. It was generated using MOLGENIS.</p>

	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
