package org.molgenis.util.plink.drivers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.plink.datatypes.PedEntry;

public class CachingPedFileDriver extends PedFileDriver
{
	private List<PedEntry> cache;
	private List<PedEntry> entries;

	public CachingPedFileDriver(File pedFile) throws Exception
	{
		super(pedFile);
		this.cache = super.getAllEntries();
		this.entries = new ArrayList<PedEntry>(cache);
	}

	public void setFilters(List<QueryRule> rules)
	{
		this.entries = new ArrayList<PedEntry>(cache);

		for (QueryRule rule : rules)
		{
			entries = filter(entries, rule);
		}

		nrOfElements = entries.size();
	}

	@Override
	public List<PedEntry> getAllEntries() throws Exception
	{
		return entries;
	}

	@Override
	public List<PedEntry> getEntries(long from, long to) throws Exception
	{
		return getAllEntries().subList((int) from, (int) to);
	}

	/** for now only simple single queries are implemented **/
	private List<PedEntry> filter(List<PedEntry> entries, final QueryRule filter)
	{
		List<PedEntry> filtered = new ArrayList<PedEntry>();

		for (PedEntry entry : entries)
		{
			// TODO do not hardcode columnnames
			// TODO implement more then just 'equals'
			if (filter.getOperator() == Operator.EQUALS)
			{
				if (filter.getField().equals("IndividualID") && entry.getIndividual().equals(filter.getValue()))
				{
					filtered.add(entry);
				}
				else if (filter.getField().equals("FamilyID") && entry.getFamily().equals(filter.getValue()))
				{
					filtered.add(entry);
				}
				else if (filter.getField().equals("FatherID") && entry.getFather().equals(filter.getValue()))
				{
					filtered.add(entry);
				}
				else if (filter.getField().equals("MotherID") && entry.getMother().equals(filter.getValue()))
				{
					filtered.add(entry);
				}
				else if (filter.getField().equals("Sex")
						&& new Byte(entry.getSex()).toString().equals(filter.getValue()))
				{
					filtered.add(entry);
				}
				else if (filter.getField().equals("Phenotype")
						&& new Double(entry.getPhenotype()).toString().equals(filter.getValue()))
				{
					filtered.add(entry);
				}

			}
		}

		return filtered;
	}
}
