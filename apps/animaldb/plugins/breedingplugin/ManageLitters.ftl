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
					<td style='padding:5px'><a href="molgenis.do?__target=${screen.name}&__action=ShowWean&id=${litter.id?string.computer}">Wean</a></td>
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
					<td style='padding:5px'><a href="molgenis.do?__target=${screen.name}&__action=ShowGenotype&id=${litter.id?string.computer}">Genotype</a></td>
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
				<option value='${pgl.id?string.computer}'>${pgl.name}</option>
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
	
	<h2>Genotype litter</h2>
	
	<p>${screen.parentInfo}</p>
	
	<p><em>Note: sexes have been pre-filled based on weaning info</em></p>
	
	<table>
		<tr>
			<th>Animal name</th>
			<th>Sex</th>
			<th>Color</th>
			<th>Earmark</th>
			<th>Background</th>
			<th>Gene name</th>
			<th>Gene state</th>
		</tr>
	<#assign animalCount = 0>
	<#list screen.getAnimalsInLitter() as animal>
		<tr>
			<td>${animal.name}</td>
			<td>
				<select id='sex_${animalCount}' name='sex_${animalCount}'>
				<#if screen.sexList?exists>
					<#list screen.sexList as sex>
						<option value='${sex.id?string.computer}'
						<#if screen.getAnimalSex(animal.id) = sex.id>
							selected="selected"
						</#if>
						>${sex.name}</option>
					</#list>
				</#if>
				</select>
			</td>
			<td>
				<select id='color_${animalCount}' name='color_${animalCount}'>
				<#if screen.colorList?exists>
					<#list screen.colorList as color>
						<option value='${color}'
						<#if screen.getAnimalColor(animal.id) = color>
							selected="selected"
						</#if>
						>${color}</option>
					</#list>
				</#if>
				</select>
			</td>
			<td>
				<select id='earmark_${animalCount}' name='earmark_${animalCount}'>
				<#if screen.earmarkList?exists>
					<#list screen.earmarkList as earmark>
						<option value='${earmark}'
						<#if screen.getAnimalEarmark(animal.id) = earmark>
							selected="selected"
						</#if>
						>${earmark}</option>
					</#list>
				</#if>
				</select>
			</td>
			<td>
				<select id='background_${animalCount}' name='background_${animalCount}'>
				<#if screen.backgroundList?exists>
					<#list screen.backgroundList as background>
						<option value='${background.id?string.computer}'
						<#if screen.getAnimalBackground(animal.id) = background.id>
							selected="selected"
						</#if>
						>${background.name}</option>
					</#list>
				</#if>
				</select>
			</td>
			<td>
				<select id='geneName_${animalCount}' name='geneName_${animalCount}'>
				<#if screen.geneNameList?exists>
					<#list screen.geneNameList as geneName>
						<option value='${geneName}'
						<#if screen.getAnimalGeneName(animal.id) = geneName>
							selected="selected"
						</#if>
						>${geneName}</option>
					</#list>
				</#if>
				</select>
			</td>
			<td>
				<select id='geneState_${animalCount}' name='geneState_${animalCount}'>
				<#if screen.geneStateList?exists>
					<#list screen.geneStateList as geneState>
						<option value='${geneState}'
						<#if screen.getAnimalGeneState(animal.id) = geneState>
							selected="selected"
						</#if>
						>${geneState}</option>
					</#list>
				</#if>
				</select>
			</td>
		</tr>
		<#assign animalCount = animalCount + 1>
	</#list>
	</table>
	<!-- "+" button for extra geneName + geneState columns, TODO get working
	<input type='submit' class='addbutton' value='+' onclick="" /> -->
	
	<!-- Save button -->
	<div id='save' class='row'>
		<input type='submit' class='addbutton' value='Save' onclick="__action.value='Genotype'" />
	</div>
	
</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
