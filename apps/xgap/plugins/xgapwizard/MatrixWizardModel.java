package plugins.xgapwizard;

import java.util.List;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class MatrixWizardModel {

	private List<DataInfo> dataInfo;
	private Boolean showVerified;
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

	public Boolean getShowVerified() {
		return showVerified;
	}

	public void setShowVerified(Boolean showVerified) {
		this.showVerified = showVerified;
	}

	
}
