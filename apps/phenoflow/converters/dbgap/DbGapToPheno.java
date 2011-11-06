package converters.dbgap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.molgenis.core.OntologyTerm;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Code;
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
	List<Measurement> features = new ArrayList<Measurement>();
	Set<OntologyTerm> ontologyterms = new HashSet<OntologyTerm>();
	Map<String, Code> terms = new TreeMap<String, Code>();
	List<Panel> panels = new ArrayList<Panel>();
	List<ObservedValue> inferredValues = new ArrayList<ObservedValue>();

	public static void main(String[] args) throws Exception {
		// This will need updating if run on a different machine
		// String outputFolder = "d:/Data/dbgap/";
		String outputFolder = "C:/Users/Tomasz/pheno_workspace/pheno_data/dbgap/";
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
			if (lastversions.get(s.id) == null
					|| Integer.parseInt(lastversions.get(s.id).version.replace(
							"v", "")) < Integer.parseInt(s.version.replace("v",
							"")))
				lastversions.put(s.id, s);
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
			if (count > 10)
				break;
		}

	}

	public void write(File dir) throws Exception {
		new CsvExport().exportAll(dir, investigations,
				new ArrayList<OntologyTerm>(ontologyterms), protocols,
				features, new ArrayList<Code>(terms.values()), panels);
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
				Measurement f = new Measurement();
				f.setInvestigation_Name(i.getName());
				f.setName(var.name.toLowerCase());
				f.setDescription(var.description);
				// todo: add annotation feature NVT type?
				if (var.type != null)
					f.setDescription(f.getDescription() + " Type=" + var.type
							+ ".");
				if (var.logical_min != null)
					f.setDescription(f.getDescription() + " LogicalMin="
							+ var.logical_min + ".");
				if (var.logical_min != null)
					f.setDescription(f.getDescription() + " LogicalMax="
							+ var.logical_max + ".");
				f.setUnit_Name(var.unit);

				if (var.unit != null && !var.unit.equals("")) {
					OntologyTerm ot = new OntologyTerm();
					ot.setName(var.unit);
					ontologyterms.add(ot);
				}

				features.add(f);
				List<String> f_labels = p.getFeatures_Name();
				f_labels.add(f.getName());
				p.setFeatures_Name(f_labels);

				if (var.unit != null && terms.get(var.unit) == null) {
					Code t = new Code();
					t.setCode_String(var.unit);
					t.setDescription("N/A.");
					// t.setInvestigationLabel(i.getName());

					if (terms.containsKey(var.unit))
						logger.warn("duplicate term " + var.unit);
					terms.put(var.unit, t);
				}

				if (var.values.size() > 0) {
					for (Value v : var.values) {
						Code code = new Code();
						code.setCode_String(v.code);
						code.setDescription(v.value);
						code.getFeature().add(f.getId());
						// f.getValueCodesLabels().add(code.getTerm());

						// give error on duplicate term
						if (terms.containsKey(v.code))
							logger.warn("duplicate term " + v.code);
						terms.put(v.code, code);
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
				addInferredValue(panel, investigation, vs, stat.n, "n");
			if (stat.nulls != null)
				addInferredValue(panel, investigation, vs, stat.nulls, "nulls");
			if (stat.invalid_values != null)
				addInferredValue(panel, investigation, vs, stat.invalid_values,
						"invalid_values");
			if (stat.special_values != null)
				addInferredValue(panel, investigation, vs, stat.special_values,
						"special_values");
			if (stat.mean != null)
				addInferredValue(panel, investigation, vs, stat.mean, "mean");
			if (stat.mean_count != null)
				addInferredValue(panel, investigation, vs, stat.mean_count,
						"mean_count");
			if (stat.sd != null)
				addInferredValue(panel, investigation, vs, stat.sd, "sd");
			if (stat.median != null)
				addInferredValue(panel, investigation, vs, stat.median,
						"median");
			if (stat.median_count != null)
				addInferredValue(panel, investigation, vs, stat.median_count,
						"median_count");
			if (stat.min != null)
				addInferredValue(panel, investigation, vs, stat.min, "min");
			if (stat.min_count != null)
				addInferredValue(panel, investigation, vs, stat.min_count,
						"min_count");
			if (stat.max != null)
				addInferredValue(panel, investigation, vs, stat.max, "max");
			if (stat.max_count != null)
				addInferredValue(panel, investigation, vs, stat.max_count,
						"max_count");
		}
	}

	private void addInferredValue(Panel p, Investigation i, VariableSummary vs,
			String value, String inferenceType) {
		// NB (2011-11-04, ER): InferredValue (or DerivedValue) does not exist
		// at the moment
		// in the Pheno model. So I temporarily changed it to ObservedValue.

		if (terms.get(inferenceType) == null) {
			Code t = new Code();
			t.setCode_String(inferenceType);
			t.setDescription("N/A.");
			// t.setInvestigation_Name(i.getName());
			terms.put(inferenceType, t);
		}

		ObservedValue v = new ObservedValue();
		v.setInvestigation_Name(i.getName());
		v.setFeature_Name(vs.var_name.toLowerCase());
		v.setValue(value);
		// v.setInferenceTypeLabel(inferenceType);
		v.setTarget_Name(p.getName());
		// System.out.println("inferfed value " + v);
		this.inferredValues.add(v);

	}

	public String toString() {
		String result = "";
		for (Investigation i : investigations)
			result += i + "\n";
		for (Protocol p2 : protocols)
			result += p2 + "\n";
		for (Measurement f2 : features)
			result += f2 + "\n";
		for (Code t : terms.values())
			result += t + "\n";
		for (Panel p : panels)
			result += p + "\n";
		for (ObservedValue i : inferredValues)
			result += i + "\n";

		return result;
	}

}
