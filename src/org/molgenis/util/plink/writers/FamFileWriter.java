package org.molgenis.util.plink.writers;

import java.io.File;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.FamEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class FamFileWriter
{
	private CsvFileWriter writer;
	
	public FamFileWriter(File famFile) throws Exception
	{
		writer = new CsvFileWriter(famFile);
		writer.setHeaders(FamEntry.famHeader());
		writer.setSeparator(" ");
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
	public void writeSingle(FamEntry fam){
		writer.writeRow(FamEntry.famToTuple(fam));
	}
	
	/**
	 * Write multiple entries in order.
	 */
	public void writeMulti(List<FamEntry> fams){
		for(FamEntry fam : fams){
			writer.writeRow(FamEntry.famToTuple(fam));
		}
	}
	
	/**
	 * Write all entries and close the writer.
	 */
	public void writeAll(List<FamEntry> fams){
		for(FamEntry fam : fams){
			writer.writeRow(FamEntry.famToTuple(fam));
		}
		writer.close();
	}
	
}
