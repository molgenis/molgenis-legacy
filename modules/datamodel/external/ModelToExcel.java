package external;

import java.io.IOException;
import java.io.StringWriter;

import org.molgenis.fieldtypes.MrefField;
import org.molgenis.fieldtypes.XrefField;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Field;
import org.molgenis.model.elements.Model;
import org.molgenis.util.CsvStringWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.JDBCMetaDatabase;

public class ModelToExcel
{
	public static void main(String[] args) throws DatabaseException, IOException, MolgenisModelException
	{
		Model m = new JDBCMetaDatabase();

		System.out.println(write(m));
	}

	public static String write(Model m)
	{
		StringWriter sw = new StringWriter();
		CsvStringWriter w;
		try
		{
			w = new CsvStringWriter(sw);

			write(m, w);

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sw.toString();
	}

	public static void write(Model m, CsvWriter w) throws MolgenisModelException, IOException
	{
		w.setHeaders("module", "entity", "field", "type", "nillable", "xref", "description");
		w.writeHeader();

		for (Entity e : m.getEntities())
			if (!e.isAbstract())
			{
				Tuple t = new SimpleTuple();
				if (e.getModule() != null) t.set("module", e.getModule().getName());
				t.set("entity", e.getName());
				t.set("field", "====");
				t.set("type", e.getAncestor() != null ? e.getAncestor().getName() : "");
				t.set("nillable", "====");
				t.set("xref", "====");
				t.set("description", e.getDescription());

				w.writeRow(t);

				for (Field f : e.getAllFields())
				{
					t.set("field", f.getName());
					t.set("type", f.getType());
					t.set("nillable", f.isNillable());
					t.set("description", f.getDescription());
					t.set("xref", null);
					
					if(f.getType() instanceof XrefField || f.getType() instanceof MrefField)
					{
						t.set("xref", f.getXrefEntityName());
					}

					w.writeRow(t);
				}
			}
	}
}
