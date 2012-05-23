<script src="jqGrid/grid.locale-en.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="jqGrid/jquery.json-2.3.min.js" type="text/javascript"></script>

<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.dialog.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.datepicker.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="jquery/development-bundle/themes/smoothness/jquery-ui-1.8.7.custom.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.multiselect.css">
<script type="text/javascript">
var colNames = [
	   	<#list columns as col>
	    '${col.name}'<#if col_has_next>,</#if>
		</#list> 
		];

var colModel = [
	   		<#list columns as col>	
	   		{name:'${col.name}',index:'${col.name}', width:150}<#if col_has_next>,</#if>
	   		</#list>
	   		];
	   		
function createJQGrid() {
	var myColNames = $.toJSON(colNames);
	var myColModel = $.toJSON(colModel);

	jQuery("#${tableId}").jqGrid({
	   	url:'${dataSourceUrl}',
		datatype: "json",
		jsonReader: { repeatitems: false },
		postData : {colNames : myColNames, colModel: myColModel},
	   	colNames: colNames,   	
	   	colModel: colModel,
	   	rowNum: 10,
	   	rowList: [10,20,30],
	   	pager: '#${tableId}Pager',
	   	sortname: '${sortName}',
	    viewrecords: true,
	    sortorder: "desc",
	    caption:"jqGrid viewer"
	});
	jQuery("#${tableId}").jqGrid('navGrid','#${tableId}Pager',{edit:false,add:false,del:false});
}

$(document).ready(function(){
	createJQGrid();	
});
</script>

<table id="${tableId}"></table>
<div id="${tableId}Pager"></div>