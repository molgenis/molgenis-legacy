package org.molgenis.datatable.view.renderers;

import java.io.OutputStream;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;

public abstract class AbstractExporter
{
	protected final TupleTable table;

	public AbstractExporter(TupleTable tableModel)
	{
		this.table = tableModel;
	}

	public abstract void export(OutputStream os) throws TableException;
}
