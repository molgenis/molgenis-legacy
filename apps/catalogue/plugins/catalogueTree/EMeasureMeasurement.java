package plugins.catalogueTree;

import org.molgenis.framework.db.Database;
import org.molgenis.pheno.Measurement;

public class EMeasureMeasurement {

	public String addXML(Measurement m, Database db) throws Exception {
		StringBuffer out = new StringBuffer();

		out.append("\t<subjectOf>\n" + "\t\t<measureAttribute>");
		String code = m.getName();
		String codeSystem = "TBD";
		String displayName = m.getDescription();
		String datatype = m.getDataType();
		String codeDatatype = "dunno";
		String codeSystemDatatype = "TBD";
		String displayNameDatatype = "This should be the mappingsname";
		out.append("<code code=\"" + code + "\" codeSystem=\"" + codeSystem
				+ "\"" + " displayName=\"" + displayName + "\" />");

		out.append("<value xsi:type=\"" + datatype + "\" code=\""
				+ codeDatatype + "\" codeSystem=\"" + codeSystemDatatype + "\""
				+ " displayName=\"" + displayNameDatatype + "\" />");

		// List<OntologyTerm> otList = db.find(OntologyTerm.class, new
		// QueryRule(OntologyTerm.ID, Operator.IN,
		// m.getOntologyReference_Id()));

		out.append("\t\t</measureAttribute>\n" + "\t</subjectOf>");

		System.out.println(out.toString());

		return out.toString();
	}

}
