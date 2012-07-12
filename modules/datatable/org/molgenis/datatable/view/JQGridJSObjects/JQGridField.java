package org.molgenis.datatable.view.JQGridJSObjects;

public class JQGridField {
    public final String name;
    public final String index;
    public int width = 100;
    public boolean sortable = false;

    public JQGridField(String name, String index) {
        this.name = name;
        this.index = index;
    }
}