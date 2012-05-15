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
	   		{name:'${col.name}',index:'${col.name}', width:55}<#if col_has_next>,</#if>
	   		</#list>
	   		];
	   		
function createJQGrid() {
	jQuery("#${tableId}").jqGrid({
	   	url:'${dataSourceUrl}',
		datatype: "json",
		postData : {colNames : colNames, colModel: colModel},
	   	colNames: colNames,   	
	   	colModel: colModel,
	   	rowNum: 10,
	   	rowList: [10,20,30],
	   	pager: '#pager2',
	   	sortname: '${sortName}',
	    viewrecords: true,
	    sortorder: "desc",
	    caption:"jqGrid viewer"
	});
	jQuery("#${tableId}").jqGrid('navGrid','#${tableId}pager',{edit:false,add:false,del:false});
}

$(document).ready(function(){
	createJQGrid();	
}
</script>

<table id="${tableId}"></table>
<div id="${tableId}pager"></div>