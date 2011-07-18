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

<h3>Welcome to the Catalogue of Dutch biobanks</h3>
<p>This application allows you to view all biobanks associated with BBMRI-NL in Biobank Overview. The catalogue is updated monthly.</p>
<p>To apply for inclusion of your biobank in this catalogue, please <a href="molgenis.do?__target=main&select=BbmriContact">contact the BBMRI-NL office</a>.</p>
<p>You can download data from the database. It is also possible to upload new or edited information, but for security reasons all submittals will be monitored by the BBMRI-NL office before publication.</p>
<p>To find your way around the application, you might want to check out the <a href="molgenis.do?__target=main&select=BbmriHelp">User manual</a>.</p>
<p>If you have any questions or remarks, please do not hesitate to <a href="molgenis.do?__target=main&select=BbmriContact">contact us</a>.</p>

<!--

<h1> Welcome to the BBMRI-NL biobank registry </h1>

<p>This registry lists Dutch Biobanks, Biobankers and their host institutes.</p>

<p>To use the registry:
<ul><li/>Please <a href="molgenis.do?__target=main&select=UserLogin">login</a> using the username and password provided.
<li/>Then you can browse biobanks, biobankers and host institutes from the menu on the left
<li/>Click <img src="http://www.bbmriwiki.nl/biobanks/generated-res/img/editview.gif"/> to get details or edit the entry
<li/>Click <img src="http://www.bbmriwiki.nl/biobanks/generated-res/img/listview.png"/> to go back to listview
</ul></p>

<!#if screen.userId != 0>
<p><a href="molgenis.do?__target=Cohorts&__action=filter_set&__filter_attribute=Approved&__filter_operator=EQUALS&__filter_value=1">See all approved cohorts</a></p>
<!/#if>

<p> This is a beta system and we want to improve the user interface still a lot including an advanced search option. Also now everybody can edit everything which we are now making more finegrained.
<br/>Please report any ideas, suggestions, bugs to <a href="mailto:m.a.swertz@rug.nl">m.a.swertz@rug.nl</a>  </p>

<p>This system was developed by the NBIC Biobanking platform, BBMRI-NL bioinformatics rainbow hosted at the Coordination Center, UMCG, and in collaboration with GEN2PHEN,
LifeLines and the European Bionformatics Institute regarding data model and tools. It was generated using MOLGENIS.</p>


<span style="color: #3366CC;"><span style="font-size: .75em;">
<!#if screen.mostRecentChangeLogEntry??>
	Last update was on {screen.mostRecentChangeLogEntry.date}.
<!#else>
	No updates yet.
<!/#if>
</span></span>

-->
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
