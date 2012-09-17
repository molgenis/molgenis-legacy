package plugins.richwizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.fieldtypes.BoolField;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.IntField;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Field;


public class Helper
{
	public static HashMap<String, String> getExampleCSVs(List<Entity> entities) throws MolgenisModelException
	{
		int maxColSize = 0;
		HashMap<String, String> exampleCsvs = new HashMap<String, String>();
		for (Entity entity : entities)
		{
			ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();
			for (Field f : entity.getAllFields())
			{
				ArrayList<String> col = new ArrayList<String>();
//				System.out.println("NAME " + f.getName());
//				System.out.println("expr1 " + !f.isAuto());
//				System.out.println("expr2 " + !(f.getType().equals("xref") && f.getXrefLabelNames().get(0).equals("name") && f
//						.getXrefEntityName().equals("Investigation")));
//				System.out.println("expr3 " + !f.isNillable());
				
				if (!f.isAuto()
						&& !(f.getType().equals("xref") && f.getXrefLabelNames().get(0).equals("name") && f
								.getXrefEntityName().equals("Investigation")) && !f.isNillable())
				{
					col.add(f.getName());
					if (f.getType() instanceof StringField)
					{
						col.add("aap");
						col.add("noot");
						col.add("mies");
					}
					else if (f.getType() instanceof IntField)
					{
						col.add("125");
						col.add("6278");
						col.add("45");
					}
					else if (f.getType() instanceof BoolField)
					{
						col.add("true");
						col.add("false");
						col.add("true");
					}
					else if (f.getType() instanceof DecimalField)
					{
						col.add("3.14159265");
						col.add("2.71828183");
						col.add("3564.234367");
					}
					else
					{
						col.add("TODO");
						col.add("TODO");
						col.add("TODO");
					}
					//System.out.println("ADDING COL: " + col.toString());
					if(col.size() > maxColSize){
						maxColSize = col.size();
					}
					columns.add(col);
				}
			}
			//'transpose' and concat
			String exampleCsv = "";
			for (int i = 0; i < maxColSize; i++)
			{
				for (int col = 0; col < columns.size(); col++)
				{
					if(i < columns.get(col).size()){
						exampleCsv += columns.get(col).get(i);
						if(col < (columns.size()-1)){
							exampleCsv += "\t";
						}
					}
				}
				exampleCsv += "\n";
			}
			exampleCsvs.put(entity.getName(), exampleCsv);
			//System.out.println("exampleCsv: " + exampleCsv);
			exampleCsv = "";
		}
		return exampleCsvs;
	}
}
