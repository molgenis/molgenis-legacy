package org.molgenis.mutation.util;

import java.util.Comparator;

import org.molgenis.mutation.Mutation;

public class MutationComparator implements Comparator<Mutation> {

	@Override
	public int compare(Mutation o1, Mutation o2) {
		Integer identifier1 = Integer.valueOf(o1.getIdentifier().substring(1));
		Integer identifier2 = Integer.valueOf(o2.getIdentifier().substring(1));
		return identifier1.compareTo(identifier2);
	}

}
