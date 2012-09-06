<script src="jqGrid/grid.locale-en.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.src.js" type="text/javascript"></script>
<script src="jqGrid/jquery.json-2.3.min.js" type="text/javascript"></script>
<script src="jqGrid/grid.common.js" type="text/javascript"></script>
<script src="jqGrid/grid.formedit.js" type="text/javascript"></script>
<script src="jqGrid/jqDnR.js" type="text/javascript"></script>
<script src="jqGrid/jqModal.js" type="text/javascript"></script>
<script src="jqGrid/jqGridCustomjavascript.js" type="text/javascript"></script>


<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.dialog.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.datepicker.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="jquery/development-bundle/themes/smoothness/jquery-ui-1.8.7.custom.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.multiselect.css">
<link rel="stylesheet" type="text/css" media="screen" href="res/css/editableJQGrid.css">

<link href="dynatree-1.2.0/src/skin/ui.dynatree.css" rel="stylesheet" type="text/css" id="skinSheet">
<script src="dynatree-1.2.0/src/jquery.dynatree.js" type="text/javascript"></script>

<script type="text/javascript">
//TODO: place in JS file after dev!

// Main object, wraps JQgrid and stores/manipulates many state variables
var JQGridView = {
    tableId : null,
    pagerId : null,
    config : null,
    tree : null,
    editurl : null,
    colModel : null, 
    
    columnPage : 1,				//current column Page
    columnPagerSize : 6,		//current columnPager size
    columnPageEnabled : true,
        
    prevColModel : null,		// Cache to speed up column paging: don't create new colmodel if not necessary
    numOfSelectedNodes : 0,		// # nodes selected in tree
    
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
        editurl = config.url;
        this.numOfSelectedNodes = this.config.colModel.length;
        
        // Deep copy
        
        $.each(this.config.colModel, function(index, value) {
        	self.treeColModel.push($.extend(true, {}, value));
        });
        
       
        this.showVisibleColumns();
        
        this.grid = this.createJQGrid(null);
        this.createDialog();
      
		//load & create Tree
	    $.getJSON(configUrl + "&Operation=LOAD_TREE").done(function(data) { 
	    	 self.tree = self.createTree(data);   
	    });

        return JQGridView;
    },
    
    // Show one page of columns and hide all other data.
    showVisibleColumns : function() {
		if(this.columnPageEnabled) {
		
			this.oldColNames = this.config.colNames;
			this.oldColModel = this.config.colModel;
			
			//reset columnPage to within selection
			this.maxPage = Math.floor( this.numOfSelectedNodes / this.columnPagerSize);
			if( (this.numOfSelectedNodes % this.columnPagerSize) > 0) this.maxPage = this.maxPage + 1;
			
			this.columnPage = Math.min(this.columnPage, this.maxPage);
			
			//offset
			begin = (this.columnPage - 1) * this.columnPagerSize + 1;
			end = begin + this.columnPagerSize;
			
			columnNames = new Array();
			colCount = 0;
			// Build a column model of which columns to display, excluding hidden columns.
			this.config.colModel[0].hidden = false;
			columnNames.push(this.config.colModel[0].name);
			
			for(i = 0; i < this.config.colModel.length; ++i) {
				if(!this.config.colModel[i].hidden) {
					if(colCount >= begin && colCount < end) {
						columnNames.push(this.config.colModel[i].name);	
					} else if(i != 0){
						this.config.colModel[i].hidden = true;
					}
					++colCount;					
				}
			}
			
			this.config.postData.colNames = columnNames;
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
		
		var selectedTreeNodeNames = new Array();
		$.each(columnModel, function(index, value) {
			selectedTreeNodeNames.push(value.name);
		});
		this.config.postData.treeSelectColNames = selectedTreeNodeNames;

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

		this.showVisibleColumns();		
		
		filters = this.grid.getGridParam("postData").filters;
		
    	$("table#"+this.tableId).jqGrid('GridUnload');

    	this.grid = this.createJQGrid(filters);
    	
    },
    
    createJQGrid : function(filters) {
    	var self = this; 	
    	
		if(filters != null) { // If condition may be redundant?
			this.config.postData.filters = filters; 
		}
    	
    	if(self.grid != null){
    		
    		currentPage = self.grid.jqGrid('getGridParam', 'page');
    		this.config.page = currentPage;
    	}
        
    	
    	grid = jQuery("table#"+this.tableId).jqGrid(this.config)
            .jqGrid('navGrid', "#"+this.pagerId,
            	this.config.settings,
            	{
            		onclickSubmit : function(param) {
						self.config.postData.Operation = "EDIT_RECORD";
						return self.config.postData;
					},
					afterComplete : function (response, postdata, formid) {
						delete self.config.postData["Operation"];
					} 
            	},
            	{//ADD RECORD
            	},
            	{
            		onclickSubmit : function(param) {
						self.config.postData.Operation = "DELETE_RECORD";
						self.config.postData.SelectedRow = self.grid.jqGrid('getGridParam', 'selrow');
						return self.config.postData;
					},
					afterComplete : function (response, postdata, formid) {
						delete self.config.postData["Operation"];
						delete self.config.postData["SelectedRow"];
					} 
					 
            	},{multipleSearch:true, multipleGroup:true, showQuery: true} // search options
            ).jqGrid('gridResize');
 
        if(this.columnPageEnabled) {

        	firstButton = $("<input id='firstColButton' type='button' value='|< Columns' style='height:20px;font-size:-3'/>")
        	prevButton = $("<input id='prevColButton' type='button' value='< Columns' style='height:20px;font-size:-3'/>");
        	nextButton = $("<input id='nextColButton' type='button' value='Column >' style='height:20px;font-size:-3'/>");
        	lastButton = $("<input id='lastColButton' type='button' value='Columns >|' style='height:20px;font-size:-3'/>")        	
        	
        	colPager = $("<div id='columnPager'/>");
        	pageInput = $("<input id='colPageNr' type='text' size='3'>");
        	
        	$(pageInput).attr('value', this.columnPage);  

			maxPage = Math.floor( this.numOfSelectedNodes / this.columnPagerSize);
			if( (this.numOfSelectedNodes % this.columnPagerSize) > 0) maxPage = maxPage + 2;

			// handle input of specific column page number
        	$(pageInput).change(function() {
        		value = parseInt($(this).val(), 10);
        		
        		
        		if(value - 1 > 0 && value - 1 < maxPage) {
        			$(this).attr('value', value);
        			self.setColumnPageIndex(value - 1);
        		} else {
        			if(value - 1 >= maxPage) {
        				$(this).attr('value', value);
        				self.setColumnPageIndex(maxPage - 1);
        			}
        			if(value - 1 <= 0) {
        				$(this).attr('value', value);
        				self.setColumnPageIndex(0);        			
        			}
        		}
        	});

        	// Enable/disable forwards and backwards buttons appropriately
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
        	
        	// construct GUI
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
    	
    	//Remove the default click function.
    	$('#add_test').unbind('click');
		
		//Add custom click event
		$('#add_test').click(function() {
			
			columnPage = self.columnPage;
			columnPagerSize = self.columnPagerSize;
			numOfSelectedNodes = self.numOfSelectedNodes;
			colModel = self.colModel;
			
			//Open the dialog after click
			$( "#dialog" ).dialog('open');
			
			//Remove the content of the dialog left from last click event
			$( "#dialog" ).empty();
			
			var colNames = $('#test').jqGrid('getGridParam','colNames');
			maxPage = Math.floor( numOfSelectedNodes / columnPagerSize);
			if( (numOfSelectedNodes % columnPagerSize) > 0) maxPage = maxPage + 1;
			this.columnPage = Math.min(this.columnPage, this.maxPage);
			
			//create the new table for adding values for different measurements
			addRecordTable = "<table id=\"addRecord\">";
			

 			var array;
 			var length = 0;
 			for(var index = 0; index < colModel.length; index++){
 			
 				if(colModel[index].edittype == "select"){
 					var optionString = colModel[index].editoptions.value;
 					var optionsHTML = "<option></option>";	
 					var options = optionString.split(";");
 					for(var i = 0; i < options.length; i++){
 						var nameAndValue = options[i].split(":");
 						optionsHTML += "<option>" + nameAndValue[1] + "</option>";
 					}
					addRecordTable += "<tr id=\"" + colModel[index].name + "\" style=\"display:none\"><td>" + colModel[index].name + 
 					"</td><td><select id=\""+ colModel[index].name +"_input\">"+ optionsHTML +"</select></td></tr>";
 				}else {
				
 					addRecordTable += "<tr id=\"" + colModel[index].name + "\" style=\"display:none\"><td>" + colModel[index].name + 
 					"</td><td><input id=\""+ colModel[index].name +"_input\" type=\"text\" ></input></td></tr>";
 				}		
 			}
 			
 			//close the table and add it to the dialog div
 			addRecordTable += "</table></br>";
			$('#dialog').append(addRecordTable);
			for(var index = 0; index < colModel.length; index++){
				if(colModel[index].datetype == "datetype"){
	
					$( "#"+colModel[index].name +"_input" ).datepicker({

					});
				
				}
			}
			
			//Only the first 7 rows are shown in the table.
			$('#addRecord tr:lt(7)').show();
			
			//Create a new div in which the next and previous buttons are added.
			navPage = "<div id=\"navPage\">";
			navPage += "<input id=\"prevPage\" type=\"button\" style=\"font-size:0.7em\" value=\"< previous page\"></input>";
			navPage += "<input id=\"nextPage\" type=\"button\" style=\"font-size:0.7em\" value=\"next page >\"></input>";			
			navPage += "</div>";
			
			
			$('#dialog').append(navPage);
			
			//Using jQuery UI Button
			$('#nextPage').button();
 			$('#prevPage').button();
 			
			if(columnPage==1){
 				$('#prevPage').hide();
 			}else{
 				$('#prevPage').show();
 			}
			
			//Add the submit and cancel buttons to the dialog
			controlDiv = "</br><div id=\"controlDiv\">";
			controlDiv += "<input id=\"submitAddRecord\" type=\"submit\" style=\"font-size:1.3em\" value=\"Submit\"></input>";
			controlDiv += "<input id=\"quitAddRecord\" type=\"button\" style=\"font-size:1.3em\" value=\"Cancel\"></input>";
			controlDiv += "</div>";
			$('#dialog').append(controlDiv);
			//Using jQuery UI Button
			$('#submitAddRecord').button();
			$('#quitAddRecord').button();
			
			
			//Set up the event for clicking previous button. 
 			$('#prevPage').click(function(){
 				
 				if(columnPage - 1 > 0){
		 			columnPage = columnPage - 1;
		 			beginningIndex = (columnPage - 1) *columnPagerSize + 1;
		 			endingIndex = (columnPage - 1) *columnPagerSize + 6;
		 			allRows = $('#addRecord tr');
		 			$(allRows).hide();
		 			$(allRows).eq(0).show();
		 			for(var index = beginningIndex; index <= endingIndex; index++){
		 				$(allRows).eq(index).show();
		 			}
		 			if(columnPage==(maxPage)){
		 		
						$('#nextPage').hide();
		 			}else{
		 				$('#nextPage').show();
		 			}
		 			if(columnPage==1){
						$('#prevPage').hide();
		 			}else{
		 				$('#prevPage').show();
		 			}
	 			}
 			});
			
			
			//Set up the event for clicking next button. 
 			$('#nextPage').click(function(){
 				
 				if(columnPage + 1 <= maxPage){
 					columnPage = columnPage + 1;
		 			
		 			beginningIndex = (columnPage - 1) *columnPagerSize + 1;
		 			endingIndex = (columnPage - 1) *columnPagerSize + 6;
		 			allRows = $('#addRecord tr');
		 			$(allRows).hide();
		 			$(allRows).eq(0).hide();
		 			for(var index = beginningIndex; index <= endingIndex; index++){
		 				$(allRows).eq(index).show();
		 			}
		 			if(columnPage==(maxPage)){
						$('#nextPage').hide();
		 			}else{
		 				$('#nextPage').show();
		 			}
		 			if(columnPage==1){
						$('#prevPage').hide();
		 			}else{
		 				$('#prevPage').show();
		 			}
	 			}
 			});

 			grid = self.grid;
 			
 			//Add click event to submit button
 			$('#submitAddRecord').click(function(){
 				
 				template = {};
 				numberOfColumns = 0;
 				//get all the values that are typed in the dialog
 				for(var index = 0; index < colNames.length; index++){
 					
 					if($("#" + colNames[index] + "_input").val() != ""){
 						template[colNames[index]] = $("#" + colNames[index] + "_input").val();
 						numberOfColumns++;
 					}
 				}
 				
 				//Get URL
 				var myUrl = $("table#"+self.tableId).jqGrid('getGridParam', 'url');
 				
 				//The first column is always observationTarget.
 				targetID = template[colNames[0]];
 				
 				if(targetID === "" || !targetID){
 					alert("The targetID needs to fill out!");
 				}else if(numberOfColumns == 1){
 					alert("Please fill out one column at least!");
 				}else{
 					//Delete the observationTarget
 					delete template[colNames[0]];
 					//Put the values in the variables attached to URL
					myUrl += "&targetID=" + targetID + "&data=" + JSON.stringify(template);
	                //Calling ajax and pass this value back to the server
	                $.ajax(myUrl + "&Operation=ADD_RECORD").done(function(status) {
				       	//If the value addition is successful, this value is inserted in 
				       	//the jqGrid table as well.
				       	if(status["success"] == true){
				       		template[colNames[0]] = targetID;
				        	grid.addRowData(grid.getGridParam('records') + 1, template, "first");	
				        	$('#dialog').dialog('close');
				        }
				        //Print out the message.
				        alert(status["message"]);
				    });
 				}            
 			});
 			
 			//Set up event for quit dialog button. If the quit button is clicked, another confirmation dialog
 			//pops up, therefore it prevents people from mis-clicking the quit button and losing input.
 			
 		
 			$('#quitAddRecord').click(function(){
 				confirmDialog = "<div id=\"confirmDialog\" title=\"Confirmation\">";
 				confirmDialog += "Are you sure you want to quit?</br>";
 				confirmDialog += "<input id=\"confirmButton\" type=\"button\" style=\"font-size:0.8em\" value=\"Confirm\"></input>";
 				confirmDialog += "<input id=\"cancelButton\" type=\"button\" style=\"font-size:0.8em\" value=\"Cancel\"></input>";
 				confirmDialog += "<div>";
 				$('#dialog').append(confirmDialog);
 				$('#confirmButton').button();
				$('#cancelButton').button();
 				$('#confirmDialog').dialog();
 				$('#confirmButton').click(function(){
 					$('#dialog').dialog('close');
 					$('#confirmDialog').remove();
 				});
 				$('#cancelButton').click(function(){$('#confirmDialog').remove();});
 			});
 			
    	});
    	
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
	
						var exportColumnSelection = $("input[name='exportColumnSelection']:checked").val();

		                //e.preventDefault();  //stop the browser from following
		                window.location.href = myUrl + "&viewType=" + viewType + "&exportSelection=" + exportSelection + "&exportColumnSelection=" + exportColumnSelection;
		            },
		            Cancel: function() {
		                $( this ).dialog( "close" );
		            }
		    },
		    close: function() {
		    }
		});
		
	},
	
	// Build the column selection tree
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
			// escape (?)
			onKeydown: function(node, event) {
				if( event.which == 32 ) {
					node.toggleSelect();
					return false;
				}
			},
		// The following options are only required if we have more than one tree on one page:
		//        initId: "treeData",
			cookieId: "dynatree-"+this.tableId+"",
			idPrefix: "dynatree-"+this.tableId+"-"
		});
	}
}

// On first load do:
$(document).ready(function() {
    configUrl = "${url}";
    $("#dialog").dialog({ autoOpen: false });
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



<div id="dialog" title="Add record" style="width:200px; height:200px;font-size:12px">
	

</div><!-- End demo -->


<div id="${tableId}_treeBox">
  <div id="${tableId}_tree"></div>
</div>

<style type="text/css">
	#${tableId} input:hover{
		background-color:#65A5D1;
	}
</style>

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
	    </fieldset>
	    <fieldset>
	            <label>Rows</label><br>
	            <input type="radio" name="exportSelection" value="ALL">All rows<br>
	            <input type="radio" name="exportSelection" value="GRID" checked>Visible rows<br> 
		</fieldset>
	    <fieldset>
	            <label>Columns</label><br>
	            <input type="radio" name="exportColumnSelection" value="ALL_COLUMNS">All Columns<br>
	            <input type="radio" name="exportColumnSelection" value="SELECTED_COLUMNS" checked>Selected<br>
	            <input type="radio" name="exportColumnSelection" value="GRID_COLUMNS">Visible Columns<br>
		</fieldset>
		</form>
	</div>
</div>
