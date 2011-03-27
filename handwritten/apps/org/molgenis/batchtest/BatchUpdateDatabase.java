package org.molgenis.batchtest;

import org.molgenis.Molgenis;

public class BatchUpdateDatabase {


	public static void main(String[] args) throws Exception {
		new Molgenis("handwritten/apps/org/molgenis/batchtest/batch.properties").updateDb(true);
	}
}
