package org.molgenis.xgap.decoratoroverriders;

import org.molgenis.framework.db.Mapper;

public class DataDecorator<E extends org.molgenis.data.Data> extends decorators.DataDecorator<E>
{
	public DataDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
		this.strict = true;
	}
}
