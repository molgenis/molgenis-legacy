package plugins.matrix.manager;

import java.util.Map;

import org.molgenis.data.Data;

public class MatrixManagerModel {

	private Data selectedData;
	private Browser browser;
	
	private String colHeader;
	private String rowHeader;
	private Map<String, String> overlibText;

	
	private boolean hasBackend;
	private boolean uploadMode;
	private String uploadTextAreaContent;
	
	
	
	public Data getSelectedData()
	{
		return selectedData;
	}
	public void setSelectedData(Data selectedData)
	{
		this.selectedData = selectedData;
	}
	public String getUploadTextAreaContent() {
		return uploadTextAreaContent;
	}
	public void setUploadTextAreaContent(String uploadTextAreaContent) {
		this.uploadTextAreaContent = uploadTextAreaContent;
	}
	public boolean isHasBackend() {
		return hasBackend;
	}
	public void setHasBackend(boolean hasBackend) {
		this.hasBackend = hasBackend;
	}
	public boolean isUploadMode() {
		return uploadMode;
	}
	public void setUploadMode(boolean uploadMode) {
		this.uploadMode = uploadMode;
	}

	public Browser getBrowser() {
		return browser;
	}
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}
	public String getColHeader() {
		return colHeader;
	}
	public void setColHeader(String colHeader) {
		this.colHeader = colHeader;
	}
	public String getRowHeader() {
		return rowHeader;
	}
	public void setRowHeader(String rowHeader) {
		this.rowHeader = rowHeader;
	}
	public Map<String, String> getOverlibText() {
		return overlibText;
	}
	public void setOverlibText(Map<String, String> overlibText) {
		this.overlibText = overlibText;
	}
	
	
}
