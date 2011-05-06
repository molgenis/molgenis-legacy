package plugins.matrix.admin;

import org.molgenis.data.Data;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;


public class MatrixAdminModel extends SimpleScreenModel {

	public MatrixAdminModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}
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
	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
