package converters.dbgap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.molgenis.core.OntologyTerm;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;

import app.CsvExport;
import converters.dbgap.jaxb.Study;
import converters.dbgap.jaxb.data_dict.Data_Dict;
import converters.dbgap.jaxb.data_dict.Value;
import converters.dbgap.jaxb.data_dict.Variable;
import converters.dbgap.jaxb.var_report.Stat;
import converters.dbgap.jaxb.var_report.Var_Report;
import converters.dbgap.jaxb.var_report.VariableSummary;

public class DbGapToPheno {
	static Logger logger = Logger.getLogger(DbGapToPheno.class);

	List<Investigation> investigations = new ArrayList<Investigation>();
	List<Protocol> protocols = new ArrayList<Protocol>();
	Map<String, Measurement> measurements = new LinkedHashMap<String, Measurement>();
	Set<OntologyTerm> ontologyterms = new HashSet<OntologyTerm>();
	Map<String, Category> categories = new TreeMap<String, Category>();
	List<Panel> panels = new ArrayList<Panel>();
	List<ObservedValue> observedValues = new ArrayList<ObservedValue>();

	public static void main(String[] args) throws Exception {
		// This will need updating if run on a different machine
		// String outputFolder = "d:/Data/dbgap/";
		String outputFolder = "../pheno_data/dbgap/";
		String dbgapUrl = outputFolder + "FTP_Table_of_Contents.xml";
		DbGapService dbgap = new DbGapService(new File(dbgapUrl).toURI()
				.toURL(), new File(outputFolder));

		int count = 1;

		// get studies
		List<Study> studies = dbgap.listStudies();

		// filter out last versions only
		Map<String, Study> lastversions = new TreeMap<String, Study>();
		for (Study s : studies) {
			// System.out.println("filtering "+ s.id + " "+s.version+ " "
			// +s.description);
			// pht000182 no v
			if (s.version.startsWith("v")
					&& (lastversions.get(s.id) == null || extractVersion(lastversions
							.get(s.id).version) < extractVersion(s.version))) {
				lastversions.put(s.id, s);
			}
		}

		// caching all files
		// for (Data_Dict vr : dbgap.listDictionaries())
		// {
		// File f = new File(outputFolder + "download/" + vr.id + ".xml");
		// if(!f.exists()) downloadFile(vr.url,f);
		// }
		// for (Var_Report vr : dbgap.listVariableReports())
		// {
		// File f = new File(outputFolder + "download/" + vr.dataset_id +
		// ".xml");
		// if(!f.exists()) downloadFile(vr.url, f);
		// }

		// download the last versions
		System.out.println("lastversions = " + lastversions.size());
		for (Study s : lastversions.values()) {
			DbGapToPheno converter = new DbGapToPheno();

			// writing the data_dicts
			File dir = new File(outputFolder + s.id);
			System.out.println("converting " + s.id + " " + s.version + " "
					+ s.description + " to " + dir);
			dbgap.loadDictionaries(s);
			dbgap.loadVariableReports(s);

			converter.read(s);

			dir.mkdirs();
			// System.out.println(convertor.toString());
			converter.write(dir);

			// debug purposes only
			count++;
			if (count > 6) {
				System.out.println("skipped other studies!");
				break;
			}
		}

	}

	public void write(File dir) throws Exception {
		new CsvExport().exportAll(dir, investigations,
				new ArrayList<OntologyTerm>(ontologyterms), protocols,
				new ArrayList(measurements.values()),
				new ArrayList(categories.values()), panels, observedValues);
	}

	public static void downloadFile(URL url, File destination)
			throws IOException {
		logger.debug("downloading " + url + " to " + destination);
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			URLConnection urlc = url.openConnection();

			in = new BufferedInputStream(urlc.getInputStream());
			out = new BufferedOutputStream(new FileOutputStream(destination));

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
		}

	}

	/**
	 * Read the dbGaP study into the convertor
	 * 
	 * @param s
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void read(Study s) throws JAXBException, IOException {
		Investigation i = new Investigation();
		i.setDescription(s.description);
		i.setName(s.id + "." + s.version);
		investigations.add(i);

		// data dictionaries = protocols + variables + features (+ ontologies)
		for (Data_Dict dd : s.dictionaries) {
			Protocol p = new Protocol();
			p.setName(dd.description);
			p.setInvestigation_Name(i.getName());
			p.setName(dd.id);
			protocols.add(p);

			for (Variable var : dd.variables) {
				Measurement measurement = new Measurement();
				measurement.setInvestigation_Name(i.getName());
				measurement.setName(var.name.toLowerCase());
				measurement.setDescription(var.description);

				// todo: add annotation feature NVT type?
				if (var.type != null && !var.type.equals("")) {
					String dataType;

					// available types:
					// xref,string,categorical,datetime,int,code,image,decimal,bool,file,log,data
					if (var.type.contains("decimal")) { // that handles 'decimal, encoded'
						dataType = "decimal";
					} else if (var.type.contains("integer")) { // same for encoded and enumerated
						dataType = "int";
					} else {
						dataType = "string";
					}

					measurement.setDataType(dataType);
					// f.set__Type(var.type);
				}

				if (var.logical_min != null)
					measurement.setDescription(measurement.getDescription()
							+ " LogicalMin=" + var.logical_min + ".");
				if (var.logical_min != null)
					measurement.setDescription(measurement.getDescription()
							+ " LogicalMax=" + var.logical_max + ".");

				measurement.setUnit_Name(var.unit);

				if (var.unit != null && !var.unit.equals("")) {
					OntologyTerm ot = new OntologyTerm();
					ot.setName(var.unit);
					ontologyterms.add(ot);
				}

				measurements.put(measurement.getName(), measurement);
				p.getFeatures_Name().add(measurement.getName());

				// if (var.unit != null && terms.get(var.unit) == null) {
				// Category t = new Category();
				// t.setName(var.unit);
				// t.setCode_String(var.unit);
				// t.setLabel(var.unit);
				// t.setDescription("N/A.");
				// t.setInvestigation_Name(i.getName());
				//
				// //link category to measurement
				// measurement.getCategories_Name().add(t.getName());
				// //t.getFeature_Name().add(f.getName());
				//
				// // t.setInvestigationLabel(i.getName());
				//
				// if (terms.containsKey(var.unit))
				// logger.warn("duplicate term " + var.unit);
				// terms.put(var.unit, t);
				// }

				if (var.values.size() > 0) {
					for (Value v : var.values) {
						Category category = new Category();
						category.setCode_String(v.code);
						category.setLabel(v.value);
						category.setDescription("NA");
						category.setName(measurement.getName() + "_" + v.code);
						category.setInvestigation_Name(i.getName());

						categories.put(category.getName(), category);

						// code.getFeature_Name().add(f.getName());
						measurement.getCategories_Name()
								.add(category.getName());

						// give error on duplicate term
						if (v.code == null) {
							logger.warn("empty code on " + v.value);
						}
						if (v.code != null && categories.containsKey(v.code)) {
							logger.warn("duplicate term " + v.code);
							if (v.code != null) {
								categories.put(v.code, category);
							}

						}
					}

				}

			}
		}

		// var report = observedValues, protocolApplication, panels
		Panel total_panel = new Panel();
		total_panel.setName("total");
		total_panel.setInvestigation_Name(i.getName());

		Panel cases_panel = new Panel();
		cases_panel.setName("cases");
		cases_panel.setInvestigation_Name(i.getName());

		Panel controls_panel = new Panel();
		controls_panel.setName("controls");
		controls_panel.setInvestigation_Name(i.getName());

		this.panels.add(total_panel);
		this.panels.add(cases_panel);
		this.panels.add(controls_panel);

		for (Var_Report vr : s.reports) {
			logger.debug("var_report " + vr.dataset_id);

			for (VariableSummary vs : vr.variables) {
				if (vs.total != null)
					addStatsToPanel(total_panel, i, vs, vs.total.stats);
				if (vs.cases != null)
					addStatsToPanel(cases_panel, i, vs, vs.cases.stats);
				if (vs.controls != null)
					addStatsToPanel(controls_panel, i, vs, vs.controls.stats);
			}
		}
	}

	/** Is there an ontology for these stat terms? */
	private void addStatsToPanel(Panel panel, Investigation investigation,
			VariableSummary vs, List<Stat> stats) {
		for (Stat stat : stats) {
			if (stat.n != null)
				addObservedValue(panel, investigation, vs, stat.n, "n");
			if (stat.nulls != null)
				addObservedValue(panel, investigation, vs, stat.nulls, "nulls");
			if (stat.invalid_values != null)
				addObservedValue(panel, investigation, vs, stat.invalid_values,
						"invalid_values");
			if (stat.special_values != null)
				addObservedValue(panel, investigation, vs, stat.special_values,
						"special_values");
			if (stat.mean != null)
				addObservedValue(panel, investigation, vs, stat.mean, "mean");
			if (stat.mean_count != null)
				addObservedValue(panel, investigation, vs, stat.mean_count,
						"mean_count");
			if (stat.sd != null)
				addObservedValue(panel, investigation, vs, stat.sd, "sd");
			if (stat.median != null)
				addObservedValue(panel, investigation, vs, stat.median,
						"median");
			if (stat.median_count != null)
				addObservedValue(panel, investigation, vs, stat.median_count,
						"median_count");
			if (stat.min != null)
				addObservedValue(panel, investigation, vs, stat.min, "min");
			if (stat.min_count != null)
				addObservedValue(panel, investigation, vs, stat.min_count,
						"min_count");
			if (stat.max != null)
				addObservedValue(panel, investigation, vs, stat.max, "max");
			if (stat.max_count != null)
				addObservedValue(panel, investigation, vs, stat.max_count,
						"max_count");
		}
	}

	private void addObservedValue(Panel p, Investigation i, VariableSummary vs,
			String value, String inferenceType) {

		Measurement inference = measurements.get(inferenceType);
		if (inference == null) {
			inference = new Measurement();
			inference.setName(inferenceType);
			inference.setDescription("N/A.");
			inference.setInvestigation_Name(i.getName());
			// t.setInvestigation_Name(i.getName());
			measurements.put(inference.getName(), inference);
		}

		Measurement feature = measurements.get(vs.var_name);
		if (feature == null) {
			logger.warn("var_name '"
					+ vs.var_name
					+ "' not found. Is it missing in dictionary? We add it now...");

			feature = new Measurement();
			feature.setName(vs.var_name);
			feature.setDataType(vs.description);
			if ("integer".equals(vs.calculated_type)
					|| "enum_integer".equals(vs.calculated_type)) {
				feature.setDataType("int");
			} else if ("decimal".equals(vs.calculated_type)) {
				feature.setDataType("decimal");
			} else if ("string".equals(vs.calculated_type)) {
				feature.setDataType("string");
			} else {
				logger.error("cannot get data type " + vs.calculated_type);
			}

			measurements.put(feature.getName(), feature);
		}
		if (feature.getName().contains("Specific diagnosis Mutation")) {
			logger.debug("found");
		}

		ObservedValue v = new ObservedValue();
		v.setInvestigation_Name(i.getName());
		v.setFeature_Name(feature.getName());
		v.setValue(value);
		v.setRelation_Name(inferenceType);
		// v.setInferenceTypeLabel(inferenceType);
		v.setTarget_Name(p.getName());
		// System.out.println("inferfed value " + v);
		this.observedValues.add(v);

	}

	public String toString() {
		String result = "";
		for (Investigation i : investigations)
			result += i + "\n";
		for (Protocol p2 : protocols)
			result += p2 + "\n";
		for (Measurement f2 : measurements.values())
			result += f2 + "\n";
		for (Category t : categories.values())
			result += t + "\n";
		for (Panel p : panels)
			result += p + "\n";
		for (ObservedValue i : observedValues)
			result += i + "\n";

		return result;
	}

	public static Integer extractVersion(String s) {
		Pattern p = Pattern.compile("\\d+$");
		Matcher m = p.matcher(s);
		m.find();
		return Integer.parseInt(m.group());
	}
}
