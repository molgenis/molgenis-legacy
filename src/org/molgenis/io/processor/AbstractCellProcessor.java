package org.molgenis.io.processor;

import java.util.List;

public abstract class AbstractCellProcessor implements CellProcessor
{
	private final boolean processHeader;
	private final boolean processData;

	public AbstractCellProcessor()
	{
		this(true, true);
	}

	public AbstractCellProcessor(boolean processHeader, boolean processData)
	{
		this.processHeader = processHeader;
		this.processData = processData;
	}

	@Override
	public boolean processHeader()
	{
		return this.processHeader;
	}

	@Override
	public boolean processData()
	{
		return this.processData;
	}

	public static String processCell(String value, boolean isHeader, List<CellProcessor> cellProcessors)
	{
		if (cellProcessors != null)
		{
			for (CellProcessor cellProcessor : cellProcessors)
			{
				boolean process = (isHeader && cellProcessor.processHeader())
						|| (!isHeader && cellProcessor.processData());
				if (process) value = cellProcessor.process(value);
			}
		}
		return value;
	}
}
