<#macro UpdateMatrixImporter screen>
<script src="jqGrid/grid.locale-en.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.src.js" type="text/javascript"></script>
<script src="jqGrid/jquery.json-2.3.min.js" type="text/javascript"></script>
<script type="text/javascript">
	$(document).ready(function() {
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
		$('#reportForm').prepend('<h3>Summary of upload file:</h3></br>');
		$('#allColHeaders').append('Existing columns: ' + existingColumns.length);
		$('#newColHeaders').append('New columns: ' + newColumns.length);
		$('#allRowRecords').append('Existing records: ' + existingRowRecords.length);
		$('#newRowRecords').append('New records: ' + newRowRecords.length);
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
				<tr><td>
					<div id="uploadFileForm" style="border-radius:0.2em;">
						<fieldset style="border-radius:0.2em;width:500px">
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
		    	</td><td>
			    	<div id="explanationForm">
			    		Please upload a file for Phenotypic or Genotypic data matrix
			    	</div>
		    	</td></tr>
	    	</table>
    	<#elseif screen.getSTATUS() == "CheckFile">
    		<div id="reportForm">
    			<fieldset>
	    			<div id="allColHeaders"></div>
	    			<div id="newColHeaders"></div>
	    			<div id="allRowRecords"></div>
	    			<div id="newRowRecords"></div>
    			</fieldset>
    			<fieldset id="controlPanel">
    				<input type="submit" name="preview" id="preview" value="Preview"  
    					style="font-size:0.6em;color:#03406A" onclick="__action.value='previewFile';return true;"/>
    				<input type="submit" name="uploadNewFile" id="uploadNewFile" value="Upload a new file" 
    					style="font-size:0.6em;color:#03406A" onclick="__action.value='uploadNewFile';return true;"/>
    			</fieldset>
    		</div>
    		<script>generateReport(${screen.getReport()});</script>
    	<#elseif screen.getSTATUS() == "previewFile">
    		${screen.getTableView()}
    		<fieldset id="controlPanelForImporting">
				<input type="submit" name="importUploadFile" id="importUploadFile" value="Import"  
					style="font-size:0.6em;color:#03406A" onclick="__action.value='importUploadFile';return true;"/>
				<input type="submit" name="uploadNewFile" id="uploadNewFile" value="Upload a new file" 
					style="font-size:0.6em;color:#03406A" onclick="__action.value='uploadNewFile';return true;"/>
			</fieldset>
    	</#if>
	</div>
</form>
</#macro>

