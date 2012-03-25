package plugins.matrix.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import matrix.AbstractDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.pheno.ObservationElement;

public class MatrixManagerModel {

	private HashMap<String, String> allOperators;
	private HashMap<String, String> valueOperators;
	
	private Data selectedData;
	private Browser browser;
	
	private String colHeader;
	private String rowHeader;
	private Map<String, ObservationElement> rowObsElem;
	private Map<String, ObservationElement> colObsElem;
	
	private List<String> rowHeaderAttr;
	private List<String> colHeaderAttr;
	
	private boolean hasBackend;
	private boolean uploadMode;
	
	private String uploadTextAreaContent;
	private String filter;
	private String selectedFilterDiv;
	
	private String tmpImgName;
	private int selectedWidth;
	private int selectedHeight;
	
	public String renderRow(String name, String screenName){
		ObservationElement o = rowObsElem.get(name);
		if(o == null){
			return name;
		}else{
			return AbstractDataMatrixInstance.render(o, screenName);
		}
	}
	
	public String renderCol(String name, String screenName){
		ObservationElement o = colObsElem.get(name);
		if(o == null){
			return name;
		}else{
			return AbstractDataMatrixInstance.render(o, screenName);
		}
	}
	
	
	
	public String getSelectedFilterDiv()
	{
		return selectedFilterDiv;
	}

	public void setSelectedFilterDiv(String selectedFilterDiv)
	{
		this.selectedFilterDiv = selectedFilterDiv;
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

	public String getFilter()
	{
		return filter;
	}

	public void setFilter(String filter)
	{
		this.filter = filter;
	}

	public List<String> getRowHeaderAttr()
	{
		return rowHeaderAttr;
	}

	public void setRowHeaderAttr(List<String> rowHeaderAttr)
	{
		this.rowHeaderAttr = rowHeaderAttr;
	}

	public List<String> getColHeaderAttr()
	{
		return colHeaderAttr;
	}

	public void setColHeaderAttr(List<String> colHeaderAttr)
	{
		this.colHeaderAttr = colHeaderAttr;
	}



	public HashMap<String, String> getAllOperators()
	{
		return allOperators;
	}

	public void setAllOperators(HashMap<String, String> allOperators)
	{
		this.allOperators = allOperators;
	}

	public HashMap<String, String> getValueOperators()
	{
		return valueOperators;
	}

	public void setValueOperators(HashMap<String, String> valueOperators)
	{
		this.valueOperators = valueOperators;
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
