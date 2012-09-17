<#macro org_molgenis_animaldb_plugins_breeding_Breedingnew screen>
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
			<script>$.ctNotify("${message.text}", {type: 'confirmation', delay: 5000});</script>
		<!-- <p class="successmessage">${message.text}</p> -->
			
			<#else>
			<script>$.ctNotify("${message.text}", {type: 'error', delay: 7000});</script>	        	
		<!-- <p class="errormessage">${message.text}</p> -->
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<div style="float:left">
	<label for="line">Breeding line:</label>
	<select name="line" id="line" class="selectbox">
		<#if screen.lineList??>
			<#list screen.lineList as line>
				<option value="${line.name}" <#if line.name == screen.line>selected="selected"</#if>>${line.name}</option>
			</#list>
		</#if>
	</select>
</div>
<div style="float:left">
	<input type="image" onclick="__action.value='changeLine'" src="generated-res/img/update.gif" />
</div>
<br /><br /><br />
<div style="clear:both; margin-bottom:2px">
	<div id='menuParentgroups' class='<#if screen.entity == "Parentgroups">navigationSelected<#else>navigationNotSelected</#if>' onclick="window.location.href='molgenis.do?__target=${screen.name}&__action=switchParentgroups'">Parentgroups</div>
	<div id='menuLitters' class='<#if screen.entity == "Litters">navigationSelected<#else>navigationNotSelected</#if>' onclick="window.location.href='molgenis.do?__target=${screen.name}&__action=switchLitters'">Litters</div>
</div>

<#if screen.action == "createParentgroup">
	
	<div class="form_header">Create parentgroup, step 1/2: how many parentgroups do you want to create?</div>
	<div>
	   <p>
	       <label style="font-weight:bold" for"numberPG" >Select how many parent groups: </label><input type='text' class="ui-widget-content ui-corner-all" id="numberPG" name="numberPG">
	   </p>
	   <p>    
	      <input type='submit' id='cancel2' class='addbutton ui-button ui-widget ui-state-default ui-corner-all' value='Cancel' onclick="__action.value='init'" />
	      <input type='submit' id='selectt' class='addbutton ui-button ui-widget ui-state-default ui-corner-all' value='Next' onclick="__action.value='selectParents'" />
	   </p>
	</div>
	
	
	<br />
	
<#elseif screen.action?starts_with("selectParents")>

	<div class="form_header">Create parentgroup, step 2/2: select mother(s) and father(s) for your parentgroup(s)</div>
	
	<#if screen.numberOfPG gt -1>

			<br />
		
		<div style="clear:both">
			${screen.motherMatrixViewer}<br />
		</div>
		<#list screen.hashFathers?keys as n>
			<div style="float:left; margin:15px; border:5px solid #5B82A4">
			<table>	
				<#--value='<#if screen.pgName[n]?exists>${screen.pgName[n]}</#if>'-->
				<tr>
					<td><label style="font-weight:bold" for="parentgroup${n}">parentgroup:${n+1}</label></td>
				</tr>	
				<tr>
					<td><label for="father${n}">father:</label></td><td><input type='text' class="ui-widget-content ui-corner-all" value="<#compress>
						<#list screen.getFatherElement(n) as selParents >
							${selParents}
							<#if selParents_has_next>,</#if>
						</#list>					
					</#compress>"
					name="father${n}" id="father${n}">
					<input style="margin-left:2px; margin-right:5px" class="ui-widget-content ui-corner-all" id="fatherB${n}"type="image" src="res/img/button_down_adb.jpg" height="16px" width="16px" onclick="__action.value='selectParentsM${n}'">
					</td>
					
					<td ><label for="mother${n}">mother:</label></td><td><input type='text' class="ui-widget-content ui-corner-all" value="<#compress>
						<#list screen.getMotherElement(n) as selParents >
							${selParents}
							<#if selParents_has_next>,</#if>
						</#list>					
						</#compress>"
						 name="mother${n}" id="mother${n}">
						<input style="margin-left:2px; margin-right:5px" class="ui-widget-content ui-corner-all" id="motherB${n}" type="image" src="res/img/button_down_adb_pink.jpg" height="16px" width="16px" onclick="__action.value='selectParentsF${n}'">
					</td>
				</tr>
				<tr>
					<td><label for="startdate${n}">startdate:</label></td>
					<script>
						$(function() {
							$( "#startdate${n}" ).datepicker({
								numberOfMonths: 1,
								showButtonPanel: true,
								dateFormat: "yy-mm-dd"
							});
						});
					</script>
					<td><input type="text" id="startdate${n}" class="ui-widget-content ui-corner-all" name="startdate${n}" value="<#if screen.startdate??>${screen.getStartdate()}"</#if></td>
					<td><label for="remarks${n}">remarks:</label></td><td><input type='text' class="ui-widget-content ui-corner-all" name="remarks${n}" id="remarks${n}"></td>	
				</tr>
			</table>
			</div>	
			</#list>
		<div style="clear:both">
			<input type='submit' id='cancel2' class='addbutton' value='Cancel' onclick="__action.value='init'" />
			<input type='submit' id='from2to3' class='addbutton' value='Next' onclick="__action.value='JensonButton'" />
		</div>
	</#if>
	<#--input type='submit' name="submitswitch" id="submitswitch" class='addbutton' value='Switch' onclick="__action.value='SelectParentsswitchtofather'" /-->
	</div>
<#--
<#elseif screen.action == "addParentgroupScreen3">

	<div class="form_header">Create parentgroup, step 2/3: select father(s)</div>
	<br />
	${screen.fatherMatrixViewer}<br />
	<div style="clear:both">
		<input type='submit' id='cancel3' class='addbutton' value='Cancel' onclick="__action.value='init'" />
		<input type='submit' id='from3to2' class='addbutton' value='Previous' onclick="__action.value='createParentgroup'" />
		<input type='submit' id='from3to4' class='addbutton' value='Next' onclick="__action.value='addParentgroupScreen4'" />
	</div>

<#elseif screen.action == "addParentgroupScreen4">

	<div class="form_header">Create parentgroup, step 3/3: set start date and remarks</div>
	<br />
	<div style="clear:both; display:block">
		<label style="width:16em;float:left;" for="startdate">Start date:</label>
		<input type="text" class="text ui-widget-content ui-corner-all" id="startdate" name="startdate" value="<#if screen.startdate?exists>${screen.getStartdate()}</#if>" onclick='showDateInput(this)' autocomplete='off'  />
	</div>
	<div style="clear:both; display:block">
		<label style="width:16em;float:left;" for="remarks">Remarks:</label>
		<input type="text" class="text ui-widget-content ui-corner-all" id="remarks" name="remarks" />
	</div>
	<br />
	<div>
		<input type='submit' id='cancel4' class='addbutton' value='Cancel' onclick="__action.value='init'" />
		<input type='submit' id='from4to3' class='addbutton' value='Previous' onclick="__action.value='addParentgroupScreen3'" />
		<input type='submit' id='addpg' class='addbutton' value='Add' onclick="__action.value='addParentgroup'" />
	</div>
-->
<#elseif screen.action == "createLitter">

	<div class="form_header">Create litter from parentgroup ${screen.getSelectedParentgroup()}</div>
	<div style="clear:both; display:block">
		<label style="width:16em;float:left;" for='birthdate'>Birth date:</label>		
		<input type='text' class="text ui-widget-content ui-corner-all" id='birthdate' name='birthdate' value='' readonly='readonly'/>
	</div>
	<div style="clear:both; display:block">
		<label style="width:16em;float:left;" for='littersize'>Litter size:</label>
		<input type='text' class="text ui-widget-content ui-corner-all" name='littersize' id='littersize' value='<#if screen.litterSize?exists>${screen.getLitterSize()}</#if>' />
	</div>
	<div style="clear:both; display:block">
		<label style="width:16em;float:left;" for="sizeapp_toggle">Size approximate:</label>
		<input type="checkbox" id="sizeapp_toggle" name="sizeapp_toggle" value="sizeapp" checked="yes" />
	</div>
	<div style="clear:both; display:block">
		<label style="width:16em;float:left;" for='litterremarks'>Remarks:</label>
		<input type='text' class="text ui-widget-content ui-corner-all" name='litterremarks' id='litterremarks' value='<#if screen.litterRemarks?exists>${screen.getLitterRemarks()}</#if>' />
	</div>
	<div style="clear:both; display:block">
		<input type='submit' id='cancelcreatelitter' value='Cancel' onclick="__action.value='init'" />
		<input type='submit' id='addlitter' value='Add' onclick="__action.value='addLitter'" />
	</div>

<#elseif screen.action == "WeanLitter">

	<#if screen.stillToWeanYN == true>


		<div class="form_header">Wean litter ${screen.getLitter()}</div>
		<div style="clear:both; display:block">
		  <p>
			<label style="width:16em;float:left;" for='weandate'>Wean date:</label>		
			<input type='text' class="text ui-widget-content ui-corner-all" id='weandate' name='weandate' readonly='readonly' value=''/>
		      <script>
		          $(function() {
                    $( "#weandate" ).datepicker({
                        numberOfMonths: 1,
                        showButtonPanel: true,
                        dateFormat: "yy-mm-dd",
                        minDate: "${screen.getBirthdate()}"
                    });
                  });  
              </script>
           </p>   	
		</div>
		<div style="clear:both; display:block">
			<label style="width:16em;float:left;" for='weansizefemale'>Nr. of females:</label>
			<input type='text' class="text ui-widget-content ui-corner-all" name='weansizefemale' id='weansizefemale' value='<#if screen.weanSizeFemale?exists>${screen.getWeanSizeFemale()}<#else>0</#if>' />
		</div>
		<div style="clear:both; display:block">
			<label style="width:16em;float:left;" for='weansizemale'>Nr. of males:</label>
			<input type='text' class="text ui-widget-content ui-corner-all" name='weansizemale' id='weansizemale' value='<#if screen.weanSizeMale?exists>${screen.getWeanSizeMale()}<#else>0</#if>' />
		</div>
		<div style="clear:both; display:block">
			<label style="width:16em;float:left;" for='weansizeunknown'>Nr. of unknowns:</label>
			<input type='text' class="text ui-widget-content ui-corner-all" name='weansizeunknown' id='weansizeunknown' value='<#if screen.weanSizeUnknown?exists>${screen.getWeanSizeUnknown()}<#else>0</#if>' />
		</div>
		<div id="divnamebase" style="clear:both; display:block">
			<label style="width:16em;float:left;" for="namebase">Name prefix (may be empty):</label>
			<select id="namebase" name="namebase" onchange="updateStartNumberAndNewNameBase(this.value)">
				<option value=""></option>
				<option value="New">New (specify below)</option>
				<#list screen.bases as base>
					<option value="${base}" <#if screen.speciesBase == base>selected="selected"</#if> >${base}</option>
				</#list>
			</select>
		</div>
		<input id="startnumberhelper" type="hidden" value="${screen.getStartNumberHelperContent()}" />
		<div id="divnewnamebasePanel" style="display:none; clear:both">
			<label style="width:16em;float:left;" for="newnamebase">New name prefix:</label>
			<input type="text" class="text ui-widget-content ui-corner-all" name="newnamebase" id="newnamebase" class="textbox" />
		</div>
		<div id="divstartnumber" style="clear:both; display:block">
			<label style="width:16em;float:left;" for="startnumber">Start numbering at:</label>
			<input type="text" class="text ui-widget-content ui-corner-all" readonly="true" name="startnumber" id="startnumber" value="${screen.getStartNumberForPreselectedBase()?string.computer}" />
		</div>
		<div style="clear:both; display:block">
			<label style="width:16em;float:left;" for='remarks'>Weaning remarks:</label>
			<input type='text' class="text ui-widget-content ui-corner-all" name='remarks' id='remarks' />
		</div>
		<div style="clear:both; display:block">
			<label style="width:16em;float:left;" for='respres'>Responsible researcher:</label>
			<input type='text' class="text ui-widget-content ui-corner-all" name='respres' id='respres' value='<#if screen.responsibleResearcher?exists>${screen.getResponsibleResearcher()}</#if>' />
		</div>
		<div style="clear:both; display:block">
			<label style="width:16em;float:left;" for="location">Location (optional):</label>
			<select id="location" name="location">
				<option value=""></option>
				<#list screen.locationList as loc>
					<option value="${loc.name}">${loc.name}</option>
				</#list>
			</select>
		</div>
		<div style="clear:both; display:block">
			<input type='submit' id='cancelweanlitter' value='Cancel' onclick="__action.value='init'" />
			<input type='submit' id='wean' name='wean' value='Wean' onclick="__action.value='applyWean'" />
		</div>
		
		</#if>

<#elseif screen.action == "GenotypeLitter">
	<#if screen.stillToGenotypeYN == true>
		<div class="form_header">Genotype litter ${screen.getLitter()}</div>
		<p>${screen.parentInfo}</p>
		${screen.getGenotypeTable()}
		<p>
		  <input type='submit' id='addgenocol' value='Add Gene modification + state' onclick="__action.value='AddGenoCol'" />
		  <input type='submit' id='remgenocol' value='Remove Gene modification + state' onclick="__action.value='RemGenoCol'" />
		<p>
		<div style="clear:both; display:block">
		  <p>
			<label style="width:16em;float:left;" for='genodate'>Genotyping date:</label>
			<input type='text' class="text ui-widget-content ui-corner-all" name='genodate' id='genodate' value='' readonly='readonly' />
		  </p>
		</div>
		<div style="clear:both; display:block">
		  <p>
			<label style="width:16em;float:left;" for='remarks'>Genotyping remarks:</label>
			<input type='text' class="text ui-widget-content ui-corner-all" class='textbox' name='remarks' id='remarks'  />
		  </p> 
		</div>
		<div style="clear:both; display:block">
			<input type='submit' id='cancelgenotypelitter' value='Cancel' onclick="__action.value='init'" />
			<input type='submit' id='save' value='Save' onclick="__action.value='applyGenotype'" />
		</div>
	</#if>
	
<#elseif screen.action == "makeLabels">

	<div class="form_header">Download cage labels for litter ${screen.getLitter()}</div>
	<#if screen.labelDownloadLink??>
		<p>${screen.labelDownloadLink}</p>
	</#if>
	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

<#else>

	<#if screen.entity == "Parentgroups">
	
		<div class="form_header">Parentgroups</div>
		<div>
			<br />
			<a href="molgenis.do?__target=${screen.name}&__action=createParentgroup"><img id="createParentgroup" title="Create parentgroup" alt="Create parentgroup" src="generated-res/img/new.png"></a>
			<br /><br />
			${screen.pgMatrixViewer}
			<br />
			<input type='submit' id='createlitter' value='Create new litter from selected parentgroup' onclick="__action.value='createLitter'" />
			<br />
			<input type='submit' id='deactivate' value='(De)activate selected parentgroup' onclick="__action.value='deActivate'" />
		</div>
	
	<#else>
	
		<div style="clear:both" class="form_header">Litters</div>
		<div>
			<br />
			${screen.litterMatrixViewer}
			<br />
			<input type='submit' id='weanlitter' value='Wean selected litter' onclick="__action.value='WeanLitter'" />
			<br />		
			<input type='submit' id='genotypelitter' value='Genotype selected litter' onclick="__action.value='GenotypeLitter'" />
			<br />
			
			<input type='submit' id='label' value='Make cage labels for selected litter' onclick="__action.value='makeLabels'" />
			<br />
			<input type='submit' id='deactivate' value='(De)activate selected litter' onclick="__action.value='deActivateLitter'" />
		</div>
	
	</#if>

</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	jQuery('#createlitter').button();
	jQuery('#from2to3').button();
	jQuery('#from3to4').button();
	jQuery('#from4to3').button();
	jQuery('#from3to2').button();
	jQuery('#cancel2').button();
	jQuery('#cancel3').button();
	jQuery('#cancel4').button();
	jQuery('#addpg').button();
	jQuery('#deactivate').button();
	jQuery('#weanlitter').button();
	jQuery('#genotypelitter').button();
	jQuery('#label').button();
	jQuery('#addlitter').button();
	jQuery('#wean').button();
	jQuery('#addgenocol').button();
	jQuery('#remgenocol').button();
	jQuery('#save').button();
	jQuery('#cancelcreatelitter').button();
	jQuery('#cancelweanlitter').button();
	jQuery('#cancelgenotypelitter').button();
	
	jQuery('#line').chosen();
	jQuery('#namebase').chosen();
	jQuery('#location').chosen();
	
	$(function() {
		$("#birthdate").datepicker({
			numberOfMonths: 1,
			showButtonPanel: true,
			dateFormat: "yy-mm-dd"
		});
	});
	$(function() {
        $( "#genodate" ).datepicker({
            numberOfMonths: 1,
            showButtonPanel: true,
            dateFormat: "yy-mm-dd"
        });
    });


	var oTable = jQuery('#pgstable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
