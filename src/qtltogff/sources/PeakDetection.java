package qtltogff.sources;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PeakDetection {

	private LinkedHashMap<String, Marker> allMarkers;
	private List<String> markersInMatrix;
	private Map<String, List<String>> markersPerChr;
	private boolean verbose;
	static DecimalFormat twoDForm = new DecimalFormat("#.##");
	
	public PeakDetection(LinkedHashMap<String, Marker> allMarkers, List<String> markersInMatrix, boolean verbose)
	{
		this.allMarkers = allMarkers;
		this.markersInMatrix = markersInMatrix;
		this.verbose = verbose;
		markersPerChr = new HashMap<String, List<String>>();
		
		System.out.println();
		for(String key : allMarkers.keySet()){
			
			Marker m = allMarkers.get(key);
			
			//important: ignore the ones not in the matrix!
			if(markersInMatrix.contains(m.getName()))
			{
				List<String> markerList = null;
				
				if(markersPerChr.containsKey(m.getChromosomeName()))
				{
					markerList = markersPerChr.get(m.getChromosomeName());
				}
				else
				{
					markerList = new ArrayList<String>();
	 			}
				markerList.add(m.getName());
				markersPerChr.put(m.getChromosomeName(), markerList);
			}
		}
		
		for(String key : markersPerChr.keySet())
		{
			System.out.println("Markers on chr "+ key);
			for(String m : markersPerChr.get(key))
			{
				System.out.print(" " + m);
			}
			System.out.println();
			System.out.println();
		}
		
	}
	
	public List<Peak> detectPeaks(Object[] values, double lod_thres, double lod_drop, String traitName) throws Exception
	{
		List<Peak> peaks = new ArrayList<Peak>();
		Map<String, List<Double>> valuesPerChr = new HashMap<String, List<Double>>();
		
		//cut up values to smaller lists per chromosome
		List<Double> valuesForThisChr = new ArrayList<Double>();
		
		//as a start value, assign the chromosome of the first marker
		String curChr = allMarkers.get(markersInMatrix.get(0)).getChromosomeName();
		for(int i = 0; i < values.length; i++)
		{
			double d;
			if(values[i] == null)
			{
				d = 0.0;
				System.out.println("WARNING: Missing value at marker " + allMarkers.get(markersInMatrix.get(i)) + ", replacing by 0.0");
			}
			else
			{
				d = ((Double)values[i]).doubleValue();
			}
			//on chromosome switch: add current values to map
			if(!curChr.equals(allMarkers.get(markersInMatrix.get(i)).getChromosomeName()))
			{
				valuesPerChr.put(curChr, valuesForThisChr);
				valuesForThisChr = new ArrayList<Double>();
				curChr = allMarkers.get(markersInMatrix.get(i)).getChromosomeName();
			}
			valuesForThisChr.add(d);
			//last element: add the last chromosome to the map
			if(i == values.length-1)
			{
				valuesPerChr.put(curChr, valuesForThisChr);
			}
		}
		
		//iterate over chromosomes for this trait
		for(String key : valuesPerChr.keySet())
		{
			//assume there is a peak
			boolean hasPeak = true;
			
			//continue scanning for peaks until there are no more (though typically only 1 or 2)
			int iteration = 1;
			while(hasPeak)
			{
				//set to false unless one is found
				hasPeak = false;
				
				//scan the profile for the highest value and the position of this value
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

			
				//a significant highest value was found, now detect the region and dropoff
				if(highestValue > lod_thres)
				{
					hasPeak = true;
					
					if(verbose) System.out.println("------- " + traitName + " on chr " + key + ", peak "+iteration+" -------");
					if(verbose) System.out.println("profile:");
					iteration++;
					
					//find left dropoff / descent
					int leftDropoff = -1;
					int leftDescentRegion = -1;
					
					int pointer = highestPosInThisChr;
					while(leftDescentRegion == -1)
					{
						//System.out.println("WHILE POINTER LEFT: " + pointer + " VALUE: " + valuesPerChr.get(key).get(pointer));
						if(pointer == 0)
						{
							leftDescentRegion = pointer;
							break;
						}
						
						pointer--;
						
						if(valuesPerChr.get(key).get(pointer) <= (valuesPerChr.get(key).get(pointer+1)))
						{
							if(valuesPerChr.get(key).get(pointer) < lod_thres)
							{
								leftDescentRegion = pointer;
							}
							else if(valuesPerChr.get(key).get(pointer) < (highestValue - lod_drop) && leftDropoff == -1)
							{
								leftDropoff = pointer;
							}
						}
						else
						{
	//						System.out.println("********* LEFT DESCENT REGION END BEFORE END OF QTL REGION (>THRESHOLD) ********* ");
	//						System.out.println("p\t" + pointer + "\t" + valuesPerChr.get(key).get(pointer));
	//						System.out.println("p+1\t" + (pointer+1) + "\t"+ (valuesPerChr.get(key).get(pointer+1)));
							leftDescentRegion = pointer+1;
						}
					}
					
					//find right dropoff / descent
					int rightDropoff = -1;
					int rightDescentRegion = -1;
					
					pointer = highestPosInThisChr;
					while(rightDescentRegion == -1)
					{
						//System.out.println("WHILE POINTER RIGHT: " + pointer + " VALUE: " + valuesPerChr.get(key).get(pointer));
						if(pointer == valuesPerChr.get(key).size()-1)
						{
							rightDescentRegion = pointer;
							break;
						}
					
						pointer++;
						
						if(valuesPerChr.get(key).get(pointer) <= (valuesPerChr.get(key).get(pointer-1)))
						{
							if(valuesPerChr.get(key).get(pointer) < lod_thres)
							{
								rightDescentRegion = pointer;
							}
							else if(valuesPerChr.get(key).get(pointer) < (highestValue - lod_drop) && rightDropoff == -1)
							{
								rightDropoff = pointer;
							}
						}
						else
						{
	//						System.out.println("********* RIGHT DESCENT REGION END BEFORE END OF QTL REGION (>THRESHOLD) ********* ");
	//						System.out.println("p\t" + pointer + "\t" + valuesPerChr.get(key).get(pointer));
	//						System.out.println("p-1\t" + (pointer-1) + "\t"+ (valuesPerChr.get(key).get(pointer-1)));
							rightDescentRegion = pointer-1;
						}
					}
					
					if(verbose){
						for(Double v : valuesPerChr.get(key))
						{
							System.out.print(Double.valueOf(twoDForm.format(v)) + "\t");
						}
						System.out.println();
						
						for(int i = 0; i < valuesPerChr.get(key).size(); i++)
						{
							System.out.print(i + "\t");
						}
						
						System.out.println();
						System.out.println();
						System.out.println("highest value is " + highestValue + " at position " + highestPosInThisChr);
						System.out.println("left dropoff: " + leftDropoff);
						System.out.println("right dropoff: " + rightDropoff);
						System.out.println("left descent region: " + leftDescentRegion);
						System.out.println("right descent region: " + rightDescentRegion);
						System.out.println();
					}
					
					int start;
					int stop;
					if(leftDropoff != -1 && leftDropoff > leftDescentRegion)
					{
						start = leftDropoff;
						
					}else
					{
						start = leftDescentRegion;
					}
						
					if(rightDropoff != -1 && rightDropoff < rightDescentRegion)
					{
						stop = rightDropoff;
					}else
					{
						stop = rightDescentRegion;
						
					}
					
					if(verbose){
						System.out.println("left flanking marker put at: " + start + ", which is " + markersPerChr.get(key).get(start));
						System.out.println("right flanking marker put at: " + stop + ", which is " + markersPerChr.get(key).get(stop));
						System.out.println("peak marker put at: " + highestPosInThisChr + ", which is " + markersPerChr.get(key).get(highestPosInThisChr));
						System.out.println("zeroing region: " + leftDescentRegion + " to " + rightDescentRegion);
						System.out.println();
					}
					
					Peak p = new Peak(markersPerChr.get(key).get(start), markersPerChr.get(key).get(stop), markersPerChr.get(key).get(highestPosInThisChr), highestValue);
					peaks.add(p);
					
					for(int i = leftDescentRegion; i <= rightDescentRegion; i++)
					{
						valuesPerChr.get(key).set(i, 0.0);
					}
						
				}
			}
		}
		return peaks;
	}
}
