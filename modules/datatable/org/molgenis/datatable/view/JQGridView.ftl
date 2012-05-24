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
    caption : "datatable",
    jqGrid : null,
    
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
    	
    	if(config.caption) {
    		this.caption = config.caption;
    	}
        
        this.grid = this.createJQGrid();
        this.createDialog();
    },
    
    getGrid: function() {
    	return $(this.tableSelector);
    },
    
    createJQGrid : function() {
    	var grid = jQuery(this.tableSelector).jqGrid({
            url: this.dataSource.dataSourceUrl,
            datatype: "json",
            jsonReader: { repeatitems: false },
            postData : 
            	{viewType : 'jqGridView',
            	colNames : $.toJSON(this.colNames), 
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
            caption: this.caption
        });
        grid.jqGrid('navGrid', this.pagerSelector,
            {search:true, edit:false,add:false,del:false},
            {}, // edit options
            {}, // add options
            {}, //del options
            {multipleSearch:true} // search options
        );
        return grid;
	},
    
    createDialog : function() {
    	var self = JQGridView;
    	$( "#dialog-form" ).dialog({
		    autoOpen: false,
		    height: 300,
		    width: 350,
		    modal: true,
		    buttons: {
		            "Export": function() {
		            	var viewType = $("input[name='viewType']:checked").val();
		            	var exportSelection = $("input[name='exportSelection']:checked").val();
		
		              	var myUrl = self.grid.jqGrid('getGridParam', 'url') + "?";
		              	var postData = self.grid.jqGrid('getGridParam', 'postData');
		
						var first = true;
						$.each(postData, function(key, value) {
							if(key != "viewType") {
								if(!first) {
									myUrl += "&";
								}
								myUrl += key+"="+encodeURIComponent(value);
								first = false;
							}
						});
		            	
		                //e.preventDefault();  //stop the browser from following
		                window.location.href = myUrl + "&viewType=" + viewType + "&exportSelection=" + exportSelection;
		            },
		            Cancel: function() {
		                $( this ).dialog( "close" );
		            }
		    },
		    close: function() {
		    }
		});
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
    var myGrid = JQGridView.init({tableSelector : "#${tableId}", colModel: myColModel, colNames: myColNames, sortColumn: mySortColumn, dataSource: myDataSource});
    $("#exportCsv").click(function() {
        $( "#dialog-form" ).dialog( "open" );    	
    });
});

</script>

<table id="${tableId}"></table>
<div id="${tableId}Pager"></div>
<input id="exportCsv" type="button" value="save to CSV"/>
<div id="dialog-form" title="Export data">
	<form>
	<fieldset>
            <label >File type</label><br>
            <input type="radio" name="viewType" value="excelView" checked>Excel<br>
            <input type="radio" name="viewType" value="spssView">Spss<br> 
            <input type="radio" name="viewType" value="csvView">Csv<br> 
            <label>Export option</label><br>
            <input type="radio" name="exportSelection" value="All" checked>All<br>
            <input type="radio" name="exportSelection" value="Grid">Grid<br> 
	</fieldset>
	</form>
</div>