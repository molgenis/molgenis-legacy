package org.molgenis.gonl_pheno;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.ActionInput.Type;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.TupleTable;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Tuple;

public class SampleReport extends EasyPluginController<SampleReport>
{
	private static final long serialVersionUID = -13373267614102578L;
	private List<Tuple> rows;

	public SampleReport(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(this); // you can create a seperate class as 'model'.
	}

	// what is shown to the user
	public ScreenView getView()
	{
		// uncomment next line if you want to use template file instead
		// return new FreemarkerView("SampleReportView.ftl", getModel());

		MolgenisForm view = new MolgenisForm(this);

		view.add(new ActionInput("download_txt_samples", Type.DOWNLOAD));
		view.add(new TupleTable("sampleReport", rows));

		return view;
	}

	public void download_txt_samples(Database db, Tuple tuple, OutputStream out)
	{
		CsvWriter writer = new CsvWriter(new PrintWriter(out));
		if (rows.size() > 0)
		{
			writer.setHeaders(rows.get(0).getFields());
			writer.writeHeader();
			
			for (Tuple r : rows)
			{
				writer.writeRow(r);
			}
		}
		writer.close();

	}

	@Override
	public void reload(Database db) throws Exception
	{
		if (rows == null) rows = new TupleSource(db).getRows();
	}
}