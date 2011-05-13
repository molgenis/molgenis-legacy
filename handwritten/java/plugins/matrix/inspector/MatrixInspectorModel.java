package plugins.matrix.inspector;

import org.molgenis.data.Data;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class MatrixInspectorModel{

	private WarningsAndErrors warningsAndErrors;
	private Data selectedData;
	private boolean hasBackend;
	
	public WarningsAndErrors getWarningsAndErrors()
	{
		return warningsAndErrors;
	}
	public void setWarningsAndErrors(WarningsAndErrors warningsAndErrors)
	{
		this.warningsAndErrors = warningsAndErrors;
	}
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
