package org.molgenis.datatable.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.model.elements.Field;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.MapEntry;
import org.molgenis.util.plink.datatypes.PedEntry;
import org.molgenis.util.plink.drivers.MapFileDriver;
import org.molgenis.util.plink.drivers.PedFileDriver;

public class PedMapTupleTable extends AbstractFilterableTupleTable
{
	private PedFileDriver pedFile;
	private MapFileDriver mapFile;
	private List<Field> columns = null;

	private static String[] fixedColumns = new String[]
	{ "FamilyID", "IndividualID", "FatherID", "MotherID", "Sex", "Phenotype" };

	public PedMapTupleTable(File ped, File map) throws Exception
	{
		this.pedFile = new PedFileDriver(ped);
		this.mapFile = new MapFileDriver(map);
	}

	@Override
	public List<Field> getAllColumns() throws TableException
	{
		try
		{
			if (columns == null)
			{
				columns = new ArrayList<Field>();
				for (String col : fixedColumns)
				{
					columns.add(new Field(col));
				}

				for (MapEntry me : mapFile.getAllEntries())
				{
					columns.add(new Field(me.getSNP()));
				}
			}
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}
		return columns;
	}

	private static class PedMapIterator implements Iterator<Tuple>
	{
		int count = 0;

		// source data
		PedFileDriver pedFile;
		MapFileDriver mapFile;
		List<String> columns;

		// wrapper state
		TupleTable table;

		// colLimit
		int colLimit;

		PedMapIterator(PedFileDriver pedFile, MapFileDriver mapFile, TupleTable table)
		{
			this.pedFile = pedFile;
			this.mapFile = mapFile;
			this.table = table;

			columns = new ArrayList<String>();
			for (String col : fixedColumns)
			{
				columns.add(col);
			}

			try
			{
				for (MapEntry me : mapFile.getAllEntries())
				{
					columns.add(me.getSNP());
				}
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}

			colLimit = (int) (table.getColLimit() == 0 ? mapFile.getNrOfElements() + 6 : table.getColLimit());
		}

		@Override
		public boolean hasNext()
		{
			if (table.getOffset() + count >= pedFile.getNrOfElements()
					|| (table.getLimit() > 0 && count >= table.getLimit()))
			{
				return false;
			}
			return true;
		}

		@Override
		public Tuple next()
		{
			try
			{
				Tuple result = new SimpleTuple();

				List<PedEntry> pedEntries = new ArrayList<PedEntry>();
				pedEntries = pedFile.getEntries(table.getOffset() + count, table.getOffset() + count + 1);

				// List<PedEntry> colLimitedPedEntries = new
				// ArrayList<PedEntry>();
				// for (PedEntry pe : pedEntries)
				// {
				// colLimitedPedEntries
				// .add(new PedEntry(pe,
				// pe.getBialleles().subList(table.getColOffset(), colLimit)));
				// }

				for (PedEntry pe : pedEntries)
				{
					result.set(fixedColumns[0], pe.getFamily());
					result.set(fixedColumns[1], pe.getIndividual());
					result.set(fixedColumns[2], pe.getFather());
					result.set(fixedColumns[3], pe.getMother());
					result.set(fixedColumns[4], pe.getSex());
					result.set(fixedColumns[5], pe.getPhenotype());

					for (int i = table.getColOffset(); i < table.getColOffset() + colLimit; i++)
					{
						result.set(columns.get(i), pe.getBialleles().get(i).getAllele1()
								+ pe.getBialleles().get(i).getAllele2());
					}
				}

				count++;

				return result;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove()
		{
			// TODO Auto-generated method stub
		}

	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return new PedMapIterator(pedFile, mapFile, this);
	}

	@Override
	public int getCount() throws TableException
	{
		return (int) pedFile.getNrOfElements();
	}

	@Override
	public int getColCount() throws TableException
	{
		return (int) (mapFile.getNrOfElements() + 6);
	}
}
