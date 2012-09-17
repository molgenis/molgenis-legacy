package plugins.rplot;

import java.util.ArrayList;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.DataMatrixInstance;

import org.molgenis.data.Data;

public class RplotPluginModel{

	private List<String> matrixRows = new ArrayList<String>();
	private List<String> matrixCols = new ArrayList<String>();
	private Data selectedData;

	private String selectedRow;
	private String selectedCol;

	private int selectedWidth;
	private int selectedHeight;
	
	private String tmpImgName;
	private String selectedPlotType;
	
	private DataMatrixInstance matrixInstance;
	
	
	
	public DataMatrixInstance getMatrixInstance()
	{
		return matrixInstance;
	}
	public void setMatrixInstance(DataMatrixInstance matrixInstance)
	{
		this.matrixInstance = matrixInstance;
	}
	public List<String> getMatrixRows()
	{
		return matrixRows;
	}
	public void setMatrixRows(List<String> matrixRows)
	{
		this.matrixRows = matrixRows;
	}
	public List<String> getMatrixCols()
	{
		return matrixCols;
	}
	public void setMatrixCols(List<String> matrixCols)
	{
		this.matrixCols = matrixCols;
	}
	public Data getSelectedData()
	{
		return selectedData;
	}
	public void setSelectedData(Data selectedData)
	{
		this.selectedData = selectedData;
	}
	public String getSelectedRow()
	{
		return selectedRow;
	}
	public void setSelectedRow(String selectedRow)
	{
		this.selectedRow = selectedRow;
	}
	public String getSelectedCol()
	{
		return selectedCol;
	}
	public void setSelectedCol(String selectedCol)
	{
		this.selectedCol = selectedCol;
	}
	public int getSelectedWidth()
	{
		return selectedWidth;
	}
	public void setSelectedWidth(int selectedWidth)
	{
		this.selectedWidth = selectedWidth;
	}
	public int getSelectedHeight()
	{
		return selectedHeight;
	}
	public void setSelectedHeight(int selectedHeight)
	{
		this.selectedHeight = selectedHeight;
	}
	public String getTmpImgName()
	{
		return tmpImgName;
	}
	public void setTmpImgName(String tmpImgName)
	{
		this.tmpImgName = tmpImgName;
	}
	public String getSelectedPlotType()
	{
		return selectedPlotType;
	}
	public void setSelectedPlotType(String selectedPlotType)
	{
		this.selectedPlotType = selectedPlotType;
	}
	
}
