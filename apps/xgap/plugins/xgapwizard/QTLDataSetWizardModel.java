package plugins.xgapwizard;

import java.util.List;

import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.organization.Investigation;

public class QTLDataSetWizardModel extends EasyPluginModel
{
	public QTLDataSetWizardModel(EasyPluginController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private Integer selectedInv;
	private List<Investigation> investigations;
	private List<OntologyTerm> crosses;
	private List<String> xqtlObservableFeatureTypes;
	
	public void clearMessage()
	{
		this.setMessages();
	}


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
