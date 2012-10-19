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
  
   
$.fn.extend({
    molgenisGrid: function(options) { 
        return this.each(function(){
           	
           	var container = $(this);
			var columnPager = $('.columnpager', container).detach();
           	var grid; //The jqGrid
           	var currentPage = 1;
           	var maxColPage;
           	
       		reloadGrid = function(operation, keepCurrentPageNr) {
       			$.ajax(options.url + '&Operation=' + operation).done(function(config) {
       				
       				//Unload the grid if it already exists
       				if (grid) {
       					grid.jqGrid('GridUnload');
       				}
       				
       				//For the column type requests remember the current page otherwise it wil be reset to 1
       				//But remember it only once cause other type of requests can follow
       				config.loadComplete = function(data) {
       					currentPage = data.page;
       				};
       				
       				config.beforeRequest = function(){
       		
       					if (keepCurrentPageNr) {
       						getPostData().page = currentPage;
       						keepCurrentPageNr = false;
     					}
     					
       					return true;
       				};
    				
       				//Create new jqGrid
       				var deleteRecordConfig = {
       					onclickSubmit : function(param) {
							config.postData.Operation = "DELETE_RECORD";
							
							var selRowId = grid.jqGrid ('getGridParam', 'selrow');
							var rowData = grid.jqGrid('getRowData', selRowId);
							
							for (col in rowData) {
								config.postData[col] = rowData[col];
							}
							
							return config.postData;
						},
						afterComplete : function (response, postdata, formid) {
							delete config.postdata.Operation;
						} 
       				}
       				
       				var editRecordConfig = {
       					onclickSubmit : function(param) {
							config.postData.Operation = "EDIT_RECORD";
							return config.postData;
						},
						afterComplete : function (response, postdata, formid) {
							delete config.postData.Operation;
						} 
       				}
       				
       				var addRecordConfig = {
       					onclickSubmit : function(param) {
							config.postData.Operation = "ADD_RECORD";
							return config.postData;
						},
						afterComplete : function (response, postdata, formid) {
							delete config.postData.Operation;
						} 
       				}
       				
       				grid = $('#' + options.tableId, container).jqGrid(config).jqGrid('navGrid', config.pager, config.settings, editRecordConfig, addRecordConfig, deleteRecordConfig, config.searchOptions);
       			
       				addColumnRemoveButtons(config);
       				
       				grid.bind('jqGridAddEditBeforeShowForm', function(e, form, oper) {
       					
					});
					
       				//Put the columnpager in the grid toolbar
       				var toolbar = $('#t_' + options.tableId);
       				toolbar.css({"height" : "30px", "width" : "100%"});//Style of the toolbar where all paging widges are
        			columnPager.appendTo(toolbar);
					
					if (config.hiddenColumns.length == 0) {
						$('#hiddenColumnsEditor').hide();
					
					} else {
						$('#hiddenColumnsEditor').show();
						
						//Add hidden columns to the dropdown
						$('#hiddenColumnsDropdown').find('option').remove();//Remove all options
					
						//Add hidden columns as options
						$.each(config.hiddenColumns, function(index, column) {
							$('#hiddenColumnsDropdown').append(new Option(column));
						});
					
						//Button to add a hidden column (next to dropdown)
						$('#addColumn').button({icons: { primary: 'ui-icon-circle-plus' },text: false, label: 'Show column'})
										.click(function (e) {
											var selectedColumn = $('#hiddenColumnsDropdown').val();
											reloadGrid('SHOW_COLUMN&column=' + selectedColumn, true);
											return false;
										});
					}
					
        			//Add the columnpaging info
        			var start = config.colOffset + 1;
        			var end = start + config.colLimit - 1;
        			if (end > config.totalColumnCount) 
        			{
        				end = config.totalColumnCount;
        			}
        			
					$('.ui-columnpaging-info', container).html('Column ' + start + '-' + end + ' of ' + config.totalColumnCount);
					
					maxColPage = Math.floor(config.totalColumnCount / config.colLimit);
					if ((config.totalColumnCount % config.colLimit) > 0) {
						maxColPage++;
					}
					$('.total-column-pages', container).html(maxColPage);
					
					//Calculate the current columnpage
					var colPage = Math.floor(end / config.colLimit);
					if ((end % config.colLimit) > 0) {
						colPage++;
					}
					$('.colpager-input', container).val(colPage);
					
					//Update the buttons
					$('.first_columnpager', container).removeClass('ui-state-disabled'); 
					$('.prev_columnpager', container).removeClass('ui-state-disabled'); 
					$('.next_columnpager', container).removeClass('ui-state-disabled'); 
					$('.last_columnpager', container).removeClass('ui-state-disabled'); 
					
					if (start <= 1) {
						$('.prev_columnpager', container).addClass('ui-state-disabled');
						$('.first_columnpager', container).addClass('ui-state-disabled');
					}
					if (end >= config.totalColumnCount) {
						$('.next_columnpager', container).addClass('ui-state-disabled');
						$('.last_columnpager', container).addClass('ui-state-disabled');
					}	
       			});	
       		} 
       		
       		addColumnRemoveButtons = function(config) {
       			grid.closest("div.ui-jqgrid-view")
       				.find("div.ui-jqgrid-hdiv table.ui-jqgrid-htable tr.ui-jqgrid-labels > th.ui-th-column > div.ui-jqgrid-sortable")
    				.each(function (index) {
    				
    					if (!config.firstColumnFixed || index > 0) {
        					$('<button>').css({"width" : "16px", "height": "16px", "position" : "absolute", "top" : "50%", "margin-top" : "-8px","left" : "100%", "margin-left" : "-18px" }).appendTo(this).button({
            					icons: { primary: "ui-icon-circle-close" },
            					text: false,
            					label: 'Hide column'
        					}).click(function (e) {
        			 			var idPrefix = "jqgh_" + grid[0].id + "_";
                				var thId = $(e.target).closest('div.ui-jqgrid-sortable')[0].id;
                		
            					// thId will be like "jqgh_tablename_column"
            					if (thId.substr(0, idPrefix.length) === idPrefix) {
            						var column = thId.substr(idPrefix.length);
                					reloadGrid('HIDE_COLUMN&column=' + column, true);
                			
                					return false;
            					}

       						});
       					}
    			});
       				
       		}
       		
       		getPostData = function() {
       			return $('#' + options.tableId, container).getGridParam('postData');
       		}
       		
       		//Handle first column range click
       		$('.first_columnpager', container).live('click',function() {
       			if (!$('.first_columnpager', container).hasClass('ui-state-disabled')) {
					reloadGrid('SET_COLUMN_PAGE&colPage=1', true);
				}
			});
			
       		//Handle next column range click
       		$('.next_columnpager', container).live('click',function() {
       			if (!$('.next_columnpager', container).hasClass('ui-state-disabled')) {
					reloadGrid('NEXT_COLUMNS', true);
				}
			});
				
			//Handle column inputbox
       		$('.colpager-input', container).live('change',function(event) {
       			reloadGrid('SET_COLUMN_PAGE&colPage=' + $(this).val(), true);
			});
			
			//Handle prev column range click
       		$('.prev_columnpager', container).live('click',function() {
       			if (!$('.prev_columnpager', container).hasClass('ui-state-disabled')) {
					reloadGrid('PREVIOUS_COLUMNS', true);
				}
			});
			
			//Handle last column range click
       		$('.last_columnpager', container).live('click',function() {
       			if (!$('.last_columnpager', container).hasClass('ui-state-disabled')) {
					reloadGrid('SET_COLUMN_PAGE&colPage=' + maxColPage, true);
				}
			});
			
			
			//Add or remove hover class from all elements that have the hovarable class
			$('.hoverable', container).live({
        		mouseenter: function() {
        			if (!$(this).hasClass('ui-state-disabled')) {//Only for enabled elements
        				$(this).addClass('ui-state-hover');
        			}
           		},
        		mouseleave: function() {
					$(this).removeClass('ui-state-hover');
           		}
       		});	
			
       		reloadGrid('LOAD_CONFIG', false);
        });
        
        return this;
    }
});


// On first load do:
$(document).ready(function() {
	$('#${tableId}_gridBox').molgenisGrid({url:'${url}', tableId:'${tableId}'});
});

</script>

<style type="text/css">
    #${tableId} input:hover{
        background-color:#65A5D1;
    }
</style>

<div id="${tableId}_gridBox">
    
    <div class="columnpager" style="width:100%;">
    	<table class="ui-pg-table" cellspacing="0" cellpadding="0" border="0" role="row" style="width:100%;table-layout:fixed;height:100%;">
			<tbody>
				<tr>
    				<td align="left">
    					<div id="hiddenColumnsEditor">
    						<select id="hiddenColumnsDropdown" role="listbox" class="ui-pg-selbox ui-widget-content ui-corner-all" style="width:200px;float:left;"></select>
							<button id="addColumn" style="float:left;height:18px;margin-left:4px"></button>
						</div>
    				</td>
    				<td align="center" style="width:200px">
        				<table class="ui-pg-table" cellspacing="0" cellpadding="0" border="0" style="table-layout:auto; width:100%">
            				<tbody>
                				<tr>
                					<td class="first_columnpager hoverable ui-pg-button ui-corner-all" style="cursor: default;">
										<span class="ui-icon ui-icon-seek-first"></span>
									</td>
                    				<td class="prev_columnpager hoverable ui-pg-button ui-corner-all" style="cursor: default;">
                        				<span class="ui-icon ui-icon-seek-prev"></span>
                    				</td>
                    				<td dir="ltr" style="font-size:10px" align="center">
										Columns <input class="colpager-input ui-pg-input" type="text" role="textbox" value="1" maxlength="7" size="1" /> of <span class="total-column-pages"></span>
									</td>
                    				<td class="next_columnpager hoverable ui-pg-button ui-corner-all" style="cursor: default;">
                        				<span class="ui-icon ui-icon-seek-next"></span>
                    				</td>
                    				<td class="last_columnpager hoverable ui-pg-button ui-corner-all" style="cursor: default;">
										<span class="ui-icon ui-icon-seek-end"></span>
									</td>
                				</tr>
            				</tbody>
        				</table>
        			</td>
        			<td align="right">
        				<div class="ui-columnpaging-info" style="text-align:right" dir="ltr"></div>
        			</td>
        		</tr>
       		</tbody>
 		</table>       	       
    </div>
   
  
    <table id="${tableId}"></table>
   
    <div id="${tableId}_pager"></div>
</div>