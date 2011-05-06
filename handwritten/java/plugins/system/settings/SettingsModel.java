package plugins.system.settings;

import java.io.File;
import java.util.HashMap;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

public class SettingsModel extends SimpleScreenModel {

	public SettingsModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	String hasSystemSettingsTable;
	HashMap<String, String> keyValsFromSettingsTable;
	String mkDirSuccess;
	String rwDirSuccess;
	File fileDir;
	Boolean verified;
	
	Boolean folderExists;
	Boolean folderHasContent;
	
	public String getHasSystemSettingsTable() {
		return hasSystemSettingsTable;
	}

	public void setHasSystemSettingsTable(String hasSystemSettingsTable) {
		this.hasSystemSettingsTable = hasSystemSettingsTable;
	}

	public HashMap<String, String> getKeyValsFromSettingsTable() {
		return keyValsFromSettingsTable;
	}

	public void setKeyValsFromSettingsTable(
			HashMap<String, String> keyValsFromSettingsTable) {
		this.keyValsFromSettingsTable = keyValsFromSettingsTable;
	}

	public String getMkDirSuccess() {
		return mkDirSuccess;
	}
	
	public Boolean getFolderExists() {
		return folderExists;
	}

	public void setFolderExists(Boolean folderExists) {
		this.folderExists = folderExists;
	}

	public Boolean getFolderHasContent() {
		return folderHasContent;
	}

	public void setFolderHasContent(Boolean folderHasContent) {
		this.folderHasContent = folderHasContent;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public void setMkDirSuccess(String mkDirSuccess) {
		this.mkDirSuccess = mkDirSuccess;
	}

	public File getFileDir() {
		return fileDir;
	}

	public void setFileDir(File fileDir) {
		this.fileDir = fileDir;
	}

	public String getRwDirSuccess() {
		return rwDirSuccess;
	}

	public void setRwDirSuccess(String rwDirSuccess) {
		this.rwDirSuccess = rwDirSuccess;
	}

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	


}
