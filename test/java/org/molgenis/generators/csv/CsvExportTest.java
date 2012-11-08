package org.molgenis.generators.csv;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.molgenis.GeneratorTestCase;
import org.molgenis.generators.Generator;
import org.molgenis.model.Feature;
import org.molgenis.model.MolgenisModelException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CsvExportTest extends GeneratorTestCase
{
	@Override
	protected Generator getGenerator() throws MolgenisModelException
	{
		return new CsvExportGen();
	}

	@Test
	public void testExportFeature() throws Exception
	{
		CsvExport csvExport = new CsvExport();
		File dir = new File(System.getProperty("java.io.tmpdir"));

		Feature feature1 = new Feature();
		feature1.setDataType("string");
		feature1.setName("feature1");

		Feature feature2 = new Feature();
		feature2.setDataType("boolean");
		feature2.setName("feature2");

		csvExport.exportAll(dir, Arrays.asList(feature1, feature2));

		String csvFeatures = FileUtils.readFileToString(
				new File(System.getProperty("java.io.tmpdir") + "/feature.txt"), Charset.forName("UTF-8"));

		String expected = "name\t__Type\tdataType\nfeature1\tFeature\tstring\nfeature2\tFeature\tboolean\n";

		Assert.assertEquals(csvFeatures, expected);
	}
}
