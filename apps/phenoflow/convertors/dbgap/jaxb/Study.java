package convertors.dbgap.jaxb;

import java.util.ArrayList;
import java.util.List;

import convertors.dbgap.jaxb.data_dict.Data_Dict;
import convertors.dbgap.jaxb.var_report.Var_Report;

public class Study
{
	public String id;
	public String name;
	public String version;
	public String description;
	public List<Data_Dict> dictionaries = new ArrayList<Data_Dict>();
	public List<Var_Report> reports = new ArrayList<Var_Report>();
	
	public String toString()
	{
		String result = "Study: "+name + " "+version +"\n";
		for(Data_Dict d: dictionaries) result += d.id+"\n";
		for(Var_Report r: reports) result += r.dataset_id+"\n";
		return result;
	}
}
