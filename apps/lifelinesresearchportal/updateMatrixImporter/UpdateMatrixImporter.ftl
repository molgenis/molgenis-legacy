<#macro UpdateMatrixImporter screen>
<script src="jqGrid/grid.locale-en.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.src.js" type="text/javascript"></script>
<script src="jqGrid/jquery.json-2.3.min.js" type="text/javascript"></script>
<script type="text/javascript">
	$(document).ready(function() {
		
		$('#previousStepSummary').button();
		$('#importUploadFile').button();
		$('#preview').button();
		$('#uploadNewFile').button();
		$('#fileUploadNext').button();
		$('#fileUploadCancel').button();
		$('#fileUploadNext').click(function(){
			if($('#uploadFileName').val() != ""){
				$('input[name="__action"]').val('uploadFile');
				$('form[name="${screen.name}"]').submit();
			}else{
				alert("Please upload a file!");
			}
		});
	});
	
	function generateReport(reportObject){
		
		existingColumns = reportObject['colHeaders'];
		newColumns = reportObject['newFeatures'];
		existingRowRecords = reportObject['rowHeaders'];
		newRowRecords = reportObject['newTargets'];
		$('#reportForm').prepend("<h3>Summary of upload file:</h3></br>");
		$('#existingColHeaders').append("Existing columns: " + existingColumns.length);
		$('#newColHeaders').append("New columns: " + newColumns.length);
		$('#existingRowRecords').append("Existing records: " + existingRowRecords.length);
		$('#newRowRecords').append("New records: " + newRowRecords.length);
		
		
		$('#reportForm div').each(function(){
			hoverOver(this);
		});
		
		$('#newColHeaders').click(function(){
			
			$('#reportForm').hide();
			
			if(newColumns.length > 0){
				
				if(newColumns.length > 0 && $('#newColumnsMapping').children().length == 0){
			
					var molgenisDataOptions;
					
					var protocolTables;
					
					<#list screen.getFeatureDataTypes() as dataType>
						<#if dataType == "string">
							molgenisDataOptions += "<option value=\"${dataType}\" selected=\"selected\">${dataType}</option>";
						<#else>
							molgenisDataOptions += "<option value=\"${dataType}\">${dataType}</option>";
						</#if>
					</#list>
					<#list screen.getProtocolTables() as table>
						<#if table == "NotClassified">
							protocolTables += "<option value=\"${table}\" selected=\"selected\">${table}</option>";
						<#else>
							protocolTables += "<option value=\"${table}\">${table}</option>";
						</#if>
					</#list>
					
					mappingTable = "<table id=\"mappingTable\" style=\"border:1px\"><tr><th>Variable</th><th>Data type</th><th>Category</th><th>Table</th><th>Import</th></tr>";
					
					for(var i = 0; i < newColumns.length; i++ ){
						
						identifier = newColumns[i].replace(" ","_");
						
						selectDataTypes = "<select id=\"" + identifier + "_dataType\" name=\"" + identifier + "_dataType\">" + molgenisDataOptions + "</select>";
						
						categories = "<div id=\"" + identifier + "_categoriesControl\" style=\"display:none\"><select id=\"" + identifier + "_categories\" name=\"" + identifier + "_categories\"></select>"
							+ "<input type=\"button\" id=\"" + identifier + "_editCategory\" style=\"font-size:0.7em\" value=\"edit\" /></div>";
						
						tables = "<select id=\"" + identifier + "_protocolTable\" name=\"" + identifier + "_protocolTable\">" + protocolTables + "</select>";
						
						checkBox = "<input type=\"checkbox\" id=\"" + identifier + "_check\" name=\"" + identifier + "_check\" checked=\"checked\" value=\"yes\"> yes";
						
						mappingTable += "<tr id=\"" + identifier + "_mapping\"><td>" + newColumns[i] + "</td><td>" 
							+ selectDataTypes + "</td><td>" + categories + "</td><td>" + tables + "</td><td>" + checkBox + "</td></tr>";
	
					} 
					
					mappingTable += "</table>";
					
					mappingHeader = "Please specify the type of the new columns. The data type is by default string. </br></br>";
					
					mappingHeader += "<fieldset>Upload your mapping file <input type=\"file\" id=\"mappingForColumns\" name=\"mappingForColumns\" />";
					
					mappingHeader += "<input type=\"submit\" id=\"uploadMapping\" name=\"uploadMapping\""+
						"style=\"font-size:0.6em;color:#03406A\" value=\"upload\" onclick=\"__action.value='uploadMapping';return true;\">";
					
					mappingHeader += "<input type=\"submit\" id=\"downloadTemplate\" name=\"downloadTemplate\""+
						"style=\"font-size:0.6em;color:#03406A\" value=\"download template\" onclick=\"__action.value='downloadTemplate';return true;\"></fieldset>";
					
					$('#newColumnsMapping').append(mappingTable);
					$('#newColumnsMapping').prepend(mappingHeader);
					$('#newColumnsMapping').append("</br><input type=\"button\" id=\"previousFromMapping\" style=\"font-size:0.6em;color:#03406A\" value=\"Previous\"/>");
					
					<#if screen.getJsonForMapping()??>
						mappingResult = eval(${screen.getJsonForMapping()});
						for(var index = 0; index < mappingResult.length; index++){
							eachMapping = mappingResult[index];
							identifier = eachMapping["variableName"].replace(" ","_");
							$('#' + identifier + '_dataType').val(eachMapping["dataType"]);
							if(eachMapping["dataType"] == "categorical"){
								$('#' + identifier + '_categoriesControl').show();
							}
							
							listOfCategories = eachMapping["listOfCategories"];
							
							for(var i = 0; i < listOfCategories.length; i++){
								$('#' + identifier + '_categories').append("<option>" + listOfCategories[i] + "</option>");
							}
							$('#' + identifier + '_protocolTable').val(eachMapping["table"]);
						}
					</#if>
					
					
					
					$('#previousFromMapping').button();
					$('#downloadTemplate').button();
					$('#uploadMapping').button();
					
					$('#mappingTable th').width(700);
					$('#mappingTable td').css('text-align','center');
					$('#mappingTable th').css('background','#65A5D1');
					$('#mappingTable tr').css('border-bottom','1px dotted');
					
					$('#previousFromMapping').click(function(){
						$('#newColumnsMapping').hide();
						$('#reportForm').fadeIn();
					});
					
					$('#mappingTable tr:gt(0)').each(function(){
						
						$(this).find('select').eq(0).change(function(){
							
							identifier = $(this).parents('tr:first').attr('id').replace("_mapping","");
							
							if($(this).val() == "categorical"){
								$('#' + identifier + '_categoriesControl').show();
							}else{
								$('#' + identifier + '_categoriesControl').hide();
							}
						});
						
						$(this).find('td:eq(2) input').click(function(){
							identifier = $(this).parents('tr:first').attr('id').replace("_mapping","");
							
							select = "<select id=\"" + identifier + "_dialogSelect\">";
							
							$('#' + identifier + '_categories option').each(function(){
								select += "<option>" + $(this).html() + "</option>";
							});
							
							select += "</select>";
							
							removeButton = "<input type=\"button\" id=\"" + identifier + "_dialogRemove\" value=\"remove\"/>";
							
							addButton = "<input type=\"button\" id=\"" + identifier + "_dialogAdd\" value=\"add\"/>";
							
							addCategoryInput = "<div id=\"" + identifier + "_dialogInput\"></div>";
							
							addCategoryInput += "<input type=\"text\" id=\"" + identifier + "_dialogCode\" size=\"25\">";
							
							addCategoryInput += "<input type=\"text\" id=\"" + identifier + "_dialogString\" size=\"25\"></div>";
							
							dialogPanel = "<div id=\"" + identifier + "_dialog\">" + select + removeButton + addButton + addCategoryInput + "</div>";
							
							$('#' + identifier + '_categoriesControl').append(dialogPanel);
							
							$('#' + identifier + '_dialog').dialog();
						});
					});
				}
				
				$('#newColumnsMapping').fadeIn(1000);
				
			}else{
				alert("There are no new columns in the file!");
			}
		});
		
		//Submit the form and reload page to show the new records only
		$('#newRowRecords').click(function(){
			$('input[name="__action"]').val('showNewRecordsOnly');
			$('form[name="${screen.name}"]').submit();
		});
	}
	
	function hoverOver(element){
	
		$(element).hover(
		  function () {
		  	font = 	parseInt($(this).css('font-size'));
		    $(this).css('font-size', font + 2);
		    $(this).css('color', 'grey');
		    $('#notification').append("Click to see " + $(this).attr('id'));
		  }, 
		  function () {
		    font = parseInt($(this).css('font-size'));
		    $(this).css('font-size', font - 2);
		    $(this).css('color', 'black');
		    $('#notification').empty();
		  }
		);
	}
</script>

<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
	<input type="hidden" name="__dataTypeCount">
	
	<div class="formscreen">	
		
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#if screen.getSTATUS() == "UploadFile">
			<table>
				<tr><td style="font-size:22px">
					<div id="uploadFileForm" style="border-radius:0.2em;">
						<fieldset style="border-radius:0.2em;width:400px">
					            <label >Data type</label><br>
					            <input type="radio" name="uploadFileType" value="Pheno" checked>Pheno<br>
					            <input type="radio" name="uploadFileType" value="Geno">Geno<br> 
					    </fieldset>
					    <fieldset style="border-radius:0.2em;">
					            <label >Upload a file</label><br>
					            <input type="file" id="uploadFileName" name="uploadFileName" style="font-size:12px"/> 
					    </fieldset>
			            <fieldset style="border-radius:0.2em;">
			            <input id="fileUploadNext" type="button" style="font-size:1.0em;color:#03406A" value="Next" />
			            <input id="fileUploadCancel" type="button" style="font-size:1.0em;color:#03406A" value="Cancel"/>
			            </fieldset>
			    	</div>
		    	</td><td style="font-size:22px">
			    	<div id="explanationForm">
			    		Please upload a file for Phenotypic or Genotypic data matrix
			    	</div>
		    	</td></tr>
	    	</table>
    	<#elseif screen.getSTATUS() == "CheckFile">
    		<div id="newColumnsMapping"></div>
    		<div id="reportForm">
    			<fieldset>
    				<table style="width:100%;">
	    				<tr><td id="reportContent" style="width:50%;font-size:22px">
			    			<div id="existingColHeaders" style="cursor:pointer" value="existing columns"></div>
			    			<div id="newColHeaders" style="cursor:pointer" value="new columns"></div>
			    			<div id="existingRowRecords" style="cursor:pointer" value="existing records"></div>
			    			<div id="newRowRecords" style="cursor:pointer" value="new records"></div>
			    		</td><td id="notification" style="width:50%;font-size:22px"></td></tr>
	    			</table>
    			</fieldset>
    			<fieldset id="controlPanel">
    				<input type="submit" name="preview" id="preview" value="Preview"  
    					style="font-size:0.6em;color:#03406A" onclick="__action.value='previewFileAction';return true;"/>
					<input type="submit" name="importUploadFile" id="importUploadFile" value="Import"  
						style="font-size:0.6em;color:#03406A" onclick="__action.value='importUploadFile';return true;"/>
    				<input type="submit" name="uploadNewFile" id="uploadNewFile" value="Upload a new file" 
    					style="font-size:0.6em;color:#03406A" onclick="__action.value='uploadNewFile';return true;"/>
    			</fieldset>
    			<script>
    				generateReport(${screen.getReport()});
    				<#if screen.getJsonForMapping()??>
						$('#newColHeaders').trigger('click');
					</#if>
    			</script>
    		</div>
    		
    	<#elseif screen.getSTATUS() == "previewFile">
    		${screen.getTableView()}
    		<fieldset id="controlPanelForImporting">
				<input type="submit" name="previousStepSummary" id="previousStepSummary" value="Previous"  
    					style="font-size:0.6em;color:#03406A" onclick="__action.value='previousStepSummary';return true;"/>
				<input type="submit" name="uploadNewFile" id="uploadNewFile" value="Upload a new file" 
					style="font-size:0.6em;color:#03406A" onclick="__action.value='uploadNewFile';return true;"/>
			</fieldset>
    	<#elseif screen.getSTATUS() == "mappingVariables">
    		<div id="newColumnsMapping" style="display:none"></div>
    	</#if>
	</div>
</form>
</#macro>