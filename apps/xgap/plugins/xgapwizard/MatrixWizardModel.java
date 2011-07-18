package plugins.xgapwizard;

import java.util.List;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class MatrixWizardModel extends SimpleScreenModel {

	public MatrixWizardModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private List<DataInfo> dataInfo;
	private boolean showVerified;
	private List<String> tagList;
	


	public List<String> getTagList()
	{
		return tagList;
	}

	public void setTagList(List<String> tagList)
	{
		this.tagList = tagList;
	}

	public List<DataInfo> getDataInfo()
	{
		return this.dataInfo;
	}

	public void setDataInfo(List<DataInfo> dataInfo)
	{
		this.dataInfo = dataInfo;
	}

	public boolean isShowVerified()
	{
		return this.showVerified;
	}

	public void setShowVerified(boolean showVerified)
	{
		this.showVerified = showVerified;
	}

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	

}
