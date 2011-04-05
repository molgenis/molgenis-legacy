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
			<p> This is a beta system and we want to improve the user interface still a lot including an advanced search option. 
			Also now everybody can edit everything which we are now making more finegrained.</p>
			<ul><li>
					Bij vragen of opmerkingen kunt u contact opnemen met BBMRI-NL door te emailen of bellen naar <br/>
					Margreet Brandsma, programmamanager<br/>
					<a href="mailto:m.brandsma@bbmri.nl">m.brandsma AT bbmri.nl</a><br/>
					071 - 5269412<br/>
					ma, di, wo bereikbaar<br/><br/><br/>
					Margot Heesakker - Heintz<br/>
					<a href="mailto:m.heesakker@bbmri.nl">m.heesakker AT bbmri.nl</a><br/>
					071 - 5268498<br/>
					di, wo, vr bereikbaar<br/><br/>
			 </li>
			<li>For more information about the application, please report any ideas, suggestions, bugs to <a href="mailto:m.a.swertz@rug.nl">m.a.swertz AT rug.nl</a>  </p></li>
			</ul>
			</div>
		</div>
	</div>
</form>
</#macro>
