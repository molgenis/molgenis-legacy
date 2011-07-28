<#macro plugins_breedingplugin_ManageLitters screen>
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

<#if screen.action == "ShowLitters">

	<#if screen.labelDownloadLink??>
		<p>${screen.labelDownloadLink}</p>
	</#if>

	<p><a href="molgenis.do?__target=${screen.name}&__action=AddLitter">Make new litter</a></p>
	
	<#if screen.litterList?exists>
		<#if screen.litterList?size gt 0>
			<h2>Unweaned litters</h2>
			<table cellpadding="10" cellspacing="2" border="1">
				<tr>
					<th>Name</th><th>Parentgroup</th><th>Birth date</th><th>Size</th><th>Size approximate?</th><th></th>
				</tr>
			<#list screen.litterList as litter>
				<tr>
					<td style='padding:5px'>${litter.name}</td>
					<td style='padding:5px'>${litter.parentgroup}</td>
					<td style='padding:5px'>${litter.birthDate}</td>
					<td style='padding:5px'>${litter.size}</td>
					<td style='padding:5px'>${litter.isSizeApproximate}</td>
					<td style='padding:5px'><a href="molgenis.do?__target=${screen.name}&__action=ShowWean&id=${litter.id}">Wean</a></td>
				</tr>
			</#list>
			</table>
		</#if>
	</#if>
	
	<#if screen.genoLitterList?exists>
		<#if screen.genoLitterList?size gt 0>
			<h2>Weaned litters that have not been genotyped yet</h2>
			<table cellpadding="10" cellspacing="2" border="1">
				<tr>
					<th>Name</th><th>Parentgroup</th><th>Birth date</th><th>Wean date</th><th>Size</th><th></th>
				</tr>
			<#list screen.genoLitterList as litter>
				<tr>
					<td style='padding:5px'>${litter.name}</td>
					<td style='padding:5px'>${litter.parentgroup}</td>
					<td style='padding:5px'>${litter.birthDate}</td>
					<td style='padding:5px'>${litter.weanDate}</td>
					<td style='padding:5px'>${litter.size}</td>
					<td style='padding:5px'><a href="molgenis.do?__target=${screen.name}&__action=ShowGenotype&id=${litter.id}">Genotype</a></td>
				</tr>
			</#list>
			</table>
		</#if>
	</#if>

<#elseif screen.action == "AddLitter">

	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>

	<div id='newlitter_name_part' class='row' style="width:700px">
		<label for='littername'>Litter name:</label>
		<input type='text' class='textbox' name='littername' id='littername' value='<#if screen.litterName?exists>${screen.getLitterName()}</#if>' />
	</div>
	
	<!-- Parent group -->
	<div id="parentgroupselect" class="row" style='clear:left'>
		<label for="parentgroup">Parent group:</label>
		<select name='parentgroup'>
		<#if screen.parentgroupList?exists>
			<#list screen.parentgroupList as pgl>
				<option value='${pgl.id}'>${pgl.name}</option>
			</#list>
		</#if>
		</select>
	</div>
	
	<!-- Date and time of birth -->
	<div id='newlitter_datetimevalue_part' class='row'>
		<label for='birthdatetime'>Date and time of birth:</label>
		<input type='text' class='textbox' id='birthdatetime' name='birthdatetime' value='<#if screen.birthdatetime?exists>${screen.getBirthdatetime()}</#if>' onclick='showDateInput(this,true)' autocomplete='off' />
	</div>
	
	<!-- Size -->
	<div id='newlitter_size_part' class='row'>
		<label for='littersize'>Litter size:</label>
		<input type='text' class='textbox' name='littersize' id='littersize' value='<#if screen.litterSize?exists>${screen.getLitterSize()}</#if>' />
	</div>
	
	<!-- Size approximate? -->
	<div id="sizeapp_div" class="row">
		<label for="sizeapp_toggle">Size approximate:</label>
		<input type="checkbox" id="sizeapp_toggle" name="sizeapp_toggle" value="sizeapp" />
	</div>
	
	<!-- Add button -->
	<div id='newlitter_buttons_part' class='row'>
		<input type='submit' class='addbutton' value='Add' onclick="__action.value='ApplyAddLitter'" />
	</div>
	
<#elseif screen.action == "ShowWean">

	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>
	
	<!-- Date and time of weaning -->
	<div id='weandatetimediv' class='row'>
		<label for='weandatetime'>Date and time of weaning:</label>
		<input type='text' class='textbox' id='weandatetime' name='weandatetime' value='<#if screen.weandatetime?exists>${screen.getWeandatetime()}</#if>' onclick='showDateInput(this,true)' autocomplete='off' />
	</div>
	
	<!-- Size -->
	<div id='weansize_part1' class='row'>
		<label for='weansizefemale'>Nr. of females:</label>
		<input type='text' class='textbox' name='weansizefemale' id='weansizefemale' value='<#if screen.weanSizeFemale?exists>${screen.getWeanSizeFemale()}</#if>' />
	</div>
	<div id='weansize_part2' class='row'>
		<label for='weansizemale'>Nr. of males:</label>
		<input type='text' class='textbox' name='weansizemale' id='weansizemale' value='<#if screen.weanSizeMale?exists>${screen.getWeanSizeMale()}</#if>' />
	</div>
	
	<#if screen.getCustomNameFeature()??>
		<#assign label = screen.getCustomNameFeature()>
	
		<div id="customname" class="row">
			<label for="customname">${label} base:</label>
			<input type="text" name="customname" id="customname" class="textbox" />
		</div>
	
		<div id="startnumber" class="row">
			<label for="startnumber">${label} start number:</label>
			<input type="text" name="startnumber" id="startnumber" class="textbox" />
		</div>
	</#if>

	<!-- Add button -->
	<div id='addlitter' class='row'>
		<input type='submit' class='addbutton' value='Wean' onclick="__action.value='Wean'" />
	</div>

<#elseif screen.action == "ShowGenotype">
	
	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>
	
	<p>Genotype screen</p>
	
	<#list screen.getAnimalsInLitter() as animal>
		${animal.name}<br />
	</#list>
	
</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
