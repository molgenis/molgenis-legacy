package plugins.matrix.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.core.Nameable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.ObservationTarget;

public class OverlibText {
	public static Map<String, String> getOverlibText(Database db, List<String> rowNames, List<String> colNames) throws Exception
	{
		List<ObservationTarget> rows = db.find(ObservationTarget.class, new QueryRule("name", Operator.IN, rowNames));
		List<ObservationTarget> cols = db.find(ObservationTarget.class, new QueryRule("name", Operator.IN, colNames));
		
		List<String> foundRows = new ArrayList<String>();
		List<String> foundCols = new ArrayList<String>();
		
		for (Nameable iden : rows)
		{
			foundRows.add(iden.getName());
		}
		for (Nameable iden : cols)
		{
			foundCols.add(iden.getName());
		}
		
		for(String rowName : rowNames){
			if(!foundRows.contains(rowName)){
				ObservationTarget nullIden = new ObservationTarget();
				nullIden.setName(rowName);
				nullIden.set("id", "-1");
				rows.add(nullIden);
			}
		}
		for(String colName : colNames){
			if(!foundCols.contains(colName)){
				ObservationTarget nullIden = new ObservationTarget();
				nullIden.setName(colName);
				nullIden.set("id", "-1");
				cols.add(nullIden);
			}
		}

		Map<String, String> overlibText = new HashMap<String, String>();
		for (Nameable iden : rows)
		{
			String text = appendFields(iden);
			overlibText.put(iden.getName(), org.apache.commons.lang.StringEscapeUtils.escapeHtml(text));
		}
		for (Nameable iden : cols)
		{
			String text = appendFields(iden);
			overlibText.put(iden.getName(), org.apache.commons.lang.StringEscapeUtils.escapeHtml(text));
			//overlibText.put(iden.getName(), text);
		}
		return overlibText;
	}

	private static String appendFields(Nameable iden){
		String text = "";
		
		if(iden.getId().intValue() == -1){
			text = "ERROR" + iden.getName() + ", ";
		}else{
		
		for (String field : iden.getFields())
		{
			if(iden.get(field) == null){
				text += field + " = " + "null, ";
			}else{
				text += field + " = " + iden.get(field).toString() + ", ";
			}
		}
		}
		return text;
	}
}
