package qtltogff.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeakDetection {

	private Map<String, Marker> markers;
	
	private char pr_plus = '+';
	private char pr_minus = '-';
	private char pr_equal = '=';
	private char pr_below = ' ';
	
	public PeakDetection(Map<String, Marker> markers)
	{
		this.markers = markers;
	}
	
	public List<Peak> detectPeaks(Object[] values, List<String> markersInMatrix, double lod_thres, double lod_drop, String traitName) throws Exception
	{
		List<Peak> peaks = new ArrayList<Peak>();
		
		
		Map<String, List<Double>> valuesPerChr = new HashMap<String, List<Double>>();
		
		
		
		String[] DEBUG_chromos = new String[values.length];
		

		
		//cut up values to smaller lists per chromosome
		List<Double> valuesForThisChr = new ArrayList<Double>();
		//as a start value, assign the chromosome of the first marker
		String curChr = markers.get(markersInMatrix.get(0)).getChromosomeName();
		for(int i = 0; i < values.length; i++)
		{
			double d = ((Double)values[i]).doubleValue();
			//on chromosome switch: add current values to map
			if(!curChr.equals(markers.get(markersInMatrix.get(i)).getChromosomeName()))
			{
				valuesPerChr.put(curChr, valuesForThisChr);
				valuesForThisChr = new ArrayList<Double>();
				curChr = markers.get(markersInMatrix.get(i)).getChromosomeName();
			}
			valuesForThisChr.add(d);
			//last element: add the last chromosome to the map
			if(i == values.length-1)
			{
				valuesPerChr.put(curChr, valuesForThisChr);
			}
		}
		
		
		for(String key : valuesPerChr.keySet())
		{
			int highestPosInThisChr = -1;
			double highestValue = -1.0;
			
			for(int i = 0; i < valuesPerChr.get(key).size(); i++)
			{
				double d = valuesPerChr.get(key).get(i);

				if(d > highestValue)
				{
					highestPosInThisChr = i;
					highestValue = d;
				}
			}
			
			//if we have at least 1 peak in this chromosome..
			if(highestValue > lod_thres)
			{
				System.out.println(traitName + " highest in chr " + key + " is " + highestPosInThisChr + " value " + highestValue);
				
				for(Double v : valuesPerChr.get(key))
				{
					System.out.print(v + "\t");
				}
				System.out.println();
				
				for(int i = 0; i < valuesPerChr.get(key).size(); i++)
				{
					System.out.print(i + "\t");
				}
				
				System.out.println();
				
				//find left flanking marker
				int leftDropoffIndex = -1;
				int leftQtlRegionEnd = -1;
				
				int pointer = highestPosInThisChr;
				while(leftQtlRegionEnd == -1)
				{
					if(highestPosInThisChr > 0)
					{
						pointer--;
						if(valuesPerChr.get(key).get(pointer) < lod_thres)
						{
							leftQtlRegionEnd = pointer;
						}
						else if(valuesPerChr.get(key).get(pointer) < (highestValue - lod_drop))
						{
							leftDropoffIndex = pointer;
						}
					}
					else
					{
						leftDropoffIndex = 0;
						leftQtlRegionEnd = 0;
					}
				}
				
				int rightDropoffIndex = -1;
				int rightQtlRegionEnd = -1;
				
				pointer = highestPosInThisChr;
				while(rightQtlRegionEnd == -1)
				{
					if(highestPosInThisChr < valuesPerChr.get(key).size())
					{
						pointer++;
						if(valuesPerChr.get(key).get(pointer) < lod_thres)
						{
							rightQtlRegionEnd = pointer;
						}
						else if(valuesPerChr.get(key).get(pointer) < (highestValue - lod_drop))
						{
							rightDropoffIndex = pointer;
						}
					}
					else
					{
						rightDropoffIndex = 0;
						rightQtlRegionEnd = 0;
					}
				}
				
				
				
				System.out.println("left dropoff: " + leftDropoffIndex);
				System.out.println("right dropoff: " + rightDropoffIndex);
				System.out.println("left region: " + leftQtlRegionEnd);
				System.out.println("right region: " + rightQtlRegionEnd);
				System.out.println();
				
			}
		
			
		}
		
		
		/**
		
		for(String key : valuesPerChr.keySet())
		{
			boolean DEBUG_peakfound = false;
			char[] profile = new char[valuesPerChr.get(key).size()];
			boolean inRegion = false;
			
			for(int i = 0; i < valuesPerChr.get(key).size(); i++)
			{
				double d = valuesPerChr.get(key).get(i);
				if(d > lod_thres)
				{
					if(inRegion)
					{
						if(valuesPerChr.get(key).get(i-1) < d)
						{
							profile[i] = pr_plus;
						}
						else if(valuesPerChr.get(key).get(i-1) == d)
						{
							profile[i] = pr_equal;
						}
						else
						{
							profile[i] = pr_minus;
						}
					}
					else
					{
						profile[i] = pr_plus;
						inRegion = true;
						DEBUG_peakfound = true;
					}
				}
				else
				{
					profile[i] = pr_below;
				}
			}
			

			if(DEBUG_peakfound){
				System.out.println(traitName + " has one or more peaks on chromosome " + key + ":");
				for(char c : profile)
				{
					System.out.print(c + "\t");
				}
				System.out.println();
				
				for(Double v : valuesPerChr.get(key))
				{
					System.out.print(v + "\t");
				}
				System.out.println();
				
				for(int i = 0; i < valuesPerChr.get(key).size(); i++)
				{
					System.out.print(i + "\t");
				}
				
				System.out.println();
				
				
				
				String[] peak = new String[valuesPerChr.get(key).size()];
				
				boolean insidePeak = false;
				boolean peakDropped = false;
				double highestInPeak = -1.0;
				int highestInPeakPos = -1;
				for(int i = 0; i < valuesPerChr.get(key).size(); i++)
				{
					double d = valuesPerChr.get(key).get(i);
					
					//detect start of peak
					if(!insidePeak && profile[i] == pr_plus)
					{
						insidePeak = true;
						highestInPeak = d;
						highestInPeakPos = i;
						peak[i] = "start";
						//on the last marker: assign flank/peak, not start
						if(i == valuesPerChr.get(key).size()-1)
						{
							peak[i] = "F/peak";
						}
						//on the first marker: assign flank/start
						else if(i==0)
						{
							peak[i] = "start";
						}
						continue;
					}
					
					//detect end of peak by threshold
					if(insidePeak && (profile[i] == pr_below || i == valuesPerChr.get(key).size()-1))
					{
						if(peakDropped)
						{
							peak[i] = "end";
						}
						else
						{
							peak[i] = "F/end";
						}
						peak[highestInPeakPos] = "peak";
						insidePeak = false;
						continue;
					}
					
					//detect end of peak by dropoff
					if(insidePeak && !peakDropped && d < (highestInPeak - lod_drop))
					{
						peak[i] = "F/drop";
						peak[highestInPeakPos] = "peak";
						peakDropped = true;
						continue;
					}
					
					if(insidePeak)
					{
						peak[i] = "-->";
						if(d > highestInPeak)
						{
							highestInPeak = d;
							highestInPeakPos = i;
						//	peak[i] = "insNH";
						}
						continue;
					}
					
					peak[i] = "";
					
				}
				
				for(int p = 0; p < peak.length; p++)
				{
					System.out.print(peak[p] + "\t");
				}
				System.out.println();
				
				System.out.println();
				
				int[][] jaap = new int[1][1];
				jaap[0] = new int[]{1};
			}
			
			}
			
			*/

		
		
		return peaks;
	}
	
	public List<Peak> detectPeaks_(Object[] values, List<String> markersInMatrix, double lod_thres) throws Exception
	{
		
		List<Peak> peaks = new ArrayList<Peak>();
		
		//naive: just find regions over cutoff and pick flanking markers
		//TODO: proper peak detection, e.g. waterfill
		
		int peakStart = -1;
		int peakMarker = -1;
		double highestMarkerValue = -1.0;
		
		double prev = -1.0;
		
		char[] profile = new char[values.length];
		String[] chromos = new String[values.length];
		
		for(int i = 0; i < values.length; i++)
		{
		
			double d = ((Double)values[i]).doubleValue();
			
			profile[i] = 'o';
			
			chromos[i] =  markers.get(markersInMatrix.get(i)).getChromosomeName();
			
			//start of a peak
			if(peakStart == -1 && d > lod_thres)
			{
				peakStart = i;
				prev = i==0 ? ((Double)values[i]).doubleValue() : ((Double)values[i-1]).doubleValue();
				profile[i==0?i:i-1] = 'F';
				
				highestMarkerValue = d;
				peakMarker = i;
			}
			
			//inside a peak: find the highest marker
			if(peakStart != -1)
			{
				if(d > prev)
				{
					profile[i] = pr_plus;
				}
				else if(d == prev)
				{
					profile[i] = pr_below;
				}
				else{
					profile[i] = pr_minus;
				}
				prev = d;
				
				if(d > highestMarkerValue)
				{
					highestMarkerValue = d;
					peakMarker = i;
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
				
				
				
				System.out.println("Peak : " + p.getStartIndex() + " to " + p.getStopIndex() + " peakmarker = " + p.getPeakMarker());
				peakStart = -1;
				highestMarkerValue = -1.0;
			}
			
		}
		
		if(peaks.size() > 0)
		{
		//	String s = new String(profile);
		//	System.out.println(s);
			
			for(char c : profile)
			{
				System.out.print(c + "\t");
			}
			System.out.println();
			
			for(Object v : values)
			{
				double d = ((Double)v).doubleValue();
				System.out.print(d + "\t");
			}
			System.out.println();
			
			for(String ch : chromos)
			{
				System.out.print(ch + "\t");
			}
			System.out.println();
			
			for(int i = 0; i < values.length; i++)
			{
				System.out.print(i + "\t");
			}
			
			System.out.println();
			System.out.println();
		}
		
		
		return peaks;
		
	}
	
	
}
