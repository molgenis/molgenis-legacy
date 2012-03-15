<!--Date:        October 28, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro org_molgenis_animaldb_plugins_accessibility_ManualPlugin screen>
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
	
<h2>AnimalDB manuals</h2>

<p><strong>Dutch / Nederlands</strong><br />
<a href="animaldb/AddingAnimals.pdf" target="_blank">Handleiding 1: Toevoegen van dieren anders dan via de kweek</a><br />
<a href="animaldb/RemovingAnimals.pdf" target="_blank">Handleiding 2: Verwijderen van dieren, gebruik van de matrix</a><br />
<a href="animaldb/Breeding.pdf" target="_blank">Handleiding 3: Kweekbeheer</a><br />
<a href="animaldb/AnimalsInLocations.pdf" target="_blank">Handleiding 4: Dieren in locaties</a><br />
<a href="animaldb/Measurements.pdf" target="_blank">Handleiding 5: Eigenschappen (wat betekenen de kolommen in de matrix?)</a>
</p>

<#--end of your plugin-->
			</div>
		</div>
	</div>
</form>
</#macro>
