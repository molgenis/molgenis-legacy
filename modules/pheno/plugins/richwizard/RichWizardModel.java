package plugins.richwizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;
import org.molgenis.organization.Investigation;

public class RichWizardModel extends SimpleScreenModel {

	
	public RichWizardModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private String state;
	private List<String> dataTypes;
	private List<Investigation> investigations;
	private List<org.molgenis.model.elements.Entity> entities;
	private List<String> entityNames;
	private ArrayList<String> uniqueAncestorsOfEntities;
	private HashMap<String, String> exampleCsvs;
	
	private HashMap<String, Integer> importProgress;
	
	
	
	public HashMap<String, Integer> getImportProgress()
	{
		return importProgress;
	}

	public void setImportProgress(HashMap<String, Integer> importProgress)
	{
		this.importProgress = importProgress;
	}

	public HashMap<String, String> getExampleCsvs()
	{
		return exampleCsvs;
	}

	public void setExampleCsvs(HashMap<String, String> exampleCsvs)
	{
		this.exampleCsvs = exampleCsvs;
	}

	public ArrayList<String> getUniqueAncestorsOfEntities()
	{
		return uniqueAncestorsOfEntities;
	}

	public void setUniqueAncestorsOfEntities(ArrayList<String> uniqueAncestorsOfEntities)
	{
		this.uniqueAncestorsOfEntities = uniqueAncestorsOfEntities;
	}

	public List<String> getEntityNames()
	{
		return entityNames;
	}

	public void setEntityNames(List<String> entityNames)
	{
		this.entityNames = entityNames;
	}

	public List<org.molgenis.model.elements.Entity> getEntities()
	{
		return entities;
	}

	public void setEntities(List<org.molgenis.model.elements.Entity> entities)
	{
		this.entities = entities;
	}

	public List<Investigation> getInvestigations()
	{
		return investigations;
	}

	public void setInvestigations(List<Investigation> investigations)
	{
		this.investigations = investigations;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public List<String> getDataTypes()
	{
		return dataTypes;
	}

	public void setDataTypes(List<String> dataTypes)
	{
		this.dataTypes = dataTypes;
	}

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
