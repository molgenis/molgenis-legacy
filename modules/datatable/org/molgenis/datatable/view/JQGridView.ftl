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
    tableId : null,
    pagerId : null,
    config : null,
    tree : null,
    colModel : null, 
    
    columnPage : 1,				//current column Page
    columnPagerSize : 5,		//current columnPager size
    columnPageEnabled : true,
    
    oldColNames : null,
    oldColModel : null,
    
    treeColModel : new Array(),
    selectedColModel: new Array(),
    numOfSelectedNodes : 0,
    
    init: function(tableId, pagerId, config) {
    	var self = this;
    
        this.tableId = tableId;
        this.pagerId = pagerId;
        this.config = config;
        this.colModel = this.config.colModel;
        
        this.numOfSelectedNodes = this.config.colModel.length;
        
        // Deep copy
        
        $.each(this.config.colModel, function(index, value) {
        	self.treeColModel.push($.extend(true, {}, value));
        	self.selectedColModel.push($.extend(true, {}, value));
        });
        
        
        this.sliceColumns();
        
        this.grid = this.createJQGrid(null);
        this.createDialog();

		//load & create Tree
	    $.getJSON(configUrl + "&Operation=LOAD_TREE")   
	    .done(function(data) { 
	    	 self.tree = self.createTree(data);   
	    });
        this.restoreSliceColumns();
        		
        return JQGridView;
    },
    
    sliceColumns : function() {
		if(this.columnPageEnabled) {
			this.oldColNames = this.config.colNames;
			this.oldColModel = this.config.colModel;
			
			//reset columnPage to within selection
			this.maxPage = Math.floor( this.numOfSelectedNodes / this.columnPagerSize);
			if( (this.numOfSelectedNodes % this.columnPagerSize) > 0) this.maxPage = this.maxPage + 1;
			
			this.columnPage = Math.min(this.columnPage, this.maxPage);
			
			//offset
			begin = (this.columnPage - 1) * this.columnPagerSize;
			end = begin + this.columnPagerSize;
			
			columnNames = new Array();
			colCount = 0;
			for(i = 0; i < this.config.colModel.length; ++i) {
				if(!this.config.colModel[i].hidden) {
					if(colCount >= begin && colCount < end) {
						columnNames.push(this.config.colModel[i].name);				
					} else {
						this.config.colModel[i].hidden = true;
					}
					++colCount;					
				} else {
					this.config.colModel[i].hidden = true;
				}
			}	
			this.config.postData.colNames = columnNames;		
			//this.config.colNames = this.config.colNames.slice(begin, end);
			//this.config.colModel = this.config.colModel.slice(begin, end);
		}    
    },
    
    restoreSliceColumns : function() {
		if(this.columnPageEnabled) {
			//this.config.colNames = this.oldColNames;
			//this.config.colModel = this.oldColModel;
		}     
    }, 
    
    getGrid: function() {
    	return $("table#"+this.tableId);
    },
    
    changeColumns: function(columnModel) {
    	var self = this;
		
		//null == no change in columnModel
		if(columnModel == null) {
			columnModel = self.selectedColModel;
		}
		else {
			//some selection
			if(columnModel.length > 0) {
				self.selectedColModel = columnModel;
			}
			//empty selection; reset to view all columns
			else {
				self.selectedColModel = this.colModel;
			}
			this.numOfSelectedNodes = self.selectedColModel.length;
		}

		//add all columnNames
		var names = new Array();
		$.each(this.config.colModel, function(index, value) {
			names.push(value.name);
		});
		this.config.colNames = names;

		var columnNames = new Array();
		gridColModel = this.grid.getGridParam("colModel");
		
		//reset all hidden (outside column paging)
    	for(i = 0; i < gridColModel.length; ++i) {
    		colName = gridColModel[i].name;
    		
    		if(columnModel != null && columnModel.length > 0) 
    		{
    			hidden = true;
	    		for(j = 0; j < columnModel.length; ++j) {
	    			if(colName == columnModel[j].name) {
	    				columnNames.push(colName);
	    				hidden = false;
	    				break;
	    			}
    			}
    		}
    		else {
    			columnNames.push(colName);
    			hidden = false;
    		}
    		
    		gridColModel[i].hidden = hidden;
    	}

    	this.config.colModel = gridColModel; 
    	this.config.postData.colNames = columnNames;

		this.sliceColumns();		
		
		filters = this.grid.getGridParam("postData").filters;
		
    	$("table#"+this.tableId).jqGrid('GridUnload');

    	this.grid = this.createJQGrid(filters);
    	
    	this.restoreSliceColumns();
    },
    
    createJQGrid : function(filters) {
    	var self = this;
    	
		if(filters != null) {
			this.config.postData.filters = filters; 
		}
    	
    	grid = jQuery("table#"+this.tableId).jqGrid(this.config)
            .jqGrid('navGrid', "#"+this.pagerId,
            	this.config.settings,{},{},{},{multipleSearch:true, multipleGroup:true, showQuery: true} // search options
            ).jqGrid('gridResize');
        //is not correct (will not work with two grids!)
        if(this.columnPageEnabled) {
        	firstButton = $("<input id='firstColButton' type='button' value='|< Columns' style='height:20px;font-size:-3'/>")
        	prevButton = $("<input id='prevColButton' type='button' value='< Columns' style='height:20px;font-size:-3'/>");
        	nextButton = $("<input id='nextColButton' type='button' value='Column >' style='height:20px;font-size:-3'/>");
        	lastButton = $("<input id='lastColButton' type='button' value='Columns >|' style='height:20px;font-size:-3'/>")        	
        	
        	colPager = $("<div id='columnPager'/>");
        	pageInput = $("<input id='colPageNr' type='text' size='3'>");
        	
        	$(pageInput).attr('value', this.columnPage);  

			maxPage = Math.floor( this.numOfSelectedNodes / this.columnPagerSize);
			if( (this.numOfSelectedNodes % this.columnPagerSize) > 0) maxPage = maxPage + 1;
        	
        	if(this.columnPage >= maxPage) {
        		nextButton.attr("disabled","disabled");
        		lastButton.attr("disabled","disabled");
        	}        	
        	if(this.columnPage <= 1) {
        		prevButton.attr("disabled","disabled");
        		firstButton.attr("disabled","disabled");
        	}        	
        	
        	$(firstButton).click(function() {
        		self.setColumnPageIndex(1);
        	});
        	
        	$(prevButton).click(function() {
				self.columnPagerLeft();
        	});
        
        	$(nextButton).click(function() {
        		self.columnPagerRight();
        	});
        	
        	$(lastButton).click(function() {
        		self.setColumnPageIndex(maxPage);
        	});
        	
			colPager.append(firstButton);
        	colPager.append(prevButton);
        	colPager.append("Page ");
        	colPager.append(pageInput);
        	colPager.append(" Of " + maxPage);
        	colPager.append(nextButton);
        	colPager.append(lastButton);
        	
        	toolbar = $("#t_"+this.tableId); 
        	toolbar.append(colPager);

    	}
        return grid;
	},

	setColumnPageIndex : function(columnPagerIndex) {
		this.columnPage = columnPagerIndex;
		this.changeColumns(null);
	},
	
	columnPagerLeft : function () {
		var self = this;
		this.columnPage--;
		this.changeColumns(null);
	},	
    
    columnPagerRight : function () {
		var self = this;
		this.columnPage++;
		this.changeColumns(null);
	},	
    
    getColumnNames : function(colModel) {
    	result = new Array();
    	$.each(colModel, function(index, value) {
    		result.push(value.name);
    	});
    	return result;
    },
    
    createDialog : function() {
    	var self = this;
    	$("#"+this.tableId+"_dialog-form" ).dialog({
		    autoOpen: false,
		    height: 300,
		    width: 350,
		    modal: true,
		    buttons: {
		            "Export": function() {
		            	var viewType = $("input[name='viewType']:checked").val();
		            	var exportSelection = $("input[name='exportSelection']:checked").val();
		
		              	var myUrl = $("table#"+self.tableId).jqGrid('getGridParam', 'url');
						myUrl += "&" +$.param($("table#"+self.tableId).jqGrid('getGridParam', 'postData'));		
						              	

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
	},
	
	createTree : function(nodes) {
		var self = this;
		return $("#"+this.tableId+"_tree").dynatree({
			checkbox: true,
			selectMode: 3,
			children: nodes,
			onSelect: function(select, node) {
				// Get a list of all selected nodes, and convert to a key array:
				self.selectedColModel = new Array();        
				var selectedColumns = node.tree.getSelectedNodes();
				var tableNodes = new Array();
				for(i = 0; i < selectedColumns.length; ++i) {
					var treeNode = selectedColumns[i].data;
					
					//handle branch-nodes
					if(treeNode.isFolder) {
						tableNodes.push(treeNode.title);
					}					

					//handle leaf-nodes
					if(!treeNode.isFolder) {
						colModelNode = $.grep(self.colModel, function(item){
      							return item.path == treeNode.path;
							});
						self.selectedColModel.push(colModelNode[0]);
					}
				}
				
				self.config.postData.tableNames = tableNodes;
				
				self.changeColumns(self.selectedColModel);        
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
			cookieId: "dynatree-"+this.tableId+"",
			idPrefix: "dynatree-"+this.tableId+"-"
		});
	}
}





$(document).ready(function() {
    configUrl = "${url}";
    
    //load JQGrid configuration and creates grid
    $.ajax(configUrl + "&Operation=LOAD_CONFIG").done(function(data) {
        config = data;
        grid = JQGridView.init("${tableId}", "${tableId}_pager", config);
    });
	$('#${tableId}_exportButton').click(function() {
		$( "#${tableId}_dialog-form" ).dialog('open');
	});
	
});

</script>

<div id="${tableId}_treeBox">
  <div id="${tableId}_tree"></div>
</div>

<div id="${tableId}_gridBox">
	<table id="${tableId}"></table>
	<div id="${tableId}_pager"></div>
	<input id="${tableId}_exportButton" type="button" value="export data"/>
	<div id="${tableId}_dialog-form" title="Export data">
		<form>
		<fieldset>
	            <label >File type</label><br>
	            <input type="radio" name="viewType" value="EXCEL" checked>Excel<br>
	            <input type="radio" name="viewType" value="SPSS">Spss<br> 
	            <input type="radio" name="viewType" value="CSV">Csv<br> 
	            <label>Export option</label><br>
	            <input type="radio" name="exportSelection" value="ALL" checked>All rows<br>
	            <input type="radio" name="exportSelection" value="GRID">Visible rows<br> 
		</fieldset>
		</form>
	</div>
</div>
