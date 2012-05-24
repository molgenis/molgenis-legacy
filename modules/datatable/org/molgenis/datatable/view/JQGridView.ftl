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
//TODO: place in JS file after dev!
var JQGridView = {
	tableSelector : null,
	pagerSelector : null,
    colModel : null,
    colNames : null, 
    dataSource : null,
    sortColumn : 'id',
    
    init: function(config) {
    	//required parameters
        this.tableSelector = config.tableSelector;
        this.colModel = config.colModel;
        this.colNames = config.colNames;
        this.dataSource = config.dataSource;
        
        //optional parameters
        if(config.pagerSelector) {
        	this.pagerSelector = config.pagerSelector;
        } else {
        	this.pagerSelector = this.tableSelector + 'Pager';
    	}
    	
    	if(config.sortColumn) {
    		this.sortColumn = config.sortColumn;
    	}
        
        
        this.createJQGrid();
    },
    
    getGrid: function() {
    	return $(this.tableSelector);
    },
    
    createJQGrid : function() {
        jQuery(this.tableSelector).jqGrid({
            url: this.dataSource.dataSourceUrl,
            datatype: "json",
            jsonReader: { repeatitems: false },
            postData : 
            	{colNames : $.toJSON(this.colNames), 
        		colModel: $.toJSON(this.colModel), 
        		dataSource: $.toJSON(this.dataSource)},
            colNames: this.colNames,   	
            colModel: this.colModel,
            rowNum: 10,
            rowList: [10,20,30],
            pager: this.pagerSelector,
            sortname: this.sortColumn,
            viewrecords: true,
            sortorder: "desc",
            caption:"jqGrid viewer"
        });
        jQuery(this.tableSelector).jqGrid('navGrid', this.pagerSelector,
            {search:true, edit:false,add:false,del:false},
            {}, // edit options
            {}, // add options
            {}, //del options
            {multipleSearch:true} // search options
        );
    }
}



$(document).ready(function() {
    var myColModel = 
        [
            <#list columns as col>	
            {name:'${col.name}',index:'${col.name}', width:150, searchrules:{required:${(!col.nillable)?string}${col.columnType}}}<#if col_has_next>,</#if>
            </#list>
        ];
    var myColNames = [
            <#list columns as col>
            '${col.name}'<#if col_has_next>,</#if>
            </#list> 
        ];		   		
	var myDataSource = {type: 'jdbc', fromExpression: 'Country', dataSourceUrl: '${dataSourceUrl}'};
	var mySortColumn = '${sortName}';
    var myGrid1 = JQGridView.init({tableSelector : "#${tableId}1", colModel: myColModel, colNames: myColNames, sortColumn: mySortColumn, dataSource: myDataSource});
});
</script>

<table id="${tableId}1"></table>
<div id="${tableId}1Pager"></div>