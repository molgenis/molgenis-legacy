package org.molgenis.datatable.view;


public interface Exporter {
	public void export();
	
	public String getFileExtension();
	public String getMimeType();
}