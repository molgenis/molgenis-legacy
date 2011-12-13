package tritoplinkslice.sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import tritobin.sources.SNP;
import tritobin.sources.SNPLoader;
import tritobin.sources.TriTyperGenotypeData;



public class TriTyperSliceToPlink
{
	private TriTyperGenotypeData data;
	private File pedDest;
	private File mapDest;
	private File slice;
	private String[] individuals;
	private String[] markers;
	Map<String,Double> individualsAndPhenotypes;
	
	//the subselection (indices) of String[] individuals
	private int[] individualsToBeSelected;

	public TriTyperSliceToPlink(TriTyperGenotypeData data, File pedDest, File mapDest, File slice) throws Exception
	{
		this.data = data;
		this.pedDest = pedDest;
		this.mapDest = mapDest;
		this.slice = slice;
		this.individuals = data.getIndividuals();
		this.markers = data.getSNPs();
		
		//read individuals from slice.txt and match against individuals in TriTyper
		individualsAndPhenotypes = readSliceFile(slice);
		individualsToBeSelected = new int[individualsAndPhenotypes.size()];
		
		int selectIndex = 0;
		for (int i = 0; i < individuals.length; i++)
		{
			if(individualsAndPhenotypes.keySet().contains(individuals[i]))
			{
				individualsToBeSelected[selectIndex] = i;
				selectIndex++;
				individualsAndPhenotypes.remove(individuals[i]);
			}
		}
		
		//check if all individuals in the slice file were selected and indexed
		if(individualsAndPhenotypes.size() > 0)
		{
			System.err.println("Not matched:");
			for(String s : individualsAndPhenotypes.keySet())
			{
				System.err.println(s);
			}
			throw new Exception("ERROR: Not all individuals in your slice file were matched. See above.");
		}
	}
	
	private Map<String,Double> readSliceFile(File f) throws FileNotFoundException
	{
		Scanner s = new Scanner(f);
		Map<String,Double> result = new HashMap<String,Double>();
		while (s.hasNext()){
		    String[] indivPlusPheno = s.next().split("\t");
		    String indiv = indivPlusPheno[0];
		    double pheno = Double.parseDouble(indivPlusPheno[1]);
		    result.put(indiv, pheno);
		}
		s.close();
		return result;
	}
	
	

	public boolean makePedAndMap() throws Exception
	{
		// TODO Auto-generated method stub
		
		MapFileWriter mfw = new MapFileWriter(mapDest);
		List<MapEntry> mapEntries = new ArrayList<MapEntry>();
		
		PedFileWriter pfw = new PedFileWriter(pedDest);
		List<PedEntry> pedEntries = new ArrayList<PedEntry>();
		
		

		
		mfw.writeMulti(mapEntries);
		
		
		pfw.writeMulti(pedEntries);
		
		
		mfw.close();
		pfw.close();
		
		
		
		
		SNPLoader loader = data.createSNPLoader();
		for (int m = 0; m < markers.length; m++)
		{
			SNP snpObject = data.getSNPObject(m);
			loader.loadGenotypes(snpObject);
			
			String chr = (char)snpObject.getChr() + "";
			String name = snpObject.getName();
			double cM = 0.0;
			long bpPos = snpObject.getChrPos();
			
			MapEntry me = new MapEntry(chr, name, cM, bpPos);
			
			
			byte[] all1 = snpObject.getAllele1();
			byte[] all2 = snpObject.getAllele2();
			
			for (int i = 0; i < individualsToBeSelected.length; i++)
			{
				int index = individualsToBeSelected[i];
				
				
				String famId = "LIFELINES";
				String indIv = individuals[index];
				String father = "NA";
				String mother = "NA";
				byte sex = (byte) (data.getIsFemale()[index] == true ? 2 : 1);
				double phenoType = individualsAndPhenotypes.get(indIv);
				List<Biallele> bialleles = new ArrayList<Biallele>();
				
		//		PedEntry pe = new PedEntry();
				
				
				//DO THIS:

				//Transposed filesets @ http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml

				
		//		dos.write(all1[index]);
		//		dos.write(all2[index]);
			}
		}
		
		
		
		
		return false;
	}
	

}
