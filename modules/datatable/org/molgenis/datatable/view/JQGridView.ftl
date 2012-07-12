<script src="jqGrid/grid.locale-en.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="jqGrid/jquery.json-2.3.min.js" type="text/javascript"></script>

<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.dialog.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.datepicker.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="jquery/development-bundle/themes/smoothness/jquery-ui-1.8.7.custom.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.multiselect.css">

<link href="dynatree-1.2.0/src/skin/ui.dynatree.css" rel="stylesheet" type="text/css" id="skinSheet">
<script src="dynatree-1.2.0/src/jquery.dynatree.js" type="text/javascript"></script>


<script type="text/javascript">
//TODO: place in JS file after dev!
var JQGridView = {
    tableSelector : null,
    pagerSelector : null,
    config : null,
    
    init: function(tableSelector, pagerSelector, config) {
        this.tableSelector = tableSelector;
        this.pagerSelector = pagerSelector;
        this.config = config;
        
        this.grid = this.createJQGrid();
        //this.createDialog();
        
        return JQGridView;
    },
    
    getGrid: function() {
    	return $(this.tableSelector);
    },
    
    changeColumns: function(columnModel) {
    	var self = JQGridView;
    	this.colModel = columnModel;
    	self.colNames = this.getColumnNames(columnModel);
    	$(this.tableSelector).jqGrid('GridUnload');
    	this.createJQGrid();
    },
    
    createJQGrid : function() {
    	var grid = jQuery(this.tableSelector).jqGrid(this.config);
        grid.jqGrid('navGrid', this.pagerSelector,
            {search:true, edit:false,add:false,del:false},
            {}, // edit options
            {}, // add options
            {}, //del options
            {multipleSearch:true} // search options
        );
        return grid;
	},
    
    getColumnNames : function(colModel) {
    	result = new Array();
    	$.each(colModel, function(index, value) {
    		result.push(value.name);
    	});
    	return result;
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
		
		              	var myUrl = $(self.tableSelector).jqGrid('getGridParam', 'url') + "?";
                                var postData = $(self.tableSelector).jqGrid('getGridParam', 'postData');		              	
		
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
    configUrl = "${url}";
    $.ajax(configUrl + "&Operation=loadConfig").done(function(data) {
        config = data;
        grid = JQGridView.init("table#${tableId}", "div#${tableId}Pager", config);
    });
});

</script>

<div id="treeBox">
  <div id="tree3"></div>
</div>

<div id="gridBox">
	<table id="${tableId}"></table>
	<div id="${tableId}Pager"></div>
	<input id="exportButton" type="button" value="export data"/>
	<div id="dialog-form" title="Export data">
		<form>
		<fieldset>
	            <label >File type</label><br>
	            <input type="radio" name="viewType" value="EXCEL" checked>Excel<br>
	            <input type="radio" name="viewType" value="SPSS">Spss<br> 
	            <input type="radio" name="viewType" value="CSV">Csv<br> 
	            <label>Export option</label><br>
	            <input type="radio" name="exportSelection" value="ALL" checked>All<br>
	            <input type="radio" name="exportSelection" value="GRID">Grid<br> 
		</fieldset>
		</form>
	</div>
</div>
<!--
  <div>Selected keys: <span id="echoSelection3">-</span></div>
  <div>Selected root keys: <span id="echoSelectionRootKeys3">-</span></div>
  <div>Selected root nodes: <span id="echoSelectionRoots3">-</span></div>
-->
