package plugins.matrix_dev.manager;

import org.molgenis.data.Data;
import org.molgenis.matrix.component.MatrixRenderer;

public class MatrixManagerModel {

	
	private Data selectedData;
	private boolean hasBackend;
	private boolean uploadMode;
	private MatrixRenderer matrix;
	
	
	public MatrixRenderer getMatrix() {
		return matrix;
	}
	public void setMatrix(MatrixRenderer matrix) {
		this.matrix = matrix;
	}
	public Data getSelectedData()
	{
		return selectedData;
	}
	public void setSelectedData(Data selectedData)
	{
		this.selectedData = selectedData;
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
	
}
