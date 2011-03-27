<#macro plugins_contact_BbmriContactPlugin screen>
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

			<h1> Contact </h1>
			
			<p> This is a beta system and we want to improve the user interface still a lot including an advanced search option. Also now everybody can edit everything which we are now making more finegrained.</p>
			<ul><li>For more information concerning the biobank, please contact Margreet Brandsma . </li>
			<li>For more information about the application, please report any ideas, suggestions, bugs to <a href="mailto:m.a.swertz@rug.nl">m.a.swertz AT rug.nl</a>  </p></li>
			
			<li><p>This system was developed by the NBIC Biobanking platform, BBMRI-NL bioinformatics rainbow hosted at the Coordination Center, UMCG, and in collaboration with GEN2PHEN,
			LifeLines and the European Bionformatics Institute regarding data model and tools. It was generated using MOLGENIS.</p></li>
			</ul>
			</div>
		</div>
	</div>
</form>
</#macro>
