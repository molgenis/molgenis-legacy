package org.molgenis.util.plink.writers;

import java.io.File;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.BimEntry;

/**
 * Write BIM file entries to a selected location.
 */
public class BimFileWriter
{
	private CsvFileWriter writer;
	
	public BimFileWriter(File bimFile) throws Exception
	{
		writer = new CsvFileWriter(bimFile);
		writer.setHeaders(BimEntry.bimHeader());
	}
	
	/**
	 * Close the underlying writer.
	 */
	public void close(){
		writer.close();
	}
	
	/**
	 * Write a single entry.
	 */
	public void writeSingle(BimEntry bim){
		writer.writeRow(BimEntry.bimToTuple(bim));
	}
	
	/**
	 * Write multiple entries in order.
	 */
	public void writeMulti(List<BimEntry> bims){
		for(BimEntry bim : bims){
			writer.writeRow(BimEntry.bimToTuple(bim));
		}
	}
	
	/**
	 * Write all entries and close the writer.
	 */
	public void writeAll(List<BimEntry> bims){
		for(BimEntry bim : bims){
			writer.writeRow(BimEntry.bimToTuple(bim));
		}
		writer.close();
	}
	
}
