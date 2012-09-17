package org.molgenis.mutation.util;

import java.util.Comparator;

import org.molgenis.pheno.AlternateId;

public class AlternateIdComparator implements Comparator<AlternateId>
{
	@Override
	public int compare(AlternateId o1, AlternateId o2)
	{
		Integer identifier1 = Integer.valueOf(o1.getName().substring(1));
		Integer identifier2 = Integer.valueOf(o2.getName().substring(1));
		return identifier1.compareTo(identifier2);
	}

}
