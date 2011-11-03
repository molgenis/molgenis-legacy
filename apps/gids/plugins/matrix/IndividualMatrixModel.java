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
public class IndividualMatrixModel extends EasyPluginModel
{
	//a system variable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	
	MatrixViewer matrixViewerIndv = null;
	MatrixViewer matrixViewerSample = null;
	static String INDVMATRIX = "indvmatrix";
	static String SAMPLEMATRIX = "samplematrix";
	String action = "setIndividual";
	boolean error = false;
	String selection = null;
	String chosenProtocolName = "Individual_info";
	List<Integer> listIndividuals = null;
	String individualNavClass;
	String personalNavClass;
	String medicalNavClass;

	
	
	
	
	//another example, you can also use getInvestigations() and setInvestigations(...)
	//public List<Investigation> investigations = new ArrayList<Investigation>();

	

	public IndividualMatrixModel(IndividualMatrix controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
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

	public String getChosenProtocolName() {
		return chosenProtocolName;
	}

	public void setChosenProtocolName(String chosenProtocolName) {
		this.chosenProtocolName = chosenProtocolName;
	}

	
	public List<Integer> getListIndividuals() {
		return listIndividuals;
	}

	public void setListIndividuals(List<Integer> listIndividuals) {
		this.listIndividuals = listIndividuals;
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


}
