package plugins.xgapwizard;

import java.util.List;

import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;
import org.molgenis.organization.Investigation;

public class QTLDataSetWizardModel extends SimpleScreenModel
{
	public QTLDataSetWizardModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private Integer selectedInv;
	private List<Investigation> investigations;
	private List<OntologyTerm> crosses;
	private List<String> xqtlObservableFeatureTypes;
	
	


	public List<String> getXqtlObservableFeatureTypes()
	{
		return xqtlObservableFeatureTypes;
	}

	public void setXqtlObservableFeatureTypes(List<String> xqtlObservableFeatureTypes)
	{
		this.xqtlObservableFeatureTypes = xqtlObservableFeatureTypes;
	}

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

	public List<OntologyTerm> getCrosses()
	{
		return crosses;
	}

	public void setCrosses(List<OntologyTerm> crosses)
	{
		this.crosses = crosses;
	}


}
