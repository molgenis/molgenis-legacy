package plugins.matrix.admin;

import org.molgenis.data.Data;


public class MatrixAdminModel{

	private Data selectedData;
	private boolean hasBackend;
	
	public Data getSelectedData()
	{
		return selectedData;
	}
	public void setSelectedData(Data selectedData)
	{
		this.selectedData = selectedData;
	}
	public boolean isHasBackend()
	{
		return hasBackend;
	}
	public void setHasBackend(boolean hasBackend)
	{
		this.hasBackend = hasBackend;
	}

}
