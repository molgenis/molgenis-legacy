package qtltogff.sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import bintocsv.sources.BinaryDataMatrixInstance;

import csvtobin.sources.CsvFileReader;


public class QtlToGFFConverter
{
	Map<String, Marker> markers;
	Map<String, Trait> traits;
	Map<String, Chromosome> chromosomes;
	
	public QtlToGFFConverter(File matrixFile, File chromosomeFile, File markerFile, File traitFile, boolean trait_has_bppos, boolean cumu_trait_bppos, boolean cumu_marker_bppos, double lod_thres, double lod_drop, double lod_limit, File output) throws Exception
	{
		chromosomes = loadChromosomes(chromosomeFile);
		markers = loadMarkers(markerFile);
		traits = loadTraits(traitFile);
		
		BinaryDataMatrixInstance instance = new BinaryDataMatrixInstance(matrixFile);
		
		List<String> colNames = instance.getColNames();
		List<String> rowNames = instance.getRowNames();

	
		//TODO...
		
		//perform checks: figure out if markers are on rows of cols, account for all trait/marker names in rows/cols (must all match 100%)
		
		//get row/colnames from markers/traits and only iterate over those - report unmatched entries in matrix
		
		//take chromosome order and bp lengths into account
		
		//parse through the matrix and perform peak detection
		//maybe simple at first, so just significant regions and not proper dropoff peak detect..
		
		//append intervals to GFF file
		
		//etc
		
	}
	
	public Map<String, Marker> loadMarkers(File markerFile) throws FileNotFoundException
	{
		Map<String, Marker> markers = new HashMap<String, Marker>();
		Scanner s = new Scanner(markerFile);
		while (s.hasNextLine()){
			String line = s.nextLine();
		    String[] split = line.split("\t");
		    Marker m = new Marker();
		    m.setName(split[0]);
		    m.setBpPos(Long.parseLong(split[1]));
		    m.setChromosomeName(split[2]);
		    markers.put(split[0], m);
		}
		System.out.println("Read in markers");
		return markers;
	}
	
	public Map<String,Chromosome> loadChromosomes(File chromosomeFile) throws FileNotFoundException
	{
		Map<String, Chromosome> chromosomes = new HashMap<String, Chromosome>();
		Scanner s = new Scanner(chromosomeFile);
		while (s.hasNextLine()){
			String line = s.nextLine();
		    String[] split = line.split("\t");
		    Chromosome c = new Chromosome();
		    c.setName(split[0]);
		    c.setGffName(split[1]);
		    c.setBpLength(Long.parseLong(split[2]));
		    c.setOrderNr(Short.parseShort(split[3]));
		    chromosomes.put(split[0], c);
		}
		System.out.println("Read in chromosomes");
		return chromosomes;
	}
	
	public Map<String, Trait> loadTraits(File traitFile) throws FileNotFoundException
	{
		Map<String, Trait> traits = new HashMap<String, Trait>();
		Scanner s = new Scanner(traitFile);
		while (s.hasNextLine()){
			String line = s.nextLine();
		    String[] split = line.split("\t");
		    Trait t = new Trait();
		    t.setName(split[0]);
		    t.setBpPos(Long.parseLong(split[1]));
		    t.setChromosomeName(split[2]);
		    traits.put(split[0], t);
		}
		System.out.println("Read in traits");
		return traits;
	}
}
