package org.molgenis.datatable.view.JQGridJSObjects;

public class JQGridSettings {
	public boolean del;
	public boolean add;
	public boolean edit;
	public boolean search;

	public JQGridSettings() {}

	public JQGridSettings(boolean del, boolean add, boolean edit,
			boolean search) {
		this.del = del;
		this.add = add;
		this.edit = edit;
		this.search = search;
	}
}