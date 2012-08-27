package plugins.reportbuilder;

import java.util.List;

import org.molgenis.data.Data;
import org.molgenis.util.Entity;

public class Report
{
	
	public Report(Entity entity)
	{
		this.entity = entity;
	}
	
	Entity entity;
	List<MatrixLocation> matrices;

	public Entity getEntity()
	{
		return entity;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	public List<MatrixLocation> getMatrices()
	{
		return matrices;
	}

	public void setMatrices(List<MatrixLocation> matrices)
	{
		this.matrices = matrices;
	}
	
	
}
