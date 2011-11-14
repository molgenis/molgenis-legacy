package plugins.reportbuilder;

import java.util.TreeMap;

import org.molgenis.data.Data;

public class MatrixLocation
{
	private Data data;
	private int rowIndex;
	private int colIndex;
	private int totalRows;
	private int totalCols;
	private String rowImg;
	private String colImg;
	private TreeMap<String, Double> rowCorr;
	private TreeMap<String, Double> colCorr;
	

	public MatrixLocation(Data data, int rowIndex, int colIndex, int totalRows, int totalCols)
	{
		super();
		this.data = data;
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.totalRows = totalRows;
		this.totalCols = totalCols;
	}

	

	public TreeMap<String, Double> getRowCorr()
	{
		return rowCorr;
	}



	public void setRowCorr(TreeMap<String, Double> rowCorr)
	{
		this.rowCorr = rowCorr;
	}



	public TreeMap<String, Double> getColCorr()
	{
		return colCorr;
	}



	public void setColCorr(TreeMap<String, Double> colCorr)
	{
		this.colCorr = colCorr;
	}



	public String getRowImg()
	{
		return rowImg;
	}


	public void setRowImg(String rowImg)
	{
		this.rowImg = rowImg;
	}


	public String getColImg()
	{
		return colImg;
	}


	public void setColImg(String colImg)
	{
		this.colImg = colImg;
	}


	public Data getData()
	{
		return data;
	}


	public int getRowIndex()
	{
		return rowIndex;
	}


	public int getColIndex()
	{
		return colIndex;
	}


	public int getTotalRows()
	{
		return totalRows;
	}


	public int getTotalCols()
	{
		return totalCols;
	}



	
	
	
	
}
