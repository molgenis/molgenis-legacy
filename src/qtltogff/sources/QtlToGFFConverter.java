package qtltogff.sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import bintocsv.sources.BinaryDataMatrixInstance;


public class QtlToGFFConverter
{
	Map<String, Marker> markers;
	Map<String, Trait> traits;
	Map<String, Chromosome> chromosomes;
	
	public QtlToGFFConverter(File matrixFile, File chromosomeFile, File markerFile, File traitFile, boolean trait_has_bppos, boolean cumu_trait_bppos, boolean cumu_marker_bppos, double lod_thres, double lod_drop, double lod_limit, File output) throws Exception
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
				System.out.println("deduction amount for " + chr + " is: " + chromosomes.get(chr).getCumuBpDeductionAmount());
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
		
		//iterate traits and detect peaks
		System.out.println("Now going to iterate over the traits in your annotation file and detect peaks..");
		Map<String, List<GffEntry>> entriesPerTrait = new HashMap<String, List<GffEntry>>();
		for(String t : traits.keySet())
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
			
			List<Peak> peaks = detectPeaks(values, markersInMatrix, lod_thres);
			List<GffEntry> entries = new ArrayList<GffEntry>();
			
			int peakCount = 0;
			for(Peak p : peaks)
			{
				String traitGroup = t + "_" + peakCount;
				
				if(p.getStartIndex() == p.getStopIndex())
				{
					throw new Exception("No interval for " + t + ", start and stop is both index " + p.getStartIndex());
				}
				
				String leftFlankingMarker = markersInMatrix.get(p.getStartIndex());
				String rightFlankingMarker = markersInMatrix.get(p.getStopIndex());
				
				long leftBpPos = markers.get(leftFlankingMarker).getBpPos();
				long rightBpPos = markers.get(rightFlankingMarker).getBpPos();
				
				if(!markers.get(leftFlankingMarker).getChromosomeName().equals(markers.get(rightFlankingMarker).getChromosomeName()))
				{
					throw new Exception("FATAL: QTL region found crossing two chromosomes in trait " + t);
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
				else{
					score = (int) ((lod_limit / p.getPeakValue().doubleValue()) * 100);
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
						traitGroup = t + "_"+peakCount+"_has_transqtl_"+peakCount+"_on_" + peakMarkerChromosomeGffName;
					}
					
					GffEntry trait_loc = new GffEntry(traitChromosomeGffName, "QtlToGFF", "trait_loc", traitLoc, traitLoc+30, score, traitGroup);
					entries.add(trait_loc);
				}
					
				peakCount++;
			}
			
			if(entries.size() > 0){
				entriesPerTrait.put(t, entries);
			}
			
			
			
//			System.out.println("values for trait " + t);
//			
//			for(Object v : values)
//			{
//				System.out.print(v + " ");
//			}
//			System.out.print("\n");
			

		}
		
		int total = 0;
		for(String tr : entriesPerTrait.keySet())
		{
			total += entriesPerTrait.get(tr).size();
			for(GffEntry g: entriesPerTrait.get(tr))
			{
				System.out.println(g.toString());
			}
		}
		System.out.println("Total amount of GFF entries written: " + total);
		
		
		
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
	
	public List<Peak> detectPeaks(Object[] values, List<String> markersInMatrix, double lod_thres) throws Exception
	{
		List<Peak> peaks = new ArrayList<Peak>();
		
		//naive: just find regions over cutoff and pick flanking markers
		//TODO: proper peak detection, e.g. waterfill
		
		int peakStart = -1;
		int peakMarker = -1;
		double highestMarkerValue = -1.0;
		
		for(int i = 0; i < values.length; i++)
		{
			double d = ((Double)values[i]).doubleValue();
			
			//start of a peak
			if(peakStart == -1 && d > lod_thres)
			{
				peakStart = i;
			}
			
			//inside a peak: find the highest marker
			if(peakStart != -1)
			{
				if(highestMarkerValue == -1.0)
				{
					highestMarkerValue = d;
					peakMarker = i;
				}
				else
				{
					if(d > highestMarkerValue)
					{
						highestMarkerValue = d;
						peakMarker = i;
					}
				}
			}
			
			//inside a peak: finish when LOD drops below threshold, or we reached the last value
			if(peakStart != -1 && (d < lod_thres || i == values.length-1))
			{
				
				//adjust for flanking marker: start -1 if possible for left flank (i is always 1 ahead anyway for right flank)
				Peak p = new Peak(peakStart != 0 ? peakStart-1 : peakStart, i, markersInMatrix.get(peakMarker), highestMarkerValue);
				
				String leftFlankMarkerChr = markers.get(markersInMatrix.get(p.getStartIndex())).getChromosomeName();
				String rightFlankMarkerChr = markers.get(markersInMatrix.get(p.getStopIndex())).getChromosomeName();
				
				//the flanking (+1 and -1 markers around region) can be across chromosomes by accident..
				if(!leftFlankMarkerChr.equals(rightFlankMarkerChr))
				{
					String leftFlankMarkerChrPlusOne = markers.get(markersInMatrix.get(p.getStartIndex()+1)).getChromosomeName();
					String rightFlankMarkerChrMinusOne = markers.get(markersInMatrix.get(p.getStopIndex()-1)).getChromosomeName();
				
					//left flank marker is wrongly positioned another chromosome
					if(leftFlankMarkerChrPlusOne.equals(rightFlankMarkerChr))
					{
						p.setStartIndex(p.getStartIndex()+1);
//						System.out.println("Corrected a QTL region by pushing the left flanking marker back to the right chromosome..");
					}
					//right flank marker is wrongly positioned another chromosome
					else if(rightFlankMarkerChrMinusOne.equals(leftFlankMarkerChr))
					{
						p.setStopIndex(p.getStopIndex()-1);
//						System.out.println("Corrected a QTL region by pulling the right flanking marker back to the right chromosome..");
					}
					else{
						throw new Exception("Cannot make sense of this QTL region.. aborting");
					}
					
				}
				
				peaks.add(p);
				
//				System.out.println("Peak : " + p.getStartIndex() + " to " + p.getStopIndex() + " peakmarker = " + p.getPeakMarker());
				peakStart = -1;
				highestMarkerValue = -1.0;
			}
			
		}
		
//		if(peaks.size() > 0)
//		{
//			System.out.println("peaks were found in these values:");
//			for(Object v : values)
//			{
//				System.out.print(v + " ");
//			}
//			System.out.print("\n");
//		}
		
		return peaks;
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
