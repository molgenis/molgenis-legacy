package org.molgenis.util.vcf;

public interface VcfReaderListener
{
	public void handleLine(int lineNumber, VcfRecord record) throws Exception;
}
