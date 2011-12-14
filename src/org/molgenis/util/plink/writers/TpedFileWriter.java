package org.molgenis.util.plink.writers;

import java.io.File;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.MapEntry;
import org.molgenis.util.plink.datatypes.TpedEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class TpedFileWriter
{
	private CsvFileWriter writer;
	
	public TpedFileWriter(File tpedFile) throws Exception
	{
		writer = new CsvFileWriter(tpedFile);
		writer.setHeaders(TpedEntry.tpedHeader());
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
	public void writeSingle(TpedEntry tped){
		writer.writeRow(TpedEntry.tpedToTuple(tped));
	}
	
	/**
	 * Write multiple entries in order.
	 */
	public void writeMulti(List<TpedEntry> tpeds){
		for(TpedEntry tped : tpeds){
			writer.writeRow(TpedEntry.tpedToTuple(tped));
		}
	}
	
	/**
	 * Write all entries and close the writer.
	 */
	public void writeAll(List<TpedEntry> tpeds){
		for(TpedEntry tped : tpeds){
			writer.writeRow(TpedEntry.tpedToTuple(tped));
		}
		writer.close();
	}
	
}
