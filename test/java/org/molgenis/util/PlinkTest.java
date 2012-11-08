package org.molgenis.util;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.IOException;

import org.molgenis.util.plink.drivers.BedFileDriver;
import org.molgenis.util.plink.drivers.BimFileDriver;
import org.molgenis.util.plink.drivers.FamFileDriver;
import org.molgenis.util.plink.drivers.MapFileDriver;
import org.molgenis.util.plink.drivers.PedFileDriver;
import org.molgenis.util.plink.drivers.TpedFileDriver;
import org.molgenis.util.plink.writers.BimFileWriter;
import org.molgenis.util.plink.writers.FamFileWriter;
import org.molgenis.util.plink.writers.MapFileWriter;
import org.molgenis.util.plink.writers.PedFileWriter;
import org.molgenis.util.plink.writers.TpedFileWriter;
import org.molgenis.util.test.AbstractResourceTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test the file drivers for Plink formats
 * 
 */
public class PlinkTest extends AbstractResourceTest
{
	File testBed;
	BedFileDriver bedfd;

	File testBim;
	BimFileDriver bimfd;

	File testFam;
	FamFileDriver famfd;

	File testPed;
	PedFileDriver pedfd;

	File testMap;
	MapFileDriver mapfd;

	File testTped;
	TpedFileDriver tpedfd;

	@BeforeClass
	public void setup() throws Exception
	{
		testBed = getTestResource("/test.bed");
		bedfd = new BedFileDriver(testBed);

		testBim = getTestResource("/test.bim");
		bimfd = new BimFileDriver(testBim);

		testFam = getTestResource("/test.fam");
		famfd = new FamFileDriver(testFam);

		testPed = getTestResource("/test.ped");
		pedfd = new PedFileDriver(testPed);

		testMap = getTestResource("/test.map");
		mapfd = new MapFileDriver(testMap);

		testTped = getTestResource("/test.tped");
		tpedfd = new TpedFileDriver(testTped);
	}

	@Test
	public void BED_construct() throws Exception
	{
		assertEquals(1, bedfd.getMode());
		assertEquals(16, bedfd.getNrOfElements());
	}

	@Test
	public void BIM_construct() throws Exception
	{
		assertEquals(2, bimfd.getNrOfElements());
	}

	@Test
	public void FAM_construct() throws Exception
	{
		assertEquals(6, famfd.getNrOfElements());
	}

	@Test
	public void PED_construct() throws Exception
	{
		assertEquals(6, pedfd.getNrOfElements());
	}

	@Test
	public void MAP_construct() throws Exception
	{
		assertEquals(2, mapfd.getNrOfElements());
	}

	@Test
	public void BED_getElements() throws Exception
	{
		// String[] all = new String[]
		// { "00", "01", "11", "01", "11", "11", "00", "00", "01", "01", "00",
		// "11", "01", "11", "00", "00" };

		// String[] subset1 = new String[]
		// { all[0], all[1], all[2], all[3] };
		// String[] subset2 = new String[]
		// { all[1], all[2], all[3], all[4] };
		// String[] subset3 = new String[]
		// { all[2], all[3], all[4], all[5] };
		// String[] subset4 = new String[]
		// { all[3], all[4], all[5], all[6] };
		// String[] subset5 = new String[]
		// { all[13], all[14] };
		// String[] subset6 = new String[]
		// { all[14], all[15] };
		// String[] subset7 = new String[]
		// { all[10], all[11], all[12], all[13], all[14] };
		// String[] subset8 = new String[]
		// { all[5], all[6], all[7], all[8], all[9] };

		// single elements
		/*
		 * assertTrue(stringArrEqual(new String[] { all[0] },
		 * bedfd.getElements(0, 1))); assertTrue(stringArrEqual(new String[] {
		 * all[1] }, bedfd.getElements(1, 2))); assertTrue(stringArrEqual(new
		 * String[] { all[2] }, bedfd.getElements(2, 3)));
		 * assertTrue(stringArrEqual(new String[] { all[3] },
		 * bedfd.getElements(3, 4))); assertTrue(stringArrEqual(new String[] {
		 * all[4] }, bedfd.getElements(4, 5))); assertTrue(stringArrEqual(new
		 * String[] { all[5] }, bedfd.getElements(5, 6)));
		 * assertTrue(stringArrEqual(new String[] { all[6] },
		 * bedfd.getElements(6, 7))); assertTrue(stringArrEqual(new String[] {
		 * all[7] }, bedfd.getElements(7, 8))); assertTrue(stringArrEqual(new
		 * String[] { all[8] }, bedfd.getElements(8, 9)));
		 * assertTrue(stringArrEqual(new String[] { all[9] },
		 * bedfd.getElements(9, 10))); assertTrue(stringArrEqual(new String[] {
		 * all[10] }, bedfd.getElements(10, 11))); assertTrue(stringArrEqual(new
		 * String[] { all[11] }, bedfd.getElements(11, 12)));
		 * assertTrue(stringArrEqual(new String[] { all[12] },
		 * bedfd.getElements(12, 13))); assertTrue(stringArrEqual(new String[] {
		 * all[13] }, bedfd.getElements(13, 14))); assertTrue(stringArrEqual(new
		 * String[] { all[14] }, bedfd.getElements(14, 15)));
		 * assertTrue(stringArrEqual(new String[] { all[15] },
		 * bedfd.getElements(15, 16)));
		 * 
		 * // subsets assertTrue(stringArrEqual(subset1, bedfd.getElements(0,
		 * 4))); assertTrue(stringArrEqual(subset2, bedfd.getElements(1, 5)));
		 * assertTrue(stringArrEqual(subset3, bedfd.getElements(2, 6)));
		 * assertTrue(stringArrEqual(subset4, bedfd.getElements(3, 7)));
		 * assertTrue(stringArrEqual(subset5, bedfd.getElements(13, 15)));
		 * assertTrue(stringArrEqual(subset6, bedfd.getElements(14, 16)));
		 * assertTrue(stringArrEqual(subset7, bedfd.getElements(10, 15)));
		 * assertTrue(stringArrEqual(subset8, bedfd.getElements(5, 10)));
		 */

		// everything
		// assertTrue(stringArrEqual(all, bedfd.getElements(0, 16)));
	}

	@Test
	public void BED_getElement() throws Exception
	{
		assertEquals("00", bedfd.getElement(0));
		assertEquals("01", bedfd.getElement(1));
		assertEquals("11", bedfd.getElement(2));
		assertEquals("01", bedfd.getElement(3));
		assertEquals("11", bedfd.getElement(4));
		assertEquals("11", bedfd.getElement(5));
		assertEquals("00", bedfd.getElement(6));
		assertEquals("00", bedfd.getElement(7));
		assertEquals("01", bedfd.getElement(8));
		assertEquals("01", bedfd.getElement(9));
		assertEquals("00", bedfd.getElement(10));
		assertEquals("11", bedfd.getElement(11));
		assertEquals("01", bedfd.getElement(12));
		assertEquals("11", bedfd.getElement(13));
		assertEquals("00", bedfd.getElement(14));
		assertEquals("00", bedfd.getElement(15));
	}

	@Test
	public void BIM_getEntries() throws Exception
	{
		assertEquals(1, bimfd.getEntries(0, 1).size());
		assertEquals(1, bimfd.getEntries(1, 2).size());
		assertEquals(2, bimfd.getEntries(0, 2).size());
		assertEquals(2, bimfd.getAllEntries().size());

		assertEquals("snp1", bimfd.getEntries(0, 1).get(0).getSNP());
		assertEquals('A', bimfd.getEntries(0, 1).get(0).getBiallele().getAllele1());
		assertEquals(0.0, bimfd.getEntries(1, 2).get(0).getcM());
		assertEquals("1", bimfd.getEntries(1, 2).get(0).getChromosome());

		assertEquals('C', bimfd.getAllEntries().get(0).getBiallele().getAllele2());
		assertEquals('T', bimfd.getAllEntries().get(1).getBiallele().getAllele2());
		assertEquals(1, bimfd.getAllEntries().get(0).getBpPos());
		assertEquals(2, bimfd.getAllEntries().get(1).getBpPos());
		assertEquals("snp2", bimfd.getAllEntries().get(1).getSNP());
	}

	@Test
	public void FAM_getEntries() throws Exception
	{
		assertEquals(1, famfd.getEntries(0, 1).size());
		assertEquals(1, famfd.getEntries(1, 2).size());
		assertEquals(2, famfd.getEntries(0, 2).size());
		assertEquals(6, famfd.getEntries(0, 6).size());

		assertEquals("1", famfd.getEntries(0, 1).get(0).getFamily());
		assertEquals("2", famfd.getEntries(0, 2).get(1).getFamily());

		assertEquals("5", famfd.getEntries(3, 5).get(1).getFamily());
		assertEquals("6", famfd.getEntries(0, 6).get(5).getFamily());

		assertEquals(1.0, famfd.getAllEntries().get(2).getPhenotype());
		assertEquals(2.0, famfd.getAllEntries().get(3).getPhenotype());
	}

	// TODO
	@Test
	public void PED_getEntries() throws Exception
	{
		// 1 1 0 0 1 1 A A G T
		assertEquals('A', pedfd.getAllEntries().get(0).getBialleles().get(0).getAllele1());
		assertEquals('A', pedfd.getAllEntries().get(0).getBialleles().get(0).getAllele2());
		assertEquals('G', pedfd.getAllEntries().get(0).getBialleles().get(1).getAllele1());
		assertEquals('T', pedfd.getAllEntries().get(0).getBialleles().get(1).getAllele2());

		assertEquals('A', pedfd.getAllEntries().get(1).getBialleles().get(0).getAllele1());
		assertEquals('C', pedfd.getAllEntries().get(1).getBialleles().get(0).getAllele2());
		assertEquals('T', pedfd.getAllEntries().get(1).getBialleles().get(1).getAllele1());
		assertEquals('G', pedfd.getAllEntries().get(1).getBialleles().get(1).getAllele2());

		assertEquals('C', pedfd.getAllEntries().get(5).getBialleles().get(0).getAllele1());
		assertEquals('C', pedfd.getAllEntries().get(5).getBialleles().get(0).getAllele2());
		assertEquals('T', pedfd.getAllEntries().get(5).getBialleles().get(1).getAllele1());
		assertEquals('T', pedfd.getAllEntries().get(5).getBialleles().get(1).getAllele2());

		assertEquals("3", pedfd.getAllEntries().get(2).getFamily());
		assertEquals("0", pedfd.getAllEntries().get(2).getFather());
		assertEquals("0", pedfd.getAllEntries().get(2).getMother());
		assertEquals(1.0, pedfd.getAllEntries().get(2).getPhenotype());
		assertEquals("1", pedfd.getAllEntries().get(2).getIndividual());
		assertEquals(1, pedfd.getAllEntries().get(2).getSex());

		assertEquals("3", pedfd.getEntries(2, 3).get(0).getFamily());
		assertEquals('G', pedfd.getEntries(2, 3).get(0).getBialleles().get(1).getAllele2());
		assertEquals("3", pedfd.getEntries(0, 3).get(2).getFamily());
		assertEquals('G', pedfd.getEntries(0, 3).get(2).getBialleles().get(1).getAllele1());
		assertEquals("4", pedfd.getEntries(2, 4).get(1).getFamily());
		assertEquals(2.0, pedfd.getEntries(2, 4).get(1).getPhenotype());

		assertEquals('C', pedfd.getEntries(0, 6).get(4).getBialleles().get(0).getAllele1());
		assertEquals('C', pedfd.getEntries(1, 6).get(3).getBialleles().get(0).getAllele2());
		assertEquals('G', pedfd.getEntries(2, 6).get(2).getBialleles().get(1).getAllele1());
		assertEquals('T', pedfd.getEntries(3, 6).get(1).getBialleles().get(1).getAllele2());

	}

	@Test
	public void MAP_getEntries() throws Exception
	{
		assertEquals(1, mapfd.getEntries(0, 1).size());
		assertEquals(1, mapfd.getEntries(1, 2).size());
		assertEquals(2, mapfd.getEntries(0, 2).size());
		assertEquals(2, mapfd.getAllEntries().size());
		assertEquals("snp1", mapfd.getEntries(0, 1).get(0).getSNP());
		assertEquals(0.0, mapfd.getEntries(1, 2).get(0).getcM());
		assertEquals("1", mapfd.getEntries(1, 2).get(0).getChromosome());
		assertEquals(1, mapfd.getAllEntries().get(0).getBpPos());
		assertEquals(2, mapfd.getAllEntries().get(1).getBpPos());
		assertEquals("snp2", mapfd.getAllEntries().get(1).getSNP());
	}

	@Test
	public void BIM_writer() throws Exception
	{
		File newBim = new File(testBim.getAbsolutePath().replace(testBim.getName(), "new.bim"));
		BimFileWriter w = new BimFileWriter(newBim);
		try
		{
			w.writeAll(bimfd.getAllEntries());
			boolean filesAreEqual = DirectoryCompare.compareFileContent(testBim, newBim);
			assertTrue(filesAreEqual);
		}
		finally
		{
			w.close();
		}

	}

	@Test
	public void MAP_writer() throws Exception
	{
		File newMap = new File(testMap.getAbsolutePath().replace(testMap.getName(), "new.map"));
		MapFileWriter w = new MapFileWriter(newMap);
		try
		{
			w.writeAll(mapfd.getAllEntries());
			boolean filesAreEqual = DirectoryCompare.compareFileContent(testMap, newMap);
			assertTrue(filesAreEqual);
		}
		finally
		{
			w.close();
		}
	}

	@Test
	public void PED_writer() throws Exception
	{
		File newPed = new File(testPed.getAbsolutePath().replace(testPed.getName(), "new.ped"));
		PedFileWriter w = new PedFileWriter(newPed);
		try
		{
			w.writeAll(pedfd.getAllEntries());
			boolean filesAreEqual = DirectoryCompare.compareFileContent(testPed, newPed);
			assertTrue(filesAreEqual);
		}
		finally
		{
			w.close();
		}
	}

	@Test
	public void FAM_writer() throws Exception
	{
		File newFam = new File(testFam.getAbsolutePath().replace(testFam.getName(), "new.fam"));
		FamFileWriter w = new FamFileWriter(newFam);
		try
		{
			w.writeAll(famfd.getAllEntries());
			boolean filesAreEqual = DirectoryCompare.compareFileContent(testFam, newFam);
			assertTrue(filesAreEqual);
		}
		finally
		{
			w.close();
		}
	}

	@Test
	public void TPED_writer() throws Exception
	{
		File newTped = new File(testTped.getAbsolutePath().replace(testTped.getName(), "new.tped"));
		TpedFileWriter w = new TpedFileWriter(newTped);
		try
		{
			w.writeAll(tpedfd.getAllEntries());
			boolean filesAreEqual = DirectoryCompare.compareFileContent(testTped, newTped);
			assertTrue(filesAreEqual);
		}
		finally
		{
			w.close();
		}
	}

	@AfterClass
	public void close() throws IOException
	{
		bimfd.close();
		famfd.close();
		pedfd.close();
		mapfd.close();
		tpedfd.close();
	}
}
