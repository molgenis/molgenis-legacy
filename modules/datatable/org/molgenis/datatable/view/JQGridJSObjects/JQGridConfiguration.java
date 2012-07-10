package org.molgenis.datatable.view.JQGridJSObjects;

import java.util.ArrayList;
import java.util.List;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.model.elements.Field;

public class JQGridConfiguration {

    private final String id;
    public final String url;
    public String datatype = "json";
    public List<String> colNames = new ArrayList<String>();
    public List<JQGridField> colModel = new ArrayList<JQGridField>();
    public int rowNum = 10;
    public Integer[] rowList = new Integer[]{10, 20, 30};
    public boolean viewrecords = true;
    public String sortorder = "desc";
    public String caption = "jqGrid View";
    public boolean autowidth = true;
    public boolean sortable = false;
    //public String postData = "viewType : JQ_GRID";

    public JQGridConfiguration(String id, String url, String caption, TupleTable tupleTable) throws TableException {
        this.id = id;
        this.url = url;
        this.caption = caption;
        
        for(final Field f : tupleTable.getColumns()) {
            colModel.add(new JQGridField(f.getName(), f.getName()));
            colNames.add(f.getName());
        }
    }

    public JQGridConfiguration(String id, String url, String caption) {
        this.id = id;
        this.url = url;
        this.caption = caption;
    }
}