package org.molgenis.matrix.component.legacy;

public interface RenderDescriptor<R, C, V> {
	
	/**
	 * Explains the renderer how to visualize your values from the generic type.
	 */
	public String renderValue(V value) throws Exception;
	
	/**
	 * Explains the renderer how to visualize your row headers from the generic type.
	 */
	public String renderRow(R row) throws Exception;
	
	/**
	 * Explains the renderer how to visualize your column headers from the generic type.
	 */
	public String renderCol(C col) throws Exception;
	
	/**
	 * Simple (short string) render of the row header. Needed for e.g. dropdown selects.
	 */
	public String renderRowSimple(R row) throws Exception;
	
	/**
	 * Simple (short string) render of the column header. Needed for e.g. dropdown selects.
	 */
	public String renderColSimple(C col) throws Exception;
	
}
