package plugins.xgapwizard;

import java.util.HashMap;
import java.util.List;

import org.molgenis.organization.Investigation;

public class QTLDataSetWizardModel
{
	private Integer selectedInv;
	private List<Investigation> investigations;

	public Integer getSelectedInv()
	{
		return selectedInv;
	}

	public void setSelectedInv(Integer selectedInv)
	{
		this.selectedInv = selectedInv;
	}

	public List<Investigation> getInvestigations()
	{
		return investigations;
	}

	public void setInvestigations(List<Investigation> investigations)
	{
		this.investigations = investigations;
	}

}
