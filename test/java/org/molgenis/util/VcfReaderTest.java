package org.molgenis.util;


/**
 * Non-validating parser for the TSV like VCF 'Variant Calling Format'.
 * 
 * TODO: enable parsing of INFO, FILTER and FORMAT headers
 * 
 * See http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-
 * variant-call-format-version-40
 */
public class VcfReaderTest
{
	// TODO this test fails, fix it
	/*
	 * @BeforeClass public void setUp() { BasicConfigurator.configure(); }
	 * 
	 * Logger logger = Logger.getLogger(VcfReaderTest.class);
	 * 
	 * @Test public void test1() throws Exception { File f = new
	 * File("/Users/mswertz/test.vcf");
	 * 
	 * VcfReader reader = new VcfReader(f);
	 * 
	 * logger.debug("headers in "+f.getAbsolutePath()); for(String header:
	 * reader.getMetaData()) { logger.debug(header); }
	 * 
	 * 
	 * logger.debug("columns in "+f.getAbsolutePath()); for(String header:
	 * reader.getColumnHeaders()) { logger.debug(header); }
	 * 
	 * logger.debug("info in "+f.getAbsolutePath()); for(VcfInfo info:
	 * reader.getInfos()) { logger.debug(info); }
	 * 
	 * logger.debug("filter in "+f.getAbsolutePath()); for(VcfFilter filter:
	 * reader.getFilters()) { logger.debug(filter); }
	 * 
	 * logger.debug("format in "+f.getAbsolutePath()); for(VcfFormat format:
	 * reader.getFormats()) { logger.debug(format); }
	 * 
	 * //parse first 10 records reader.parse(new VcfReaderListener(){
	 * 
	 * @Override public void handleLine(int lineNumber, VcfRecord record) throws
	 * Exception { logger.debug(record); if(lineNumber > 10) return; } }); }
	 */
}
