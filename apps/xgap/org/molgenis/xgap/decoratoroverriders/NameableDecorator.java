package org.molgenis.xgap.decoratoroverriders;

import org.molgenis.core.Nameable;
import org.molgenis.framework.db.Mapper;

public class NameableDecorator<E extends Nameable> extends decorators.NameableDecorator<E>
{
	public NameableDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
		this.strict = true;
	}
}
