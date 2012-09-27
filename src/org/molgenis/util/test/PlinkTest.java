package org.molgenis.util.test;


/**
 * Test the file drivers for Plink formats
 * 
 */
public class PlinkTest
{
	// TODO this test fails, fix it
	/*
	 * File testBed; BedFileDriver bedfd;
	 * 
	 * File testBim; BimFileDriver bimfd;
	 * 
	 * File testFam; FamFileDriver famfd;
	 * 
	 * File testPed; PedFileDriver pedfd;
	 * 
	 * File testMap; MapFileDriver mapfd;
	 * 
	 * File testTped; TpedFileDriver tpedfd;
	 * 
	 * @BeforeClass public void setup() throws Exception { testBed = new
	 * File(Biallele.class.getResource("../testfiles/test.bed").getFile());
	 * bedfd = new BedFileDriver(testBed);
	 * 
	 * testBim = new
	 * File(Biallele.class.getResource("../testfiles/test.bim").getFile());
	 * bimfd = new BimFileDriver(testBim);
	 * 
	 * testFam = new
	 * File(Biallele.class.getResource("../testfiles/test.fam").getFile());
	 * famfd = new FamFileDriver(testFam);
	 * 
	 * testPed = new
	 * File(Biallele.class.getResource("../testfiles/test.ped").getFile());
	 * pedfd = new PedFileDriver(testPed);
	 * 
	 * testMap = new
	 * File(Biallele.class.getResource("../testfiles/test.map").getFile());
	 * mapfd = new MapFileDriver(testMap);
	 * 
	 * testTped = new
	 * File(Biallele.class.getResource("../testfiles/test.tped").getFile());
	 * tpedfd = new TpedFileDriver(testTped); }
	 * 
	 * @Test public void BED_construct() throws Exception {
	 * Assert.assertEquals(1, bedfd.getMode()); Assert.assertEquals(16,
	 * bedfd.getNrOfElements()); }
	 * 
	 * @Test public void BIM_construct() throws Exception {
	 * Assert.assertEquals(2, bimfd.getNrOfElements()); }
	 * 
	 * @Test public void FAM_construct() throws Exception {
	 * Assert.assertEquals(6, famfd.getNrOfElements()); }
	 * 
	 * @Test public void PED_construct() throws Exception {
	 * Assert.assertEquals(6, pedfd.getNrOfElements()); }
	 * 
	 * @Test public void MAP_construct() throws Exception {
	 * Assert.assertEquals(2, mapfd.getNrOfElements()); }
	 * 
	 * @Test public void BED_getElements() throws Exception { String[] all = new
	 * String[] { null, "01", "11", "01", "11", "11", "00", "00", "01", "01",
	 * "00", "11", "01", "11", "00", "00" };
	 * 
	 * String[] subset1 = new String[] { all[0], all[1], all[2], all[3] };
	 * String[] subset2 = new String[] { all[1], all[2], all[3], all[4] };
	 * String[] subset3 = new String[] { all[2], all[3], all[4], all[5] };
	 * String[] subset4 = new String[] { all[3], all[4], all[5], all[6] };
	 * String[] subset5 = new String[] { all[13], all[14] }; String[] subset6 =
	 * new String[] { all[14], all[15] }; String[] subset7 = new String[] {
	 * all[10], all[11], all[12], all[13], all[14] }; String[] subset8 = new
	 * String[] { all[5], all[6], all[7], all[8], all[9] };
	 * 
	 * // single elements Assert.assertTrue(stringArrEqual(new String[] { all[0]
	 * }, bedfd.getElements(0, 1, 2, 0))); Assert.assertTrue(stringArrEqual(new
	 * String[] { all[1] }, bedfd.getElements(1, 2, 3, 1))); // //
	 * Assert.assertTrue(stringArrEqual(new String[] { all[2] }, //
	 * bedfd.getElements(2, 3))); Assert.assertTrue(stringArrEqual(new //
	 * String[] { all[3] }, bedfd.getElements(3, 4))); //
	 * Assert.assertTrue(stringArrEqual(new String[] { all[4] }, //
	 * bedfd.getElements(4, 5))); Assert.assertTrue(stringArrEqual(new //
	 * String[] { all[5] }, bedfd.getElements(5, 6))); //
	 * Assert.assertTrue(stringArrEqual(new String[] { all[6] }, //
	 * bedfd.getElements(6, 7))); Assert.assertTrue(stringArrEqual(new //
	 * String[] { all[7] }, bedfd.getElements(7, 8))); //
	 * Assert.assertTrue(stringArrEqual(new String[] { all[8] }, //
	 * bedfd.getElements(8, 9))); Assert.assertTrue(stringArrEqual(new //
	 * String[] { all[9] }, bedfd.getElements(9, 10))); //
	 * Assert.assertTrue(stringArrEqual(new String[] { all[10] }, //
	 * bedfd.getElements(10, 11))); Assert.assertTrue(stringArrEqual(new //
	 * String[] { all[11] }, bedfd.getElements(11, 12))); //
	 * Assert.assertTrue(stringArrEqual(new String[] { all[12] }, //
	 * bedfd.getElements(12, 13))); Assert.assertTrue(stringArrEqual(new //
	 * String[] { all[13] }, bedfd.getElements(13, 14))); //
	 * Assert.assertTrue(stringArrEqual(new String[] { all[14] }, //
	 * bedfd.getElements(14, 15))); Assert.assertTrue(stringArrEqual(new //
	 * String[] { all[15] }, bedfd.getElements(15, 16))); // // // subsets
	 * Assert.assertTrue(stringArrEqual(subset1, // bedfd.getElements(0, 4)));
	 * Assert.assertTrue(stringArrEqual(subset2, // bedfd.getElements(1, 5)));
	 * Assert.assertTrue(stringArrEqual(subset3, // bedfd.getElements(2, 6)));
	 * Assert.assertTrue(stringArrEqual(subset4, // bedfd.getElements(3, 7)));
	 * Assert.assertTrue(stringArrEqual(subset5, // bedfd.getElements(13, 15)));
	 * // Assert.assertTrue(stringArrEqual(subset6, bedfd.getElements(14, //
	 * 16))); Assert.assertTrue(stringArrEqual(subset7, // bedfd.getElements(10,
	 * 15))); // Assert.assertTrue(stringArrEqual(subset8, bedfd.getElements(5,
	 * 10))); // // // everything // Assert.assertTrue(stringArrEqual(all,
	 * bedfd.getElements(0, 16))); }
	 * 
	 * @Test public void BED_getElement() throws Exception {
	 * Assert.assertEquals("00", bedfd.getElement(0)); Assert.assertEquals("01",
	 * bedfd.getElement(1)); Assert.assertEquals("11", bedfd.getElement(2));
	 * Assert.assertEquals("01", bedfd.getElement(3)); Assert.assertEquals("11",
	 * bedfd.getElement(4)); Assert.assertEquals("11", bedfd.getElement(5));
	 * Assert.assertEquals("00", bedfd.getElement(6)); Assert.assertEquals("00",
	 * bedfd.getElement(7)); Assert.assertEquals("01", bedfd.getElement(8));
	 * Assert.assertEquals("01", bedfd.getElement(9)); Assert.assertEquals("00",
	 * bedfd.getElement(10)); Assert.assertEquals("11", bedfd.getElement(11));
	 * Assert.assertEquals("01", bedfd.getElement(12));
	 * Assert.assertEquals("11", bedfd.getElement(13));
	 * Assert.assertEquals("00", bedfd.getElement(14));
	 * Assert.assertEquals("00", bedfd.getElement(15)); }
	 * 
	 * @Test public void BIM_getEntries() throws Exception {
	 * Assert.assertEquals(1, bimfd.getEntries(0, 1).size());
	 * Assert.assertEquals(1, bimfd.getEntries(1, 2).size());
	 * Assert.assertEquals(2, bimfd.getEntries(0, 2).size());
	 * Assert.assertEquals(2, bimfd.getAllEntries().size());
	 * 
	 * Assert.assertEquals("snp1", bimfd.getEntries(0, 1).get(0).getSNP());
	 * Assert.assertEquals('A', bimfd.getEntries(0,
	 * 1).get(0).getBiallele().getAllele1()); Assert.assertEquals(0.0,
	 * bimfd.getEntries(1, 2).get(0).getcM()); Assert.assertEquals("1",
	 * bimfd.getEntries(1, 2).get(0).getChromosome());
	 * 
	 * Assert.assertEquals('C',
	 * bimfd.getAllEntries().get(0).getBiallele().getAllele2());
	 * Assert.assertEquals('T',
	 * bimfd.getAllEntries().get(1).getBiallele().getAllele2());
	 * Assert.assertEquals(1, bimfd.getAllEntries().get(0).getBpPos());
	 * Assert.assertEquals(2, bimfd.getAllEntries().get(1).getBpPos());
	 * Assert.assertEquals("snp2", bimfd.getAllEntries().get(1).getSNP()); }
	 * 
	 * @Test public void FAM_getEntries() throws Exception {
	 * Assert.assertEquals(1, famfd.getEntries(0, 1).size());
	 * Assert.assertEquals(1, famfd.getEntries(1, 2).size());
	 * Assert.assertEquals(2, famfd.getEntries(0, 2).size());
	 * Assert.assertEquals(6, famfd.getEntries(0, 6).size());
	 * 
	 * Assert.assertEquals("1", famfd.getEntries(0, 1).get(0).getFamily());
	 * Assert.assertEquals("2", famfd.getEntries(0, 2).get(1).getFamily());
	 * 
	 * Assert.assertEquals("5", famfd.getEntries(3, 5).get(1).getFamily());
	 * Assert.assertEquals("6", famfd.getEntries(0, 6).get(5).getFamily());
	 * 
	 * Assert.assertEquals(1.0, famfd.getAllEntries().get(2).getPhenotype());
	 * Assert.assertEquals(2.0, famfd.getAllEntries().get(3).getPhenotype()); }
	 * 
	 * // TODO
	 * 
	 * @Test public void PED_getEntries() throws Exception { // 1 1 0 0 1 1 A A
	 * G T Assert.assertEquals('A',
	 * pedfd.getAllEntries().get(0).getBialleles().get(0).getAllele1());
	 * Assert.assertEquals('A',
	 * pedfd.getAllEntries().get(0).getBialleles().get(0).getAllele2());
	 * Assert.assertEquals('G',
	 * pedfd.getAllEntries().get(0).getBialleles().get(1).getAllele1());
	 * Assert.assertEquals('T',
	 * pedfd.getAllEntries().get(0).getBialleles().get(1).getAllele2());
	 * 
	 * Assert.assertEquals('A',
	 * pedfd.getAllEntries().get(1).getBialleles().get(0).getAllele1());
	 * Assert.assertEquals('C',
	 * pedfd.getAllEntries().get(1).getBialleles().get(0).getAllele2());
	 * Assert.assertEquals('T',
	 * pedfd.getAllEntries().get(1).getBialleles().get(1).getAllele1());
	 * Assert.assertEquals('G',
	 * pedfd.getAllEntries().get(1).getBialleles().get(1).getAllele2());
	 * 
	 * Assert.assertEquals('C',
	 * pedfd.getAllEntries().get(5).getBialleles().get(0).getAllele1());
	 * Assert.assertEquals('C',
	 * pedfd.getAllEntries().get(5).getBialleles().get(0).getAllele2());
	 * Assert.assertEquals('T',
	 * pedfd.getAllEntries().get(5).getBialleles().get(1).getAllele1());
	 * Assert.assertEquals('T',
	 * pedfd.getAllEntries().get(5).getBialleles().get(1).getAllele2());
	 * 
	 * Assert.assertEquals("3", pedfd.getAllEntries().get(2).getFamily());
	 * Assert.assertEquals("0", pedfd.getAllEntries().get(2).getFather());
	 * Assert.assertEquals("0", pedfd.getAllEntries().get(2).getMother());
	 * Assert.assertEquals(1.0, pedfd.getAllEntries().get(2).getPhenotype());
	 * Assert.assertEquals("1", pedfd.getAllEntries().get(2).getIndividual());
	 * Assert.assertEquals(1, pedfd.getAllEntries().get(2).getSex());
	 * 
	 * Assert.assertEquals("3", pedfd.getEntries(2, 3).get(0).getFamily());
	 * Assert.assertEquals('G', pedfd.getEntries(2,
	 * 3).get(0).getBialleles().get(1).getAllele2()); Assert.assertEquals("3",
	 * pedfd.getEntries(0, 3).get(2).getFamily()); Assert.assertEquals('G',
	 * pedfd.getEntries(0, 3).get(2).getBialleles().get(1).getAllele1());
	 * Assert.assertEquals("4", pedfd.getEntries(2, 4).get(1).getFamily());
	 * Assert.assertEquals(2.0, pedfd.getEntries(2, 4).get(1).getPhenotype());
	 * 
	 * Assert.assertEquals('C', pedfd.getEntries(0,
	 * 6).get(4).getBialleles().get(0).getAllele1()); Assert.assertEquals('C',
	 * pedfd.getEntries(1, 6).get(3).getBialleles().get(0).getAllele2());
	 * Assert.assertEquals('G', pedfd.getEntries(2,
	 * 6).get(2).getBialleles().get(1).getAllele1()); Assert.assertEquals('T',
	 * pedfd.getEntries(3, 6).get(1).getBialleles().get(1).getAllele2());
	 * 
	 * }
	 * 
	 * @Test public void MAP_getEntries() throws Exception {
	 * Assert.assertEquals(1, mapfd.getEntries(0, 1).size());
	 * Assert.assertEquals(1, mapfd.getEntries(1, 2).size());
	 * Assert.assertEquals(2, mapfd.getEntries(0, 2).size());
	 * Assert.assertEquals(2, mapfd.getAllEntries().size());
	 * Assert.assertEquals("snp1", mapfd.getEntries(0, 1).get(0).getSNP());
	 * Assert.assertEquals(0.0, mapfd.getEntries(1, 2).get(0).getcM());
	 * Assert.assertEquals("1", mapfd.getEntries(1, 2).get(0).getChromosome());
	 * Assert.assertEquals(1, mapfd.getAllEntries().get(0).getBpPos());
	 * Assert.assertEquals(2, mapfd.getAllEntries().get(1).getBpPos());
	 * Assert.assertEquals("snp2", mapfd.getAllEntries().get(1).getSNP()); }
	 * 
	 * @Test public void BIM_writer() throws Exception { File newBim = new
	 * File(testBim.getAbsolutePath().replace(testBim.getName(), "new.bim"));
	 * BimFileWriter w = new BimFileWriter(newBim);
	 * w.writeAll(bimfd.getAllEntries()); boolean filesAreEqual =
	 * DirectoryCompare.compareFileContent(testBim, newBim);
	 * Assert.assertTrue(filesAreEqual); }
	 * 
	 * @Test public void MAP_writer() throws Exception { File newMap = new
	 * File(testMap.getAbsolutePath().replace(testMap.getName(), "new.map"));
	 * MapFileWriter w = new MapFileWriter(newMap);
	 * w.writeAll(mapfd.getAllEntries()); boolean filesAreEqual =
	 * DirectoryCompare.compareFileContent(testMap, newMap);
	 * Assert.assertTrue(filesAreEqual); }
	 * 
	 * @Test public void PED_writer() throws Exception { File newPed = new
	 * File(testPed.getAbsolutePath().replace(testPed.getName(), "new.ped"));
	 * PedFileWriter w = new PedFileWriter(newPed);
	 * w.writeAll(pedfd.getAllEntries()); boolean filesAreEqual =
	 * DirectoryCompare.compareFileContent(testPed, newPed);
	 * Assert.assertTrue(filesAreEqual); }
	 * 
	 * @Test public void FAM_writer() throws Exception { File newFam = new
	 * File(testFam.getAbsolutePath().replace(testFam.getName(), "new.fam"));
	 * FamFileWriter w = new FamFileWriter(newFam);
	 * w.writeAll(famfd.getAllEntries()); boolean filesAreEqual =
	 * DirectoryCompare.compareFileContent(testFam, newFam);
	 * Assert.assertTrue(filesAreEqual); }
	 * 
	 * @Test public void TPED_writer() throws Exception { File newTped = new
	 * File(testTped.getAbsolutePath().replace(testTped.getName(), "new.tped"));
	 * TpedFileWriter w = new TpedFileWriter(newTped);
	 * w.writeAll(tpedfd.getAllEntries()); boolean filesAreEqual =
	 * DirectoryCompare.compareFileContent(testTped, newTped);
	 * Assert.assertTrue(filesAreEqual); }
	 * 
	 * @AfterClass public void close() throws IOException { bimfd.close();
	 * famfd.close(); pedfd.close(); mapfd.close(); tpedfd.close(); }
	 * 
	 * private boolean stringArrEqual(String[] arr1, String[] arr2) { if
	 * (arr1.length != arr2.length) { return false; } for (int i = 0; i <
	 * arr1.length; i++) { if (arr1[i] == null && arr2[i] == null) { // equal }
	 * else if (!arr1[i].equals(arr2[i])) { for (int s = 0; s < arr1.length;
	 * s++) { System.out.println(arr1[s] + " vs " + arr2[s]); } return false; }
	 * } return true; }
	 */
}
