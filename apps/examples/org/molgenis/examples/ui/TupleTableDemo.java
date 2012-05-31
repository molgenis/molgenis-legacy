package org.molgenis.examples.ui;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.FlowLayout;
import org.molgenis.framework.ui.html.JavaInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.TupleTable;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class TupleTableDemo extends EasyPluginController
{
	private static final long serialVersionUID = 7794050660074280454L;

	public TupleTableDemo(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// this method is called on every page reload
	}

	@Override
	public ScreenView getView()
	{
		MolgenisForm f = new MolgenisForm(this, new FlowLayout());

		List<Tuple> tuples = new ArrayList<Tuple>();
		for (int row = 1; row <= 100; row++)
		{
			Tuple t = new SimpleTuple();
			for (int col = 1; col <= 10; col++)
			{
				t.set("column" + col, "value" + row + "." + col);
			}

			tuples.add(t);
		}

		f.add(new Paragraph(
				"<h3>TupleTable:</h3>TupleTable is a quick viewer for a list of tuples. NB: 'Tuple' is a central data container in MOLGENIS and is used for example in CsvReader and db.sql(). For large tables use the AjaxTupleTable + AjaxTupleService"));

		f.add(new TupleTable("myTableTuple", tuples));

		f.add(new Paragraph("<h3>Code example:</h3>"));

		f.add(new JavaInput(
				"CodeExample",
				"...\n\npublic ScreenView getView() {\n  MolgenisForm f = new MolgenisForm(this);\n  List<Tuples> tuples = ...\n  f.add(new TupleTable(\"myTupleTable\", tuples));\n  return f\n}\n\n..."));

		return f;
	}

}
