package org.molgenis.framework.tupletable.view.renderers;

import java.io.OutputStream;

import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;

public abstract class AbstractExporter
{
	protected final TupleTable table;

	public AbstractExporter(TupleTable tableModel)
	{
		this.table = tableModel;
	}

	public abstract void export(OutputStream os) throws TableException;
}
