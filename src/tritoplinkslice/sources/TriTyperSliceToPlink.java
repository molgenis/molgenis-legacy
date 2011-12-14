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
	private File tpedDest;
	private File tfamDest;
	private String[] individuals;
	private String[] markers;
	Map<String,Double> individualsAndPhenotypes;
	
	//the subselection (indices) of String[] individuals
	private int[] individualsToBeSelected;

	public TriTyperSliceToPlink(TriTyperGenotypeData data, File tpedDest, File tfamDest, File slice) throws Exception
	{
		this.data = data;
		this.tpedDest = tpedDest;
		this.tfamDest = tfamDest;
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
		
		//get them again from the file - since they have now all been removed
		//not the most efficient but not the most costly process either..
		individualsAndPhenotypes = readSliceFile(slice);
	}
	
	private Map<String,Double> readSliceFile(File f) throws FileNotFoundException
	{
		Scanner s = new Scanner(f);
		Map<String,Double> result = new HashMap<String,Double>();
		while (s.hasNextLine()){
			String line = s.nextLine();
		    String[] indivPlusPheno = line.split("\t");
		    String indiv = indivPlusPheno[0];
		    double pheno = Double.parseDouble(indivPlusPheno[1]);
		    result.put(indiv, pheno);
		}
		s.close();
		return result;
	}

	public boolean makeTPEDAndFAM() throws Exception
	{

		//create and write the TFAM file (format equivalent to FAM, but belonging to a TPED)
		FamFileWriter ffw = new FamFileWriter(tfamDest);
		
		for (int i = 0; i < individualsToBeSelected.length; i++)
		{
			int index = individualsToBeSelected[i];
			
			String famId = "LIFELINES";
			String indId = individuals[index];
			String father = "NA";
			String mother = "NA";
			byte sex = (byte) (data.getIsFemale()[index] != null ? (data.getIsFemale()[index] == true ? 2 : 1) : 0);
			double phenoType = individualsAndPhenotypes.get(indId);
			
			FamEntry fe = new FamEntry(famId, indId, father, mother, sex, phenoType);
			
			ffw.writeSingle(fe);
		}
		
		ffw.close();
		
		//create and write the TPED file
		//see: Transposed filesets @ http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml
		TpedFileWriter tfw = new TpedFileWriter(tpedDest);
		
		SNPLoader loader = data.createSNPLoader();
		for (int m = 0; m < markers.length; m++)
		{
			SNP snpObject = data.getSNPObject(m);
			loader.loadGenotypes(snpObject);
			
			String chr = snpObject.getChr() + "";
			String name = snpObject.getName();
			double cM = 0.0;
			long bpPos = snpObject.getChrPos();
			
			//the list of bialleles iterates over the INDIVIDUALS and not SNP'S as is the case with regular PED files!
			List<Biallele> bialleles = new ArrayList<Biallele>();
			
			byte[] all1 = snpObject.getAllele1();
			byte[] all2 = snpObject.getAllele2();
			
			for (int i = 0; i < individualsToBeSelected.length; i++)
			{
				int index = individualsToBeSelected[i];

				Biallele b = new Biallele((char)all1[index], (char)all2[index]);
				bialleles.add(b);
			}
			
			TpedEntry pe = new TpedEntry(chr, name, cM, bpPos, bialleles);
			tfw.writeSingle(pe);
		}
		
		tfw.close();
		
		return true;
	}
	

}
