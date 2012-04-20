package qtltogff.sources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import bintocsv.sources.BinaryDataMatrixInstance;


public class QtlToGFFConverter
{
	LinkedHashMap<String, Marker> markers; //must be in correct order of ascending bp/chromosomes!
	Map<String, Trait> traits;
	Map<String, Chromosome> chromosomes;
	
	public QtlToGFFConverter(File matrixFile, File chromosomeFile, File markerFile, File traitFile, boolean trait_has_bppos, boolean cumu_trait_bppos, boolean cumu_marker_bppos, double lod_thres, double lod_drop, double lod_limit, File output, boolean verbose) throws Exception
	{
		System.out.println("##########\n" +
		"QtlToGFF conversion starting with arguments:\n"+
		" - Data matrix file: "+matrixFile.getAbsolutePath()+"\n"+
		" - Chromosome annotation file: "+chromosomeFile.getAbsolutePath()+"\n"+
		" - Marker file: "+markerFile.getAbsolutePath()+"\n"+
		" - Trait file: "+traitFile.getAbsolutePath()+"\n"+
		" - Boolean (a): trait has genomic locations: "+trait_has_bppos+"\n"+
		" - Boolean (b): trait basepair positions are cumulative: "+cumu_trait_bppos+"\n"+
		" - Boolean (c): marker basepair positions are cumulative: "+cumu_marker_bppos+"\n"+
		" - LOD threshold: "+lod_thres+"\n"+
		" - LOD drop: "+lod_drop+"\n"+
		" - LOD limit: "+lod_limit+"\n" +
		" - Boolean (d): verbose: "+verbose+"\n"+
		"##########");
		
		
		//load supplied annotation files
		chromosomes = loadChromosomes(chromosomeFile);
		markers = loadMarkers(markerFile);
		traits = loadTraits(traitFile);
		
		//calculate chromosome bp deduction amounts of we have cumulative annotations
		if(cumu_trait_bppos || cumu_marker_bppos)
		{
			for(String chr : chromosomes.keySet())
			{
				chromosomes.get(chr).setCumuBpDeductionAmount(getCumuBpDeductionAmount(chr));
				System.out.println("Deduction amount for " + chr + " is: " + chromosomes.get(chr).getCumuBpDeductionAmount());
			}
		}
		
		
		//create matrix instance and get row/col names
		BinaryDataMatrixInstance instance = new BinaryDataMatrixInstance(matrixFile);
		List<String> colNames = instance.getColNames();
		List<String> rowNames = instance.getRowNames();
		System.out.println("Matrix instance created and row/colnames retrieved..");
		
		//find out if markers are on rows or columns
		boolean markersAreOnRows;
		if(instance.getData().getFeatureType().equals("Marker"))
		{
			System.out.println("Markers are on the columns");
			markersAreOnRows = false;
		}
		else if(instance.getData().getTargetType().equals("Marker"))
		{
			System.out.println("Markers are on the rows");
			markersAreOnRows = true;
		}
		else
		{
			throw new Exception("Either matrix columns or rows must be of type 'Marker'. (marker subclasses such as SNP not supported at the moment)");
		}
		
		//assign lists of trait & marker names now we know where they are
		List<String> traitsInMatrix = new ArrayList<String>();
		List<String> markersInMatrix = new ArrayList<String>();
		if(markersAreOnRows)
		{
			traitsInMatrix = colNames;
			markersInMatrix = rowNames;
		}
		else
		{
			traitsInMatrix = rowNames;
			markersInMatrix = colNames;
		}
		
		//check if values are decimals
		if(instance.getData().getValueType().equals("Decimal"))
		{
			System.out.println("Matrix contains decimal values..");
		}
		else
		{
			throw new Exception("Matrix does NOT contain decimal values!");
		}
		
		//iterate traits and check overlap with traits in matrix
		List<String> traitsNotInFile = new ArrayList<String>();
		List<String> traitsNotInMatrix = new ArrayList<String>();
		for(String traitInFile : traits.keySet())
		{
			if(!traitsInMatrix.contains(traitInFile))
			{
				traitsNotInMatrix.add(traitInFile);
			}
		}
		for(String traitInMatrix : traitsInMatrix)
		{
			if(!traits.keySet().contains(traitInMatrix))
			{
				traitsNotInFile.add(traitInMatrix);
			}
		}
		if(traitsNotInFile.size() > 0)
		{
			System.out.println("\nWARNING: The following "+traitsNotInFile.size()+" traits were found in the matrix, but not in your annotation file:");
			for(String t : traitsNotInFile)
			{
				System.out.print(t + " ");
			}
			System.out.print("\n");
		}
		if(traitsNotInMatrix.size() > 0)
		{
			System.out.println("\nWARNING: The following "+traitsNotInMatrix.size()+" traits were found in the annotation file, but not in your matrix:");
			for(String t : traitsNotInMatrix)
			{
				System.out.print(t + " ");
			}
			System.out.print("\n");
		}
		
		//iterate markers and check overlap with matrix in matrix
		List<String> markersNotInFile = new ArrayList<String>();
		List<String> markersNotInMatrix = new ArrayList<String>();
		for(String markerInFile : markers.keySet())
		{
			if(!markersInMatrix.contains(markerInFile))
			{
				markersNotInMatrix.add(markerInFile);
			}
		}
		for(String markerInMatrix : markersInMatrix)
		{
			if(!markers.keySet().contains(markerInMatrix))
			{
				markersNotInFile.add(markerInMatrix);
			}
		}
		if(markersNotInFile.size() > 0)
		{
			System.out.println("\nFATAL: The following "+markersNotInFile.size()+" markers were found in the matrix, but not in your annotation file:");
			for(String m : markersNotInFile)
			{
				System.out.print(m + " ");
			}
			System.out.print("\n");
			throw new Exception("All markers in the matrix must be accounted for. Quitting..");
		}
		if(markersNotInMatrix.size() > 0)
		{
			System.out.println("\nWARNING: The following "+markersNotInMatrix.size()+" markers were found in the annotation file, but not in your matrix:");
			for(String m : markersNotInMatrix)
			{
				System.out.print(m + " ");
			}
			System.out.print("\n");
		}
		
		//iterate markers in matrix and check if they are in ascending chromosomes
		//TODO
		
		//iterate markers in matrix and check if they are in ascending bp position as well
		//TODO
		
		//iterate markers in file and check if they are in ascending chromosomes
		//TODO
		
		//iterate markers in file and check if they are in ascending bp position as well
		//TODO
		
		//create peak detector
		PeakDetection pd = new PeakDetection(markers, markersInMatrix, verbose);
		
		//iterate traits and detect peaks
		System.out.println("Now going to iterate over the traits in your annotation file and detect peaks..\n");
		Map<String, List<GffEntry>> entriesPerTrait = new HashMap<String, List<GffEntry>>();
		for(final String t : traits.keySet())
		{
			if(traitsNotInMatrix.contains(t))
			{
				System.out.println("Skipping " + t + " because it is not in matrix.. (see warning above)");
				continue;
			}
			
			Object[] values;
			
			if(markersAreOnRows)
			{
				values = instance.getCol(colNames.indexOf(t));
			}
			else
			{
				values = instance.getRow(rowNames.indexOf(t)); //as in example set & verified
			}
			
			
			List<Peak> peaks = pd.detectPeaks(values, lod_thres, lod_drop, t);
			List<GffEntry> entries = new ArrayList<GffEntry>();
			
			int multiPeakCount = 0;
			for(Peak p : peaks)
			{
				//String traitGroup = t + "_" + multiPeakCount;
				
				if(markers.get(p.getLeftFlankMarker()).getBpPos().longValue() > markers.get(p.getRightFlankMarker()).getBpPos().longValue())
				{
					throw new Exception("No valid interval for " + t + ", start bp exceeds stop bp!");
				}
				
				String leftFlankingMarker = p.getLeftFlankMarker();
				String rightFlankingMarker = p.getRightFlankMarker();
				
				long leftBpPos = markers.get(leftFlankingMarker).getBpPos();
				long rightBpPos = markers.get(rightFlankingMarker).getBpPos();
				
				if(!markers.get(leftFlankingMarker).getChromosomeName().equals(markers.get(rightFlankingMarker).getChromosomeName()))
				{
					throw new Exception("FATAL: QTL region found crossing two chromosomes in trait " + t + ". Marker " + leftFlankingMarker + " is on chr " + markers.get(leftFlankingMarker).getChromosomeName() + " while marker " + rightFlankingMarker + " is on chr " + markers.get(rightFlankingMarker).getChromosomeName());
				}
				
				if(cumu_marker_bppos)
				{
					leftBpPos = leftBpPos - chromosomes.get(markers.get(leftFlankingMarker).getChromosomeName()).getCumuBpDeductionAmount();
					rightBpPos = rightBpPos - chromosomes.get(markers.get(rightFlankingMarker).getChromosomeName()).getCumuBpDeductionAmount();
				}
				
				String peakMarkerChromosomeGffName = chromosomes.get(markers.get(p.getPeakMarker()).getChromosomeName()).getGffName();

				int score;
				if(p.getPeakValue().doubleValue() > lod_limit)
				{
					score = 1000;
				}
				else
				{
					//scale until limit: 0->limit = 0->1000
					score = (int) (1000.0 * (p.getPeakValue().doubleValue() / lod_limit));
				}
				
				String traitGroup = t;
				if(peaks.size() > 1)
				{
					traitGroup = t + "_" + multiPeakCount;
				}
				
				GffEntry qtl_interval = new GffEntry(peakMarkerChromosomeGffName, "QtlToGFF", "qtl_interval", leftBpPos, rightBpPos, score, traitGroup);
				entries.add(qtl_interval);
				
				if(trait_has_bppos)
				{
					long traitLoc = traits.get(t).getBpPos();
					if(cumu_trait_bppos)
					{
						traitLoc = traitLoc - chromosomes.get(traits.get(t).getChromosomeName()).getCumuBpDeductionAmount();
					}
					String traitChromosomeGffName = chromosomes.get(traits.get(t).getChromosomeName()).getGffName();
					
					if(!peakMarkerChromosomeGffName.equals(traitChromosomeGffName))
					{
						traitGroup = traitGroup + "_has_transqtl_on_" + peakMarkerChromosomeGffName;
					}
					
					GffEntry trait_loc = new GffEntry(traitChromosomeGffName, "QtlToGFF", "trait_loc", traitLoc, traitLoc+30, score, traitGroup);
					entries.add(trait_loc);
				}
					
				multiPeakCount++;
			}
			
			if(entries.size() > 0){
				entriesPerTrait.put(t, entries);
			}
			
		}
		
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output)));
		
		String chr1 = ((Chromosome)chromosomes.values().toArray()[0]).getGffName();
		Long chr1size = ((Chromosome)chromosomes.values().toArray()[0]).getBpLength();
		String trackname = matrixFile.getName().substring(0, matrixFile.getName().length()-4);
		writer.println("browser position "+chr1+":1-"+chr1size);
		//writer.println("browser hide all");
		writer.println("track name="+trackname+" description=\"QTL results of "+trackname+" compiled by QtlToGFF\" visibility=3 useScore=1");

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		
		writer.println("##name "+trackname);
		writer.println("##description QTL results of "+trackname+" compiled by QtlToGFF");
		writer.println("##date "+dateFormat.format(date));
		writer.println("##gff-version 2");
		writer.println("##source QtlToGFF, see: http://www.molgenis.org/svn/standalone_tools/src/qtltogff/");
		
		int total = 0;
		for(String tr : entriesPerTrait.keySet())
		{
			total += entriesPerTrait.get(tr).size();
			for(GffEntry g: entriesPerTrait.get(tr))
			{
				writer.println(g);
			}
		}
		System.out.println(total + " GFF entries written to file" + output.getAbsolutePath());
		
		writer.close();
		
	}
	
	public long getCumuBpDeductionAmount(String chromosomeName) throws Exception
	{
		Chromosome chr = chromosomes.get(chromosomeName);
		List<Short> orderNrsMatched = new ArrayList<Short>();
		long amount = 0;
		for(short orderNr = 1; orderNr < chr.getOrderNr(); orderNr++)
		{
			boolean match = false;
			for(String matchMe : chromosomes.keySet())
			{
				if(chromosomes.get(matchMe).getOrderNr().shortValue() == orderNr)
				{
					if(orderNrsMatched.contains(orderNr)){
						throw new Exception("Duplicate order number "+orderNr+" in chromosomes for name " + matchMe);
					}
					orderNrsMatched.add(orderNr);
					amount += chromosomes.get(matchMe).getBpLength();
					match = true;
				}
			}
			if(!match)
			{
				throw new Exception("No chromosome found for expected order nr " + orderNr);
			}
		}
		return amount;
	}
	
	public LinkedHashMap<String, Marker> loadMarkers(File markerFile) throws Exception
	{
		LinkedHashMap<String, Marker> markers = new LinkedHashMap<String, Marker>();
		Scanner s = new Scanner(markerFile);
		while (s.hasNextLine()){
		    String line = null;
		    try
			{
		    	line = s.nextLine();
			    String[] split = line.split("\t");
			    Marker m = new Marker();
			    m.setName(split[0]);
			    m.setBpPos(Long.parseLong(split[1]));
			    m.setChromosomeName(split[2]);
			    markers.put(split[0], m);
			}
			catch(Exception e)
			{
				System.out.println("ERROR in line '"+line+"'");
				throw new Exception(e);
			}
		    
		}
		System.out.println("Read in markers");
		return markers;
	}
	
	public Map<String,Chromosome> loadChromosomes(File chromosomeFile) throws Exception
	{
		Map<String, Chromosome> chromosomes = new HashMap<String, Chromosome>();
		Scanner s = new Scanner(chromosomeFile);
		while (s.hasNextLine()){
			String line = null;
		    try
			{
		    	line = s.nextLine();
			    String[] split = line.split("\t");
			    Chromosome c = new Chromosome();
			    c.setName(split[0]);
			    c.setGffName(split[1]);
			    c.setBpLength(Long.parseLong(split[2]));
			    c.setOrderNr(Short.parseShort(split[3]));
			    chromosomes.put(split[0], c);
			}
			catch(Exception e)
			{
				System.out.println("ERROR in line '"+line+"'");
				throw new Exception(e);
			}
		}
		System.out.println("Read in chromosomes");
		return chromosomes;
	}
	
	public Map<String, Trait> loadTraits(File traitFile) throws Exception
	{
		Map<String, Trait> traits = new HashMap<String, Trait>();
		Scanner s = new Scanner(traitFile);
		while (s.hasNextLine()){
			String line = null;
			try
			{
				line = s.nextLine();
			    String[] split = line.split("\t");
			    Trait t = new Trait();
			    t.setName(split[0]);
			    t.setBpPos(Long.parseLong(split[1]));
			    t.setChromosomeName(split[2]);
			    traits.put(split[0], t);
			}
			catch(Exception e)
			{
				System.out.println("ERROR in line '"+line+"'");
				throw new Exception(e);
			}
		}
		System.out.println("Read in traits");
		return traits;
	}
}
