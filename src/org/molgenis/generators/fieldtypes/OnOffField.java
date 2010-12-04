package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;

public class OnOffField extends BoolField
{
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "INTEGER";
	}
}
