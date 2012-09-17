/* Date:        October 21, 2011
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix;

import java.util.Date;
import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.matrix.component.MatrixViewer;

/**
 * GidsMatrixModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class SampleMatrixModel extends EasyPluginModel
{
	//a system variable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	
	MatrixViewer matrixViewerIndv = null;
	MatrixViewer matrixViewerSample = null;
	static String INDVMATRIXS = "indvmatrixs";
	static String SAMPLEMATRIXS = "samplematrixs";
	String action = "setSample";
	boolean error = false;
	String selection = null;
	String chosenProtocolNameI = "Individual_info";
	String chosenProtocolNameS = "Sample_info";
	List<Integer> listSamples = null;
	String sampleNavClass;
	String dnaNavClass;
	String rnaNavClass;
	String biopsiesNavClass;
	String serumNavClass;
	String plasmaNavClass;
	String hlaNavClass;
	String individualNavClass;
	String personalNavClass;
	String medicalNavClass;
	String projectSpecificNavClass;
	int selectedScreenI = 1;
	int selectedScreenS = 1;
	private Boolean checkIfInvestchanges = false;
	private Boolean checkForPaging = false;
	private String lastInvest = "";
	boolean isProjectShared;
	String investigation;
	private List<String> measExport;
	
	
	//another example, you can also use getInvestigations() and setInvestigations(...)
	//public List<Investigation> investigations = new ArrayList<Investigation>();

	

	public SampleMatrixModel(SampleMatrix controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}
	
	public List<String> getMeasExport() {
		return measExport;
	}

	public void setMeasExport(List<String> measExport) {
		this.measExport = measExport;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getChosenProtocolNameI() {
		return chosenProtocolNameI;
	}

	public void setChosenProtocolNameI(String chosenProtocolName) {
		this.chosenProtocolNameI = chosenProtocolName;
	}
	public String getChosenProtocolNameS() {
		return chosenProtocolNameS;
	}

	public void setChosenProtocolNameS(String chosenProtocolName) {
		this.chosenProtocolNameS = chosenProtocolName;
	}
	
	public List<Integer> getListSamples() {
		return listSamples;
	}

	public void setListSamples(List<Integer> listSamples) {
		this.listSamples = listSamples;
	}
	
	/**
	 * Render the matrix viewer as html.
	 * 
	 * @return
	 */
	public String getMatrixViewerIndv() {
		if (matrixViewerIndv != null) {
			return matrixViewerIndv.render();
		} else {
			return "No viewer available, matrix cannot be rendered.";
		}
	}
	public String getMatrixViewerSample() {
		if (matrixViewerSample != null) {
			return matrixViewerSample.render();
		} else {
			return "No viewer available, matrix cannot be rendered.";
		}
	}
	
	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getSampleNavClass() {
		return sampleNavClass;
	}

	public void setSampleNavClass(String sampleNavClass) {
		this.sampleNavClass = sampleNavClass;
	}

	public String getDnaNavClass() {
		return dnaNavClass;
	}

	public void setDnaNavClass(String dnaNavClass) {
		this.dnaNavClass = dnaNavClass;
	}

	public String getRnaNavClass() {
		return rnaNavClass;
	}

	public void setRnaNavClass(String rnaNavClass) {
		this.rnaNavClass = rnaNavClass;
	}

	public String getBiopsiesNavClass() {
		return biopsiesNavClass;
	}

	public void setBiopsiesNavClass(String biopsiesNavClass) {
		this.biopsiesNavClass = biopsiesNavClass;
	}

	public String getSerumNavClass() {
		return serumNavClass;
	}

	public void setSerumNavClass(String serumNavClass) {
		this.serumNavClass = serumNavClass;
	}

	public String getPlasmaNavClass() {
		return plasmaNavClass;
	}

	public void setPlasmaNavClass(String plasmaNavClass) {
		this.plasmaNavClass = plasmaNavClass;
	}

	public String getHlaNavClass() {
		return hlaNavClass;
	}

	public void setHlaNavClass(String hlaNavClass) {
		this.hlaNavClass = hlaNavClass;
	}

	public String getIndividualNavClass() {
		return individualNavClass;
	}

	public void setIndividualNavClass(String individualNavClass) {
		this.individualNavClass = individualNavClass;
	}

	public String getPersonalNavClass() {
		return personalNavClass;
	}

	public void setPersonalNavClass(String personalNavClass) {
		this.personalNavClass = personalNavClass;
	}

	public String getMedicalNavClass() {
		return medicalNavClass;
	}

	public void setMedicalNavClass(String medicalNavClass) {
		this.medicalNavClass = medicalNavClass;
	}
	public int getSelectedScreenI() {
		return selectedScreenI;
	}

	public void setSelectedScreenI(int selectedScreenI) {
		this.selectedScreenI = selectedScreenI;
	}

	public int getSelectedScreenS() {
		return selectedScreenS;
	}

	public void setSelectedScreenS(int selectedScreenS) {
		this.selectedScreenS = selectedScreenS;
	}

	public Boolean getCheckIfInvestchanges() {
		return checkIfInvestchanges;
	}

	public void setCheckIfInvestchanges(Boolean checkIfInvestchanges) {
		this.checkIfInvestchanges = checkIfInvestchanges;
	}

	public Boolean getCheckForPaging() {
		return checkForPaging;
	}

	public void setCheckForPaging(Boolean checkForPaging) {
		this.checkForPaging = checkForPaging;
	}

	public String getLastInvest() {
		return lastInvest;
	}

	public void setLastInvest(String lastInvest) {
		this.lastInvest = lastInvest;
	}
	
	public boolean isProjectShared() {
		return isProjectShared;
	}

	public void setProjectShared(boolean isProjectShared) {
		this.isProjectShared = isProjectShared;
	}

	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	public String getProjectSpecificNavClass() {
		return projectSpecificNavClass;
	}

	public void setProjectSpecificNavClass(String projectSpecificNavClass) {
		this.projectSpecificNavClass = projectSpecificNavClass;
	}
	
}
