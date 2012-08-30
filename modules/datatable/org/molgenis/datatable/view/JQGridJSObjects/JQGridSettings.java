package org.molgenis.datatable.view.JQGridJSObjects;

public class JQGridSettings {
	public boolean del = false;
	public boolean add = false;
	public boolean edit = false;
	public boolean search = false;

	public JQGridSettings() {
	}

	public JQGridSettings(boolean del, boolean add, boolean edit, boolean search) {
		this.del = del;
		this.add = add;
		this.edit = edit;
		this.search = search;
	}
}