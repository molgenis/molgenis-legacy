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
        this.createDialog();
        
        return JQGridView;
    },
    
    getGrid: function() {
    	return $(this.tableSelector);
    },
    
    changeColumns: function(columnModel) {
    	var self = JQGridView;
		this.config.colModel = columnModel;

		var names = new Array();
		$.each(columnModel, function(index, value) {
			names.push(value.name);
		});
		this.config.colNames = names;

		this.config.postData = {colNames:names};
    	$(this.tableSelector).jqGrid('GridUnload');
    	this.grid = this.createJQGrid();
    },
    
    createJQGrid : function() {
    	return jQuery(this.tableSelector).jqGrid(this.config)
            .jqGrid('navGrid', this.pagerSelector,
                {del:false,add:false,edit:false},{},{},{},{multipleSearch:true} // search options
            );
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
		
		              	var myUrl = $(self.tableSelector).jqGrid('getGridParam', 'url');
						myUrl += "&" +$.param($(self.tableSelector).jqGrid('getGridParam', 'postData'));		              	

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


function createTree() {
	$("#tree3").dynatree({
		checkbox: true,
		selectMode: 3,
		children: config.colModel,
		onSelect: function(select, node) {
			// Get a list of all selected nodes, and convert to a key array:
			var selectedColModel = new Array();        
			var selectedColumns = node.tree.getSelectedNodes();
			for(i = 0; i < selectedColumns.length; ++i) {
				var x = selectedColumns[i].data;
				if(!x.isFolder) {
					selectedColModel.push(x);
				}
			}
			grid.changeColumns(selectedColModel);        
		},
		onDblClick: function(node, event) {
			node.toggleSelect();
		},
		onKeydown: function(node, event) {
			if( event.which == 32 ) {
			node.toggleSelect();
			return false;
			}
		},
		// The following options are only required, if we have more than one tree on one page:
	//        initId: "treeData",
		cookieId: "dynatree-Cb3",
		idPrefix: "dynatree-Cb3-"
	});
}


$(document).ready(function() {
    configUrl = "${url}";
    $.ajax(configUrl + "&Operation=loadConfig").done(function(data) {
        config = data;
        grid = JQGridView.init("table#${tableId}", "#${tableId}Pager", config);

		createTree();

		
    });

	$('#exportButton').click(function() {
		$( "#dialog-form" ).dialog('open');
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
	            <input type="radio" name="exportSelection" value="ALL" checked>All rows<br>
	            <input type="radio" name="exportSelection" value="GRID">Visable rows<br> 
		</fieldset>
		</form>
	</div>
</div>