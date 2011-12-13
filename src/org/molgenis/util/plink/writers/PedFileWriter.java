package org.molgenis.util.plink.writers;

import java.io.File;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.MapEntry;
import org.molgenis.util.plink.datatypes.PedEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class PedFileWriter
{
	private CsvFileWriter writer;
	
	public PedFileWriter(File pedFile) throws Exception
	{
		writer = new CsvFileWriter(pedFile);
		writer.setHeaders(PedEntry.pedHeader());
		writer.setSeparator(" ");
		writer.setListSeparator(" ");
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
	public void writeSingle(PedEntry ped){
		writer.writeRow(PedEntry.pedToTuple(ped));
	}
	
	/**
	 * Write multiple entries in order.
	 */
	public void writeMulti(List<PedEntry> peds){
		for(PedEntry ped : peds){
			writer.writeRow(PedEntry.pedToTuple(ped));
		}
	}
	
	/**
	 * Write all entries and close the writer.
	 */
	public void writeAll(List<PedEntry> peds){
		for(PedEntry ped : peds){
			writer.writeRow(PedEntry.pedToTuple(ped));
		}
		writer.close();
	}
	
}
