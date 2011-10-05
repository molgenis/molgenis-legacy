package org.molgenis.patho;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.core.OntologyTerm;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.vcf.VcfReader;
import org.molgenis.util.vcf.VcfReaderListener;
import org.molgenis.util.vcf.VcfRecord;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.SequenceVariant;

/**
 * This method can extract cohort level or individual level data from vcf files
 * and convert this into a pheno model compatible data set.
 * 
 * Currently, only aggregate data is exported as observedvalue.
 */
public class ConvertVcfToPheno {
	public static final int BATCH_SIZE = 10000;

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		File vcfFile = null, outputDir = null;

		if (args.length == 2) {

		} else {
			vcfFile = new File("/Volumes/Scratch/gonl_small_debug_variants.vcf");
			outputDir = new File("/Volumes/Scratch/");
		}

		ConvertVcfToPheno convert = new ConvertVcfToPheno();
		convert.convertVariants(vcfFile, outputDir);
	}

	public void convertVariants(final File vcfFile, final File outputDir)
			throws Exception {
		System.out.println("converting aggregate data from vcf=" + vcfFile
				+ " to directory " + outputDir);
		VcfReader vcf = new VcfReader(vcfFile);

		final List<SequenceVariant> variants = new ArrayList<SequenceVariant>();
		final List<ObservedValue> values = new ArrayList<ObservedValue>();
		final List<String> chromosomes = new ArrayList<String>();
		final List<String> dbXrefs = new ArrayList<String>();

		// create file names
		final File sequenceVariants = new File(outputDir.getAbsolutePath()
				+ File.separatorChar + "SequenceVariant.txt");
		final File observedValues = new File(outputDir.getAbsolutePath()
				+ File.separatorChar + "ObservedValues.txt");

		// create file headers
		final String[] svHeaders = new String[] { SequenceVariant.NAME,
				SequenceVariant.CHR_NAME, SequenceVariant.STARTBP,
				SequenceVariant.ENDBP, SequenceVariant.REF,
				SequenceVariant.ALT, SequenceVariant.DESCRIPTION,
				SequenceVariant.DBREFS_NAME };
		final String[] ovHeaders = new String[] { ObservedValue.TARGET_NAME,
				ObservedValue.RELATION_NAME, ObservedValue.FEATURE_NAME, ObservedValue.VALUE };

		// create files
		createFileAndHeader(sequenceVariants, svHeaders);
		createFileAndHeader(observedValues, ovHeaders);

		final List<Integer> count = new ArrayList<Integer>();
		count.add(0);

		vcf.parse(new VcfReaderListener() {

			@Override
			public void handleLine(int lineNumber, VcfRecord record)
					throws Exception {
				SequenceVariant v = new SequenceVariant();
				ObservedValue o = new ObservedValue();

				// create hgvs notation
				// chr1:g.[12345A>T];[12345A>G]
				if (record.getAlt().size() > 1) {
					String result = "chr" + record.getChrom() + ":g.";
					int i = 0;
					for (String alt : record.getAlt()) {
						if (i++ > 0)
							result += ";";
						result += "[" + record.getPos() + record.getRef() + ">"
								+ alt + "]";
					}
					v.setName(result);
				} else {
					v.setName("chr" + record.getChrom() + ":g."
							+ record.getPos() + record.getRef() + ">"
							+ record.getAlt().get(0));
				}

				// ref
				v.setRef(record.getRef());

				// alt

				v.setAlt(toCsv(record.getAlt()));

				// chr
				v.setChr_Name(record.getChrom());

				if (!chromosomes.contains(record.getChrom()))
					chromosomes.add(record.getChrom());

				// pos
				v.setStartBP(record.getPos());
				v.setEndBP(record.getPos());

				// dbrefs name
				if (record.getId().size() > 0
						&& !".".equals(record.getId().get(0))) {
					v.setDbRefs_Name(record.getId());
					for (String ref : record.getId()) {
						if (!dbXrefs.contains(ref))
							dbXrefs.add(ref);
					}

				}

				// put alt allele counts in description
				o.setValue(record.getInfo("AC").toString());
				// TODO: fetch panel and relation from VCF if possible...
				// and create it first, so we can use it here.
				o.setTarget_Name("goNL");
				o.setRelation_Name("Allele count");
				o.setFeature_Name(v.getName());

				variants.add(v);
				values.add(o);

				if (variants.size() == BATCH_SIZE) {
					writeBatch(variants, sequenceVariants, svHeaders);
					variants.clear();
					writeBatch(values, observedValues, ovHeaders);
					values.clear();

					count.set(0, count.get(0) + BATCH_SIZE);

					System.out.println(new Date() + ":" + count.get(0));
				}
			}
		});

		// write remaining data for last batch.
		writeBatch(variants, sequenceVariants, svHeaders);
		writeBatch(values, observedValues, ovHeaders);

		// write chromsomes
		List<Chromosome> chrList = new ArrayList<Chromosome>();
		for (String chr : chromosomes) {
			Chromosome c = new Chromosome();
			c.setName(chr);
			chrList.add(c);
		}

		// write dbXrefs
		List<OntologyTerm> ontoList = new ArrayList<OntologyTerm>();
		for (String dbXref : dbXrefs) {
			OntologyTerm t = new OntologyTerm();
			t.setName(dbXref);
			ontoList.add(t);
		}

		File chrFile = new File(outputDir.getAbsolutePath()
				+ File.separatorChar + "Chromosome.txt");
		String[] chrHeader = new String[] { "name" };
		createFileAndHeader(chrFile, chrHeader);
		writeBatch(chrList, chrFile, chrHeader);

		File ontoFile = new File(outputDir.getAbsolutePath()
				+ File.separatorChar + "OntologyTerm.txt");
		String[] ontoHeader = new String[] { "name" };
		createFileAndHeader(ontoFile, ontoHeader);
		writeBatch(ontoList, ontoFile, ontoHeader);

	}

	private String toCsv(List<String> values) {
		String result = "";
		boolean first = true;
		for (String val : values) {
			if (first) {
				result += val;
				first = false;
			} else {
				result += "," + val;
			}
		}
		return result;
	}

	private void createFileAndHeader(File file, String[] fields)
			throws IOException {
		CsvWriter writer = new CsvFileWriter(file, Arrays.asList(fields));
		writer.writeHeader();
		writer.close();
	}

	private void writeBatch(List<? extends Entity> entities, File file,
			String[] fields) throws IOException {
		if (entities.size() > 0) {
			System.out.println("Writing to " + file);

			// create appending csvWriter using the selected headers
			CsvWriter writer = new CsvFileWriter(file, Arrays.asList(fields),
					true);

			// write batch to csv
			for (Entity e : entities) {
				writer.writeRow(e);
			}

			writer.close();

		}
	}

}
