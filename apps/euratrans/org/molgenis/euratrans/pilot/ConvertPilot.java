package org.molgenis.euratrans.pilot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import matrix.general.VerifyCsv;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.data.Data;
import org.molgenis.euratrans.ChipPeak;
import org.molgenis.euratrans.Promoter;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.Panel;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.Gene;
import org.molgenis.xgap.InvestigationFile;
import org.molgenis.xgap.Locus;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Sample;

public class ConvertPilot
{
	static String source = "/Users/mswertz/Downloads/EuratransPilot/XQTL_pilot/";
	static String target = "/Users/mswertz/Downloads/EuratransPilot/converted/";

	static List<Data> dataMatrices = new ArrayList<Data>();
	static Map<String, Chromosome> chromosomes = new LinkedHashMap<String, Chromosome>();
	static Map<String, Sample> samples = new LinkedHashMap<String, Sample>();
	static Map<String, Gene> genes = new LinkedHashMap<String, Gene>();
	static Map<String, Promoter> promoters = new LinkedHashMap<String, Promoter>();
	static Map<String, Measurement> measurements = new LinkedHashMap<String, Measurement>();
	static Map<String, String> nameAndfiles = new LinkedHashMap<String, String>();

	static Logger logger = Logger.getLogger("convert");

	// static Map<String, Panel> panels = new LinkedHashMap<String, Panel>();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		BasicConfigurator.configure();

		File dataDir = new File(target + "/data");
		dataDir.mkdirs();

		File binDir = new File(target + "/binarydatamatrix");
		binDir.mkdirs();

		File fileDir = new File(target + "/investigationfile");
		fileDir.mkdirs();

		// genotyping
		convertMarkers();
		convertGenotypes();

		// convert histone enriched gene bodies
		convertHistoneGeneBodies("histone_modifications_and_rna_seq/H3K27me3/liver/ensembl-59-gene-bodies.gff");
		convertHistoneGeneBodies("histone_modifications_and_rna_seq/H3K27me3/lv/ensembl-59-gene-bodies.gff");

		convertHistoneData("H3K27me3_liver_data_matrix",
				"histone_modifications_and_rna_seq/H3K27me3/liver/ensembl-59-gene-bodies-bound-data-matrix.txt",
				Gene.class);

		convertHistoneData("H3K27me3_lv_data_matrix",
				"histone_modifications_and_rna_seq/H3K27me3/lv/ensembl-59-gene-bodies-bound-data-matrix.txt",
				Gene.class);

		// convertQtlResults("H3K27me3_liver_qtl_results",
		// "histone_modifications_and_rna_seq/H3K27me3/liver/ensembl-59-gene-bodies-bound-qtl-results.txt");

		convertGffToCsvMatrix("H3K27me3_liver_qtl_intervals",
				"histone_modifications_and_rna_seq/H3K27me3/liver/ensembl-59-gene-bodies-bound-qtl-results.gff");

		convertGffToCsvMatrix("H3K27me3_lv_qtl_intervals",
				"histone_modifications_and_rna_seq/H3K27me3/lv/ensembl-59-gene-bodies-bound-qtl-results.gff");

		// convert histone enriched promotors
		convertHistonePromoters("histone_modifications_and_rna_seq/H3K4me3/liver/ensembl-59-promoters.gff");
		convertHistonePromoters("histone_modifications_and_rna_seq/H3K4me3/lv/ensembl-59-promoters.gff");

		convertHistoneData("H3K4me3_liver_data_matrix",
				"histone_modifications_and_rna_seq/H3K4me3/liver/ensembl-59-promoters-bound-data-matrix.txt",
				Promoter.class);
		convertHistoneData("H3K4me3_lv_data_matrix",
				"histone_modifications_and_rna_seq/H3K4me3/lv/ensembl-59-promoters-bound-data-matrix.txt",
				Promoter.class);

		convertGffToCsvMatrix("H3K4me3_liver_qtl_intervals",
				"histone_modifications_and_rna_seq/H3K4me3/liver/ensembl-59-promoters-bound-qtl-results.gff");

		convertGffToCsvMatrix("H3K4me3_lv_qtl_intervals",
				"histone_modifications_and_rna_seq/H3K4me3/lv/ensembl-59-promoters-bound-qtl-results.gff");

		// load the CHIP MAC peaks data
		convertChipData("bnlx2H3K27ac_peaks.bed");
		convertChipData("bnlx2H3K4me1_peaks.bed");
		convertChipData("bnlx2H3K4me3_peaks.bed");
		convertChipData("bnlx3H3K27ac_peaks.bed");
		convertChipData("bnlx3H3K4me1_peaks.bed");
		convertChipData("bnlx3H3K4me3_peaks.bed");
		convertChipData("shr2H3K27ac_peaks.bed");
		convertChipData("shr2H3K4me1_peaks.bed");
		convertChipData("shr2H3K4me3_peaks.bed");
		convertChipData("shr3H3K27ac_peaks.bed");
		convertChipData("shr3H3K4me1_peaks.bed");
		convertChipData("shr3H3K4me3_peaks.bed");

		// convert to bin
		for (Data d : dataMatrices)
		{
			d.setInvestigation_Name("EURATRANS");
			convertCsvToBin(new File(target + "data/" + d.getName() + ".txt"), d);
		}

		// write out annotations
		writeChromosomes();
		writeSamples();
		writeGenes();
		writePromotors();
		writeData();

		writeFiles(nameAndfiles);

	}

	/**
	 * Because peaks don't have meaning between data sets we don't consider the
	 * Peak numbers 'targets'
	 */
	static void convertChipData(String path) throws IOException, DataFormatException
	{
		// simply convert BED to data (null X Measurement)
		Data d = new Data();
		d.setName(path.substring(0, path.length() - 4));
		d.setFeatureType(Measurement.class.getSimpleName());
		d.setTargetType(ChipPeak.class.getSimpleName());
		d.setValueType("Text");

		dataMatrices.add(d);

		CsvReader in = new CsvFileReader(new File(source + "chip/peaks/" + path));

		CsvWriter out = new CsvFileWriter(new File(target + "data/" + d.getName() + ".txt"));
		out.setHeaders(Arrays.asList(new String[]
		{ "", "chr", "start", "end", "p_value_10_log" }));

		out.writeHeader();
		for (Tuple row : in)
		{
			Tuple result = new SimpleTuple();
			result.set("", row.getString(3));
			result.set("chr", row.getString(0));
			result.set("start", row.getString(1));
			result.set("end", row.getString(2));
			result.set("p_value_10_log", row.getString(4));
			out.writeRow(result);
		}
		out.close();

	}

	static void convertQtlResults(String name, String file) throws Exception
	{
		Data qtls = new Data();
		qtls.setName(name);
		qtls.setFeatureType(Measurement.class.getSimpleName());
		// ai!
		qtls.setTargetType(Measurement.class.getSimpleName());
		qtls.setValueType("Text");

		dataMatrices.add(qtls);

		CsvReader in = new CsvFileReader(new File(source + file));

		// extract Measurement
		for (String measurement : in.colnames())
		{
			if (!measurements.containsKey(measurement))
			{
				Measurement m = new Measurement();
				m.setName(measurement);
				measurements.put(m.getName(), m);
			}
		}

		// convert the csv; need to add artificial column with numbers :-(
		CsvWriter matrixCsv = new CsvFileWriter(new File(target + "data/" + qtls.getName() + ".txt"));
		List<String> names = new ArrayList<String>(in.colnames());
		names.add(0, "");
		matrixCsv.setHeaders(names);
		matrixCsv.writeHeader();

		int i = 1;
		for (Tuple row : in)
		{
			// add count
			((SimpleTuple) row).set("", "qtl" + i);

			matrixCsv.writeRow(row);

			i++;
		}
		matrixCsv.close();

	}

	static void writeFiles(Map<String, String> files) throws IOException
	{
		CsvWriter dataCsv = new CsvFileWriter(new File(target + "investigationfile.txt"));
		dataCsv.setHeaders(Arrays.asList(new String[]
		{ InvestigationFile.NAME, InvestigationFile.EXTENSION }));

		dataCsv.writeHeader();
		for (String file : files.keySet())
		{
			File src = new File(source + files.get(file));

			InvestigationFile f = new InvestigationFile();
			f.setName(file.trim());
			f.setExtension(src.getName().substring(src.getName().lastIndexOf(".") + 1));

			FileUtils.copyFile(src, new File(target + "/investigationfile/" + f.getName()));

			dataCsv.writeRow(f);
		}
		dataCsv.close();
	}

	// sdps2snps.csv -> marker.txt + chromosome.txt
	// TODO: details on the chromosomes
	public static void convertMarkers() throws IOException, DataFormatException
	{
		logger.info("convert markers:");
		// chrom,representative,marker,physical.pos,cM
		CsvReader in = new CsvFileReader(new File(source + "histone_modifications_and_rna_seq/Genotypes/sdps2snps.csv"));

		// chromosome_name,name,symbol,bpStart,cm
		CsvWriter markerCsv = new CsvFileWriter(new File(target + "marker.txt"));
		markerCsv.setHeaders(Arrays.asList(new String[]
		{ Marker.CHROMOSOME_NAME, Marker.NAME, Marker.BPSTART, Marker.CM }));

		markerCsv.writeHeader();
		for (Tuple t : in)
		{
			Marker m = new Marker();
			m.setName(t.getString("marker"));
			m.setChromosome_Name("chr" + t.getString("chrom"));
			// todo m.setReportsFor_Name(("representative"));
			if (!"NA".equals(t.getString("physical.pos"))) m.setBpStart(t.getLong("physical.pos"));
			else
				m.setBpStart(null);
			m.setCM(t.getDouble("cM"));

			markerCsv.writeRow(m);

			if (chromosomes.get(m.getChromosome_Name()) == null)
			{
				Chromosome c = new Chromosome();
				c.setName(m.getChromosome_Name());
				chromosomes.put(m.getChromosome_Name(), c);
			}
		}
		markerCsv.close();

		logger.info("convert markers complete");
	}

	public static void writeChromosomes() throws IOException
	{
		logger.info("write chromosomes ...");
		CsvWriter chromosomeCsv = new CsvFileWriter(new File(target + "chromosome.txt"));
		chromosomeCsv.setHeaders(Arrays.asList(new String[]
		{ Chromosome.NAME, Chromosome.ORDERNR, Chromosome.ISAUTOSOMAL }));

		chromosomeCsv.writeHeader();
		for (Chromosome c : chromosomes.values())
		{
			if (c.getName().contains("M"))
			{
				c.setOrderNr(22);
				c.setIsAutosomal(false);
			}
			else if (c.getName().contains("X"))
			{
				c.setOrderNr(22);
				c.setIsAutosomal(false);
			}
			else
			{
				c.setOrderNr(Integer.parseInt(c.getName().substring(3)));
				c.setIsAutosomal(true);
			}
			chromosomeCsv.writeRow(c);
		}
		chromosomeCsv.close();

		logger.info("write chromosomes complete");
	}

	public static void writeData() throws IOException
	{
		CsvWriter dataCsv = new CsvFileWriter(new File(target + "data.txt"));
		dataCsv.setHeaders(Arrays.asList(new String[]
		{ Data.NAME, Data.FEATURETYPE, Data.TARGETTYPE, Data.VALUETYPE }));

		dataCsv.writeHeader();
		for (Data c : dataMatrices)
		{
			dataCsv.writeRow(c);
		}
		dataCsv.close();
	}

	// star-genetic-map-20071109.csv -> panel.txt, genotypes.txt, data.txt
	// TODO: externalid different from other file??
	public static void convertGenotypes() throws Exception
	{
		logger.info("convert genotypes ...");

		Data genotypes = new Data();
		genotypes.setName("genetic_map_20071109");
		genotypes.setFeatureType(Panel.class.getSimpleName());
		genotypes.setTargetType(Marker.class.getSimpleName());
		genotypes.setValueType("Text");

		dataMatrices.add(genotypes);

		// we need to filter away the marker info
		CsvReader in = new CsvFileReader(new File(source
				+ "histone_modifications_and_rna_seq/Genotypes/star-genetic-map-20071109.csv"));

		List<String> skip = Arrays.asList(new String[]
		{ "chrom", "externalid", "physical.pos", "physical.pos", "cM" });
		List<Panel> panels = new ArrayList<Panel>();
		List<String> names = new ArrayList<String>();
		// names.add("");
		for (String name : in.colnames())
		{
			if (!skip.contains(name))
			{
				names.add(name);
				Panel p = new Panel();
				p.setName(name);
				panels.add(p);
			}
		}

		CsvWriter panelCsv = new CsvFileWriter(new File(target + "panel.txt"));
		panelCsv.setHeaders(Arrays.asList(new String[]
		{ Panel.NAME }));
		panelCsv.writeHeader();
		for (Panel p : panels)
			panelCsv.writeRow(p);
		panelCsv.close();

		CsvWriter matrixCsv = new CsvFileWriter(new File(target + "data/" + genotypes.getName() + ".txt"));
		names.set(0, "");
		matrixCsv.setHeaders(names);
		matrixCsv.writeHeader();
		for (Tuple row : in)
		{
			// rename first col to empty
			List<String> cols = row.getFields();
			cols.set(cols.indexOf("markeralias"), "");
			((SimpleTuple) row).setFields(cols);

			matrixCsv.writeRow(row);
		}
		matrixCsv.close();

		logger.info("convert genotypes complete");
	}

	public static void convertHistonePromoters(String path) throws IOException, DataFormatException
	{
		logger.info("convert promoters " + path + " ...");

		// we need to extract individual, gene
		CsvReader in = new CsvFileReader(new File(source + path));

		// CsvWriter geneCsv = new CsvFileWriter(new File(target + "gene.txt"));
		// geneCsv.setHeaders(Arrays.asList(new String[]
		// { Gene.CHROMOSOME_NAME, Gene.NAME, Gene.SYMBOL, Gene.BPSTART,
		// Gene.BPEND }));
		//
		// geneCsv.writeHeader();
		for (Tuple t : in)
		{
			Promoter p = new Promoter();

			// strip the leading 'Chr'
			p.setChromosome_Name(t.getString(0));

			String[] geneNames = t.getString(8).substring(3).split(";Name=");
			// strip leading 'ID=', split symbo
			p.setName("PROMOTER_" + geneNames[0]);
			if (geneNames.length > 1) p.setSymbol(geneNames[1]);
			p.setBpStart(t.getLong(3));
			p.setBpEnd(t.getLong(4));

			if (!promoters.containsKey(p.getName()))
			{
				promoters.put(p.getName(), p);
			}

			// TODO strand / orientation

			// geneCsv.writeRow(g);

			if (chromosomes.get(p.getChromosome_Name()) == null)
			{
				Chromosome c = new Chromosome();
				c.setName(p.getChromosome_Name());
				chromosomes.put(p.getChromosome_Name(), c);
			}
		}
		// geneCsv.close();

		logger.info("convert promoters complete");
	}

	public static void convertHistoneGeneBodies(String path) throws IOException, DataFormatException
	{
		logger.info("convert gene bodies " + path + " ...");

		// we need to extract individual, gene
		CsvReader in = new CsvFileReader(new File(source + path));

		// CsvWriter geneCsv = new CsvFileWriter(new File(target + "gene.txt"));
		// geneCsv.setHeaders(Arrays.asList(new String[]
		// { Gene.CHROMOSOME_NAME, Gene.NAME, Gene.SYMBOL, Gene.BPSTART,
		// Gene.BPEND }));
		//
		// geneCsv.writeHeader();
		for (Tuple t : in)
		{
			Gene g = new Gene();

			// strip the leading 'Chr'
			g.setChromosome_Name(t.getString(0));

			String[] geneNames = t.getString(8).substring(3).split(";Name=");
			// strip leading 'ID=', split symbo
			g.setName(geneNames[0]);
			if (geneNames.length > 1) g.setSymbol(geneNames[1]);
			g.setBpStart(t.getLong(3));
			g.setBpEnd(t.getLong(4));

			if (!genes.containsKey(g.getName()))
			{
				genes.put(g.getName(), g);
			}

			// TODO strand / orientation

			// geneCsv.writeRow(g);

			if (chromosomes.get(g.getChromosome_Name()) == null)
			{
				Chromosome c = new Chromosome();
				c.setName(g.getChromosome_Name());
				chromosomes.put(g.getChromosome_Name(), c);
			}
		}
		// geneCsv.close();

		logger.info("convert gene bodies complete");
	}

	public static void convertHistoneData(String name, String path, Class<? extends Locus> klazz) throws Exception
	{
		logger.info("convert data " + path + " ...");

		Data genotypes = new Data();
		genotypes.setName(name);
		genotypes.setFeatureType(Sample.class.getSimpleName());
		genotypes.setTargetType(klazz.getSimpleName());
		genotypes.setValueType("Decimal");

		dataMatrices.add(genotypes);

		// we need to extract individual
		// TODO: are individuals shared over these sets??? And what about the
		// 'merged'? Are these 'strain' level info?
		CsvReader in = new CsvFileReader(new File(source + path));
		List<String> indNames = in.colnames();

		for (String iName : indNames)
		{
			iName = iName.replace(".", "_");
			if (!"".equals(iName) && !samples.containsKey(iName))
			{
				Sample i = new Sample();
				i.setName(iName);

				samples.put(i.getName(), i);
			}
		}

		// assume gene bodies complete; only push headers a bit
		CsvWriter out = new CsvFileWriter(new File(target + "data/" + name + ".txt"));
		List<String> headers = new ArrayList<String>();
		for(String n: in.colnames())
			headers.add(n.replace(".", "_"));

		out.setHeaders(headers);
		out.writeHeader();
		for (Tuple t : in)
		{
			for (int i = 0; i < t.size(); i++)
			{
				if (i > 0) out.writeSeparator();
				out.writeValue(t.getString(i));
			}
			out.writeEndOfLine();
		}
		out.close();

		logger.info("convert data complete");
	}

	static void writeSamples() throws IOException
	{
		logger.info("write samples ...");

		CsvWriter indCsv = new CsvFileWriter(new File(target + "sample.txt"));
		indCsv.setHeaders(Arrays.asList(new String[]
		{ Sample.NAME }));

		indCsv.writeHeader();
		for (Sample s : samples.values())
		{
			indCsv.writeRow(s);
		}
		indCsv.close();

		logger.info("write samples complete ...");
	}

	static void writeGenes() throws IOException
	{
		logger.info("write genes ...");

		CsvWriter indCsv = new CsvFileWriter(new File(target + "gene.txt"));
		indCsv.setHeaders(Arrays.asList(new String[]
		{ Gene.CHROMOSOME_NAME, Gene.NAME, Gene.SYMBOL, Gene.BPSTART, Gene.BPEND }));

		indCsv.writeHeader();
		for (Gene s : genes.values())
		{
			indCsv.writeRow(s);
		}
		indCsv.close();

		logger.info("write genes complete ...");
	}

	static void writePromotors() throws IOException
	{
		logger.info("write promoters:");

		CsvWriter indCsv = new CsvFileWriter(new File(target + "promoter.txt"));
		indCsv.setHeaders(Arrays.asList(new String[]
		{ Promoter.CHROMOSOME_NAME, Promoter.NAME, Promoter.BPSTART, Promoter.BPEND }));

		indCsv.writeHeader();
		for (Promoter s : promoters.values())
		{
			indCsv.writeRow(s);
		}
		indCsv.close();

		logger.info("write promoters complete");
	}

	static void writeMeasurements() throws IOException
	{
		logger.info("write measurements:");

		CsvWriter indCsv = new CsvFileWriter(new File(target + "measurement.txt"));
		indCsv.setHeaders(Arrays.asList(new String[]
		{ Measurement.NAME }));

		indCsv.writeHeader();
		for (Measurement s : measurements.values())
		{
			indCsv.writeRow(s);
		}
		indCsv.close();

		logger.info("write measurements complete");
	}

	static void convertCsvToBin(File src, Data d) throws Exception
	{
		logger.info("converting csv to bin " + src.getPath());

		// verify the CSV file to be a correct matrix and get the dimensions
		int[] dims = VerifyCsv.verify(src, d.getValueType());

		logger.info("verified");

		// convert to binary
		File dest = new File(target + "binarydatamatrix/" + d.getName() + ".bin");

		logger.info("Starting conversion..");

		new MakeBinary().makeBinaryBackend(d, src, dest, dims[0], dims[1]);

		logger.info("..done!");
	}

	static void convertGffToCsvMatrix(String dataName, String src) throws Exception
	{
		nameAndfiles.put(dataName, src);

		CsvReader in = new CsvFileReader(new File(source + src));

		Data data = new Data();
		data.setName(dataName);
		data.setFeatureType(Measurement.class.getSimpleName());
		data.setTargetType(Measurement.class.getSimpleName());
		data.setValueType("Text");

		dataMatrices.add(data);

		CsvWriter out = new CsvFileWriter(new File(target + "data/" + data.getName() + ".txt"));
		List<String> labels = Arrays.asList(new String[]
		{ "", "start", "end", "peak", "minp", "trait", "distance", "cis", "BN", "SHR", "log2_BN_over_SHR" });

		out.setHeaders(labels);
		out.writeHeader();
		int i = 1;
		for (Tuple row : in)
		{
			SimpleTuple result = new SimpleTuple();
			result.set("", "qtl" + i++);
			result.set("start", row.getString(3));
			result.set("end", row.getString(4));

			String[] valuePairs = row.getString(8).split(";");

			for (String vp : valuePairs)
			{
				String[] labelValue = vp.split("=");
				if (!labels.contains(labelValue[0])) throw new Exception("Label=" + labelValue[0] + " not known");
				result.set(labelValue[0], labelValue[1]);
			}

			out.writeRow(result);
		}
		out.close();
	}
}
