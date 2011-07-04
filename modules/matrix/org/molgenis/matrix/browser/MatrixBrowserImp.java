package org.molgenis.matrix.browser;

public class MatrixBrowserImp implements MatrixBrowser
{
	private int colOffset;
	private int colPageSize;
	
	private int rowOffset;
	private int rowPageSize;
	
	public int getColOffset()
	{
		return colOffset;
	}
	public void setColOffset(int colOffset)
	{
		this.colOffset = colOffset;
	}
	public int getColPageSize()
	{
		return colPageSize;
	}
	public void setColPageSize(int colPageSize)
	{
		this.colPageSize = colPageSize;
	}
	public int getRowOffset()
	{
		return rowOffset;
	}
	public void setRowOffset(int rowOffset)
	{
		this.rowOffset = rowOffset;
	}
	public int getRowPageSize()
	{
		return rowPageSize;
	}
	public void setRowPageSize(int rowPageSize)
	{
		this.rowPageSize = rowPageSize;
	}

	

	
}
