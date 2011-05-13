package plugins.genericwizard;

import java.io.File;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

import app.ImportWizardExcelPrognosis;

public class GenericWizardModel {

	String whichScreen;
	File currentFile;
	ImportWizardExcelPrognosis iwep;
	boolean importSuccess;
	
	public String getWhichScreen() {
		return whichScreen;
	}

	public void setWhichScreen(String whichScreen) {
		this.whichScreen = whichScreen;
	}
	
	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}

	public ImportWizardExcelPrognosis getIwep() {
		return iwep;
	}

	public void setIwep(ImportWizardExcelPrognosis iwep) {
		this.iwep = iwep;
	}

	public boolean isImportSuccess() {
		return importSuccess;
	}

	public void setImportSuccess(boolean importSuccess) {
		this.importSuccess = importSuccess;
	}

}
