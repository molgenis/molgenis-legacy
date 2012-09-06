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
			
			$('#reportForm').fadeOut();
			
			var molgenisDataOptions;
			<#list screen.getFeatureDataTypes() as dataType>
				$('#newColumnsMapping').append("${dataType}</br>");
				molgenisDataOptions += "<option>${dataType}</option>";
			</#list>
			
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
    			<script>generateReport(${screen.getReport()});</script>
    		</div>
    		<div id="newColumnsMapping">
    		</div>
    	<#elseif screen.getSTATUS() == "previewFile">
    		${screen.getTableView()}
    		<fieldset id="controlPanelForImporting">
				<input type="submit" name="previousStepSummary" id="previousStepSummary" value="Previous"  
    					style="font-size:0.6em;color:#03406A" onclick="__action.value='previousStepSummary';return true;"/>
				<input type="submit" name="uploadNewFile" id="uploadNewFile" value="Upload a new file" 
					style="font-size:0.6em;color:#03406A" onclick="__action.value='uploadNewFile';return true;"/>
			</fieldset>
    	</#if>
	</div>
</form>
</#macro>