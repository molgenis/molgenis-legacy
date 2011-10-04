package plugins.matrix.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationElement;

public class OverlibText {
	
	public static Map<String, ObservationElement> getObservationElements(Database db, List<String> names, String type) throws DatabaseException
	{
		Class subClass = db.getClassForName(type);
		Map<String, ObservationElement> res = new HashMap<String, ObservationElement>();
		List<ObservationElement> obsvElem = db.find(subClass, new QueryRule("name", Operator.IN, names));
		List<String> found = new ArrayList<String>();
		for (ObservationElement el : obsvElem)
		{
			found.add(el.getName());
		}
		for(String name : names){
			if(found.contains(name)){
				for(ObservationElement o : obsvElem){
					if(o.getName().equals(name)){
						res.put(name, o);
						break;
					}
				}
			}else{
				res.put(name, null);
			}
		}
		return res;
	}
}
