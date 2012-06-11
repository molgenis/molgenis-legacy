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
    colModel : null,
    colNames : null, 
    sortColumn : 'id',
    caption : "dataTable",
    jqGrid : null,
    backendUrl : null,
    viewFactory : null,
    
    init: function(config) {
    	//required parameters
    	this.backendUrl = config.backendUrl;
        this.tableSelector = config.tableSelector;
        this.colModel = config.colModel;
        this.colNames = this.getColumnNames(this.colModel);
        this.viewFactoryClassName = config.viewFactoryClassName;
        
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
    	var grid = jQuery(this.tableSelector).jqGrid({
            url: this.backendUrl,
            datatype: "json",
            jsonReader: { repeatitems: false },
            postData : 
            	{
            	__show: 'jqGrid',
            	viewType : 'JQ_GRID',
            	colNames : $.toJSON(this.colNames), 
        		colModel: $.toJSON(this.colModel), 
        		viewFactoryClassName : this.viewFactoryClassName,
        		caption: this.caption
        		},
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
	var $self = this;
    this.myColModel = 
        [
            <#list columns as col>	
            {title:'${col.name}', key:'${col.name}', name:'${col.name}',index:'${col.name}', width:150, searchrules:{required:${(!col.nillable)?string}}, table:'Country'}<#if col_has_next>,</#if>
            </#list>
        ];
   
	this.mySortColumn = '${sortName}';
	
    this.myGrid = JQGridView.init(
    	{	
    		backendUrl : "${backendUrl}",     
    		viewFactoryClassName: "${viewFactoryClassName}",
    		tableSelector : "#${tableId}", 
    		colModel: this.myColModel, sortColumn: this.mySortColumn
		});
    $("#exportButton").click(function() {
        $( "#dialog-form" ).dialog( "open" );    	
    });
    
    $("#testChangeColumns").click(function() {
    	$self.myColModel.pop();
    	$self.myGrid.changeColumns($self.myColModel);	
	});
	
	$("#tree3").dynatree({
      checkbox: true,
      selectMode: 3,
      children: this.myColModel,
      onSelect: function(select, node) {
        // Get a list of all selected nodes, and convert to a key array:
        
        var colModel = new Array();        
        var selectedColumns = node.tree.getSelectedNodes();
        for(i = 0; i < selectedColumns.length; ++i) {
        	var x = selectedColumns[i].data;
        	colModel.push(x);
        }
        $self.myGrid.changeColumns(colModel);        
        
        var selKeys = $.map(node.tree.getSelectedNodes(), function(node){
          return node.data;
        });
        $("#echoSelection3").text(selKeys.join(", "));

        // Get a list of all selected TOP nodes
        var selRootNodes = node.tree.getSelectedNodes(true);
        // ... and convert to a key array:
        var selRootKeys = $.map(selRootNodes, function(node){
          return node.data.key;
        });
        $("#echoSelectionRootKeys3").text(selRootKeys.join(", "));
        $("#echoSelectionRoots3").text(selRootNodes.join(", "));
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
