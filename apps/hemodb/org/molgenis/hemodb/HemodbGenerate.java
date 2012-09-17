package org.molgenis.hemodb;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;

public class HemodbGenerate {
	public static void main(String[] args) throws Exception {
		try {
			FileUtils.deleteDirectory(new File("hsqldb"));
			new Molgenis("apps/hemodb/org/molgenis/hemodb/hemodb.properties")
					.generate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
