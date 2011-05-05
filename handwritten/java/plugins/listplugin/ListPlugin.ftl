<#macro plugins_listplugin_ListPlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}" />
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

<div id="togglesdiv">
Show date-time info with values:
<input type="checkbox" id="datetimetoggle" name="datetimetoggle" value="datetime" onclick="fnReloadTable()" />
&nbsp;
Limit values to most recent one:
<input type="checkbox" id="limitvaltoggle" name="limitvaltoggle" value="limitval" checked="true" onclick="fnReloadTable()" />
</div>

<div id="showhideselect">
	<strong>Show/hide features</strong>
	<select id="" name="" onchange="fnAddRemFeature(this.value);">
		<option value="0">&nbsp;</option>
		<#assign i = 1>
		<#list screen.featureList as fl>
			<option value="${fl.id}">${fl.name}</option>
			<#assign i = i + 1>
		</#list>
	</select>
</div>

<div id='top_part'>
	<strong>Type of target:</strong>
	<select name="targettype" id="targettype" onchange="fnReloadTable()">
		<option value="All">All</option>
		<option value="Individual">Individual</option>
		<option value="Panel">Panel</option>
		<option value="Location">Location</option>
	</select>
</div>

<div id="demo">

	<table cellpadding="0" cellspacing="0" border="0" class="display" id="listtable">
	<thead>
		<tr>
			<th></th> <!-- Here the header for the Target column will be inserted -->
			<#assign i = 1>
			<#list screen.featureList as fl>
				<th></th> <!-- Here the header for this Feature column will be inserted -->
				<#assign i = i + 1>
			</#list>
		</tr>
	</thead>
	
	<tbody>
		<tr colspan="${i}" class="gradeA">
			<td>Loading data from server</td>
		</tr>
	</tbody>
	
	<tfoot>
		<tr>
			<th></th>
			<#assign i = 1>
			<#list screen.featureList as fl>
				<th></th>
				<#assign i = i + 1>
			</#list>
		</tr>
	</tfoot>
	
	</table>
</div>

<div id='buttons_part' style='border:1px solid; margin:10px; padding:10px; float:left'>
	<input type="hidden" name="saveselection" id="saveselection" />
	Add visible selection to Batch:
	<select id="batch" name="batch">
		<#list screen.batchList as batch>
			<option value="${batch.id}">${batch.name}</option>
		</#list>
	</select>
	<br />Or, save as new Batch with name: <input type="text" name="newbatchname" id="newbatchname" class="textbox" />
	<br /><input type='submit' class='addbutton' value='Apply' onclick="var targetNames = fnMakeGroup(); saveselection.value = targetNames; __action.value='saveBatch'" />
</div>
<div style='clear:left'>
	<!-- Bogus div so previous div remains within the page limits -->
</div>

<#--end of your plugin-->
			</div>
		</div>
	</div>
</form>

<script>

var storedDisplayStart;
var storedDisplayLength;
var nrcols = document.getElementById('listtable').getElementsByTagName('tr')[0].getElementsByTagName('th').length;

<!-- Initialization of 'listtable' as DataTable -->
var oTable = jQuery('#listtable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": true,
	  "sAjaxSource": "EventViewerJSONServlet",
	  "fnServerData": function ( sSource, aoData, fnCallback ) {
		/* Add extra data to the sender */
		aoData.push( { "name": "printValInfo", "value": document.getElementById('datetimetoggle').checked } );
		aoData.push( { "name": "limitVal", "value": document.getElementById('limitvaltoggle').checked } );
		aoData.push( { "name": "targetType", "value": document.getElementById('targettype').value } );
		jQuery.getJSON( sSource, aoData, function (json) {
			storedDisplayStart = json.iDisplayStart;
			storedDisplayLength = json.iDisplayLength;
			fnCallback(json);
		} );
	  },
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false }
);

<!-- piece of code to set the headers and footers, and initially hide all columns except the one with the targets -->
var oSettings = oTable.fnSettings();
oSettings.aoColumns[0].sTitle = document.getElementById('targettype').value;
nTh = oSettings.aoColumns[0].nTh;
nTh.innerHTML = oSettings.aoColumns[0].sTitle;
nTf = oSettings.aoColumns[0].nTf;
nTf.innerHTML = "<input type='text' name='search_feat0' value= 'Filter " + document.getElementById('targettype').value + "' class='search_init' onkeyup='oTable.fnFilter(this.value, 0)' onclick='if ( this.className == \"search_init\" ){ this.className = \"\"; this.value = \"\";}' />";
for (var i = 1; i < nrcols; i++) {
	oTable.fnSetColumnVis(i, false);
}

<!-- Handler for adding or removing a feature -->
function fnAddRemFeature(sId) {
	if (sId > 0) {
	
		var oSettings = oTable.fnSettings();
		oTable.fnClearTable(0);
		
		// Temporarily attach new 'fnServerData' for adding/removing column
		oSettings.fnServerData = function (sSource, aoData, fnCallback) {
			aoData.push( { "name": "feature", "value": sId } );
			aoData.push( { "name": "printValInfo", "value": document.getElementById('datetimetoggle').checked } );
			aoData.push( { "name": "limitVal", "value": document.getElementById('limitvaltoggle').checked } );
			aoData.push( { "name": "targetType", "value": document.getElementById('targettype').value } );
			aoData.splice(3, 1, { "name": "iDisplayStart", "value": storedDisplayStart });
			oSettings._iDisplayStart = storedDisplayStart;
			aoData.splice(4, 1, { "name": "iDisplayLength", "value": storedDisplayLength });
			oSettings._iDisplayLength = storedDisplayLength;	
			jQuery.getJSON( sSource, aoData, function (json) {
				storedDisplayStart = json.iDisplayStart;
				storedDisplayLength = json.iDisplayLength;
				var i;
				for (i = 0; i < json.iFeatureCol; i++) {
					oTable.fnSetColumnVis(i, true);
					oSettings.aoColumns[i].sTitle = json.asFeatureNames[i];
					nTh = oSettings.aoColumns[i].nTh;
					nTh.innerHTML = oSettings.aoColumns[i].sTitle;
					nTf = oSettings.aoColumns[i].nTf;
					var filterText;
					var className = "";
					if (json.asFilterTerms[i] != "") {
						filterText = json.asFilterTerms[i];
						oSettings.aoPreSearchCols[i].sSearch = filterText;
					} else {
						filterText = "Filter " + json.asFeatureNames[i];
						className = "search_init";
					}
					nTf.innerHTML = "<input type='text' name='search_feat" + i + "' value= '" + filterText + "' class='" + className + "' onkeyup='oTable.fnFilter(this.value, " + i + ")' onclick='if ( this.className == \"search_init\" ){ this.className = \"\"; this.value = \"\";}' />";
				}
				for (j = i; j < nrcols; j++) {
					oSettings.aoPreSearchCols[j].sSearch = "";
					oTable.fnSetColumnVis(j, false);
				}
				fnCallback(json);
			} );
		};
		
		oTable.fnDraw();
		
		// Reattach standard 'fnServerData'
		oSettings.fnServerData = function ( sSource, aoData, fnCallback ) {
			/* Add extra data to the sender */
			aoData.push( { "name": "printValInfo", "value": document.getElementById('datetimetoggle').checked } );
			aoData.push( { "name": "limitVal", "value": document.getElementById('limitvaltoggle').checked } );
			aoData.push( { "name": "targetType", "value": document.getElementById('targettype').value } );
			jQuery.getJSON( sSource, aoData, function (json) {
				storedDisplayStart = json.iDisplayStart;
				storedDisplayLength = json.iDisplayLength;
				fnCallback(json);
			} );
		};
	}
}

<!-- Handler for updating the table in other situations than when adding or removing a feature -->
function fnReloadTable() {
	oTable.fnClearTable(0);
			
	// Attach modified 'fnServerData'
	var oSettings = oTable.fnSettings();
	oSettings.fnServerData = function ( sSource, aoData, fnCallback ) {
		/* Add extra data to the sender */
		aoData.push( { "name": "printValInfo", "value": document.getElementById('datetimetoggle').checked } );
		aoData.push( { "name": "limitVal", "value": document.getElementById('limitvaltoggle').checked } );
		aoData.push( { "name": "targetType", "value": document.getElementById('targettype').value } );
		// These lines are why all this code is here, we do not want the table to lose its paging info at every update:
		aoData.splice(3, 1, { "name": "iDisplayStart", "value": storedDisplayStart });
		oSettings._iDisplayStart = storedDisplayStart;
		aoData.splice(4, 1, { "name": "iDisplayLength", "value": storedDisplayLength });
		oSettings._iDisplayLength = storedDisplayLength;
		//
		jQuery.getJSON( sSource, aoData, function (json) {
			for (i = 0; i < json.iFeatureCol; i++) {
				oTable.fnSetColumnVis(i, true);
				oSettings.aoColumns[i].sTitle = json.asFeatureNames[i];
				nTh = oSettings.aoColumns[i].nTh;
				nTh.innerHTML = oSettings.aoColumns[i].sTitle;
				nTf = oSettings.aoColumns[i].nTf;
				var filterText;
				var className = "";
				if (json.asFilterTerms[i] != "") {
					filterText = json.asFilterTerms[i];
					oSettings.aoPreSearchCols[i].sSearch = filterText;
				} else {
					filterText = "Filter " + json.asFeatureNames[i];
					className = "search_init";
				}
				nTf.innerHTML = "<input type='text' name='search_feat" + i + "' value= '" + filterText + "' class='" + className + "' onkeyup='oTable.fnFilter(this.value, " + i + ")' onclick='if ( this.className == \"search_init\" ){ this.className = \"\"; this.value = \"\";}' />";
			}
			for (j = i; j < nrcols; j++) {
				oSettings.aoPreSearchCols[j].sSearch = "";
				oTable.fnSetColumnVis(j, false);
			}
			fnCallback(json);
		} );
	};
	
	oTable.fnDraw();
	
	// Reattach standard 'fnServerData'
	oSettings.fnServerData = function ( sSource, aoData, fnCallback ) {
		/* Add extra data to the sender */
		aoData.push( { "name": "printValInfo", "value": document.getElementById('datetimetoggle').checked } );
		aoData.push( { "name": "limitVal", "value": document.getElementById('limitvaltoggle').checked } );
		aoData.push( { "name": "targetType", "value": document.getElementById('targettype').value } );
		jQuery.getJSON( sSource, aoData, function (json) {
			storedDisplayStart = json.iDisplayStart;
			storedDisplayLength = json.iDisplayLength;
			fnCallback(json);
		} );
	};	
}

<!-- Handler for populating a list of selected targets -->
function fnMakeGroup() {
	var oSettings = oTable.fnSettings();
	// list of rows which we're going to loop through
	var aiRows;
	
	// use only filtered rows
	//aiRows = oSettings.aiDisplay;
	// use all rows
	aiRows = oSettings.aiDisplayMaster; // all row numbers

	// set up data array	
	var asResultData = new Array();
	
	for (var i = 0, c = aiRows.length; i < c; i++) {
		iRow = aiRows[i];
		var aData = oTable.fnGetData(iRow);
		var sValue = aData[0];
		
		// ignore empty values
		if (sValue.length == 0) continue;

		// ignore unique values?
		// else if (bUnique == true && jQuery.inArray(sValue, asResultData) > -1) continue;
		
		// else push the value onto the result data array
		else asResultData.push(sValue);
	}
	return asResultData;
}

</script>

</#macro>

