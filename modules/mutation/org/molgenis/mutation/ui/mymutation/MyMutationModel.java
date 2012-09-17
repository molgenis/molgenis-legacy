/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.mymutation;

import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.mutation.vo.PatientSummaryVO;

/**
 * MyCOL7A1Model takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class MyMutationModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L;
	private String patientPager;
	private List<PatientSummaryVO> patientSummaryVOList;
	private String rawOutput = "";

	public String getPatientPager() {
		return patientPager;
	}

	public void setPatientPager(String patientPager) {
		this.patientPager = patientPager;
	}

	public List<PatientSummaryVO> getPatientSummaryVOList() {
		return patientSummaryVOList;
	}

	public void setPatientSummaryVOList(List<PatientSummaryVO> patientSummaryVOList) {
		this.patientSummaryVOList = patientSummaryVOList;
	}

	public String getRawOutput() {
		return rawOutput;
	}

	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}

	public MyMutationModel(MyMutation controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}
	
	
}
