package org.molgenis.patho;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.Panel;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.vcf.VcfInfo;
import org.molgenis.util.vcf.VcfReader;
import org.molgenis.util.vcf.VcfReaderListener;
import org.molgenis.util.vcf.VcfRecord;
import org.molgenis.variant.SequenceFeature;

/**
 * This method can extract cohort level or individual level data from vcf files
 * and convert this into a pheno model compatible data set.
 * 
 * Currently, only aggregate data is exported as observedvalue.
 */
public class ConvertVcfToPheno
{
	// buffer of variants
	List<SequenceFeature> variants = new ArrayList<SequenceFeature>();

	// buffer of variant features
	List<ObservableFeature> features = new ArrayList<ObservableFeature>();

	// buffer of individuals
	List<Individual> individuals = new ArrayList<Individual>();

	// cohort
	Panel cohort = null;

	// List of observations should go into a matrix???

	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();

		File vcfFile = null, outputDir = null;

		if (args.length == 2)
		{

		}
		else
		{
			vcfFile = new File("/Users/mswertz/test.vcf");
			outputDir = new File("/Users/mswertz/test");
		}

		ConvertVcfToPheno convert = new ConvertVcfToPheno();
		convert.convertVariants(vcfFile, outputDir);
	}

	public void convertVariants(File vcfFile, File outputDir) throws Exception
	{
		System.out.println("converting aggregate data from vcf=" + vcfFile
				+ " to directory " + outputDir);
		VcfReader vcf = new VcfReader(vcfFile);

		final List<SequenceFeature> variants = new ArrayList<SequenceFeature>();
		
		int maxAlleles = vcf.getSampleList().size() * 2;

		vcf.parse(new VcfReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, VcfRecord record)
					throws Exception
			{
				SequenceFeature v = new SequenceFeature();
				//create hgvs notation
				v.setName("chr" + record.getChrom() + ":g." + record.getPos()
						+ record.getRef() + ">" + toCsv(record.getAlt()));
				
				// ref
				v.setRef(record.getRef());
				
				// alt
				v.setAlt(toCsv(record.getAlt()));
				
				// chr
				v.setChr_Name(record.getChrom());
				
				// pos
				v.setStartBP(record.getPos());
				
				// dbrefs name
				if (record.getId().size() > 0 && !".".equals(record.getId().get(0)))
				{
					v.setDbRefs_Name(record.getId());
				}

				//put alt allele counts in description
				v.setDescription(record.getInfo("AC").toString());
				
				variants.add(v);

			}
		});
		
		this.write(variants, outputDir, new String[]{SequenceFeature.NAME, SequenceFeature.CHR_NAME, SequenceFeature.STARTBP, SequenceFeature.REF, SequenceFeature.ALT, SequenceFeature.DESCRIPTION, SequenceFeature.DBREFS_NAME});
	}

	private String toCsv(List<String> values)
	{
		String result = "";
		boolean first = true;
		for (String val : values)
		{
			if (first)
			{
				result += val;
				first = false;
			}
			else
			{
				result += "," + val;
			}
		}
		return result;
	}

	public void convertAggregateData(File vcfFile, File outputDir)
			throws IOException
	{
		System.out.println("converting aggregate data from vcf=" + vcfFile
				+ " to directory " + outputDir);
		VcfReader vcf = new VcfReader(vcfFile);

		// first extract the features and write to outputDir
		for (VcfInfo format : vcf.getInfos())
		{
			ObservableFeature feature = new ObservableFeature();
			feature.setName(format.getId());
			feature.setDescription(format.getDescription());
			// TODO: format.getNumber() format.getType()
			features.add(feature);
		}

		write(features, outputDir, new String[]
		{ "name", "description" });

	}

	private void append(List<? extends Entity> entities, File directory,
			String[] fields) throws IOException
	{
		this.write(entities, directory, fields, true);
	}

	private void write(List<? extends Entity> entities, File directory,
			String[] fields) throws IOException
	{
		this.write(entities, directory, fields, false);
	}

	private void write(List<? extends Entity> entities, File directory,
			String[] fields, boolean append) throws IOException
	{
		if (entities.size() > 0)
		{
			// create filename
			File file = new File(directory.getAbsolutePath() + File.separatorChar
					+ entities.get(0).getClass().getSimpleName().toLowerCase()
					+ ".txt");
			
			System.out.println("Writing to "+file);

			// create csvWriter using the selected headers
			CsvWriter writer = new CsvFileWriter(file, Arrays.asList(fields),
					append);

			// if !append write headers
			if (!append) writer.writeHeader();

			// write all to csv
			for (Entity e : entities)
			{
				writer.writeRow(e);
			}
			
			writer.close();

		}
	}

}
