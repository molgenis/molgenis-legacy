package org.molgenis.util.plink.writers;

import java.io.File;
import java.util.List;

import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.plink.datatypes.MapEntry;

/**
 * Write MAP file entries to a selected location.
 */
public class MapFileWriter
{
	private CsvFileWriter writer;
	
	public MapFileWriter(File mapFile) throws Exception
	{
		writer = new CsvFileWriter(mapFile);
		writer.setHeaders(MapEntry.mapHeader());
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
	public void writeSingle(MapEntry map){
		writer.writeRow(MapEntry.mapToTuple(map));
	}
	
	/**
	 * Write multiple entries in order.
	 */
	public void writeMulti(List<MapEntry> maps){
		for(MapEntry map : maps){
			writer.writeRow(MapEntry.mapToTuple(map));
		}
	}
	
	/**
	 * Write all entries and close the writer.
	 */
	public void writeAll(List<MapEntry> maps){
		for(MapEntry map : maps){
			writer.writeRow(MapEntry.mapToTuple(map));
		}
		writer.close();
	}
	
}
