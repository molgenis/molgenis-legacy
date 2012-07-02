package org.molgenis.ngs.decorators;

import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;
import org.molgenis.ngs.LibraryLane;

public class LibraryLaneDecorator extends MapperDecorator<LibraryLane>
{

	public LibraryLaneDecorator(Mapper<LibraryLane> generatedMapper)
	{
		super(generatedMapper);
	}

	public int add(List<LibraryLane> entities) throws DatabaseException
	{
		this.generateIdentifiers(entities);
		return super.add(entities);
	}

	public int update(List<LibraryLane> entities) throws DatabaseException
	{
		this.generateIdentifiers(entities);
		return super.update(entities);
	}

	public void generateIdentifiers(List<LibraryLane> entities)
	{
		for (LibraryLane e : entities)
		{
			if (e.getBarcode_Name() != null)
			{
				e.setIdentifier(e.getFlowcell_Identifier() + "_L" + e.getLane() + "_" + e.getBarcode_Name() + "_" + e.getSample_Identifier());
			}
			else
			{
				e.setIdentifier(e.getFlowcell_Identifier() + "_L" + e.getLane() + "_" + e.getSample_Identifier());
			}
		}
	}
}
