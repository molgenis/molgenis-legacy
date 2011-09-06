package plugins.matrix.manager;

import java.util.Map;

import matrix.AbstractDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.pheno.ObservationElement;

public class MatrixManagerModel {

	
	private Data selectedData;
	private Browser browser;
	
	private String colHeader;
	private String rowHeader;
	private Map<String, ObservationElement> rowObsElem;
	private Map<String, ObservationElement> colObsElem;
	
	private boolean hasBackend;
	private boolean uploadMode;
	private String uploadTextAreaContent;
	
	public String renderRow(String name){
		ObservationElement o = rowObsElem.get(name);
		if(o == null){
			return name;
		}else{
			return AbstractDataMatrixInstance.render(o);
		}
	}
	
	public String renderCol(String name){
		ObservationElement o = colObsElem.get(name);
		if(o == null){
			return name;
		}else{
			return AbstractDataMatrixInstance.render(o);
		}
	}
	
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
	public Map<String, ObservationElement> getRowObsElem()
	{
		return rowObsElem;
	}
	public void setRowObsElem(Map<String, ObservationElement> rowObsElem)
	{
		this.rowObsElem = rowObsElem;
	}
	public Map<String, ObservationElement> getColObsElem()
	{
		return colObsElem;
	}
	public void setColObsElem(Map<String, ObservationElement> colObsElem)
	{
		this.colObsElem = colObsElem;
	}
	
}
