package org.molgenis.generators.fieldtypes;

import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;

public class OnOffField extends BoolField
{
	@Override
	public String getMysqlType() throws MolgenisModelException
	{
		return "INTEGER";
	}
}
