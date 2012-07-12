package org.molgenis.datatable.view.JQGridJSObjects;

public class JQGridField {
    public final String name;
    public final String index;
    public int width = 100;
    public boolean sortable = true;
	public boolean search = true;

	//for tree view
	public final String title; 
	public final boolean isFolder = false;
	public final String path;
	
    public JQGridField(String name, String index) {
        this.name = name;
        this.index = index;
		this.title = name; 
		this.path = title;
    }
}