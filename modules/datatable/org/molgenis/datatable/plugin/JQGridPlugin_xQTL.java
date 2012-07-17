package org.molgenis.datatable.plugin;

import java.io.File;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.molgenis.datatable.model.BinaryTupleTable;
import org.molgenis.datatable.model.QueryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/**
 * View data in a matrix.
 */
public class JQGridPlugin_xQTL extends JQGridPlugin
{
	public interface TupleTableBuilder
	{
		TupleTable create(Database db, Tuple request) throws TableException;
	}

	private static final long serialVersionUID = 8804579908239186037L;

	private final TupleTableBuilder tupleTableBuilder;

	public JQGridPlugin_xQTL(String name, ScreenController<?> parent)
	{
		super(name, parent);

		tupleTableBuilder = new TupleTableBuilder()
		{
			@Override
			public TupleTable create(Database db, Tuple request) throws TableException
			{
				try
				{
					BinaryTupleTable btt = new BinaryTupleTable(new File("/data/xqtl_panacea/binarydatamatrix/age_nil_phe.bin"));
					return btt;
				}
				catch (Exception ex)
				{
					throw new TableException(ex);
				}
			}
		};
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			final TupleTable tupleTable = tupleTableBuilder.create(db, null);
			// strange way to retrieve columns! Sould be in a ajax call when
			// grid is constructed!
			gridView = new JQGridView("myGrid", tupleTable);
			tupleTable.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	/**
	 * Handle a particular {@link MolgenisRequest}, and encode any resulting
	 * renderings/exports into a {@link MolgenisResponse}. Particulars handled:
	 * <ul>
	 * <li>Select the appropriate view towards which to export/render.</li>
	 * <li>Apply proper sorting and filter rules.</li>
	 * <li>Wrap the desired data source in the appropriate instantiation of
	 * {@link TupleTable}.</li>
	 * <li>Select and render the data.</li>
	 * </ul>
	 */
	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException
	{
		try
		{
			final TupleTable tupleTable = tupleTableBuilder.create(db, request);
			final int limit = request.getInt("rows");
			addFilters(request, (QueryTable) tupleTable);
			int rowCount = -1;
			rowCount = tupleTable.getCount();
			tupleTable.close(); // Not nice! We should fix this!
			int totalPages = 1;
			totalPages = (int) Math.ceil(rowCount / limit);
			int page = Math.min(request.getInt("page"), totalPages);
			int offset = Math.max(limit * page - limit, 0);
			addSortOrderLimitOffset(request, (QueryTable) tupleTable, offset);
			renderData(((MolgenisRequest) request).getRequest(), ((MolgenisRequest) request).getResponse(), page,
					totalPages, tupleTable);
			tupleTable.close();
		}
		catch (Exception e)
		{
			throw new HandleRequestDelegationException(e);
		}
		return null;
	}
}