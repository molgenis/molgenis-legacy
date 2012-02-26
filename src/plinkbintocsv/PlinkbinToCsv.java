package plinkbintocsv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import plinkbintocsv.sources.BedFileDriver;
import plinkbintocsv.sources.BimEntry;
import plinkbintocsv.sources.BimFileDriver;
import plinkbintocsv.sources.FamFileDriver;
import tritoplinkslice.sources.Biallele;
import tritoplinkslice.sources.FamEntry;

/**
 * Tool to convert Plink binary to Observ-OM CSV
 * See http://pngu.mgh.harvard.edu/~purcell/plink/binary.shtml
 * Output:
 * - 'matrix' of individuals (columns) x snp (rows) in {basename}_genotypes.txt
 * - SNP annotations in {basename}_snps.txt
 * - Individual annotations in {basename}_individuals.txt
 * @author joerivandervelde
 *
 */
public class PlinkbinToCsv {

	public static void main(String[] args) throws Exception
	{
		if(args.length != 1){
			throw new Exception("You must supply 1 argument: the base name of your Plink files. For example: you supply the name 'mydata'. This will make " +
					"the tool look for these 3 files: mydata.bim, mydata.bed and mydata.fam. The output files may NOT yet exist and are called: " +
					"mydata_individuals.txt, mydata_snps.txt, mydata_genotypes.txt");
		}
		
		//get args
		String baseFileName = args[0];
		
		//print args
		System.out.println("PlinkbinToCsv called with argument:");
		System.out.println("base file name = " + baseFileName);
		
		//check if the expected files exist
		File bimFile = new File(baseFileName + ".bim");
		if (bimFile == null || !bimFile.exists()){
			throw new Exception("Expected source file '"+bimFile.getName()+"' not found at location '"+bimFile.getAbsolutePath()+"'");
		}
		File bedFile = new File(baseFileName + ".bed");
		if (bedFile == null || !bedFile.exists()){
			throw new Exception("Expected source file '"+bedFile.getName()+"' not found at location '"+bedFile.getAbsolutePath()+"'");
		}
		File famFile = new File(baseFileName + ".fam");
		if (famFile == null || !famFile.exists()){
			throw new Exception("Expected source file '"+famFile.getName()+"' not found at location '"+famFile.getAbsolutePath()+"'");
		}
		
		System.out.println("Source files OK, now checking output files..");
		
		//check if output files not exist
		File individuals = new File(baseFileName + "_individuals.txt");
		if(individuals.exists())
		{
			throw new Exception("Output file '" + individuals.getName() + "' already present at " +
					"location '"+individuals.getAbsolutePath()+"', please (re)move it and restart the tool.");
		}
		
		File snps = new File(baseFileName + "_snps.txt");
		if(snps.exists())
		{
			throw new Exception("Output file '" + snps.getName() + "' already present at " +
					"location '"+snps.getAbsolutePath()+"', please (re)move it and restart the tool.");
		}
		
		File genotypes = new File(baseFileName + "_genotypes.txt");
		if(snps.exists())
		{
			throw new Exception("Output file '" + genotypes.getName() + "' already present at " +
					"location '"+genotypes.getAbsolutePath()+"', please (re)move it and restart the tool.");
		}
		
		System.out.println("Output files OK, now creating file drivers");
		
		//instantiate file drivers on top of the source file
		BimFileDriver bimfd = new BimFileDriver(bimFile);
		BedFileDriver bedfd = new BedFileDriver(bedFile);
		FamFileDriver famfd = new FamFileDriver(famFile);
		
		//check for mode: we can only parse SNP-major for now (which is the default)
		if(bedfd.getMode() == 0)
		{
			throw new Exception("Individual-major mode parsing of BED files is currently not supported!");
		}
		
		System.out.println("File drivers created and BED file mode OK, now working on FAM file..");
		
		//write the individuals file
		//FIXME: stream from file to file instead of reading everything at once? this is memory intensive
		long nrOfIndividuals = famfd.getNrOfElements();
		List<String> individualNames = new ArrayList<String>();
		List<FamEntry> famEntries = famfd.getAllEntries();
		//extra check on dimensions
		if(famEntries.size() != nrOfIndividuals)
		{
			throw new Exception("Problem with FAM file: scanned number of elements does not match number of parsed elements");
		}
		
		Writer individualsOut = new OutputStreamWriter(new FileOutputStream(individuals), "UTF-8");
		individualsOut.write("name\tdescription\n");
		
		for(FamEntry fe : famEntries)
		{
			String line = fe.getIndividual() + "\t";
			line += "fam:" + fe.getFamily() + ",";
			line += "dad:" + fe.getFather() + ",";
			line += "mom:" + fe.getMother() + ",";
			line += "sex:" + fe.getSex() + ",";
			line += "phe:" + fe.getPhenotype() + "\n";
			individualsOut.write(line);
			if(individualNames.contains(fe.getIndividual()))
			{
				throw new Exception("Problem with FAM file: Individual '" + fe.getIndividual() +"' is not unique!");
			}
			individualNames.add(fe.getIndividual());
		}
		individualsOut.close();
		
		System.out.println("FAM file done, now working on BIM file..");
		
		//remember the snp encoding for parsing binary format later on
		HashMap<String, Biallele> snpCoding = new HashMap<String, Biallele>();
		List<String> snpNames = new ArrayList<String>();
		
		//write the SNP file
		//FIXME: stream from file to file instead of reading everything at once? this is memory intensive
		long nrOfSnps = bimfd.getNrOfElements();
		List<BimEntry> bimEntries = bimfd.getAllEntries();
		//extra check on dimensions
		if(bimEntries.size() != nrOfSnps)
		{
			throw new Exception("Problem with BIM file: scanned number of elements does not match number of parsed elements");
		}
		
		Writer snpsOut = new OutputStreamWriter(new FileOutputStream(snps), "UTF-8");
		snpsOut.write("name\tchromosome_name\tbpstart\tcm\n");
		
		for(BimEntry be : bimEntries)
		{
			String line = be.getSNP() + "\t";
			line += be.getChromosome() + "\t";
			line += be.getBpPos() + "\t";
			line += be.getcM() + "\n";
			snpsOut.write(line);
			if(snpCoding.containsKey(be.getSNP()))
			{
				throw new Exception("Problem with BIM file: SNP '" + be.getSNP() +"' is not unique!");
			}
			snpCoding.put(be.getSNP(), be.getBiallele());
			snpNames.add(be.getSNP());
		}
		snpsOut.close();
		bimEntries = null;
		
		System.out.println("BIM file done, now working on BED file..");
		
		//now write the genotype matrix
		
		//check: genotypes must be equal to individuals x snps
		long nrOfGenotypes = bedfd.getNrOfElements();
		if(nrOfIndividuals * nrOfSnps != nrOfGenotypes)
		{
			throw new Exception("Problem with BED file: Number of genotypes (" + nrOfGenotypes + ") is not equal to the number of individuals (" + nrOfIndividuals + ") times number of SNPs ("+nrOfSnps+")");
		}  
		
		Writer genotypesOut = new OutputStreamWriter(new FileOutputStream(genotypes), "UTF-8");
		
		///header: all individual names
		for(String indvName : individualNames)
		{
			genotypesOut.write("\t" + indvName);
		}
		genotypesOut.write("\n");
		
		//elements: snp name + genotypes
		int snpCounter = 0;
		for(int individual = 0; individual < nrOfGenotypes; individual += nrOfIndividuals)
		{
			System.out.print(".");
			
			String snpName = snpNames.get(snpCounter);
			String[] allIndividualsForThisSNP = bedfd.getElements(individual, individual + nrOfIndividuals);
			String a1 = Character.toString(snpCoding.get(snpName).getAllele1());
			String a2 = Character.toString(snpCoding.get(snpName).getAllele2());
			String hom1 = a1+a1;
			String hom2 = a2+a2;
			String hetr = a1+a2;
			
			String lineOfGenotypes = snpName;
			for(String s : allIndividualsForThisSNP)
			{
				lineOfGenotypes += "\t" + bedfd.convertGenoCoding(s, hom1, hom2, hetr, "");
			}
			genotypesOut.write(lineOfGenotypes + "\n");
			
			snpCounter++;
		}
		
		genotypesOut.close();
		
		System.out.println("\nConversion complete.");
		
	}

}
