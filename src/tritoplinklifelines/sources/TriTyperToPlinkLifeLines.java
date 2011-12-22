package tritoplinklifelines.sources;

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
import tritoplinkslice.sources.Biallele;
import tritoplinkslice.sources.FamEntry;
import tritoplinkslice.sources.FamFileWriter;
import tritoplinkslice.sources.TpedEntry;
import tritoplinkslice.sources.TpedFileWriter;



public class TriTyperToPlinkLifeLines
{
	private TriTyperGenotypeData data;
	private File tpedDest;
	private File tfamDest;
	private String[] individuals;
	private String[] markers;
	Map<String,PseudoPheno> individualsPseudoAndPhenotypes;
	
	//the subselection (indices) of String[] individuals
	private int[] individualsToBeSelected;

	public TriTyperToPlinkLifeLines(TriTyperGenotypeData data, File tpedDest, File tfamDest, File slicePseudo) throws Exception
	{
		this.data = data;
		this.tpedDest = tpedDest;
		this.tfamDest = tfamDest;
		this.individuals = data.getIndividuals();
		this.markers = data.getSNPs();
		
		//read individuals from slice.txt and match against individuals in TriTyper
		individualsPseudoAndPhenotypes = readSliceFile(slicePseudo);
		individualsToBeSelected = new int[individualsPseudoAndPhenotypes.size()];
		
		int selectIndex = 0;
		for (int i = 0; i < individuals.length; i++)
		{
			if(individualsPseudoAndPhenotypes.keySet().contains(individuals[i]))
			{
				individualsToBeSelected[selectIndex] = i;
				selectIndex++;
				individualsPseudoAndPhenotypes.remove(individuals[i]);
			}
		}
		
		//check if all individuals in the slice file were selected and indexed
		if(individualsPseudoAndPhenotypes.size() > 0)
		{
			System.err.println("Not matched:");
			for(String s : individualsPseudoAndPhenotypes.keySet())
			{
				System.err.println(s);
			}
			throw new Exception("ERROR: Not all individuals in your slice file were matched. See above.");
		}
		
		//get them again from the file - since they have now all been removed
		//not the most efficient but not the most costly process either..
		individualsPseudoAndPhenotypes = readSliceFile(slicePseudo);
	}
	
	private Map<String,PseudoPheno> readSliceFile(File f) throws FileNotFoundException
	{
		Scanner s = new Scanner(f);
		Map<String,PseudoPheno> result = new HashMap<String,PseudoPheno>();
		while (s.hasNextLine()){
			String line = s.nextLine();
		    String[] indivPlusPheno = line.split("\t");
		    String indiv = indivPlusPheno[0];
		    String pseudo = indivPlusPheno[1];
		    double pheno = Double.parseDouble(indivPlusPheno[2]);
		    PseudoPheno p = new PseudoPheno(pheno, pseudo);
		    
		    result.put(indiv, p);
		}
		s.close();
		return result;
	}

	public boolean makeTPEDAndFAM(String makeWhat) throws Exception
	{

		if(makeWhat.equals("F") || makeWhat.equals("B"))
		{
			//create and write the TFAM file (format equivalent to FAM, but belonging to a TPED)
			FamFileWriter ffw = new FamFileWriter(tfamDest);
			
			for (int i = 0; i < individualsToBeSelected.length; i++)
			{
				int index = individualsToBeSelected[i];
				
				String famId = "LIFELINES";
				String indId = individuals[index];
				String pseudo = individualsPseudoAndPhenotypes.get(indId).getPseudo();
				String father = "NA";
				String mother = "NA";
				byte sex = (byte) (data.getIsFemale()[index] != null ? (data.getIsFemale()[index] == true ? 2 : 1) : 0);
				double phenoType = individualsPseudoAndPhenotypes.get(indId).getPheno();
				
				FamEntry fe = new FamEntry(famId, pseudo, father, mother, sex, phenoType);
				
				ffw.writeSingle(fe);
			}
			ffw.close();
		}
		
		if(makeWhat.equals("P") || makeWhat.equals("B"))
		{
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
		}
		
		return true;
	}
	

}
