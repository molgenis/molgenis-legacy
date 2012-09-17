Ext.onReady(function(){
    Ext.BLANK_IMAGE_URL = 'res/scripts/custom/extjs/resources/images/default/s.gif';
    Ext.QuickTips.init();
    
    
  
    var store = new Ext.data.GroupingStore({
            url: 'EventViewerExtJsJSONServlet',
            sortInfo: {
            	field: 'Sex',
            	direction: "ASC"
            	},
            groupField: 'Sex',
            reader: new Ext.data.JsonReader({
                root:'rows',
                totalProperty: 'results',
                id:'Target'
                }, [
                'Target',
                'Species',
                'Sex'               
            ]),
            
            autoLoad: true
        });
        
        
        var grid = new Ext.grid.GridPanel({
            renderTo: extjsdemo,
            frame:true,
            stripeRows: true,
            title: 'animal list',
            height:300,
            width: 600,
            enableColumnMove: true,
            collapsible: true,
            store: store,
            columns: [
                {header: 'Targets', dataIndex: 'Target', sortable: true },
                {header: 'Species', dataIndex: 'Species', sortable: true, resizable: true},
                {header: 'Sex', dataIndex: 'Sex',sortable: true, resizable: true}               
            ],
            view: new Ext.grid.GroupingView(),
            
            fbar  : ['->', {
                text:'Clear Grouping',
                handler : function(){
                    store.clearGrouping();
                }
            }],
        });
        
    });