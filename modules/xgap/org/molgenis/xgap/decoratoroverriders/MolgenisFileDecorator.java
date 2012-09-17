package org.molgenis.xgap.decoratoroverriders;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Mapper;

public class MolgenisFileDecorator<E extends MolgenisFile> extends decorators.MolgenisFileDecorator<E>
{
	public MolgenisFileDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
		this.strict = true;
	}
}
